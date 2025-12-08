package com.blog.service.impl;

import com.blog.service.CacheService;
import com.blog.service.CacheStats;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

/**
 * 缓存服务实现类
 * 支持Redis + 本地缓存双重保障
 * 防止缓存雪崩、击穿、穿透等问题
 * 无锁化高性能缓存服务实现
 * 使用软过期+异步刷新策略，彻底避免锁的性能损耗
 * 
 * @author Worlt
 * @date 2025/9/5
 */
@Service
@Slf4j
public class CacheServiceImpl implements CacheService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    // 本地缓存
    private final ConcurrentHashMap<String, CacheItem> localCache = new ConcurrentHashMap<>();
    
    // 软过期时间映射（核心：用于异步刷新判断）
    private final ConcurrentHashMap<String, Long> softExpireMap = new ConcurrentHashMap<>();
    
    // 正在刷新的Key集合（避免重复刷新，无锁CAS操作）
    private final Set<String> refreshingKeys = ConcurrentHashMap.newKeySet();
    
    // 异步刷新线程池
    private final ScheduledExecutorService refreshExecutor = Executors.newScheduledThreadPool(
        Math.max(2, Runtime.getRuntime().availableProcessors() / 2), 
        r -> {
            Thread thread = new Thread(r, "cache-refresh-" + System.currentTimeMillis());
            thread.setDaemon(true);
            return thread;
        }
    );
    
    // 空值缓存（防穿透）
    private static final String NULL_VALUE = "CACHE_NULL_VALUE";
    private static final int NULL_CACHE_TTL = 300; // 空值缓存5分钟
    
    // 布隆过滤器模拟（简化版）
    private final Set<String> bloomFilter = ConcurrentHashMap.newKeySet();
    
    // 热点Key快速缓存（L1缓存）
    private final ConcurrentHashMap<String, TimestampedValue> hotKeyCache = new ConcurrentHashMap<>();
    
    // 缓存统计（原子操作）
    private volatile long hitCount = 0;
    private volatile long missCount = 0;
    private volatile long nullHitCount = 0;
    private volatile long asyncRefreshCount = 0; // 异步刷新计数
    
    // 配置参数
    private static final int MAX_LOCAL_CACHE_SIZE = 1000;
    private static final int MAX_HOT_KEY_CACHE_SIZE = 200;
    private static final int EXPIRE_RANDOM_RANGE = 300; // 5分钟随机范围（防雪崩）
    private static final double SOFT_EXPIRE_RATIO = 0.8; // 软过期比例：80%时触发异步刷新
    private static final int ASYNC_REFRESH_ADVANCE_MS = 10000; // 提前10秒开始异步刷新
    
    // Redis健康状态缓存（避免频繁检查）
    private volatile long lastRedisCheckTime = 0;
    private volatile boolean lastRedisStatus = true;
    private static final long REDIS_CHECK_INTERVAL = 5000; // 5秒检查一次
    private static final int REDIS_CHECK_TIMEOUT = 200; // 200ms超时
    
    public CacheServiceImpl() {
        // 启动定时清理任务
        startCleanupTasks();
    }
    
    /**
     * 设置缓存（防雪崩优化）
     */
    @Override
    public boolean set(String key, Object value, long timeout, TimeUnit unit) {
        if (key == null || value == null) {
            return false;
        }
        
        // 添加到布隆过滤器
        bloomFilter.add(key);
        
        // 转换为毫秒
        long timeoutMs = unit.toMillis(timeout);
        
        // 防雪崩：添加随机过期时间
        long randomTimeoutMs = timeoutMs + new Random().nextInt(EXPIRE_RANDOM_RANGE * 1000);
        
        // 计算软过期时间（80%时触发异步刷新）
        long softExpireTime = System.currentTimeMillis() + (long)(randomTimeoutMs * SOFT_EXPIRE_RATIO);
        
        boolean redisSuccess = false;
        boolean localSuccess = false;
        
        // 尝试写入Redis（硬过期时间）
        if (isRedisAvailable()) {
            try {
                redisTemplate.opsForValue().set(key, value, randomTimeoutMs, TimeUnit.MILLISECONDS);
                redisSuccess = true;
                log.debug("Redis缓存写入成功: key={}, timeout={}ms", key, randomTimeoutMs);
            } catch (Exception e) {
                log.warn("Redis缓存写入失败: key={}, error={}", key, e.getMessage());
            }
        }
        
        // 写入本地缓存
        try {
            cleanLocalCacheIfNeeded();
            
            long expireTime = System.currentTimeMillis() + randomTimeoutMs;
            CacheItem item = new CacheItem(value, expireTime);
            localCache.put(key, item);
            localSuccess = true;
            
            // 记录软过期时间
            softExpireMap.put(key, softExpireTime);
            
            log.debug("本地缓存写入成功: key={}, softExpire={}", key, softExpireTime);
        } catch (Exception e) {
            log.warn("本地缓存写入失败: key={}, error={}", key, e.getMessage());
        }
        
        return redisSuccess || localSuccess;
    }
    
    /**
     * 获取缓存（无锁化防击穿、防穿透）
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        if (key == null) {
            return null;
        }
        
        // 1. 防穿透：布隆过滤器预检查
        if (!bloomFilter.contains(key)) {
            log.debug("布隆过滤器未命中，可能不存在: key={}", key);
            missCount++;
            return null;
        }
        
        // 2. 检查热点Key快速缓存（L1缓存）
        TimestampedValue hotValue = hotKeyCache.get(key);
        if (hotValue != null && !hotValue.isExpired()) {
            if (NULL_VALUE.equals(hotValue.getValue())) {
                nullHitCount++;
                log.debug("热点Key空值缓存命中: key={}", key);
                return null;
            }
            hitCount++;
            log.debug("热点Key缓存命中: key={}", key);
            return (T) hotValue.getValue();
        }
        
        // 3. 尝试从Redis获取
        Object redisValue = getFromRedis(key);
        if (redisValue != null) {
            // 异步更新热点Key缓存
            updateHotKeyCacheAsync(key, redisValue);
            
            if (NULL_VALUE.equals(redisValue)) {
                nullHitCount++;
                log.debug("Redis空值缓存命中: key={}", key);
                return null;
            }
            
            hitCount++;
            log.debug("Redis缓存命中: key={}", key);
            return (T) redisValue;
        }
        
        // 4. 从本地缓存获取
        CacheItem localItem = localCache.get(key);
        if (localItem != null && !localItem.isExpired()) {
            if (NULL_VALUE.equals(localItem.getValue())) {
                nullHitCount++;
                log.debug("本地空值缓存命中: key={}", key);
                return null;
            }
            
            hitCount++;
            log.debug("本地缓存命中: key={}", key);
            return (T) localItem.getValue();
        }
        
        // 5. 所有缓存都未命中
        missCount++;
        log.debug("缓存完全未命中: key={}", key);
        return null;
    }
    
    /**
     * 获取缓存或加载数据（无锁化防击穿）
     * 核心：使用软过期 + 异步刷新策略
     */
    public <T> T getOrLoad(String key, Callable<T> loader, long timeout, TimeUnit unit) {
        if (key == null || loader == null) {
            return null;
        }
        
        // 1. 先尝试从缓存获取
        T cachedValue = get(key, (Class<T>) Object.class);
        
        // 2. 如果缓存命中，检查是否需要异步刷新
        if (cachedValue != null) {
            triggerAsyncRefreshIfNeeded(key, loader, timeout, unit);
            return cachedValue;
        }
        
        // 3. 缓存完全未命中，需要同步加载
        return loadDataSafely(key, loader, timeout, unit);
    }
    
    /**
     * 检查并触发异步刷新（软过期策略）
     */
    private <T> void triggerAsyncRefreshIfNeeded(String key, Callable<T> loader, long timeout, TimeUnit unit) {
        Long softExpireTime = softExpireMap.get(key);
        if (softExpireTime != null) {
            long currentTime = System.currentTimeMillis();
            
            // 软过期检查：提前异步刷新
            if (currentTime >= softExpireTime - ASYNC_REFRESH_ADVANCE_MS) {
                // 使用CAS操作避免重复刷新
                if (refreshingKeys.add(key)) {
                    refreshExecutor.submit(() -> {
                        try {
                            log.debug("开始异步刷新缓存: key={}", key);
                            T data = loader.call();
                            
                            if (data != null) {
                                set(key, data, timeout, unit);
                                asyncRefreshCount++;
                                log.info("异步刷新缓存成功: key={}", key);
                            } else {
                                // 数据为空，缓存空值
                                set(key, NULL_VALUE, NULL_CACHE_TTL, TimeUnit.SECONDS);
                                log.info("异步刷新发现空数据，缓存空值: key={}", key);
                            }
                        } catch (Exception e) {
                            log.error("异步刷新缓存失败: key={}", key, e);
                        } finally {
                            refreshingKeys.remove(key);
                        }
                    });
                }
            }
        }
    }
    
    /**
     * 安全地加载数据（避免缓存击穿）
     */
    private <T> T loadDataSafely(String key, Callable<T> loader, long timeout, TimeUnit unit) {
        // 使用CAS操作标记正在加载
        if (refreshingKeys.add(key)) {
            try {
                // 双重检查：可能其他线程已经加载完成
                T cachedValue = get(key, (Class<T>) Object.class);
                if (cachedValue != null) {
                    return cachedValue;
                }
                
                log.info("缓存未命中，开始同步加载数据: key={}", key);
                T data = loader.call();
                
                if (data != null) {
                    set(key, data, timeout, unit);
                    log.info("数据同步加载并缓存成功: key={}", key);
                } else {
                    // 缓存空值防穿透
                    set(key, NULL_VALUE, NULL_CACHE_TTL, TimeUnit.SECONDS);
                    log.info("数据为空，缓存空值防穿透: key={}", key);
                }
                
                return data;
                
            } catch (Exception e) {
                log.error("同步加载数据失败: key={}", key, e);
                return null;
            } finally {
                refreshingKeys.remove(key);
            }
        } else {
            // 其他线程正在加载，短暂等待后重试
            try {
                Thread.sleep(10 + new Random().nextInt(20)); // 10-30ms随机等待
                T cachedValue = get(key, (Class<T>) Object.class);
                if (cachedValue != null) {
                    return cachedValue;
                }
                
                // 仍然未命中，直接加载数据（不缓存）
                log.debug("其他线程正在加载，直接返回数据: key={}", key);
                return loader.call();
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("等待缓存加载被中断: key={}", key);
                return null;
            } catch (Exception e) {
                log.error("降级加载数据失败: key={}", key, e);
                return null;
            }
        }
    }
    
    /**
     * 从Redis获取数据
     */
    private Object getFromRedis(String key) {
        if (!isRedisAvailable()) {
            return null;
        }
        
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.warn("Redis获取失败: key={}, error={}", key, e.getMessage());
            return null;
        }
    }
    
    /**
     * 异步更新热点Key缓存
     */
    private void updateHotKeyCacheAsync(String key, Object value) {
        refreshExecutor.submit(() -> {
            try {
                cleanHotKeyCacheIfNeeded();
                long expireTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(10); // 热点缓存10分钟
                hotKeyCache.put(key, new TimestampedValue(value, expireTime));
                log.debug("异步更新热点Key缓存: key={}", key);
            } catch (Exception e) {
                log.warn("异步更新热点Key缓存失败: key={}, error={}", key, e.getMessage());
            }
        });
    }
    
    @Override
    public boolean delete(String key) {
        if (key == null) {
            return false;
        }
        
        boolean redisSuccess = false;
        boolean localSuccess = false;
        
        // 从Redis删除
        if (isRedisAvailable()) {
            try {
                redisTemplate.delete(key);
                redisSuccess = true;
                log.debug("Redis缓存删除成功: key={}", key);
            } catch (Exception e) {
                log.warn("Redis缓存删除失败: key={}, error={}", key, e.getMessage());
            }
        }
        
        // 从本地缓存删除
        localCache.remove(key);
        hotKeyCache.remove(key);
        softExpireMap.remove(key);
        refreshingKeys.remove(key);
        bloomFilter.remove(key);
        localSuccess = true;
        
        log.debug("本地缓存删除成功: key={}", key);
        return redisSuccess || localSuccess;
    }
    
    @Override
    public long deleteByPattern(String pattern) {
        long deleted = 0;
        
        // Redis批量删除
        if (isRedisAvailable()) {
            try {
                Set<String> keys = redisTemplate.keys(pattern);
                if (keys != null && !keys.isEmpty()) {
                    Long result = redisTemplate.delete(keys);
                    deleted += (result != null ? result : 0);
                }
            } catch (Exception e) {
                log.warn("Redis批量删除失败: pattern={}, error={}", pattern, e.getMessage());
            }
        }
        
        // 本地缓存批量删除
        try {
            String regex = pattern.replace("*", ".*");
            List<String> keysToDelete = localCache.keySet().stream()
                .filter(key -> key.matches(regex))
                .collect(java.util.stream.Collectors.toList());
            
            keysToDelete.forEach(key -> {
                localCache.remove(key);
                hotKeyCache.remove(key);
                softExpireMap.remove(key);
                refreshingKeys.remove(key);
            });
            deleted += keysToDelete.size();
            
        } catch (Exception e) {
            log.warn("本地缓存批量删除失败: pattern={}, error={}", pattern, e.getMessage());
        }
        
        return deleted;
    }
    
    @Override
    public long cleanExpiredCache() {
        long cleaned = 0;
        
        // 清理本地缓存过期项
        long before = localCache.size();
        localCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
        cleaned += (before - localCache.size());
        
        // 清理热点Key缓存过期项
        before = hotKeyCache.size();
        hotKeyCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
        cleaned += (before - hotKeyCache.size());
        
        // 清理软过期时间映射
        long now = System.currentTimeMillis();
        before = softExpireMap.size();
        softExpireMap.entrySet().removeIf(entry -> 
            now > entry.getValue() + TimeUnit.HOURS.toMillis(1)
        );
        cleaned += (before - softExpireMap.size());
        
        log.debug("清理过期缓存完成: {} 项", cleaned);
        return cleaned;
    }
    
    @Override
    public CacheStats getCacheStats() {
        CacheStats stats = new CacheStats();
        stats.setRedisAvailable(isRedisAvailable());
        stats.setTotalKeys(localCache.size());
        stats.setHitCount(hitCount);
        stats.setMissCount(missCount);
        stats.setNullHitCount(nullHitCount);
        
        // 计算命中率
        long totalRequests = hitCount + missCount;
        if (totalRequests > 0) {
            stats.setHitRate((double) hitCount / totalRequests);
        }
        
        // 设置其他统计信息
        stats.setMemoryUsageMB((localCache.size() + hotKeyCache.size()) * 2.0 / 1024); // 粗略估算
        stats.setExpiredKeys(0); // 可以添加过期键统计
        stats.setBloomFilterSize(bloomFilter.size());
        stats.setHotKeyCacheSize(hotKeyCache.size());
        
        // 异步刷新统计 - 使用lastUpdateTime字段存储
        stats.setLastUpdateTime(asyncRefreshCount);
        
        return stats;
    }
    
    /**
     * 检查Redis是否可用（优化版本）
     * 添加健康状态缓存，避免频繁ping检查
     */
    @Override
    public boolean isRedisAvailable() {
        long currentTime = System.currentTimeMillis();
        
        // 如果距离上次检查不到5秒，直接返回缓存状态
        if (currentTime - lastRedisCheckTime < REDIS_CHECK_INTERVAL) {
            return lastRedisStatus;
        }
        
        // 执行快速检查
        boolean isAvailable = false;
        try {
            // 使用超时控制的ping检查
            CompletableFuture<Void> pingFuture = CompletableFuture.runAsync(() -> {
        try {
            redisTemplate.getConnectionFactory().getConnection().ping();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            
            // 等待最多200ms
            pingFuture.get(REDIS_CHECK_TIMEOUT, TimeUnit.MILLISECONDS);
            isAvailable = true;
            
        } catch (Exception e) {
            isAvailable = false;
            log.debug("Redis健康检查失败: {}", e.getMessage());
        }
        
        // 更新缓存状态
        lastRedisCheckTime = currentTime;
        lastRedisStatus = isAvailable;
        
        if (!isAvailable) {
            log.warn("Redis不可用，已切换到本地缓存模式");
        }
        
        return isAvailable;
    }
    
    /**
     * 清理本地缓存
     */
    private void cleanLocalCacheIfNeeded() {
        if (localCache.size() > MAX_LOCAL_CACHE_SIZE) {
            localCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
            
            // 如果还是太多，移除最老的项
            if (localCache.size() > MAX_LOCAL_CACHE_SIZE) {
                localCache.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue((a, b) -> 
                        Long.compare(a.getExpireTime(), b.getExpireTime())))
                    .limit(localCache.size() - MAX_LOCAL_CACHE_SIZE + 100)
                    .forEach(entry -> localCache.remove(entry.getKey()));
            }
        }
    }
    
    /**
     * 清理热点Key缓存
     */
    private void cleanHotKeyCacheIfNeeded() {
        if (hotKeyCache.size() > MAX_HOT_KEY_CACHE_SIZE) {
            hotKeyCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
            
            if (hotKeyCache.size() > MAX_HOT_KEY_CACHE_SIZE) {
                hotKeyCache.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue((a, b) -> 
                        Long.compare(a.getExpireTime(), b.getExpireTime())))
                    .limit(hotKeyCache.size() - MAX_HOT_KEY_CACHE_SIZE + 20)
                    .forEach(entry -> hotKeyCache.remove(entry.getKey()));
            }
        }
    }
    
    /**
     * 启动清理任务
     */
    private void startCleanupTasks() {
        // 每5分钟清理过期数据
        refreshExecutor.scheduleAtFixedRate(() -> {
            try {
                cleanLocalCacheIfNeeded();
                cleanHotKeyCacheIfNeeded();
                
                // 清理软过期时间映射
                long now = System.currentTimeMillis();
                softExpireMap.entrySet().removeIf(entry -> 
                    now > entry.getValue() + TimeUnit.HOURS.toMillis(1)
                );
                
                log.debug("定时清理缓存完成");
            } catch (Exception e) {
                log.error("定时清理缓存失败", e);
            }
        }, 5, 5, TimeUnit.MINUTES);
    }
    
    /**
     * 缓存项
     */
    private static class CacheItem {
        private final Object value;
        private final long expireTime;
        
        public CacheItem(Object value, long expireTime) {
            this.value = value;
            this.expireTime = expireTime;
        }
        
        public Object getValue() { return value; }
        public long getExpireTime() { return expireTime; }
        public boolean isExpired() { return System.currentTimeMillis() > expireTime; }
    }
    
    /**
     * 带时间戳的值
     */
    private static class TimestampedValue {
        private final Object value;
        private final long expireTime;
        
        public TimestampedValue(Object value, long expireTime) {
            this.value = value;
            this.expireTime = expireTime;
        }
        
        public Object getValue() { return value; }
        public long getExpireTime() { return expireTime; }
        public boolean isExpired() { return System.currentTimeMillis() > expireTime; }
    }
} 
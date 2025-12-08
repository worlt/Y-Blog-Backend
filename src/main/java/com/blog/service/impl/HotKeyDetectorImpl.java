package com.blog.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 热点Key检测器
 * 识别高频访问的缓存键，进行特殊处理
 * 
 * @author Worlt
 * @date 2025/9/5
 */
@Service
@Slf4j
public class HotKeyDetectorImpl {
    
    // 访问计数器
    private final Map<String, AtomicLong> accessCounters = new ConcurrentHashMap<>();
    
    // 热点Key集合
    private final Map<String, HotKeyInfo> hotKeys = new ConcurrentHashMap<>();
    
    // 定时任务执行器
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    
    // 配置参数
    private static final long HOT_KEY_THRESHOLD = 100; // 热点阈值：100次/分钟
    private static final int MAX_HOT_KEYS = 1000; // 最大热点Key数量
    private static final int STATS_WINDOW_MINUTES = 5; // 统计窗口：5分钟
    
    public HotKeyDetectorImpl() {
        // 启动定时任务
        startScheduledTasks();
    }
    
    /**
     * 记录Key访问
     * 
     * @param key 缓存键
     */
    public void recordAccess(String key) {
        if (key == null) {
            return;
        }
        
        // 增加访问计数
        accessCounters.computeIfAbsent(key, k -> new AtomicLong(0))
                     .incrementAndGet();
        
        // 检查是否成为热点Key
        checkAndMarkHotKey(key);
    }
    
    /**
     * 检查是否为热点Key
     * 
     * @param key 缓存键
     * @return 是否为热点
     */
    public boolean isHotKey(String key) {
        if (key == null) {
            return false;
        }
        
        HotKeyInfo hotKeyInfo = hotKeys.get(key);
        return hotKeyInfo != null && !hotKeyInfo.isExpired();
    }
    
    /**
     * 获取热点Key信息
     * 
     * @param key 缓存键
     * @return 热点Key信息
     */
    public HotKeyInfo getHotKeyInfo(String key) {
        return hotKeys.get(key);
    }
    
    /**
     * 获取所有热点Key
     * 
     * @return 热点Key映射
     */
    public Map<String, HotKeyInfo> getAllHotKeys() {
        // 清理过期的热点Key
        hotKeys.entrySet().removeIf(entry -> entry.getValue().isExpired());
        return new ConcurrentHashMap<>(hotKeys);
    }
    
    /**
     * 手动标记热点Key
     * 
     * @param key 缓存键
     * @param duration 持续时间（分钟）
     */
    public void markAsHotKey(String key, int duration) {
        if (key == null) {
            return;
        }
        
        long expireTime = System.currentTimeMillis() + duration * 60 * 1000L;
        long currentCount = accessCounters.getOrDefault(key, new AtomicLong(0)).get();
        
        HotKeyInfo hotKeyInfo = new HotKeyInfo(key, currentCount, expireTime, true);
        hotKeys.put(key, hotKeyInfo);
        
        log.info("手动标记热点Key: key={}, duration={}分钟", key, duration);
    }
    
    /**
     * 移除热点Key标记
     * 
     * @param key 缓存键
     */
    public void removeHotKey(String key) {
        if (key == null) {
            return;
        }
        
        hotKeys.remove(key);
        log.info("移除热点Key标记: key={}", key);
    }
    
    /**
     * 获取热点Key统计信息
     * 
     * @return 统计信息
     */
    public HotKeyStats getStats() {
        HotKeyStats stats = new HotKeyStats();
        
        // 清理过期数据
        cleanExpiredData();
        
        stats.setTotalAccessKeys(accessCounters.size());
        stats.setCurrentHotKeys(hotKeys.size());
        stats.setHotKeyThreshold(HOT_KEY_THRESHOLD);
        
        // 计算总访问次数
        long totalAccess = accessCounters.values().stream()
                                       .mapToLong(AtomicLong::get)
                                       .sum();
        stats.setTotalAccessCount(totalAccess);
        
        // 计算热点Key访问次数
        long hotKeyAccess = hotKeys.values().stream()
                                  .mapToLong(HotKeyInfo::getAccessCount)
                                  .sum();
        stats.setHotKeyAccessCount(hotKeyAccess);
        
        // 计算热点Key访问占比
        if (totalAccess > 0) {
            stats.setHotKeyAccessRatio((double) hotKeyAccess / totalAccess);
        }
        
        return stats;
    }
    
    /**
     * 清理过期数据
     */
    public void cleanExpiredData() {
        // 清理过期的热点Key
        int removedHotKeys = 0;
        for (Map.Entry<String, HotKeyInfo> entry : hotKeys.entrySet()) {
            if (entry.getValue().isExpired()) {
                hotKeys.remove(entry.getKey());
                removedHotKeys++;
            }
        }
        
        // 清理访问计数器（保留最近的）
        int removedCounters = 0;
        if (accessCounters.size() > MAX_HOT_KEYS * 2) {
            // 简单策略：清理访问次数较少的键
            accessCounters.entrySet().stream()
                         .filter(entry -> entry.getValue().get() < 10)
                         .limit(accessCounters.size() - MAX_HOT_KEYS)
                         .forEach(entry -> {
                             accessCounters.remove(entry.getKey());
                         });
            removedCounters = accessCounters.size() > MAX_HOT_KEYS ? 
                            accessCounters.size() - MAX_HOT_KEYS : 0;
        }
        
        if (removedHotKeys > 0 || removedCounters > 0) {
            log.debug("热点Key数据清理完成 - 热点Key: {}, 计数器: {}", removedHotKeys, removedCounters);
        }
    }
    
    /**
     * 检查并标记热点Key
     */
    private void checkAndMarkHotKey(String key) {
        AtomicLong counter = accessCounters.get(key);
        if (counter == null) {
            return;
        }
        
        long accessCount = counter.get();
        
        // 达到热点阈值且不在热点列表中
        if (accessCount >= HOT_KEY_THRESHOLD && !hotKeys.containsKey(key)) {
            // 检查热点Key数量限制
            if (hotKeys.size() >= MAX_HOT_KEYS) {
                // 移除最老的热点Key
                removeOldestHotKey();
            }
            
            // 标记为热点Key，持续时间为统计窗口的2倍
            long expireTime = System.currentTimeMillis() + STATS_WINDOW_MINUTES * 2 * 60 * 1000L;
            HotKeyInfo hotKeyInfo = new HotKeyInfo(key, accessCount, expireTime, false);
            hotKeys.put(key, hotKeyInfo);
            
            log.info("检测到新热点Key: key={}, accessCount={}", key, accessCount);
        }
    }
    
    /**
     * 移除最老的热点Key
     */
    private void removeOldestHotKey() {
        hotKeys.entrySet().stream()
              .min((e1, e2) -> Long.compare(e1.getValue().getCreateTime(), e2.getValue().getCreateTime()))
              .ifPresent(entry -> {
                  hotKeys.remove(entry.getKey());
                  log.debug("移除最老的热点Key: key={}", entry.getKey());
              });
    }
    
    /**
     * 启动定时任务
     */
    private void startScheduledTasks() {
        // 每分钟重置访问计数（滑动窗口）
        scheduler.scheduleAtFixedRate(() -> {
            try {
                resetAccessCounters();
            } catch (Exception e) {
                log.error("重置访问计数失败", e);
            }
        }, 1, 1, TimeUnit.MINUTES);
        
        // 每5分钟清理过期数据
        scheduler.scheduleAtFixedRate(() -> {
            try {
                cleanExpiredData();
            } catch (Exception e) {
                log.error("清理过期数据失败", e);
            }
        }, 5, 5, TimeUnit.MINUTES);
    }
    
    /**
     * 重置访问计数器（实现滑动窗口）
     */
    private void resetAccessCounters() {
        // 简单策略：每分钟将计数器减半，模拟滑动窗口
        accessCounters.values().forEach(counter -> {
            long currentValue = counter.get();
            counter.set(currentValue / 2);
        });
        
        // 清理计数为0的项
        accessCounters.entrySet().removeIf(entry -> entry.getValue().get() == 0);
        
        log.debug("访问计数器重置完成，当前活跃Key: {}", accessCounters.size());
    }
    
    /**
     * 热点Key信息
     */
    public static class HotKeyInfo {
        private final String key;
        private final long accessCount;
        private final long expireTime;
        private final long createTime;
        private final boolean manualMarked; // 是否手动标记
        
        public HotKeyInfo(String key, long accessCount, long expireTime, boolean manualMarked) {
            this.key = key;
            this.accessCount = accessCount;
            this.expireTime = expireTime;
            this.manualMarked = manualMarked;
            this.createTime = System.currentTimeMillis();
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() > expireTime;
        }
        
        // Getters
        public String getKey() { return key; }
        public long getAccessCount() { return accessCount; }
        public long getExpireTime() { return expireTime; }
        public long getCreateTime() { return createTime; }
        public boolean isManualMarked() { return manualMarked; }
    }
    
    /**
     * 热点Key统计信息
     */
    public static class HotKeyStats {
        private int totalAccessKeys;
        private int currentHotKeys;
        private long hotKeyThreshold;
        private long totalAccessCount;
        private long hotKeyAccessCount;
        private double hotKeyAccessRatio;
        
        // Getters and Setters
        public int getTotalAccessKeys() { return totalAccessKeys; }
        public void setTotalAccessKeys(int totalAccessKeys) { this.totalAccessKeys = totalAccessKeys; }
        
        public int getCurrentHotKeys() { return currentHotKeys; }
        public void setCurrentHotKeys(int currentHotKeys) { this.currentHotKeys = currentHotKeys; }
        
        public long getHotKeyThreshold() { return hotKeyThreshold; }
        public void setHotKeyThreshold(long hotKeyThreshold) { this.hotKeyThreshold = hotKeyThreshold; }
        
        public long getTotalAccessCount() { return totalAccessCount; }
        public void setTotalAccessCount(long totalAccessCount) { this.totalAccessCount = totalAccessCount; }
        
        public long getHotKeyAccessCount() { return hotKeyAccessCount; }
        public void setHotKeyAccessCount(long hotKeyAccessCount) { this.hotKeyAccessCount = hotKeyAccessCount; }
        
        public double getHotKeyAccessRatio() { return hotKeyAccessRatio; }
        public void setHotKeyAccessRatio(double hotKeyAccessRatio) { this.hotKeyAccessRatio = hotKeyAccessRatio; }
    }
} 
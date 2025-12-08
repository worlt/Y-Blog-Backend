package com.blog.service.impl;

import com.blog.service.CacheStats;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 高级缓存管理器
 * 整合防雪崩、击穿、穿透的所有机制
 * 
 * @author Worlt
 * @date 2025/9/5
 */
@Service
@Slf4j
public class AdvancedCacheManagerImpl {
    
    @Autowired
    private CacheServiceImpl cacheService;
    
    @Autowired
    private BloomFilterServiceImpl bloomFilterService;
    
    @Autowired
    private HotKeyDetectorImpl hotKeyDetector;
    
    @Autowired
    private BlogCacheServiceImpl blogCacheService;
    
    /**
     * 高级缓存获取方法
     * 集成所有防护机制
     * 
     * @param key 缓存键
     * @param loader 数据加载器（缓存未命中时调用）
     * @param timeout 缓存时间
     * @param unit 时间单位
     * @param <T> 返回类型
     * @return 缓存数据
     */
    public <T> T getWithProtection(String key, Callable<T> loader, long timeout, TimeUnit unit) {
        if (key == null) {
            return null;
        }
        
        try {
            // 1. 记录访问，用于热点Key检测
            hotKeyDetector.recordAccess(key);
            
            // 2. 防穿透：布隆过滤器预检查
            if (!bloomFilterService.mightContain(key)) {
                log.debug("布隆过滤器未命中，数据可能不存在: key={}", key);
                
                // 仍然尝试加载数据，如果存在则添加到布隆过滤器
                T data = loadAndCache(key, loader, timeout, unit);
                if (data != null) {
                    bloomFilterService.add(key);
                    log.info("数据存在但不在布隆过滤器中，已添加: key={}", key);
                }
                return data;
            }
            
            // 3. 检查是否为热点Key
            if (hotKeyDetector.isHotKey(key)) {
                log.debug("检测到热点Key访问: key={}", key);
                // 热点Key使用更短的锁等待时间，避免长时间阻塞
                return getHotKeyWithProtection(key, loader, timeout, unit);
            }
            
            // 4. 普通Key处理：无锁化防击穿
            return cacheService.getOrLoad(key, loader, timeout, unit);
            
        } catch (Exception e) {
            log.error("高级缓存获取失败: key={}, error={}", key, e.getMessage(), e);
            
            // 降级处理：直接加载数据
            try {
                return loader.call();
            } catch (Exception ex) {
                log.error("降级数据加载失败: key={}, error={}", key, ex.getMessage(), ex);
                return null;
            }
        }
    }
    
    /**
     * 热点Key特殊处理
     * 使用无锁化异步刷新策略
     */
    private <T> T getHotKeyWithProtection(String key, Callable<T> loader, long timeout, TimeUnit unit) {
        // 热点Key直接使用缓存服务的无锁化getOrLoad方法
        // 该方法内部已经实现了软过期+异步刷新机制
        T data = cacheService.getOrLoad(key, loader, timeout * 2, unit); // 热点Key缓存时间加倍
        
        if (data != null) {
            log.debug("热点Key处理完成: key={}", key);
        } else {
            log.warn("热点Key数据加载失败: key={}", key);
        }
        
        return data;
    }
    
    /**
     * 加载数据并缓存
     */
    private <T> T loadAndCache(String key, Callable<T> loader, long timeout, TimeUnit unit) {
        try {
            T data = loader.call();
            if (data != null) {
                cacheService.set(key, data, timeout, unit);
                log.debug("数据加载并缓存成功: key={}", key);
            } else {
                // 缓存空值防穿透
                cacheService.set(key, "NULL_VALUE", 5, TimeUnit.MINUTES);
                log.debug("数据为空，缓存空值防穿透: key={}", key);
            }
            return data;
        } catch (Exception e) {
            log.error("数据加载失败: key={}, error={}", key, e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 预热缓存
     * 将重要数据提前加载到缓存中
     * 
     * @param key 缓存键
     * @param data 数据
     * @param timeout 缓存时间
     * @param unit 时间单位
     */
    public <T> void warmUp(String key, T data, long timeout, TimeUnit unit) {
        if (key == null || data == null) {
            return;
        }
        
        // 添加到布隆过滤器
        bloomFilterService.add(key);
        
        // 设置缓存
        cacheService.set(key, data, timeout, unit);
        
        log.info("缓存预热完成: key={}", key);
    }
    
    /**
     * 批量预热缓存
     */
    public void batchWarmUp(java.util.Map<String, Object> dataMap, long timeout, TimeUnit unit) {
        if (dataMap == null || dataMap.isEmpty()) {
            return;
        }
        
        dataMap.forEach((key, data) -> warmUp(key, data, timeout, unit));
        
        log.info("批量缓存预热完成: {} 项", dataMap.size());
    }
    
    /**
     * 智能缓存更新
     * 根据Key类型和访问频率选择更新策略
     */
    public <T> void smartUpdate(String key, T newData, long timeout, TimeUnit unit) {
        if (key == null) {
            return;
        }
        
        // 检查是否为热点Key
        if (hotKeyDetector.isHotKey(key)) {
            // 热点Key：先更新缓存，再异步通知
            cacheService.set(key, newData, timeout * 2, unit); // 热点Key缓存时间加倍
            log.info("热点Key智能更新: key={}", key);
        } else {
            // 普通Key：直接删除缓存，让下次访问重新加载
            cacheService.delete(key);
            log.info("普通Key智能更新（删除缓存）: key={}", key);
        }
        
        // 更新布隆过滤器
        if (newData != null) {
            bloomFilterService.add(key);
        }
    }
    
    /**
     * 获取缓存保护统计信息
     */
    public CacheProtectionStats getProtectionStats() {
        CacheProtectionStats stats = new CacheProtectionStats();
        
        // 基础缓存统计
        CacheStats cacheStats = cacheService.getCacheStats();
        stats.setCacheStats(cacheStats);
        
        // 布隆过滤器统计
        BloomFilterServiceImpl.BloomFilterStats bloomStats = bloomFilterService.getStats();
        stats.setBloomFilterStats(bloomStats);
        
        // 热点Key统计
        HotKeyDetectorImpl.HotKeyStats hotKeyStats = hotKeyDetector.getStats();
        stats.setHotKeyStats(hotKeyStats);
        
        return stats;
    }
    
    /**
     * 缓存保护统计信息汇总
     */
    public static class CacheProtectionStats {
        private CacheStats cacheStats;
        private BloomFilterServiceImpl.BloomFilterStats bloomFilterStats;
        private HotKeyDetectorImpl.HotKeyStats hotKeyStats;
        
        // Getters and Setters
        public CacheStats getCacheStats() { return cacheStats; }
        public void setCacheStats(CacheStats cacheStats) { this.cacheStats = cacheStats; }
        
        public BloomFilterServiceImpl.BloomFilterStats getBloomFilterStats() { return bloomFilterStats; }
        public void setBloomFilterStats(BloomFilterServiceImpl.BloomFilterStats bloomFilterStats) { 
            this.bloomFilterStats = bloomFilterStats; 
        }
        
        public HotKeyDetectorImpl.HotKeyStats getHotKeyStats() { return hotKeyStats; }
        public void setHotKeyStats(HotKeyDetectorImpl.HotKeyStats hotKeyStats) { 
            this.hotKeyStats = hotKeyStats; 
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== 缓存保护统计报告 ===\n");
            
            if (cacheStats != null) {
                sb.append("缓存统计:\n");
                sb.append(String.format("  Redis状态: %s\n", cacheStats.isRedisAvailable() ? "可用" : "不可用"));
                sb.append(String.format("  总缓存数: %d\n", cacheStats.getTotalKeys()));
                sb.append(String.format("  命中率: %.2f%%\n", cacheStats.getHitRate() * 100));
                sb.append(String.format("  空值命中: %d\n", cacheStats.getNullHitCount()));
            }
            
            if (bloomFilterStats != null) {
                sb.append("布隆过滤器:\n");
                sb.append(String.format("  元素总数: %d\n", bloomFilterStats.getTotalElements()));
                sb.append(String.format("  误判率: %.4f%%\n", bloomFilterStats.getEstimatedFalsePositiveRate() * 100));
            }
            
            if (hotKeyStats != null) {
                sb.append("热点Key:\n");
                sb.append(String.format("  当前热点Key: %d\n", hotKeyStats.getCurrentHotKeys()));
                sb.append(String.format("  热点访问占比: %.2f%%\n", hotKeyStats.getHotKeyAccessRatio() * 100));
            }
            
            return sb.toString();
        }
    }
} 
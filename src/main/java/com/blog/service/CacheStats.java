package com.blog.service;

import lombok.Data;

/**
 * 缓存统计信息
 * 
 * @author Worlt
 * @date 2025/9/5
 */
@Data
public class CacheStats {
    
    /**
     * Redis是否可用
     */
    private boolean redisAvailable;
    
    /**
     * 缓存总数
     */
    private long totalKeys;
    
    /**
     * 内存使用量（MB）
     */
    private double memoryUsageMB;
    
    /**
     * 命中次数
     */
    private long hitCount;
    
    /**
     * 未命中次数
     */
    private long missCount;
    
    /**
     * 命中率
     */
    private double hitRate;
    
    /**
     * 过期键数量
     */
    private long expiredKeys;
    
    /**
     * 最后更新时间
     */
    private long lastUpdateTime;
    
    // 新增字段
    private long nullHitCount; // 空值命中次数
    private int bloomFilterSize; // 布隆过滤器大小
    private int hotKeyCacheSize; // 热点Key缓存大小
    
    public CacheStats() {
        this.lastUpdateTime = System.currentTimeMillis();
    }
    
    // 新增的getter和setter方法
    public long getNullHitCount() { return nullHitCount; }
    public void setNullHitCount(long nullHitCount) { this.nullHitCount = nullHitCount; }
    
    public int getBloomFilterSize() { return bloomFilterSize; }
    public void setBloomFilterSize(int bloomFilterSize) { this.bloomFilterSize = bloomFilterSize; }
    
    public int getHotKeyCacheSize() { return hotKeyCacheSize; }
    public void setHotKeyCacheSize(int hotKeyCacheSize) { this.hotKeyCacheSize = hotKeyCacheSize; }
} 
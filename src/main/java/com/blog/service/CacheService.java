package com.blog.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存服务接口
 * 提供统一的缓存操作，包含降级处理
 * 
 * @author Worlt
 * @date 2025/9/5
 */
public interface CacheService {
    
    /**
     * 设置缓存
     * 
     * @param key 缓存键
     * @param value 缓存值
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return 是否成功
     */
    boolean set(String key, Object value, long timeout, TimeUnit unit);
    
    /**
     * 获取缓存
     * 
     * @param key 缓存键
     * @param clazz 返回类型
     * @return 缓存值
     */
    <T> T get(String key, Class<T> clazz);
    
    /**
     * 删除缓存
     * 
     * @param key 缓存键
     * @return 是否成功
     */
    boolean delete(String key);
    
    /**
     * 批量删除缓存
     * 
     * @param pattern 匹配模式
     * @return 删除数量
     */
    long deleteByPattern(String pattern);
    
    // 移除了hasKey、expire、getExpire方法，简化接口
    
    /**
     * 清理过期缓存
     * 
     * @return 清理数量
     */
    long cleanExpiredCache();
    
    /**
     * 获取缓存统计信息
     * 
     * @return 统计信息
     */
    CacheStats getCacheStats();
    
    /**
     * 检查Redis连接状态
     * 
     * @return 是否可用
     */
    boolean isRedisAvailable();
} 
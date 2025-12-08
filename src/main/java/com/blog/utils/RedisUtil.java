package com.blog.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 * 提供统一的超时控制和降级处理
 * 
 * @authorWorlt
 * @date 2025/1/20
 */
@Component
@Slf4j
public class RedisUtil {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final int DEFAULT_TIMEOUT_MS = 200; // 默认200ms超时
    
    /**
     * 安全地检查key是否存在
     * @param key 键
     * @return 是否存在，出错返回false
     */
    public boolean hasKey(String key) {
        return hasKey(key, DEFAULT_TIMEOUT_MS);
    }
    
    /**
     * 安全地检查key是否存在（带超时）
     * @param key 键
     * @param timeoutMs 超时时间（毫秒）
     * @return 是否存在，出错返回false
     */
    public boolean hasKey(String key, long timeoutMs) {
        try {
            CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
                try {
                    return Boolean.TRUE.equals(redisTemplate.hasKey(key));
                } catch (Exception e) {
                    log.debug("Redis hasKey操作失败: key={}, error={}", key, e.getMessage());
                    return false;
                }
            });
            
            return future.get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.debug("Redis hasKey操作超时或失败: key={}, timeout={}ms", key, timeoutMs);
            return false;
        }
    }
    
    /**
     * 安全地设置值
     * @param key 键
     * @param value 值
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return 是否成功
     */
    public boolean set(String key, Object value, long timeout, TimeUnit unit) {
        return set(key, value, timeout, unit, DEFAULT_TIMEOUT_MS);
    }
    
    /**
     * 安全地设置值（带超时）
     * @param key 键
     * @param value 值
     * @param timeout 过期时间
     * @param unit 时间单位
     * @param operationTimeoutMs 操作超时时间（毫秒）
     * @return 是否成功
     */
    public boolean set(String key, Object value, long timeout, TimeUnit unit, long operationTimeoutMs) {
        try {
            CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
                try {
                    redisTemplate.opsForValue().set(key, value, timeout, unit);
                    return true;
                } catch (Exception e) {
                    log.debug("Redis set操作失败: key={}, error={}", key, e.getMessage());
                    return false;
                }
            });
            
            return future.get(operationTimeoutMs, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.debug("Redis set操作超时或失败: key={}, timeout={}ms", key, operationTimeoutMs);
            return false;
        }
    }
    
    /**
     * 安全地获取值
     * @param key 键
     * @return 值，出错返回null
     */
    public Object get(String key) {
        return get(key, DEFAULT_TIMEOUT_MS);
    }
    
    /**
     * 安全地获取值（带超时）
     * @param key 键
     * @param timeoutMs 超时时间（毫秒）
     * @return 值，出错返回null
     */
    public Object get(String key, long timeoutMs) {
        try {
            CompletableFuture<Object> future = CompletableFuture.supplyAsync(() -> {
                try {
                    return redisTemplate.opsForValue().get(key);
                } catch (Exception e) {
                    log.debug("Redis get操作失败: key={}, error={}", key, e.getMessage());
                    return null;
                }
            });
            
            return future.get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.debug("Redis get操作超时或失败: key={}, timeout={}ms", key, timeoutMs);
            return null;
        }
    }
    
    /**
     * 安全地删除key
     * @param key 键
     * @return 是否成功
     */
    public boolean delete(String key) {
        return delete(key, DEFAULT_TIMEOUT_MS);
    }
    
    /**
     * 安全地删除key（带超时）
     * @param key 键
     * @param timeoutMs 超时时间（毫秒）
     * @return 是否成功
     */
    public boolean delete(String key, long timeoutMs) {
        try {
            CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
                try {
                    return Boolean.TRUE.equals(redisTemplate.delete(key));
                } catch (Exception e) {
                    log.debug("Redis delete操作失败: key={}, error={}", key, e.getMessage());
                    return false;
                }
            });
            
            return future.get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.debug("Redis delete操作超时或失败: key={}, timeout={}ms", key, timeoutMs);
            return false;
        }
    }
    
    /**
     * 快速检查Redis是否可用
     * @return 是否可用
     */
    public boolean isRedisAvailable() {
        try {
            CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
                try {
                    redisTemplate.hasKey("__health_check__");
                    return true;
                } catch (Exception e) {
                    return false;
                }
            });
            
            return future.get(100, TimeUnit.MILLISECONDS); // 100ms超时
        } catch (Exception e) {
            return false;
        }
    }
} 
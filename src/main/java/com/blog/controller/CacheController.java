package com.blog.controller;

import com.blog.service.CacheService;
import com.blog.service.CacheStats;
import com.blog.service.impl.BlogCacheServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 缓存管理控制器
 * 提供缓存统计查看和管理功能
 * 
 * @author Worlt
 * @date 2025/9/5
 */
@RestController
@RequestMapping("/api/admin/cache")
@Slf4j
public class CacheController {
    
    @Autowired
    private CacheService cacheService;
    
    @Autowired
    private BlogCacheServiceImpl blogCacheService;
    
    /**
     * 获取缓存统计信息
     * 
     * @return 缓存统计
     */
    @GetMapping("/stats")
    public CacheStats getCacheStats() {
        log.info("获取缓存统计信息");
        return cacheService.getCacheStats();
    }
    
    /**
     * 获取缓存统计报告（格式化文本）
     * 
     * @return 统计报告
     */
    @GetMapping("/report")
    public String getCacheReport() {
        log.info("获取缓存统计报告");
        return blogCacheService.getCacheStatsReport();
    }
    
    /**
     * 检查Redis连接状态
     * 
     * @return Redis状态
     */
    @GetMapping("/redis/health")
    public boolean checkRedisHealth() {
        boolean isAvailable = cacheService.isRedisAvailable();
        log.info("Redis健康检查结果: {}", isAvailable ? "可用" : "不可用");
        return isAvailable;
    }
    
    /**
     * 手动清理过期缓存
     * 
     * @return 清理数量
     */
    @PostMapping("/clean")
    public long cleanExpiredCache() {
        log.info("手动清理过期缓存");
        long cleaned = blogCacheService.cleanAllExpiredCache();
        log.info("手动缓存清理完成，清理 {} 个过期项", cleaned);
        return cleaned;
    }
    
    /**
     * 清除指定博客的缓存
     * 
     * @param blogId 博客ID
     * @return 操作结果
     */
    @DeleteMapping("/blog/{blogId}")
    public String evictBlogCache(@PathVariable Long blogId) {
        log.info("清除博客缓存: blogId={}", blogId);
        blogCacheService.evictBlogCache(blogId);
        return "博客缓存清除成功: " + blogId;
    }
    
    /**
     * 清除指定用户的缓存
     * 
     * @param userId 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/user/{userId}")
    public String evictUserCache(@PathVariable Long userId) {
        log.info("清除用户缓存: userId={}", userId);
        blogCacheService.evictUserCache(userId);
        return "用户缓存清除成功: " + userId;
    }
    
    /**
     * 批量删除匹配模式的缓存
     * 
     * @param pattern 匹配模式
     * @return 删除数量
     */
    @DeleteMapping("/pattern")
    public long deleteByPattern(@RequestParam String pattern) {
        log.info("批量删除缓存，模式: {}", pattern);
        long deleted = cacheService.deleteByPattern(pattern);
        log.info("批量删除缓存完成，删除 {} 个缓存项", deleted);
        return deleted;
    }
} 
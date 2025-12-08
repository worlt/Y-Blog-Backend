package com.blog.config;

import com.blog.service.CacheService;
import com.blog.service.impl.BlogCacheServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 缓存定时任务配置
 * 定期清理过期缓存，防止内存泄漏
 * 
 * @author Worlt
 * @date 2025/9/5
 */
@Configuration
@EnableScheduling
@Slf4j
public class CacheScheduleConfig {
    
    @Autowired
    private CacheService cacheService;
    
    @Autowired
    private BlogCacheServiceImpl blogCacheService;
    
    /**
     * 每小时清理一次过期缓存
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void cleanExpiredCache() {
        log.info("开始定时清理过期缓存");
        
        try {
            long cleaned = blogCacheService.cleanAllExpiredCache();
            log.info("定时缓存清理完成，清理 {} 个过期项", cleaned);
        } catch (Exception e) {
            log.error("定时缓存清理失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 每6小时输出缓存统计信息
     */
    @Scheduled(cron = "0 0 */6 * * ?")
    public void logCacheStats() {
        log.info("缓存统计信息");
        
        try {
            String statsReport = blogCacheService.getCacheStatsReport();
            log.info("\n{}", statsReport);
        } catch (Exception e) {
            log.error("获取缓存统计信息失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 每30分钟检查Redis连接状态
     */
    @Scheduled(cron = "0 */30 * * * ?")
    public void checkRedisHealth() {
        try {
            boolean isAvailable = cacheService.isRedisAvailable();
            if (isAvailable) {
                log.debug("Redis连接正常");
            } else {
                log.warn("Redis连接异常，已切换到本地缓存模式");
            }
        } catch (Exception e) {
            log.error("Redis健康检查失败: {}", e.getMessage(), e);
        }
    }
} 
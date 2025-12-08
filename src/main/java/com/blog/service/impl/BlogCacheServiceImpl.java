package com.blog.service.impl;

// 注意：实际使用时需要根据项目结构调整实体类导入路径
// import com.blog.entity.Blog;
// import com.blog.entity.User;
import com.blog.service.CacheService;
import com.blog.service.CacheStats;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 博客缓存策略实现
 * 管理热点数据缓存，包含降级处理
 * 
 * @author Worlt
 * @date 2025/9/5
 */
@Service
@Slf4j
public class BlogCacheServiceImpl {
    
    @Autowired
    private CacheService cacheService;
    
    @Autowired
    private BloomFilterServiceImpl bloomFilterService;
    
    @Autowired
    private HotKeyDetectorImpl hotKeyDetector;
    
    // 缓存键前缀
    private static final String BLOG_KEY_PREFIX = "blog:";
    private static final String USER_KEY_PREFIX = "user:";
    private static final String HOT_BLOGS_KEY = "hot:blogs";
    private static final String BLOG_STATS_PREFIX = "stats:blog:";
    private static final String USER_STATS_PREFIX = "stats:user:";
    private static final String CATEGORY_BLOGS_PREFIX = "category:blogs:";
    
    // 缓存过期时间配置
    private static final long BLOG_CACHE_MINUTES = 30; // 博客详情缓存30分钟
    private static final long USER_CACHE_MINUTES = 60; // 用户信息缓存1小时
    private static final long HOT_BLOGS_CACHE_MINUTES = 15; // 热门博客缓存15分钟
    private static final long STATS_CACHE_MINUTES = 10; // 统计数据缓存10分钟
    private static final long CATEGORY_CACHE_MINUTES = 20; // 分类博客缓存20分钟
    
    /**
     * 缓存博客详情
     * 
     * @param blogId 博客ID
     * @param blog 博客对象
     */
    public void cacheBlog(Long blogId, Object blog) {
        if (blogId == null || blog == null) {
            return;
        }
        
        String key = BLOG_KEY_PREFIX + blogId;
        
        // 添加到布隆过滤器
        bloomFilterService.add(key);
        
        boolean success = cacheService.set(key, blog, BLOG_CACHE_MINUTES, TimeUnit.MINUTES);
        
        if (success) {
            log.debug("博客缓存成功: blogId={}", blogId);
        } else {
            log.warn("博客缓存失败: blogId={}", blogId);
        }
    }
    
    /**
     * 获取缓存的博客详情
     * 
     * @param blogId 博客ID
     * @return 博客对象，未命中返回null
     */
    public Object getCachedBlog(Long blogId) {
        if (blogId == null) {
            return null;
        }
        
        String key = BLOG_KEY_PREFIX + blogId;
        
        // 记录访问，用于热点Key检测
        hotKeyDetector.recordAccess(key);
        
        // 防穿透：布隆过滤器预检查
        if (!bloomFilterService.mightContain(key)) {
            log.debug("布隆过滤器未命中，博客可能不存在: blogId={}", blogId);
            return null;
        }
        
        Object blog = cacheService.get(key, Object.class);
        
        if (blog != null) {
            log.debug("博客缓存命中: blogId={}", blogId);
        } else {
            log.debug("博客缓存未命中: blogId={}", blogId);
        }
        
        return blog;
    }
    
    /**
     * 缓存用户信息
     * 
     * @param userId 用户ID
     * @param user 用户对象
     */
    public void cacheUser(Long userId, Object user) {
        if (userId == null || user == null) {
            return;
        }
        
        String key = USER_KEY_PREFIX + userId;
        boolean success = cacheService.set(key, user, USER_CACHE_MINUTES, TimeUnit.MINUTES);
        
        if (success) {
            log.debug("用户缓存成功: userId={}", userId);
        } else {
            log.warn("用户缓存失败: userId={}", userId);
        }
    }
    
    /**
     * 获取缓存的用户信息
     * 
     * @param userId 用户ID
     * @return 用户对象，未命中返回null
     */
    public Object getCachedUser(Long userId) {
        if (userId == null) {
            return null;
        }
        
        String key = USER_KEY_PREFIX + userId;
        Object user = cacheService.get(key, Object.class);
        
        if (user != null) {
            log.debug("用户缓存命中: userId={}", userId);
        } else {
            log.debug("用户缓存未命中: userId={}", userId);
        }
        
        return user;
    }
    
    /**
     * 缓存热门博客列表
     * 
     * @param hotBlogs 热门博客列表
     */
    public void cacheHotBlogs(List<?> hotBlogs) {
        if (hotBlogs == null) {
            return;
        }
        
        boolean success = cacheService.set(HOT_BLOGS_KEY, hotBlogs, HOT_BLOGS_CACHE_MINUTES, TimeUnit.MINUTES);
        
        if (success) {
            log.info("热门博客缓存成功: {} 篇博客", hotBlogs.size());
        } else {
            log.warn("热门博客缓存失败");
        }
    }
    
    /**
     * 获取缓存的热门博客列表
     * 
     * @return 热门博客列表，未命中返回null
     */
    @SuppressWarnings("unchecked")
    public List<?> getCachedHotBlogs() {
        List<?> hotBlogs = (List<?>) cacheService.get(HOT_BLOGS_KEY, List.class);
        
        if (hotBlogs != null) {
            log.debug("热门博客缓存命中: {} 篇博客", hotBlogs.size());
        } else {
            log.debug("热门博客缓存未命中");
        }
        
        return hotBlogs;
    }
    
    /**
     * 缓存博客统计数据
     * 
     * @param blogId 博客ID
     * @param viewCount 浏览数
     * @param likeCount 点赞数
     * @param commentCount 评论数
     */
    public void cacheBlogStats(Long blogId, Long viewCount, Long likeCount, Long commentCount) {
        if (blogId == null) {
            return;
        }
        
        String key = BLOG_STATS_PREFIX + blogId;
        BlogStats stats = new BlogStats(viewCount, likeCount, commentCount);
        
        boolean success = cacheService.set(key, stats, STATS_CACHE_MINUTES, TimeUnit.MINUTES);
        
        if (success) {
            log.debug("博客统计缓存成功: blogId={}", blogId);
        } else {
            log.warn("博客统计缓存失败: blogId={}", blogId);
        }
    }
    
    /**
     * 获取缓存的博客统计数据
     * 
     * @param blogId 博客ID
     * @return 统计数据，未命中返回null
     */
    public BlogStats getCachedBlogStats(Long blogId) {
        if (blogId == null) {
            return null;
        }
        
        String key = BLOG_STATS_PREFIX + blogId;
        BlogStats stats = cacheService.get(key, BlogStats.class);
        
        if (stats != null) {
            log.debug("博客统计缓存命中: blogId={}", blogId);
        } else {
            log.debug("博客统计缓存未命中: blogId={}", blogId);
        }
        
        return stats;
    }
    
    /**
     * 缓存分类博客列表
     * 
     * @param categoryId 分类ID
     * @param blogs 博客列表
     */
    public void cacheCategoryBlogs(Long categoryId, List<?> blogs) {
        if (categoryId == null || blogs == null) {
            return;
        }
        
        String key = CATEGORY_BLOGS_PREFIX + categoryId;
        boolean success = cacheService.set(key, blogs, CATEGORY_CACHE_MINUTES, TimeUnit.MINUTES);
        
        if (success) {
            log.debug("分类博客缓存成功: categoryId={}, count={}", categoryId, blogs.size());
        } else {
            log.warn("分类博客缓存失败: categoryId={}", categoryId);
        }
    }
    
    /**
     * 获取缓存的分类博客列表
     * 
     * @param categoryId 分类ID
     * @return 博客列表，未命中返回null
     */
    @SuppressWarnings("unchecked")
    public List<?> getCachedCategoryBlogs(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        
        String key = CATEGORY_BLOGS_PREFIX + categoryId;
        List<?> blogs = (List<?>) cacheService.get(key, List.class);
        
        if (blogs != null) {
            log.debug("分类博客缓存命中: categoryId={}, count={}", categoryId, blogs.size());
        } else {
            log.debug("分类博客缓存未命中: categoryId={}", categoryId);
        }
        
        return blogs;
    }
    
    /**
     * 清除博客相关缓存
     * 
     * @param blogId 博客ID
     */
    public void evictBlogCache(Long blogId) {
        if (blogId == null) {
            return;
        }
        
        // 清除博客详情缓存
        cacheService.delete(BLOG_KEY_PREFIX + blogId);
        
        // 清除博客统计缓存
        cacheService.delete(BLOG_STATS_PREFIX + blogId);
        
        // 清除热门博客缓存（因为可能包含该博客）
        cacheService.delete(HOT_BLOGS_KEY);
        
        // 清除分类博客缓存（使用模式匹配）
        cacheService.deleteByPattern(CATEGORY_BLOGS_PREFIX + "*");
        
        log.info("清除博客相关缓存: blogId={}", blogId);
    }
    
    /**
     * 清除用户相关缓存
     * 
     * @param userId 用户ID
     */
    public void evictUserCache(Long userId) {
        if (userId == null) {
            return;
        }
        
        // 清除用户信息缓存
        cacheService.delete(USER_KEY_PREFIX + userId);
        
        // 清除用户统计缓存
        cacheService.delete(USER_STATS_PREFIX + userId);
        
        log.info("清除用户相关缓存: userId={}", userId);
    }
    
    /**
     * 清理所有过期缓存
     * 
     * @return 清理数量
     */
    public long cleanAllExpiredCache() {
        long cleaned = cacheService.cleanExpiredCache();
        log.info("缓存清理完成，清理 {} 个过期项", cleaned);
        return cleaned;
    }
    
    /**
     * 获取缓存统计信息
     * 
     * @return 缓存统计
     */
    public String getCacheStatsReport() {
        CacheStats stats = cacheService.getCacheStats();
        
        return String.format(
            "缓存统计报告:\n" +
            "Redis状态: %s\n" +
            "总缓存数: %d\n" +
            "命中次数: %d\n" +
            "未命中次数: %d\n" +
            "命中率: %.2f%%\n" +
            "内存使用: %.2fMB",
            stats.isRedisAvailable() ? "可用" : "不可用",
            stats.getTotalKeys(),
            stats.getHitCount(),
            stats.getMissCount(),
            stats.getHitRate() * 100,
            stats.getMemoryUsageMB()
        );
    }
    
    /**
     * 博客统计数据内部类
     */
    public static class BlogStats {
        private Long viewCount;
        private Long likeCount;
        private Long commentCount;
        
        public BlogStats() {}
        
        public BlogStats(Long viewCount, Long likeCount, Long commentCount) {
            this.viewCount = viewCount;
            this.likeCount = likeCount;
            this.commentCount = commentCount;
        }
        
        // Getters and Setters
        public Long getViewCount() { return viewCount; }
        public void setViewCount(Long viewCount) { this.viewCount = viewCount; }
        
        public Long getLikeCount() { return likeCount; }
        public void setLikeCount(Long likeCount) { this.likeCount = likeCount; }
        
        public Long getCommentCount() { return commentCount; }
        public void setCommentCount(Long commentCount) { this.commentCount = commentCount; }
    }
} 
package com.blog.service;

/**
 * 博客统计数据校正服务
 * 用于定期校正博客的点赞、点踩、收藏、分享和浏览数量
 * 
 * @Author worlt
 * @Date 2025/6/10 下午2:00
 */
public interface BlogStatsService {
    
    /**
     * 校正指定博客的统计数据
     * 
     * @param blogId 博客ID
     */
    void correctBlogStats(Long blogId);
    
    /**
     * 校正所有博客的统计数据
     */
    void correctAllBlogsStats();
} 
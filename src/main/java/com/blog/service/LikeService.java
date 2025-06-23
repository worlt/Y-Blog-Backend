package com.blog.service;

/**
 * @Author worlt
 * @Date 2025/6/9 下午9:23
 */
public interface LikeService {
    
    /**
     * 博客点赞/取消点赞
     * @param blogId 博客id
     * @param userId 用户id
     */
    void likeBlog(Long blogId, Long userId);
    
    /**
     * 评论点赞/取消点赞
     * @param commentId 评论id
     * @param userId 用户id
     */
    void likeComment(Long commentId, Long userId);
    
    /**
     * 检查用户是否已点赞博客
     * @param blogId 博客id
     * @param userId 用户id
     * @return 是否已点赞
     */
    boolean isLiked(Long blogId, Long userId);
} 
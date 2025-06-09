package com.blog.service;

/**
 * @Author worlt
 * @Date 2025/6/9 下午9:23
 */
public interface DislikeService {
    
    /**
     * 博客点踩/取消点踩
     * @param blogId 博客id
     * @param userId 用户id
     */
    void dislikeBlog(Long blogId, Long userId);
    
    /**
     * 评论点踩/取消点踩
     * @param commentId 评论id
     * @param userId 用户id
     */
    void dislikeComment(Long commentId, Long userId);
} 
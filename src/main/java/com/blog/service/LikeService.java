package com.blog.service;

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
} 
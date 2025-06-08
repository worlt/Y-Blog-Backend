package com.blog.service;

import com.blog.result.Result;

public interface DislikeService {
    
    /**
     * 博客点踩/取消点踩
     * @param blogId 博客id
     * @param userId 用户id
     * @return 结果
     */
    Result dislikeBlog(Long blogId, Long userId);
    
    /**
     * 评论点踩/取消点踩
     * @param commentId 评论id
     * @param userId 用户id
     * @return 结果
     */
    Result dislikeComment(Long commentId, Long userId);
} 
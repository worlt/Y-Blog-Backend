package com.blog.service;

import com.blog.pojo.dto.BlogPageQueryDTO;
import com.blog.pojo.vo.BlogVO;
import com.blog.result.PageResult;

import java.util.List;

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
    
    /**
     * 检查用户是否已点踩博客
     * @param blogId 博客id
     * @param userId 用户id
     * @return 是否已点踩
     */
    boolean isDisliked(Long blogId, Long userId);
    
    /**
     * 获取用户点踩的博客列表
     * @param userId 用户id
     * @return 点踩的博客列表
     */
    List<BlogVO> getUserDislikedBlogs(Long userId);
    
    /**
     * 分页查询用户点踩的博客列表
     * @param blogPageQueryDTO 分页查询参数
     * @return 分页结果
     */
    PageResult getUserDislikedBlogsPage(BlogPageQueryDTO blogPageQueryDTO);
} 
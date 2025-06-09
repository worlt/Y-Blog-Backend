package com.blog.service;

import com.blog.pojo.vo.BlogVO;

import java.util.List;

/**
 * 浏览记录服务接口
 * 
 * @Author worlt
 * @Date 2025/6/9 下午9:23
 */
public interface ViewService {
    
    /**
     * 记录用户浏览博客操作
     * 同一用户对同一博客只记录一次浏览量
     * 
     * @param blogId 博客ID
     * @param userId 用户ID
     */
    void recordView(Long blogId, Long userId);
    
    /**
     * 获取用户的浏览历史
     * 
     * @param userId 用户ID
     * @return 博客VO列表
     */
    List<BlogVO> getUserViewHistory(Long userId);
    
    /**
     * 检查用户是否已浏览过博客
     * 
     * @param blogId 博客ID
     * @param userId 用户ID
     * @return 是否已浏览
     */
    boolean hasViewed(Long blogId, Long userId);
} 
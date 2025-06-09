package com.blog.service;

import com.blog.pojo.vo.ShareStatisticsVO;
import com.blog.pojo.vo.ShareVO;

import java.util.List;

/**
 * @Author worlt
 * @Date 2025/6/9 下午9:23
 */
public interface ShareService {
    
    /**
     * 分享博客
     * @param blogId 博客id
     * @param userId 用户id
     * @param platform 分享平台
     */
    void shareBlog(Long blogId, Long userId, String platform);
    
    /**
     * 获取博客的分享统计
     * @param blogId 博客id
     * @return 分享统计
     */
    ShareStatisticsVO getShareStatistics(Long blogId);
    
    /**
     * 获取支持的分享平台列表
     * @return 平台列表
     */
    List<String> getSupportedPlatforms();
    
    /**
     * 获取用户的分享记录
     * @param userId 用户id
     * @return 分享记录
     */
    List<ShareVO> getUserShares(Long userId);
    
    /**
     * 获取博客的分享记录
     * @param blogId 博客id
     * @return 分享记录
     */
    List<ShareVO> getBlogShares(Long blogId);
} 
package com.blog.service;

import com.blog.pojo.dto.BlogPageQueryDTO;
import com.blog.pojo.vo.BlogVO;
import com.blog.result.PageResult;

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
     * 同一用户或IP对同一博客只记录一次浏览量
     * 
     * @param blogId 博客ID
     * @param userId 用户ID
     * @param ip     用户IP地址
     * @return 是否增加浏览量
     */
    boolean recordView(Long blogId, Long userId, String ip);

    /**
     * 获取用户的浏览历史
     * 
     * @param userId 用户ID
     * @return 博客VO列表
     */
    List<BlogVO> getUserViewHistory(Long userId);

    /**
     * 分页查询用户的浏览历史
     * 
     * @param blogPageQueryDTO 分页查询参数
     * @return 分页结果
     */
    PageResult getUserViewHistoryPage(BlogPageQueryDTO blogPageQueryDTO);

    /**
     * 检查用户是否已浏览过博客
     * 
     * @param blogId 博客ID
     * @param userId 用户ID
     * @return 是否已浏览
     */
    boolean hasViewed(Long blogId, Long userId);
}
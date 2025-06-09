package com.blog.service;

import com.blog.pojo.vo.UserFollowVO;

import java.util.List;

public interface UserFollowService {
    
    /**
     * 关注/取消关注用户
     * @param followUserId 被关注用户id
     * @param userId 当前用户id
     */
    void followUser(Long followUserId, Long userId);
    
    /**
     * 获取用户关注列表
     * @param userId 用户id
     * @param currentUserId 当前登录用户id
     * @return 关注列表
     */
    List<UserFollowVO> getFollowList(Long userId, Long currentUserId);
    
    /**
     * 获取用户粉丝列表
     * @param userId 用户id
     * @param currentUserId 当前登录用户id
     * @return 粉丝列表
     */
    List<UserFollowVO> getFansList(Long userId, Long currentUserId);
    
    /**
     * 检查用户是否已关注指定用户
     * @param followUserId 被关注用户id
     * @param userId 当前用户id
     * @return 是否已关注
     */
    boolean isFollowed(Long followUserId, Long userId);
} 
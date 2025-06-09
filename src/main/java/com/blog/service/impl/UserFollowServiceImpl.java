package com.blog.service.impl;

import com.blog.constant.MessageConstant;
import com.blog.mapper.UserFollowMapper;
import com.blog.mapper.UserMapper;
import com.blog.pojo.entity.User;
import com.blog.pojo.entity.UserFollow;
import com.blog.pojo.vo.UserFollowVO;
import com.blog.service.UserFollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserFollowServiceImpl implements UserFollowService {

    @Autowired
    private UserFollowMapper userFollowMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public void followUser(Long followUserId, Long userId) {
        // 1. 不能关注自己
        if (followUserId.equals(userId)) {
            throw new RuntimeException(MessageConstant.FOLLOW_SELF_FAIL);
        }
        
        // 2. 检查被关注的用户是否存在
        User followUser = userMapper.getById(followUserId);
        if (followUser == null) {
            throw new RuntimeException(MessageConstant.USER_NOT_FOUND);
        }
        
        // 3. 检查是否已经关注
        UserFollow userFollow = userFollowMapper.findByUserIdAndFollowUserId(userId, followUserId);
        
        if (userFollow != null) {
            // 已关注，取消关注
            int rows = userFollowMapper.deleteByUserIdAndFollowUserId(userId, followUserId);
            log.info("用户{}取消了对用户{}的关注", userId, followUserId);
            if (rows <= 0) {
                throw new RuntimeException(MessageConstant.UNFOLLOW_FAIL);
            }
            
            // 更新被关注用户的粉丝数
            updateUserFansCount(followUserId);
        } else {
            // 未关注，添加关注
            UserFollow newUserFollow = UserFollow.builder()
                    .userId(userId)
                    .followUserId(followUserId)
                    .followTime(LocalDateTime.now())
                    .build();
            
            int rows = userFollowMapper.insert(newUserFollow);
            log.info("用户{}关注了用户{}", userId, followUserId);
            if (rows <= 0) {
                throw new RuntimeException(MessageConstant.FOLLOW_FAIL);
            }
            
            // 更新被关注用户的粉丝数
            updateUserFansCount(followUserId);
        }
    }

    @Override
    public List<UserFollowVO> getFollowList(Long userId, Long currentUserId) {
        // 1. 获取用户关注列表
        List<UserFollow> follows = userFollowMapper.findFollowsByUserId(userId);
        List<UserFollowVO> followVOS = new ArrayList<>();
        
        // 2. 转换为VO对象
        for (UserFollow follow : follows) {
            // 获取被关注用户信息
            User followUser = userMapper.getById(follow.getFollowUserId());
            if (followUser == null) {
                continue;
            }
            
            // 检查当前登录用户是否也关注了这个用户
            boolean isFollowed = false;
            if (currentUserId != null) {
                isFollowed = userFollowMapper.findByUserIdAndFollowUserId(currentUserId, follow.getFollowUserId()) != null;
            }
            
            // 构建VO对象
            UserFollowVO vo = UserFollowVO.builder()
                    .userId(followUser.getId())
                    .username(followUser.getUsername())
                    .avatar(followUser.getAvatar())
                    .gender(followUser.getGender())
                    .fans(followUser.getFans())
                    .followTime(follow.getFollowTime())
                    .isFollowed(isFollowed)
                    .build();
            
            followVOS.add(vo);
        }
        
        return followVOS;
    }

    @Override
    public List<UserFollowVO> getFansList(Long userId, Long currentUserId) {
        // 1. 获取用户粉丝列表
        List<UserFollow> fans = userFollowMapper.findFansByFollowUserId(userId);
        List<UserFollowVO> fanVOS = new ArrayList<>();
        
        // 2. 转换为VO对象
        for (UserFollow fan : fans) {
            // 获取粉丝用户信息
            User fanUser = userMapper.getById(fan.getUserId());
            if (fanUser == null) {
                continue;
            }
            
            // 检查当前登录用户是否关注了这个粉丝
            boolean isFollowed = false;
            if (currentUserId != null) {
                isFollowed = userFollowMapper.findByUserIdAndFollowUserId(currentUserId, fan.getUserId()) != null;
            }
            
            // 构建VO对象
            UserFollowVO vo = UserFollowVO.builder()
                    .userId(fanUser.getId())
                    .username(fanUser.getUsername())
                    .avatar(fanUser.getAvatar())
                    .gender(fanUser.getGender())
                    .fans(fanUser.getFans())
                    .followTime(fan.getFollowTime())
                    .isFollowed(isFollowed)
                    .build();
            
            fanVOS.add(vo);
        }
        
        return fanVOS;
    }

    @Override
    public boolean isFollowed(Long followUserId, Long userId) {
        return userFollowMapper.findByUserIdAndFollowUserId(userId, followUserId) != null;
    }
    
    /**
     * 更新用户粉丝数量
     * @param userId 用户id
     */
    private void updateUserFansCount(Long userId) {
        int count = userFollowMapper.countFansByFollowUserId(userId);
        User user = userMapper.getById(userId);
        if (user != null) {
            user.setFans((long) count);
            userMapper.update(user);
        }
    }
} 
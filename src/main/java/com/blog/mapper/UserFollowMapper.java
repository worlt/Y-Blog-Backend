package com.blog.mapper;

import com.blog.pojo.entity.UserFollow;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @Author worlt
 * @Date 2025/6/9 下午9:23
 */
@Mapper
public interface UserFollowMapper {
    
    /**
     * 添加关注记录
     * @param userFollow 关注对象
     * @return 影响的行数
     */
    int insert(UserFollow userFollow);
    
    /**
     * 查询用户是否关注了指定用户
     * @param userId 用户id
     * @param followUserId 被关注用户id
     * @return 关注对象
     */
    @Select("select * from user_follow where user_id = #{userId} and followed_user_id = #{followUserId}")
    UserFollow findByUserIdAndFollowUserId(Long userId, Long followUserId);
    
    /**
     * 删除关注记录
     * @param userId 用户id
     * @param followUserId 被关注用户id
     * @return 影响的行数
     */
    @Delete("delete from user_follow where user_id = #{userId} and followed_user_id = #{followUserId}")
    int deleteByUserIdAndFollowUserId(Long userId, Long followUserId);
    
    /**
     * 查询用户的关注列表
     * @param userId 用户id
     * @return 关注列表
     */
    @Select("select * from user_follow where user_id = #{userId} order by follow_time desc")
    List<UserFollow> findFollowsByUserId(Long userId);
    
    /**
     * 查询用户的粉丝列表
     * @param followUserId 被关注用户id
     * @return 粉丝列表
     */
    @Select("select * from user_follow where followed_user_id = #{followUserId} order by follow_time desc")
    List<UserFollow> findFansByFollowUserId(Long followUserId);
    
    /**
     * 统计用户关注的人数
     * @param userId 用户id
     * @return 关注人数
     */
    @Select("select count(*) from user_follow where user_id = #{userId}")
    int countFollowsByUserId(Long userId);
    
    /**
     * 统计用户关注的人数（别名，与getUserStats方法中保持一致）
     * @param userId 用户id
     * @return 关注人数
     */
    @Select("select count(*) from user_follow where user_id = #{userId}")
    Integer countUserFollows(Long userId);
    
    /**
     * 统计用户的粉丝数
     * @param followUserId 被关注用户id
     * @return 粉丝数
     */
    @Select("select count(*) from user_follow where followed_user_id = #{followUserId}")
    int countFansByFollowUserId(Long followUserId);
} 
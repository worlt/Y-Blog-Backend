package com.blog.mapper;

import com.blog.pojo.entity.Share;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @Author worlt
 * @Date 2025/6/9 下午9:23
 */
@Mapper
public interface ShareMapper {
    
    /**
     * 添加分享记录
     * @param share 分享对象
     * @return 影响的行数
     */
    int insert(Share share);
    
    /**
     * 查询博客的分享记录
     * @param blogId 博客id
     * @return 分享列表
     */
    @Select("select * from share where blog_id = #{blogId} order by share_time desc")
    List<Share> findByBlogId(Long blogId);
    
    /**
     * 查询用户的分享记录
     * @param userId 用户id
     * @return 分享列表
     */
    @Select("select * from share where user_id = #{userId} order by share_time desc")
    List<Share> findByUserId(Long userId);
    
    /**
     * 统计博客被分享的次数
     * @param blogId 博客id
     * @return 分享次数
     */
    @Select("select count(*) from share where blog_id = #{blogId}")
    int countByBlogId(Long blogId);
    
    /**
     * 统计博客在特定平台上被分享的次数
     * @param blogId 博客id
     * @param platform 平台
     * @return 分享次数
     */
    @Select("select count(*) from share where blog_id = #{blogId} and share_platform = #{platform}")
    int countByBlogIdAndPlatform(Long blogId, String platform);
    
    /**
     * 获取分享平台列表（去重）
     * @return 平台列表
     */
    @Select("select distinct share_platform from share order by share_platform")
    List<String> findAllPlatforms();
} 
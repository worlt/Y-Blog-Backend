package com.blog.mapper;

import com.blog.pojo.entity.Like;
import org.apache.ibatis.annotations.*;

/**
 * @Author worlt
 * @Date 2025/6/9 下午9:23
 */
@Mapper
public interface LikeMapper {
    
    /**
     * 查询用户是否点赞了博客
     * @param userId 用户id
     * @param blogId 博客id
     * @return 点赞对象
     */
    @Select("select * from `like` where user_id = #{userId} and blog_id = #{blogId} and comment_id is null")
    Like findByUserIdAndBlogId(Long userId, Long blogId);
    
    /**
     * 查询用户是否点赞了评论
     * @param userId 用户id
     * @param commentId 评论id
     * @return 点赞对象
     */
    @Select("select * from `like` where user_id = #{userId} and comment_id = #{commentId}")
    Like findByUserIdAndCommentId(Long userId, Long commentId);
    
    /**
     * 删除博客点赞记录
     * @param userId 用户id
     * @param blogId 博客id
     * @return 影响的行数
     */
    @Delete("delete from `like` where user_id = #{userId} and blog_id = #{blogId} and comment_id is null")
    int deleteByUserIdAndBlogId(Long userId, Long blogId);
    
    /**
     * 删除评论点赞记录
     * @param userId 用户id
     * @param commentId 评论id
     * @return 影响的行数
     */
    @Delete("delete from `like` where user_id = #{userId} and comment_id = #{commentId}")
    int deleteByUserIdAndCommentId(Long userId, Long commentId);
    
    /**
     * 添加点赞记录
     * @param like 点赞对象
     * @return 影响的行数
     */
    int insert(Like like);
    
    /**
     * 统计博客被点赞的次数
     * @param blogId 博客id
     * @return 点赞次数
     */
    @Select("select count(*) from `like` where blog_id = #{blogId} and comment_id is null")
    int countByBlogId(Long blogId);
    
    /**
     * 统计评论被点赞的次数
     * @param commentId 评论id
     * @return 点赞次数
     */
    @Select("select count(*) from `like` where comment_id = #{commentId}")
    int countByCommentId(Long commentId);
} 
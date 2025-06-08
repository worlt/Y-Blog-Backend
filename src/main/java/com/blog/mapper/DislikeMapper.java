package com.blog.mapper;

import com.blog.pojo.entity.Dislike;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DislikeMapper {
    
    /**
     * 查询用户是否点踩了博客
     * @param userId 用户id
     * @param blogId 博客id
     * @return 点踩对象
     */
    @Select("select * from dislike where user_id = #{userId} and blog_id = #{blogId} and comment_id is null")
    Dislike findByUserIdAndBlogId(Long userId, Long blogId);
    
    /**
     * 查询用户是否点踩了评论
     * @param userId 用户id
     * @param commentId 评论id
     * @return 点踩对象
     */
    @Select("select * from dislike where user_id = #{userId} and comment_id = #{commentId}")
    Dislike findByUserIdAndCommentId(Long userId, Long commentId);
    
    /**
     * 删除博客点踩记录
     * @param userId 用户id
     * @param blogId 博客id
     * @return 影响的行数
     */
    @Delete("delete from dislike where user_id = #{userId} and blog_id = #{blogId} and comment_id is null")
    int deleteByUserIdAndBlogId(Long userId, Long blogId);
    
    /**
     * 删除评论点踩记录
     * @param userId 用户id
     * @param commentId 评论id
     * @return 影响的行数
     */
    @Delete("delete from dislike where user_id = #{userId} and comment_id = #{commentId}")
    int deleteByUserIdAndCommentId(Long userId, Long commentId);
    
    /**
     * 添加点踩记录
     * @param dislike 点踩对象
     * @return 影响的行数
     */
    @Insert("insert into dislike(user_id, blog_id, comment_id, dislike_time) values(#{userId}, #{blogId}, #{commentId}, #{dislikeTime})")
    int insert(Dislike dislike);
} 
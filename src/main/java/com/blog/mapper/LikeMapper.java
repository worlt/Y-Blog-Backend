package com.blog.mapper;

import com.blog.pojo.entity.Like;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LikeMapper {
    
    /**
     * 查询用户是否点赞了博客
     * @param userId 用户id
     * @param blogId 博客id
     * @return 点赞对象
     */
    @Select("select * from like where user_id = #{userId} and blog_id = #{blogId} and comment_id is null")
    Like findByUserIdAndBlogId(Long userId, Long blogId);
    
    /**
     * 查询用户是否点赞了评论
     * @param userId 用户id
     * @param commentId 评论id
     * @return 点赞对象
     */
    @Select("select * from like where user_id = #{userId} and comment_id = #{commentId}")
    Like findByUserIdAndCommentId(Long userId, Long commentId);
    
    /**
     * 删除博客点赞记录
     * @param userId 用户id
     * @param blogId 博客id
     * @return 影响的行数
     */
    @Delete("delete from like where user_id = #{userId} and blog_id = #{blogId} and comment_id is null")
    int deleteByUserIdAndBlogId(Long userId, Long blogId);
    
    /**
     * 删除评论点赞记录
     * @param userId 用户id
     * @param commentId 评论id
     * @return 影响的行数
     */
    @Delete("delete from like where user_id = #{userId} and comment_id = #{commentId}")
    int deleteByUserIdAndCommentId(Long userId, Long commentId);
    
    /**
     * 添加点赞记录
     * @param like 点赞对象
     * @return 影响的行数
     */
    @Insert("insert into like(user_id, blog_id, comment_id, like_time) values(#{userId}, #{blogId}, #{commentId}, #{likeTime})")
    int insert(Like like);
} 
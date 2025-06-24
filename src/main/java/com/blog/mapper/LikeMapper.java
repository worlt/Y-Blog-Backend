package com.blog.mapper;

import com.blog.pojo.dto.BlogPageQueryDTO;
import com.blog.pojo.entity.Like;
import com.blog.pojo.vo.BlogVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.*;

import java.util.List;

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
    
    /**
     * 获取用户点赞的博客列表
     * @param userId 用户ID
     * @return 博客VO列表
     */
    List<BlogVO> getUserLikedBlogs(Long userId);
    
    /**
     * 分页查询用户点赞的博客列表
     * @param blogPageQueryDTO 分页查询参数
     * @return 博客VO列表（分页）
     */
    Page<BlogVO> getUserLikedBlogsPage(BlogPageQueryDTO blogPageQueryDTO);
    
    /**
     * 统计用户收到的点赞数量（包括博客和评论）
     * @param userId 用户ID
     * @return 用户收到的点赞总数
     */
    @Select("SELECT COUNT(*) FROM `like` l " +
            "LEFT JOIN blog b ON l.blog_id = b.id " +
            "LEFT JOIN comment c ON l.comment_id = c.id " +
            "WHERE b.user_id = #{userId} OR c.user_id = #{userId}")
    Integer countUserReceived(Long userId);
} 
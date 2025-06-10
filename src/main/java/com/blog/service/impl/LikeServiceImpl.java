package com.blog.service.impl;

import com.blog.constant.MessageConstant;
import com.blog.context.BaseContext;
import com.blog.mapper.BlogMapper;
import com.blog.mapper.DislikeMapper;
import com.blog.mapper.LikeMapper;
import com.blog.pojo.entity.Blog;
import com.blog.pojo.entity.Dislike;
import com.blog.pojo.entity.Like;
import com.blog.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 点赞服务实现类
 *
 * @Author worlt
 * @Date 2025/6/9 下午9:23
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LikeServiceImpl implements LikeService {

    @Autowired
    private LikeMapper likeMapper;
    @Autowired
    private DislikeMapper dislikeMapper;
    @Autowired
    private BlogMapper blogMapper;

    /**
     * 点赞或取消点赞博客
     * 
     * @param blogId 博客ID
     * @param userId 用户ID
     */
    @Override
    @Transactional
    public void likeBlog(Long blogId, Long userId) {
        // 1. 检查博客是否存在
        Blog blog = blogMapper.getById(blogId);
        if (blog == null) {
            throw new RuntimeException(MessageConstant.BLOG_NOT_FOUND);
        }
        
        // 2. 检查是否点踩了该博客
        if (dislikeMapper.findByUserIdAndBlogId(userId, blogId) != null) {
            // 如果已经点踩，先删除点踩记录
            dislikeMapper.deleteByUserIdAndBlogId(userId, blogId);
            log.info("用户{}取消了对博客{}的点踩", userId, blogId);
            
            // 更新博客点踩数量
            updateBlogDislikeCount(blogId);
        }

        // 3. 检查是否已经点赞
        Like like = likeMapper.findByUserIdAndBlogId(userId, blogId);
        
        if (like != null) {
            // 已点赞，取消点赞
            int rows = likeMapper.deleteByUserIdAndBlogId(userId, blogId);
            log.info("用户{}取消了对博客{}的点赞", userId, blogId);
            if (rows <= 0) {
                throw new RuntimeException(MessageConstant.UNLIKE_FAIL);
            }
            
            // 更新博客点赞数量
            updateBlogLikeCount(blogId);
        } else {
            // 未点赞，添加点赞
            Like newLike = Like.builder()
                    .userId(userId)
                    .blogId(blogId)
                    .commentId(null) // 博客点赞，评论ID为空
                    .likeTime(LocalDateTime.now())
                    .build();
            
            int rows = likeMapper.insert(newLike);
            log.info("用户{}点赞了博客{}", userId, blogId);
            if (rows <= 0) {
                throw new RuntimeException(MessageConstant.LIKE_FAIL);
            }
            
            // 更新博客点赞数量
            updateBlogLikeCount(blogId);
        }
    }

    /**
     * 点赞或取消点赞评论
     * 
     * @param commentId 评论ID
     * @param userId 用户ID
     */
    @Override
    @Transactional
    public void likeComment(Long commentId, Long userId) {
        // 1. 检查是否点踩了该评论
        if (dislikeMapper.findByUserIdAndCommentId(userId, commentId) != null) {
            // 如果已经点踩，先删除点踩记录
            dislikeMapper.deleteByUserIdAndCommentId(userId, commentId);
            log.info("用户{}取消了对评论{}的点踩", userId, commentId);
        }

        // 2. 检查是否已经点赞
        Like like = likeMapper.findByUserIdAndCommentId(userId, commentId);
        
        if (like != null) {
            // 已点赞，取消点赞
            int rows = likeMapper.deleteByUserIdAndCommentId(userId, commentId);
            log.info("用户{}取消了对评论{}的点赞", userId, commentId);
            if (rows <= 0) {
                throw new RuntimeException(MessageConstant.UNLIKE_FAIL);
            }
        } else {
            // 未点赞，添加点赞
            Like newLike = Like.builder()
                    .userId(userId)
                    .blogId(null) // 评论点赞，博客ID为空
                    .commentId(commentId)
                    .likeTime(LocalDateTime.now())
                    .build();
            
            int rows = likeMapper.insert(newLike);
            log.info("用户{}点赞了评论{}", userId, commentId);
            if (rows <= 0) {
                throw new RuntimeException(MessageConstant.LIKE_FAIL);
            }
        }
    }
    
    /**
     * 更新博客点赞数量
     * 
     * @param blogId 博客ID
     */
    private void updateBlogLikeCount(Long blogId) {
        try {
            // 获取当前用户ID
            Long userId = BaseContext.getCurrentId();
            
            // 判断操作是添加还是删除点赞，根据likeMapper中的记录确定
            Like like = likeMapper.findByUserIdAndBlogId(userId, blogId);
            
            if (like != null) {
                // 添加了点赞，增加点赞计数
                blogMapper.incrementLikes(blogId);
                log.info("更新博客{}点赞量+1", blogId);
            } else {
                // 取消了点赞，减少点赞计数
                blogMapper.decrementLikes(blogId);
                log.info("更新博客{}点赞量-1", blogId);
            }
        } catch (Exception e) {
            log.error("更新博客点赞数量失败", e);
        }
    }
    
    /**
     * 更新博客点踩数量
     * 
     * @param blogId 博客ID
     */
    private void updateBlogDislikeCount(Long blogId) {
        try {
            // 判断操作是添加还是删除点踩，根据dislikeMapper中的记录确定
            Dislike dislike = dislikeMapper.findByUserIdAndBlogId(BaseContext.getCurrentId(), blogId);
            
            if (dislike != null) {
                // 添加了点踩，增加点踩计数
                blogMapper.incrementDislikes(blogId);
                log.info("更新博客{}点踩量+1", blogId);
            } else {
                // 取消了点踩，减少点踩计数
                blogMapper.decrementDislikes(blogId);
                log.info("更新博客{}点踩量-1", blogId);
            }
        } catch (Exception e) {
            log.error("更新博客点踩数量失败", e);
        }
    }
} 
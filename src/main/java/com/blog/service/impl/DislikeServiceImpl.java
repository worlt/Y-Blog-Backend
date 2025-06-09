package com.blog.service.impl;

import com.blog.constant.MessageConstant;
import com.blog.context.BaseContext;
import com.blog.mapper.BlogMapper;
import com.blog.mapper.DislikeMapper;
import com.blog.mapper.LikeMapper;
import com.blog.pojo.entity.Blog;
import com.blog.pojo.entity.Dislike;
import com.blog.pojo.entity.Like;
import com.blog.service.DislikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 点踩服务实现类
 *
 * @Author worlt
 * @Date 2025/6/9 下午9:23
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DislikeServiceImpl implements DislikeService {

    @Autowired
    private DislikeMapper dislikeMapper;
    @Autowired
    private LikeMapper likeMapper;
    @Autowired
    private BlogMapper blogMapper;

    /**
     * 点踩或取消点踩博客
     * 
     * @param blogId 博客ID
     * @param userId 用户ID
     */
    @Override
    @Transactional
    public void dislikeBlog(Long blogId, Long userId) {
        // 1. 检查博客是否存在
        Blog blog = blogMapper.getById(blogId);
        if (blog == null) {
            throw new RuntimeException(MessageConstant.BLOG_NOT_FOUND);
        }
        
        // 2. 检查是否点赞了该博客
        if (likeMapper.findByUserIdAndBlogId(userId, blogId) != null) {
            // 如果已经点赞，先删除点赞记录
            likeMapper.deleteByUserIdAndBlogId(userId, blogId);
            log.info("用户{}取消了对博客{}的点赞", userId, blogId);
            
            // 更新博客点赞数量
            updateBlogLikeCount(blogId);
        }

        // 3. 检查是否已经点踩
        Dislike dislike = dislikeMapper.findByUserIdAndBlogId(userId, blogId);
        
        if (dislike != null) {
            // 已点踩，取消点踩
            int rows = dislikeMapper.deleteByUserIdAndBlogId(userId, blogId);
            log.info("用户{}取消了对博客{}的点踩", userId, blogId);
            if (rows <= 0) {
                throw new RuntimeException(MessageConstant.UNDISLIKE_FAIL);
            }
            
            // 更新博客点踩数量
            updateBlogDislikeCount(blogId);
        } else {
            // 未点踩，添加点踩
            Dislike newDislike = Dislike.builder()
                    .userId(userId)
                    .blogId(blogId)
                    .commentId(null) // 博客点踩，评论ID为空
                    .dislikeTime(LocalDateTime.now())
                    .build();
            
            int rows = dislikeMapper.insert(newDislike);
            log.info("用户{}点踩了博客{}", userId, blogId);
            if (rows <= 0) {
                throw new RuntimeException(MessageConstant.DISLIKE_FAIL);
            }
            
            // 更新博客点踩数量
            updateBlogDislikeCount(blogId);
        }
    }

    /**
     * 点踩或取消点踩评论
     * 
     * @param commentId 评论ID
     * @param userId 用户ID
     */
    @Override
    @Transactional
    public void dislikeComment(Long commentId, Long userId) {
        // 1. 检查是否点赞了该评论
        if (likeMapper.findByUserIdAndCommentId(userId, commentId) != null) {
            // 如果已经点赞，先删除点赞记录
            likeMapper.deleteByUserIdAndCommentId(userId, commentId);
            log.info("用户{}取消了对评论{}的点赞", userId, commentId);
        }

        // 2. 检查是否已经点踩
        Dislike dislike = dislikeMapper.findByUserIdAndCommentId(userId, commentId);
        
        if (dislike != null) {
            // 已点踩，取消点踩
            int rows = dislikeMapper.deleteByUserIdAndCommentId(userId, commentId);
            log.info("用户{}取消了对评论{}的点踩", userId, commentId);
            if (rows <= 0) {
                throw new RuntimeException(MessageConstant.UNDISLIKE_FAIL);
            }
        } else {
            // 未点踩，添加点踩
            Dislike newDislike = Dislike.builder()
                    .userId(userId)
                    .blogId(null) // 评论点踩，博客ID为空
                    .commentId(commentId)
                    .dislikeTime(LocalDateTime.now())
                    .build();
            
            int rows = dislikeMapper.insert(newDislike);
            log.info("用户{}点踩了评论{}", userId, commentId);
            if (rows <= 0) {
                throw new RuntimeException(MessageConstant.DISLIKE_FAIL);
            }
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
    
    /**
     * 更新博客点赞数量
     * 
     * @param blogId 博客ID
     */
    private void updateBlogLikeCount(Long blogId) {
        try {
            // 判断操作是添加还是删除点赞，根据likeMapper中的记录确定
            Like like = likeMapper.findByUserIdAndBlogId(BaseContext.getCurrentId(), blogId);
            
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
} 
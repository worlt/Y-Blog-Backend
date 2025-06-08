package com.blog.service.impl;

import com.blog.constant.MessageConstant;
import com.blog.mapper.DislikeMapper;
import com.blog.mapper.LikeMapper;
import com.blog.pojo.entity.Like;
import com.blog.result.Result;
import com.blog.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeServiceImpl implements LikeService {

    private final LikeMapper likeMapper;
    private final DislikeMapper dislikeMapper;

    @Override
    @Transactional
    public Result likeBlog(Long blogId, Long userId) {
        // 1. 检查是否点踩了该博客
        if (dislikeMapper.findByUserIdAndBlogId(userId, blogId) != null) {
            // 如果已经点踩，先删除点踩记录
            dislikeMapper.deleteByUserIdAndBlogId(userId, blogId);
            log.info("用户{}取消了对博客{}的点踩", userId, blogId);
        }

        // 2. 检查是否已经点赞
        Like like = likeMapper.findByUserIdAndBlogId(userId, blogId);
        
        if (like != null) {
            // 已点赞，取消点赞
            int rows = likeMapper.deleteByUserIdAndBlogId(userId, blogId);
            log.info("用户{}取消了对博客{}的点赞", userId, blogId);
            if (rows <= 0) {
                throw new RuntimeException(MessageConstant.UNLIKE_FAIL);
            }
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
        }
        
        return Result.success();
    }

    @Override
    @Transactional
    public Result likeComment(Long commentId, Long userId) {
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
        
        return Result.success();
    }
} 
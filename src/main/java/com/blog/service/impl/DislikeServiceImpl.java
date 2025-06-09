package com.blog.service.impl;

import com.blog.constant.MessageConstant;
import com.blog.mapper.DislikeMapper;
import com.blog.mapper.LikeMapper;
import com.blog.pojo.entity.Dislike;
import com.blog.service.DislikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class DislikeServiceImpl implements DislikeService {

    private final DislikeMapper dislikeMapper;
    private final LikeMapper likeMapper;

    @Override
    @Transactional
    public void dislikeBlog(Long blogId, Long userId) {
        // 1. 检查是否点赞了该博客
        if (likeMapper.findByUserIdAndBlogId(userId, blogId) != null) {
            // 如果已经点赞，先删除点赞记录
            likeMapper.deleteByUserIdAndBlogId(userId, blogId);
            log.info("用户{}取消了对博客{}的点赞", userId, blogId);
        }

        // 2. 检查是否已经点踩
        Dislike dislike = dislikeMapper.findByUserIdAndBlogId(userId, blogId);
        
        if (dislike != null) {
            // 已点踩，取消点踩
            int rows = dislikeMapper.deleteByUserIdAndBlogId(userId, blogId);
            log.info("用户{}取消了对博客{}的点踩", userId, blogId);
            if (rows <= 0) {
                throw new RuntimeException(MessageConstant.UNDISLIKE_FAIL);
            }
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
        }
    }

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
} 
package com.blog.service.impl;

import com.blog.mapper.BlogMapper;
import com.blog.mapper.CommentMapper;
import com.blog.mapper.LikeMapper;
import com.blog.mapper.CollectionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 数据校正服务，定期校对文章的统计数据
 *
 * @Author worlt
 * @Date 2025/6/10 下午8:00
 */
@Service
@Slf4j
public class DataUpdateService {

    @Autowired
    private BlogMapper blogMapper;

    @Autowired
    private LikeMapper likeMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CollectionMapper collectionMapper;

    /**
     * 每天凌晨3点执行数据校正
     */
    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void correctBlogStatistics() {
        log.info("开始执行博客数据校正...");
        try {
            // 校正点赞数
            correctLikesCount();
            
            // 校正收藏数
            correctCollectionsCount();
            
            // 校正评论数
            correctCommentsCount();
            
            log.info("博客数据校正完成");
        } catch (Exception e) {
            log.error("博客数据校正失败", e);
            throw e;
        }
    }

    /**
     * 校正点赞数
     */
    private void correctLikesCount() {
        log.info("校正点赞数...");
        int count = blogMapper.correctLikesCount();
        log.info("校正点赞数完成，更新了{}条数据", count);
    }

    /**
     * 校正收藏数
     */
    private void correctCollectionsCount() {
        log.info("校正收藏数...");
        int count = blogMapper.correctCollectionsCount();
        log.info("校正收藏数完成，更新了{}条数据", count);
    }

    /**
     * 校正评论数
     */
    private void correctCommentsCount() {
        log.info("校正评论数...");
        int count = blogMapper.correctCommentsCount();
        log.info("校正评论数完成，更新了{}条数据", count);
    }
} 
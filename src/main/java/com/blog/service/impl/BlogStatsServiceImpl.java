package com.blog.service.impl;

import com.blog.mapper.*;
import com.blog.pojo.entity.Blog;
import com.blog.service.BlogStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 博客统计数据校正服务实现类
 * 
 * @Author worlt
 * @Date 2025/6/10 下午2:00
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BlogStatsServiceImpl implements BlogStatsService {

    @Autowired
    private BlogMapper blogMapper;
    
    @Autowired
    private ViewMapper viewMapper;
    
    @Autowired
    private LikeMapper likeMapper;
    
    @Autowired
    private DislikeMapper dislikeMapper;
    
    @Autowired
    private CollectionMapper collectionMapper;
    
    @Autowired
    private ShareMapper shareMapper;
    
    /**
     * 校正指定博客的统计数据
     * 该方法会从各个计数表中统计准确数量并更新博客表
     * 
     * @param blogId 博客ID
     */
    @Override
    @Transactional
    public void correctBlogStats(Long blogId) {
        try {
            log.info("开始校正博客{}的统计数据", blogId);
            
            // 获取博客
            Blog blog = blogMapper.getById(blogId);
            if (blog == null) {
                log.error("博客{}不存在，无法校正统计数据", blogId);
                return;
            }
            
            // 统计各指标数量
            int viewsCount = viewMapper.countByBlogId(blogId);
            int likesCount = likeMapper.countByBlogId(blogId);
            int dislikesCount = dislikeMapper.countByBlogId(blogId);
            int collectionsCount = collectionMapper.countByBlogId(blogId);
            int sharesCount = shareMapper.countByBlogId(blogId);
            
            // 更新博客统计数据
            Blog updateBlog = new Blog();
            updateBlog.setId(blogId);
            updateBlog.setViews((long) viewsCount);
            updateBlog.setLikes((long) likesCount);
            updateBlog.setDislikes((long) dislikesCount);
            updateBlog.setCollections((long) collectionsCount);
            updateBlog.setShares((long) sharesCount);
            updateBlog.setUpdateTime(LocalDateTime.now());
            
            blogMapper.update(updateBlog);
            log.info("博客{}统计数据校正完成: 浏览量={}, 点赞={}, 点踩={}, 收藏={}, 分享={}", 
                    blogId, viewsCount, likesCount, dislikesCount, collectionsCount, sharesCount);
        } catch (Exception e) {
            log.error("校正博客{}统计数据失败", blogId, e);
            throw new RuntimeException("校正博客统计数据失败", e);
        }
    }
    
    /**
     * 校正所有博客的统计数据
     * 该方法适合定时任务调用，确保所有博客的统计数据准确
     */
    @Override
    @Transactional
    public void correctAllBlogsStats() {
        try {
            log.info("开始校正所有博客的统计数据");
            
            // 获取所有博客ID
            // 这里可以根据实际情况优化，例如分批处理
            List<Long> blogIds = getAllBlogIds();
            
            int successCount = 0;
            int failCount = 0;
            
            // 逐个校正
            for (Long blogId : blogIds) {
                try {
                    correctBlogStats(blogId);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    log.error("校正博客{}统计数据失败", blogId, e);
                }
            }
            
            log.info("所有博客统计数据校正完成，成功: {}，失败: {}", successCount, failCount);
        } catch (Exception e) {
            log.error("校正所有博客统计数据失败", e);
            throw new RuntimeException("校正所有博客统计数据失败", e);
        }
    }
    
    /**
     * 获取所有博客ID
     * 
     * @return 博客ID列表
     */
    private List<Long> getAllBlogIds() {
        // 这里应该调用BlogMapper中的方法获取所有博客ID
        // 暂时使用简单的实现，实际项目中可能需要分页处理
        return blogMapper.getAllBlogIds();
    }
} 
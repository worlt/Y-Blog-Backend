package com.blog.controller.admin;

import com.blog.mapper.ViewMapper;
import com.blog.mapper.BlogMapper;
import com.blog.mapper.CommentMapper;
import com.blog.pojo.entity.Blog;
import com.blog.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 博客统计信息控制器
 * 提供博客互动数据统计功能
 *
 * @Author worlt
 * @Date 2025/6/10 上午10:00
 */
@RestController
@RequestMapping("/admin/blog/statistics")
@Slf4j
@Api(tags = "博客统计信息接口")
@RequiredArgsConstructor
public class BlogStatisticsController {

    @Autowired
    private ViewMapper viewMapper;

    @Autowired
    private BlogMapper blogMapper;

    @Autowired
    private CommentMapper commentMapper;

    /**
     * 获取博客浏览量信息
     *
     * @param blogId 博客ID
     * @return 博客浏览量信息
     */
    @GetMapping("/view/{blogId}")
    @ApiOperation("获取博客浏览量信息")
    public Result<Map<String, Object>> getBlogViewStatistics(@PathVariable Long blogId) {
        log.info("获取博客{}的浏览量统计信息", blogId);
        
        int totalViews = viewMapper.countByBlogId(blogId);
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("blogId", blogId);
        statistics.put("totalViews", totalViews);
        
        return Result.success(statistics);
    }
    
    /**
     * 获取博客所有互动统计信息
     *
     * @param blogId 博客ID
     * @return 博客所有统计信息
     */
    @GetMapping("/all/{blogId}")
    @ApiOperation("获取博客所有互动统计信息")
    public Result<Map<String, Object>> getBlogAllStatistics(@PathVariable Long blogId) {
        log.info("获取博客{}的所有互动统计信息", blogId);
        
        // 从数据库获取博客信息
        Blog blog = blogMapper.getById(blogId);
        if (blog == null) {
            return Result.error("博客不存在");
        }
        
        // 获取评论数量
        int commentCount = commentMapper.countByBlogId(blogId);
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("blogId", blogId);
        statistics.put("title", blog.getTitle());
        statistics.put("views", blog.getViews());
        statistics.put("likes", blog.getLikes());
        statistics.put("dislikes", blog.getDislikes());
        statistics.put("collections", blog.getCollections());
        statistics.put("shares", blog.getShares());
        statistics.put("commentCount", commentCount);
        
        return Result.success(statistics);
    }
} 
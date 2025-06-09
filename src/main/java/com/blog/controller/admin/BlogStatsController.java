package com.blog.controller.admin;

import com.blog.result.Result;
import com.blog.service.BlogStatsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 博客统计校正控制器
 * 提供博客统计数据校正功能
 * 
 * @Author worlt
 * @Date 2025/6/10 下午2:00
 */
@RestController
@RequestMapping("/admin/blog/stats")
@Slf4j
@Api(tags = "博客统计校正接口")
@RequiredArgsConstructor
public class BlogStatsController {

    @Autowired
    private BlogStatsService blogStatsService;

    /**
     * 校正指定博客的统计数据
     *
     * @param blogId 博客ID
     * @return 操作结果
     */
    @PostMapping("/correct/{blogId}")
    @ApiOperation("校正指定博客的统计数据")
    public Result correctBlogStats(@PathVariable Long blogId) {
        log.info("校正博客{}的统计数据", blogId);
        blogStatsService.correctBlogStats(blogId);
        return Result.success();
    }

    /**
     * 校正所有博客的统计数据
     *
     * @return 操作结果
     */
    @PostMapping("/correctAll")
    @ApiOperation("校正所有博客的统计数据")
    public Result correctAllBlogsStats() {
        log.info("校正所有博客的统计数据");
        blogStatsService.correctAllBlogsStats();
        return Result.success();
    }
} 
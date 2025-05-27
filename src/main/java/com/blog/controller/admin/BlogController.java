package com.blog.controller.admin;

import com.blog.pojo.dto.BlogAddDTO;
import com.blog.pojo.dto.BlogPageQueryDTO;
import com.blog.result.PageResult;
import com.blog.result.Result;
import com.blog.service.BlogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author worlt
 * @Date 2025/5/21 下午4:09
 */
@RestController
@RequestMapping("/admin/blog")
@Api(tags = "博客文章相关接口")
@Slf4j
public class BlogController {

    @Autowired
    private BlogService blogService;

    /**
     * 博客文章分页查询
     * @param blogPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("博客文章分页查询")
    public Result<PageResult> page(BlogPageQueryDTO blogPageQueryDTO) {
        log.info("博客文章分页查询：{}", blogPageQueryDTO);
        PageResult pageResult = blogService.pageQuery(blogPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 新增博客文章
     * @param blogAddDTO
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("新增博客文章")
    public Result add(BlogAddDTO blogAddDTO) {
        log.info("新增博客文章：{}", blogAddDTO);
        blogService.add(blogAddDTO);
        return Result.success();
    }


}

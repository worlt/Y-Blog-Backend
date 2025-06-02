package com.blog.controller.admin;

import com.blog.pojo.dto.BlogAddDTO;
import com.blog.pojo.dto.BlogPageQueryDTO;
import com.blog.pojo.dto.BlogUpdateDTO;
import com.blog.pojo.vo.BlogVO;
import com.blog.result.PageResult;
import com.blog.result.Result;
import com.blog.service.BlogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @ApiOperation("新增博客")
    public Result add(BlogAddDTO blogAddDTO) {
        log.info("新增博客文章：{}", blogAddDTO);
        blogService.add(blogAddDTO);
        return Result.success();
    }

    /**
     * 编辑博客
     * @param blogUpdateDTO
     * @return
     */
    @PutMapping("/update")
    @ApiOperation("编辑博客")
    public Result update(@RequestBody BlogUpdateDTO blogUpdateDTO) {
        log.info("更新博客：{}", blogUpdateDTO);
        blogService.update(blogUpdateDTO);
        return Result.success();
    }

    /**
     * 根据博客id查询博客详情
     * @param id
     * @return
     */
    @GetMapping("/getById/{id}")
    @ApiOperation("根据id查询博客详情")
    public Result<BlogVO> getById(@PathVariable("id") Long id) {
        log.info("根据id查询博客详情：{}", id);
        BlogVO blogVO = blogService.getById(id);
        return Result.success(blogVO);
    }

    /**
     * 根据id删除博客
     * @param id
     * @return
     */
    @DeleteMapping("/deleteById/{id}")
    @ApiOperation("根据id删除博客")
    public Result deleteById(@PathVariable("id") Long id) {
        log.info("根据id删除博客：{}", id);
        blogService.deleteById(id);
        return Result.success();
    }

}

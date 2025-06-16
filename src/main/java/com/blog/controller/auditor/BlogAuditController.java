package com.blog.controller.auditor;

import com.blog.pojo.dto.BlogAuditPageQueryDTO;
import com.blog.pojo.dto.BlogAuditorDTO;
import com.blog.pojo.vo.BlogAuditVO;
import com.blog.result.PageResult;
import com.blog.result.Result;
import com.blog.service.BlogAuditService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Author worlt
 * @Date 2025/5/30 下午11:08
 */
@RestController("auditorBlogController")
@RequestMapping("/auditor/blogAudit")
@Slf4j
@Api(tags = "博客审核相关接口")
public class BlogAuditController {

    @Autowired
    private BlogAuditService blogAuditService;

    /**
     * 分页查询博客审核
     * @param blogAuditPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询博客审核")
    public Result<PageResult> page(BlogAuditPageQueryDTO blogAuditPageQueryDTO) {
        log.info("分页查询博客审核：{}", blogAuditPageQueryDTO);
        PageResult pageResult = blogAuditService.pageQuery(blogAuditPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据id查询审核博客详情
     * @param id
     * @return
     */
    @GetMapping("/getById/{id}")
    @ApiOperation("根据id查询审核博客详情")
    public Result<BlogAuditVO> getById(@PathVariable("id") Long id) {
        log.info("根据id查询审核博客详情：{}", id);
        return Result.success(blogAuditService.getById(id));
    }

    /**
     * 审核博客
     * @param id
     * @param status
     * @return
     */
    @PutMapping("/setStatus/{status}")
    @ApiOperation("审核博客")
    public Result setStatus(@RequestParam("id") Long id, @PathVariable("status") Integer status) {
        log.info("审核博客：id：{}， status：{}", id, status);
        blogAuditService.setStatus(id, status);
        return Result.success();
    }

    /**
     * 处理博客审核
     * @param blogAuditorDTO
     * @return
     */
    @PutMapping("/handle")
    public Result handle(@RequestBody BlogAuditorDTO blogAuditorDTO) {
        log.info("处理博客审核：{}", blogAuditorDTO);
        blogAuditService.handle(blogAuditorDTO);
        return Result.success();
    }

}

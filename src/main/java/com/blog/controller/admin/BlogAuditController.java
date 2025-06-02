package com.blog.controller.admin;

import com.blog.pojo.dto.BlogAuditPageQueryDTO;
import com.blog.pojo.vo.BlogAuditVO;
import com.blog.result.PageResult;
import com.blog.result.Result;
import com.blog.service.BlogAuditService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Author worlt
 * @Date 2025/5/30 下午11:08
 */
@RestController
@RequestMapping("/admin/blogAudit")
@Slf4j
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
     * @param map
     * @param status
     * @return
     */
    @PutMapping("/status/{status}")
    public Result setStatus(@RequestBody Map<String, Long> map, @PathVariable("status") Integer status) {
        Long id = map.get("id");
        blogAuditService.setStatus(id, status);
        return Result.success();
    }

}

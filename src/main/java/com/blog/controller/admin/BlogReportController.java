package com.blog.controller.admin;

import com.blog.pojo.dto.BlogReportDTO;
import com.blog.pojo.dto.BlogReportPageQueryDTO;
import com.blog.pojo.vo.BlogReportVO;
import com.blog.result.PageResult;
import com.blog.result.Result;
import com.blog.service.BlogReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Author worlt
 * @Date 2025/6/2 下午8:23
 */
@RestController
@RequestMapping("/admin/blogReport")
@Slf4j
@Api(tags = "博客举报相关接口")
public class BlogReportController {

    @Autowired
    private BlogReportService blogReportService;

    /**
     * 分页查询举报列表
     * @param blogReportPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("举报博客分页查询")
    public Result<PageResult> page(BlogReportPageQueryDTO blogReportPageQueryDTO) {
        log.info("举报博客分页查询：{}", blogReportPageQueryDTO);
        PageResult pageResult = blogReportService.pageQuery(blogReportPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据id查询举报详情
     * @param id
     * @return
     */
    @GetMapping("/getById/{id}")
    @ApiOperation("根据id查询举报详情")
    public Result<BlogReportVO> getById(@PathVariable("id") Long id) {
        log.info("根据id查询举报详情：{}", id);
        BlogReportVO blogReportVO = blogReportService.getById(id);
        return Result.success(blogReportVO);
    }

    /**
     * 处理举报
     * @param blogReportDTO
     * @return
     */
    @PutMapping("/handle")
    @ApiOperation("处理举报")
    public Result handle(@RequestBody BlogReportDTO blogReportDTO) {
        log.info("处理举报：{}", blogReportDTO);
        blogReportService.handle(blogReportDTO);
        return Result.success();
    }

    /**
     * 更新举报状态
     * @param map
     * @param status
     * @return
     */
    @PutMapping("/status/{status}")
    @ApiOperation("更新举报状态")
    public Result setStatus(@RequestBody Map<String, Long> map, @PathVariable("status") Integer status) {
        Long id = map.get("id");
        log.info("更新举报状态：id：{}，status：{}", id, status);
        blogReportService.setStatus(id, status);
        return Result.success();
    }

    /**
     * 根据id删除举报
     * @param id
     * @return
     */
    @DeleteMapping("/deleteById/{id}")
    @ApiOperation("根据id删除举报")
    public Result deleteById(@PathVariable("id") Long id) {
        log.info("根据id删除举报：{}", id);
        blogReportService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除举报
     * @param ids
     * @return
     */
    @DeleteMapping("/deleteByIds")
    @ApiOperation("批量删除举报")
    public Result deleteByIds(@RequestParam("ids") Long[] ids) {
        log.info("批量删除举报：{}", (Object) ids);
        blogReportService.deleteByIds(ids);
        return Result.success();
    }
}

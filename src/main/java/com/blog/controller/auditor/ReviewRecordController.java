package com.blog.controller.auditor;

import com.blog.pojo.dto.ReviewRecordPageQueryDTO;
import com.blog.pojo.vo.ReviewRecordVO;
import com.blog.result.PageResult;
import com.blog.result.Result;
import com.blog.service.ReviewRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 审核记录控制器
 * @Author worlt
 * @Date 2025/6/4 下午9:26
 */
@RestController
@RequestMapping("/auditor/reviewRecord")
@Api(tags = "审核记录相关接口")
@Slf4j
public class ReviewRecordController {
    
    @Autowired
    private ReviewRecordService reviewRecordService;
    
    /**
     * 分页查询审核记录
     * 支持多种查询场景：
     * 1. 通用条件查询：审核员ID、审核类型、审核结果、时间范围等
     * 2. 关联ID查询：用户ID、博客ID、评论ID
     * 3. 单个记录查询：记录ID
     * @param reviewRecordPageQueryDTO 查询条件
     * @return 分页结果
     */
    @GetMapping("/page")
    @ApiOperation("分页查询审核记录")
    public Result<PageResult> page(ReviewRecordPageQueryDTO reviewRecordPageQueryDTO) {
        log.info("分页查询审核记录：{}", reviewRecordPageQueryDTO);
        PageResult pageResult = reviewRecordService.pageQuery(reviewRecordPageQueryDTO);
        return Result.success(pageResult);
    }
    
    /**
     * 根据id查询审核记录详情
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询审核记录详情")
    public Result<ReviewRecordVO> getById(@PathVariable Long id) {
        log.info("根据id查询审核记录详情：{}", id);
        ReviewRecordVO reviewRecordVO = reviewRecordService.getById(id);
        return Result.success(reviewRecordVO);
    }
}

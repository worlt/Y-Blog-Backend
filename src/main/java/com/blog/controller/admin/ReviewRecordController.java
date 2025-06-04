package com.blog.controller.admin;

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
@RequestMapping("/admin/review-record")
@Api(tags = "审核记录相关接口")
@Slf4j
public class ReviewRecordController {
    
    @Autowired
    private ReviewRecordService reviewRecordService;
    
    /**
     * 分页查询审核记录
     * @param reviewRecordPageQueryDTO
     * @return
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
    
    /**
     * 根据类型和关联ID查询审核记录列表
     * @param reviewType 审核类型（0-用户、1-博客、2-评论、3-申诉）
     * @param targetId 关联ID（用户ID、博客ID、评论ID等）
     * @return
     */
    @GetMapping("/list/{reviewType}/{targetId}")
    @ApiOperation("根据类型和关联ID查询审核记录列表")
    public Result<PageResult> listByTypeAndTargetId(
            @PathVariable Integer reviewType,
            @PathVariable Long targetId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("根据类型和关联ID查询审核记录列表：reviewType={}, targetId={}, page={}, pageSize={}", 
                reviewType, targetId, page, pageSize);
        PageResult pageResult = reviewRecordService.pageQueryByTypeAndTargetId(
                reviewType, targetId, page, pageSize);
        return Result.success(pageResult);
    }
    
    /**
     * 根据审核员ID查询审核记录
     * @param reviewerId
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/reviewer/{reviewerId}")
    @ApiOperation("根据审核员ID查询审核记录")
    public Result<PageResult> listByReviewerId(
            @PathVariable Long reviewerId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("根据审核员ID查询审核记录：reviewerId={}, page={}, pageSize={}", 
                reviewerId, page, pageSize);
        PageResult pageResult = reviewRecordService.pageQueryByReviewerId(
                reviewerId, page, pageSize);
        return Result.success(pageResult);
    }
}

package com.blog.controller.auditor;

import com.blog.pojo.dto.CommentReportDTO;
import com.blog.pojo.dto.CommentReportPageQueryDTO;
import com.blog.pojo.vo.CommentReportVO;
import com.blog.pojo.vo.CommentVO;
import com.blog.result.PageResult;
import com.blog.result.Result;
import com.blog.service.CommentReportService;
import com.blog.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Author worlt
 * @Date 2025/6/4 下午5:28
 */
@RestController("auditorCommentController")
@RequestMapping("/auditor/commentReport")
@Slf4j
@Api(tags = "评论举报相关接口")
public class CommentReportController {

    @Autowired
    private CommentReportService commentReportService;
    
    @Autowired
    private CommentService commentService;

    /**
     * 分页查询评论举报列表
     * @param commentReportPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("评论举报分页查询")
    public Result<PageResult> page(CommentReportPageQueryDTO commentReportPageQueryDTO) {
        log.info("评论举报分页查询：{}", commentReportPageQueryDTO);
        PageResult pageResult = commentReportService.pageQuery(commentReportPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据id查询评论举报详情
     * @param id
     * @return
     */
    @GetMapping("/getById/{id}")
    @ApiOperation("根据id查询评论举报详情")
    public Result<CommentReportVO> getById(@PathVariable("id") Long id) {
        log.info("根据id查询评论举报详情：{}", id);
        CommentReportVO commentReportVO = commentReportService.getById(id);
        return Result.success(commentReportVO);
    }
    
    /**
     * 根据id查询评论详情
     * @param id
     * @return
     */
    @GetMapping("/getCommentById/{id}")
    @ApiOperation("根据id查询评论详情")
    public Result<CommentVO> getCommentById(@PathVariable("id") Long id) {
        log.info("根据id查询评论详情：{}", id);
        CommentVO commentVO = commentService.getById(id);
        return Result.success(commentVO);
    }

    /**
     * 处理评论举报
     * @param commentReportDTO
     * @return
     */
    @PutMapping("/handle")
    @ApiOperation("处理评论举报")
    public Result handle(@RequestBody CommentReportDTO commentReportDTO) {
        log.info("处理评论举报：{}", commentReportDTO);
        commentReportService.handle(commentReportDTO);
        return Result.success();
    }

    /**
     * 更新评论举报状态
     * @param id
     * @param status
     * @return
     */
    @PutMapping("/status/{status}")
    @ApiOperation("更新评论举报状态")
    public Result setStatus(@RequestParam("id") Long id, @PathVariable("status") Integer status) {
        log.info("更新评论举报状态：id：{}，status：{}", id, status);
        commentReportService.setStatus(id, status);
        return Result.success();
    }

    /**
     * 根据id删除评论举报
     * @param id
     * @return
     */
//    @DeleteMapping("/deleteById/{id}")
//    @ApiOperation("根据id删除评论举报")
//    public Result deleteById(@PathVariable("id") Long id) {
//        log.info("根据id删除评论举报：{}", id);
//        commentReportService.deleteById(id);
//        return Result.success();
//    }

    /**
     * 批量删除评论举报
     * @param ids
     * @return
     */
//    @DeleteMapping("/deleteByIds")
//    @ApiOperation("批量删除评论举报")
//    public Result deleteByIds(@RequestParam("ids") Long[] ids) {
//        log.info("批量删除评论举报：{}", (Object) ids);
//        commentReportService.deleteByIds(ids);
//        return Result.success();
//    }
}

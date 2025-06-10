package com.blog.controller.user;

import com.blog.pojo.dto.BlogAddReportDTO;
import com.blog.pojo.dto.BlogReportDTO;
import com.blog.pojo.dto.CommentAddReportDTO;
import com.blog.pojo.dto.UserAddReportDTO;
import com.blog.result.Result;
import com.blog.service.BlogReportService;
import com.blog.service.CommentReportService;
import com.blog.service.UserReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author worlt
 * @Date 2025/6/10 下午9:17
 */
@RestController
@RequestMapping("/user/report")
@Slf4j
@Api(tags = "用户举报相关")
public class ReportController {

    @Autowired
    private BlogReportService blogReportService;
    @Autowired
    private CommentReportService commentReportService;
    @Autowired
    private UserReportService userReportService;

    /**
     * 举报博客
     * @param blogAddReportDTO
     * @return
     */
    @PostMapping("/blog")
    @ApiOperation("举报博客")
    public Result blog(@RequestBody BlogAddReportDTO blogAddReportDTO) {
        log.info("举报博客：{}", blogAddReportDTO);
        blogReportService.add(blogAddReportDTO);
        return Result.success();
    }

    /**
     * 举报评论
     * @param commentAddReportDTO
     * @return
     */
    @PostMapping("/comment")
    @ApiOperation("举报评论")
    public Result comment(@RequestBody CommentAddReportDTO commentAddReportDTO) {
        log.info("举报评论：{}", commentAddReportDTO);
        commentReportService.add(commentAddReportDTO);
        return Result.success();
    }

    /**
     * 举报用户
     * @param userAddReportDTO
     * @return
     */
    @PostMapping("/user")
    @ApiOperation("举报用户")
    public Result user(@RequestBody UserAddReportDTO userAddReportDTO) {
        log.info("举报用户：{}", userAddReportDTO);
        userReportService.add(userAddReportDTO);
        return Result.success();
    }

}

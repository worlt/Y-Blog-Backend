package com.blog.controller.admin;

import com.blog.pojo.dto.CommentAddDTO;
import com.blog.pojo.dto.CommentPageQueryDTO;
import com.blog.pojo.vo.CommentVO;
import com.blog.result.PageResult;
import com.blog.result.Result;
import com.blog.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author worlt
 * @Date 2025/5/29 下午10:27
 */
@RestController("adminCommentController")
@RequestMapping("/admin/comment")
@Slf4j
@Api(tags = "评论相关接口")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 分页查询评论
     * @param commentPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询评论")
    public Result<PageResult> page(CommentPageQueryDTO commentPageQueryDTO) {
        log.info("分页查询评论：{}", commentPageQueryDTO);
        PageResult pageResult = commentService.page(commentPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 新增评论
     * @param commentAddDTO
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("新增评论")
    public Result add(CommentAddDTO commentAddDTO) {
        log.info("新增评论：{}", commentAddDTO);
        commentService.add(commentAddDTO);
        return Result.success();
    }

    /**
     * 根据id删除评论
     * @return
     */
    @DeleteMapping("/deleteById/{id}")
    @ApiOperation("根据id删除评论")
    public Result deleteById(@PathVariable("id") Long id) {
        log.info("根据id删除评论：{}", id);
        commentService.deleteById(id);
        return Result.success();
    }

    /**
     * 根据评论id查询评论详情
     * @param id
     * @return
     */
    @GetMapping("/getById/{id}")
    @ApiOperation("根据id查询评论详情")
    public Result<CommentVO> getById(@PathVariable("id") Long id) {
        log.info("根据id查询评论详情：{}", id);
        CommentVO commentVO = commentService.getById(id);
        return Result.success(commentVO);
    }


}

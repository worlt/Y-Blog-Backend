package com.blog.controller.user;

import com.blog.constant.MessageConstant;
import com.blog.context.BaseContext;
import com.blog.pojo.dto.CommentAddDTO;
import com.blog.pojo.entity.Comment;
import com.blog.result.Result;
import com.blog.service.CommentService;
import com.blog.service.DislikeService;
import com.blog.service.LikeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author worlt
 * @Date 2025/6/8 下午9:45
 */
@RestController("userCommentController")
@RequestMapping("/user/comment")
@Slf4j
@Api(tags = "评论相关接口")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private DislikeService dislikeService;

    /**
     * 获取当前博客所有评论
     * @param id
     * @return
     */
    @GetMapping("/getByBlogId/{id}")
    @ApiOperation("获取当前博客评论")
    public Result<List<Comment>> getByBlogId(@PathVariable("id") Long id) {
        log.info("获取当前博客评论：{}", id);
        return Result.success(commentService.getByBlogId(id));
    }

    /**
     * 添加评论
     * @param commentAddDTO
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("添加评论")
    public Result add(CommentAddDTO commentAddDTO) {
        if (!commentAddDTO.getUserId().equals(BaseContext.getCurrentId())) throw new RuntimeException(MessageConstant.ADD_FAIL);
        log.info("添加评论：{}", commentAddDTO);
        commentService.add(commentAddDTO);
        return Result.success();
    }

    /**
     * 根据id删除评论
     * @param id
     * @return
     */
    @DeleteMapping("deleteById/{id}")
    @ApiOperation("根据id删除评论")
    public Result deleteById(@PathVariable("id") Long id) {
        Long userId = commentService.getUserId(id);
        if (!userId.equals(BaseContext.getCurrentId())) throw new RuntimeException(MessageConstant.DELETE_FAIL);
        commentService.deleteById(id);
        return Result.success();
    }

    /**
     * 点赞评论
     * @param id 评论id
     * @return 结果
     */
    @PostMapping("/like/{id}")
    @ApiOperation("点赞评论")
    public Result likeComment(@PathVariable("id") Long id) {
        log.info("点赞评论：{}", id);
        Long userId = BaseContext.getCurrentId();
        likeService.likeComment(id, userId);
        return Result.success();
    }

    /**
     * 点踩评论
     * @param id 评论id
     * @return 结果
     */
    @PostMapping("/dislike/{id}")
    @ApiOperation("点踩评论")
    public Result dislikeComment(@PathVariable("id") Long id) {
        log.info("点踩评论：{}", id);
        Long userId = BaseContext.getCurrentId();
        dislikeService.dislikeComment(id, userId);
        return Result.success();
    }

    /**
     * 获取当前用户的所有评论
     * @return
     */
    @GetMapping("/getMyComments")
    public Result<List<Comment>> getMyComments() {
        Long userId = BaseContext.getCurrentId();
        log.info("获取我的评论：{}", userId);
        return Result.success(commentService.getByUserId(userId));
    }

}

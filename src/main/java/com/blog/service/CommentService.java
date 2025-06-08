package com.blog.service;

import com.blog.pojo.dto.CommentAddDTO;
import com.blog.pojo.dto.CommentPageQueryDTO;
import com.blog.pojo.entity.Comment;
import com.blog.pojo.vo.CommentVO;
import com.blog.result.PageResult;

import java.util.List;

/**
 * @Author worlt
 * @Date 2025/5/29 下午10:28
 */
public interface CommentService {

    /**
     * 分页查询评论
     *
     * @param commentPageQueryDTO
     * @return
     */
    PageResult page(CommentPageQueryDTO commentPageQueryDTO);

    /**
     * 新增评论
     * @param commentAddDTO
     */
    void add(CommentAddDTO commentAddDTO);

    /**
     * 根据id删除评论
     * @param id
     */
    void deleteById(Long id);

    /**
     * 根据id查询评论详情
     * @param id
     * @return
     */
    CommentVO getById(Long id);

    /**
     * 获取当前博客所有评论
     * @param id
     * @return
     */
    List<Comment> getByBlogId(Long id);

    /**
     * 获取评论用户的id
     * @param id
     * @return
     */
    Long getUserId(Long id);
}

package com.blog.service.impl;

import com.blog.constant.statusConstant.CommentStatusConstant;
import com.blog.mapper.CommentMapper;
import com.blog.pojo.dto.CommentAddDTO;
import com.blog.pojo.dto.CommentPageQueryDTO;
import com.blog.pojo.entity.Comment;
import com.blog.pojo.vo.CommentVO;
import com.blog.result.PageResult;
import com.blog.service.CommentService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * @Author worlt
 * @Date 2025/5/29 下午10:28
 */
@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    /**
     * 分页查询评论
     *
     * @param commentPageQueryDTO
     * @return
     */
    @Override
    public PageResult page(CommentPageQueryDTO commentPageQueryDTO) {
        PageHelper.startPage(commentPageQueryDTO.getPage(), commentPageQueryDTO.getPageSize());
        Page<CommentVO> page = commentMapper.page(commentPageQueryDTO);

        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 新增评论
     * @param commentAddDTO
     */
    @Override
    public void add(CommentAddDTO commentAddDTO) {
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentAddDTO, comment);

        comment.setCreateTime(LocalDateTime.now());
        comment.setLikes(0L);
        comment.setDislikes(0L);
        comment.setStatus(CommentStatusConstant.NORMAL);

        commentMapper.add(comment);
    }

    /**
     * 根据id删除评论
     * @param id
     */
    @Override
    @Transactional
    public void deleteById(Long id) {
        // 使用递归CTE删除评论及其所有的子评论
        commentMapper.deleteCommentWithChildren(id);
        // 删除该评论
//        commentMapper.deleteById(id);
    }

    /**
     * 根据id查询评论详情
     * @param id
     * @return
     */
    @Override
    public CommentVO getById(Long id) {
        return commentMapper.getVOById(id);
    }

    /**
     * 获取当前博客所有评论
     * @param id
     * @return
     */
    @Override
    public List<Comment> getByBlogId(Long id) {
        List<Comment> commentList = commentMapper.getByBlogId(id);
        return commentList != null ? commentList : Collections.emptyList();
    }

    /**
     * 获取评论用户的id
     * @param id
     * @return
     */
    @Override
    public Long getUserId(Long id) {
        return commentMapper.getUserId(id);
    }

    /**
     * 获取用户的有评论
     * @param userId
     * @return
     */
    @Override
    public List<Comment> getByUserId(Long userId) {
        List<Comment> commentList = commentMapper.getByUserId(userId);
        return  commentList != null ? commentList : Collections.emptyList();
    }

    /**
     * 根据id获取vo对象
     * @param id
     * @return
     */
    @Override
    public List<CommentVO> getVOByBlogId(Long id) {
        List<CommentVO> commentList = commentMapper.getVOByBlogId(id);
        return commentList != null ? commentList : Collections.emptyList();
    }
}

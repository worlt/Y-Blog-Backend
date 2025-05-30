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

import java.time.LocalDateTime;

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
    public void deleteById(Long id) {
        // TODO：连带删除子评论（问题：（递归删除子评论）子评论又有子评论呢）
//        commentMapper.deleteChildCommentByParentId(id);
        // 删除该评论
        commentMapper.deleteById(id);
    }

    /**
     * 根据id查询评论详情
     * @param id
     * @return
     */
    @Override
    public CommentVO getById(Long id) {
        return commentMapper.getById(id);
    }
}

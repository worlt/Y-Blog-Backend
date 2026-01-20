package com.blog.service.impl;

import com.blog.context.BaseContext;
import com.blog.mapper.BlogMapper;
import com.blog.constant.statusConstant.CommentStatusConstant;
import com.blog.mapper.CommentMapper;
import com.blog.pojo.dto.CommentAddDTO;
import com.blog.pojo.dto.CommentPageQueryDTO;
import com.blog.pojo.entity.Comment;
import com.blog.pojo.entity.Blog;
import com.blog.pojo.vo.CommentVO;
import com.blog.result.PageResult;
import com.blog.service.CommentService;
import com.blog.service.NotificationService;
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

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private BlogMapper blogMapper;

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
     * 
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

        // 发送评论通知
        // 获取博客信息
        Blog blog = blogMapper.getById(comment.getBlogId());
        if (blog != null) {
            Long senderId = BaseContext.getCurrentId();
            if (senderId != null) {
                Long blogOwnerId = blog.getUserId();
                boolean shouldNotifyBlogOwner = true;

                // 检查是否是回复评论
                if (comment.getParentId() != null) {
                    // 获取父评论及其作者
                    Long parentUserId = commentMapper.getUserId(comment.getParentId());

                    if (parentUserId != null) {
                        // 如果回复的不是自己的评论，且父评论作者不是当前发送者
                        if (!parentUserId.equals(senderId)) {
                            notificationService.sendReplyNotification(senderId, parentUserId, blog.getId(),
                                    blog.getTitle(), comment.getContent());
                        }

                        // 如果父评论作者就是博客作者，则不需要再单独发送“评论通知”，已经发送了“回复通知”
                        // 避免博客主收到两条消息
                        if (parentUserId.equals(blogOwnerId)) {
                            shouldNotifyBlogOwner = false;
                        }
                    }
                }

                // 发送博客评论通知（如果需要）
                // 只有当接收者不是发送者自己，且没有被去重逻辑排除时才发送
                if (shouldNotifyBlogOwner && !blogOwnerId.equals(senderId)) {
                    notificationService.sendCommentNotification(senderId, blogOwnerId, blog.getId(), blog.getTitle(),
                            comment.getContent());
                }
            }
        }
    }

    /**
     * 根据id删除评论
     * 
     * @param id
     */
    @Override
    @Transactional
    public void deleteById(Long id) {
        // 使用递归CTE删除评论及其所有的子评论
        commentMapper.deleteCommentWithChildren(id);
        // 删除该评论
        // commentMapper.deleteById(id);
    }

    /**
     * 根据id查询评论详情
     * 
     * @param id
     * @return
     */
    @Override
    public CommentVO getById(Long id) {
        return commentMapper.getVOById(id);
    }

    /**
     * 获取当前博客所有评论
     * 
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
     * 
     * @param id
     * @return
     */
    @Override
    public Long getUserId(Long id) {
        return commentMapper.getUserId(id);
    }

    /**
     * 获取用户的有评论
     * 
     * @param userId
     * @return
     */
    @Override
    public List<Comment> getByUserId(Long userId) {
        List<Comment> commentList = commentMapper.getByUserId(userId);
        return commentList != null ? commentList : Collections.emptyList();
    }

    /**
     * 根据id获取vo对象
     * 
     * @param id
     * @return
     */
    @Override
    public List<CommentVO> getVOByBlogId(Long id) {
        List<CommentVO> commentList = commentMapper.getVOByBlogId(id);
        return commentList != null ? commentList : Collections.emptyList();
    }
}

package com.blog.service.impl;

import com.blog.constant.MessageTypeConstant;
import com.blog.constant.ReviewType;
import com.blog.constant.statusConstant.MessageStatusConstant;
import com.blog.constant.statusConstant.ReportStatusConstant;
import com.blog.constant.resultConstant.CommentReportResultConstant;
import com.blog.mapper.*;
import com.blog.pojo.dto.CommentReportDTO;
import com.blog.pojo.dto.CommentReportPageQueryDTO;
import com.blog.pojo.entity.*;
import com.blog.pojo.vo.CommentReportVO;
import com.blog.result.PageResult;
import com.blog.service.CommentReportService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 评论举报服务实现
 * @Author worlt
 * @Date 2025/6/4 下午6:10
 */
@Service
@Slf4j
public class CommentReportServiceImpl implements CommentReportService {

    @Autowired
    private CommentReportMapper commentReportMapper;
    
    @Autowired
    private CommentMapper commentMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private ConversationMapper conversationMapper;
    
    @Autowired
    private MessageMapper messageMapper;
    
    @Autowired
    private ReviewRecordMapper reviewRecordMapper;
    
    /**
     * 分页查询
     * @param commentReportPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(CommentReportPageQueryDTO commentReportPageQueryDTO) {
        PageHelper.startPage(commentReportPageQueryDTO.getPage(), commentReportPageQueryDTO.getPageSize());
        Page<CommentReportVO> page = commentReportMapper.pageQuery(commentReportPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 根据id查询举报详情
     * @param id
     * @return
     */
    @Override
    public CommentReportVO getById(Long id) {
        return commentReportMapper.getById(id);
    }

    /**
     * 处理举报
     * @param commentReportDTO
     */
    @Override
    @Transactional
    public void handle(CommentReportDTO commentReportDTO) {
        // 获取举报详情
        CommentReport report = commentReportMapper.selectById(commentReportDTO.getId());
        if (report == null) {
            throw new RuntimeException("举报记录不存在");
        }
        
        // 获取被举报评论
        Comment comment = commentMapper.getById(report.getCommentId());
        if (comment == null) {
            throw new RuntimeException("被举报评论不存在");
        }
        
        // 更新举报状态
        CommentReport commentReport = CommentReport.builder()
                .id(commentReportDTO.getId())
                .reviewerId(commentReportDTO.getReviewerId())
                .reviewTime(LocalDateTime.now())
                .reviewResult(commentReportDTO.getReviewResult())
                .reviewReason(commentReportDTO.getReviewReason())
                .status(ReportStatusConstant.RESOLVED) // 已处理
                .build();
        commentReportMapper.update(commentReport);
        
        // 构建消息内容
        String messageContent = null;
        if (commentReportDTO.getReviewResult().equals(CommentReportResultConstant.DELETE)) {
            // 删除评论
            messageContent = "您的评论因被举报且审核结果为违规，已被删除。原因：" + commentReportDTO.getReviewReason();
            
            // 更新评论状态为删除
            commentMapper.deleteById(report.getCommentId());
        }
        
        // 创建审核记录
        ReviewRecord reviewRecord = ReviewRecord.builder()
                .reviewerId(commentReportDTO.getReviewerId())
                .userId(comment.getUserId())
                .commentId(comment.getId())
                .reviewTime(LocalDateTime.now())
                .reviewResult(commentReportDTO.getReviewResult())
                .reviewReason(commentReportDTO.getReviewReason())
                .messageToAuthor(messageContent)
                .reviewType(ReviewType.COMMENT)
                .build();
        reviewRecordMapper.add(reviewRecord);
        
        // 如果需要删除评论，则通知评论作者
        if (commentReportDTO.getReviewResult().equals(CommentReportResultConstant.DELETE)) {
            // 向评论作者发送消息通知
            Long receiverId = comment.getUserId();
            
            // 查找是否存在会话，不存在则创建新会话
            Long conversationId = messageMapper.getConversationByReceiverIdAndMessageType(receiverId, MessageTypeConstant.SYSTEM);
            if (conversationId == null) {
                // 创建新会话
                Conversation conversation = Conversation.builder()
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .build();
                conversationId = conversationMapper.add(conversation);
            }
            
            // 创建消息
            Message message = Message.builder()
                    .conversationId(conversationId)
                    .senderId(commentReportDTO.getReviewerId())
                    .receiverId(receiverId)
                    .content(messageContent)
                    .sendTime(LocalDateTime.now())
                    .isRead(MessageStatusConstant.MESSAGE_UNREAD)
                    .messageType(MessageTypeConstant.SYSTEM)
                    .build();
            messageMapper.add(message);
        }
    }

    /**
     * 更新举报状态
     * @param id
     * @param status
     */
    @Override
    public void setStatus(Long id, Integer status) {
        CommentReport commentReport = CommentReport.builder()
                .id(id)
                .status(status)
                .build();
        commentReportMapper.update(commentReport);
    }

    /**
     * 根据id删除举报
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        commentReportMapper.deleteById(id);
    }

    /**
     * 批量删除举报
     * @param ids
     */
    @Override
    public void deleteByIds(Long[] ids) {
        commentReportMapper.deleteByIds(ids);
    }
} 
package com.blog.service.impl;

import com.blog.constant.ConversationTypeConstant;
import com.blog.constant.MessageConstant;
import com.blog.constant.MessageTypeConstant;
import com.blog.constant.ReviewType;
import com.blog.constant.statusConstant.MessageStatusConstant;
import com.blog.constant.statusConstant.ReportStatusConstant;
import com.blog.constant.resultConstant.CommentReportResultConstant;
import com.blog.context.BaseContext;
import com.blog.mapper.*;
import com.blog.pojo.dto.CommentAddReportDTO;
import com.blog.pojo.dto.CommentReportDTO;
import com.blog.pojo.dto.CommentReportPageQueryDTO;
import com.blog.pojo.entity.*;
import com.blog.pojo.vo.CommentReportVO;
import com.blog.result.PageResult;
import com.blog.service.CommentReportService;
import com.blog.service.NotificationService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 评论举报服务实现
 * 处理评论举报相关的业务逻辑
 * 
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

    @Autowired
    private NotificationService notificationService;

    /**
     * 分页查询评论举报记录
     * 支持根据用户ID、评论ID和状态进行筛选
     * 
     * @param commentReportPageQueryDTO 分页查询条件DTO，包含页码、每页大小和筛选条件
     * @return 分页结果，包含总记录数和当前页数据
     */
    @Override
    public PageResult pageQuery(CommentReportPageQueryDTO commentReportPageQueryDTO) {
        PageHelper.startPage(commentReportPageQueryDTO.getPage(), commentReportPageQueryDTO.getPageSize());
        Page<CommentReportVO> page = commentReportMapper.pageQuery(commentReportPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 根据ID查询举报详情
     * 包含举报人信息、被举报评论内容和评论作者信息
     * 
     * @param id 举报ID
     * @return 举报详情VO对象
     */
    @Override
    public CommentReportVO getById(Long id) {
        return commentReportMapper.getById(id);
    }

    /**
     * 处理评论举报
     * 包括更新举报状态、处理评论（如删除）、创建审核记录、发送通知消息
     * 
     * @param commentReportDTO 处理结果DTO，包含审核结果、处理原因等
     * @throws RuntimeException 当举报记录或被举报评论不存在时抛出
     */
    @Override
    @Transactional
    public void handle(CommentReportDTO commentReportDTO) {
        // 获取举报详情
        CommentReport report = commentReportMapper.selectById(commentReportDTO.getId());
        if (report == null) {
            throw new RuntimeException("举报记录不存在");
        }

        // 权限检查：如果状态是处理中且有reviewerId，必须是同一个审核员
        if (report.getStatus() != null && report.getStatus().equals(ReportStatusConstant.IN_PROCESS)) {
            if (report.getReviewerId() != null && !report.getReviewerId().equals(commentReportDTO.getReviewerId())) {
                throw new RuntimeException("该举报正在被其他审核员处理，您无权操作");
            }
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

        // 批量更新同一评论的所有其他待处理/处理中举报
        commentReportMapper.batchUpdateByCommentId(
                report.getCommentId(),
                commentReportDTO.getReviewerId(),
                LocalDateTime.now(),
                commentReportDTO.getReviewResult(),
                commentReportDTO.getReviewReason(),
                ReportStatusConstant.RESOLVED);

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
            Long systemId = 0L;
            Long conversationId;
            Conversation conversation = conversationMapper.findByUserPair(systemId, receiverId);

            if (conversation == null) {
                // 创建新会话
                conversation = Conversation.builder()
                        .user1Id(systemId)
                        .user2Id(receiverId)
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .lastMessage(messageContent)
                        .unreadCount1(0)
                        .unreadCount2(1)
                        .type(ConversationTypeConstant.SYSTEM)
                        .build();
                conversationMapper.insert(conversation);
                conversationId = conversation.getId();
            } else {
                conversationId = conversation.getId();
                // 更新会话最后消息和未读计数
                conversation.setLastMessage(messageContent);
                conversation.setUpdateTime(LocalDateTime.now());
                conversationMapper.updateLastMessage(conversation);
                conversationMapper.incrementUnreadCount2(conversationId);
            }

            // 创建消息
            Message message = Message.builder()
                    .conversationId(conversationId)
                    .senderId(0L) // 发送者设置为0L(系统)
                    .receiverId(receiverId)
                    .content(messageContent)
                    .sendTime(LocalDateTime.now())
                    .isRead(MessageStatusConstant.MESSAGE_UNREAD)
                    .messageType(MessageTypeConstant.SYSTEM)
                    .build();
            messageMapper.add(message);
        }

        // 构建给举报人的反馈通知
        String reporterMessage;
        if (commentReportDTO.getReviewResult().equals(CommentReportResultConstant.DELETE)) {
            reporterMessage = "您好，您举报的评论已被审核处理，由于违规，该评论已被删除/处理。感谢您的贡献！";
        } else {
            reporterMessage = "您好，您举报的评论已被审核处理，暂未发现严重违规，我们会持续监督。感谢您的反馈！";
        }

        // 发送系统通知给举报人
        notificationService.sendSystemMessage(report.getReporterId(), 0L, reporterMessage);
    }

    /**
     * 更新举报状态
     * 用于简单地变更举报记录的状态，不涉及评论处理
     * 
     * @param id     举报ID
     * @param status 新状态值
     */
    @Override
    public void setStatus(Long id, Integer status) {
        // 查询举报记录
        CommentReport report = commentReportMapper.selectById(id);
        if (report == null) {
            throw new RuntimeException("举报记录不存在");
        }

        // 权限检查：如果当前状态是处理中，且有reviewerId，必须是同一个审核员才能修改状态
        if (report.getStatus() != null && report.getStatus().equals(ReportStatusConstant.IN_PROCESS)) {
            Long currentUserId = com.blog.context.BaseContext.getCurrentId();
            if (report.getReviewerId() != null && !report.getReviewerId().equals(currentUserId)) {
                log.error("权限不足，举报ID：{}，当前审核员：{}，处理中审核员：{}",
                        id, currentUserId, report.getReviewerId());
                throw new RuntimeException("该举报正在被其他审核员处理，您无权操作");
            }
        }

        CommentReport commentReport = CommentReport.builder()
                .id(id)
                .status(status)
                .build();
        commentReportMapper.update(commentReport);
    }

    /**
     * 删除单个举报记录
     * 
     * @param id 举报ID
     */
    @Override
    public void deleteById(Long id) {
        commentReportMapper.deleteById(id);
    }

    /**
     * 批量删除举报记录
     * 
     * @param ids 举报ID数组
     */
    @Override
    public void deleteByIds(Long[] ids) {
        commentReportMapper.deleteByIds(ids);
    }

    /**
     * 用户举报评论
     * 
     * @param commentAddReportDTO
     */
    @Override
    public void add(CommentAddReportDTO commentAddReportDTO) {
        Long userId = BaseContext.getCurrentId();
        if (userId == null)
            throw new RuntimeException(MessageConstant.USER_NOT_LOGIN);

        // 获取评论信息
        Comment comment = commentMapper.getById(commentAddReportDTO.getCommentId());
        if (comment == null) {
            throw new RuntimeException("该评论不存在");
        }

        CommentReport commentReport = CommentReport.builder()
                .reporterId(userId)
                .commentId(commentAddReportDTO.getCommentId())
                .reportedUserId(comment.getUserId()) // 设置被举报人ID
                .reportReason(commentAddReportDTO.getReportReason())
                .reportTime(LocalDateTime.now())
                .status(ReportStatusConstant.PENDING)
                .build();
        commentReportMapper.add(commentReport);
    }

    /**
     * 标记为处理中（联动更新同一评论的所有举报）
     * 
     * @param id         举报ID
     * @param reviewerId 审核员ID
     */
    @Override
    @Transactional
    public void markAsInProcess(Long id, Long reviewerId) {
        log.info("标记评论举报为处理中：id={}, reviewerId={}", id, reviewerId);

        // 查询举报记录
        CommentReport report = commentReportMapper.selectById(id);
        if (report == null) {
            throw new RuntimeException("举报记录不存在");
        }

        // 检查状态
        if (report.getStatus().equals(ReportStatusConstant.IN_PROCESS)) {
            if (!report.getReviewerId().equals(reviewerId)) {
                throw new RuntimeException("该举报正在被其他审核员处理，无法操作");
            }
            return; // 已经是自己处理中，直接返回
        }

        if (report.getStatus().equals(ReportStatusConstant.RESOLVED)) {
            throw new RuntimeException("该举报已处理完毕，无法标记");
        }

        // 批量更新同一评论的所有举报为处理中状态
        commentReportMapper.updateStatusAndReviewerByCommentId(
                report.getCommentId(),
                ReportStatusConstant.IN_PROCESS,
                reviewerId);

        log.info("已将评论ID={}的所有举报标记为处理中，处理人ID={}", report.getCommentId(), reviewerId);
    }
}
package com.blog.service.impl;

import com.blog.constant.ConversationTypeConstant;
import com.blog.constant.MessageConstant;
import com.blog.constant.MessageTypeConstant;
import com.blog.constant.ReviewType;
import com.blog.constant.resultConstant.BlogReportResultConstant;
import com.blog.constant.statusConstant.BlogStatusConstant;
import com.blog.constant.statusConstant.MessageStatusConstant;
import com.blog.constant.statusConstant.ReportStatusConstant;
import com.blog.context.BaseContext;
import com.blog.mapper.*;
import com.blog.pojo.dto.BlogAddReportDTO;
import com.blog.pojo.dto.BlogReportDTO;
import com.blog.pojo.dto.BlogReportPageQueryDTO;
import com.blog.pojo.entity.*;
import com.blog.pojo.vo.BlogReportVO;
import com.blog.result.PageResult;
import com.blog.service.BlogReportService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 博客举报服务实现
 * 
 * @Author worlt
 * @Date 2025/6/3 下午5:50
 */
@Service
@Slf4j
public class BlogReportServiceImpl implements BlogReportService {

    @Autowired
    private BlogReportMapper blogReportMapper;

    @Autowired
    private BlogMapper blogMapper;

    @Autowired
    private ConversationMapper conversationMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private ReviewRecordMapper reviewRecordMapper;

    /**
     * 分页查询
     * 
     * @param blogReportPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(BlogReportPageQueryDTO blogReportPageQueryDTO) {
        PageHelper.startPage(blogReportPageQueryDTO.getPage(), blogReportPageQueryDTO.getPageSize());
        Page<BlogReportVO> page = blogReportMapper.pageQuery(blogReportPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 根据id查询举报详情
     * 
     * @param id
     * @return
     */
    @Override
    public BlogReportVO getById(Long id) {
        return blogReportMapper.getById(id);
    }

    /**
     * 处理举报
     * 
     * @param blogReportDTO
     */
    @Override
    @Transactional
    public void handle(BlogReportDTO blogReportDTO) {
        // 获取举报详情
        BlogReport report = blogReportMapper.selectById(blogReportDTO.getId());
        if (report == null) {
            throw new RuntimeException("举报记录不存在");
        }

        // 权限检查：如果状态是处理中且有reviewerId，必须是同一个审核员
        if (report.getStatus() != null && report.getStatus().equals(ReportStatusConstant.IN_PROCESS)) {
            if (report.getReviewerId() != null && !report.getReviewerId().equals(blogReportDTO.getReviewerId())) {
                throw new RuntimeException("该举报正在被其他审核员处理，您无权操作");
            }
        }

        // 获取博客信息
        Blog blog = blogMapper.getById(report.getBlogId());
        if (blog == null) {
            throw new RuntimeException("被举报博客不存在");
        }

        // 更新举报状态
        BlogReport blogReport = BlogReport.builder()
                .id(blogReportDTO.getId())
                .reviewerId(blogReportDTO.getReviewerId())
                .reviewTime(LocalDateTime.now())
                .reviewResult(blogReportDTO.getReviewResult())
                .reviewReason(blogReportDTO.getReviewReason()) // 修复：使用reviewReason而不是reportReason
                .status(ReportStatusConstant.RESOLVED) // 已处理
                .build();
        blogReportMapper.update(blogReport);

        // 批量更新同一博客的所有其他待处理/处理中举报
        // 将它们也设置为相同的处理结果和审核原因
        blogReportMapper.batchUpdateByBlogId(
                report.getBlogId(),
                blogReportDTO.getReviewerId(),
                LocalDateTime.now(),
                blogReportDTO.getReviewResult(),
                blogReportDTO.getReviewReason(),
                ReportStatusConstant.RESOLVED);

        // 创建审核记录
        String messageContent = null;
        if (blogReportDTO.getReviewResult().equals(BlogReportResultConstant.NEEDS_REVIEW)) {
            messageContent = "您的博客《" + blog.getTitle() + "》被举报且需要整改。原因：" + blogReportDTO.getReviewReason();
        } else if (blogReportDTO.getReviewResult().equals(BlogReportResultConstant.PERMANENTLY_BANNED)) {
            messageContent = "您的博客《" + blog.getTitle() + "》被举报且已被永久封禁。原因：" + blogReportDTO.getReviewReason();
        }

        ReviewRecord reviewRecord = ReviewRecord.builder()
                .reviewerId(blogReportDTO.getReviewerId())
                .blogId(report.getBlogId())
                // 用户id是举报用户时填写的
                .userId(blog.getUserId())
                // 评论id
                // .commentId()
                .reviewTime(LocalDateTime.now())
                .reviewResult(blogReportDTO.getReviewResult())
                .reviewReason(blogReportDTO.getReviewReason())
                .messageToAuthor(messageContent)
                .reviewType(ReviewType.BLOG)
                .build();
        reviewRecordMapper.add(reviewRecord);

        // 如果是整改或封禁，则更新博客状态
        if (blogReportDTO.getReviewResult().equals(BlogReportResultConstant.NEEDS_REVIEW) ||
                blogReportDTO.getReviewResult().equals(BlogReportResultConstant.PERMANENTLY_BANNED)) {

            // 更新博客状态：整改或永久封禁
            Integer blogStatus = blogReportDTO.getReviewResult().equals(BlogReportResultConstant.NEEDS_REVIEW)
                    ? BlogStatusConstant.IN_REVIEW
                    : BlogStatusConstant.PERMANENTLY_BANNED;
            blogMapper.updateStatus(report.getBlogId(), blogStatus);

            // 向作者发送系统消息
            Long receiverId = blog.getUserId();

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
    }

    /**
     * 更新举报状态
     * 
     * @param id
     * @param status
     */
    @Override
    public void setStatus(Long id, Integer status) {
        BlogReport blogReport = BlogReport.builder()
                .id(id)
                .status(status)
                .build();
        blogReportMapper.update(blogReport);
    }

    /**
     * 根据id删除举报
     * 
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        blogReportMapper.deleteById(id);
    }

    /**
     * 批量删除举报
     * 
     * @param ids
     */
    @Override
    public void deleteByIds(Long[] ids) {
        blogReportMapper.deleteByIds(ids);
    }

    /**
     * 新增博客举报
     * 
     * @param blogAddReportDTO
     */
    @Override
    public void add(BlogAddReportDTO blogAddReportDTO) {
        Long userId = BaseContext.getCurrentId();
        if (userId == null)
            throw new RuntimeException(MessageConstant.USER_NOT_LOGIN);

        // 获取博客信息以获取作者ID
        Blog blog = blogMapper.getById(blogAddReportDTO.getBlogId());
        if (blog == null) {
            throw new RuntimeException("该博客不存在");
        }

        BlogReport blogReport = BlogReport.builder()
                .reporterId(userId)
                .blogId(blogAddReportDTO.getBlogId())
                .reportedUserId(blog.getUserId()) // 设置被举报人ID
                .reportReason(blogAddReportDTO.getReportReason())
                .reportTime(LocalDateTime.now())
                .status(ReportStatusConstant.PENDING)
                .build();
        blogReportMapper.add(blogReport);
    }

    /**
     * 标记为处理中（联动更新同一博客的所有举报）
     * 
     * @param id         举报ID
     * @param reviewerId 审核员ID
     */
    @Override
    @Transactional
    public void markAsInProcess(Long id, Long reviewerId) {
        log.info("标记博客举报为处理中：id={}, reviewerId={}", id, reviewerId);

        // 查询举报记录
        BlogReport report = blogReportMapper.selectById(id);
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

        // 批量更新同一博客的所有举报为处理中状态
        blogReportMapper.updateStatusAndReviewerByBlogId(
                report.getBlogId(),
                ReportStatusConstant.IN_PROCESS,
                reviewerId);

        log.info("已将博客ID={}的所有举报标记为处理中，处理人ID={}", report.getBlogId(), reviewerId);
    }
}

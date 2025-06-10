package com.blog.service.impl;

import com.blog.constant.ConversationTypeConstant;
import com.blog.constant.MessageTypeConstant;
import com.blog.constant.ReviewType;
import com.blog.constant.resultConstant.BlogReportResultConstant;
import com.blog.constant.statusConstant.BlogStatusConstant;
import com.blog.constant.statusConstant.MessageStatusConstant;
import com.blog.constant.statusConstant.ReportStatusConstant;
import com.blog.mapper.*;
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
     * @param id
     * @return
     */
    @Override
    public BlogReportVO getById(Long id) {
        return blogReportMapper.getById(id);
    }

    /**
     * 处理举报
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
                .reportReason(blogReportDTO.getReviewReason())
                .status(ReportStatusConstant.RESOLVED) // 已处理
                .build();
        blogReportMapper.update(blogReport);

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
//                .userId(blog.getUserId())
                // 评论id
//                .commentId()
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
            Integer blogStatus = blogReportDTO.getReviewResult().equals(BlogReportResultConstant.NEEDS_REVIEW) ?
                    BlogStatusConstant.IN_REVIEW : BlogStatusConstant.PERMANENTLY_BANNED;
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
                    .senderId(blogReportDTO.getReviewerId())
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
        BlogReport blogReport = BlogReport.builder()
                .id(id)
                .status(status)
                .build();
        blogReportMapper.update(blogReport);
    }

    /**
     * 根据id删除举报
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        blogReportMapper.deleteById(id);
    }

    /**
     * 批量删除举报
     * @param ids
     */
    @Override
    public void deleteByIds(Long[] ids) {
        blogReportMapper.deleteByIds(ids);
    }
}

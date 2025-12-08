package com.blog.service.impl;

import com.blog.constant.ConversationTypeConstant;
import com.blog.constant.statusConstant.MessageStatusConstant;
import com.blog.constant.MessageTypeConstant;
import com.blog.constant.ReviewType;
import com.blog.mapper.ConversationMapper;
import com.blog.mapper.MessageMapper;
import com.blog.mapper.ReviewRecordMapper;
import com.blog.pojo.entity.Conversation;
import com.blog.pojo.entity.Message;
import com.blog.pojo.entity.ReviewRecord;
import com.blog.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 消息通知服务实现类
 * 统一处理系统消息发送和审核记录创建逻辑
 * @author Worlt
 */
@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private ConversationMapper conversationMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private ReviewRecordMapper reviewRecordMapper;

    /**
     * 发送系统通知消息
     * @param receiverId 接收者ID
     * @param senderId 发送者ID（通常为审核员ID）
     * @param messageContent 消息内容
     * @return 消息ID
     */
    @Override
    @Transactional
    public Long sendSystemMessage(Long receiverId, Long senderId, String messageContent) {
        log.debug("发送系统消息给用户: receiverId={}, senderId={}", receiverId, senderId);
        
        Long conversationId = getOrCreateSystemConversation(receiverId, messageContent);
        
        // 创建消息
        Message message = Message.builder()
                .conversationId(conversationId)
                .senderId(senderId)
                .receiverId(receiverId)
                .content(messageContent)
                .sendTime(LocalDateTime.now())
                .isRead(MessageStatusConstant.MESSAGE_UNREAD)
                .messageType(MessageTypeConstant.SYSTEM)
                .build();
        messageMapper.add(message);
        
        log.info("系统消息发送成功: messageId={}", message.getId());
        return message.getId();
    }

    /**
     * 创建审核记录
     * @param reviewerId 审核员ID
     * @param userId 用户ID（可选）
     * @param blogId 博客ID（可选）
     * @param commentId 评论ID（可选）
     * @param reviewResult 审核结果
     * @param reviewReason 审核原因
     * @param messageToAuthor 给作者的消息
     * @param reviewType 审核类型
     * @return 审核记录ID
     */
    @Override
    @Transactional
    public Long createReviewRecord(Long reviewerId, Long userId, Long blogId, Long commentId,
                                  Integer reviewResult, String reviewReason, String messageToAuthor,
                                  Integer reviewType) {
        log.debug("创建审核记录: reviewerId={}, reviewType={}", reviewerId, reviewType);
        
        ReviewRecord reviewRecord = ReviewRecord.builder()
                .reviewerId(reviewerId)
                .userId(userId)
                .blogId(blogId)
                .commentId(commentId)
                .reviewTime(LocalDateTime.now())
                .reviewResult(reviewResult)
                .reviewReason(reviewReason)
                .messageToAuthor(messageToAuthor)
                .reviewType(reviewType)
                .build();
        reviewRecordMapper.add(reviewRecord);
        
        log.info("审核记录创建成功: recordId={}", reviewRecord.getId());
        return reviewRecord.getId();
    }

    /**
     * 发送审核结果通知（包含创建审核记录和发送消息）
     * @param receiverId 接收者ID
     * @param senderId 发送者ID
     * @param messageContent 消息内容
     * @param reviewerId 审核员ID
     * @param userId 用户ID（可选）
     * @param blogId 博客ID（可选）
     * @param commentId 评论ID（可选）
     * @param reviewResult 审核结果
     * @param reviewReason 审核原因
     * @param reviewType 审核类型
     */
    @Override
    @Transactional
    public void sendReviewNotification(Long receiverId, Long senderId, String messageContent,
                                      Long reviewerId, Long userId, Long blogId, Long commentId,
                                      Integer reviewResult, String reviewReason, Integer reviewType) {
        log.debug("发送审核结果通知: receiverId={}, reviewType={}", receiverId, reviewType);
        
        // 创建审核记录
        createReviewRecord(reviewerId, userId, blogId, commentId, reviewResult, reviewReason, 
                          messageContent, reviewType);
        
        // 发送系统消息
        sendSystemMessage(receiverId, senderId, messageContent);
        
        log.info("审核结果通知发送完成: receiverId={}, reviewType={}", receiverId, reviewType);
    }

    /**
     * 获取或创建系统会话
     * @param receiverId 接收者ID
     * @param messageContent 消息内容
     * @return 会话ID
     */
    private Long getOrCreateSystemConversation(Long receiverId, String messageContent) {
        Long systemId = 0L;
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
                    .unreadCount2(1) // 接收方未读消息+1
                    .type(ConversationTypeConstant.SYSTEM)
                    .build();
            conversationMapper.insert(conversation);
            log.debug("创建新系统会话: conversationId={}", conversation.getId());
            return conversation.getId();
        } else {
            // 更新现有会话
            conversation.setLastMessage(messageContent);
            conversation.setUpdateTime(LocalDateTime.now());
            conversationMapper.updateLastMessage(conversation);
            conversationMapper.incrementUnreadCount2(conversation.getId());
            log.debug("更新现有系统会话: conversationId={}", conversation.getId());
            return conversation.getId();
        }
    }
} 
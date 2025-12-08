package com.blog.service;

import com.blog.constant.ReviewType;

/**
 * 消息通知服务接口
 * 统一处理系统消息发送逻辑，减少代码重复
 * @author Worlt
 */
public interface NotificationService {

    /**
     * 发送系统通知消息
     * @param receiverId 接收者ID
     * @param senderId 发送者ID（通常为审核员ID）
     * @param messageContent 消息内容
     * @return 消息ID
     */
    Long sendSystemMessage(Long receiverId, Long senderId, String messageContent);

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
    Long createReviewRecord(Long reviewerId, Long userId, Long blogId, Long commentId,
                           Integer reviewResult, String reviewReason, String messageToAuthor,
                           Integer reviewType);

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
    void sendReviewNotification(Long receiverId, Long senderId, String messageContent,
                               Long reviewerId, Long userId, Long blogId, Long commentId,
                               Integer reviewResult, String reviewReason, Integer reviewType);
} 
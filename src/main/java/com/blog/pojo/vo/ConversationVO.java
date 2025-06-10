package com.blog.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 会话视图对象
 *
 * @Author worlt
 * @Date 2025/6/10 下午8:00
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationVO {
    
    /**
     * 会话ID
     */
    private Long conversationId;
    
    /**
     * 对方用户ID
     */
    private Long targetUserId;
    
    /**
     * 对方用户名
     */
    private String targetUsername;
    
    /**
     * 对方头像
     */
    private String targetAvatar;
    
    /**
     * 最后一条消息预览
     */
    private String lastMessage;
    
    /**
     * 最后一条消息时间
     */
    private LocalDateTime lastMessageTime;
    
    /**
     * 未读消息数
     */
    private Integer unreadCount;
    
    /**
     * 会话类型：0-系统通知，1-用户私聊
     */
    private Integer type;
} 
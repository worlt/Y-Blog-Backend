package com.blog.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 消息视图对象
 *
 * @Author worlt
 * @Date 2025/6/10 下午8:00
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageVO {
    
    /**
     * 消息ID
     */
    private Long id;

    /**
     * 会话ID
     */
    private Long conversationId;
    
    /**
     * 发送者ID
     */
    private Long senderId;

    /**
     * 接收者ID
     */
    private Long receiverId;
    
    /**
     * 发送者用户名
     */
    private String senderName;

    /**
     * 接收者用户名
     */
    private String receiverName;
    
    /**
     * 发送者头像
     */
    private String senderAvatar;

    /**
     * 接收者头像
     */
    private String receiverAvatar;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 发送时间
     */
    private LocalDateTime sendTime;
    
    /**
     * 是否已读：0-未读，1-已读
     */
    private Integer isRead;
    
    /**
     * 消息类型：0-系统消息，1-用户消息
     */
    private Integer messageType;
    
    /**
     * 是否是自己发送的消息
     */
    private Boolean isMine;
}

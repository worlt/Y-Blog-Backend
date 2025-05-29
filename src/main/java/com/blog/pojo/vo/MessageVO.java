package com.blog.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Author worlt
 * @Date 2025/5/29 下午3:11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageVO {

    public Long id;

    public Long conversationId;

    public Long senderId;

    public String senderName;

    public String senderAvatar;

    public Long receiverId;

    public String receiverName;

    public String receiverAvatar;

    public String content;

    public LocalDateTime sendTime;

    public Integer isRead;

    public String messageType;

}

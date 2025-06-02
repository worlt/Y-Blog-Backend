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

    private Long id;

    private Long conversationId;

    private Long senderId;

    private String senderName;

    private String senderAvatar;

    private Long receiverId;

    private String receiverName;

    private String receiverAvatar;

    private String content;

    private LocalDateTime sendTime;

    private Integer isRead;

    private String messageType;

}

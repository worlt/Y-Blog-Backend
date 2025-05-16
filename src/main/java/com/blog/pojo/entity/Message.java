package com.blog.pojo.entity;

import io.micrometer.observation.transport.SenderContext;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 消息表：存储用户之间的消息记录
 *
 * @Author worlt
 * @Date 2025/5/16 下午9:41
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private Long id;

    // 会话id
    private Long conversationId;

    // 发送者id
    private Long senderId;

    // 接收者id
    private Long receiverId;

    // 消息内容
    private String content;

    // 发送时间
    private LocalDateTime sendTime;

    // 是否已读
    private Integer isRead;

    // 消息类型（如图片、文本）
    private String messageType;

}

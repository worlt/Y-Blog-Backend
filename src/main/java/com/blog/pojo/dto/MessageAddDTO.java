package com.blog.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author worlt
 * @Date 2025/5/29 下午9:47
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageAddDTO {

    private Long conversationId;

    private Long senderId;

    private Long receiverId;

    private String content;

    private String messageType;
}

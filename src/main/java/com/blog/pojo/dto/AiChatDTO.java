package com.blog.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI 对话请求 DTO
 * 
 * @author worlt
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AiChatDTO {

    /**
     * 用户消息内容
     */
    private String message;

    /**
     * 会话ID（可选，用于上下文记忆）
     */
    private String conversationId;
}

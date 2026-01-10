package com.blog.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI 对话响应 VO
 * 
 * @author worlt
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AiChatVO {

    /**
     * AI 回复内容
     */
    private String reply;

    /**
     * 会话ID
     */
    private String conversationId;

    /**
     * 思考过程（用于深度推理模型）
     */
    private String thinking;

    /**
     * 响应时间戳
     */
    private Long timestamp;
}

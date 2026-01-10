package com.blog.service;

import com.blog.pojo.dto.AiChatDTO;
import com.blog.pojo.vo.AiChatVO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * AI 对话服务接口
 * 
 * @author worlt
 */
public interface AiChatService {

    /**
     * AI 对话
     * 
     * @param aiChatDTO 对话参数
     * @return 对话结果
     */
    AiChatVO chat(AiChatDTO aiChatDTO);

    /**
     * 流式 AI 对话
     * 
     * @param aiChatDTO 对话参数
     * @param emitter   SSE Emitter
     */
    void streamChat(AiChatDTO aiChatDTO, SseEmitter emitter);
}

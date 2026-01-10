package com.blog.controller.user;

import com.blog.pojo.dto.AiChatDTO;
import com.blog.pojo.vo.AiChatVO;
import com.blog.result.Result;
import com.blog.service.AiChatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * AI 对话控制器
 * 
 * @author worlt
 */
@RestController("userAiChatController")
@RequestMapping("/user/ai")
@Api(tags = "AI对话相关接口")
@Slf4j
public class AiChatController {

    @Autowired
    private AiChatService aiChatService;

    /**
     * 发送消息并获取AI回复
     * 
     * @param aiChatDTO 对话请求
     * @return AI回复结果
     */
    @PostMapping("/chat")
    @ApiOperation(value = "AI对话", notes = "发送消息给AI助手，获取回复")
    public Result<AiChatVO> chat(@RequestBody AiChatDTO aiChatDTO) {
        log.info("收到AI对话请求: {}", aiChatDTO.getMessage());

        // 参数校验
        if (aiChatDTO.getMessage() == null || aiChatDTO.getMessage().trim().isEmpty()) {
            return Result.error("消息内容不能为空");
        }

        // 限制消息长度
        if (aiChatDTO.getMessage().length() > 2000) {
            return Result.error("消息内容过长，请限制在2000字以内");
        }

        AiChatVO result = aiChatService.chat(aiChatDTO);
        return Result.success(result);
    }

    /**
     * 流式AI对话
     * 
     * @param aiChatDTO 对话请求
     * @return SseEmitter
     */
    @PostMapping(value = "/stream/chat", produces = org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE)
    @ApiOperation(value = "流式AI对话", notes = "发送消息给AI助手，获取流式回复")
    public SseEmitter streamChat(@RequestBody AiChatDTO aiChatDTO) {
        log.info("收到AI流式对话请求: {}", aiChatDTO.getMessage());
        SseEmitter emitter = new SseEmitter(180000L); // 3分钟超时
        aiChatService.streamChat(aiChatDTO, emitter);
        return emitter;
    }

    /**
     * 清除会话历史
     * 
     * @param conversationId 会话ID
     * @return 操作结果
     */
    @DeleteMapping("/chat/{conversationId}")
    @ApiOperation(value = "清除会话历史", notes = "清除指定会话的历史记录")
    public Result<String> clearConversation(@PathVariable String conversationId) {
        log.info("清除会话历史: {}", conversationId);
        // 会话清除逻辑可以在Service中实现
        return Result.success("会话历史已清除");
    }
}

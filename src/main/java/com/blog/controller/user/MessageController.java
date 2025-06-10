package com.blog.controller.user;

import com.blog.context.BaseContext;
import com.blog.pojo.vo.ConversationVO;
import com.blog.pojo.vo.MessageVO;
import com.blog.result.Result;
import com.blog.service.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户消息控制器
 *
 * @Author worlt
 * @Date 2025/6/10 下午8:00
 */
@RestController("userMessageController")
@RequestMapping("/user/message")
@Api(tags = "用户消息相关接口")
@Slf4j
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 创建私聊会话
     * @param targetUserId 目标用户ID
     * @return 会话ID
     */
    @PostMapping("/conversations/private/{targetUserId}")
    @ApiOperation(value = "创建私聊会话")
    public Result<Long> createPrivateConversation(
            @PathVariable @ApiParam(value = "目标用户ID", required = true) Long targetUserId) {
        Long currentUserId = BaseContext.getCurrentId();
        return messageService.createPrivateConversation(currentUserId, targetUserId);
    }

    /**
     * 获取当前用户的会话列表
     * @return 会话列表
     */
    @GetMapping("/getConversations")
    @ApiOperation(value = "获取会话列表")
    public Result<List<ConversationVO>> getConversations() {
        Long currentUserId = BaseContext.getCurrentId();
        return messageService.getUserConversations(currentUserId);
    }

    /**
     * 获取会话消息列表
     * @param conversationId 会话ID
     * @return 消息列表
     */
    @GetMapping("/getConversationMessages/{conversationId}")
    @ApiOperation(value = "获取会话消息列表")
    public Result<List<MessageVO>> getConversationMessages(
            @PathVariable @ApiParam(value = "会话ID", required = true) Long conversationId,
            @RequestParam(required = false, defaultValue = "true") 
            @ApiParam(value = "是否自动标记已读", required = false) Boolean autoMarkRead) {
        Long currentUserId = BaseContext.getCurrentId();
        Result<List<MessageVO>> result = messageService.getConversationMessages(conversationId, currentUserId);
        
        // 如果设置了自动标记已读，则标记消息为已读
        if (autoMarkRead && result.getCode() == 1) {
            messageService.markConversationAsRead(conversationId, currentUserId);
        }
        
        return result;
    }

    /**
     * 发送私聊消息
     * @param receiverId 接收者ID
     * @param content 消息内容
     * @return 结果
     */
    @PostMapping("/send/private/{receiverId}")
    @ApiOperation(value = "发送私聊消息")
    public Result<Void> sendPrivateMessage(
            @PathVariable @ApiParam(value = "接收者ID", required = true) Long receiverId,
            @RequestParam @ApiParam(value = "消息内容", required = true) String content) {
        Long currentUserId = BaseContext.getCurrentId();
        return messageService.sendPrivateMessage(currentUserId, receiverId, content);
    }

    /**
     * 标记会话为已读
     * @param conversationId 会话ID
     * @return 结果
     */
    @PostMapping("/conversations/{conversationId}/read")
    @ApiOperation(value = "标记会话为已读")
    public Result<String> markConversationAsRead(
            @PathVariable @ApiParam(value = "会话ID", required = true) Long conversationId) {
        Long currentUserId = BaseContext.getCurrentId();
        messageService.markConversationAsRead(conversationId, currentUserId);
        return Result.success("消息已标记为已读");
    }

    /**
     * 从会话列表标记会话为已读
     * @param conversationId 会话ID
     * @return 结果
     */
    @PostMapping("/conversations/list/read/{conversationId}")
    @ApiOperation(value = "从会话列表标记会话为已读")
    public Result<String> markConversationAsReadFromList(
            @PathVariable @ApiParam(value = "会话ID", required = true) Long conversationId) {
        Long currentUserId = BaseContext.getCurrentId();
        messageService.markConversationAsRead(conversationId, currentUserId);
        return Result.success("消息已标记为已读");
    }
    
    /**
     * 一键标记所有会话为已读
     * @return 结果
     */
    @PostMapping("/conversations/read-all")
    @ApiOperation(value = "一键标记所有会话为已读")
    public Result<String> markAllConversationsAsRead() {
        Long currentUserId = BaseContext.getCurrentId();
        int count = messageService.markAllConversationsAsRead(currentUserId);
        return Result.success("已将" + count + "条消息标记为已读");
    }

    /**
     * 获取未读消息数
     * @return 未读消息数
     */
    @GetMapping("/unread/count")
    @ApiOperation(value = "获取未读消息数")
    public Result<Integer> getUnreadCount() {
        Long currentUserId = BaseContext.getCurrentId();
        Integer count = messageService.getUnreadMessageCount(currentUserId);
        return Result.success(count);
    }


    // TODO：消息通知
} 
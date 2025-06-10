package com.blog.service;

import com.blog.pojo.dto.MessageAddDTO;
import com.blog.pojo.dto.MessagePageQueryDTO;
import com.blog.pojo.vo.ConversationVO;
import com.blog.pojo.vo.MessageVO;
import com.blog.result.PageResult;
import com.blog.result.Result;

import java.util.List;

/**
 * 消息服务接口
 *
 * @Author worlt
 * @Date 2025/6/10 下午8:00
 */
public interface MessageService {

    /**
     * 分页查询消息
     * @param messagePageQueryDTO
     * @return
     */
    PageResult page(MessagePageQueryDTO messagePageQueryDTO);

    /**
     * 新增消息
     * @param messageAddDTO
     */
    void add(MessageAddDTO messageAddDTO);

    /**
     * 根据id查询消息详情
     * @param id
     * @return
     */
    MessageVO getById(Long id);

    /**
     * 根据id删除消息
     * @param id
     */
    void deleteById(Long id);

    /**
     * 创建私聊会话
     * @param currentUserId 当前用户ID
     * @param targetUserId 目标用户ID
     * @return 会话ID
     */
    Result<Long> createPrivateConversation(Long currentUserId, Long targetUserId);
    
    /**
     * 获取用户的会话列表
     * @param userId 用户ID
     * @return 会话列表
     */
    Result<List<ConversationVO>> getUserConversations(Long userId);
    
    /**
     * 获取会话消息列表
     * @param conversationId 会话ID
     * @param userId 请求者ID
     * @return 消息列表
     */
    Result<List<MessageVO>> getConversationMessages(Long conversationId, Long userId);
    
    /**
     * 发送私聊消息
     * @param senderId 发送者ID
     * @param receiverId 接收者ID
     * @param content 消息内容
     * @return 结果
     */
    Result<Void> sendPrivateMessage(Long senderId, Long receiverId, String content);
    
    /**
     * 发送系统通知
     * @param receiverId 接收者ID
     * @param content 消息内容
     * @return 结果
     */
    Result<Void> sendSystemMessage(Long receiverId, String content);
    
    /**
     * 将会话中的消息标记为已读
     * @param conversationId 会话ID
     * @param userId 用户ID
     */
    void markConversationAsRead(Long conversationId, Long userId);
    
    /**
     * 获取用户未读消息总数
     * @param userId 用户ID
     * @return 未读消息数
     */
    Integer getUnreadMessageCount(Long userId);
    
    /**
     * 检查是否有权限访问会话
     * @param conversationId 会话ID
     * @param userId 用户ID
     * @return 是否有权限
     */
    boolean canAccessConversation(Long conversationId, Long userId);

    /**
     * 一键标记所有会话为已读
     * @param userId 用户ID
     * @return 标记为已读的消息数量
     */
    Integer markAllConversationsAsRead(Long userId);
}

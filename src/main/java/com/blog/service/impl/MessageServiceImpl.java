package com.blog.service.impl;

import com.blog.constant.ConversationTypeConstant;
import com.blog.constant.MessageConstant;
import com.blog.constant.MessageTypeConstant;
import com.blog.constant.statusConstant.MessageStatusConstant;
import com.blog.mapper.ConversationMapper;
import com.blog.mapper.MessageMapper;
import com.blog.mapper.UserMapper;
import com.blog.pojo.dto.MessageAddDTO;
import com.blog.pojo.dto.MessagePageQueryDTO;
import com.blog.pojo.entity.Conversation;
import com.blog.pojo.entity.Message;
import com.blog.pojo.entity.User;
import com.blog.pojo.vo.ConversationVO;
import com.blog.pojo.vo.MessageVO;
import com.blog.result.PageResult;
import com.blog.result.Result;
import com.blog.service.MessageService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息服务实现类
 *
 * @Author worlt
 * @Date 2025/6/10 下午8:00
 */
@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private ConversationMapper conversationMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 分页查询消息
     * @param messagePageQueryDTO
     * @return
     */
    @Override
    public PageResult page(MessagePageQueryDTO messagePageQueryDTO) {
        PageHelper.startPage(messagePageQueryDTO.getPage(), messagePageQueryDTO.getPageSize());
        Page<MessageVO> page = messageMapper.page(messagePageQueryDTO);
        
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 新增消息
     * @param messageAddDTO
     */
    @Override
    public void add(MessageAddDTO messageAddDTO) {
        Message message = new Message();
        BeanUtils.copyProperties(messageAddDTO, message);

        message.setSendTime(LocalDateTime.now());
        message.setIsRead(MessageStatusConstant.MESSAGE_UNREAD);

        messageMapper.add(message);
    }

    /**
     * 根据id查询消息详情
     * @param id
     * @return
     */
    @Override
    public MessageVO getVOById(Long id) {
        return messageMapper.getVOById(id);
    }

    @Override
    public void deleteById(Long id) {
        messageMapper.deleteById(id);
    }

    /**
     * 创建私聊会话
     * @param currentUserId 当前用户ID
     * @param targetUserId 目标用户ID
     * @return 会话ID
     */
    @Override
    @Transactional
    public Result<Long> createPrivateConversation(Long currentUserId, Long targetUserId) {
        // 确保两个用户ID顺序一致
        Long smallerId = Math.min(currentUserId, targetUserId);
        Long largerId = Math.max(currentUserId, targetUserId);

        // 检查是否已存在会话
        Conversation existingConversation = conversationMapper.findByUserPair(smallerId, largerId);
        if (existingConversation != null) {
            return Result.success(existingConversation.getId());
        }

        // 创建新会话
        Conversation conversation = Conversation.builder()
                .user1Id(smallerId)
                .user2Id(largerId)
                .lastMessage("")
                .unreadCount1(0)
                .unreadCount2(0)
                .type(ConversationTypeConstant.PRIVATE)
                .build();

        conversationMapper.insert(conversation);
        return Result.success(conversation.getId());
    }

    /**
     * 获取用户的会话列表
     * @param userId 用户ID
     * @return 会话列表
     */
    @Override
    public Result<List<ConversationVO>> getUserConversations(Long userId) {
        List<Conversation> conversations = conversationMapper.findByUserId(userId);
        List<ConversationVO> result = conversations.stream()
                .map(conversation -> {
                    ConversationVO vo = new ConversationVO();
                    vo.setConversationId(conversation.getId());
                    vo.setLastMessage(conversation.getLastMessage());
                    vo.setLastMessageTime(conversation.getUpdateTime());
                    vo.setType(conversation.getType());

                    // 设置对方用户信息
                    if (conversation.getType() == ConversationTypeConstant.PRIVATE) {
                        Long targetUserId = conversation.getUser1Id().equals(userId) 
                                ? conversation.getUser2Id() : conversation.getUser1Id();
                        User targetUser = userMapper.getById(targetUserId);
                        if (targetUser != null) {
                            vo.setTargetUserId(targetUserId);
                            vo.setTargetUsername(targetUser.getUsername());
                            vo.setTargetAvatar(targetUser.getAvatar());
                        }
                    } else {
                        // 系统通知会话
                        vo.setTargetUsername("系统通知");
                        vo.setTargetAvatar("/images/system_avatar.png");
                    }

                    // 设置未读消息数
                    if (userId.equals(conversation.getUser1Id())) {
                        vo.setUnreadCount(conversation.getUnreadCount1());
                    } else {
                        vo.setUnreadCount(conversation.getUnreadCount2());
                    }

                    return vo;
                })
                .collect(Collectors.toList());

        return Result.success(result);
    }

    /**
     * 获取会话消息列表
     * @param conversationId 会话ID
     * @param userId 请求者ID
     * @return 消息列表
     */
    @Override
    public Result<List<MessageVO>> getConversationMessages(Long conversationId, Long userId) {
        // 检查权限
        if (!canAccessConversation(conversationId, userId)) {
            return Result.error(MessageConstant.NO_PERMISSION);
        }

        // 获取消息列表
        List<Message> messages = messageMapper.findByConversationId(conversationId);
        List<MessageVO> result = messages.stream()
                .map(message -> {
                    MessageVO vo = new MessageVO();
                    vo.setMessageId(message.getId());
                    vo.setSenderId(message.getSenderId());
                    vo.setContent(message.getContent());
                    vo.setSendTime(message.getSendTime());
                    vo.setIsRead(message.getIsRead());
                    vo.setMessageType(message.getMessageType());
                    vo.setIsMine(userId.equals(message.getSenderId()));

                    // 设置发送者信息
                    User sender = userMapper.getById(message.getSenderId());
                    if (sender != null) {
                        vo.setSenderName(sender.getUsername());
                        vo.setSenderAvatar(sender.getAvatar());
                    } else if (MessageTypeConstant.SYSTEM.equals(message.getMessageType())) {
                        vo.setSenderName("系统通知");
                        vo.setSenderAvatar("/images/system_avatar.png");
                    }

                    return vo;
                })
                .collect(Collectors.toList());

        return Result.success(result);
    }

    /**
     * 发送私聊消息
     * @param senderId 发送者ID
     * @param receiverId 接收者ID
     * @param content 消息内容
     * @return 结果
     */
    @Override
    @Transactional
    public Result<Void> sendPrivateMessage(Long senderId, Long receiverId, String content) {
        try {
            // 获取或创建会话
            Long smallerId = Math.min(senderId, receiverId);
            Long largerId = Math.max(senderId, receiverId);
            
            // 查找已存在的会话
            Conversation conversation = conversationMapper.findByUserPair(smallerId, largerId);
            
            // 创建新会话
            if (conversation == null) {
                // 再次查询以防并发情况下重复创建
                conversation = conversationMapper.findByUserPair(smallerId, largerId);
                
                if (conversation == null) {
                    conversation = Conversation.builder()
                            .user1Id(smallerId)
                            .user2Id(largerId)
                            .lastMessage(content)
                            .unreadCount1(0)
                            .unreadCount2(0)
                            .type(ConversationTypeConstant.PRIVATE)
                            .build();
                    conversationMapper.insert(conversation);
                }
            }
            
            // 发送消息
            Message message = Message.builder()
                    .conversationId(conversation.getId())
                    .senderId(senderId)
                    .receiverId(receiverId)
                    .content(content)
                    .isRead(MessageStatusConstant.MESSAGE_UNREAD)
                    .messageType(MessageTypeConstant.USER)
                    .build();
            messageMapper.insert(message);
            
            // 更新会话最后消息和未读计数
            conversation.setLastMessage(content);
            conversationMapper.updateLastMessage(conversation);
            
            // 更新未读消息计数
            if (receiverId.equals(conversation.getUser1Id())) {
                conversationMapper.incrementUnreadCount1(conversation.getId());
            } else {
                conversationMapper.incrementUnreadCount2(conversation.getId());
            }
            
            return Result.success();
        } catch (Exception e) {
            log.error("发送私聊消息失败: ", e);
            return Result.error(MessageConstant.SEND_MESSAGE_FAILED);
        }
    }

    /**
     * 发送系统通知
     * @param receiverId 接收者ID
     * @param content 消息内容
     * @return 结果
     */
    @Override
    @Transactional
    public Result<Void> sendSystemMessage(Long receiverId, String content) {
        try {
            // 为系统通知创建会话，这里系统ID用0表示
            Long systemId = 0L;
            
            // 查找已存在的系统通知会话
            Conversation conversation = conversationMapper.findByUserPair(systemId, receiverId);
            
            if (conversation == null) {
                // 再次查询以防并发情况下重复创建
                conversation = conversationMapper.findByUserPair(systemId, receiverId);
                
                if (conversation == null) {
                    conversation = Conversation.builder()
                            .user1Id(systemId)
                            .user2Id(receiverId)
                            .lastMessage(content)
                            .unreadCount1(0)
                            .unreadCount2(0)
                            .type(ConversationTypeConstant.SYSTEM)
                            .build();
                    conversationMapper.insert(conversation);
                }
            }
            
            // 发送消息
            Message message = Message.builder()
                    .conversationId(conversation.getId())
                    .senderId(systemId)
                    .receiverId(receiverId)
                    .content(content)
                    .isRead(MessageStatusConstant.MESSAGE_UNREAD)
                    .messageType(MessageTypeConstant.SYSTEM)
                    .build();
            messageMapper.insert(message);
            
            // 更新会话最后消息和未读计数
            conversation.setLastMessage(content);
            conversationMapper.updateLastMessage(conversation);
            conversationMapper.incrementUnreadCount2(conversation.getId());
            
            return Result.success();
        } catch (Exception e) {
            log.error("发送系统消息失败: ", e);
            return Result.error(MessageConstant.SEND_MESSAGE_FAILED);
        }
    }

    /**
     * 将会话中的消息标记为已读
     * @param conversationId 会话ID
     * @param userId 用户ID
     */
    @Override
    @Transactional
    public void markConversationAsRead(Long conversationId, Long userId) {
        // 检查会话是否存在
        Conversation conversation = conversationMapper.getById(conversationId);
        if (conversation == null) {
            throw new RuntimeException(MessageConstant.CONVERSATION_NOT_EXIST);
        }
        
        // 检查用户是否有权限访问该会话
        if (!canAccessConversation(conversationId, userId)) {
            throw new RuntimeException(MessageConstant.NO_PERMISSION);
        }
        
        // 标记消息为已读
        int updatedCount = messageMapper.markAsRead(conversationId, userId);
        
        // 重置未读计数
        if (userId.equals(conversation.getUser1Id())) {
            conversationMapper.resetUnreadCount1(conversationId);
        } else if (userId.equals(conversation.getUser2Id())) {
            conversationMapper.resetUnreadCount2(conversationId);
        }
        
        log.info("用户{}将会话{}中的{}条消息标记为已读", userId, conversationId, updatedCount);
    }

    /**
     * 获取用户未读消息总数
     * @param userId 用户ID
     * @return 未读消息数
     */
    @Override
    public Integer getUnreadMessageCount(Long userId) {
        return conversationMapper.countUserUnread(userId);
    }

    /**
     * 检查是否有权限访问会话
     * @param conversationId 会话ID
     * @param userId 用户ID
     * @return 是否有权限
     */
    @Override
    public boolean canAccessConversation(Long conversationId, Long userId) {
        Conversation conversation = conversationMapper.getById(conversationId);
        if (conversation == null) {
            return false;
        }
        
        // 检查用户是否为会话参与者
        return userId.equals(conversation.getUser1Id()) || userId.equals(conversation.getUser2Id());
    }

    /**
     * 一键标记所有会话为已读
     * @param userId 用户ID
     * @return 标记为已读的消息数量
     */
    @Override
    @Transactional
    public Integer markAllConversationsAsRead(Long userId) {
        // 获取所有有未读消息的会话
        List<Conversation> unreadConversations = conversationMapper.findUnreadByUserId(userId);
        if (unreadConversations.isEmpty()) {
            return 0;
        }
        
        int totalMarked = 0;
        
        // 遍历每个会话并标记为已读
        for (Conversation conversation : unreadConversations) {
            // 标记消息为已读
            int updatedCount = messageMapper.markAsRead(conversation.getId(), userId);
            totalMarked += updatedCount;
            
            // 重置未读计数
            if (userId.equals(conversation.getUser1Id())) {
                conversationMapper.resetUnreadCount1(conversation.getId());
            } else if (userId.equals(conversation.getUser2Id())) {
                conversationMapper.resetUnreadCount2(conversation.getId());
            }
        }
        
        log.info("用户{}一键已读，共标记{}个会话中的{}条消息为已读", userId, unreadConversations.size(), totalMarked);
        return totalMarked;
    }

    /**
     * 撤回两分钟内的消息
     * @param userId
     * @param id
     */
    @Override
    @Transactional
    public void retract(Long userId, Long id) {
        // 1. 获取消息
        Message message = messageMapper.getById(id);
        if (message == null) {
            throw new RuntimeException(MessageConstant.MESSAGE_NOT_FOUND);
        }

        // 2. 验证发送者
        if (!userId.equals(message.getSenderId())) {
            throw new RuntimeException("只能撤回自己发送的消息");
        }

        // 3. 检查消息发送时间是否在2分钟内
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sendTime = message.getSendTime();
        long minutesDiff = java.time.Duration.between(sendTime, now).toMinutes();

        if (minutesDiff > 2) {
            throw new RuntimeException("只能撤回2分钟内的消息");
        }

        // 4. 撤回消息（标记为已撤回或删除）
        message.setContent("[该消息已撤回]");
        messageMapper.update(message);

        // 5. 如果是会话的最后一条消息，更新会话的最后消息预览
        Conversation conversation = conversationMapper.getById(message.getConversationId());
        if (conversation != null && conversation.getLastMessage().equals(message.getContent())) {
            conversation.setLastMessage("[该消息已撤回]");
            conversationMapper.updateLastMessage(conversation);
        }
    }
}

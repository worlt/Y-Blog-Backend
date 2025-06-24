package com.blog.service.impl;

import com.blog.constant.ConversationTypeConstant;
import com.blog.constant.MessageTypeConstant;
import com.blog.constant.ReviewType;
import com.blog.constant.statusConstant.AppealStatusConstant;
import com.blog.constant.statusConstant.MessageStatusConstant;
import com.blog.constant.statusConstant.UserStatusConstant;
import com.blog.constant.resultConstant.AppealResultConstant;
import com.blog.mapper.*;
import com.blog.pojo.dto.AppealDTO;
import com.blog.pojo.dto.AppealPageQueryDTO;
import com.blog.pojo.entity.*;
import com.blog.pojo.vo.AppealVO;
import com.blog.result.PageResult;
import com.blog.result.Result;
import com.blog.service.AppealService;
import com.blog.service.EmailService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 申诉服务实现
 * @Author worlt
 * @Date 2025/6/4 下午9:25
 */
@Service
@Slf4j
public class AppealServiceImpl implements AppealService {

    @Autowired
    private AppealMapper appealMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private ConversationMapper conversationMapper;
    
    @Autowired
    private MessageMapper messageMapper;
    
    @Autowired
    private ReviewRecordMapper reviewRecordMapper;
    
    @Autowired
    private EmailService emailService;
    
    /**
     * 分页查询
     * @param appealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(AppealPageQueryDTO appealPageQueryDTO) {
        PageHelper.startPage(appealPageQueryDTO.getPage(), appealPageQueryDTO.getPageSize());
        Page<AppealVO> page = appealMapper.pageQuery(appealPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 根据id查询申诉详情
     * @param id
     * @return
     */
    @Override
    public AppealVO getById(Long id) {
        return appealMapper.getById(id);
    }

    /**
     * 处理申诉
     * @param appealDTO
     */
    @Override
    @Transactional
    public void handle(AppealDTO appealDTO) {
        // 获取申诉详情
        Appeal appeal = appealMapper.selectById(appealDTO.getId());
        if (appeal == null) {
            throw new RuntimeException("申诉记录不存在");
        }
        
        // 获取申诉用户
        User user = userMapper.getById(appeal.getUserId());
        if (user == null) {
            throw new RuntimeException("申诉用户不存在");
        }
        
        // 更新申诉状态
        Appeal appealUpdate = Appeal.builder()
                .id(appealDTO.getId())
                .reviewerId(appealDTO.getReviewerId())
                .reviewTime(LocalDateTime.now())
                .reviewResult(appealDTO.getReviewResult())
                .reviewReason(appealDTO.getReviewReason())
                .status(AppealStatusConstant.RESOLVED) // 已处理
                .build();
        appealMapper.update(appealUpdate);
        
        // 构建消息内容
        String messageContent = null;
        boolean isAccepted = false;
        
        // 如果审核结果为接受，则解封用户
        if (appealDTO.getReviewResult().equals(AppealResultConstant.ACCEPT)) {
            messageContent = "您的申诉已通过审核，账号已解封。";
            userMapper.updateStatus(appeal.getUserId(), UserStatusConstant.NORMAL);
            isAccepted = true;
        } else {
            messageContent = "您的申诉审核未通过。原因：" + appealDTO.getReviewReason();
        }
        
        // 创建审核记录
        ReviewRecord reviewRecord = ReviewRecord.builder()
                .reviewerId(appealDTO.getReviewerId())
                .userId(appeal.getUserId())
                .reviewTime(LocalDateTime.now())
                .reviewResult(appealDTO.getReviewResult())
                .reviewReason(appealDTO.getReviewReason())
                .messageToAuthor(messageContent)
                .reviewType(ReviewType.APPEAL)
                .build();
        reviewRecordMapper.add(reviewRecord);
        
        // 发送消息通知
        Long receiverId = appeal.getUserId();
        
        // 查找是否存在会话，不存在则创建新会话
        Long systemId = 0L;
        Long conversationId;
        Conversation conversation = conversationMapper.findByUserPair(systemId, receiverId);
        
        if (conversation == null) {
            // 创建新会话
            conversation = Conversation.builder()
                    .user1Id(systemId)
                    .user2Id(receiverId)
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .lastMessage(messageContent)
                    .unreadCount1(0)
                    .unreadCount2(1) // 接收方未读消息+1
                    .type(ConversationTypeConstant.SYSTEM)
                    .build();
            conversationMapper.insert(conversation);
            conversationId = conversation.getId();
        } else {
            conversationId = conversation.getId();
            // 更新会话最后消息和未读计数
            conversation.setLastMessage(messageContent);
            conversation.setUpdateTime(LocalDateTime.now());
            conversationMapper.updateLastMessage(conversation);
            conversationMapper.incrementUnreadCount2(conversationId);
        }
        
        // 创建消息
        Message message = Message.builder()
                .conversationId(conversationId)
                .senderId(appealDTO.getReviewerId())
                .receiverId(receiverId)
                .content(messageContent)
                .sendTime(LocalDateTime.now())
                .isRead(MessageStatusConstant.MESSAGE_UNREAD)
                .messageType(MessageTypeConstant.SYSTEM)
                .build();
        messageMapper.add(message);
        
        // 发送邮件通知
        String email = appeal.getEmail();
        if (email != null && !email.isEmpty()) {
            String subject = "申诉结果通知";
            boolean emailSent = emailService.sendAppealResultEmail(
                    email, 
                    subject, 
                    messageContent, 
                    isAccepted, 
                    appealDTO.getReviewReason()
            );
            
            if (!emailSent) {
                log.error("申诉结果邮件发送失败，用户ID: {}, 邮箱: {}", appeal.getUserId(), email);
            }
        } else {
            log.warn("申诉用户没有提供邮箱，无法发送通知邮件，用户ID: {}", appeal.getUserId());
        }
    }

    /**
     * 更新申诉状态
     * @param id
     * @param status
     */
    @Override
    public void setStatus(Long id, Integer status) {
        Appeal appeal = Appeal.builder()
                .id(id)
                .status(status)
                .build();
        appealMapper.update(appeal);
    }

    /**
     * 根据id删除申诉
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        appealMapper.deleteById(id);
    }

    /**
     * 批量删除申诉
     * @param ids
     */
    @Override
    public void deleteByIds(Long[] ids) {
        appealMapper.deleteByIds(ids);
    }
    
    /**
     * 提交申诉
     * @param appeal
     */
    @Override
    public void submit(Appeal appeal) {
        // 校验申诉邮箱
        String email = appeal.getEmail();
        if (email == null || email.isEmpty()) {
            throw new RuntimeException("申诉邮箱不能为空");
        }

        // 校验申诉原因
        if (appeal.getAppealReason() == null || appeal.getAppealReason().isEmpty()) {
            throw new RuntimeException("申诉原因不能为空");
        }

        // 校验申诉的用户是否存在
        User user = userMapper.getByEmail(appeal.getEmail());
        if (user == null) {
            throw new RuntimeException("申诉用户不存在");
        }

        // 设置申诉时间和状态
        appeal.setAppealTime(LocalDateTime.now());
        appeal.setStatus(AppealStatusConstant.PENDING); // 待审核
        appeal.setUserId(user.getId());
        
        // 校验用户状态，只有被封禁的用户才能申诉
        if (!UserStatusConstant.BANNED.equals(user.getStatus())) {
            throw new RuntimeException("只有被封禁的账号才能提交申诉");
        }
        
        // 校验是否已有待处理的申诉
        AppealPageQueryDTO queryDTO = new AppealPageQueryDTO();
        queryDTO.setUserId(user.getId());
        queryDTO.setStatus(AppealStatusConstant.PENDING);
        PageResult pageResult = pageQuery(queryDTO);
        if (pageResult.getTotal() > 0) {
            throw new RuntimeException("您已有待处理的申诉，请勿重复提交");
        }
        
        // 添加申诉记录
        appealMapper.add(appeal);
        
        log.info("用户 {} 提交申诉成功，申诉ID: {}", appeal.getUserId(), appeal.getId());
    }
} 
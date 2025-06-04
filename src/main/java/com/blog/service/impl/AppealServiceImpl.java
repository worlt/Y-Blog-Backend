package com.blog.service.impl;

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
import com.blog.service.AppealService;
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
        
        // 如果审核结果为接受，则解封用户
        if (appealDTO.getReviewResult().equals(AppealResultConstant.ACCEPT)) {
            messageContent = "您的申诉已通过审核，账号已解封。";
            userMapper.updateStatus(appeal.getUserId(), UserStatusConstant.NORMAL);
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
        Long conversationId = messageMapper.getConversationByReceiverIdAndMessageType(receiverId, MessageTypeConstant.SYSTEM);
        if (conversationId == null) {
            // 创建新会话
            Conversation conversation = Conversation.builder()
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();
            conversationId = conversationMapper.add(conversation);
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
} 
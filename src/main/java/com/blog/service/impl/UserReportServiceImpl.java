package com.blog.service.impl;

import com.blog.constant.ConversationTypeConstant;
import com.blog.constant.MessageConstant;
import com.blog.constant.MessageTypeConstant;
import com.blog.constant.ReviewType;
import com.blog.constant.statusConstant.MessageStatusConstant;
import com.blog.constant.statusConstant.ReportStatusConstant;
import com.blog.constant.statusConstant.UserStatusConstant;
import com.blog.constant.resultConstant.UserReportResultConstant;
import com.blog.context.BaseContext;
import com.blog.mapper.*;
import com.blog.pojo.dto.UserAddReportDTO;
import com.blog.pojo.dto.UserReportDTO;
import com.blog.pojo.dto.UserReportPageQueryDTO;
import com.blog.pojo.entity.*;
import com.blog.pojo.vo.UserReportVO;
import com.blog.result.PageResult;
import com.blog.service.UserReportService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户举报服务实现
 * @Author worlt
 * @Date 2025/6/4 下午2:40
 */
@Service
@Slf4j
public class UserReportServiceImpl implements UserReportService {

    @Autowired
    private UserReportMapper userReportMapper;
    
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
     * @param userReportPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(UserReportPageQueryDTO userReportPageQueryDTO) {
        PageHelper.startPage(userReportPageQueryDTO.getPage(), userReportPageQueryDTO.getPageSize());
        Page<UserReportVO> page = userReportMapper.pageQuery(userReportPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 根据id查询举报详情
     * @param id
     * @return
     */
    @Override
    public UserReportVO getById(Long id) {
        return userReportMapper.getById(id);
    }

    /**
     * 处理举报
     * @param userReportDTO
     */
    @Override
    @Transactional
    public void handle(UserReportDTO userReportDTO) {
        // 获取举报详情
        UserReport report = userReportMapper.selectById(userReportDTO.getId());
        if (report == null) {
            throw new RuntimeException("举报记录不存在");
        }
        
        // 获取被举报用户
        User reportedUser = userMapper.getById(report.getReportedUserId());
        if (reportedUser == null) {
            throw new RuntimeException("被举报用户不存在");
        }
        
        // 更新举报状态
        UserReport userReport = UserReport.builder()
                .id(userReportDTO.getId())
                .reviewerId(userReportDTO.getReviewerId())
                .reviewTime(LocalDateTime.now())
                .reviewResult(userReportDTO.getReviewResult())
                .reviewReason(userReportDTO.getReviewReason())
                .status(ReportStatusConstant.RESOLVED) // 已处理
                .build();
        userReportMapper.update(userReport);
        
        // 如果审核结果为封禁，则更新用户状态
        if (userReportDTO.getReviewResult().equals(UserReportResultConstant.BANNED)) { // 封禁
            userMapper.updateStatus(report.getReportedUserId(), UserStatusConstant.BANNED);
        }
        
        // 向被举报用户发送消息通知
        Long receiverId = report.getReportedUserId();
        
        // 构建消息内容
        String messageContent = null;
        if (userReportDTO.getReviewResult().equals(UserReportResultConstant.BANNED)) { // 封禁
            messageContent = "您的账号因被举报且审核结果为违规，已被封禁。原因：" + userReportDTO.getReviewReason();
        } else {
            messageContent = "您的账号被举报，但经审核未发现违规行为，账号状态正常。";
        }
        
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
                    .type(ConversationTypeConstant.SYSTEM) // 使用常量
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
        
        ReviewRecord reviewRecord = ReviewRecord.builder()
                .reviewerId(userReportDTO.getReviewerId())
                .userId(reportedUser.getId())
                .reviewTime(LocalDateTime.now())
                .reviewResult(userReportDTO.getReviewResult())
                .reviewReason(userReportDTO.getReviewReason())
                .messageToAuthor(messageContent)
                .reviewType(ReviewType.USER)
                .build();
        reviewRecordMapper.add(reviewRecord);
        
        // 创建消息
        Message message = Message.builder()
                .conversationId(conversationId)
                .senderId(userReportDTO.getReviewerId())
                .receiverId(receiverId)
                .content(messageContent)
                .sendTime(LocalDateTime.now())
                .isRead(MessageStatusConstant.MESSAGE_UNREAD)
                .messageType(MessageTypeConstant.SYSTEM)
                .build();
        messageMapper.add(message);
    }

    /**
     * 更新举报状态
     * @param id
     * @param status
     */
    @Override
    public void setStatus(Long id, Integer status) {
        UserReport userReport = UserReport.builder()
                .id(id)
                .status(status)
                .build();
        userReportMapper.update(userReport);
    }

    /**
     * 根据id删除举报
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        userReportMapper.deleteById(id);
    }

    /**
     * 批量删除举报
     * @param ids
     */
    @Override
    public void deleteByIds(Long[] ids) {
        userReportMapper.deleteByIds(ids);
    }

    /**
     * 举报用户
     * @param userAddReportDTO
     */
    @Override
    public void add(UserAddReportDTO userAddReportDTO) {
        Long userId = BaseContext.getCurrentId();
        if (userId == null) throw new RuntimeException(MessageConstant.USER_NOT_LOGIN);

        UserReport userReport = UserReport.builder()
                .userId(userId)
                .reportedUserId(userAddReportDTO.getReportedUserId())
                .reportReason(userAddReportDTO.getReportReason())
                .reportTime(LocalDateTime.now())
                .status(ReportStatusConstant.PENDING)
                .build();
        userReportMapper.add(userReport);
    }
}

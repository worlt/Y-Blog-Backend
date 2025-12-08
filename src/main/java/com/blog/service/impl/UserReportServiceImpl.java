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
import com.blog.service.NotificationService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@Slf4j
public class UserReportServiceImpl implements UserReportService {

    @Autowired
    private UserReportMapper userReportMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private ReviewRecordMapper reviewRecordMapper;
    
    @Autowired
    private ConversationMapper conversationMapper;
    
    @Autowired
    private MessageMapper messageMapper;

    private final NotificationService notificationService;
    
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
     * @param userReportDTO 用户举报处理DTO
     */
    @Override
    @Transactional
    public void handle(UserReportDTO userReportDTO) {
        log.info("开始处理用户举报，举报ID：{}", userReportDTO.getId());
        
        // 验证举报记录和用户存在性
        UserReport report = validateReportExists(userReportDTO.getId());
        User reportedUser = validateUserExists(report.getReportedUserId());
        
        // 更新举报状态
        updateReportStatus(userReportDTO);
        
        // 处理举报结果
        processReportResult(userReportDTO, report);
        
        // 构建通知消息
        String messageContent = buildNotificationMessage(userReportDTO);
        
        // 发送通知
        sendNotification(userReportDTO, reportedUser, messageContent);
        
        log.info("用户举报处理完成，举报ID：{}", userReportDTO.getId());
    }

    /**
     * 验证举报记录是否存在
     * @param reportId 举报ID
     * @return 举报记录
     */
    private UserReport validateReportExists(Long reportId) {
        UserReport report = userReportMapper.selectById(reportId);
        if (report == null) {
            log.error("举报记录不存在，举报ID：{}", reportId);
            throw new RuntimeException("举报记录不存在");
        }
        return report;
    }

    /**
     * 验证被举报用户是否存在
     * @param userId 用户ID
     * @return 用户记录
     */
    private User validateUserExists(Long userId) {
        User user = userMapper.getById(userId);
        if (user == null) {
            log.error("被举报用户不存在，用户ID：{}", userId);
            throw new RuntimeException("被举报用户不存在");
        }
        return user;
    }

    /**
     * 更新举报状态
     * @param userReportDTO 举报处理DTO
     */
    private void updateReportStatus(UserReportDTO userReportDTO) {
        UserReport userReport = UserReport.builder()
                .id(userReportDTO.getId())
                .reviewerId(userReportDTO.getReviewerId())
                .reviewTime(LocalDateTime.now())
                .reviewResult(userReportDTO.getReviewResult())
                .reviewReason(userReportDTO.getReviewReason())
                .status(ReportStatusConstant.RESOLVED) // 已处理
                .build();
        userReportMapper.update(userReport);
        log.debug("举报状态更新成功，举报ID：{}", userReportDTO.getId());
    }

    /**
     * 处理举报结果（封禁用户等）
     * @param userReportDTO 举报处理DTO
     * @param report 举报记录
     */
    private void processReportResult(UserReportDTO userReportDTO, UserReport report) {
        if (userReportDTO.getReviewResult().equals(UserReportResultConstant.BANNED)) {
            userMapper.updateStatus(report.getReportedUserId(), UserStatusConstant.BANNED);
            log.info("用户被封禁，用户ID：{}", report.getReportedUserId());
        } else {
            log.info("用户举报审核未通过，用户状态保持不变，用户ID：{}", report.getReportedUserId());
        }
    }

    /**
     * 构建通知消息内容
     * @param userReportDTO 举报处理DTO
     * @return 消息内容
     */
    private String buildNotificationMessage(UserReportDTO userReportDTO) {
        String messageContent;
        if (userReportDTO.getReviewResult().equals(UserReportResultConstant.BANNED)) {
            messageContent = "您的账号因被举报且审核结果为违规，已被封禁。原因：" + userReportDTO.getReviewReason();
        } else {
            messageContent = "您的账号被举报，但经审核未发现违规行为，账号状态正常。";
        }
        return messageContent;
    }

    /**
     * 发送通知消息
     * @param userReportDTO 举报处理DTO
     * @param reportedUser 被举报用户
     * @param messageContent 消息内容
     */
    private void sendNotification(UserReportDTO userReportDTO, User reportedUser, String messageContent) {
        notificationService.sendReviewNotification(
                reportedUser.getId(),
                userReportDTO.getReviewerId(),
                messageContent,
                userReportDTO.getReviewerId(),
                reportedUser.getId(),
                null,
                null,
                userReportDTO.getReviewResult(),
                userReportDTO.getReviewReason(),
                ReviewType.USER
        );
        log.info("用户举报结果通知发送完成，用户ID：{}", reportedUser.getId());
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

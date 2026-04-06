package com.blog.service.impl;

import com.blog.constant.MessageConstant;
import com.blog.constant.ReviewType;
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
 * 
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

    private final NotificationService notificationService;

    // 使用UserService来统一处理用户状态变更和邮件通知
    @Autowired
    private com.blog.service.UserService userService;

    /**
     * 分页查询
     * 
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
     * 
     * @param id
     * @return
     */
    @Override
    public UserReportVO getById(Long id) {
        return userReportMapper.getById(id);
    }

    /**
     * 处理举报
     * 
     * @param userReportDTO 用户举报处理DTO
     */
    @Override
    @Transactional
    public void handle(UserReportDTO userReportDTO) {
        log.info("开始处理用户举报，举报ID：{}", userReportDTO.getId());

        // 验证举报记录和用户存在性
        UserReport report = validateReportExists(userReportDTO.getId());

        // 权限检查：必须是处理中状态，并且与当前审核员匹配
        if (report.getStatus() == null || !report.getStatus().equals(ReportStatusConstant.IN_PROCESS)) {
            throw new RuntimeException("该举报未处于处理中状态，无法进行审核操作");
        }
        if (report.getReviewerId() == null || !report.getReviewerId().equals(userReportDTO.getReviewerId())) {
            log.error("权限不足，举报ID：{}，当前审核员：{}，处理中审核员：{}",
                    userReportDTO.getId(), userReportDTO.getReviewerId(), report.getReviewerId());
            throw new RuntimeException("该举报正在被其他审核员处理，您无权操作");
        }

        User reportedUser = validateUserExists(report.getReportedUserId());

        // 更新举报状态
        updateReportStatus(userReportDTO, report);

        // 处理举报结果
        processReportResult(userReportDTO, report);

        // 构建给被举报人的通知消息
        String messageContent = buildNotificationMessage(userReportDTO);

        // 创建审核记录
        ReviewRecord reviewRecord = ReviewRecord.builder()
                .reviewerId(userReportDTO.getReviewerId())
                .userId(report.getReportedUserId()) // 被举报人ID
                .reviewTime(LocalDateTime.now())
                .reviewResult(userReportDTO.getReviewResult())
                .reviewReason(userReportDTO.getReviewReason())
                .messageToAuthor(messageContent)
                .reviewType(ReviewType.USER)
                .build();
        reviewRecordMapper.add(reviewRecord);

        // 发送通知给被举报人
        sendNotification(userReportDTO, reportedUser, messageContent);

        // 构建给举报人的反馈通知
        String reporterMessage;
        if (userReportDTO.getReviewResult().equals(UserReportResultConstant.BANNED)) {
            reporterMessage = "您好，您举报的用户已被审核处理，他已经被封禁。感谢您对维护社区环境做出的贡献！";
        } else {
            reporterMessage = "您好，您举报的用户已被审核处理，暂未发现严重违规，我们会持续监督。感谢您的反馈！";
        }

        // 发送系统通知给举报人
        notificationService.sendSystemMessage(report.getReporterId(), 0L, reporterMessage);

        log.info("用户举报处理完成，举报ID：{}", userReportDTO.getId());
    }

    /**
     * 验证举报记录是否存在
     * 
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
     * 
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
     * 
     * @param userReportDTO 举报处理DTO
     * @param report        举报记录
     */
    private void updateReportStatus(UserReportDTO userReportDTO, UserReport report) {
        UserReport userReport = UserReport.builder()
                .id(userReportDTO.getId())
                .reviewerId(userReportDTO.getReviewerId())
                .reviewTime(LocalDateTime.now())
                .reviewResult(userReportDTO.getReviewResult())
                .reviewReason(userReportDTO.getReviewReason())
                .status(ReportStatusConstant.RESOLVED) // 已处理
                .build();
        userReportMapper.update(userReport);

        // 批量更新同一被举报用户的所有其他待处理/处理中举报
        userReportMapper.batchUpdateByReportedUserId(
                report.getReportedUserId(),
                userReportDTO.getReviewerId(),
                LocalDateTime.now(),
                userReportDTO.getReviewResult(),
                userReportDTO.getReviewReason(),
                ReportStatusConstant.RESOLVED);

        log.debug("举报状态更新成功，举报ID：{}", userReportDTO.getId());
    }

    /**
     * 处理举报结果（封禁用户等）
     * 
     * @param userReportDTO 举报处理DTO
     * @param report        举报记录
     */
    private void processReportResult(UserReportDTO userReportDTO, UserReport report) {
        if (userReportDTO.getReviewResult().equals(UserReportResultConstant.BANNED)) {
            // 使用UserService.setStatus统一处理状态变更和邮件通知
            userService.setStatus(report.getReportedUserId(), UserStatusConstant.BANNED,
                    userReportDTO.getReviewReason());
            log.info("用户被封禁，用户ID：{}", report.getReportedUserId());
        } else {
            log.info("用户举报审核未通过，用户状态保持不变，用户ID：{}", report.getReportedUserId());
        }
    }

    /**
     * 构建通知消息内容
     * 
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
     * 
     * @param userReportDTO  举报处理DTO
     * @param reportedUser   被举报用户
     * @param messageContent 消息内容
     */
    private void sendNotification(UserReportDTO userReportDTO, User reportedUser, String messageContent) {
        notificationService.sendReviewNotification(
                reportedUser.getId(),
                0L, // 发送者设置为系统(0L)，避免显示为个人发送
                messageContent,
                userReportDTO.getReviewerId(),
                reportedUser.getId(),
                null,
                null,
                userReportDTO.getReviewResult(),
                userReportDTO.getReviewReason(),
                ReviewType.USER);
        log.info("用户举报结果通知发送完成，用户ID：{}", reportedUser.getId());
    }

    /**
     * 更新举报状态
     * 
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
     * 
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        userReportMapper.deleteById(id);
    }

    /**
     * 批量删除举报
     * 
     * @param ids
     */
    @Override
    public void deleteByIds(Long[] ids) {
        userReportMapper.deleteByIds(ids);
    }

    /**
     * 举报用户
     * 
     * @param userAddReportDTO
     */
    @Override
    public void add(UserAddReportDTO userAddReportDTO) {
        Long userId = BaseContext.getCurrentId();
        if (userId == null)
            throw new RuntimeException(MessageConstant.USER_NOT_LOGIN);

        UserReport userReport = UserReport.builder()
                .reporterId(userId)
                .reportedUserId(userAddReportDTO.getReportedUserId())
                .reportReason(userAddReportDTO.getReportReason())
                .reportTime(LocalDateTime.now())
                .status(ReportStatusConstant.PENDING)
                .build();
        userReportMapper.add(userReport);
    }

    /**
     * 标记为处理中（联动更新同一用户的所有举报）
     * 
     * @param id         举报ID
     * @param reviewerId 审核员ID
     */
    @Override
    @Transactional
    public void markAsInProcess(Long id, Long reviewerId) {
        log.info("标记用户举报为处理中：id={}, reviewerId={}", id, reviewerId);

        // 查询举报记录
        UserReport report = userReportMapper.selectById(id);
        if (report == null) {
            throw new RuntimeException("举报记录不存在");
        }

        // 检查状态
        if (report.getStatus().equals(ReportStatusConstant.IN_PROCESS)) {
            if (!report.getReviewerId().equals(reviewerId)) {
                throw new RuntimeException("该举报正在被其他审核员处理，无法操作");
            }
            return; // 已经是自己处理中，直接返回
        }

        if (report.getStatus().equals(ReportStatusConstant.RESOLVED)) {
            throw new RuntimeException("该举报已处理完毕，无法标记");
        }

        // 批量更新同一用户的所有举报为处理中状态
        int affected = userReportMapper.updateStatusAndReviewerByReportedUserId(
                report.getReportedUserId(),
                ReportStatusConstant.IN_PROCESS,
                reviewerId);

        if (affected == 0) {
            throw new RuntimeException("认领失败，该用户的所有举报已被其他审核员抢先处理");
        }

        log.info("已将用户ID={}的所有举报标记为处理中，处理人ID={}", report.getReportedUserId(), reviewerId);
    }
}

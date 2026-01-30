package com.blog.service.impl;

import com.blog.constant.ReviewType;
import com.blog.constant.statusConstant.AppealStatusConstant;
import com.blog.constant.statusConstant.UserStatusConstant;
import com.blog.constant.resultConstant.AppealResultConstant;
import com.blog.mapper.*;
import com.blog.pojo.dto.AppealDTO;
import com.blog.pojo.dto.AppealPageQueryDTO;
import com.blog.pojo.entity.*;
import com.blog.pojo.vo.AppealVO;
import com.blog.result.PageResult;
import com.blog.service.AppealService;
import com.blog.service.EmailService;
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
 * 申诉服务实现
 * 
 * @Author worlt
 * @Date 2025/6/4 下午9:25
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AppealServiceImpl implements AppealService {

    @Autowired
    private AppealMapper appealMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ReviewRecordMapper reviewRecordMapper;

    @Autowired
    private ConversationMapper conversationMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private EmailService emailService;

    @Autowired
    private NotificationService notificationService;

    /**
     * 分页查询
     * 
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
     * 
     * @param id
     * @return
     */
    @Override
    public AppealVO getById(Long id) {
        return appealMapper.getById(id);
    }

    /**
     * 处理申诉
     * 
     * @param appealDTO 申诉处理DTO
     */
    @Override
    @Transactional
    public void handle(AppealDTO appealDTO) {
        log.info("开始处理申诉，申诉ID：{}", appealDTO.getId());

        // 验证申诉和用户存在性
        Appeal appeal = validateAppealExists(appealDTO.getId());
        User user = validateUserExists(appeal.getUserId());

        // 更新申诉状态
        updateAppealStatus(appealDTO, appeal);

        // 处理申诉结果
        String messageContent = processAppealResult(appealDTO, appeal);

        // 发送通知和邮件
        sendNotifications(appealDTO, appeal, messageContent);

        log.info("申诉处理完成，申诉ID：{}", appealDTO.getId());
    }

    /**
     * 验证申诉记录是否存在
     * 
     * @param appealId 申诉ID
     * @return 申诉记录
     */
    private Appeal validateAppealExists(Long appealId) {
        Appeal appeal = appealMapper.selectById(appealId);
        if (appeal == null) {
            log.error("申诉记录不存在，申诉ID：{}", appealId);
            throw new RuntimeException("申诉记录不存在");
        }
        return appeal;
    }

    /**
     * 验证用户是否存在
     * 
     * @param userId 用户ID
     * @return 用户记录
     */
    private User validateUserExists(Long userId) {
        User user = userMapper.getById(userId);
        if (user == null) {
            log.error("申诉用户不存在，用户ID：{}", userId);
            throw new RuntimeException("申诉用户不存在");
        }
        return user;
    }

    /**
     * 标记为处理中
     * 
     * @param id         申诉ID
     * @param reviewerId 审核员ID
     */
    @Override
    @Transactional
    public void markAsInProcess(Long id, Long reviewerId) {
        // 查询申诉记录
        Appeal appeal = appealMapper.selectById(id);
        if (appeal == null) {
            throw new RuntimeException("申诉记录不存在");
        }

        // 检查状态
        if (appeal.getStatus().equals(AppealStatusConstant.IN_PROCESS)) {
            if (appeal.getReviewerId() != null && !appeal.getReviewerId().equals(reviewerId)) {
                throw new RuntimeException("该申诉正在被其他审核员处理，无法操作");
            }
            return; // 已经是自己处理中
        }

        if (!appeal.getStatus().equals(AppealStatusConstant.PENDING)) {
            throw new RuntimeException("该申诉不是待处理状态，无法标记");
        }

        // 更新状态和审核员
        Appeal appealUpdate = Appeal.builder()
                .id(id)
                .status(AppealStatusConstant.IN_PROCESS)
                .reviewerId(reviewerId)
                .build();
        appealMapper.update(appealUpdate);
    }

    /**
     * 更新申诉状态
     * 
     * @param appealDTO 申诉处理DTO
     * @param appeal    申诉记录
     */
    private void updateAppealStatus(AppealDTO appealDTO, Appeal appeal) {
        // 权限校验
        if (appeal.getStatus().equals(AppealStatusConstant.IN_PROCESS)) {
            if (appeal.getReviewerId() != null && !appeal.getReviewerId().equals(appealDTO.getReviewerId())) {
                throw new RuntimeException("该申诉正在被其他审核员处理，您无权操作");
            }
        }

        Appeal appealUpdate = Appeal.builder()
                .id(appealDTO.getId())
                .reviewerId(appealDTO.getReviewerId())
                .reviewTime(LocalDateTime.now())
                .reviewResult(appealDTO.getReviewResult())
                .reviewReason(appealDTO.getReviewReason())
                .status(AppealStatusConstant.RESOLVED) // 已处理
                .build();
        appealMapper.update(appealUpdate);
    }

    /**
     * 处理申诉结果（解封或维持）
     * 
     * @param appealDTO 申诉处理DTO
     * @param appeal    申诉记录
     * @return 消息内容
     */
    private String processAppealResult(AppealDTO appealDTO, Appeal appeal) {
        String messageContent;

        if (appealDTO.getReviewResult().equals(AppealResultConstant.ACCEPT)) {
            // 申诉通过，解封用户
            String reason = appealDTO.getReviewReason() != null ? appealDTO.getReviewReason() : "无";
            messageContent = "您的申诉已通过审核，账号已解封。原因：" + reason;
            userMapper.updateStatus(appeal.getUserId(), UserStatusConstant.NORMAL);
            log.info("用户解封成功，用户ID：{}", appeal.getUserId());
        } else {
            // 申诉未通过
            messageContent = "您的申诉审核未通过。原因：" + appealDTO.getReviewReason();
            log.info("用户申诉被拒绝，用户ID：{}", appeal.getUserId());
        }

        return messageContent;
    }

    /**
     * 发送通知和邮件
     * 
     * @param appealDTO      申诉处理DTO
     * @param appeal         申诉记录
     * @param messageContent 消息内容
     */
    private void sendNotifications(AppealDTO appealDTO, Appeal appeal, String messageContent) {
        // 发送系统消息和创建审核记录
        notificationService.sendReviewNotification(
                appeal.getUserId(),
                appealDTO.getReviewerId(),
                messageContent,
                appealDTO.getReviewerId(),
                appeal.getUserId(),
                null,
                null,
                appealDTO.getReviewResult(),
                appealDTO.getReviewReason(),
                ReviewType.APPEAL);

        // 发送邮件通知
        sendEmailNotification(appeal, messageContent, appealDTO.getReviewResult(), appealDTO.getReviewReason());
    }

    /**
     * 发送邮件通知
     * 
     * @param appeal         申诉记录
     * @param messageContent 消息内容
     * @param reviewResult   审核结果
     * @param reviewReason   审核原因
     */
    private void sendEmailNotification(Appeal appeal, String messageContent, Integer reviewResult,
            String reviewReason) {
        String email = appeal.getEmail();
        if (email != null && !email.isEmpty()) {
            String subject = "申诉结果通知";
            emailService.sendAppealResultEmail(
                    email,
                    subject,
                    messageContent,
                    reviewResult.equals(AppealResultConstant.ACCEPT),
                    reviewReason);

            log.info("申诉结果邮件发送请求已提交，邮箱：{}", email);
        } else {
            log.debug("申诉记录无邮箱信息，跳过邮件发送，申诉ID：{}", appeal.getId());
        }
    }

    /**
     * 更新申诉状态
     * 
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
     * 
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        appealMapper.deleteById(id);
    }

    /**
     * 批量删除申诉
     * 
     * @param ids
     */
    @Override
    public void deleteByIds(Long[] ids) {
        appealMapper.deleteByIds(ids);
    }

    /**
     * 提交申诉
     * 
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
package com.blog.service.impl;

import com.blog.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * 邮件服务实现类
 *
 * @Author worlt
 * @Date 2025/6/4 下午20:05
 */
@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    /**
     * 发送验证码邮件
     *
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @param code 验证码
     * @return 是否发送成功
     */
    @Override
    public boolean sendVerificationCode(String to, String subject, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            
            // 邮件内容
            String content = "<div style='padding:20px;background-color:#f7f7f7;'>"
                    + "<h2 style='color:#333;'>验证码</h2>"
                    + "<p style='font-size:16px;'>您好，您的验证码为：</p>"
                    + "<div style='padding:10px 20px;background-color:#fff;border-left:4px solid #3498db;margin:20px 0;'>"
                    + "<p style='font-size:24px;font-weight:bold;color:#3498db;'>" + code + "</p>"
                    + "</div>"
                    + "<p style='font-size:14px;color:#666;'>验证码10分钟内有效，请勿泄露给他人。</p>"
                    + "<p style='font-size:14px;color:#666;'>如非本人操作，请忽略此邮件。</p>"
                    + "</div>";
            
            helper.setText(content, true);
            mailSender.send(message);
            log.info("邮件发送成功: {}", to);
            return true;
        } catch (MessagingException e) {
            log.error("邮件发送失败: {}, 原因: {}", to, e.getMessage());
            return false;
        }
    }
    
    /**
     * 发送申诉结果邮件
     *
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件内容
     * @param isAccepted 是否通过申诉
     * @param reason 审核理由
     * @return 是否发送成功
     */
    @Override
    public boolean sendAppealResultEmail(String to, String subject, String content, boolean isAccepted, String reason) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            
            // 根据申诉结果选择不同的样式
            String borderColor = isAccepted ? "#27ae60" : "#e74c3c";
            String statusText = isAccepted ? "已通过" : "未通过";
            String statusColor = isAccepted ? "#27ae60" : "#e74c3c";
            
            // 邮件内容
            String htmlContent = "<div style='padding:20px;background-color:#f7f7f7;'>"
                    + "<h2 style='color:#333;'>申诉结果通知</h2>"
                    + "<p style='font-size:16px;'>您好，您的账号申诉结果如下：</p>"
                    + "<div style='padding:15px 20px;background-color:#fff;border-left:4px solid " + borderColor + ";margin:20px 0;'>"
                    + "<p style='font-size:18px;margin-bottom:10px;'>申诉状态：<span style='color:" + statusColor + ";font-weight:bold;'>" + statusText + "</span></p>"
                    + "<p style='font-size:16px;margin-bottom:10px;'>处理结果：" + content + "</p>"
                    + "<p style='font-size:14px;color:#666;'>审核理由：" + reason + "</p>"
                    + "</div>"
                    + "<p style='font-size:14px;color:#666;'>如有任何疑问，请联系我们的客服团队。</p>"
                    + "</div>";
            
            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("申诉结果邮件发送成功: {}", to);
            return true;
        } catch (MessagingException e) {
            log.error("申诉结果邮件发送失败: {}, 原因: {}", to, e.getMessage());
            return false;
        }
    }
} 
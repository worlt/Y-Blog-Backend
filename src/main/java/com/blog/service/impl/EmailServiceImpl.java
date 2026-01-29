package com.blog.service.impl;

import com.blog.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;

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
     * @param to      收件人邮箱
     * @param subject 邮件主题
     * @param code    验证码
     */
    @Async
    @Override
    public void sendVerificationCode(String to, String subject, String code) {
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
            log.info("验证码邮件发送成功，收件人：{}", to);
        } catch (MessagingException e) {
            log.error("验证码邮件发送失败，收件人：{}，错误信息：{}", to, e.getMessage());
        }
    }

    /**
     * 发送申诉结果邮件
     *
     * @param to         收件人邮箱
     * @param subject    邮件主题
     * @param content    邮件内容
     * @param isAccepted 是否通过申诉
     * @param reason     审核理由
     */
    @Async
    @Override
    public void sendAppealResultEmail(String to, String subject, String content, boolean isAccepted, String reason) {
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
                    + "<div style='padding:15px 20px;background-color:#fff;border-left:4px solid " + borderColor
                    + ";margin:20px 0;'>"
                    + "<p style='font-size:18px;margin-bottom:10px;'>申诉状态：<span style='color:" + statusColor
                    + ";font-weight:bold;'>" + statusText + "</span></p>"
                    + "<p style='font-size:16px;margin-bottom:10px;'>处理结果：" + content + "</p>"
                    + "<p style='font-size:14px;color:#666;'>审核理由：" + reason + "</p>"
                    + "</div>"
                    + "<p style='font-size:14px;color:#666;'>如有任何疑问，请联系我们的客服团队。</p>"
                    + "</div>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("申诉结果邮件发送成功，收件人：{}", to);
        } catch (MessagingException e) {
            log.error("申诉结果邮件发送失败，收件人：{}，错误信息：{}", to, e.getMessage());
        }
    }

    /**
     * 发送封禁/解禁通知邮件
     *
     * @param to       收件人邮箱
     * @param username 用户名
     * @param isBanned true=封禁，false=解禁
     * @param reason   封禁/解禁原因
     */
    @Async
    @Override
    public void sendBanNotificationEmail(String to, String username, boolean isBanned, String reason) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);

            // 根据封禁/解禁状态设置邮件主题和样式
            String subject = isBanned ? "Y-Blog账号封禁通知" : "Y-Blog账号解禁通知";
            helper.setSubject(subject);

            String borderColor = isBanned ? "#e74c3c" : "#27ae60";
            String statusText = isBanned ? "已被封禁" : "已解禁";
            String statusColor = isBanned ? "#e74c3c" : "#27ae60";
            String icon = isBanned ? "⚠️" : "✅";

            // 构建邮件内容
            StringBuilder htmlContent = new StringBuilder();
            htmlContent.append("<div style='padding:20px;background-color:#f7f7f7;font-family:Arial,sans-serif;'>");
            htmlContent.append("<h2 style='color:#333;'>").append(icon).append(" 账号状态变更通知</h2>");
            htmlContent.append("<p style='font-size:16px;'>尊敬的用户 <strong>").append(username).append("</strong>：</p>");
            htmlContent.append("<div style='padding:15px 20px;background-color:#fff;border-left:4px solid ")
                    .append(borderColor).append(";margin:20px 0;'>");
            htmlContent.append("<p style='font-size:18px;margin-bottom:10px;'>账号状态：<span style='color:")
                    .append(statusColor).append(";font-weight:bold;'>").append(statusText).append("</span></p>");
            htmlContent.append("<p style='font-size:14px;color:#666;'>原因：")
                    .append(reason != null && !reason.isEmpty() ? reason : "未提供").append("</p>");
            htmlContent.append("</div>");

            if (isBanned) {
                // 封禁时显示申诉信息
                htmlContent
                        .append("<div style='padding:15px;background-color:#fff3cd;border-radius:5px;margin:20px 0;'>");
                htmlContent.append("<p style='font-size:14px;color:#856404;margin:0;'><strong>如何申诉？</strong></p>");
                htmlContent.append(
                        "<p style='font-size:14px;color:#856404;margin:10px 0 0 0;'>如果您认为这是一个错误，可以通过以下方式提交申诉：</p>");
                htmlContent.append("<ol style='font-size:14px;color:#856404;margin:10px 0;'>");
                htmlContent.append("<li>访问Y-Blog网站登录页面</li>");
                htmlContent.append("<li>尝试登录后系统会引导您进入申诉流程</li>");
                htmlContent.append("<li>填写申诉理由并提交，我们会尽快处理</li>");
                htmlContent.append("</ol>");
                htmlContent.append("</div>");
            } else {
                // 解禁时显示欢迎回来信息
                htmlContent.append("<p style='font-size:14px;color:#666;'>欢迎回来！您现在可以正常使用Y-Blog的所有功能。</p>");
            }

            htmlContent.append("<p style='font-size:14px;color:#999;margin-top:30px;'>此邮件由系统自动发送，请勿回复。</p>");
            htmlContent.append("<p style='font-size:12px;color:#999;'>—— Y-Blog团队</p>");
            htmlContent.append("</div>");

            helper.setText(htmlContent.toString(), true);
            mailSender.send(message);
            log.info("{}通知邮件发送成功，收件人：{}", isBanned ? "封禁" : "解禁", to);
        } catch (MessagingException e) {
            log.error("{}通知邮件发送失败，收件人：{}，错误信息：{}", isBanned ? "封禁" : "解禁", to, e.getMessage());
        }
    }
}
package com.blog.service;

/**
 * 邮件发送服务
 * 
 * @Author worlt
 * @Date 2025/6/4 下午20:00
 */
public interface EmailService {

    /**
     * 发送验证码邮件
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @param code 验证码
     * @return 是否发送成功
     */
    boolean sendVerificationCode(String to, String subject, String code);
} 
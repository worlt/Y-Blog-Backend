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
     * 
     * @param to      收件人邮箱
     * @param subject 邮件主题
     * @param code    验证码
     *                /**
     *                发送验证码邮件
     * 
     * @param to      收件人邮箱
     * @param subject 邮件主题
     * @param code    验证码
     */
    void sendVerificationCode(String to, String subject, String code);

    /**
     * 发送申诉结果邮件
     * 
     * @param to         收件人邮箱
     * @param subject    邮件主题
     * @param content    邮件内容
     * @param isAccepted 是否通过申诉
     * @param reason     审核理由
     *                   /**
     *                   发送申诉结果邮件
     * 
     * @param to         收件人邮箱
     * @param subject    邮件主题
     * @param content    邮件内容
     * @param isAccepted 是否通过申诉
     * @param reason     审核理由
     */
    void sendAppealResultEmail(String to, String subject, String content, boolean isAccepted, String reason);

    /**
     * 发送封禁/解禁通知邮件
     * 
     * @param to       收件人邮箱
     * @param username 用户名
     * @param isBanned true=封禁，false=解禁
     * @param reason   封禁/解禁原因
     *                 /**
     *                 发送封禁/解禁通知邮件
     * 
     * @param to       收件人邮箱
     * @param username 用户名
     * @param isBanned true=封禁，false=解禁
     * @param reason   封禁/解禁原因
     */
    void sendBanNotificationEmail(String to, String username, boolean isBanned, String reason);
}
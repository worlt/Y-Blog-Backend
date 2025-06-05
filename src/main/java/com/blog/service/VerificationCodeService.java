package com.blog.service;

/**
 * 验证码服务
 *
 * @Author worlt
 * @Date 2025/6/4 下午20:10
 */
public interface VerificationCodeService {

    /**
     * 生成验证码并发送邮件
     * @param email 邮箱
     * @return 是否成功
     */
    boolean sendVerificationCode(String email);

    /**
     * 验证验证码
     * @param email 邮箱
     * @param code 验证码
     * @return 是否通过验证
     */
    boolean verifyCode(String email, String code);
} 
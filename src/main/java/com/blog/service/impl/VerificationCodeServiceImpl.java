package com.blog.service.impl;

import com.blog.constant.statusConstant.EmailVerificationStatusConstant;
import com.blog.mapper.EmailVerificationMapper;
import com.blog.pojo.entity.EmailVerification;
import com.blog.service.EmailService;
import com.blog.service.VerificationCodeService;
import com.blog.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务实现类
 * 
 * @Author worlt
 * @Date 2025/6/4 下午20:15
 */
@Service
@Slf4j
public class VerificationCodeServiceImpl implements VerificationCodeService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailVerificationMapper emailVerificationMapper;

    // Redis键前缀
    private static final String CODE_KEY_PREFIX = "email:code:";
    // 验证码有效期（分钟）- “默认有效期为5分钟”
    private static final int CODE_EXPIRE_MINUTES = 5;
    // 发送频率限制（秒）
    private static final int SEND_INTERVAL_SECONDS = 60;

    /**
     * 生成验证码并发送邮件
     * 
     * @param email 邮箱
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean sendVerificationCode(String email) {
        // 快速检查Redis可用性
        boolean redisAvailable = isRedisAvailableQuickly();

        // 检查发送频率限制 - 只在Redis可用时进行
        String limitKey = CODE_KEY_PREFIX + "limit:" + email;

        if (redisAvailable) {
            // 使用RedisUtil进行安全的操作
            if (redisUtil.hasKey(limitKey)) {
                log.warn("邮箱[{}]发送验证码过于频繁", email);
                return false;
            }

            // 设置发送频率限制
            if (!redisUtil.set(limitKey, "1", SEND_INTERVAL_SECONDS, TimeUnit.SECONDS)) {
                log.warn("Redis频率限制设置失败，但继续发送验证码");
            }
        } else {
            log.info("Redis不可用，跳过频率限制检查，直接发送验证码");
        }

        // 生成6位随机验证码
        String code = generateRandomCode(6);
        String key = CODE_KEY_PREFIX + email;

        // 发送邮件(异步)
        emailService.sendVerificationCode(email, "Y-Blog验证码", code);
        log.info("验证码邮件发送请求已提交: {}", email);

        // 如果Redis可用，尝试存储到Redis
        if (redisAvailable) {
            if (!redisUtil.set(key, code, CODE_EXPIRE_MINUTES, TimeUnit.MINUTES)) {
                log.warn("Redis存储验证码失败，已存储到数据库");
            }
        }

        // 异步存储到数据库作为容灾备份（降低DB写入压力，Redis为主力核验层）
        CompletableFuture.runAsync(() -> {
            try {
                storeInDatabase(email, code);
            } catch (Exception e) {
                log.error("异步存储验证码到数据库失败: {}", e.getMessage());
            }
        });

        return true;
    }

    /**
     * 验证验证码
     * 
     * @param email 邮箱
     * @param code  验证码
     * @return 是否通过验证
     */
    @Override
    @Transactional
    public boolean verifyCode(String email, String code) {
        if (email == null || code == null) {
            return false;
        }

        String key = CODE_KEY_PREFIX + email;
        boolean verified = false;

        // 快速检查Redis可用性
        boolean redisAvailable = isRedisAvailableQuickly();

        // 如果Redis可用，首先尝试从Redis验证
        if (redisAvailable) {
            String storedCode = (String) redisUtil.get(key);
            if (code.equals(storedCode)) {
                verified = true;
                // 验证成功后删除Redis缓存
                if (!redisUtil.delete(key)) {
                    log.warn("Redis删除验证码失败，但验证成功");
                }
                // 关键修复：异步回写数据库is_used状态，切断重放攻击路径
                CompletableFuture.runAsync(() -> {
                    try {
                        markCodeAsUsed(email, code);
                    } catch (Exception e) {
                        log.error("异步回写is_used失败: {}", e.getMessage());
                    }
                });
            }
        } else {
            log.info("Redis不可用，直接使用数据库验证");
        }

        // 如果Redis不可用或者Redis中没有找到验证码，从数据库验证
        if (!verified) {
            verified = verifyFromDatabase(email, code);
        }

        return verified;
    }

    /**
     * 生成随机验证码
     * 
     * @param length 长度
     * @return 验证码
     */
    private String generateRandomCode(int length) {
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    /**
     * 存储验证码到数据库
     * 
     * @param email 邮箱
     * @param code  验证码
     * @return 是否成功
     */
    private boolean storeInDatabase(String email, String code) {
        try {
            // 先检查是否已有未过期的验证码
            EmailVerification existingCode = emailVerificationMapper.findValidCodeByEmail(email);
            if (existingCode != null) {
                // 更新已有记录
                existingCode.setVerificationCode(code);
                existingCode.setExpirationTime(LocalDateTime.now().plusMinutes(CODE_EXPIRE_MINUTES));
                existingCode.setCreateTime(LocalDateTime.now());
                existingCode.setIsUsed(EmailVerificationStatusConstant.UNUSED);
                emailVerificationMapper.update(existingCode);
                log.info("更新验证码记录：{}", email);
            } else {
                // 创建新记录
                EmailVerification emailVerification = EmailVerification.builder()
                        .email(email)
                        .verificationCode(code)
                        .expirationTime(LocalDateTime.now().plusMinutes(CODE_EXPIRE_MINUTES))
                        .createTime(LocalDateTime.now())
                        .isUsed(EmailVerificationStatusConstant.UNUSED)
                        .build();
                emailVerificationMapper.insert(emailVerification);
                log.info("创建新验证码记录：{}", email);
            }
            return true;
        } catch (DataAccessException e) {
            log.error("数据库存储验证码失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从数据库验证验证码
     * 
     * @param email 邮箱
     * @param code  验证码
     * @return 是否验证通过
     */
    private boolean verifyFromDatabase(String email, String code) {
        try {
            EmailVerification verification = emailVerificationMapper.findValidCodeByEmail(email);
            if (verification != null
                    && code.equals(verification.getVerificationCode())
                    && verification.getExpirationTime().isAfter(LocalDateTime.now())
                    && verification.getIsUsed().equals(EmailVerificationStatusConstant.UNUSED)) {

                // 标记为已使用，确保更新成功
                verification.setIsUsed(EmailVerificationStatusConstant.USED);
                emailVerificationMapper.update(verification);

                // 记录验证信息，用于调试
                log.info("数据库验证码验证成功，已将ID为{}的验证码标记为已使用", verification.getId());

                return true;
            }
            return false;
        } catch (DataAccessException e) {
            log.error("数据库验证码验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 快速检查Redis是否可用
     */
    private boolean isRedisAvailableQuickly() {
        return redisUtil.isRedisAvailable();
    }

    /**
     * 标记验证码为已使用（切断重放攻击路径）
     * 
     * @param email 邮箱
     * @param code  验证码
     */
    private void markCodeAsUsed(String email, String code) {
        try {
            EmailVerification verification = emailVerificationMapper.findValidCodeByEmail(email);
            if (verification != null && code.equals(verification.getVerificationCode())) {
                verification.setIsUsed(EmailVerificationStatusConstant.USED);
                emailVerificationMapper.update(verification);
                log.info("已将邮箱[{}]的验证码标记为已使用，防止重放攻击", email);
            }
        } catch (DataAccessException e) {
            log.error("标记验证码为已使用失败: {}", e.getMessage());
        }
    }
}
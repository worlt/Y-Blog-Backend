package com.blog.mapper;

import com.blog.pojo.entity.EmailVerification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 邮箱验证码Mapper接口
 *
 * @Author worlt
 * @Date 2025/6/4 下午20:30
 */
@Mapper
public interface EmailVerificationMapper {

    /**
     * 根据邮箱查询有效的验证码
     * @param email 邮箱
     * @return 验证码实体
     */
    EmailVerification findValidCodeByEmail(@Param("email") String email);

    /**
     * 插入验证码记录
     * @param emailVerification 验证码实体
     */
    void insert(EmailVerification emailVerification);

    /**
     * 更新验证码记录
     * @param emailVerification 验证码实体
     */
    void update(EmailVerification emailVerification);
} 
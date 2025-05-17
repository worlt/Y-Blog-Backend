package com.blog.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 邮箱验证码表
 *
 * @Author worlt
 * @Date 2025/5/16 下午9:49
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerification implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private String email;

    private String verificationCode;

    // 过期时间
    private LocalDateTime expirationTime;

    // 创建时间
    private LocalDateTime createTime;

    // 是否已使用
    private Integer isUsed;


}

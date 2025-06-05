package com.blog.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户表
 *
 * @Author worlt
 * @Date 2025/5/16 下午5:00
 */
@Data
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String password;

    private String phone;

    // 邮箱
    private String email;

    // 小程序 openid
    private String openid;

    // 小程序 unionid
    private String unionid;

    private Integer gender;

    // 头像
    private String avatar;

    // 创建时间
    private LocalDateTime createTime;

    // 更新时间
    private LocalDateTime updateTime;

    private Integer status;

    // 用户角色（2-普通用户，3-VIP用户）
    private Integer role;

    // 粉丝数
    private Long fans;

}

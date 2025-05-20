package com.blog.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Author worlt
 * @Date 2025/5/20 下午10:20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String password;

    private String phone;

    // 小程序 openid
    private String openid;

    // 小程序 unionid
    private String unionid;

    private Integer gender;

    // 头像
    private String avatar;

    private Integer status;

    // 用户角色（2-普通用户，3-VIP用户）
    private Integer role;

    // 粉丝数
    private Long fans;

}

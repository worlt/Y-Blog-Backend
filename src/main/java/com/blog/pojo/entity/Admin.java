package com.blog.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 管理员表
 *
 * @Author worlt
 * @Date 2025/5/16 下午4:53
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Admin implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String username;

    private String password;

    private String email;

    private Integer gender;

    private String avatar;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer status;

    // 角色（0-管理员，1-审核员）
    private Integer role;
}

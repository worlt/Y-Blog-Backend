package com.blog.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author worlt
 * @Date 2025/6/2 下午1:50
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditorVO implements Serializable {

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

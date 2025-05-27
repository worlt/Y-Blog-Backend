package com.blog.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author worlt
 * @Date 2025/5/20 下午7:39
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminVO implements Serializable {

    private Long id;

    private String name;

    private String username;

    private String password;

    private String email;

    private String avatar;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer status;

    private Integer role;
}

package com.blog.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Author worlt
 * @Date 2025/5/18 下午9:42
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminDTO {

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

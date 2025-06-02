package com.blog.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author worlt
 * @Date 2025/5/18 下午9:42
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String username;

    private String password;

    private Integer gender;

    private String email;

    private String avatar;

    private Integer status;

    private Integer role;

}

package com.blog.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 分类表
 *
 * @Author worlt
 * @Date 2025/5/16 下午5:17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Category implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String description;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}

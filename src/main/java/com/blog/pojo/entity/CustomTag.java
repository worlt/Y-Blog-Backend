package com.blog.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 自定义标签：存储用户自定义标签
 *
 * @Author worlt
 * @Date 2025/5/16 下午5:27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomTag implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long userId;

    private String name;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}

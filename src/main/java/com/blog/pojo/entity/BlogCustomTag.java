package com.blog.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 博客自定义标签关联表：关联博客文章和用户自定义标签
 *
 * @Author worlt
 * @Date 2025/5/16 下午5:29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogCustomTag implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long blogId;

    private Long customTagId;

    private LocalDateTime createTime;

}

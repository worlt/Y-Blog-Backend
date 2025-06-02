package com.blog.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 博客表
 *
 * @Author worlt
 * @Date 2025/5/16 下午5:04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Blog implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String title;

    private String content;

    // 创建时间
    private LocalDateTime createTime;

    // 发布时间
    private LocalDateTime publishTime;

    // 更新时间
    private LocalDateTime updateTime;

    // 浏览量
    private Long views;

    // 点赞量
    private Long likes;

    // 点踩量
    private Long dislikes;

    // 收藏量
    private Long collections;

    // 分享量
    private Long shares;

    private Long userId;

    private Long categoryId;

    private Integer status;

}

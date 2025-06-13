package com.blog.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author worlt
 * @Date 2025/5/28 下午9:16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlogUpdateDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String title;

    private String content;

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

    private List<Long> tagIdList;

    private List<String> customTagList;

    private Integer status;
    
    // 是否置顶（0-不置顶、1-置顶）
    private Integer isTop;
    
    // 博客封面
    private String cover;
}

package com.blog.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author worlt
 * @Date 2025/5/21 下午4:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlogAddDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String title;

    private String content;

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

    private List<Integer> tagIdList;

    private List<String> customTagList;

    private Integer status;
}

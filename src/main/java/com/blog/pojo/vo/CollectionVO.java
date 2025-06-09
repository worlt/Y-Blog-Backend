package com.blog.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 收藏博客VO
 *
 * @Author worlt
 * @Date 2025/5/16 下午6:20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id; // 博客id
    
    private String title; // 博客标题
    
    private Long userId; // 作者id
    
    private String username; // 作者名称
    
    private String avatar; // 作者头像
    
    private String categoryName; // 分类名称
    
    private Long views; // 浏览量
    
    private Long likes; // 点赞数
    
    private Long dislikes; // 点踩数
    
    private Long collections; // 收藏数
    
    private LocalDateTime collectTime; // 收藏时间
} 
package com.blog.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 浏览记录实体类
 * 用于记录用户对博客的浏览历史
 * 
 * @Author worlt
 * @Date 2025/6/9 下午9:23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class View {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 博客ID
     */
    private Long blogId;
    
    /**
     * 浏览时间
     */
    private LocalDateTime viewTime;
} 
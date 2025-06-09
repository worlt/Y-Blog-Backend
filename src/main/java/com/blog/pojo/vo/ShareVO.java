package com.blog.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 分享VO
 *
 * @Author worlt
 * @Date 2025/6/15 下午4:35
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShareVO implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Long id;
    
    private Long userId;
    
    private String username;
    
    private String avatar;
    
    private Long blogId;
    
    private String blogTitle;
    
    private LocalDateTime shareTime;
    
    private String sharePlatform;
} 
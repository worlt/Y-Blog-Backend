package com.blog.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户关注VO
 *
 * @Author worlt
 * @Date 2025/5/16 下午6:25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFollowVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId; // 用户id
    
    private String username; // 用户名
    
    private String avatar; // 头像
    
    private Integer gender; // 性别
    
    private Long fans; // 粉丝数
    
    private LocalDateTime followTime; // 关注时间
    
    private Boolean isFollowed; // 是否已关注（当前登录用户是否关注了该用户）
} 
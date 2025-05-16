package com.blog.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * 关注表
 *
 * @Author worlt
 * @Date 2025/5/16 下午10:24
 */
@Data
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserFollow {

    private Long id;

    // 关注者id
    private Long userId;

    // 被关注者id
    private Long followUserId;

    // 关注时间
    private LocalDateTime followTime;
}

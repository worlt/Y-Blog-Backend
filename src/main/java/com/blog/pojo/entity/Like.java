package com.blog.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 点赞表
 *
 * @Author worlt
 * @Date 2025/5/16 下午6:13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Like implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long userId;

    private Long blogId;

    // 点赞评论的id
    private Long commentId;

    private LocalDateTime likeTime;

}

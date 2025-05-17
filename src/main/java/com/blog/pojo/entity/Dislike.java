package com.blog.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 点踩表
 *
 * @Author worlt
 * @Date 2025/5/16 下午6:15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dislike implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long userId;

    private Long blogId;

    // 点踩评论的id
    private Long commentId;

    private LocalDateTime dislikeTime;

}

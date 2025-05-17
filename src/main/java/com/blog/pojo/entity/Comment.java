package com.blog.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 评论表
 *
 * @Author worlt
 * @Date 2025/5/16 下午5:32
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private String content;

    private Long userId;

    private Long blogId;

    // 父评论ID（用于评论回复）
    private Long parentId;

    private Long likes;

    private Long dislikes;

    private Integer status;

}

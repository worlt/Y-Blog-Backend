package com.blog.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Author worlt
 * @Date 2025/5/30 上午9:05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentVO {

    private Long id;

    private String content;

    private LocalDateTime createTime;

    private Long userId;

    private String username;

    private String userAvatar;

    private Long blogId;

    private Long parentId;

    private Long likes;

    private Long dislikes;

    private Integer status;

}

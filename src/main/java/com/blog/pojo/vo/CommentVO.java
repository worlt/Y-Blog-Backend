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

    public Long id;

    public String comment;

    public LocalDateTime createTime;

    public Long userId;

    public String username;

    public String userAvatar;

    public Long blogId;

    public Long parentId;

    public Long likes;

    public Long dislikes;

    public Integer status;

}

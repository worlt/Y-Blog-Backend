package com.blog.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 博客标签关联表
 *
 * @Author worlt
 * @Date 2025/5/16 下午5:22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogTag {

    private Long id;

    private Long blogId;

    private Long tagId;

    private LocalDateTime createTime;

}

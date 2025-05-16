package com.blog.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 文章举报
 *
 * @Author worlt
 * @Date 2025/5/16 下午6:30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleReport {

    private Long id;

    // 举报用户
    private Long userId;

    // 被举报博客
    private Long blogId;

    private LocalDateTime reportTime;

    // 举报原因
    private String reportReason;

    private Integer status;

    // 审核员ID
    private Long reviewerId;

    // 审核时间
    private LocalDateTime reviewTime;

    // 审核结果
    private Integer reviewResult;

}

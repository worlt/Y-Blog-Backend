package com.blog.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评论举报实体
 * 
 * @Author worlt
 * @Date 2025/6/4 下午5:55
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentReport implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 举报唯一标识符
     */
    private Long id;

    /**
     * 举报用户ID（举报人）
     */
    private Long reporterId;

    /**
     * 被举报评论ID
     */
    private Long commentId;

    /**
     * 被举报人ID
     */
    private Long reportedUserId;

    /**
     * 举报时间
     */
    private LocalDateTime reportTime;

    /**
     * 举报原因
     */
    private String reportReason;

    /**
     * 举报状态（0-待审核、1-处理中、2-已处理）
     */
    private Integer status;

    /**
     * 审核员ID
     */
    private Long reviewerId;

    /**
     * 审核时间
     */
    private LocalDateTime reviewTime;

    /**
     * 审核结果（0-正常、1-删除）
     */
    private Integer reviewResult;

    /**
     * 审核原因
     */
    private String reviewReason;
}
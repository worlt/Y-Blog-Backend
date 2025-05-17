package com.blog.pojo.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户举报表
 *
 * @Author worlt
 * @Date 2025/5/16 下午6:42
 */
public class UserReport implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    // 举报用户
    private Long userId;

    // 被举报用户
    private Long reportedUserId;

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

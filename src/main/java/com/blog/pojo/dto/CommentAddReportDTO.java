package com.blog.pojo.dto;

import lombok.Data;

/**
 * 用户举报评论DTO
 * @Author worlt
 * @Date 2025/6/3 下午5:30
 */
@Data
public class CommentAddReportDTO {

    private Long userId;

    private Long commentId;

    private String reportReason;

}
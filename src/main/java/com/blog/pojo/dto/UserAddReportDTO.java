package com.blog.pojo.dto;

import lombok.Data;

/**
 * 用户举报用户DTO
 * 
 * @Author worlt
 * @Date 2025/6/3 下午5:30
 */
@Data
public class UserAddReportDTO {

    private Long reportedUserId;

    private String reportReason;

}
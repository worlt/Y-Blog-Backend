package com.blog.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户举报博客DTO
 * 
 * @Author worlt
 * @Date 2025/6/3 下午5:30
 */
@Data
public class BlogAddReportDTO {

    private Long blogId;

    private String reportReason;

}
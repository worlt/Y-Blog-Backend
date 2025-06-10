package com.blog.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 处理博客举报DTO
 * @Author worlt
 * @Date 2025/6/3 下午5:30
 */
@Data
@ApiModel(description = "博客举报处理数据传输对象")
public class BlogReportDTO {

    @ApiModelProperty("举报ID")
    private Long id;

    @ApiModelProperty("审核员ID")
    private Long reviewerId;

    @ApiModelProperty("举报状态（0-待审核、1-处理中、2-已处理）")
    private Integer status;

    @ApiModelProperty("审核结果（0-正常、1-整改、2-永久封禁）")
    private Integer reviewResult;
    
    @ApiModelProperty("审核原因")
    private String reviewReason;
} 
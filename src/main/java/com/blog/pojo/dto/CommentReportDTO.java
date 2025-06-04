package com.blog.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 评论举报DTO
 * @Author worlt
 * @Date 2025/6/4 下午5:40
 */
@Data
@ApiModel(description = "评论举报处理数据传输对象")
public class CommentReportDTO {

    @ApiModelProperty("举报ID")
    private Long id;

    @ApiModelProperty("审核员ID")
    private Long reviewerId;

    @ApiModelProperty("举报状态（0-待审核、1-处理中、2-已处理）")
    private Integer status;

    @ApiModelProperty("审核结果（0-正常、1-删除）")
    private Integer reviewResult;
    
    @ApiModelProperty("审核原因")
    private String reviewReason;
} 
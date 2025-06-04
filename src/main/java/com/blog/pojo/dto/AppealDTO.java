package com.blog.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 申诉DTO
 * @Author worlt
 * @Date 2025/6/4 下午9:10
 */
@Data
@ApiModel(description = "申诉处理数据传输对象")
public class AppealDTO {

    @ApiModelProperty("申诉ID")
    private Long id;

    @ApiModelProperty("审核员ID")
    private Long reviewerId;

    @ApiModelProperty("申诉状态（0-待审核、1-处理中、2-已处理）")
    private Integer status;

    @ApiModelProperty("审核结果（0-接受、1-拒绝）")
    private Integer reviewResult;
    
    @ApiModelProperty("审核原因")
    private String reviewReason;
} 
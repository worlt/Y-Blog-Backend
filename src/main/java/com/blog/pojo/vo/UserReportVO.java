package com.blog.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户举报VO
 * @Author worlt
 * @Date 2025/6/4 下午2:20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "用户举报视图对象")
public class UserReportVO {

    @ApiModelProperty("举报ID")
    private Long id;

    @ApiModelProperty("举报用户ID")
    private Long userId;
    
    @ApiModelProperty("举报用户名")
    private String username;
    
    @ApiModelProperty("被举报用户ID")
    private Long reportedUserId;
    
    @ApiModelProperty("被举报用户名")
    private String reportedUsername;
    
    @ApiModelProperty("举报时间")
    private LocalDateTime reportTime;
    
    @ApiModelProperty("举报原因")
    private String reportReason;
    
    @ApiModelProperty("0-待审核、1-处理中、2-已处理）")
    private Integer status;
    
    @ApiModelProperty("审核员ID")
    private Long reviewerId;
    
    @ApiModelProperty("审核员名称")
    private String reviewerName;
    
    @ApiModelProperty("审核时间")
    private LocalDateTime reviewTime;
    
    @ApiModelProperty("审核结果（0-正常、1-封禁）")
    private Integer reviewResult;
    
    @ApiModelProperty("审核原因")
    private String reviewReason;
} 
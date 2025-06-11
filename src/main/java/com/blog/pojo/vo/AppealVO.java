package com.blog.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 申诉VO
 * @Author worlt
 * @Date 2025/6/4 下午9:15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "申诉视图对象")
public class AppealVO {

    @ApiModelProperty("申诉ID")
    private Long id;

    @ApiModelProperty("申诉用户ID")
    private Long userId;
    
    @ApiModelProperty("申诉用户名")
    private String username;
    
    @ApiModelProperty("申诉时间")
    private LocalDateTime appealTime;
    
    @ApiModelProperty("申诉原因")
    private String appealReason;
    
    @ApiModelProperty("申诉状态（0-待审核、1-处理中、2-已处理）")
    private Integer status;
    
    @ApiModelProperty("审核员ID")
    private Long reviewerId;
    
    @ApiModelProperty("审核员名称")
    private String reviewerName;
    
    @ApiModelProperty("审核时间")
    private LocalDateTime reviewTime;
    
    @ApiModelProperty("审核结果（0-接受、1-拒绝）")
    private Integer reviewResult;
    
    @ApiModelProperty("审核原因")
    private String reviewReason;
    
    @ApiModelProperty("申诉邮箱")
    private String email;
} 
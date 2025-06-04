package com.blog.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 审核记录VO
 * @Author worlt
 * @Date 2025/6/4 下午9:45
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "审核记录视图对象")
public class ReviewRecordVO {
    
    @ApiModelProperty("审核记录ID")
    private Long id;
    
    @ApiModelProperty("审核员ID")
    private Long reviewerId;
    
    @ApiModelProperty("审核员姓名")
    private String reviewerName;
    
    @ApiModelProperty("审核博客ID")
    private Long blogId;
    
    @ApiModelProperty("博客标题")
    private String blogTitle;
    
    @ApiModelProperty("审核用户ID")
    private Long userId;
    
    @ApiModelProperty("用户名")
    private String username;
    
    @ApiModelProperty("审核评论ID")
    private Long commentId;
    
    @ApiModelProperty("评论内容")
    private String commentContent;
    
    @ApiModelProperty("审核时间")
    private LocalDateTime reviewTime;
    
    @ApiModelProperty("审核结果")
    private Integer reviewResult;
    
    @ApiModelProperty("审核原因")
    private String reviewReason;
    
    @ApiModelProperty("给作者的消息")
    private String messageToAuthor;
    
    @ApiModelProperty("审核类型（0-用户、1-博客、2-评论、3-申诉）")
    private Integer reviewType;
} 
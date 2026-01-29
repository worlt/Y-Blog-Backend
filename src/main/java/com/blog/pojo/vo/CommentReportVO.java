package com.blog.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 评论举报VO
 * 
 * @Author worlt
 * @Date 2025/6/4 下午5:50
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "评论举报视图对象")
public class CommentReportVO {

    @ApiModelProperty("举报ID")
    private Long id;

    @ApiModelProperty("举报用户ID（举报人）")
    private Long reporterId;

    @ApiModelProperty("举报用户名")
    private String reporterName;

    @ApiModelProperty("被举报评论ID")
    private Long commentId;

    @ApiModelProperty("评论内容")
    private String commentContent;

    @ApiModelProperty("评论作者ID")
    private Long commentUserId;

    @ApiModelProperty("评论作者名")
    private String commentUsername;

    @ApiModelProperty("举报时间")
    private LocalDateTime reportTime;

    @ApiModelProperty("举报原因")
    private String reportReason;

    @ApiModelProperty("举报状态（0-待审核、1-处理中、2-已处理）")
    private Integer status;

    @ApiModelProperty("审核员ID")
    private Long reviewerId;

    @ApiModelProperty("审核员名称")
    private String reviewerName;

    @ApiModelProperty("审核时间")
    private LocalDateTime reviewTime;

    @ApiModelProperty("审核结果（0-正常、1-删除）")
    private Integer reviewResult;

    @ApiModelProperty("审核原因")
    private String reviewReason;
}
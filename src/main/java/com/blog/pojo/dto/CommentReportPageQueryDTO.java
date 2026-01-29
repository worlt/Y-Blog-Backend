package com.blog.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 评论举报分页查询DTO
 * 
 * @Author worlt
 * @Date 2025/6/4 下午5:45
 */
@Data
@ApiModel(description = "评论举报分页查询数据传输对象")
public class CommentReportPageQueryDTO {

    @ApiModelProperty("页码")
    private int page;

    @ApiModelProperty("每页记录数")
    private int pageSize;

    @ApiModelProperty("举报状态（0-待审核、1-处理中、2-已处理）")
    private Integer status;

    @ApiModelProperty("举报用户ID（举报人）")
    private Long reporterId;

    @ApiModelProperty("被举报评论ID")
    private Long commentId;

    @ApiModelProperty("审核员ID")
    private Long reviewerId;

    @ApiModelProperty("举报用户名称（用于模糊查询）")
    private String reporterName;

    @ApiModelProperty("评论内容（用于模糊查询）")
    private String commentContent;
}
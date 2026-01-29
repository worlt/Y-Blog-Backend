package com.blog.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 博客举报分页查询DTO
 * 
 * @Author worlt
 * @Date 2025/6/3 下午5:35
 */
@Data
@ApiModel(description = "博客举报分页查询数据传输对象")
public class BlogReportPageQueryDTO {

    @ApiModelProperty("页码")
    private int page;

    @ApiModelProperty("每页记录数")
    private int pageSize;

    @ApiModelProperty("举报状态（0-待审核、1-处理中、2-已处理）")
    private Integer status;

    @ApiModelProperty("举报用户ID（举报人）")
    private Long reporterId;

    @ApiModelProperty("举报用户名")
    private String reporterName;

    @ApiModelProperty("被举报博客ID")
    private Long blogId;

    @ApiModelProperty("审核员ID")
    private Long reviewerId;
}

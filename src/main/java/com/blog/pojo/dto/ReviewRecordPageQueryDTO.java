package com.blog.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审核记录分页查询DTO
 * @Author worlt
 * @Date 2025/6/4 下午9:40
 */
@Data
@ApiModel(description = "审核记录分页查询数据传输对象")
public class ReviewRecordPageQueryDTO {

    @ApiModelProperty("审核记录ID")
    private Long recordId;

    @ApiModelProperty("页码")
    private int page;

    @ApiModelProperty("每页记录数")
    private int pageSize;
    
    @ApiModelProperty("审核员ID")
    private Long reviewerId;
    
    @ApiModelProperty("审核类型（0-用户、1-博客、2-评论、3-申诉）")
    private Integer reviewType;
    
    @ApiModelProperty("审核结果")
    private Integer reviewResult;
    
//    @ApiModelProperty("开始时间")
//    private LocalDateTime beginTime;
//
//    @ApiModelProperty("结束时间")
//    private LocalDateTime endTime;
    
    @ApiModelProperty("用户ID")
    private Long userId;
    
    @ApiModelProperty("博客ID")
    private Long blogId;
    
    @ApiModelProperty("评论ID")
    private Long commentId;
    
    @ApiModelProperty("目标ID（关联ID，根据审核类型不同对应不同实体）")
    private Long targetId;
} 
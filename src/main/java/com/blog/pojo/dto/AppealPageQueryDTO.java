package com.blog.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 申诉分页查询DTO
 * @Author worlt
 * @Date 2025/6/4 下午9:12
 */
@Data
@ApiModel(description = "申诉分页查询数据传输对象")
public class AppealPageQueryDTO {

    @ApiModelProperty("页码")
    private int page;

    @ApiModelProperty("每页记录数")
    private int pageSize;

    @ApiModelProperty("申诉状态（0-待审核、1-处理中、2-已处理）")
    private Integer status;

    @ApiModelProperty("申诉用户ID")
    private Long userId;
    
    @ApiModelProperty("审核员ID")
    private Long reviewerId;
} 
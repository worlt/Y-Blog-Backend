package com.blog.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 管理员密码修改数据传输对象
 */
@Data
@ApiModel(description = "管理员密码修改数据传输对象")
public class AdminPasswordDTO {
    
    @ApiModelProperty("管理员ID")
    private Long id;
    
    @ApiModelProperty("原密码")
    private String oldPassword;
    
    @ApiModelProperty("新密码")
    private String newPassword;
} 
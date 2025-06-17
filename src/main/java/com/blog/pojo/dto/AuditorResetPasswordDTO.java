package com.blog.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 管理员通过邮箱重置密码数据传输对象
 */
@Data
@ApiModel(description = "管理员通过邮箱重置密码数据传输对象")
public class AuditorResetPasswordDTO {
    
    @ApiModelProperty("邮箱")
    private String email;
    
    @ApiModelProperty("验证码")
    private String code;
    
    @ApiModelProperty("新密码")
    private String newPassword;
} 
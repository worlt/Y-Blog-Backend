package com.blog.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 重置密码DTO
 *
 * @Author worlt
 * @Date 2025/6/4 下午20:51
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "重置密码数据传输对象")
public class ResetPasswordDTO {
    
    @ApiModelProperty("邮箱")
    private String email;
    
    @ApiModelProperty("验证码")
    private String code;
    
    @ApiModelProperty("新密码")
    private String newPassword;
} 
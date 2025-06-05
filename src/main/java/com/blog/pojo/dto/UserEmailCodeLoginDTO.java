package com.blog.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户邮箱验证码登录DTO
 *
 * @Author worlt
 * @Date 2025/6/4 下午20:50
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "用户邮箱验证码登录数据传输对象")
public class UserEmailCodeLoginDTO {
    
    @ApiModelProperty("邮箱")
    private String email;
    
    @ApiModelProperty("验证码")
    private String code;
} 
package com.blog.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户邮箱密码登录DTO
 *
 * @Author worlt
 * @Date 2025/6/4 下午20:47
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "用户邮箱密码登录数据传输对象")
public class UserEmailLoginDTO {
    
    @ApiModelProperty("邮箱")
    private String email;
    
    @ApiModelProperty("密码")
    private String password;
} 
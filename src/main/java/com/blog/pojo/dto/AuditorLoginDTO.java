package com.blog.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author worlt
 * @Date 2025/5/18 下午7:52
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditorLoginDTO implements Serializable {

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("验证码token (可选，用于验证滑动验证码)")
    private String captchaToken;

}

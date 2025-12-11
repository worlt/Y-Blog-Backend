package com.blog.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 滑动验证码验证返回数据
 *
 * @Author cascade
 * @Date 2025/12/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "滑动验证码验证结果")
public class CaptchaVerifyVO implements Serializable {

    @ApiModelProperty("验证token (用于登录时验证)")
    private String captchaToken;

    @ApiModelProperty("验证模式: backend/frontend")
    private String mode;

}

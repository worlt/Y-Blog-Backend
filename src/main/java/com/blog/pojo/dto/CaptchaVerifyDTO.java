package com.blog.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 滑动验证码验证请求
 *
 * @Author cascade
 * @Date 2025/12/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CaptchaVerifyDTO implements Serializable {

    @ApiModelProperty("拼图ID")
    private String puzzleId;

    @ApiModelProperty("用户拖动的X偏移量")
    private Integer offsetX;

    @ApiModelProperty("用户拖动的Y偏移量")
    private Integer offsetY;

    @ApiModelProperty("难度等级: easy/normal/hard")
    private String difficulty;

}

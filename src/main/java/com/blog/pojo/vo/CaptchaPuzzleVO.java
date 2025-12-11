package com.blog.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 滑动验证码拼图返回数据
 *
 * @Author cascade
 * @Date 2025/12/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "滑动验证码拼图数据")
public class CaptchaPuzzleVO implements Serializable {

    @ApiModelProperty("拼图ID (null表示降级到前端模式)")
    private String puzzleId;

    @ApiModelProperty("背景图片 (base64或URL)")
    private String backgroundImage;

    @ApiModelProperty("拼图块X轴偏移量")
    private Integer offsetX;

    @ApiModelProperty("拼图块Y轴偏移量")
    private Integer offsetY;

    @ApiModelProperty("拼图块颜色/图片")
    private String puzzleImage;

    @ApiModelProperty("难度等级: easy/normal/hard")
    private String difficulty;

    @ApiModelProperty("验证模式: backend/frontend")
    private String mode;

}

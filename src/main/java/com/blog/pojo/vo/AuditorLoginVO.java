package com.blog.pojo.vo;

import io.swagger.annotations.ApiModel;
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
@ApiModel(description = "编辑员登录返回的数据格式")
public class AuditorLoginVO implements Serializable {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("jwt令牌")
    private String token;

}

package com.blog.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户登录返回数据
 *
 * @Author worlt
 * @Date 2025/5/18 下午7:52
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "用户登录返回的数据格式")
public class UserLoginVO implements Serializable {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("头像URL")
    private String avatar;

    @ApiModelProperty("微信小程序openid")
    private String openid;

    @ApiModelProperty("微信小程序unionid")
    private String unionid;

    @ApiModelProperty("角色类型(0-管理员，1-审核员，2-普通用户)")
    private Integer role;

    @ApiModelProperty("jwt令牌")
    private String token;

}

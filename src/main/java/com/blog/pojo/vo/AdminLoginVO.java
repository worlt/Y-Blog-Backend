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
@ApiModel(description = "管理员登录返回的数据格式")
public class AdminLoginVO implements Serializable {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("头像URL")
    private String avatar;

    @ApiModelProperty("角色类型(0-管理员，1-审核员，2-普通用户)")
    private Integer role;

    @ApiModelProperty("jwt令牌")
    private String token;

}

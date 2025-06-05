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

}

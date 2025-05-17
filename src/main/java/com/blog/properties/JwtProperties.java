package com.blog.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT配置管理类
 *
 *
 * @Author worlt
 * @Date 2025/5/17 下午10:10
 */
@Component
@ConfigurationProperties(prefix = "blog.jwt")
@Data
public class JwtProperties {

    /**
     * 管理员生成jwt令牌相关配置
     */
    private String adminSecretKey;
    private Long adminTtl;
    private String adminTokenName;

    /**
     * 审核员生成jwt令牌相关配置
     */
    private String auditorSecretKey;
    private Long auditorTtl;
    private String auditorTokenName;

    /**
     * 用户生成jwt令牌相关配置
     */
    private String userSecretKey;
    private Long userTtl;
    private String userTokenName;

}

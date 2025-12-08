package com.blog.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * swagger配置类
 *
 * @Author worlt
 * @Date 2025/5/17 下午4:11
 */
@Configuration
@EnableSwagger2
@Slf4j
public class SwaggerConfiguration {

    @Bean
    public Docket docket1() {
        log.info("正在初始化管理端接口文档配置");
        ApiInfo apiInfo =  new ApiInfoBuilder()
                .title("管理端项目接口文档")
                .version("1.0")
                .description("管理端项目接口文档")
                .build();

        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .groupName("管理端接口")
                .apiInfo(apiInfo)
                .select()
                // 指定接口需要扫面描的包
                .apis(RequestHandlerSelectors.basePackage("com.blog.controller.admin"))
                .paths(PathSelectors.any())
                .build();

        return docket;
    }

    @Bean
    public Docket docket2() {
        log.info("准备生成审核端接口文档......");
        ApiInfo apiInfo =  new ApiInfoBuilder()
                .title("审核端项目接口文档")
                .version("1.0")
                .description("审核端项目接口文档")
                .build();

        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .groupName("审核端接口")
                .apiInfo(apiInfo)
                .select()
                // 指定接口需要扫面描的包
                .apis(RequestHandlerSelectors.basePackage("com.blog.controller.auditor"))
                .paths(PathSelectors.any())
                .build();

        return docket;
    }

    @Bean
    public Docket docket3() {
        log.info("准备生成用户端接口文档......");
        ApiInfo apiInfo =  new ApiInfoBuilder()
                .title("用户端项目接口文档")
                .version("1.0")
                .description("用户端项目接口文档")
                .build();

        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .groupName("用户端接口")
                .apiInfo(apiInfo)
                .select()
                // 指定接口需要扫面描的包
                .apis(RequestHandlerSelectors.basePackage("com.blog.controller.user"))
                .paths(PathSelectors.any())
                .build();

        return docket;
    }
    
    @Bean
    public Docket docket4() {
        log.info("准备生成通用功能接口文档......");
        ApiInfo apiInfo =  new ApiInfoBuilder()
                .title("通用功能接口文档")
                .version("1.0")
                .description("通用功能接口文档，包括文件上传等公共服务")
                .build();

        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .groupName("通用功能接口")
                .apiInfo(apiInfo)
                .select()
                // 指定接口需要扫面描的包
                .apis(RequestHandlerSelectors.basePackage("com.blog.controller.common"))
                .paths(PathSelectors.any())
                .build();

        return docket;
    }
}

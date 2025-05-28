package com.blog.config;

import com.blog.filter.XssFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Web安全配置类
 * 注册XSS过滤器和其他安全配置
 */
@Configuration
public class WebSecurityConfig {

    /**
     * 注册XSS过滤器
     */
    @Bean
    public FilterRegistrationBean<XssFilter> xssFilterRegistration() {
        FilterRegistrationBean<XssFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new XssFilter());
        registration.addUrlPatterns("/*");
        registration.setName("xssFilter");
        registration.setOrder(1);
        
        Map<String, String> initParameters = new HashMap<>();
        // 排除特定路径，例如富文本编辑器上传接口等
        initParameters.put("excludes", "/druid/*,/swagger/*,/v2/api-docs,/webjars/*");
        registration.setInitParameters(initParameters);
        
        return registration;
    }
} 
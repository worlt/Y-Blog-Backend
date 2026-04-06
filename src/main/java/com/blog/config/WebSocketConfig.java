package com.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket 配置类
 * 开启WebSocket支持
 *
 * @author worlt
 */
@Configuration
public class WebSocketConfig {

    /**
     * 自动注册使用了 @ServerEndpoint 注解声明的 Websocket endpoint
     * 注意：如果使用独立的 Servlet 容器（如 Tomcat），不需要此 Bean。
     * 但如果是 Spring Boot 内置容器，则必须有。
     * 
     * 在单元测试环境（MockMvc）下，不启动容器，故此 Bean 会因找不到 ServerContainer 而报错。
     * 使用 @Profile("!test") 确保测试环境下不加载此 Bean。
     */
    @Bean
    @Profile("!test")
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}

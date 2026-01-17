package com.blog.config;

import com.blog.interceptor.*;
import com.blog.json.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * 配置类，注册web层相关组件
 *
 * @Author worlt
 * @Date 2025/5/16 下午4:46
 */
@Configuration
@Slf4j
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

        @Autowired
        private JwtTokenAdminInterceptor jwtTokenAdminInterceptor;

        @Autowired
        private JwtTokenAuditorInterceptor jwtTokenAuditorInterceptor;

        @Autowired
        private JwtTokenUserInterceptor jwtTokenUserInterceptor;

        @Autowired
        private BitifulOssInterceptor bitifulOssInterceptor;

        @Autowired
        private GlobalTokenInterceptor globalTokenInterceptor;

        /**
         * 注册自定义拦截器
         *
         * @param registry
         */
        @Override
        protected void addInterceptors(InterceptorRegistry registry) {
                log.info("开始注册自定义拦截器...");

                // 注册全局Token拦截器，拦截所有请求，并设置为最高优先级
                registry.addInterceptor(globalTokenInterceptor)
                                .addPathPatterns("/**")
                                .order(1);

                registry.addInterceptor(jwtTokenAdminInterceptor)
                                .addPathPatterns("/admin/**")
                                .excludePathPatterns("/admin/admin/login")
                                .order(2);

                registry.addInterceptor(jwtTokenAuditorInterceptor)
                                .addPathPatterns("/auditor/**")
                                .excludePathPatterns("/auditor/auditor/login")
                                .order(3);

                registry.addInterceptor(jwtTokenUserInterceptor)
                                .addPathPatterns("/user/**")
                                .excludePathPatterns("/user/user/resetPassword")
                                .excludePathPatterns("/user/user/login/code")
                                .excludePathPatterns("/user/user/login/password")
                                .excludePathPatterns("/user/user/register")
                                .excludePathPatterns("/user/user/sendCode")
                                // 公开申诉接口
                                .excludePathPatterns("/user/appeal/submit")
                                // 博客相关公开接口
                                .excludePathPatterns("/user/blog/page")
                                .excludePathPatterns("/user/blog/getById/**")
                                .excludePathPatterns("/user/blog/viewBlog/**")
                                .excludePathPatterns("/user/blog/getShareStatistics/**")
                                // 用户公开信息
                                .excludePathPatterns("/user/user/getById/**")
                                // 评论公开查看
                                .excludePathPatterns("/user/comment/getByBlogId/**")
                                // 分类公开查看
                                .excludePathPatterns("/user/tag-category/**")
                                // 网站访问统计 (公开)
                                .excludePathPatterns("/visit")
                                .order(4);

                // 注册OSS文件上传拦截器
                registry.addInterceptor(bitifulOssInterceptor)
                                .addPathPatterns("/common/oss/**")
                                .excludePathPatterns("/common/oss/presigned") // 预签名URL可以公开访问
                                .order(5);
        }

        /**
         * 扩展Spring MVC框架的消息转化器
         *
         * @param converters
         */
        protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
                log.info("扩展消息转换器...");
                // 创建一个消息转换器对象
                MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
                // 需要为消息转换器设置一个对象转换器，对象转换器可以将Java对象序列化为json数据
                converter.setObjectMapper(new JacksonObjectMapper());
                // 将自己的消息转化器加入容器中
                converters.add(0, converter);
        }

        /**
         * 设置静态资源映射
         *
         * @param registry
         */
        @Override
        protected void addResourceHandlers(ResourceHandlerRegistry registry) {
                log.info("设置静态资源映射...");
                registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
                registry.addResourceHandler("/webjars/**")
                                .addResourceLocations("classpath:/META-INF/resources/webjars/");
                registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
                // 必须调用父类方法，否则会导致默认的静态资源处理失效
                super.addResourceHandlers(registry);
        }
}

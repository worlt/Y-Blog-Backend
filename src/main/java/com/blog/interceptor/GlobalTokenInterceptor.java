package com.blog.interceptor;

import com.blog.constant.JwtClaimsConstant;
import com.blog.context.BaseContext;
import com.blog.properties.JwtProperties;
import com.blog.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 全局Token拦截器
 * 拦截所有请求，解析token并将用户ID存入ThreadLocal
 * 
 * @author worlt
 * @date 2025/06/23 15:00:34
 */
@Component
@Slf4j
public class GlobalTokenInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 前置处理：在请求处理之前执行，检查token并解析用户ID
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 获取请求头中的token
        String token = request.getHeader(jwtProperties.getUserTokenName());
        
        // 如果存在token，尝试解析
        if (StringUtils.hasText(token)) {
            try {
                log.debug("全局Token拦截器检测到token: {}", token);
                
                // 解析token，获取用户id
                Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
                Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
                
                // 将用户ID存入ThreadLocal
                BaseContext.setCurrentId(userId);
                log.debug("全局Token拦截器解析用户ID: {}", userId);
            } catch (Exception e) {
                // token解析失败，不影响请求继续处理
                log.warn("全局Token拦截器解析token失败: {}", e.getMessage());
            }
        } else {
            log.debug("全局Token拦截器未检测到token");
        }
        
        // 无论token是否有效，都放行请求
        return true;
    }

    /**
     * 后置处理：在请求处理完成后执行，清除ThreadLocal中的用户ID
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清除ThreadLocal中的数据，防止内存泄漏
        BaseContext.removeCurrentId();
        log.debug("全局Token拦截器清除ThreadLocal");
    }
} 
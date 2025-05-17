package com.blog.interceptor;

import com.blog.constant.JwtClaimsConstant;
import com.blog.context.BaseContext;
import com.blog.properties.JwtProperties;
import com.blog.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 用户jwt令牌拦截器
 *
 * @Author worlt
 * @Date 2025/5/18 上午12:03
 */
@Component
@Slf4j
public class JwtTokenUserInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handle) {
        if (!(handle instanceof HandlerMethod)) {
            return true;
        }

        String token = request.getHeader(jwtProperties.getUserTokenName());
        try {
            log.info("jwt校验：{}", token);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
            Long userId = (Long) claims.get(JwtClaimsConstant.USER_ID.toString());
            log.info("当前用户id：{}", userId);
            BaseContext.setCurrentId(userId);
            return true;
        } catch (Exception e) {
            response.setStatus(401);
            return false;
        }
    }

}

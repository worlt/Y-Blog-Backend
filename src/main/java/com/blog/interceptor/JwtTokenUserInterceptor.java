package com.blog.interceptor;

import com.blog.constant.JwtClaimsConstant;
import com.blog.constant.MessageConstant;
import com.blog.constant.statusConstant.UserStatusConstant;
import com.blog.context.BaseContext;
import com.blog.mapper.UserMapper;
import com.blog.pojo.entity.User;
import com.blog.properties.JwtProperties;
import com.blog.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

    @Autowired
    private UserMapper userMapper;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handle)
            throws IOException {
        // 放行OPTIONS请求，解决跨域预检问题
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        if (!(handle instanceof HandlerMethod)) {
            return true;
        }

        String token = request.getHeader(jwtProperties.getUserTokenName());
        try {
            log.info("jwt校验：{}", token);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
            Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
            log.info("当前用户id：{}", userId);

            // 检查用户是否被封禁
            User user = userMapper.getById(userId);
            if (user != null && UserStatusConstant.BANNED.equals(user.getStatus())) {
                log.warn("用户已被封禁，userId: {}", userId);
                // 返回403状态码和封禁提示信息
                response.setStatus(403);
                response.setContentType("application/json;charset=UTF-8");
                Map<String, Object> result = new HashMap<>();
                result.put("code", 403);
                result.put("msg", "您的账号已被封禁，请提交申诉");
                response.getWriter().write(new ObjectMapper().writeValueAsString(result));
                return false;
            }

            BaseContext.setCurrentId(userId);
            return true;
        } catch (Exception e) {
            response.setStatus(401);
            throw new RuntimeException(MessageConstant.RELOGIN);
            // return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // 请求结束后清理ThreadLocal
        log.debug("清理ThreadLocal中的用户ID信息：{}", BaseContext.getCurrentId());
        BaseContext.removeCurrentId();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

}

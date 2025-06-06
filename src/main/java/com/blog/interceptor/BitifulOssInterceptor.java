package com.blog.interceptor;

import com.blog.constant.JwtClaimsConstant;
import com.blog.constant.MessageConstant;
import com.blog.context.BaseContext;
import com.blog.properties.JwtProperties;
import com.blog.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 文件上传拦截器
 * 
 * @Author worlt
 * @Date 2025/6/8 下午2:30
 */
@Component
@Slf4j
public class BitifulOssInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 拦截器前置处理
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("OSS拦截器开始校验...");
        
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // 从请求头中获取token
        String token = request.getHeader(jwtProperties.getUserTokenName());
        if (token == null) {
            token = request.getHeader(jwtProperties.getAdminTokenName());
        }
        if (token == null) {
            token = request.getHeader(jwtProperties.getAuditorTokenName());
        }
        
        // 如果没有token，拒绝访问
        if (token == null) {
            log.warn("未提供授权Token，拒绝访问");
            response.setStatus(401);
            throw new RuntimeException(MessageConstant.RELOGIN);
//            return false;
        }

        try {
            // 尝试以用户token解析
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
            Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
            log.info("当前用户ID：{}", userId);
            BaseContext.setCurrentId(userId);
            return true;
        } catch (Exception e1) {
            try {
                // 尝试以管理员token解析
                Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
                Long adminId = Long.valueOf(claims.get(JwtClaimsConstant.ADMIN_ID).toString());
                log.info("当前管理员ID：{}", adminId);
                BaseContext.setCurrentId(adminId);
                return true;
            } catch (Exception e2) {
                try {
                    // 尝试以审核员token解析
                    Claims claims = JwtUtil.parseJWT(jwtProperties.getAuditorSecretKey(), token);
                    Long auditorId = Long.valueOf(claims.get(JwtClaimsConstant.AUDITOR_ID).toString());
                    log.info("当前审核员ID：{}", auditorId);
                    BaseContext.setCurrentId(auditorId);
                    return true;
                } catch (Exception e3) {
                    log.error("JWT校验失败");
                    response.setStatus(401);
                    throw new RuntimeException(MessageConstant.RELOGIN);
//                    return false;
                }
            }
        }
    }

    /**
     * 请求完成后的清理操作
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求结束后清理ThreadLocal
        log.debug("清理ThreadLocal中的用户ID信息：{}", BaseContext.getCurrentId());
        BaseContext.removeCurrentId();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
} 
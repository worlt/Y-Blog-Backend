package com.blog.interceptor;

import com.blog.constant.JwtClaimsConstant;
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
 * 审核员jwt令牌拦截器
 *
 * @Author worlt
 * @Date 2025/5/17 下午11:52
 */
@Component
@Slf4j
public class JwtTokenAuditorInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handle) throws Exception {
        if (!(handle instanceof HandlerMethod)) {
            return true;
        }

        String token = request.getHeader(jwtProperties.getAuditorTokenName());
        try {
            log.info("jwt校验：{}", token);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getAuditorSecretKey(), token);
            Long auditorId = (Long) claims.get(JwtClaimsConstant.AUDITOR_ID.toString());
            log.info("当前审核员id：{}", auditorId);
            BaseContext.setCurrentId(auditorId);
            return true;
        } catch (Exception e) {
            response.setStatus(401);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求结束后清理ThreadLocal
        log.debug("清理ThreadLocal中的审核员ID信息：{}", BaseContext.getCurrentId());
        BaseContext.removeCurrentId();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

}

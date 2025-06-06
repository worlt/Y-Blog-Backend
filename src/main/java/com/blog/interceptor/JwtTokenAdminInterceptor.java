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
 * 管理员jwt令牌拦截器
 *
 * @Author worlt
 * @Date 2025/5/17 下午10:32
 */
@Component
@Slf4j
public class JwtTokenAdminInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handle) throws Exception {
        // 判断当前拦截到的是controller的方法还是其他的资源
        if (!(handle instanceof HandlerMethod)) {
            // 当前拦截器拦截的不是静态方法，直接放行
            return true;
        }

        // 1.从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getAdminTokenName());

        // 2.校验令牌
        try {
            log.info("jwt校验：{}", token);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
            Long adminId = Long.valueOf(claims.get(JwtClaimsConstant.ADMIN_ID).toString());
            log.info("当前管理员id：{}", adminId);
            BaseContext.setCurrentId(adminId);
            // 3.放行
            return true;
        } catch (Exception e) {
            // 4.不通过，响应401
            response.setStatus(401);
            throw new RuntimeException(MessageConstant.RELOGIN);
//            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求结束后清理ThreadLocal
        log.debug("清理ThreadLocal中的管理员ID信息：{}", BaseContext.getCurrentId());
        BaseContext.removeCurrentId();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

}

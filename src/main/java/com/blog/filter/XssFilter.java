package com.blog.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * XSS攻击防护过滤器
 * 用于拦截所有请求并进行XSS清理
 */
@Slf4j
public class XssFilter implements Filter {

    /**
     * 不需要过滤的路径
     */
    private List<Pattern> excludePatterns = Collections.emptyList();
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String excludes = filterConfig.getInitParameter("excludes");
        if (excludes != null && !excludes.isEmpty()) {
            String[] excludeArray = excludes.split(",");
            excludePatterns = Arrays.stream(excludeArray)
                    .map(pattern -> Pattern.compile(pattern.trim(), Pattern.CASE_INSENSITIVE))
                    .collect(Collectors.toList());
            log.info("XSS防护过滤器初始化成功，排除路径: {}", excludes);
        } else {
            log.info("XSS防护过滤器初始化成功，未设置排除路径");
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getServletPath();
        
        // 检查是否需要排除该请求
        if (isExcluded(httpRequest)) {
            log.debug("XSS过滤器 - 排除路径: {}", path);
            chain.doFilter(request, response);
            return;
        }
        
        // 包装请求，应用XSS清理
        log.debug("XSS过滤器 - 处理请求: {}", path);
        XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper(httpRequest);
        chain.doFilter(xssRequest, response);
    }

    @Override
    public void destroy() {
        log.info("XSS过滤器已销毁");
    }
    
    /**
     * 检查请求是否在排除列表中
     * @param request HTTP请求
     * @return 是否排除XSS过滤
     */
    private boolean isExcluded(HttpServletRequest request) {
        String path = request.getServletPath();
        
        // 静态资源排除
        if (path.startsWith("/static/") || path.startsWith("/public/") || 
            path.startsWith("/resources/") || path.startsWith("/assets/") ||
            path.endsWith(".css") || path.endsWith(".js") || 
            path.endsWith(".jpg") || path.endsWith(".png") || path.endsWith(".gif")) {
            return true;
        }
        
        // 配置的排除路径
        for (Pattern pattern : excludePatterns) {
            if (pattern.matcher(path).matches()) {
                return true;
            }
        }
        
        return false;
    }
} 
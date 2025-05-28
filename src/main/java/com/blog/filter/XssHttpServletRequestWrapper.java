package com.blog.filter;

import com.blog.utils.XssCleanUtil;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.regex.Pattern;

/**
 * XSS攻击防护请求包装器
 * 用于自动过滤请求参数中的XSS攻击内容
 */
@Slf4j
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    /**
     * 匹配富文本内容的参数名称
     * 针对富文本内容使用不同的清理策略
     */
    private static final Pattern RICH_TEXT_PATTERN = Pattern.compile(".*(content|detail|html|richtext).*",
            Pattern.CASE_INSENSITIVE);

    private final String requestURI;

    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        this.requestURI = request.getRequestURI();
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        String cleanValue = cleanParameter(name, value);
        logIfModified(name, value, cleanValue, "请求参数");
        return cleanValue;
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) {
            return null;
        }

        String[] cleanValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            cleanValues[i] = cleanParameter(name, values[i]);
            logIfModified(name, values[i], cleanValues[i], "请求参数数组[" + i + "]");
        }
        return cleanValues;
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        String cleanValue = cleanParameter(name, value);
        logIfModified(name, value, cleanValue, "请求头");
        return cleanValue;
    }

    /**
     * 根据参数名和值进行XSS清理
     * 
     * @param name  参数名
     * @param value 参数值
     * @return 清理后的值
     */
    private String cleanParameter(String name, String value) {
        if (value == null) {
            return null;
        }

        // 对富文本内容使用richText清理方法，保留基本HTML格式
        if (RICH_TEXT_PATTERN.matcher(name).matches()) {
            return XssCleanUtil.cleanRichText(value);
        }
        // 对其他参数完全清理HTML标签
        else {
            return XssCleanUtil.cleanAllTags(value);
        }
    }

    /**
     * 如果值被修改，记录日志
     */
    private void logIfModified(String name, String original, String cleaned, String type) {
        if (original != null && cleaned != null && !original.equals(cleaned)) {
            // 检查是否含有常见的XSS攻击特征
            boolean containsScriptTag = original.toLowerCase().contains("<script");
            boolean containsEventHandler = original.toLowerCase().matches(".*\\s+on\\w+\\s*=.*");
            boolean containsJavaScriptUrl = original.toLowerCase().contains("javascript:");
            
            if (containsScriptTag || containsEventHandler || containsJavaScriptUrl) {
                log.warn("XSS过滤器 - 检测到潜在XSS攻击! URI: {}, 类型: {}, 参数名: {}", 
                         requestURI, type, name);
                
                // 记录截断的原始值，避免记录完整的恶意代码
                String truncated = original.length() > 50 ? 
                                  original.substring(0, 50) + "..." : 
                                  original;
                log.warn("可疑内容前缀: {}", truncated.replaceAll("\n", " "));
            }
        }
    }
}
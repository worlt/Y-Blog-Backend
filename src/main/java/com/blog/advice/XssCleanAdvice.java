package com.blog.advice;

import com.blog.utils.XssCleanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

/**
 * XSS清理切面
 * 用于处理@RequestBody注解的JSON数据
 */
@ControllerAdvice
@Slf4j
public class XssCleanAdvice extends RequestBodyAdviceAdapter {

    // 可能包含富文本内容的字段名
    private static final List<String> RICH_TEXT_FIELDS = Arrays.asList(
            "content", "detail", "html", "richtext"
    );

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, 
                           Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, 
                               MethodParameter parameter, Type targetType,
                               Class<? extends HttpMessageConverter<?>> converterType) {
        try {
            // 记录处理开始
            log.debug("开始处理JSON数据XSS清理: {}", parameter.getMethod().getName());
            
            // 递归清理对象中的所有String字段
            cleanObject(body, parameter.getMethod().getDeclaringClass().getSimpleName() + "." + parameter.getMethod().getName());
        } catch (Exception e) {
            // 记录异常但不影响请求处理
            log.error("JSON数据XSS清理异常", e);
        }
        return body;
    }

    /**
     * 递归清理对象中的所有字符串字段
     */
    private void cleanObject(Object obj, String source) throws IllegalAccessException {
        if (obj == null) {
            return;
        }
        
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(obj);
            
            if (value instanceof String) {
                String strValue = (String) value;
                String fieldName = field.getName();
                String original = strValue;
                
                // 根据字段名决定使用哪种清理方法
                String cleaned;
                if (isRichTextField(fieldName)) {
                    cleaned = XssCleanUtil.cleanRichText(strValue);
                } else {
                    cleaned = XssCleanUtil.cleanAllTags(strValue);
                }
                
                // 检查是否有XSS尝试
                if (!original.equals(cleaned)) {
                    boolean containsScriptTag = original.toLowerCase().contains("<script");
                    boolean containsEventHandler = original.toLowerCase().matches(".*\\s+on\\w+\\s*=.*");
                    boolean containsJavaScriptUrl = original.toLowerCase().contains("javascript:");
                    
                    if (containsScriptTag || containsEventHandler || containsJavaScriptUrl) {
                        log.warn("JSON数据XSS尝试 - 来源: {}, 字段: {}", source, fieldName);
                        String truncated = original.length() > 50 ? 
                                         original.substring(0, 50) + "..." : 
                                         original;
                        log.warn("可疑内容前缀: {}", truncated.replaceAll("\n", " "));
                    }
                }
                
                // 设置清理后的值
                field.set(obj, cleaned);
            } 
            // 递归处理嵌套对象
            else if (value != null && !value.getClass().isPrimitive() && !value.getClass().getName().startsWith("java.")) {
                cleanObject(value, source + "." + field.getName());
            }
        }
    }
    
    /**
     * 判断字段是否可能包含富文本内容
     */
    private boolean isRichTextField(String fieldName) {
        String lowerFieldName = fieldName.toLowerCase();
        return RICH_TEXT_FIELDS.stream().anyMatch(lowerFieldName::contains);
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, 
                                          Type targetType, Class<? extends HttpMessageConverter<?>> converterType) 
            throws IOException {
        return inputMessage;
    }
} 
package com.blog.handler;

import com.blog.constant.MessageConstant;
import com.blog.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 全局异常处理器，处理项目中抛出的各种异常
 * 
 * @author Worlt
 * @date 2025/6/17
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private final HttpMessageConverters messageConverters;

    public GlobalExceptionHandler(HttpMessageConverters messageConverters) {
        this.messageConverters = messageConverters;
    }

    /**
     * 捕获业务运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String message = ex.getMessage();
        
        // 优先处理JWT相关异常
        if (message != null && (message.contains("JWT") || message.contains("令牌"))) {
            log.warn("JWT令牌异常，请求路径：{}，异常信息：{}", requestUri, message);
            
            if (message.contains("已过期")) {
                return Result.error("登录已过期，请重新登录");
            } else if (message.contains("签名验证失败")) {
                return Result.error("登录凭证无效，请重新登录");
            } else if (message.contains("格式错误")) {
                return Result.error("登录凭证格式错误");
            } else {
                return Result.error("身份验证失败，请重新登录");
            }
        }
        
        // 处理其他业务异常
        log.error("业务异常发生，请求路径：{}，异常信息：{}", requestUri, ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 捕获参数校验异常
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleValidationException(Exception ex, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String errorMessage;
        
        if (ex instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException validEx = (MethodArgumentNotValidException) ex;
            errorMessage = validEx.getBindingResult().getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
        } else if (ex instanceof BindException) {
            BindException bindEx = (BindException) ex;
            errorMessage = bindEx.getBindingResult().getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
        } else {
            errorMessage = "参数校验失败";
        }
        
        log.warn("参数校验异常，请求路径：{}，校验错误：{}", requestUri, errorMessage);
        return Result.error("参数校验失败：" + errorMessage);
    }

    /**
     * 捕获数据库约束违反异常
     */
    @ExceptionHandler({SQLIntegrityConstraintViolationException.class, DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleDataIntegrityException(Exception ex, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        log.error("数据完整性约束违反，请求路径：{}，异常类型：{}，异常信息：{}", 
                requestUri, ex.getClass().getSimpleName(), ex.getMessage());
        
        String message;
        if (ex.getMessage().contains("Duplicate entry")) {
            message = "数据已存在，请检查是否重复提交";
        } else if (ex.getMessage().contains("cannot be null")) {
            message = "必填字段不能为空";
        } else if (ex.getMessage().contains("foreign key constraint")) {
            message = "关联数据不存在或已被删除";
        } else {
            message = "数据操作失败，请检查数据完整性";
        }
        
        return Result.error(message);
    }

    /**
     * 捕获空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result handleNullPointerException(NullPointerException ex, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        log.error("空指针异常，请求路径：{}，异常堆栈：", requestUri, ex);
        return Result.error("系统内部错误，请联系管理员");
    }

    /**
     * 捕获非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        log.warn("非法参数异常，请求路径：{}，异常信息：{}", requestUri, ex.getMessage());
        return Result.error("参数错误：" + ex.getMessage());
    }

    /**
     * 捕获所有未处理的异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result handleGenericException(Exception ex, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        log.error("未知异常，请求路径：{}，异常类型：{}，异常信息：{}", 
                requestUri, ex.getClass().getSimpleName(), ex.getMessage(), ex);
        return Result.error("系统繁忙，请稍后重试");
    }
}

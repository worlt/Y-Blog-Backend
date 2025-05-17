package com.blog.handler;

import com.blog.constant.MessageConstant;
import com.blog.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 *
 * @Author worlt
 * @Date 2025/5/17 下午10:18
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private final HttpMessageConverters messageConverters;

    public GlobalExceptionHandler(HttpMessageConverters messageConverters) {
        this.messageConverters = messageConverters;
    }

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(RuntimeException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

}

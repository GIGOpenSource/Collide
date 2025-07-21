package com.gig.collide.artist.infrastructure.handler;

import com.gig.collide.artist.infrastructure.exception.ArtistException;
import com.gig.collide.base.exception.BizException;
import com.gig.collide.web.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Set;

/**
 * 博主模块全局异常处理器
 * @author GIG
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.gig.collide.artist.controller")
public class ArtistGlobalExceptionHandler {

    /**
     * 处理博主模块自定义异常
     */
    @ExceptionHandler(ArtistException.class)
    public Result<Void> handleArtistException(ArtistException e) {
        log.warn("博主模块业务异常: {}", e.getMessage(), e);
        return Result.error(e.getErrorCode().getCode(), e.getErrorCode().getMessage());
    }

    /**
     * 处理通用业务异常
     */
    @ExceptionHandler(BizException.class)
    public Result<Void> handleBizException(BizException e) {
        log.warn("业务异常: {}", e.getMessage(), e);
        return Result.error(e.getErrorCode().getCode(), e.getErrorCode().getMessage());
    }

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        log.warn("参数校验异常: {}", message);
        return Result.error("VALIDATION_ERROR", message);
    }

    /**
     * 处理参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数绑定失败";
        log.warn("参数绑定异常: {}", message);
        return Result.error("BIND_ERROR", message);
    }

    /**
     * 处理约束校验异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        String message = violations.isEmpty() ? "约束校验失败" : 
                        violations.iterator().next().getMessage();
        log.warn("约束校验异常: {}", message);
        return Result.error("CONSTRAINT_VIOLATION", message);
    }

    /**
     * 处理其他未知异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return Result.error("SYSTEM_ERROR", "系统内部错误");
    }
} 
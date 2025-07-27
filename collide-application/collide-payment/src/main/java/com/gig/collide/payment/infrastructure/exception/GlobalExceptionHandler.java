package com.gig.collide.payment.infrastructure.exception;

import com.gig.collide.web.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理支付业务异常
     */
    @ExceptionHandler(PaymentBusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handlePaymentBusinessException(PaymentBusinessException e, HttpServletRequest request) {
        log.warn("支付业务异常 - 请求路径: {}, 错误码: {}, 错误信息: {}", 
            request.getRequestURI(), e.getErrorCode().getCode(), e.getMessage());
        
        return Result.error(e.getErrorCode().getCode(), e.getMessage());
    }

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String errorMessage = fieldErrors.stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
        
        log.warn("参数验证异常 - 请求路径: {}, 验证错误: {}", request.getRequestURI(), errorMessage);
        
        return Result.error(PaymentErrorCode.PARAM_INVALID.getCode(), "参数验证失败: " + errorMessage);
    }

    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handleBindException(BindException e, HttpServletRequest request) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String errorMessage = fieldErrors.stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
        
        log.warn("参数绑定异常 - 请求路径: {}, 绑定错误: {}", request.getRequestURI(), errorMessage);
        
        return Result.error(PaymentErrorCode.PARAM_INVALID.getCode(), "参数绑定失败: " + errorMessage);
    }

    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        String errorMessage = String.format("参数 '%s' 类型不匹配，期望类型: %s，实际值: %s", 
            e.getName(), e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown", e.getValue());
        
        log.warn("参数类型不匹配异常 - 请求路径: {}, 错误信息: {}", request.getRequestURI(), errorMessage);
        
        return Result.error(PaymentErrorCode.PARAM_INVALID.getCode(), errorMessage);
    }

    /**
     * 处理重复键异常（数据库唯一约束）
     */
    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Result<Object> handleDuplicateKeyException(DuplicateKeyException e, HttpServletRequest request) {
        log.warn("数据重复异常 - 请求路径: {}, 错误信息: {}", request.getRequestURI(), e.getMessage());
        
        String errorMessage = "数据重复，可能是订单号或流水号已存在";
        if (e.getMessage() != null) {
            if (e.getMessage().contains("uk_order_no_user_id")) {
                errorMessage = "订单号重复";
            } else if (e.getMessage().contains("uk_internal_transaction_no")) {
                errorMessage = "内部流水号重复";
            }
        }
        
        return Result.error(PaymentErrorCode.DUPLICATE_PAYMENT.getCode(), errorMessage);
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        log.warn("非法参数异常 - 请求路径: {}, 错误信息: {}", request.getRequestURI(), e.getMessage());
        
        return Result.error(PaymentErrorCode.PARAM_INVALID.getCode(), 
            "参数异常: " + (e.getMessage() != null ? e.getMessage() : "未知参数错误"));
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Object> handleNullPointerException(NullPointerException e, HttpServletRequest request) {
        log.error("空指针异常 - 请求路径: {}", request.getRequestURI(), e);
        
        return Result.error(PaymentErrorCode.SYSTEM_ERROR.getCode(), "系统内部错误，请稍后重试");
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Object> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        // 如果是支付业务异常，应该已经被上面的处理器处理了，这里是其他运行时异常
        log.error("运行时异常 - 请求路径: {}, 异常类型: {}", request.getRequestURI(), e.getClass().getSimpleName(), e);
        
        return Result.error(PaymentErrorCode.SYSTEM_ERROR.getCode(), 
            "系统异常: " + (e.getMessage() != null ? e.getMessage() : "未知系统错误"));
    }

    /**
     * 处理通用异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Object> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常 - 请求路径: {}, 异常类型: {}", request.getRequestURI(), e.getClass().getSimpleName(), e);
        
        return Result.error(PaymentErrorCode.SYSTEM_ERROR.getCode(), "系统异常，请联系管理员");
    }

    /**
     * 处理并发更新异常（乐观锁）
     */
    @ExceptionHandler(org.springframework.dao.OptimisticLockingFailureException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Result<Object> handleOptimisticLockingFailureException(
            org.springframework.dao.OptimisticLockingFailureException e, HttpServletRequest request) {
        log.warn("乐观锁异常 - 请求路径: {}, 错误信息: {}", request.getRequestURI(), e.getMessage());
        
        return Result.error(PaymentErrorCode.CONCURRENT_UPDATE.getCode(), "数据已被其他操作修改，请刷新后重试");
    }

    /**
     * 处理数据访问异常
     */
    @ExceptionHandler(org.springframework.dao.DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Object> handleDataAccessException(org.springframework.dao.DataAccessException e, HttpServletRequest request) {
        log.error("数据访问异常 - 请求路径: {}", request.getRequestURI(), e);
        
        return Result.error(PaymentErrorCode.SYSTEM_ERROR.getCode(), "数据库操作异常，请稍后重试");
    }

    /**
     * 处理连接超时异常
     */
    @ExceptionHandler({
        java.net.SocketTimeoutException.class,
        java.net.ConnectException.class,
        org.springframework.web.client.ResourceAccessException.class
    })
    @ResponseStatus(HttpStatus.GATEWAY_TIMEOUT)
    public Result<Object> handleTimeoutException(Exception e, HttpServletRequest request) {
        log.error("连接超时异常 - 请求路径: {}, 异常类型: {}", request.getRequestURI(), e.getClass().getSimpleName(), e);
        
        return Result.error(PaymentErrorCode.PAYMENT_TIMEOUT.getCode(), "操作超时，请稍后重试");
    }

    /**
     * 处理JSON解析异常
     */
    @ExceptionHandler({
        com.fasterxml.jackson.core.JsonProcessingException.class,
        com.fasterxml.jackson.databind.JsonMappingException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handleJsonException(Exception e, HttpServletRequest request) {
        log.warn("JSON解析异常 - 请求路径: {}, 错误信息: {}", request.getRequestURI(), e.getMessage());
        
        return Result.error(PaymentErrorCode.PARAM_INVALID.getCode(), "JSON格式错误: " + e.getMessage());
    }
} 
package com.gig.collide.category.infrastructure.exception;

/**
 * 分类业务异常类
 * 基于Code项目标准化思想，定义分类模块专用业务异常
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public class CategoryBusinessException extends RuntimeException {

    private CategoryErrorCode errorCode;
    private Object data;

    public CategoryBusinessException(CategoryErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CategoryBusinessException(CategoryErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public CategoryBusinessException(CategoryErrorCode errorCode, String message, Object data) {
        super(message);
        this.errorCode = errorCode;
        this.data = data;
    }

    public CategoryBusinessException(CategoryErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public CategoryBusinessException(CategoryErrorCode errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }

    public CategoryErrorCode getErrorCode() {
        return errorCode;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    // ========== 静态工厂方法 ==========

    public static CategoryBusinessException of(CategoryErrorCode errorCode) {
        return new CategoryBusinessException(errorCode);
    }

    public static CategoryBusinessException of(CategoryErrorCode errorCode, String message) {
        return new CategoryBusinessException(errorCode, message);
    }

    public static CategoryBusinessException of(CategoryErrorCode errorCode, String message, Object data) {
        return new CategoryBusinessException(errorCode, message, data);
    }

    public static CategoryBusinessException of(CategoryErrorCode errorCode, String message, Throwable cause) {
        return new CategoryBusinessException(errorCode, message, cause);
    }
} 
package com.gig.collide.payment.infrastructure.exception;

/**
 * 支付业务异常
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
public class PaymentBusinessException extends RuntimeException {

    private final PaymentErrorCode errorCode;
    private final Object data;

    public PaymentBusinessException(PaymentErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.data = null;
    }

    public PaymentBusinessException(PaymentErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.data = null;
    }

    public PaymentBusinessException(PaymentErrorCode errorCode, String message, Object data) {
        super(message);
        this.errorCode = errorCode;
        this.data = data;
    }

    public PaymentBusinessException(PaymentErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.data = null;
    }

    public PaymentBusinessException(PaymentErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.data = null;
    }

    public PaymentErrorCode getErrorCode() {
        return errorCode;
    }

    public Object getData() {
        return data;
    }

    /**
     * 静态工厂方法 - 创建简单业务异常
     */
    public static PaymentBusinessException of(PaymentErrorCode errorCode) {
        return new PaymentBusinessException(errorCode);
    }

    /**
     * 静态工厂方法 - 创建带自定义消息的业务异常
     */
    public static PaymentBusinessException of(PaymentErrorCode errorCode, String message) {
        return new PaymentBusinessException(errorCode, message);
    }

    /**
     * 静态工厂方法 - 创建带数据的业务异常
     */
    public static PaymentBusinessException of(PaymentErrorCode errorCode, String message, Object data) {
        return new PaymentBusinessException(errorCode, message, data);
    }

    /**
     * 静态工厂方法 - 创建带原因的业务异常
     */
    public static PaymentBusinessException of(PaymentErrorCode errorCode, String message, Throwable cause) {
        return new PaymentBusinessException(errorCode, message, cause);
    }
} 
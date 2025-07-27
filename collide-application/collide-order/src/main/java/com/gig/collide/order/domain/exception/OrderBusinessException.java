package com.gig.collide.order.domain.exception;

/**
 * 订单业务异常
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
public class OrderBusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final String errorCode;

    /**
     * 错误消息
     */
    private final String errorMessage;

    public OrderBusinessException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public OrderBusinessException(String errorCode, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    // 常用错误码定义
    public static class ErrorCodes {
        public static final String ORDER_NOT_FOUND = "ORDER_NOT_FOUND";
        public static final String ORDER_STATUS_INVALID = "ORDER_STATUS_INVALID";
        public static final String ORDER_CANNOT_CANCEL = "ORDER_CANNOT_CANCEL";
        public static final String ORDER_CANNOT_REFUND = "ORDER_CANNOT_REFUND";
        public static final String ORDER_EXPIRED = "ORDER_EXPIRED";
        public static final String PAYMENT_FAILED = "PAYMENT_FAILED";
        public static final String INSUFFICIENT_PERMISSION = "INSUFFICIENT_PERMISSION";
        public static final String CONCURRENT_OPERATION = "CONCURRENT_OPERATION";
        public static final String DUPLICATE_REQUEST = "DUPLICATE_REQUEST";
        public static final String PARAMETER_INVALID = "PARAMETER_INVALID";
        public static final String SYSTEM_ERROR = "SYSTEM_ERROR";
    }

    // 快捷创建方法
    public static OrderBusinessException orderNotFound(String orderNo) {
        return new OrderBusinessException(ErrorCodes.ORDER_NOT_FOUND, 
            "订单不存在：" + orderNo);
    }

    public static OrderBusinessException orderStatusInvalid(String orderNo, String currentStatus, String expectedStatus) {
        return new OrderBusinessException(ErrorCodes.ORDER_STATUS_INVALID, 
            String.format("订单状态错误，订单号：%s，当前状态：%s，期望状态：%s", orderNo, currentStatus, expectedStatus));
    }

    public static OrderBusinessException orderCannotCancel(String orderNo, String reason) {
        return new OrderBusinessException(ErrorCodes.ORDER_CANNOT_CANCEL, 
            String.format("订单不能取消，订单号：%s，原因：%s", orderNo, reason));
    }

    public static OrderBusinessException orderCannotRefund(String orderNo, String reason) {
        return new OrderBusinessException(ErrorCodes.ORDER_CANNOT_REFUND, 
            String.format("订单不能退款，订单号：%s，原因：%s", orderNo, reason));
    }

    public static OrderBusinessException orderExpired(String orderNo) {
        return new OrderBusinessException(ErrorCodes.ORDER_EXPIRED, 
            "订单已过期：" + orderNo);
    }

    public static OrderBusinessException paymentFailed(String orderNo, String reason) {
        return new OrderBusinessException(ErrorCodes.PAYMENT_FAILED, 
            String.format("支付失败，订单号：%s，原因：%s", orderNo, reason));
    }

    public static OrderBusinessException insufficientPermission(String operation) {
        return new OrderBusinessException(ErrorCodes.INSUFFICIENT_PERMISSION, 
            "权限不足，无法执行操作：" + operation);
    }

    public static OrderBusinessException concurrentOperation(String orderNo) {
        return new OrderBusinessException(ErrorCodes.CONCURRENT_OPERATION, 
            "并发操作冲突，订单号：" + orderNo);
    }

    public static OrderBusinessException duplicateRequest(String requestId) {
        return new OrderBusinessException(ErrorCodes.DUPLICATE_REQUEST, 
            "重复请求，请求ID：" + requestId);
    }

    public static OrderBusinessException parameterInvalid(String parameter, String reason) {
        return new OrderBusinessException(ErrorCodes.PARAMETER_INVALID, 
            String.format("参数错误，参数：%s，原因：%s", parameter, reason));
    }

    public static OrderBusinessException systemError(String message) {
        return new OrderBusinessException(ErrorCodes.SYSTEM_ERROR, 
            "系统错误：" + message);
    }

    public static OrderBusinessException systemError(String message, Throwable cause) {
        return new OrderBusinessException(ErrorCodes.SYSTEM_ERROR, 
            "系统错误：" + message, cause);
    }
} 
package com.gig.collide.payment.infrastructure.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付服务错误码枚举
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum PaymentErrorCode {

    // 通用错误码 (10001-10099)
    SYSTEM_ERROR("10001", "系统异常"),
    PARAM_INVALID("10002", "参数无效"),
    PAYMENT_NOT_FOUND("10003", "支付记录不存在"),
    ORDER_NOT_FOUND("10004", "订单不存在"),
    CONCURRENT_UPDATE("10005", "并发更新冲突"),
    
    // 支付创建错误 (10101-10199)
    CREATE_PAYMENT_FAILED("10101", "创建支付失败"),
    DUPLICATE_PAYMENT("10102", "重复支付"),
    INVALID_PAY_AMOUNT("10103", "支付金额无效"),
    INVALID_PAY_TYPE("10104", "支付方式无效"),
    ORDER_ALREADY_PAID("10105", "订单已支付"),
    ORDER_CANCELLED("10106", "订单已取消"),
    ORDER_EXPIRED("10107", "订单已过期"),
    
    // 支付处理错误 (10201-10299)
    PAYMENT_PROCESSING("10201", "支付处理中"),
    PAYMENT_FAILED("10202", "支付失败"),
    PAYMENT_TIMEOUT("10203", "支付超时"),
    PAYMENT_CANCELLED("10204", "支付已取消"),
    INVALID_PAYMENT_STATUS("10205", "支付状态无效"),
    PAYMENT_ALREADY_SUCCESS("10206", "支付已成功"),
    PAYMENT_ALREADY_FAILED("10207", "支付已失败"),
    
    // 第三方支付错误 (10301-10399)
    ALIPAY_CONFIG_ERROR("10301", "支付宝配置错误"),
    ALIPAY_SIGN_ERROR("10302", "支付宝签名错误"),
    ALIPAY_API_ERROR("10303", "支付宝接口调用失败"),
    WECHAT_CONFIG_ERROR("10311", "微信支付配置错误"),
    WECHAT_SIGN_ERROR("10312", "微信支付签名错误"),
    WECHAT_API_ERROR("10313", "微信支付接口调用失败"),
    UNIONPAY_CONFIG_ERROR("10321", "银联支付配置错误"),
    UNIONPAY_SIGN_ERROR("10322", "银联支付签名错误"),
    UNIONPAY_API_ERROR("10323", "银联支付接口调用失败"),
    
    // 回调处理错误 (10401-10499)
    CALLBACK_INVALID("10401", "回调数据无效"),
    CALLBACK_SIGN_ERROR("10402", "回调签名验证失败"),
    CALLBACK_DUPLICATE("10403", "重复回调"),
    CALLBACK_PROCESS_ERROR("10404", "回调处理失败"),
    CALLBACK_TIMEOUT("10405", "回调超时"),
    
    // 退款处理错误 (10501-10599)
    REFUND_NOT_ALLOWED("10501", "不允许退款"),
    REFUND_AMOUNT_INVALID("10502", "退款金额无效"),
    REFUND_ALREADY_PROCESSED("10503", "已处理退款"),
    REFUND_FAILED("10504", "退款失败"),
    REFUND_TIMEOUT("10505", "退款超时"),
    
    // 幂等性错误 (10601-10699)
    IDEMPOTENT_KEY_MISSING("10601", "幂等性Key缺失"),
    IDEMPOTENT_KEY_INVALID("10602", "幂等性Key无效"),
    IDEMPOTENT_CONFLICT("10603", "幂等性冲突"),
    IDEMPOTENT_PROCESSING("10604", "请求正在处理中"),
    
    // 限流和风控错误 (10701-10799)
    RATE_LIMIT_EXCEEDED("10701", "请求频率超限"),
    USER_PAYMENT_LIMIT("10702", "用户支付限额超限"),
    DAILY_PAYMENT_LIMIT("10703", "日支付限额超限"),
    RISK_CONTROL_REJECTED("10704", "风控拒绝"),
    SUSPICIOUS_PAYMENT("10705", "可疑支付"),
    
    // 配置和权限错误 (10801-10899)
    PAYMENT_CONFIG_ERROR("10801", "支付配置错误"),
    PAYMENT_METHOD_DISABLED("10802", "支付方式已禁用"),
    INSUFFICIENT_PERMISSION("10803", "权限不足"),
    SERVICE_UNAVAILABLE("10804", "服务不可用"),
    MAINTENANCE_MODE("10805", "维护模式");

    /**
     * 错误码
     */
    private final String code;

    /**
     * 错误消息
     */
    private final String message;

    /**
     * 根据错误码查找枚举
     * 
     * @param code 错误码
     * @return 错误码枚举
     */
    public static PaymentErrorCode getByCode(String code) {
        for (PaymentErrorCode errorCode : values()) {
            if (errorCode.getCode().equals(code)) {
                return errorCode;
            }
        }
        return SYSTEM_ERROR;
    }
} 
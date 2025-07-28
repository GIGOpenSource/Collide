package com.gig.collide.api.payment.constant;

/**
 * 支付方式枚举
 * 与payment模块保持一致
 * 
 * @author Collide Team  
 * @version 2.0
 * @since 2024-01-01
 */
public enum PaymentTypeEnum {

    /**
     * 支付宝支付
     */
    ALIPAY("支付宝支付"),
    
    /**
     * 微信支付
     */
    WECHAT("微信支付"),
    
    /**
     * 银联支付
     */
    UNIONPAY("银联支付"),
    
    /**
     * 测试支付
     */
    TEST("测试支付");

    private final String description;

    PaymentTypeEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 检查是否为第三方支付
     */
    public boolean isThirdParty() {
        return this == ALIPAY || this == WECHAT || this == UNIONPAY;
    }

    /**
     * 检查是否为测试支付
     */
    public boolean isTest() {
        return this == TEST;
    }
} 
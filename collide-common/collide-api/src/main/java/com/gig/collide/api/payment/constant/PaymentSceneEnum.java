package com.gig.collide.api.payment.constant;

/**
 * 支付场景枚举
 * 与payment模块保持一致
 * 
 * @author Collide Team  
 * @version 2.0
 * @since 2024-01-01
 */
public enum PaymentSceneEnum {

    /**
     * 网页支付
     */
    WEB("网页支付"),
    
    /**
     * 手机支付
     */
    MOBILE("手机支付"),
    
    /**
     * 应用内支付
     */
    APP("应用内支付"),
    
    /**
     * 小程序支付
     */
    MINI("小程序支付");

    private final String description;

    PaymentSceneEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 检查是否为移动端支付
     */
    public boolean isMobile() {
        return this == MOBILE || this == APP || this == MINI;
    }

    /**
     * 检查是否为PC端支付
     */
    public boolean isPC() {
        return this == WEB;
    }
} 
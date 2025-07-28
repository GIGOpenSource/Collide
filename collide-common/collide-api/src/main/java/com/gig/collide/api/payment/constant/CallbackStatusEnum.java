package com.gig.collide.api.payment.constant;

/**
 * 回调状态枚举
 * 与payment模块保持一致
 * 
 * @author Collide Team  
 * @version 2.0
 * @since 2024-01-01
 */
public enum CallbackStatusEnum {

    /**
     * 处理中
     */
    PENDING("处理中"),
    
    /**
     * 成功
     */
    SUCCESS("成功"),
    
    /**
     * 失败
     */
    FAILED("失败");

    private final String description;

    CallbackStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 检查是否为最终状态
     */
    public boolean isFinalStatus() {
        return this == SUCCESS || this == FAILED;
    }

    /**
     * 检查是否为成功状态
     */
    public boolean isSuccess() {
        return this == SUCCESS;
    }

    /**
     * 检查是否需要重试
     */
    public boolean needsRetry() {
        return this == FAILED || this == PENDING;
    }
} 
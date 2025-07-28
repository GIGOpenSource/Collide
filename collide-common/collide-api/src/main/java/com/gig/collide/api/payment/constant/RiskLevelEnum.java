package com.gig.collide.api.payment.constant;

/**
 * 风险等级枚举
 * 与payment模块保持一致
 * 
 * @author Collide Team  
 * @version 2.0
 * @since 2024-01-01
 */
public enum RiskLevelEnum {

    /**
     * 低风险
     */
    LOW("低风险"),
    
    /**
     * 中风险
     */
    MEDIUM("中风险"),
    
    /**
     * 高风险
     */
    HIGH("高风险");

    private final String description;

    RiskLevelEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 检查是否为高风险
     */
    public boolean isHighRisk() {
        return this == HIGH;
    }

    /**
     * 检查是否需要特殊处理
     */
    public boolean needsSpecialHandling() {
        return this == MEDIUM || this == HIGH;
    }

    /**
     * 获取风险分数阈值
     */
    public int getScoreThreshold() {
        switch (this) {
            case LOW:
                return 30;
            case MEDIUM:
                return 70;
            case HIGH:
                return 100;
            default:
                return 0;
        }
    }
} 
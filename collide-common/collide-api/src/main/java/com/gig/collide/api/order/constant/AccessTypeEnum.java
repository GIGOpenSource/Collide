package com.gig.collide.api.order.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 访问权限类型枚举
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum AccessTypeEnum {

    /**
     * 永久访问
     */
    PERMANENT("PERMANENT", "永久访问"),

    /**
     * 临时访问
     */
    TEMPORARY("TEMPORARY", "临时访问"),

    /**
     * 基于订阅
     */
    SUBSCRIPTION_BASED("SUBSCRIPTION_BASED", "基于订阅");

    /**
     * 枚举值
     */
    private final String code;

    /**
     * 枚举描述
     */
    private final String description;

    /**
     * 根据code获取枚举
     *
     * @param code 枚举值
     * @return 对应的枚举，如果不存在则返回null
     */
    public static AccessTypeEnum getByCode(String code) {
        for (AccessTypeEnum accessType : values()) {
            if (accessType.getCode().equals(code)) {
                return accessType;
            }
        }
        return null;
    }
} 
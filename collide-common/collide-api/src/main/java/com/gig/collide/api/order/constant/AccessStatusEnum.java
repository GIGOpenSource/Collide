package com.gig.collide.api.order.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 访问状态枚举
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum AccessStatusEnum {

    /**
     * 激活状态
     */
    ACTIVE("ACTIVE", "激活状态"),

    /**
     * 已过期
     */
    EXPIRED("EXPIRED", "已过期"),

    /**
     * 已撤销
     */
    REVOKED("REVOKED", "已撤销");

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
    public static AccessStatusEnum getByCode(String code) {
        for (AccessStatusEnum accessStatus : values()) {
            if (accessStatus.getCode().equals(code)) {
                return accessStatus;
            }
        }
        return null;
    }
} 
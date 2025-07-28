package com.gig.collide.api.order.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单类型枚举
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum OrderTypeEnum {

    /**
     * 商品购买订单
     */
    GOODS("GOODS", "商品购买"),

    /**
     * 内容直购订单
     */
    CONTENT("CONTENT", "内容直购");

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
    public static OrderTypeEnum getByCode(String code) {
        for (OrderTypeEnum orderType : values()) {
            if (orderType.getCode().equals(code)) {
                return orderType;
            }
        }
        return null;
    }
} 
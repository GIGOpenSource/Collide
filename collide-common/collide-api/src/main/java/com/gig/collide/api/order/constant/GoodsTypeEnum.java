package com.gig.collide.api.order.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 商品类型枚举
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum GoodsTypeEnum {

    /**
     * 金币类商品
     */
    COIN("COIN", "金币类"),

    /**
     * 订阅类商品
     */
    SUBSCRIPTION("SUBSCRIPTION", "订阅类");

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
    public static GoodsTypeEnum getByCode(String code) {
        for (GoodsTypeEnum goodsType : values()) {
            if (goodsType.getCode().equals(code)) {
                return goodsType;
            }
        }
        return null;
    }
} 
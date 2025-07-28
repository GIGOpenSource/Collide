package com.gig.collide.api.order.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付类型枚举
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum PayTypeEnum {

    /**
     * 支付宝支付
     */
    ALIPAY("ALIPAY", "支付宝"),

    /**
     * 微信支付
     */
    WECHAT("WECHAT", "微信支付"),

    /**
     * 测试支付
     */
    TEST("TEST", "测试支付");

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
    public static PayTypeEnum getByCode(String code) {
        for (PayTypeEnum payType : values()) {
            if (payType.getCode().equals(code)) {
                return payType;
            }
        }
        return null;
    }
} 
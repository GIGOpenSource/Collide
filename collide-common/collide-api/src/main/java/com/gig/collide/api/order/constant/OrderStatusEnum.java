package com.gig.collide.api.order.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单状态枚举
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum OrderStatusEnum {

    /**
     * 创建订单
     */
    CREATE("CREATE", "创建订单"),

    /**
     * 未付款
     */
    UNPAID("UNPAID", "未付款"),

    /**
     * 已支付
     */
    PAID("PAID", "已支付"),

    /**
     * 已取消
     */
    CANCELLED("CANCELLED", "已取消"),

    /**
     * 已退款
     */
    REFUNDED("REFUNDED", "已退款");

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
    public static OrderStatusEnum getByCode(String code) {
        for (OrderStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
} 
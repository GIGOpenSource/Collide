package com.gig.collide.api.order.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 订单取消请求 - 简洁版
 * 取消订单，更新订单状态为cancelled
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderCancelRequest implements Serializable {

    /**
     * 订单ID - 必填
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 订单号 - 可用于二次校验
     */
    private String orderNo;

    /**
     * 用户ID - 用于权限校验
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 取消原因
     */
    private String cancelReason;
} 
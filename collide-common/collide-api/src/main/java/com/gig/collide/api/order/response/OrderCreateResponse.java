package com.gig.collide.api.order.response;

import com.gig.collide.api.order.response.data.OrderInfo;
import com.gig.collide.base.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 创建订单响应
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class OrderCreateResponse extends BaseResponse {

    /**
     * 订单信息
     */
    private OrderInfo orderInfo;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 订单过期时间
     */
    private Long expireTime;

    /**
     * 支付跳转链接（如果已选择支付方式）
     */
    private String payUrl;

    /**
     * 创建时间戳
     */
    private Long createTimestamp;
} 
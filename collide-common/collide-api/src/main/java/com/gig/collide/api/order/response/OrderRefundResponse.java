package com.gig.collide.api.order.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 订单退款响应
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
public class OrderRefundResponse extends BaseResponse {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 退款单号
     */
    private String refundNo;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 退款状态: PENDING-退款处理中, SUCCESS-退款成功, FAILED-退款失败
     */
    private String refundStatus;

    /**
     * 第三方退款单号
     */
    private String thirdPartyRefundNo;

    /**
     * 退款处理时间
     */
    private Long refundTime;

    /**
     * 预计到账时间
     */
    private Long estimatedArrivalTime;
} 
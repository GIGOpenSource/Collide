package com.gig.collide.api.order.response;

import com.gig.collide.api.order.response.data.OrderInfo;
import com.gig.collide.api.order.response.data.OrderContentAssociationInfo;
import com.gig.collide.base.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 订单详情响应
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
public class OrderDetailResponse extends BaseResponse {

    /**
     * 订单基础信息
     */
    private OrderInfo orderInfo;

    /**
     * 订单关联的内容权限列表
     */
    private List<OrderContentAssociationInfo> contentAssociations;

    /**
     * 支付信息
     */
    private PaymentInfo paymentInfo;

    /**
     * 退款信息列表
     */
    private List<RefundInfo> refundList;

    /**
     * 支付信息
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class PaymentInfo {
        /**
         * 支付时间
         */
        private Long payTime;

        /**
         * 支付方式
         */
        private String payType;

        /**
         * 第三方支付单号
         */
        private String thirdPartyPayNo;
    }

    /**
     * 退款信息
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class RefundInfo {
        /**
         * 退款单号
         */
        private String refundNo;

        /**
         * 退款金额
         */
        private java.math.BigDecimal refundAmount;

        /**
         * 退款状态
         */
        private String refundStatus;

        /**
         * 退款时间
         */
        private Long refundTime;
    }
} 
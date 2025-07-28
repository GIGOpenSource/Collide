package com.gig.collide.api.payment.response;

import com.gig.collide.api.payment.response.data.PaymentMethodInfo;
import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 获取支付方式响应
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
public class PaymentMethodResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 可用支付方式列表
     */
    private List<PaymentMethodInfo> paymentMethods;

    /**
     * 默认支付方式
     */
    private PaymentMethodInfo defaultMethod;

    /**
     * 推荐支付方式
     */
    private PaymentMethodInfo recommendedMethod;
} 
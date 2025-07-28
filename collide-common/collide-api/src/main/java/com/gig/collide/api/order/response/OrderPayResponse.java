package com.gig.collide.api.order.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * 订单支付响应
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
public class OrderPayResponse extends BaseResponse {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 支付状态: PENDING-待支付, SUCCESS-支付成功, FAILED-支付失败
     */
    private String payStatus;

    /**
     * 支付跳转链接
     */
    private String payUrl;

    /**
     * 第三方支付单号
     */
    private String thirdPartyPayNo;

    /**
     * 支付参数（前端调用支付所需）
     */
    private Map<String, String> payParams;

    /**
     * 支付二维码（支付宝、微信扫码支付）
     */
    private String payQrCode;

    /**
     * 支付过期时间
     */
    private Long payExpireTime;
} 
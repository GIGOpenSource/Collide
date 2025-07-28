package com.gig.collide.api.order.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * 订单支付请求
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
public class OrderPayRequest extends BaseRequest {

    /**
     * 订单号
     */
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 支付方式: ALIPAY-支付宝, WECHAT-微信, TEST-测试支付
     */
    @NotBlank(message = "支付方式不能为空")
    private String payType;

    /**
     * 支付渠道特定参数
     */
    private Map<String, String> payParams;

    /**
     * 回调通知地址
     */
    private String notifyUrl;

    /**
     * 支付成功跳转地址
     */
    private String returnUrl;

    /**
     * 客户端IP地址
     */
    private String clientIp;

    /**
     * 设备信息
     */
    private String deviceInfo;

    /**
     * 支付场景: WEB-网页支付, APP-移动应用, H5-手机网页
     */
    private String payScene = "WEB";
} 
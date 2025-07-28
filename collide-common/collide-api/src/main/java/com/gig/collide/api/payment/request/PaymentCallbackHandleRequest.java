package com.gig.collide.api.payment.request;

import com.gig.collide.api.payment.constant.CallbackTypeEnum;
import com.gig.collide.api.payment.constant.PaymentTypeEnum;
import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Map;

/**
 * 支付回调处理请求
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCallbackHandleRequest extends BaseRequest {

    /**
     * 回调类型（必填）
     */
    @NotNull(message = "回调类型不能为空")
    private CallbackTypeEnum callbackType;

    /**
     * 回调来源（必填）
     */
    @NotNull(message = "回调来源不能为空")
    private PaymentTypeEnum callbackSource;

    /**
     * 订单号（可选）
     */
    @Size(max = 64, message = "订单号长度不能超过64个字符")
    private String orderNo;

    /**
     * 第三方交易流水号（可选）
     */
    @Size(max = 128, message = "第三方交易流水号长度不能超过128个字符")
    private String transactionNo;

    /**
     * 回调原始内容（必填）
     */
    @NotBlank(message = "回调原始内容不能为空")
    private String callbackContent;

    /**
     * 回调参数（可选）
     */
    private Map<String, String> callbackParams;

    /**
     * 回调签名（可选）
     */
    @Size(max = 512, message = "回调签名长度不能超过512个字符")
    private String callbackSignature;

    /**
     * 客户端IP地址（可选）
     */
    @Size(max = 45, message = "IP地址长度不能超过45个字符")
    private String clientIp;

    /**
     * 用户代理信息（可选）
     */
    @Size(max = 500, message = "用户代理信息长度不能超过500个字符")
    private String userAgent;

    /**
     * 请求头信息（可选）
     */
    private Map<String, String> requestHeaders;
} 
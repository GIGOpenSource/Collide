package com.gig.collide.api.order.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * 订单同步请求
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
public class OrderSyncRequest extends BaseRequest {

    /**
     * 订单号
     */
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    /**
     * 同步类型: PAYMENT-支付状态同步, REFUND-退款状态同步, ALL-全部同步
     */
    @NotBlank(message = "同步类型不能为空")
    private String syncType;

    /**
     * 第三方订单号
     */
    private String thirdPartyOrderNo;

    /**
     * 同步来源: SYSTEM-系统自动, MANUAL-手动触发
     */
    private String syncSource = "MANUAL";

    /**
     * 扩展参数
     */
    private Map<String, String> extParams;

    /**
     * 是否强制同步
     */
    private Boolean forceSync = false;
} 
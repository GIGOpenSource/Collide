package com.gig.collide.api.pro.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 付费用户续费请求
 * @author GIG
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProRenewRequest extends BaseRequest {

    /**
     * 用户ID
     */
    @NotNull(message = "userId不能为空")
    private Long userId;

    /**
     * 续费套餐类型
     */
    private String packageType;

    /**
     * 续费金额
     */
    private Long amount;

    /**
     * 续费时长（月）
     */
    private Integer duration;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 是否自动续费
     */
    private Boolean autoRenewal;
} 
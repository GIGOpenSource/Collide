package com.gig.collide.api.pro.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 用户升级到付费用户请求
 * @author GIG
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProUpgradeRequest extends BaseRequest {

    /**
     * 用户ID
     */
    @NotNull(message = "userId不能为空")
    private Long userId;

    /**
     * 付费套餐类型
     */
    private String packageType;

    /**
     * 付费金额
     */
    private Long amount;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 付费时长（月）
     */
    private Integer duration;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 是否自动开通默认权限
     */
    private Boolean autoGrantPermissions = true;
} 
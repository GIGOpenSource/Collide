package com.gig.collide.api.user.request;

import lombok.Data;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 钱包操作请求
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
public class WalletOperationRequest {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 操作金额
     */
    @NotNull(message = "操作金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于0.01")
    private BigDecimal amount;

    /**
     * 操作类型：recharge（充值）、withdraw（提现）、freeze（冻结）、unfreeze（解冻）
     */
    @NotNull(message = "操作类型不能为空")
    private String operationType;

    /**
     * 操作描述
     */
    private String description;

    /**
     * 业务关联ID（如订单ID）
     */
    private String businessId;
}
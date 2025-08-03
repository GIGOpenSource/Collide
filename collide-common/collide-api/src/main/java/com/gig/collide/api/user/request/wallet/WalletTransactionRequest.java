package com.gig.collide.api.user.request.wallet;

import java.io.Serializable;
import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 钱包交易请求
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransactionRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 交易类型：cash_deposit、cash_consume、coin_grant、coin_consume
     */
    @NotBlank(message = "交易类型不能为空")
    private String transactionType;

    /**
     * 现金金额（现金交易时使用�?
     */
    @DecimalMin(value = "0.01", message = "现金金额必须大于0")
    private BigDecimal cashAmount;

    /**
     * 金币数量（金币交易时使用�?
     */
    @Min(value = 1, message = "金币数量必须大于0")
    private Long coinAmount;

    /**
     * 业务ID（用于幂等性控制）
     */
    @NotBlank(message = "业务ID不能为空")
    private String businessId;

    /**
     * 交易描述
     */
    @NotBlank(message = "交易描述不能为空")
    private String description;

    /**
     * 业务类型（用于分类统计）
     */
    private String businessType;
}

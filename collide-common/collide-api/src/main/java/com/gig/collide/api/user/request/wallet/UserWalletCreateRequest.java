package com.gig.collide.api.user.request.wallet;

import java.io.Serializable;
import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 用户钱包创建请求 - 对应 t_user_wallet �?
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWalletCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 初始现金余额
     */
    @DecimalMin(value = "0.00", message = "现金余额不能为负数")
    private BigDecimal balance = BigDecimal.ZERO;

    /**
     * 初始金币余额
     */
    @Min(value = 0, message = "金币余额不能为负数")
    private Long coinBalance = 0L;

    /**
     * 钱包状态：active、frozen
     */
    private String status = "active";
}

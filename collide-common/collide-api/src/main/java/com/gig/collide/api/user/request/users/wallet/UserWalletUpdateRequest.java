package com.gig.collide.api.user.request.users.wallet;

import java.io.Serializable;
import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 用户钱包更新请求 - 对应 t_user_wallet �?
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWalletUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 钱包ID
     */
    @NotNull(message = "钱包ID不能为空")
    private Long id;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 现金余额
     */
    private BigDecimal balance;

    /**
     * 冻结金额
     */
    private BigDecimal frozenAmount;

    /**
     * 金币余额
     */
    private Long coinBalance;

    /**
     * 累计获得金币
     */
    private Long coinTotalEarned;

    /**
     * 累计消费金币
     */
    private Long coinTotalSpent;

    /**
     * 总收�?
     */
    private BigDecimal totalIncome;

    /**
     * 总支�?
     */
    private BigDecimal totalExpense;

    /**
     * 钱包状态：active、frozen
     */
    private String status;
}

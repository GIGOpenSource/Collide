package com.gig.collide.api.user.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 钱包响应
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
public class WalletResponse {

    /**
     * 钱包ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 总余额
     */
    private BigDecimal balance;

    /**
     * 冻结金额
     */
    private BigDecimal frozenAmount;

    /**
     * 可用余额
     */
    private BigDecimal availableBalance;

    /**
     * 总收入
     */
    private BigDecimal totalIncome;

    /**
     * 总支出
     */
    private BigDecimal totalExpense;

    /**
     * 状态：active、frozen
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
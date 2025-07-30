package com.gig.collide.users.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户钱包实体
 * 对应t_user_wallet表
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
@TableName("t_user_wallet")
public class UserWallet {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 余额
     */
    private BigDecimal balance;

    /**
     * 冻结金额
     */
    private BigDecimal frozenAmount;

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

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 初始化默认值
     */
    public void initDefaults() {
        if (this.balance == null) {
            this.balance = BigDecimal.ZERO;
        }
        if (this.frozenAmount == null) {
            this.frozenAmount = BigDecimal.ZERO;
        }
        if (this.totalIncome == null) {
            this.totalIncome = BigDecimal.ZERO;
        }
        if (this.totalExpense == null) {
            this.totalExpense = BigDecimal.ZERO;
        }
        if (this.status == null) {
            this.status = "active";
        }
    }

    /**
     * 计算可用余额
     */
    public BigDecimal getAvailableBalance() {
        return balance.subtract(frozenAmount);
    }

    /**
     * 是否有足够余额
     */
    public boolean hasSufficientBalance(BigDecimal amount) {
        return getAvailableBalance().compareTo(amount) >= 0;
    }
}
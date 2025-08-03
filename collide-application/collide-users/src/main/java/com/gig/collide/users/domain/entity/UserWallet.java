package com.gig.collide.users.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gig.collide.api.user.constant.WalletStatusConstant;
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
     * 金币余额
     */
    private Long coinBalance;

    /**
     * 金币总收入
     */
    private Long coinTotalEarned;

    /**
     * 金币总支出
     */
    private Long coinTotalSpent;

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
        if (this.coinBalance == null) {
            this.coinBalance = 0L;
        }
        if (this.coinTotalEarned == null) {
            this.coinTotalEarned = 0L;
        }
        if (this.coinTotalSpent == null) {
            this.coinTotalSpent = 0L;
        }
        if (this.status == null) {
            this.status = WalletStatusConstant.ACTIVE;
        }
        if (this.createTime == null) {
            this.createTime = LocalDateTime.now();
        }
        if (this.updateTime == null) {
            this.updateTime = LocalDateTime.now();
        }
    }

    /**
     * 计算可用余额
     */
    public BigDecimal getAvailableBalance() {
        return balance.subtract(frozenAmount);
    }

    /**
     * 是否有足够现金余额
     */
    public boolean hasSufficientBalance(BigDecimal amount) {
        return getAvailableBalance().compareTo(amount) >= 0;
    }

    /**
     * 是否有足够金币余额
     */
    public boolean hasSufficientCoinBalance(Long amount) {
        return this.coinBalance >= amount;
    }

    /**
     * 检查钱包是否激活
     */
    public boolean isActive() {
        return WalletStatusConstant.isActiveStatus(this.status);
    }

    /**
     * 检查钱包是否冻结
     */
    public boolean isFrozen() {
        return WalletStatusConstant.isFrozenStatus(this.status);
    }

    /**
     * 增加现金余额
     */
    public void addBalance(BigDecimal amount) {
        this.balance = this.balance.add(amount);
        this.totalIncome = this.totalIncome.add(amount);
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 扣除现金余额
     */
    public boolean deductBalance(BigDecimal amount) {
        if (!hasSufficientBalance(amount)) {
            return false;
        }
        this.balance = this.balance.subtract(amount);
        this.totalExpense = this.totalExpense.add(amount);
        this.updateTime = LocalDateTime.now();
        return true;
    }

    /**
     * 增加金币余额
     */
    public void addCoinBalance(Long amount) {
        this.coinBalance += amount;
        this.coinTotalEarned += amount;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 扣除金币余额
     */
    public boolean deductCoinBalance(Long amount) {
        if (!hasSufficientCoinBalance(amount)) {
            return false;
        }
        this.coinBalance -= amount;
        this.coinTotalSpent += amount;
        this.updateTime = LocalDateTime.now();
        return true;
    }

    /**
     * 冻结指定金额
     */
    public boolean freezeAmount(BigDecimal amount) {
        if (!hasSufficientBalance(amount)) {
            return false;
        }
        this.frozenAmount = this.frozenAmount.add(amount);
        this.updateTime = LocalDateTime.now();
        return true;
    }

    /**
     * 解冻指定金额
     */
    public void unfreezeAmount(BigDecimal amount) {
        BigDecimal unfreezeAmount = amount.min(this.frozenAmount);
        this.frozenAmount = this.frozenAmount.subtract(unfreezeAmount);
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 更新钱包修改时间
     */
    public void updateModifyTime() {
        this.updateTime = LocalDateTime.now();
    }
}
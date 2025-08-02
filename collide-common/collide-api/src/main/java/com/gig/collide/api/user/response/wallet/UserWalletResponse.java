package com.gig.collide.api.user.response.wallet;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 用户钱包响应 - 对应 t_user_wallet �?
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWalletResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 钱包ID
     */
    private Long id;

    /**
     * 用户ID
     */
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
     * 可用现金余额
     */
    private BigDecimal availableBalance;

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

    /**
     * 状态描�?
     */
    private String statusDesc;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getFrozenAmount() {
        return frozenAmount;
    }

    /**
     * 设置冻结金额并自动计算可用余额
     */
    public void setFrozenAmount(BigDecimal frozenAmount) {
        this.frozenAmount = frozenAmount;
        // 计算可用余额
        if (balance != null && frozenAmount != null) {
            this.availableBalance = balance.subtract(frozenAmount);
        }
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(BigDecimal availableBalance) {
        this.availableBalance = availableBalance;
    }

    public Long getCoinBalance() {
        return coinBalance;
    }

    public void setCoinBalance(Long coinBalance) {
        this.coinBalance = coinBalance;
    }

    public Long getCoinTotalEarned() {
        return coinTotalEarned;
    }

    public void setCoinTotalEarned(Long coinTotalEarned) {
        this.coinTotalEarned = coinTotalEarned;
    }

    public Long getCoinTotalSpent() {
        return coinTotalSpent;
    }

    public void setCoinTotalSpent(Long coinTotalSpent) {
        this.coinTotalSpent = coinTotalSpent;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(BigDecimal totalExpense) {
        this.totalExpense = totalExpense;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        this.statusDesc = "active".equals(status) ? "正常" : "frozen".equals(status) ? "冻结" : "未知";
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "UserWalletResponse{" +
                "id=" + id +
                ", userId=" + userId +
                ", balance=" + balance +
                ", frozenAmount=" + frozenAmount +
                ", availableBalance=" + availableBalance +
                ", coinBalance=" + coinBalance +
                ", coinTotalEarned=" + coinTotalEarned +
                ", coinTotalSpent=" + coinTotalSpent +
                ", totalIncome=" + totalIncome +
                ", totalExpense=" + totalExpense +
                ", status='" + status + '\'' +
                ", statusDesc='" + statusDesc + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}

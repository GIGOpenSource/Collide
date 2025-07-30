package com.gig.collide.users.domain.service;

import com.gig.collide.users.domain.entity.UserWallet;

import java.math.BigDecimal;

/**
 * 钱包服务接口
 * 
 * @author GIG Team
 * @version 2.0.0
 */
public interface WalletService {

    /**
     * 根据用户ID获取钱包
     */
    UserWallet getWalletByUserId(Long userId);

    /**
     * 创建用户钱包
     */
    UserWallet createWallet(Long userId);

    /**
     * 充值
     */
    UserWallet recharge(Long userId, BigDecimal amount, String description);

    /**
     * 提现
     */
    UserWallet withdraw(Long userId, BigDecimal amount, String description);

    /**
     * 冻结金额
     */
    UserWallet freezeAmount(Long userId, BigDecimal amount, String description);

    /**
     * 解冻金额
     */
    UserWallet unfreezeAmount(Long userId, BigDecimal amount, String description);

    /**
     * 检查余额是否充足
     */
    boolean checkBalance(Long userId, BigDecimal amount);

    /**
     * 扣款（内部使用）
     */
    boolean deductBalance(Long userId, BigDecimal amount, String businessId, String description);

    /**
     * 充值（内部使用）
     */
    boolean addBalance(Long userId, BigDecimal amount, String businessId, String description);

    /**
     * 更新钱包状态
     */
    void updateWalletStatus(Long userId, String status);
}
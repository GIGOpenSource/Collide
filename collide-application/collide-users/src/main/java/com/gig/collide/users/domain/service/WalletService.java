package com.gig.collide.users.domain.service;

import com.gig.collide.users.domain.entity.UserWallet;

import java.math.BigDecimal;

/**
 * 钱包服务接口 - 安全版
 * 基于users-simple.sql设计：每个用户只有一个钱包
 * 金额变动只能通过订单系统的内部方法进行
 * 
 * @author GIG Team
 * @version 2.0.0 (安全版 - 单钱包)
 */
public interface WalletService {

    // =================== 钱包查询功能 ===================

    /**
     * 根据用户ID获取钱包
     */
    UserWallet getWalletByUserId(Long userId);

    /**
     * 检查余额是否充足
     */
    boolean checkBalance(Long userId, BigDecimal amount);

    /**
     * 更新钱包状态
     */
    void updateWalletStatus(Long userId, String status);

    // =================== 钱包管理功能 ===================

    /**
     * 创建钱包（仅在用户注册时自动调用）
     */
    UserWallet createWallet(Long userId);

    // =================== 内部方法（仅供订单系统调用） ===================

    /**
     * 扣款（仅供订单系统内部使用）
     * 当用户使用金币支付订单时调用
     */
    boolean deductBalance(Long userId, BigDecimal amount, String businessId, String description);

    /**
     * 充值（仅供订单系统内部使用）
     * 当用户购买金币或获得金币奖励时调用
     */
    boolean addBalance(Long userId, BigDecimal amount, String businessId, String description);

    /**
     * 冻结金额（订单锁定金额时使用）
     */
    boolean freezeAmount(Long userId, BigDecimal amount, String businessId, String description);

    /**
     * 解冻金额（订单取消或完成时使用）
     */
    boolean unfreezeAmount(Long userId, BigDecimal amount, String businessId, String description);
}
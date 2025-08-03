package com.gig.collide.users.domain.service;

import com.gig.collide.api.user.request.wallet.UserWalletQueryRequest;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.users.domain.entity.UserWallet;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户钱包领域服务接口 - 对应 t_user_wallet 表
 * 负责用户钱包管理（现金和金币）
 * 
 * @author GIG Team
 * @version 2.0.0
 */
public interface UserWalletService {

    /**
     * 创建用户钱包
     */
    UserWallet createWallet(UserWallet userWallet);

    /**
     * 根据用户ID查询钱包
     */
    UserWallet getWalletByUserId(Long userId);

    /**
     * 批量查询用户钱包
     */
    List<UserWallet> batchGetWallets(List<Long> userIds);

    /**
     * 现金充值
     */
    boolean recharge(Long userId, BigDecimal amount, String businessId, String description);

    /**
     * 现金提现
     */
    boolean withdraw(Long userId, BigDecimal amount, String businessId, String description);

    /**
     * 现金转账
     */
    boolean transfer(Long fromUserId, Long toUserId, BigDecimal amount, String description);

    /**
     * 现金消费
     */
    boolean consume(Long userId, BigDecimal amount, String businessId, String description);

    /**
     * 金币充值
     */
    boolean rechargeCoin(Long userId, Long amount, String businessId, String description);

    /**
     * 金币消费
     */
    boolean consumeCoin(Long userId, Long amount, String businessId, String description);

    /**
     * 金币转账
     */
    boolean transferCoin(Long fromUserId, Long toUserId, Long amount, String description);

    /**
     * 冻结资金
     */
    boolean freezeAmount(Long userId, BigDecimal amount, String reason);

    /**
     * 解冻资金
     */
    boolean unfreezeAmount(Long userId, BigDecimal amount, String reason);

    /**
     * 冻结钱包
     */
    boolean freezeWallet(Long userId, String reason);

    /**
     * 解冻钱包
     */
    boolean unfreezeWallet(Long userId, String reason);

    /**
     * 更新钱包状态
     */
    boolean updateWalletStatus(Long userId, String status);

    /**
     * 检查余额是否充足
     */
    boolean checkSufficientBalance(Long userId, BigDecimal amount);

    /**
     * 检查金币余额是否充足
     */
    boolean checkSufficientCoinBalance(Long userId, Long amount);

    /**
     * 获取可用余额
     */
    BigDecimal getAvailableBalance(Long userId);

    /**
     * 获取金币余额
     */
    Long getCoinBalance(Long userId);

    /**
     * 分页查询钱包信息
     */
    PageResponse<UserWallet> queryWallets(UserWalletQueryRequest request);

    /**
     * 批量初始化钱包
     */
    List<UserWallet> batchInitializeWallets(List<Long> userIds);

    /**
     * 同步钱包数据
     */
    boolean syncWalletData(Long userId);

    /**
     * 批量同步钱包数据
     */
    boolean batchSyncWallets(List<Long> userIds);

    /**
     * 钱包余额对账
     */
    boolean reconcileBalance(Long userId);

    /**
     * 检查钱包是否正常
     */
    boolean isWalletHealthy(Long userId);

    /**
     * 删除钱包（谨慎使用）
     */
    boolean deleteWallet(Long userId);
}
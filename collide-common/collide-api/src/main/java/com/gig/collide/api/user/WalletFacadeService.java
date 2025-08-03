package com.gig.collide.api.user;

import com.gig.collide.web.vo.Result;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 钱包模块对外服务接口
 * 提供统一的钱包管理功能入口
 *
 * @author GIG Team
 * @version 2.0.0 (扩展版)
 * @since 2024-01-31
 */
public interface WalletFacadeService {

    // =================== 金币管理 ===================

    /**
     * 发放金币奖励
     *
     * @param userId      用户ID
     * @param amount      金币数量
     * @param type        奖励类型
     * @param description 描述
     * @return 操作结果
     */
    Result<Void> grantCoinReward(Long userId, Long amount, String type, String description);

    /**
     * 消费金币
     *
     * @param userId      用户ID  
     * @param amount      消费金币数量
     * @param type        消费类型
     * @param description 描述
     * @return 操作结果
     */
    Result<Void> consumeCoin(Long userId, Long amount, String type, String description);

    /**
     * 查询金币余额
     *
     * @param userId 用户ID
     * @return 金币余额
     */
    Result<Long> getCoinBalance(Long userId);

    /**
     * 查询金币交易记录
     *
     * @param userId      用户ID
     * @param currentPage 当前页码
     * @param pageSize    页面大小
     * @return 交易记录
     */
    Result<Map<String, Object>> getCoinTransactions(Long userId, Integer currentPage, Integer pageSize);

    // =================== 现金钱包管理 ===================

    /**
     * 充值现金
     *
     * @param userId      用户ID
     * @param amount      充值金额
     * @param description 描述
     * @return 操作结果
     */
    Result<Void> depositCash(Long userId, BigDecimal amount, String description);

    /**
     * 消费现金
     *
     * @param userId      用户ID
     * @param amount      消费金额
     * @param description 描述
     * @return 操作结果
     */
    Result<Void> consumeCash(Long userId, BigDecimal amount, String description);

    /**
     * 查询现金余额
     *
     * @param userId 用户ID
     * @return 现金余额
     */
    Result<BigDecimal> getCashBalance(Long userId);

    /**
     * 查询现金交易记录
     *
     * @param userId      用户ID
     * @param currentPage 当前页码
     * @param pageSize    页面大小
     * @return 交易记录
     */
    Result<Map<String, Object>> getCashTransactions(Long userId, Integer currentPage, Integer pageSize);

    // =================== 钱包统计 ===================

    /**
     * 获取钱包统计信息
     *
     * @param userId 用户ID
     * @return 统计信息
     */
    Result<Map<String, Object>> getWalletStatistics(Long userId);

    /**
     * 验证金币余额是否充足
     *
     * @param userId 用户ID
     * @param amount 需要金币数量
     * @return 验证结果
     */
    Result<Boolean> checkCoinBalance(Long userId, Long amount);

    /**
     * 验证现金余额是否充足
     *
     * @param userId 用户ID
     * @param amount 需要现金金额
     * @return 验证结果
     */
    Result<Boolean> checkCashBalance(Long userId, BigDecimal amount);
}
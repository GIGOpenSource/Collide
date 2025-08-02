package com.gig.collide.api.user;

import com.gig.collide.api.user.request.wallet.UserWalletCreateRequest;
import com.gig.collide.api.user.request.wallet.UserWalletUpdateRequest;
import com.gig.collide.api.user.request.wallet.UserWalletQueryRequest;
import com.gig.collide.api.user.request.wallet.WalletTransactionRequest;
import com.gig.collide.api.user.response.wallet.UserWalletResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import java.math.BigDecimal;

/**
 * 用户钱包服务接口 - 对应 t_user_wallet 表
 * 支持现金和金币双重钱包系统
 * 
 * @author GIG Team
 * @version 2.0.0
 */
public interface UserWalletFacadeService {

    /**
     * 创建用户钱包
     * 
     * @param request 钱包创建请求
     * @return 创建结果
     */
    Result<UserWalletResponse> createWallet(UserWalletCreateRequest request);

    /**
     * 更新钱包信息
     * 
     * @param request 钱包更新请求
     * @return 更新结果
     */
    Result<UserWalletResponse> updateWallet(UserWalletUpdateRequest request);

    /**
     * 根据用户ID查询钱包信息
     * 
     * @param userId 用户ID
     * @return 钱包信息
     */
    Result<UserWalletResponse> getWalletByUserId(Long userId);

    /**
     * 批量查询用户钱包信息
     * 
     * @param userIds 用户ID列表
     * @return 钱包信息列表
     */
    Result<java.util.List<UserWalletResponse>> batchGetWallets(java.util.List<Long> userIds);

    // =================== 现金钱包操作 ===================

    /**
     * 充值现金
     * 
     * @param request 交易请求
     * @return 操作结果
     */
    Result<UserWalletResponse> depositCash(WalletTransactionRequest request);

    /**
     * 现金消费
     * 
     * @param request 交易请求
     * @return 操作结果
     */
    Result<UserWalletResponse> consumeCash(WalletTransactionRequest request);

    /**
     * 冻结现金
     * 
     * @param userId 用户ID
     * @param amount 冻结金额
     * @param businessId 业务ID
     * @param description 描述
     * @return 冻结结果
     */
    Result<UserWalletResponse> freezeCash(Long userId, BigDecimal amount, String businessId, String description);

    /**
     * 解冻现金
     * 
     * @param userId 用户ID
     * @param amount 解冻金额
     * @param businessId 业务ID
     * @param description 描述
     * @return 解冻结果
     */
    Result<UserWalletResponse> unfreezeCash(Long userId, BigDecimal amount, String businessId, String description);

    /**
     * 检查现金余额是否充足
     * 
     * @param userId 用户ID
     * @param amount 需要金额
     * @return 是否充足
     */
    Result<Boolean> checkCashBalance(Long userId, BigDecimal amount);

    /**
     * 获取现金余额
     * 
     * @param userId 用户ID
     * @return 现金余额
     */
    Result<BigDecimal> getCashBalance(Long userId);

    /**
     * 获取可用现金余额（余额 - 冻结金额）
     * 
     * @param userId 用户ID
     * @return 可用现金余额
     */
    Result<BigDecimal> getAvailableCashBalance(Long userId);

    // =================== 金币钱包操作 ===================

    /**
     * 发放金币奖励
     * 
     * @param request 交易请求
     * @return 操作结果
     */
    Result<UserWalletResponse> grantCoinReward(WalletTransactionRequest request);

    /**
     * 消费金币
     * 
     * @param request 交易请求
     * @return 操作结果
     */
    Result<UserWalletResponse> consumeCoin(WalletTransactionRequest request);

    /**
     * 检查金币余额是否充足
     * 
     * @param userId 用户ID
     * @param amount 需要金币数量
     * @return 是否充足
     */
    Result<Boolean> checkCoinBalance(Long userId, Long amount);

    /**
     * 获取金币余额
     * 
     * @param userId 用户ID
     * @return 金币余额
     */
    Result<Long> getCoinBalance(Long userId);

    // =================== 钱包管理操作 ===================

    /**
     * 更新钱包状态
     * 
     * @param userId 用户ID
     * @param status 状态：active、frozen
     * @return 更新结果
     */
    Result<Void> updateWalletStatus(Long userId, String status);

    /**
     * 冻结钱包
     * 
     * @param userId 用户ID
     * @param reason 冻结原因
     * @return 冻结结果
     */
    Result<Void> freezeWallet(Long userId, String reason);

    /**
     * 解冻钱包
     * 
     * @param userId 用户ID
     * @param reason 解冻原因
     * @return 解冻结果
     */
    Result<Void> unfreezeWallet(Long userId, String reason);

    /**
     * 分页查询钱包信息
     * 
     * @param request 查询请求
     * @return 分页钱包列表
     */
    Result<PageResponse<UserWalletResponse>> queryWallets(UserWalletQueryRequest request);

    // =================== 统计查询操作 ===================

    /**
     * 获取钱包统计信息
     * 
     * @param userId 用户ID
     * @return 统计信息
     */
    Result<java.util.Map<String, Object>> getWalletStatistics(Long userId);

    /**
     * 获取平台钱包总统计
     * 
     * @return 平台统计数据
     */
    Result<java.util.Map<String, Object>> getPlatformWalletStats();

    /**
     * 获取现金余额排行榜
     * 
     * @param limit 返回数量限制
     * @return 钱包列表
     */
    Result<java.util.List<UserWalletResponse>> getCashBalanceRanking(Integer limit);

    /**
     * 获取金币余额排行榜
     * 
     * @param limit 返回数量限制
     * @return 钱包列表
     */
    Result<java.util.List<UserWalletResponse>> getCoinBalanceRanking(Integer limit);

    // =================== 内部服务接口 ===================

    /**
     * 钱包转账（内部接口）
     * 
     * @param fromUserId 转出用户ID
     * @param toUserId 转入用户ID
     * @param amount 转账金额
     * @param businessId 业务ID
     * @param description 描述
     * @return 转账结果
     */
    Result<Void> transferCash(Long fromUserId, Long toUserId, BigDecimal amount, String businessId, String description);

    /**
     * 金币转赠（内部接口）
     * 
     * @param fromUserId 转出用户ID
     * @param toUserId 转入用户ID
     * @param amount 转赠金币数量
     * @param businessId 业务ID
     * @param description 描述
     * @return 转赠结果
     */
    Result<Void> transferCoin(Long fromUserId, Long toUserId, Long amount, String businessId, String description);

    /**
     * 删除钱包（谨慎使用）
     * 
     * @param userId 用户ID
     * @return 删除结果
     */
    Result<Void> deleteWallet(Long userId);

    /**
     * 检查钱包是否存在
     * 
     * @param userId 用户ID
     * @return 是否存在
     */
    Result<Boolean> checkWalletExists(Long userId);
}
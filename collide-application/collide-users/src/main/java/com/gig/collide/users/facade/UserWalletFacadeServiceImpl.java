package com.gig.collide.users.facade;

import com.gig.collide.api.user.UserWalletFacadeService;
import com.gig.collide.api.user.request.wallet.UserWalletCreateRequest;
import com.gig.collide.api.user.request.wallet.UserWalletQueryRequest;
import com.gig.collide.api.user.request.wallet.UserWalletUpdateRequest;
import com.gig.collide.api.user.request.wallet.WalletTransactionRequest;
import com.gig.collide.api.user.response.wallet.UserWalletResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.users.domain.entity.UserWallet;
import com.gig.collide.users.domain.service.UserWalletService;
import com.gig.collide.users.infrastructure.cache.UserCacheConstant;
import com.gig.collide.web.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.apache.dubbo.config.annotation.DubboService;
import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheUpdate;
import com.alicp.jetcache.anno.CacheType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户钱包门面服务实现 - 对应 t_user_wallet 表
 * Dubbo独立微服务提供者 - 负责用户钱包管理（现金和金币）
 * 
 * @author GIG Team
 * @version 2.0.0 (Dubbo微服务版 - 6表架构)
 */
@Slf4j
@DubboService(version = "1.0.0", interfaceClass = UserWalletFacadeService.class)
@RequiredArgsConstructor
public class UserWalletFacadeServiceImpl implements UserWalletFacadeService {

    private final UserWalletService userWalletService;

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_WALLET_LIST_CACHE)
    public Result<UserWalletResponse> createWallet(UserWalletCreateRequest request) {
        try {
            log.info("创建用户钱包请求: userId={}", request.getUserId());
            
            UserWallet userWallet = new UserWallet();
            BeanUtils.copyProperties(request, userWallet);
            
            UserWallet savedWallet = userWalletService.createWallet(userWallet);
            UserWalletResponse response = convertToResponse(savedWallet);
            
            log.info("用户钱包创建成功: userId={}", savedWallet.getUserId());
            return Result.success(response);
        } catch (Exception e) {
            log.error("创建用户钱包失败", e);
            return Result.error("WALLET_CREATE_ERROR", "创建用户钱包失败: " + e.getMessage());
        }
    }

    @Override
    @CacheUpdate(name = UserCacheConstant.USER_WALLET_DETAIL_CACHE,
                 key = UserCacheConstant.USER_WALLET_DETAIL_KEY,
                 value = "#result.data")
    @CacheInvalidate(name = UserCacheConstant.USER_WALLET_LIST_CACHE)
    public Result<UserWalletResponse> updateWallet(UserWalletUpdateRequest request) {
        try {
            log.info("更新用户钱包请求: userId={}", request.getUserId());
            
            UserWallet wallet = userWalletService.getWalletByUserId(request.getUserId());
            if (wallet == null) {
                return Result.error("WALLET_NOT_FOUND", "用户钱包不存在");
            }
            
            BeanUtils.copyProperties(request, wallet);
            // 简化实现 - 只更新状态等非敏感字段
            boolean success = userWalletService.updateWalletStatus(request.getUserId(), request.getStatus());
            if (success) {
                UserWallet updatedWallet = userWalletService.getWalletByUserId(request.getUserId());
                UserWalletResponse response = convertToResponse(updatedWallet);
                return Result.success(response);
            } else {
                return Result.error("WALLET_UPDATE_ERROR", "更新钱包失败");
            }
        } catch (Exception e) {
            log.error("更新用户钱包失败", e);
            return Result.error("WALLET_UPDATE_ERROR", "更新用户钱包失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = UserCacheConstant.USER_WALLET_DETAIL_CACHE,
            key = UserCacheConstant.USER_WALLET_DETAIL_KEY,
            expire = UserCacheConstant.USER_WALLET_DETAIL_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<UserWalletResponse> getWalletByUserId(Long userId) {
        try {
            log.debug("获取用户钱包: userId={}", userId);
            
            UserWallet wallet = userWalletService.getWalletByUserId(userId);
            if (wallet == null) {
                // 自动创建钱包
                UserWallet newWallet = new UserWallet();
                newWallet.setUserId(userId);
                wallet = userWalletService.createWallet(newWallet);
                log.info("为用户自动创建钱包: userId={}", userId);
            }
            
            UserWalletResponse response = convertToResponse(wallet);
            return Result.success(response);
        } catch (Exception e) {
            log.error("查询用户钱包失败", e);
            return Result.error("WALLET_NOT_FOUND","查询用户钱包失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<UserWalletResponse>> batchGetWallets(List<Long> userIds) {
        try {
            List<UserWallet> wallets = userWalletService.batchGetWallets(userIds);
            List<UserWalletResponse> responses = wallets.stream()
                    .map(this::convertToResponse)
                    .toList();
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("批量查询用户钱包失败", e);
            return Result.error("BATCH_GET_WALLETS_ERROR", "批量查询用户钱包失败: " + e.getMessage());
        }
    }

    // =================== 现金钱包操作 ===================

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_WALLET_DETAIL_CACHE)
    public Result<UserWalletResponse> depositCash(WalletTransactionRequest request) {
        try {
            boolean success = userWalletService.recharge(
                request.getUserId(), 
                request.getCashAmount(), 
                request.getBusinessId(), 
                request.getDescription()
            );
            if (success) {
                UserWallet wallet = userWalletService.getWalletByUserId(request.getUserId());
                UserWalletResponse response = convertToResponse(wallet);
                return Result.success(response);
            } else {
                return Result.error("DEPOSIT_ERROR", "现金充值失败：钱包状态异常");
            }
        } catch (Exception e) {
            log.error("现金充值失败", e);
            return Result.error("DEPOSIT_ERROR", "现金充值失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_WALLET_DETAIL_CACHE)
    public Result<UserWalletResponse> consumeCash(WalletTransactionRequest request) {
        try {
            boolean success = userWalletService.consume(
                request.getUserId(), 
                request.getCashAmount(), 
                request.getBusinessId(), 
                request.getDescription()
            );
            if (success) {
                UserWallet wallet = userWalletService.getWalletByUserId(request.getUserId());
                UserWalletResponse response = convertToResponse(wallet);
                return Result.success(response);
            } else {
                return Result.error("CONSUME_ERROR", "现金消费失败：余额不足或钱包状态异常");
            }
        } catch (Exception e) {
            log.error("现金消费失败", e);
            return Result.error("CONSUME_ERROR", "现金消费失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_WALLET_DETAIL_CACHE)
    public Result<UserWalletResponse> freezeCash(Long userId, BigDecimal amount, String businessId, String description) {
        try {
            boolean success = userWalletService.freezeAmount(userId, amount, description);
            if (success) {
                UserWallet wallet = userWalletService.getWalletByUserId(userId);
                UserWalletResponse response = convertToResponse(wallet);
                return Result.success(response);
            } else {
                return Result.error("FREEZE_AMOUNT_ERROR", "冻结资金失败：余额不足");
            }
        } catch (Exception e) {
            log.error("冻结资金失败", e);
            return Result.error("FREEZE_AMOUNT_ERROR", "冻结资金失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_WALLET_DETAIL_CACHE)
    public Result<UserWalletResponse> unfreezeCash(Long userId, BigDecimal amount, String businessId, String description) {
        try {
            boolean success = userWalletService.unfreezeAmount(userId, amount, description);
            if (success) {
                UserWallet wallet = userWalletService.getWalletByUserId(userId);
                UserWalletResponse response = convertToResponse(wallet);
                return Result.success(response);
            } else {
                return Result.error("UNFREEZE_AMOUNT_ERROR", "解冻资金失败：冻结金额不足");
            }
        } catch (Exception e) {
            log.error("解冻资金失败", e);
            return Result.error("UNFREEZE_AMOUNT_ERROR", "解冻资金失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> checkCashBalance(Long userId, BigDecimal amount) {
        try {
            boolean sufficient = userWalletService.checkSufficientBalance(userId, amount);
            return Result.success(sufficient);
        } catch (Exception e) {
            log.error("检查现金余额是否充足失败", e);
            return Result.error("CHECK_CASH_BALANCE_ERROR", "检查现金余额失败: " + e.getMessage());
        }
    }

    @Override
    public Result<BigDecimal> getCashBalance(Long userId) {
        try {
            BigDecimal balance = userWalletService.getAvailableBalance(userId);
            return Result.success(balance);
        } catch (Exception e) {
            log.error("获取现金余额失败", e);
            return Result.error("GET_CASH_BALANCE_ERROR", "获取现金余额失败: " + e.getMessage());
        }
    }

    @Override
    public Result<BigDecimal> getAvailableCashBalance(Long userId) {
        try {
            BigDecimal balance = userWalletService.getAvailableBalance(userId);
            return Result.success(balance);
        } catch (Exception e) {
            log.error("获取可用现金余额失败", e);
            return Result.error("GET_AVAILABLE_BALANCE_ERROR", "获取可用余额失败: " + e.getMessage());
        }
    }

    // =================== 金币钱包操作 ===================

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_WALLET_DETAIL_CACHE)
    public Result<UserWalletResponse> grantCoinReward(WalletTransactionRequest request) {
        try {
            boolean success = userWalletService.rechargeCoin(
                request.getUserId(), 
                request.getCoinAmount(), 
                request.getBusinessId(), 
                "coin reward: " + request.getDescription()
            );
            if (success) {
                UserWallet wallet = userWalletService.getWalletByUserId(request.getUserId());
                UserWalletResponse response = convertToResponse(wallet);
                return Result.success(response);
            } else {
                return Result.error("GRANT_COIN_REWARD_ERROR", "发放金币奖励失败");
            }
        } catch (Exception e) {
            log.error("发放金币奖励失败", e);
            return Result.error("GRANT_COIN_REWARD_ERROR", "发放金币奖励失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_WALLET_DETAIL_CACHE)
    public Result<UserWalletResponse> consumeCoin(WalletTransactionRequest request) {
        try {
            boolean success = userWalletService.consumeCoin(
                request.getUserId(), 
                request.getCoinAmount(), 
                request.getBusinessId(), 
                request.getDescription()
            );
            if (success) {
                UserWallet wallet = userWalletService.getWalletByUserId(request.getUserId());
                UserWalletResponse response = convertToResponse(wallet);
                return Result.success(response);
            } else {
                return Result.error("COIN_CONSUME_ERROR", "金币消费失败：金币余额不足或钱包状态异常");
            }
        } catch (Exception e) {
            log.error("金币消费失败", e);
            return Result.error("COIN_CONSUME_ERROR", "金币消费失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> checkCoinBalance(Long userId, Long amount) {
        try {
            boolean sufficient = userWalletService.checkSufficientCoinBalance(userId, amount);
            return Result.success(sufficient);
        } catch (Exception e) {
            log.error("检查金币余额是否充足失败", e);
            return Result.error("CHECK_COIN_BALANCE_ERROR", "检查金币余额失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Long> getCoinBalance(Long userId) {
        try {
            Long balance = userWalletService.getCoinBalance(userId);
            return Result.success(balance);
        } catch (Exception e) {
            log.error("获取金币余额失败", e);
            return Result.error("GET_COIN_BALANCE_ERROR", "获取金币余额失败: " + e.getMessage());
        }
    }

    // =================== 钱包管理操作 ===================

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_WALLET_DETAIL_CACHE)
    public Result<Void> updateWalletStatus(Long userId, String status) {
        try {
            boolean success = userWalletService.updateWalletStatus(userId, status);
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("UPDATE_WALLET_STATUS_ERROR", "更新钱包状态失败");
            }
        } catch (Exception e) {
            log.error("更新钱包状态失败", e);
            return Result.error("UPDATE_WALLET_STATUS_ERROR", "更新钱包状态失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_WALLET_DETAIL_CACHE)
    public Result<Void> freezeWallet(Long userId, String reason) {
        try {
            boolean success = userWalletService.freezeWallet(userId, reason);
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("FREEZE_WALLET_ERROR", "冻结钱包失败");
            }
        } catch (Exception e) {
            log.error("冻结钱包失败", e);
            return Result.error("FREEZE_WALLET_ERROR", "冻结钱包失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_WALLET_DETAIL_CACHE)
    public Result<Void> unfreezeWallet(Long userId, String reason) {
        try {
            boolean success = userWalletService.unfreezeWallet(userId, reason);
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("UNFREEZE_WALLET_ERROR", "解冻钱包失败");
            }
        } catch (Exception e) {
            log.error("解冻钱包失败", e);
            return Result.error("UNFREEZE_WALLET_ERROR", "解冻钱包失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = UserCacheConstant.USER_WALLET_LIST_CACHE,
            key = UserCacheConstant.USER_WALLET_LIST_KEY,
            expire = UserCacheConstant.USER_WALLET_LIST_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<PageResponse<UserWalletResponse>> queryWallets(UserWalletQueryRequest request) {
        try {
            log.debug("分页查询用户钱包: currentPage={}, pageSize={}", request.getCurrentPage(), request.getPageSize());
            
            PageResponse<UserWallet> pageResult = userWalletService.queryWallets(request);
            
            List<UserWalletResponse> responses = pageResult.getDatas().stream()
                    .map(this::convertToResponse)
                    .toList();
            
            PageResponse<UserWalletResponse> result = new PageResponse<>();
            result.setDatas(responses);
            result.setCurrentPage(pageResult.getCurrentPage());
            result.setPageSize(pageResult.getPageSize());
            result.setTotal(pageResult.getTotal());
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询用户钱包列表失败", e);
            return Result.error("WALLET_LIST_QUERY_ERROR", "查询用户钱包列表失败: " + e.getMessage());
        }
    }

    // =================== 统计查询操作 ===================

    @Override
    @Cached(name = UserCacheConstant.USER_WALLET_DETAIL_CACHE,
            key = "wallet_statistics:" + "#userId",
            expire = UserCacheConstant.USER_WALLET_DETAIL_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<Map<String, Object>> getWalletStatistics(Long userId) {
        try {
            UserWallet wallet = userWalletService.getWalletByUserId(userId);
            if (wallet == null) {
                return Result.error("WALLET_NOT_FOUND", "钱包不存在");
            }
            
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("userId", userId);
            statistics.put("balance", wallet.getBalance());
            statistics.put("frozenAmount", wallet.getFrozenAmount());
            statistics.put("coinBalance", wallet.getCoinBalance());
            statistics.put("coinTotalEarned", wallet.getCoinTotalEarned());
            statistics.put("coinTotalSpent", wallet.getCoinTotalSpent());
            statistics.put("status", wallet.getStatus());
            statistics.put("isActive", wallet.isActive());
            statistics.put("isFrozen", wallet.isFrozen());
            statistics.put("timestamp", System.currentTimeMillis());
            
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取钱包统计失败", e);
            return Result.error("GET_WALLET_STATISTICS_ERROR", "获取钱包统计失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = UserCacheConstant.USER_WALLET_LIST_CACHE,
            key = "platform_wallet_stats",
            expire = UserCacheConstant.USER_WALLET_LIST_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<Map<String, Object>> getPlatformWalletStats() {
        try {
            Map<String, Object> platformStats = new HashMap<>();
            
            // 简化实现 - 基础统计数据
            platformStats.put("totalWallets", 0L);
            platformStats.put("totalCashBalance", BigDecimal.ZERO);
            platformStats.put("totalCoinBalance", 0L);
            platformStats.put("activeWallets", 0L);
            platformStats.put("frozenWallets", 0L);
            platformStats.put("timestamp", System.currentTimeMillis());
            
            return Result.success(platformStats);
        } catch (Exception e) {
            log.error("获取平台钱包统计失败", e);
            return Result.error("GET_PLATFORM_WALLET_STATS_ERROR", "获取平台钱包统计失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<UserWalletResponse>> getCashBalanceRanking(Integer limit) {
        try {
            // 简化实现 - 返回空列表
            return Result.success(java.util.Collections.emptyList());
        } catch (Exception e) {
            log.error("获取现金余额排行榜失败", e);
            return Result.error("GET_CASH_BALANCE_RANKING_ERROR", "获取现金余额排行榜失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<UserWalletResponse>> getCoinBalanceRanking(Integer limit) {
        try {
            // 简化实现 - 返回空列表
            return Result.success(java.util.Collections.emptyList());
        } catch (Exception e) {
            log.error("获取金币余额排行榜失败", e);
            return Result.error("GET_COIN_BALANCE_RANKING_ERROR", "获取金币余额排行榜失败: " + e.getMessage());
        }
    }

    // =================== 内部服务接口 ===================

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_WALLET_DETAIL_CACHE)
    public Result<Void> transferCash(Long fromUserId, Long toUserId, BigDecimal amount, String businessId, String description) {
        try {
            boolean success = userWalletService.transfer(fromUserId, toUserId, amount, description);
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("TRANSFER_ERROR", "现金转账失败：余额不足或钱包状态异常");
            }
        } catch (Exception e) {
            log.error("现金转账失败", e);
            return Result.error("TRANSFER_ERROR", "现金转账失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_WALLET_DETAIL_CACHE)
    public Result<Void> transferCoin(Long fromUserId, Long toUserId, Long amount, String businessId, String description) {
        try {
            boolean success = userWalletService.transferCoin(fromUserId, toUserId, amount, description);
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("COIN_TRANSFER_ERROR", "金币转账失败：金币余额不足或钱包状态异常");
            }
        } catch (Exception e) {
            log.error("金币转账失败", e);
            return Result.error("COIN_TRANSFER_ERROR", "金币转账失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_WALLET_DETAIL_CACHE)
    @CacheInvalidate(name = UserCacheConstant.USER_WALLET_LIST_CACHE)
    public Result<Void> deleteWallet(Long userId) {
        try {
            log.info("删除用户钱包请求: userId={}", userId);
            
            boolean success = userWalletService.deleteWallet(userId);
            if (success) {
                log.info("用户钱包删除成功: userId={}", userId);
                return Result.success(null);
            } else {
                return Result.error("WALLET_DELETE_ERROR", "删除用户钱包失败：仍有余额");
            }
        } catch (Exception e) {
            log.error("删除用户钱包失败", e);
            return Result.error("WALLET_DELETE_ERROR", "删除用户钱包失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> checkWalletExists(Long userId) {
        try {
            UserWallet wallet = userWalletService.getWalletByUserId(userId);
            return Result.success(wallet != null);
        } catch (Exception e) {
            log.error("检查钱包是否存在失败", e);
            return Result.error("CHECK_WALLET_EXISTS_ERROR", "检查钱包存在性失败: " + e.getMessage());
        }
    }

    /**
     * 转换为用户钱包响应对象
     */
    private UserWalletResponse convertToResponse(UserWallet wallet) {
        UserWalletResponse response = new UserWalletResponse();
        BeanUtils.copyProperties(wallet, response);
        return response;
    }
}
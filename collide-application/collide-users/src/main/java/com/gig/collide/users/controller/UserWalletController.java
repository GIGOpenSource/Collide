package com.gig.collide.users.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.gig.collide.api.user.UserWalletFacadeService;
import com.gig.collide.api.user.request.wallet.UserWalletCreateRequest;
import com.gig.collide.api.user.request.wallet.WalletTransactionRequest;
import com.gig.collide.api.user.response.wallet.UserWalletResponse;
import com.gig.collide.web.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户钱包控制器
 * 提供用户钱包管理和交易操作的HTTP接口
 * 支持现金和金币双重钱包系统
 * 
 * @author GIG Team
 * @version 2.0.0
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "用户钱包", description = "用户钱包管理和交易操作接口")
public class UserWalletController {

    @DubboReference
    private UserWalletFacadeService userWalletFacadeService;

    // =================== 钱包基础管理 ===================

    /**
     * 创建用户钱包
     */
    @PostMapping("/{userId}/wallet")
    @SaCheckLogin
    @Operation(summary = "创建钱包", description = "为用户创建钱包账户")
    public Result<UserWalletResponse> createWallet(
            @PathVariable("userId") @NotNull @Min(1) Long userId,
            @Valid @RequestBody UserWalletCreateRequest request) {
        try {
            // 检查权限：只能为自己创建钱包或管理员操作
            Long currentUserId = StpUtil.getLoginIdAsLong();
            if (!currentUserId.equals(userId) && !StpUtil.hasRole("admin")) {
                return Result.error("PERMISSION_DENIED", "无权限为其他用户创建钱包");
            }
            
            request.setUserId(userId);
            log.info("REST创建用户钱包: userId={}", userId);
            return userWalletFacadeService.createWallet(request);
        } catch (Exception e) {
            log.error("创建用户钱包异常", e);
            return Result.error("CREATE_WALLET_ERROR", "创建钱包失败: " + e.getMessage());
        }
    }

    /**
     * 获取钱包信息
     */
    @GetMapping("/{userId}/wallet")
    @SaCheckLogin
    @Operation(summary = "获取钱包信息", description = "获取用户钱包详细信息")
    public Result<UserWalletResponse> getWallet(@PathVariable("userId") @NotNull @Min(1) Long userId) {
        try {
            // 检查权限：只能查看自己的钱包或管理员操作
            Long currentUserId = StpUtil.getLoginIdAsLong();
            if (!currentUserId.equals(userId) && !StpUtil.hasRole("admin")) {
                return Result.error("PERMISSION_DENIED", "无权限查看其他用户钱包");
            }
            
            log.debug("REST获取用户钱包: userId={}", userId);
            return userWalletFacadeService.getWalletByUserId(userId);
        } catch (Exception e) {
            log.error("获取用户钱包异常", e);
            return Result.error("GET_WALLET_ERROR", "获取钱包信息失败: " + e.getMessage());
        }
    }

    /**
     * 批量查询钱包信息（管理员）
     */
    @PostMapping("/wallets/batch")
    @SaCheckLogin
    @Operation(summary = "批量查询钱包", description = "批量查询用户钱包信息（管理员功能）")
    public Result<List<UserWalletResponse>> batchGetWallets(@RequestBody @NotNull List<Long> userIds) {
        try {
            // 检查管理员权限
            if (!StpUtil.hasRole("admin")) {
                return Result.error("PERMISSION_DENIED", "需要管理员权限");
            }
            
            log.debug("REST批量查询钱包: count={}", userIds.size());
            return userWalletFacadeService.batchGetWallets(userIds);
        } catch (Exception e) {
            log.error("批量查询钱包异常", e);
            return Result.error("BATCH_GET_WALLETS_ERROR", "批量查询钱包失败: " + e.getMessage());
        }
    }

    // =================== 现金钱包操作 ===================

    /**
     * 充值现金
     */
    @PostMapping("/{userId}/wallet/cash/deposit")
    @SaCheckLogin
    @Operation(summary = "充值现金", description = "用户现金充值")
    public Result<UserWalletResponse> depositCash(
            @PathVariable("userId") @NotNull @Min(1) Long userId,
            @Valid @RequestBody WalletTransactionRequest request) {
        try {
            // 检查权限：只能为自己充值或管理员操作
            Long currentUserId = StpUtil.getLoginIdAsLong();
            if (!currentUserId.equals(userId) && !StpUtil.hasRole("admin")) {
                return Result.error("PERMISSION_DENIED", "无权限为其他用户充值");
            }
            
            request.setUserId(userId);
            log.info("REST现金充值: userId={}, amount={}", userId, request.getCashAmount());
            return userWalletFacadeService.depositCash(request);
        } catch (Exception e) {
            log.error("现金充值异常", e);
            return Result.error("DEPOSIT_CASH_ERROR", "现金充值失败: " + e.getMessage());
        }
    }

    /**
     * 现金消费
     */
    @PostMapping("/{userId}/wallet/cash/consume")
    @SaCheckLogin
    @Operation(summary = "现金消费", description = "用户现金消费")
    public Result<UserWalletResponse> consumeCash(
            @PathVariable("userId") @NotNull @Min(1) Long userId,
            @Valid @RequestBody WalletTransactionRequest request) {
        try {
            // 检查权限：只能消费自己的现金或管理员操作
            Long currentUserId = StpUtil.getLoginIdAsLong();
            if (!currentUserId.equals(userId) && !StpUtil.hasRole("admin")) {
                return Result.error("PERMISSION_DENIED", "无权限操作其他用户钱包");
            }
            
            request.setUserId(userId);
            log.info("REST现金消费: userId={}, amount={}", userId, request.getCashAmount());
            return userWalletFacadeService.consumeCash(request);
        } catch (Exception e) {
            log.error("现金消费异常", e);
            return Result.error("CONSUME_CASH_ERROR", "现金消费失败: " + e.getMessage());
        }
    }

    /**
     * 冻结现金（管理员）
     */
    @PostMapping("/{userId}/wallet/cash/freeze")
    @SaCheckLogin
    @Operation(summary = "冻结现金", description = "冻结用户现金（管理员功能）")
    public Result<UserWalletResponse> freezeCash(
            @PathVariable("userId") @NotNull @Min(1) Long userId,
            @RequestParam("amount") @NotNull @DecimalMin("0.01") BigDecimal amount,
            @RequestParam("businessId") @NotBlank String businessId,
            @RequestParam("description") @NotBlank String description) {
        try {
            // 检查管理员权限
            if (!StpUtil.hasRole("admin")) {
                return Result.error("PERMISSION_DENIED", "需要管理员权限");
            }
            
            log.info("REST冻结现金: userId={}, amount={}, businessId={}", userId, amount, businessId);
            return userWalletFacadeService.freezeCash(userId, amount, businessId, description);
        } catch (Exception e) {
            log.error("冻结现金异常", e);
            return Result.error("FREEZE_CASH_ERROR", "冻结现金失败: " + e.getMessage());
        }
    }

    /**
     * 解冻现金（管理员）
     */
    @PostMapping("/{userId}/wallet/cash/unfreeze")
    @SaCheckLogin
    @Operation(summary = "解冻现金", description = "解冻用户现金（管理员功能）")
    public Result<UserWalletResponse> unfreezeCash(
            @PathVariable("userId") @NotNull @Min(1) Long userId,
            @RequestParam("amount") @NotNull @DecimalMin("0.01") BigDecimal amount,
            @RequestParam("businessId") @NotBlank String businessId,
            @RequestParam("description") @NotBlank String description) {
        try {
            // 检查管理员权限
            if (!StpUtil.hasRole("admin")) {
                return Result.error("PERMISSION_DENIED", "需要管理员权限");
            }
            
            log.info("REST解冻现金: userId={}, amount={}, businessId={}", userId, amount, businessId);
            return userWalletFacadeService.unfreezeCash(userId, amount, businessId, description);
        } catch (Exception e) {
            log.error("解冻现金异常", e);
            return Result.error("UNFREEZE_CASH_ERROR", "解冻现金失败: " + e.getMessage());
        }
    }

    /**
     * 检查现金余额
     */
    @GetMapping("/{userId}/wallet/cash/balance")
    @SaCheckLogin
    @Operation(summary = "检查现金余额", description = "检查用户现金余额是否充足")
    public Result<Boolean> checkCashBalance(
            @PathVariable("userId") @NotNull @Min(1) Long userId,
            @RequestParam("amount") @NotNull @DecimalMin("0.01") BigDecimal amount) {
        try {
            // 检查权限：只能查看自己的余额或管理员操作
            Long currentUserId = StpUtil.getLoginIdAsLong();
            if (!currentUserId.equals(userId) && !StpUtil.hasRole("admin")) {
                return Result.error("PERMISSION_DENIED", "无权限查看其他用户余额");
            }
            
            log.debug("REST检查现金余额: userId={}, amount={}", userId, amount);
            return userWalletFacadeService.checkCashBalance(userId, amount);
        } catch (Exception e) {
            log.error("检查现金余额异常", e);
            return Result.error("CHECK_CASH_BALANCE_ERROR", "检查余额失败: " + e.getMessage());
        }
    }

    // =================== 金币钱包操作 ===================

    /**
     * 发放金币奖励
     */
    @PostMapping("/{userId}/wallet/coin/grant")
    @SaCheckLogin
    @Operation(summary = "发放金币奖励", description = "为用户发放金币奖励")
    public Result<UserWalletResponse> grantCoinReward(
            @PathVariable("userId") @NotNull @Min(1) Long userId,
            @Valid @RequestBody WalletTransactionRequest request) {
        try {
            // 检查权限：只能为自己发放或管理员操作
            Long currentUserId = StpUtil.getLoginIdAsLong();
            if (!currentUserId.equals(userId) && !StpUtil.hasRole("admin")) {
                return Result.error("PERMISSION_DENIED", "无权限为其他用户发放金币");
            }
            
            request.setUserId(userId);
            log.info("REST发放金币奖励: userId={}, amount={}", userId, request.getCoinAmount());
            return userWalletFacadeService.grantCoinReward(request);
        } catch (Exception e) {
            log.error("发放金币奖励异常", e);
            return Result.error("GRANT_COIN_ERROR", "发放金币失败: " + e.getMessage());
        }
    }

    /**
     * 金币消费
     */
    @PostMapping("/{userId}/wallet/coin/consume")
    @SaCheckLogin
    @Operation(summary = "金币消费", description = "用户金币消费")
    public Result<UserWalletResponse> consumeCoin(
            @PathVariable("userId") @NotNull @Min(1) Long userId,
            @Valid @RequestBody WalletTransactionRequest request) {
        try {
            // 检查权限：只能消费自己的金币或管理员操作
            Long currentUserId = StpUtil.getLoginIdAsLong();
            if (!currentUserId.equals(userId) && !StpUtil.hasRole("admin")) {
                return Result.error("PERMISSION_DENIED", "无权限操作其他用户钱包");
            }
            
            request.setUserId(userId);
            log.info("REST金币消费: userId={}, amount={}", userId, request.getCoinAmount());
            return userWalletFacadeService.consumeCoin(request);
        } catch (Exception e) {
            log.error("金币消费异常", e);
            return Result.error("CONSUME_COIN_ERROR", "金币消费失败: " + e.getMessage());
        }
    }

    /**
     * 检查金币余额
     */
    @GetMapping("/{userId}/wallet/coin/balance")
    @SaCheckLogin
    @Operation(summary = "检查金币余额", description = "检查用户金币余额是否充足")
    public Result<Boolean> checkCoinBalance(
            @PathVariable("userId") @NotNull @Min(1) Long userId,
            @RequestParam("amount") @NotNull @Min(1) Long amount) {
        try {
            // 检查权限：只能查看自己的余额或管理员操作
            Long currentUserId = StpUtil.getLoginIdAsLong();
            if (!currentUserId.equals(userId) && !StpUtil.hasRole("admin")) {
                return Result.error("PERMISSION_DENIED", "无权限查看其他用户余额");
            }
            
            log.debug("REST检查金币余额: userId={}, amount={}", userId, amount);
            return userWalletFacadeService.checkCoinBalance(userId, amount);
        } catch (Exception e) {
            log.error("检查金币余额异常", e);
            return Result.error("CHECK_COIN_BALANCE_ERROR", "检查金币余额失败: " + e.getMessage());
        }
    }

    // =================== 钱包状态管理 ===================

    /**
     * 更新钱包状态（管理员）
     */
    @PutMapping("/{userId}/wallet/status")
    @SaCheckLogin
    @Operation(summary = "更新钱包状态", description = "更新用户钱包状态（管理员功能）")
    public Result<Void> updateWalletStatus(
            @PathVariable("userId") @NotNull @Min(1) Long userId,
            @RequestParam("status") @NotBlank String status,
            @RequestParam(value = "reason", required = false) String reason) {
        try {
            // 检查管理员权限
            if (!StpUtil.hasRole("admin")) {
                return Result.error("PERMISSION_DENIED", "需要管理员权限");
            }
            
            log.info("REST更新钱包状态: userId={}, status={}", userId, status);
            return userWalletFacadeService.updateWalletStatus(userId, status);
        } catch (Exception e) {
            log.error("更新钱包状态异常", e);
            return Result.error("UPDATE_WALLET_STATUS_ERROR", "更新钱包状态失败: " + e.getMessage());
        }
    }

    /**
     * 冻结钱包（管理员）
     */
    @PostMapping("/{userId}/wallet/freeze")
    @SaCheckLogin
    @Operation(summary = "冻结钱包", description = "冻结用户钱包（管理员功能）")
    public Result<Void> freezeWallet(
            @PathVariable("userId") @NotNull @Min(1) Long userId,
            @RequestParam("reason") @NotBlank String reason) {
        try {
            // 检查管理员权限
            if (!StpUtil.hasRole("admin")) {
                return Result.error("PERMISSION_DENIED", "需要管理员权限");
            }
            
            log.info("REST冻结钱包: userId={}, reason={}", userId, reason);
            return userWalletFacadeService.freezeWallet(userId, reason);
        } catch (Exception e) {
            log.error("冻结钱包异常", e);
            return Result.error("FREEZE_WALLET_ERROR", "冻结钱包失败: " + e.getMessage());
        }
    }

    /**
     * 解冻钱包（管理员）
     */
    @PostMapping("/{userId}/wallet/unfreeze")
    @SaCheckLogin
    @Operation(summary = "解冻钱包", description = "解冻用户钱包（管理员功能）")
    public Result<Void> unfreezeWallet(
            @PathVariable("userId") @NotNull @Min(1) Long userId,
            @RequestParam("reason") @NotBlank String reason) {
        try {
            // 检查管理员权限
            if (!StpUtil.hasRole("admin")) {
                return Result.error("PERMISSION_DENIED", "需要管理员权限");
            }
            
            log.info("REST解冻钱包: userId={}, reason={}", userId, reason);
            return userWalletFacadeService.unfreezeWallet(userId, reason);
        } catch (Exception e) {
            log.error("解冻钱包异常", e);
            return Result.error("UNFREEZE_WALLET_ERROR", "解冻钱包失败: " + e.getMessage());
        }
    }

    // =================== 当前用户钱包接口 ===================

    /**
     * 获取我的钱包信息
     */
    @GetMapping("/me/wallet")
    @SaCheckLogin
    @Operation(summary = "获取我的钱包", description = "获取当前用户的钱包信息")
    public Result<UserWalletResponse> getMyWallet() {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            log.debug("REST获取当前用户钱包: userId={}", userId);
            return userWalletFacadeService.getWalletByUserId(userId);
        } catch (Exception e) {
            log.error("获取当前用户钱包异常", e);
            return Result.error("GET_MY_WALLET_ERROR", "获取钱包信息失败: " + e.getMessage());
        }
    }

    /**
     * 我的现金充值
     */
    @PostMapping("/me/wallet/cash/deposit")
    @SaCheckLogin
    @Operation(summary = "我的现金充值", description = "当前用户现金充值")
    public Result<UserWalletResponse> myDepositCash(@Valid @RequestBody WalletTransactionRequest request) {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            request.setUserId(userId);
            log.info("REST当前用户现金充值: userId={}, amount={}", userId, request.getCashAmount());
            return userWalletFacadeService.depositCash(request);
        } catch (Exception e) {
            log.error("当前用户现金充值异常", e);
            return Result.error("MY_DEPOSIT_CASH_ERROR", "现金充值失败: " + e.getMessage());
        }
    }

    /**
     * 我的金币奖励发放
     */
    @PostMapping("/me/wallet/coin/grant")
    @SaCheckLogin
    @Operation(summary = "我的金币奖励", description = "当前用户获得金币奖励")
    public Result<UserWalletResponse> myGrantCoinReward(@Valid @RequestBody WalletTransactionRequest request) {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            request.setUserId(userId);
            log.info("REST当前用户获得金币奖励: userId={}, amount={}", userId, request.getCoinAmount());
            return userWalletFacadeService.grantCoinReward(request);
        } catch (Exception e) {
            log.error("当前用户获得金币奖励异常", e);
            return Result.error("MY_GRANT_COIN_ERROR", "获得金币奖励失败: " + e.getMessage());
        }
    }

    /**
     * 健康检查
     */
    @GetMapping("/wallet/health")
    @Operation(summary = "钱包服务健康检查", description = "检查钱包服务状态")
    public Result<String> health() {
        return Result.success("User Wallet Service v2.0 - 运行正常");
    }
}
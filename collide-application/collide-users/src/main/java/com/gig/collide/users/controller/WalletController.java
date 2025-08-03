package com.gig.collide.users.controller;

import com.gig.collide.api.user.response.WalletResponse;
import com.gig.collide.users.domain.entity.UserWallet;
import com.gig.collide.users.domain.service.WalletService;
import com.gig.collide.users.facade.UserFacadeServiceImpl;
import com.gig.collide.web.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 钱包管理控制器 - 安全版
 * 基于users-simple.sql设计：每个用户只有一个钱包
 * 只允许查询钱包，金额变动只能通过订单系统
 * 
 * @author GIG Team
 * @version 2.0.0 (安全版 - 单钱包)
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users/{userId}/wallet")
@RequiredArgsConstructor
@Tag(name = "钱包管理", description = "钱包查询接口，金额变动只能通过订单系统")
public class WalletController {

    private final UserFacadeServiceImpl userFacadeService;
    private final WalletService walletService;

    /**
     * 获取用户钱包信息
     */
    @GetMapping
    @Operation(summary = "获取用户钱包信息", description = "查询用户的钱包信息，包含余额、状态等")
    public Result<WalletResponse> getUserWallet(@Parameter(description = "用户ID") @PathVariable Long userId) {
        try {
            log.info("查询用户钱包信息，用户ID：{}", userId);
            Result<WalletResponse> result = userFacadeService.getUserWallet(userId);
            return result;
        } catch (Exception e) {
            log.error("查询用户钱包失败，用户ID：{}", userId, e);
            return Result.error("WALLET_QUERY_ERROR", "查询钱包失败: " + e.getMessage());
        }
    }

    /**
     * 检查余额是否充足
     */
    @GetMapping("/balance/check")
    @Operation(summary = "检查余额是否充足", description = "检查钱包余额是否充足")
    public Result<Boolean> checkBalance(@Parameter(description = "用户ID") @PathVariable Long userId,
                                       @Parameter(description = "金额") @RequestParam BigDecimal amount) {
        try {
            log.debug("检查余额，用户ID：{}，金额：{}", userId, amount);
            
            UserWallet wallet = walletService.getWalletByUserId(userId);
            if (wallet == null) {
                return Result.error("WALLET_NOT_FOUND", "钱包不存在");
            }
            
            // 可用余额 = 总余额 - 冻结金额
            BigDecimal availableBalance = wallet.getBalance().subtract(wallet.getFrozenAmount());
            boolean sufficient = availableBalance.compareTo(amount) >= 0;
            return Result.success(sufficient);
        } catch (Exception e) {
            log.error("检查余额失败，用户ID：{}", userId, e);
            return Result.error("BALANCE_CHECK_ERROR", "检查余额失败: " + e.getMessage());
        }
    }

    /**
     * 获取钱包状态
     */
    @GetMapping("/status")
    @Operation(summary = "获取钱包状态", description = "查询钱包的状态信息")
    public Result<String> getWalletStatus(@Parameter(description = "用户ID") @PathVariable Long userId) {
        try {
            log.debug("查询钱包状态，用户ID：{}", userId);
            
            UserWallet wallet = walletService.getWalletByUserId(userId);
            if (wallet == null) {
                return Result.error("WALLET_NOT_FOUND", "钱包不存在");
            }
            
            return Result.success(wallet.getStatus());
        } catch (Exception e) {
            log.error("查询钱包状态失败，用户ID：{}", userId, e);
            return Result.error("WALLET_STATUS_ERROR", "查询钱包状态失败: " + e.getMessage());
        }
    }
}
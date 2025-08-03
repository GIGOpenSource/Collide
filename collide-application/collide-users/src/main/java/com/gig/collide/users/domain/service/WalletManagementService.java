package com.gig.collide.users.domain.service;

import com.gig.collide.users.domain.entity.UserWallet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 钱包管理公共服务类
 * 提供统一的钱包操作入口，包含完整的容错处理
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Slf4j
@Service
public class WalletManagementService {

    @Autowired
    private WalletService walletService;

    /**
     * 确保用户钱包存在，如果不存在则自动创建
     * 
     * @param userId 用户ID
     * @return 用户钱包，失败返回null
     */
    @Transactional
    public UserWallet ensureWalletExists(Long userId) {
        if (userId == null) {
            log.error("确保钱包存在失败：用户ID为空");
            return null;
        }

        try {
            // 先尝试获取现有钱包
            UserWallet wallet = walletService.getWalletByUserId(userId);
            
            if (wallet == null) {
                log.info("用户{}钱包不存在，开始自动创建", userId);
                wallet = walletService.createWallet(userId);
                if (wallet != null) {
                    log.info("用户{}钱包创建成功，钱包ID：{}", userId, wallet.getId());
                } else {
                    log.error("用户{}钱包创建失败", userId);
                }
            }
            
            return wallet;
            
        } catch (Exception e) {
            log.error("确保用户{}钱包存在失败：{}", userId, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 安全的余额操作 - 增加余额（充值/奖励）
     * 包含完整的参数验证和容错处理
     * 
     * @param userId 用户ID
     * @param amount 金额
     * @param businessId 业务ID
     * @param description 描述
     * @return 操作结果
     */
    @Transactional
    public WalletOperationResult safeAddBalance(Long userId, BigDecimal amount, String businessId, String description) {
        // 参数验证
        WalletOperationResult validation = validateParameters(userId, amount, businessId, description);
        if (!validation.isSuccess()) {
            return validation;
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return WalletOperationResult.failure("INVALID_AMOUNT", "充值金额必须大于0");
        }

        try {
            // 确保钱包存在
            UserWallet wallet = ensureWalletExists(userId);
            if (wallet == null) {
                return WalletOperationResult.failure("WALLET_CREATE_FAILED", "钱包创建失败");
            }

            // 检查钱包状态
            if (!"active".equals(wallet.getStatus())) {
                return WalletOperationResult.failure("WALLET_FROZEN", "钱包已冻结，无法进行充值操作");
            }

            // 执行充值操作
            boolean success = walletService.addBalance(userId, amount, businessId, description);
            if (success) {
                log.info("用户{}充值成功：金额={}，业务ID={}，描述={}", userId, amount, businessId, description);
                return WalletOperationResult.success("充值成功");
            } else {
                return WalletOperationResult.failure("ADD_BALANCE_FAILED", "充值操作失败");
            }

        } catch (Exception e) {
            log.error("用户{}充值异常：金额={}，错误={}", userId, amount, e.getMessage(), e);
            return WalletOperationResult.failure("ADD_BALANCE_ERROR", "充值操作异常：" + e.getMessage());
        }
    }

    /**
     * 安全的余额操作 - 扣除余额（消费/扣款）
     * 包含完整的参数验证和容错处理
     * 
     * @param userId 用户ID
     * @param amount 金额
     * @param businessId 业务ID
     * @param description 描述
     * @return 操作结果
     */
    @Transactional
    public WalletOperationResult safeDeductBalance(Long userId, BigDecimal amount, String businessId, String description) {
        // 参数验证
        WalletOperationResult validation = validateParameters(userId, amount, businessId, description);
        if (!validation.isSuccess()) {
            return validation;
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return WalletOperationResult.failure("INVALID_AMOUNT", "扣款金额必须大于0");
        }

        try {
            // 确保钱包存在
            UserWallet wallet = ensureWalletExists(userId);
            if (wallet == null) {
                return WalletOperationResult.failure("WALLET_NOT_FOUND", "用户钱包不存在");
            }

            // 检查钱包状态
            if (!"active".equals(wallet.getStatus())) {
                return WalletOperationResult.failure("WALLET_FROZEN", "钱包已冻结，无法进行扣款操作");
            }

            // 检查余额是否充足
            if (!wallet.hasSufficientBalance(amount)) {
                log.warn("用户{}余额不足：需要={}，可用余额={}", userId, amount, wallet.getAvailableBalance());
                return WalletOperationResult.failure("INSUFFICIENT_BALANCE", 
                    String.format("余额不足，需要：%s，可用余额：%s", amount, wallet.getAvailableBalance()));
            }

            // 执行扣款操作
            boolean success = walletService.deductBalance(userId, amount, businessId, description);
            if (success) {
                log.info("用户{}扣款成功：金额={}，业务ID={}，描述={}", userId, amount, businessId, description);
                return WalletOperationResult.success("扣款成功");
            } else {
                return WalletOperationResult.failure("DEDUCT_BALANCE_FAILED", "扣款操作失败");
            }

        } catch (Exception e) {
            log.error("用户{}扣款异常：金额={}，错误={}", userId, amount, e.getMessage(), e);
            return WalletOperationResult.failure("DEDUCT_BALANCE_ERROR", "扣款操作异常：" + e.getMessage());
        }
    }

    /**
     * 检查用户钱包余额
     * 
     * @param userId 用户ID
     * @param amount 需要检查的金额
     * @return 检查结果
     */
    public WalletBalanceCheckResult checkBalance(Long userId, BigDecimal amount) {
        if (userId == null) {
            return WalletBalanceCheckResult.invalid("用户ID不能为空");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            return WalletBalanceCheckResult.invalid("检查金额不能为空且必须非负");
        }

        try {
            UserWallet wallet = walletService.getWalletByUserId(userId);
            if (wallet == null) {
                return WalletBalanceCheckResult.notFound("用户钱包不存在");
            }

            if (!"active".equals(wallet.getStatus())) {
                return WalletBalanceCheckResult.frozen("钱包已冻结");
            }

            BigDecimal availableBalance = wallet.getAvailableBalance();
            boolean sufficient = availableBalance.compareTo(amount) >= 0;
            
            return WalletBalanceCheckResult.success(sufficient, availableBalance, wallet.getFrozenAmount());

        } catch (Exception e) {
            log.error("检查用户{}余额异常：{}", userId, e.getMessage(), e);
            return WalletBalanceCheckResult.error("余额检查异常：" + e.getMessage());
        }
    }

    /**
     * 参数验证
     */
    private WalletOperationResult validateParameters(Long userId, BigDecimal amount, String businessId, String description) {
        if (userId == null) {
            return WalletOperationResult.failure("INVALID_USER_ID", "用户ID不能为空");
        }

        if (amount == null) {
            return WalletOperationResult.failure("INVALID_AMOUNT", "金额不能为空");
        }

        if (businessId == null || businessId.trim().isEmpty()) {
            return WalletOperationResult.failure("INVALID_BUSINESS_ID", "业务ID不能为空");
        }

        if (description == null || description.trim().isEmpty()) {
            return WalletOperationResult.failure("INVALID_DESCRIPTION", "操作描述不能为空");
        }

        return WalletOperationResult.success("参数验证通过");
    }

    /**
     * 钱包操作结果类
     */
    public static class WalletOperationResult {
        private final boolean success;
        private final String code;
        private final String message;

        private WalletOperationResult(boolean success, String code, String message) {
            this.success = success;
            this.code = code;
            this.message = message;
        }

        public static WalletOperationResult success(String message) {
            return new WalletOperationResult(true, "SUCCESS", message);
        }

        public static WalletOperationResult failure(String code, String message) {
            return new WalletOperationResult(false, code, message);
        }

        public boolean isSuccess() { return success; }
        public String getCode() { return code; }
        public String getMessage() { return message; }
    }

    /**
     * 钱包余额检查结果类
     */
    public static class WalletBalanceCheckResult {
        private final boolean valid;
        private final boolean sufficient;
        private final String message;
        private final BigDecimal availableBalance;
        private final BigDecimal frozenAmount;

        private WalletBalanceCheckResult(boolean valid, boolean sufficient, String message, 
                                       BigDecimal availableBalance, BigDecimal frozenAmount) {
            this.valid = valid;
            this.sufficient = sufficient;
            this.message = message;
            this.availableBalance = availableBalance;
            this.frozenAmount = frozenAmount;
        }

        public static WalletBalanceCheckResult success(boolean sufficient, BigDecimal availableBalance, BigDecimal frozenAmount) {
            return new WalletBalanceCheckResult(true, sufficient, "检查成功", availableBalance, frozenAmount);
        }

        public static WalletBalanceCheckResult invalid(String message) {
            return new WalletBalanceCheckResult(false, false, message, null, null);
        }

        public static WalletBalanceCheckResult notFound(String message) {
            return new WalletBalanceCheckResult(false, false, message, null, null);
        }

        public static WalletBalanceCheckResult frozen(String message) {
            return new WalletBalanceCheckResult(false, false, message, null, null);
        }

        public static WalletBalanceCheckResult error(String message) {
            return new WalletBalanceCheckResult(false, false, message, null, null);
        }

        public boolean isValid() { return valid; }
        public boolean isSufficient() { return sufficient; }
        public String getMessage() { return message; }
        public BigDecimal getAvailableBalance() { return availableBalance; }
        public BigDecimal getFrozenAmount() { return frozenAmount; }
    }
}
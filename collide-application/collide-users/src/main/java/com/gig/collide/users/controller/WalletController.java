package com.gig.collide.users.controller;

import com.gig.collide.api.user.request.WalletOperationRequest;
import com.gig.collide.api.user.response.WalletResponse;
import com.gig.collide.users.facade.UserFacadeServiceImpl;
import com.gig.collide.web.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;

/**
 * 钱包管理控制器
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users/{userId}/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final UserFacadeServiceImpl userFacadeService;

    /**
     * 获取用户钱包信息
     */
    @GetMapping
    public WalletResponse getUserWallet(@PathVariable Long userId) {
        log.info("REST - 获取用户钱包信息，用户ID：{}", userId);
        Result<WalletResponse> result = userFacadeService.getUserWallet(userId);
        return result.getData();
    }

    /**
     * 钱包操作（充值、提现、冻结、解冻）
     */
    @PostMapping("/operation")
    public WalletResponse walletOperation(@PathVariable Long userId, 
                                        @Valid @RequestBody WalletOperationRequest request) {
        log.info("REST - 钱包操作，用户ID：{}，操作：{}", userId, request);
        request.setUserId(userId);
        Result<WalletResponse> result = userFacadeService.walletOperation(request);
        return result.getData();
    }

    /**
     * 检查余额是否充足
     */
    @GetMapping("/balance/check")
    public Boolean checkBalance(@PathVariable Long userId, 
                              @RequestParam BigDecimal amount) {
        log.info("REST - 检查余额，用户ID：{}，金额：{}", userId, amount);
        Result<Boolean> result = userFacadeService.checkWalletBalance(userId, amount);
        return result.getData();
    }

    /**
     * 钱包充值
     */
    @PostMapping("/recharge")
    public WalletResponse recharge(@PathVariable Long userId, 
                                 @RequestParam BigDecimal amount,
                                 @RequestParam(required = false) String description) {
        log.info("REST - 钱包充值，用户ID：{}，金额：{}", userId, amount);
        
        WalletOperationRequest request = new WalletOperationRequest();
        request.setUserId(userId);
        request.setAmount(amount);
        request.setOperationType("recharge");
        request.setDescription(description);
        
        Result<WalletResponse> result = userFacadeService.walletOperation(request);
        return result.getData();
    }

    /**
     * 钱包提现
     */
    @PostMapping("/withdraw")
    public WalletResponse withdraw(@PathVariable Long userId, 
                                 @RequestParam BigDecimal amount,
                                 @RequestParam(required = false) String description) {
        log.info("REST - 钱包提现，用户ID：{}，金额：{}", userId, amount);
        
        WalletOperationRequest request = new WalletOperationRequest();
        request.setUserId(userId);
        request.setAmount(amount);
        request.setOperationType("withdraw");
        request.setDescription(description);
        
        Result<WalletResponse> result = userFacadeService.walletOperation(request);
        return result.getData();
    }
}
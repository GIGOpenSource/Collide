package com.gig.collide.users.domain.service.impl;

import com.gig.collide.users.domain.entity.UserWallet;
import com.gig.collide.users.domain.service.WalletService;
import com.gig.collide.users.infrastructure.mapper.UserWalletMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 钱包服务实现
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final UserWalletMapper walletMapper;

    @Override
    public UserWallet getWalletByUserId(Long userId) {
        return walletMapper.selectByUserId(userId);
    }

    @Override
    @Transactional
    public UserWallet createWallet(Long userId) {
        // 检查是否已存在
        UserWallet existWallet = getWalletByUserId(userId);
        if (existWallet != null) {
            return existWallet;
        }

        UserWallet wallet = new UserWallet();
        wallet.setUserId(userId);
        wallet.initDefaults();
        wallet.setCreateTime(LocalDateTime.now());
        wallet.setUpdateTime(LocalDateTime.now());

        walletMapper.insert(wallet);
        return wallet;
    }

    @Override
    @Transactional
    public UserWallet recharge(Long userId, BigDecimal amount, String description) {
        log.info("用户{}充值，金额：{}，描述：{}", userId, amount, description);
        
        int result = walletMapper.addBalance(userId, amount);
        if (result <= 0) {
            throw new RuntimeException("充值失败：钱包状态异常");
        }

        return getWalletByUserId(userId);
    }

    @Override
    @Transactional
    public UserWallet withdraw(Long userId, BigDecimal amount, String description) {
        log.info("用户{}提现，金额：{}，描述：{}", userId, amount, description);

        UserWallet wallet = getWalletByUserId(userId);
        if (wallet == null) {
            throw new RuntimeException("钱包不存在");
        }

        if (!wallet.hasSufficientBalance(amount)) {
            throw new RuntimeException("余额不足");
        }

        int result = walletMapper.deductBalance(userId, amount);
        if (result <= 0) {
            throw new RuntimeException("提现失败：余额不足或钱包状态异常");
        }

        return getWalletByUserId(userId);
    }

    @Override
    @Transactional
    public UserWallet freezeAmount(Long userId, BigDecimal amount, String description) {
        log.info("用户{}冻结金额，金额：{}，描述：{}", userId, amount, description);

        UserWallet wallet = getWalletByUserId(userId);
        if (wallet == null) {
            throw new RuntimeException("钱包不存在");
        }

        if (!wallet.hasSufficientBalance(amount)) {
            throw new RuntimeException("可用余额不足");
        }

        // 减少可用余额，增加冻结金额
        int deductResult = walletMapper.updateBalance(userId, amount, false);
        int freezeResult = walletMapper.updateFrozenAmount(userId, amount, true);

        if (deductResult <= 0 || freezeResult <= 0) {
            throw new RuntimeException("冻结失败：余额不足");
        }

        return getWalletByUserId(userId);
    }

    @Override
    @Transactional
    public UserWallet unfreezeAmount(Long userId, BigDecimal amount, String description) {
        log.info("用户{}解冻金额，金额：{}，描述：{}", userId, amount, description);

        UserWallet wallet = getWalletByUserId(userId);
        if (wallet == null) {
            throw new RuntimeException("钱包不存在");
        }

        if (wallet.getFrozenAmount().compareTo(amount) < 0) {
            throw new RuntimeException("冻结金额不足");
        }

        // 减少冻结金额，增加可用余额
        int unfreezeResult = walletMapper.updateFrozenAmount(userId, amount, false);
        int addResult = walletMapper.updateBalance(userId, amount, true);

        if (unfreezeResult <= 0 || addResult <= 0) {
            throw new RuntimeException("解冻失败：冻结金额不足");
        }

        return getWalletByUserId(userId);
    }

    @Override
    public boolean checkBalance(Long userId, BigDecimal amount) {
        UserWallet wallet = getWalletByUserId(userId);
        return wallet != null && wallet.hasSufficientBalance(amount);
    }

    @Override
    @Transactional
    public boolean deductBalance(Long userId, BigDecimal amount, String businessId, String description) {
        log.info("用户{}扣款，金额：{}，业务ID：{}，描述：{}", userId, amount, businessId, description);
        
        try {
            int result = walletMapper.deductBalance(userId, amount);
            return result > 0;
        } catch (Exception e) {
            log.error("扣款失败：用户{}，金额：{}，错误：{}", userId, amount, e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public boolean addBalance(Long userId, BigDecimal amount, String businessId, String description) {
        log.info("用户{}充值，金额：{}，业务ID：{}，描述：{}", userId, amount, businessId, description);
        
        try {
            int result = walletMapper.addBalance(userId, amount);
            return result > 0;
        } catch (Exception e) {
            log.error("充值失败：用户{}，金额：{}，错误：{}", userId, amount, e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public void updateWalletStatus(Long userId, String status) {
        UserWallet wallet = getWalletByUserId(userId);
        if (wallet != null) {
            wallet.setStatus(status);
            wallet.setUpdateTime(LocalDateTime.now());
            walletMapper.updateById(wallet);
        }
    }
}
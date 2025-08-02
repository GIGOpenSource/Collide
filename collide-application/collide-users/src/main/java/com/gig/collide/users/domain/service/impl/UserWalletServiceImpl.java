package com.gig.collide.users.domain.service.impl;

import com.gig.collide.api.user.request.users.wallet.UserWalletQueryRequest;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.users.domain.entity.UserWallet;
import com.gig.collide.users.domain.service.UserWalletService;
import com.gig.collide.users.infrastructure.mapper.UserWalletMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户钱包领域服务实现 - 对应 t_user_wallet 表
 * 负责用户钱包管理（现金和金币）
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserWalletServiceImpl implements UserWalletService {

    private final UserWalletMapper walletMapper;

    @Override
    @Transactional
    public UserWallet createWallet(UserWallet userWallet) {
        try {
            // 检查是否已存在
            UserWallet existWallet = getWalletByUserId(userWallet.getUserId());
            if (existWallet != null) {
                log.info("用户钱包已存在: userId={}", userWallet.getUserId());
                return existWallet;
            }

            userWallet.initDefaults();
            walletMapper.insert(userWallet);
            
            log.info("用户钱包创建成功: userId={}", userWallet.getUserId());
            return userWallet;
        } catch (Exception e) {
            log.error("创建用户钱包失败: userId={}", userWallet.getUserId(), e);
            throw new RuntimeException("创建钱包失败", e);
        }
    }

    @Override
    public UserWallet getWalletByUserId(Long userId) {
        return walletMapper.selectByUserId(userId);
    }

    @Override
    public List<UserWallet> batchGetWallets(List<Long> userIds) {
        return walletMapper.selectByUserIds(userIds);
    }

    @Override
    @Transactional
    public boolean recharge(Long userId, BigDecimal amount, String businessId, String description) {
        log.info("用户{}现金充值，金额：{}，业务ID：{}，描述：{}", userId, amount, businessId, description);
        
        try {
            UserWallet wallet = getWalletByUserId(userId);
            if (wallet == null || !wallet.isActive()) {
                log.error("充值失败：钱包不存在或状态异常，用户ID：{}", userId);
                return false;
            }

            int result = walletMapper.addBalance(userId, amount);
            if (result > 0) {
                walletMapper.updateTotalIncome(userId, amount);
                log.info("现金充值成功: userId={}, amount={}", userId, amount);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("现金充值失败：用户{}，金额：{}，错误：{}", userId, amount, e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public boolean withdraw(Long userId, BigDecimal amount, String businessId, String description) {
        log.info("用户{}现金提现，金额：{}，业务ID：{}，描述：{}", userId, amount, businessId, description);
        
        try {
            UserWallet wallet = getWalletByUserId(userId);
            if (wallet == null || !wallet.isActive()) {
                log.error("提现失败：钱包不存在或状态异常，用户ID：{}", userId);
                return false;
            }

            if (!wallet.hasSufficientBalance(amount)) {
                log.error("提现失败：余额不足，用户ID：{}，需要金额：{}，可用余额：{}", userId, amount, wallet.getAvailableBalance());
                return false;
            }

            int result = walletMapper.deductBalance(userId, amount);
            if (result > 0) {
                walletMapper.updateTotalExpense(userId, amount);
                log.info("现金提现成功: userId={}, amount={}", userId, amount);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("现金提现失败：用户{}，金额：{}，错误：{}", userId, amount, e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public boolean transfer(Long fromUserId, Long toUserId, BigDecimal amount, String description) {
        log.info("现金转账：从用户{}转给用户{}，金额：{}，描述：{}", fromUserId, toUserId, amount, description);
        
        try {
            // 转出方扣款
            if (!withdraw(fromUserId, amount, "TRANSFER_OUT", description)) {
                return false;
            }
            
            // 转入方充值
            if (!recharge(toUserId, amount, "TRANSFER_IN", description)) {
                // 如果转入失败，需要回滚转出操作（这里简化处理）
                recharge(fromUserId, amount, "TRANSFER_ROLLBACK", "转账回滚");
                return false;
            }
            
            log.info("现金转账成功: fromUserId={}, toUserId={}, amount={}", fromUserId, toUserId, amount);
            return true;
        } catch (Exception e) {
            log.error("现金转账失败：从用户{}转给用户{}，金额：{}，错误：{}", fromUserId, toUserId, amount, e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public boolean consume(Long userId, BigDecimal amount, String businessId, String description) {
        return withdraw(userId, amount, businessId, description);
    }

    @Override
    @Transactional
    public boolean rechargeCoin(Long userId, Long amount, String businessId, String description) {
        log.info("用户{}金币充值，数量：{}，业务ID：{}，描述：{}", userId, amount, businessId, description);
        
        try {
            UserWallet wallet = getWalletByUserId(userId);
            if (wallet == null || !wallet.isActive()) {
                log.error("金币充值失败：钱包不存在或状态异常，用户ID：{}", userId);
                return false;
            }

            int result = walletMapper.addCoinBalance(userId, amount);
            if (result > 0) {
                walletMapper.updateCoinTotalEarned(userId, amount);
                log.info("金币充值成功: userId={}, amount={}", userId, amount);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("金币充值失败：用户{}，数量：{}，错误：{}", userId, amount, e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public boolean consumeCoin(Long userId, Long amount, String businessId, String description) {
        log.info("用户{}金币消费，数量：{}，业务ID：{}，描述：{}", userId, amount, businessId, description);
        
        try {
            UserWallet wallet = getWalletByUserId(userId);
            if (wallet == null || !wallet.isActive()) {
                log.error("金币消费失败：钱包不存在或状态异常，用户ID：{}", userId);
                return false;
            }

            if (!wallet.hasSufficientCoinBalance(amount)) {
                log.error("金币消费失败：金币余额不足，用户ID：{}，需要数量：{}，金币余额：{}", userId, amount, wallet.getCoinBalance());
                return false;
            }

            int result = walletMapper.deductCoinBalance(userId, amount);
            if (result > 0) {
                walletMapper.updateCoinTotalSpent(userId, amount);
                log.info("金币消费成功: userId={}, amount={}", userId, amount);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("金币消费失败：用户{}，数量：{}，错误：{}", userId, amount, e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public boolean transferCoin(Long fromUserId, Long toUserId, Long amount, String description) {
        log.info("金币转账：从用户{}转给用户{}，数量：{}，描述：{}", fromUserId, toUserId, amount, description);
        
        try {
            // 转出方扣除金币
            if (!consumeCoin(fromUserId, amount, "COIN_TRANSFER_OUT", description)) {
                return false;
            }
            
            // 转入方充值金币
            if (!rechargeCoin(toUserId, amount, "COIN_TRANSFER_IN", description)) {
                // 如果转入失败，需要回滚转出操作
                rechargeCoin(fromUserId, amount, "COIN_TRANSFER_ROLLBACK", "金币转账回滚");
                return false;
            }
            
            log.info("金币转账成功: fromUserId={}, toUserId={}, amount={}", fromUserId, toUserId, amount);
            return true;
        } catch (Exception e) {
            log.error("金币转账失败：从用户{}转给用户{}，数量：{}，错误：{}", fromUserId, toUserId, amount, e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public boolean freezeAmount(Long userId, BigDecimal amount, String reason) {
        log.info("用户{}冻结资金，金额：{}，原因：{}", userId, amount, reason);

        try {
            UserWallet wallet = getWalletByUserId(userId);
            if (wallet == null) {
                log.error("冻结失败：钱包不存在，用户ID：{}", userId);
                return false;
            }

            if (!wallet.hasSufficientBalance(amount)) {
                log.error("冻结失败：可用余额不足，用户ID：{}，需要金额：{}，可用余额：{}", userId, amount, wallet.getAvailableBalance());
                return false;
            }

            int result = walletMapper.updateFrozenAmount(userId, amount, true);
            if (result > 0) {
                log.info("资金冻结成功: userId={}, amount={}", userId, amount);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("冻结失败：用户{}，金额：{}，错误：{}", userId, amount, e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public boolean unfreezeAmount(Long userId, BigDecimal amount, String reason) {
        log.info("用户{}解冻资金，金额：{}，原因：{}", userId, amount, reason);

        try {
            UserWallet wallet = getWalletByUserId(userId);
            if (wallet == null) {
                log.error("解冻失败：钱包不存在，用户ID：{}", userId);
                return false;
            }

            if (wallet.getFrozenAmount().compareTo(amount) < 0) {
                log.error("解冻失败：冻结金额不足，用户ID：{}，需要解冻：{}，冻结金额：{}", userId, amount, wallet.getFrozenAmount());
                return false;
            }

            int result = walletMapper.updateFrozenAmount(userId, amount, false);
            if (result > 0) {
                log.info("资金解冻成功: userId={}, amount={}", userId, amount);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("解冻失败：用户{}，金额：{}，错误：{}", userId, amount, e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public boolean freezeWallet(Long userId, String reason) {
        log.info("冻结用户钱包: userId={}, reason={}", userId, reason);
        return walletMapper.freezeWallet(userId) > 0;
    }

    @Override
    @Transactional
    public boolean unfreezeWallet(Long userId, String reason) {
        log.info("解冻用户钱包: userId={}, reason={}", userId, reason);
        return walletMapper.unfreezeWallet(userId) > 0;
    }

    @Override
    public boolean updateWalletStatus(Long userId, String status) {
        return walletMapper.updateWalletStatus(userId, status) > 0;
    }

    @Override
    public boolean checkSufficientBalance(Long userId, BigDecimal amount) {
        UserWallet wallet = getWalletByUserId(userId);
        return wallet != null && wallet.hasSufficientBalance(amount);
    }

    @Override
    public boolean checkSufficientCoinBalance(Long userId, Long amount) {
        UserWallet wallet = getWalletByUserId(userId);
        return wallet != null && wallet.hasSufficientCoinBalance(amount);
    }

    @Override
    public BigDecimal getAvailableBalance(Long userId) {
        UserWallet wallet = getWalletByUserId(userId);
        return wallet != null ? wallet.getAvailableBalance() : BigDecimal.ZERO;
    }

    @Override
    public Long getCoinBalance(Long userId) {
        UserWallet wallet = getWalletByUserId(userId);
        return wallet != null ? wallet.getCoinBalance() : 0L;
    }

    @Override
    public PageResponse<UserWallet> queryWallets(UserWalletQueryRequest request) {
        try {
            int offset = (request.getCurrentPage() - 1) * request.getPageSize();
            
            List<UserWallet> wallets = walletMapper.findWalletsByCondition(
                    request.getUserId(),
                    request.getStatus(),
                    request.getBalanceMin(),
                    request.getBalanceMax(),
                    request.getCoinBalanceMin(),
                    request.getCoinBalanceMax(),
                    request.getSortField(),
                    request.getSortDirection(),
                    offset,
                    request.getPageSize()
            );
            
            Long total = walletMapper.countWalletsByCondition(
                    request.getUserId(),
                    request.getStatus(),
                    request.getBalanceMin(),
                    request.getBalanceMax(),
                    request.getCoinBalanceMin(),
                    request.getCoinBalanceMax()
            );
            
            PageResponse<UserWallet> result = new PageResponse<>();
            result.setDatas(wallets);
            result.setCurrentPage(request.getCurrentPage());
            result.setPageSize(request.getPageSize());
            result.setTotal(total);
            
            return result;
        } catch (Exception e) {
            log.error("查询钱包列表失败", e);
            throw new RuntimeException("查询钱包失败", e);
        }
    }

    @Override
    public List<UserWallet> batchInitializeWallets(List<Long> userIds) {
        // 这里可以批量创建钱包的逻辑
        return userIds.stream()
                .map(userId -> {
                    UserWallet wallet = new UserWallet();
                    wallet.setUserId(userId);
                    return createWallet(wallet);
                })
                .toList();
    }

    @Override
    public boolean syncWalletData(Long userId) {
        // 同步钱包数据的逻辑（比如从交易记录重新计算余额）
        log.info("同步钱包数据: userId={}", userId);
        return true;
    }

    @Override
    public boolean batchSyncWallets(List<Long> userIds) {
        return userIds.stream().allMatch(this::syncWalletData);
    }

    @Override
    public boolean reconcileBalance(Long userId) {
        // 钱包余额对账逻辑
        log.info("钱包余额对账: userId={}", userId);
        return true;
    }

    @Override
    public boolean isWalletHealthy(Long userId) {
        UserWallet wallet = getWalletByUserId(userId);
        return wallet != null && wallet.isActive() && 
               wallet.getBalance().compareTo(BigDecimal.ZERO) >= 0 &&
               wallet.getCoinBalance() >= 0;
    }

    @Override
    @Transactional
    public boolean deleteWallet(Long userId) {
        try {
            UserWallet wallet = getWalletByUserId(userId);
            if (wallet == null) {
                return true;
            }
            
            // 检查是否有余额
            if (wallet.getBalance().compareTo(BigDecimal.ZERO) > 0 || wallet.getCoinBalance() > 0) {
                log.error("删除钱包失败：仍有余额，用户ID：{}", userId);
                return false;
            }
            
            walletMapper.deleteById(wallet.getId());
            log.warn("用户钱包被删除: userId={}", userId);
            return true;
        } catch (Exception e) {
            log.error("删除钱包失败: userId={}", userId, e);
            return false;
        }
    }
}
package com.gig.collide.users.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.users.domain.entity.UserWallet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户钱包Mapper接口 - 对应 t_user_wallet 表
 * 负责用户钱包管理（现金和金币）
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Mapper
public interface UserWalletMapper extends BaseMapper<UserWallet> {

    /**
     * 根据用户ID查询钱包
     */
    UserWallet selectByUserId(@Param("userId") Long userId);

    /**
     * 更新余额
     */
    int updateBalance(@Param("userId") Long userId, 
                     @Param("amount") BigDecimal amount, 
                     @Param("isAdd") boolean isAdd);

    /**
     * 更新冻结金额
     */
    int updateFrozenAmount(@Param("userId") Long userId, 
                          @Param("amount") BigDecimal amount, 
                          @Param("isAdd") boolean isAdd);

    /**
     * 更新总收入
     */
    int updateTotalIncome(@Param("userId") Long userId, 
                         @Param("amount") BigDecimal amount);

    /**
     * 更新总支出
     */
    int updateTotalExpense(@Param("userId") Long userId, 
                          @Param("amount") BigDecimal amount);

    /**
     * 钱包余额扣除（原子操作）
     */
    int deductBalance(@Param("userId") Long userId, 
                     @Param("amount") BigDecimal amount);

    /**
     * 钱包余额充值（原子操作）
     */
    int addBalance(@Param("userId") Long userId, 
                  @Param("amount") BigDecimal amount);

    /**
     * 批量查询用户钱包
     */
    List<UserWallet> selectByUserIds(@Param("userIds") List<Long> userIds);

    /**
     * 分页查询钱包信息
     */
    List<UserWallet> findWalletsByCondition(@Param("userId") Long userId,
                                           @Param("status") String status,
                                           @Param("balanceMin") BigDecimal balanceMin,
                                           @Param("balanceMax") BigDecimal balanceMax,
                                           @Param("coinBalanceMin") Long coinBalanceMin,
                                           @Param("coinBalanceMax") Long coinBalanceMax,
                                           @Param("sortField") String sortField,
                                           @Param("sortDirection") String sortDirection,
                                           @Param("offset") Integer offset,
                                           @Param("size") Integer size);

    /**
     * 统计钱包数量
     */
    Long countWalletsByCondition(@Param("userId") Long userId,
                                @Param("status") String status,
                                @Param("balanceMin") BigDecimal balanceMin,
                                @Param("balanceMax") BigDecimal balanceMax,
                                @Param("coinBalanceMin") Long coinBalanceMin,
                                @Param("coinBalanceMax") Long coinBalanceMax);

    /**
     * 更新金币余额
     */
    int updateCoinBalance(@Param("userId") Long userId, 
                         @Param("amount") Long amount, 
                         @Param("isAdd") boolean isAdd);

    /**
     * 更新金币总收入
     */
    int updateCoinTotalEarned(@Param("userId") Long userId, 
                             @Param("amount") Long amount);

    /**
     * 更新金币总支出
     */
    int updateCoinTotalSpent(@Param("userId") Long userId, 
                            @Param("amount") Long amount);

    /**
     * 金币余额扣除（原子操作）
     */
    int deductCoinBalance(@Param("userId") Long userId, 
                         @Param("amount") Long amount);

    /**
     * 金币余额充值（原子操作）
     */
    int addCoinBalance(@Param("userId") Long userId, 
                      @Param("amount") Long amount);

    /**
     * 冻结钱包
     */
    int freezeWallet(@Param("userId") Long userId);

    /**
     * 解冻钱包
     */
    int unfreezeWallet(@Param("userId") Long userId);

    /**
     * 更新钱包状态
     */
    int updateWalletStatus(@Param("userId") Long userId, @Param("status") String status);

    /**
     * 检查钱包是否存在
     */
    int checkWalletExists(@Param("userId") Long userId);

    /**
     * 获取钱包余额（用于验证）
     */
    BigDecimal getWalletBalance(@Param("userId") Long userId);

    /**
     * 获取金币余额（用于验证）
     */
    Long getCoinBalance(@Param("userId") Long userId);
}
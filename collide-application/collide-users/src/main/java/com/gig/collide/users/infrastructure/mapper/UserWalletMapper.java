package com.gig.collide.users.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.users.domain.entity.UserWallet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * 用户钱包Mapper
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
}
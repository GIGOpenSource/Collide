package com.gig.collide.payment.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.payment.domain.entity.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 支付Mapper接口 - 简洁版
 *
 * @author GIG Team
 * @version 2.0.0
 */
@Mapper
public interface PaymentMapper extends BaseMapper<Payment> {

    /**
     * 根据支付单号查询
     */
    Payment selectByPaymentNo(@Param("paymentNo") String paymentNo);

    /**
     * 根据第三方支付单号查询
     */
    Payment selectByThirdPartyNo(@Param("thirdPartyNo") String thirdPartyNo);

    /**
     * 根据订单ID查询支付记录
     */
    List<Payment> selectByOrderId(@Param("orderId") Long orderId);

    /**
     * 获取用户支付记录
     */
    List<Payment> selectByUserId(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 根据用户ID和状态查询
     */
    List<Payment> selectByUserIdAndStatus(@Param("userId") Long userId, 
                                         @Param("status") String status, 
                                         @Param("limit") Integer limit);

    /**
     * 更新支付状态
     */
    int updatePaymentStatus(@Param("paymentNo") String paymentNo,
                           @Param("status") String status,
                           @Param("thirdPartyNo") String thirdPartyNo,
                           @Param("payTime") LocalDateTime payTime,
                           @Param("notifyTime") LocalDateTime notifyTime);

    /**
     * 根据状态和时间范围查询
     */
    List<Payment> selectByStatusAndTimeRange(@Param("status") String status,
                                            @Param("startTime") LocalDateTime startTime,
                                            @Param("endTime") LocalDateTime endTime);

    /**
     * 统计用户支付金额
     */
    BigDecimal sumAmountByUserId(@Param("userId") Long userId);

    /**
     * 统计用户支付次数
     */
    int countByUserId(@Param("userId") Long userId);

    /**
     * 根据支付方式统计
     */
    List<Payment> selectByPayMethodAndTimeRange(@Param("payMethod") String payMethod,
                                               @Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime);

    /**
     * 批量更新过期支付状态
     */
    int updateExpiredPayments(@Param("expireTime") LocalDateTime expireTime);
} 
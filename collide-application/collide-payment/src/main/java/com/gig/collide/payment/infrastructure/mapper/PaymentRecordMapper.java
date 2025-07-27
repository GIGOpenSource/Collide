package com.gig.collide.payment.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.payment.infrastructure.entity.PaymentRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 支付记录数据访问层（去连表设计 v2.0.0）
 * 
 * @author Collide
 * @since 2.0.0
 */
@Repository
@Mapper
public interface PaymentRecordMapper extends BaseMapper<PaymentRecord> {

    // =============================================
    // 基础查询方法（去连表化）- XML实现
    // =============================================
    
    /**
     * 根据订单号和用户ID查询（去连表设计）
     */
    PaymentRecord selectByOrderNoAndUserId(@Param("orderNo") String orderNo, @Param("userId") Long userId);

    /**
     * 根据内部交易流水号查询
     */
    PaymentRecord selectByInternalTransactionNo(@Param("internalTransactionNo") String internalTransactionNo);

    /**
     * 根据用户ID查询支付记录列表
     */
    List<PaymentRecord> selectByUserId(@Param("userId") Long userId, @Param("payStatus") String payStatus, @Param("limit") Integer limit);

    /**
     * 根据用户名查询支付记录列表（利用冗余字段）
     */
    List<PaymentRecord> selectByUserName(@Param("userName") String userName, @Param("payStatus") String payStatus, @Param("limit") Integer limit);

    /**
     * 根据商户ID查询支付记录列表（利用冗余字段）
     */
    List<PaymentRecord> selectByMerchantId(@Param("merchantId") Long merchantId, @Param("payStatus") String payStatus, 
                                          @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, 
                                          @Param("limit") Integer limit);

    // =============================================
    // 状态相关查询 - XML实现
    // =============================================
    
    /**
     * 查询待支付订单（需要过期处理）
     */
    List<PaymentRecord> selectPendingPayments(@Param("limit") Integer limit);

    // =============================================
    // 统计查询（利用冗余字段避免联表）- XML实现
    // =============================================
    
    /**
     * 统计商户支付金额（利用冗余字段避免联表）
     */
    BigDecimal sumMerchantPayAmount(@Param("merchantId") Long merchantId, @Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 统计支付方式成功率
     */
    List<Map<String, Object>> calculateSuccessRateByPayType(@Param("payType") String payType, 
                                                           @Param("startTime") LocalDateTime startTime, 
                                                           @Param("endTime") LocalDateTime endTime);

    // =============================================
    // 更新方法 - XML实现
    // =============================================
    
    /**
     * 更新支付状态（乐观锁）
     */
    int updatePayStatus(@Param("id") Long id, @Param("version") Integer version, @Param("payStatus") String payStatus, 
                       @Param("actualPayAmount") BigDecimal actualPayAmount, @Param("payTime") LocalDateTime payTime, 
                       @Param("transactionNo") String transactionNo);

    /**
     * 更新回调信息
     */
    int updateNotifyInfo(@Param("id") Long id);

    /**
     * 更新风控信息
     */
    int updateRiskInfo(@Param("id") Long id, @Param("riskLevel") String riskLevel, @Param("riskScore") Integer riskScore, 
                      @Param("failureCode") String failureCode, @Param("failureReason") String failureReason);

    // =============================================
    // 批量操作 - XML实现
    // =============================================
    
    /**
     * 批量更新过期订单状态
     */
    int batchUpdateExpiredOrders(@Param("limit") Integer limit);

    /**
     * 批量更新失败通知状态
     */
    int batchUpdateFailedNotifications(@Param("limit") Integer limit);

    // =============================================
    // 复杂查询 - XML实现
    // =============================================
    
    /**
     * 复杂条件查询支付记录
     */
    List<PaymentRecord> selectByMultipleConditions(@Param("userId") Long userId, @Param("userName") String userName, 
                                                   @Param("merchantId") Long merchantId, @Param("payType") String payType, 
                                                   @Param("payStatus") String payStatus, @Param("keyword") String keyword, 
                                                   @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, 
                                                   @Param("limit") Integer limit);
} 
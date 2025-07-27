package com.gig.collide.payment.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.payment.infrastructure.entity.PaymentCallback;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 支付回调记录数据访问层（去连表设计 v2.0.0）
 * 
 * @author Collide
 * @since 2.0.0
 */
@Repository
@Mapper
public interface PaymentCallbackMapper extends BaseMapper<PaymentCallback> {

    // =============================================
    // 基础查询方法（去连表化）- XML实现
    // =============================================
    
    /**
     * 根据支付记录ID查询回调记录列表
     */
    List<PaymentCallback> selectByPaymentRecordId(@Param("paymentRecordId") Long paymentRecordId);

    /**
     * 根据内部交易号查询回调记录（利用冗余字段）
     */
    List<PaymentCallback> selectByInternalTransactionNo(@Param("internalTransactionNo") String internalTransactionNo);

    /**
     * 根据用户ID查询回调记录（利用冗余字段）
     */
    List<PaymentCallback> selectByUserId(@Param("userId") Long userId, @Param("callbackStatus") String callbackStatus, @Param("limit") Integer limit);

    /**
     * 根据用户名查询回调记录（利用冗余字段）
     */
    List<PaymentCallback> selectByUserName(@Param("userName") String userName, @Param("callbackType") String callbackType, @Param("limit") Integer limit);

    // =============================================
    // 状态和重试相关查询 - XML实现
    // =============================================
    
    /**
     * 查询处理失败的回调记录
     */
    List<PaymentCallback> selectFailedCallbacks(@Param("limit") Integer limit);

    /**
     * 查询待重试的回调记录
     */
    List<PaymentCallback> selectPendingRetryCallbacks(@Param("limit") Integer limit);

    // =============================================
    // 统计查询 - XML实现
    // =============================================
    
    /**
     * 统计回调成功率（按来源统计）
     */
    List<Map<String, Object>> calculateSuccessRateBySource(@Param("callbackSource") String callbackSource, 
                                                           @Param("startTime") LocalDateTime startTime, 
                                                           @Param("endTime") LocalDateTime endTime);

    /**
     * 统计回调处理时间分布
     */
    List<Map<String, Object>> selectProcessTimeDistribution(@Param("startTime") LocalDateTime startTime, 
                                                           @Param("endTime") LocalDateTime endTime);

    /**
     * 查询慢处理的回调记录
     */
    List<PaymentCallback> selectSlowProcessCallbacks(@Param("thresholdMs") Long thresholdMs, @Param("limit") Integer limit);

    // =============================================
    // 更新方法 - XML实现
    // =============================================
    
    /**
     * 更新回调处理结果
     */
    int updateProcessResult(@Param("id") Long id,
                           @Param("callbackStatus") String callbackStatus,
                           @Param("processResult") String processResult,
                           @Param("processMessage") String processMessage,
                           @Param("processTime") LocalDateTime processTime);

    /**
     * 更新回调失败信息
     */
    int updateFailureInfo(@Param("id") Long id,
                         @Param("errorCode") String errorCode,
                         @Param("errorMessage") String errorMessage,
                         @Param("retryIntervalSeconds") Integer retryIntervalSeconds);

    /**
     * 更新签名验证结果
     */
    int updateSignatureValidation(@Param("id") Long id, 
                                 @Param("signatureValid") Boolean signatureValid,
                                 @Param("signatureAlgorithm") String signatureAlgorithm);

    // =============================================
    // 批量操作 - XML实现
    // =============================================
    
    /**
     * 批量更新超时回调状态
     */
    int batchUpdateTimeoutCallbacks(@Param("timeoutMinutes") Integer timeoutMinutes, @Param("limit") Integer limit);

    /**
     * 批量清理过期回调记录
     */
    int batchCleanupExpiredCallbacks(@Param("expireDays") Integer expireDays, @Param("limit") Integer limit);

    // =============================================
    // 复杂查询 - XML实现
    // =============================================
    
    /**
     * 复杂条件查询回调记录
     */
    List<PaymentCallback> selectByMultipleConditions(@Param("paymentRecordId") Long paymentRecordId,
                                                     @Param("userId") Long userId,
                                                     @Param("userName") String userName,
                                                     @Param("callbackType") String callbackType,
                                                     @Param("callbackSource") String callbackSource,
                                                     @Param("callbackStatus") String callbackStatus,
                                                     @Param("keyword") String keyword,
                                                     @Param("startTime") LocalDateTime startTime,
                                                     @Param("endTime") LocalDateTime endTime,
                                                     @Param("limit") Integer limit);
} 
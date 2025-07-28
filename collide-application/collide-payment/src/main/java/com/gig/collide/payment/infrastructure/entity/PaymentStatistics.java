package com.gig.collide.payment.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 支付统计实体类（去连表设计）
 * 对应数据库表：t_payment_statistics
 * 设计原则：通过冗余字段避免联表查询，提高查询性能
 * 
 * @author Collide
 * @since 2.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_payment_statistics")
public class PaymentStatistics {

    // =============================================
    // 基础信息
    // =============================================
    
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标识
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    /**
     * 版本号（乐观锁）
     */
    @Version
    @TableField("version")
    private Integer version;

    // =============================================
    // 统计维度
    // =============================================
    
    /**
     * 统计日期
     */
    @TableField("stat_date")
    private LocalDate statDate;

    /**
     * 统计小时（0-23，null表示全天统计）
     */
    @TableField("stat_hour")
    private Integer statHour;

    /**
     * 统计类型（DAILY-日统计、HOURLY-小时统计、MONTHLY-月统计）
     */
    @TableField("stat_type")
    private String statType;

    // =============================================
    // 商户信息（冗余字段，避免联表查询）
    // =============================================
    
    /**
     * 商户ID（冗余字段）
     */
    @TableField("merchant_id")
    private Long merchantId;

    /**
     * 商户名称（冗余字段）
     */
    @TableField("merchant_name")
    private String merchantName;

    // =============================================
    // 支付方式信息
    // =============================================
    
    /**
     * 支付类型（ALIPAY-支付宝、WECHAT-微信、BANK-银行卡等）
     */
    @TableField("pay_type")
    private String payType;

    /**
     * 支付场景（WEB-网页、APP-应用、H5-手机网页等）
     */
    @TableField("pay_scene")
    private String payScene;

    // =============================================
    // 基础统计数据
    // =============================================
    
    /**
     * 总交易笔数
     */
    @TableField("total_count")
    private Long totalCount;

    /**
     * 成功交易笔数
     */
    @TableField("success_count")
    private Long successCount;

    /**
     * 失败交易笔数
     */
    @TableField("failed_count")
    private Long failedCount;

    /**
     * 处理中交易笔数
     */
    @TableField("pending_count")
    private Long pendingCount;

    /**
     * 取消交易笔数
     */
    @TableField("cancel_count")
    private Long cancelCount;

    // =============================================
    // 金额统计
    // =============================================
    
    /**
     * 总交易金额
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /**
     * 成功交易金额
     */
    @TableField("success_amount")
    private BigDecimal successAmount;

    /**
     * 退款金额
     */
    @TableField("refund_amount")
    private BigDecimal refundAmount;

    /**
     * 手续费金额
     */
    @TableField("fee_amount")
    private BigDecimal feeAmount;

    // =============================================
    // 成功率统计
    // =============================================
    
    /**
     * 支付成功率（百分比，如：95.50）
     */
    @TableField("success_rate")
    private BigDecimal successRate;

    /**
     * 平均交易金额
     */
    @TableField("avg_amount")
    private BigDecimal avgAmount;

    // =============================================
    // 时间统计
    // =============================================
    
    /**
     * 平均处理时间（秒）
     */
    @TableField("avg_process_time")
    private Long avgProcessTime;

    /**
     * 最大处理时间（秒）
     */
    @TableField("max_process_time")
    private Long maxProcessTime;

    /**
     * 最小处理时间（秒）
     */
    @TableField("min_process_time")
    private Long minProcessTime;

    // =============================================
    // 用户统计
    // =============================================
    
    /**
     * 唯一用户数
     */
    @TableField("unique_user_count")
    private Long uniqueUserCount;

    /**
     * 新用户数
     */
    @TableField("new_user_count")
    private Long newUserCount;

    // =============================================
    // 环境信息
    // =============================================
    
    /**
     * 环境配置（dev-开发、test-测试、prod-生产）
     */
    @TableField("env_profile")
    private String envProfile;

    // =============================================
    // 扩展信息
    // =============================================
    
    /**
     * 扩展统计数据（JSON格式）
     */
    @TableField("extra_stats")
    private String extraStats;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
} 
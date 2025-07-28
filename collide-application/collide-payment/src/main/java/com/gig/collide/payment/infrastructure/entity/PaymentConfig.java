package com.gig.collide.payment.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付配置实体类（去连表设计）
 * 对应数据库表：t_payment_config
 * 设计原则：通过冗余字段避免联表查询，提高查询性能
 * 
 * @author Collide
 * @since 2.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_payment_config")
public class PaymentConfig {

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
    // 配置基础信息
    // =============================================
    
    /**
     * 配置键
     */
    @TableField("config_key")
    private String configKey;

    /**
     * 配置类型（PAYMENT-支付配置、MERCHANT-商户配置、LIMIT-限额配置等）
     */
    @TableField("config_type")
    private String configType;

    /**
     * 配置名称
     */
    @TableField("config_name")
    private String configName;

    /**
     * 配置值
     */
    @TableField("config_value")
    private String configValue;

    /**
     * 配置状态（ENABLED-启用、DISABLED-禁用）
     */
    @TableField("config_status")
    private String configStatus;

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

    /**
     * 商户类型（冗余字段）
     */
    @TableField("merchant_type")
    private String merchantType;

    // =============================================
    // 环境信息
    // =============================================
    
    /**
     * 环境配置（dev-开发、test-测试、prod-生产）
     */
    @TableField("env_profile")
    private String envProfile;

    /**
     * 平台（WEB-网页、APP-应用、H5-手机网页等）
     */
    @TableField("platform")
    private String platform;

    // =============================================
    // 支付方式配置
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

    /**
     * 支持的支付方式（JSON格式）
     */
    @TableField("supported_methods")
    private String supportedMethods;

    // =============================================
    // 限额配置
    // =============================================
    
    /**
     * 最小金额（分）
     */
    @TableField("min_amount")
    private BigDecimal minAmount;

    /**
     * 最大金额（分）
     */
    @TableField("max_amount")
    private BigDecimal maxAmount;

    /**
     * 日限额（分）
     */
    @TableField("daily_limit")
    private BigDecimal dailyLimit;

    /**
     * 月限额（分）
     */
    @TableField("monthly_limit")
    private BigDecimal monthlyLimit;

    // =============================================
    // 费率配置
    // =============================================
    
    /**
     * 费率（百分比，如：0.6表示0.6%）
     */
    @TableField("fee_rate")
    private BigDecimal feeRate;

    /**
     * 固定手续费（分）
     */
    @TableField("fixed_fee")
    private BigDecimal fixedFee;

    /**
     * 费率类型（RATE-按比例、FIXED-固定金额、MIXED-混合）
     */
    @TableField("fee_type")
    private String feeType;

    // =============================================
    // 回调配置
    // =============================================
    
    /**
     * 回调通知URL
     */
    @TableField("notify_url")
    private String notifyUrl;

    /**
     * 最大重试次数
     */
    @TableField("max_retry_count")
    private Integer maxRetryCount;

    /**
     * 重试间隔（秒）
     */
    @TableField("retry_interval")
    private Integer retryInterval;

    /**
     * 超时时间（秒）
     */
    @TableField("timeout_seconds")
    private Integer timeoutSeconds;

    // =============================================
    // 安全配置
    // =============================================
    
    /**
     * 加密密钥
     */
    @TableField("encrypt_key")
    private String encryptKey;

    /**
     * 签名密钥
     */
    @TableField("sign_key")
    private String signKey;

    /**
     * 加密算法
     */
    @TableField("algorithm")
    private String algorithm;

    /**
     * 字符编码
     */
    @TableField("charset")
    private String charset;

    // =============================================
    // 扩展配置
    // =============================================
    
    /**
     * 扩展配置（JSON格式）
     */
    @TableField("extra_config")
    private String extraConfig;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    // =============================================
    // 生效时间
    // =============================================
    
    /**
     * 生效时间
     */
    @TableField("effective_time")
    private LocalDateTime effectiveTime;

    /**
     * 过期时间
     */
    @TableField("expire_time")
    private LocalDateTime expireTime;

    // =============================================
    // 操作信息
    // =============================================
    
    /**
     * 操作人ID
     */
    @TableField("operator_id")
    private Long operatorId;

    /**
     * 操作人姓名
     */
    @TableField("operator_name")
    private String operatorName;
} 
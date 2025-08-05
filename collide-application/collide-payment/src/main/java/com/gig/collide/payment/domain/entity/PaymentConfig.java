package com.gig.collide.payment.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 支付配置实体
 * 对应t_payment_config表
 * 用于管理各种支付平台的配置信息
 *
 * @author GIG Team
 * @version 1.0.0
 */
@Data
@TableName("t_payment_config")
public class PaymentConfig {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 平台名字（如：shark_pay、alipay、wechat_pay等）
     */
    private String platformName;

    /**
     * 平台base_url（API接口基础地址）
     */
    private String baseUrl;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户密钥
     */
    private String appSecret;

    /**
     * 子商户ID（可选）
     */
    private String subMerchantId;

    /**
     * 支付渠道（默认H5）
     */
    private String paymentMethod;

    /**
     * 回调通知地址
     */
    private String notifyUrl;

    /**
     * 签名类型（MD5、SHA256、RSA等）
     */
    private String signType;

    /**
     * 软删除标记（0：未删除，大于0：已删除）
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
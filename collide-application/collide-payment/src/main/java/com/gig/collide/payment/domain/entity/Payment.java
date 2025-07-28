package com.gig.collide.payment.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付实体 - 简洁版
 * 对应t_payment表
 *
 * @author GIG Team
 * @version 2.0.0
 */
@Data
@TableName("t_payment")
public class Payment {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 支付单号
     */
    private String paymentNo;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单号（冗余）
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户昵称（冗余）
     */
    private String userNickname;

    /**
     * 支付金额
     */
    private BigDecimal amount;

    /**
     * 支付方式：alipay、wechat、balance
     */
    private String payMethod;

    /**
     * 支付渠道
     */
    private String payChannel;

    /**
     * 第三方支付单号
     */
    private String thirdPartyNo;

    /**
     * 支付状态：pending、success、failed、cancelled
     */
    private String status;

    /**
     * 支付完成时间
     */
    private LocalDateTime payTime;

    /**
     * 回调通知时间
     */
    private LocalDateTime notifyTime;

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
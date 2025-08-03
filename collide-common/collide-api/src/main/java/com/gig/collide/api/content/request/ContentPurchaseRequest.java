package com.gig.collide.api.content.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 内容购买请求
 * 用于用户购买付费内容
 *
 * @author GIG Team
 * @version 2.0.0 (内容付费版)
 * @since 2024-01-31
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ContentPurchaseRequest implements Serializable {

    /**
     * 内容ID
     */
    @NotNull(message = "内容ID不能为空")
    @Min(value = 1, message = "内容ID必须大于0")
    private Long contentId;

    /**
     * 用户ID（通常从上下文获取，这里作为备用）
     */
    private Long userId;

    /**
     * 支付方式：COIN、BALANCE（暂时支持金币支付）
     */
    private String paymentMethod = "COIN";

    /**
     * 是否确认购买价格
     * 前端需要先获取价格，用户确认后再发起购买
     */
    @NotNull(message = "必须确认购买价格")
    private Boolean confirmPrice;

    /**
     * 确认的价格（金币数量）
     * 用于防止价格变动导致的错误扣费
     */
    @NotNull(message = "确认价格不能为空")
    @Min(value = 0, message = "确认价格不能为负数")
    private Long confirmedPrice;

    /**
     * 客户端类型：WEB、MOBILE、APP
     */
    private String clientType;

    /**
     * 备注信息
     */
    private String remark;
}
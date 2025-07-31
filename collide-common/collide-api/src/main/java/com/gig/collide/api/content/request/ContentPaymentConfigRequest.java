package com.gig.collide.api.content.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 内容付费配置请求
 * 用于创建和更新内容的付费策略
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
public class ContentPaymentConfigRequest implements Serializable {

    /**
     * 内容ID（创建时必填）
     */
    @NotNull(message = "内容ID不能为空")
    @Min(value = 1, message = "内容ID必须大于0")
    private Long contentId;

    /**
     * 付费类型：FREE、COIN_PAY、VIP_FREE、TIME_LIMITED
     */
    @NotBlank(message = "付费类型不能为空")
    private String paymentType;

    /**
     * 金币价格
     */
    @Min(value = 0, message = "金币价格不能为负数")
    private Long coinPrice = 0L;

    /**
     * 原价（用于折扣显示）
     */
    @Min(value = 0, message = "原价不能为负数")
    private Long originalPrice;

    /**
     * 会员免费：false否，true是
     */
    private Boolean vipFree = false;

    /**
     * 是否只有VIP才可以购买：false否，true是
     */
    private Boolean vipOnly = false;

    /**
     * 是否支持试读：false否，true是
     */
    private Boolean trialEnabled = false;

    /**
     * 试读内容
     */
    private String trialContent;

    /**
     * 试读字数
     */
    @Min(value = 0, message = "试读字数不能为负数")
    private Integer trialWordCount = 0;

    /**
     * 是否永久有效：false否，true是
     */
    private Boolean isPermanent = true;

    /**
     * 有效天数（非永久时使用）
     */
    @Min(value = 1, message = "有效天数必须大于0")
    private Integer validDays;

    /**
     * 状态：ACTIVE、INACTIVE
     */
    private String status = "ACTIVE";

    /**
     * 备注信息
     */
    private String remark;
}
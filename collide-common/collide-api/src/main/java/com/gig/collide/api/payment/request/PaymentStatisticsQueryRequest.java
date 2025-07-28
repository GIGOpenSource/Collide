package com.gig.collide.api.payment.request;

import com.gig.collide.api.payment.constant.PaymentTypeEnum;
import com.gig.collide.api.payment.constant.PaymentSceneEnum;
import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

/**
 * 支付统计查询请求
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PaymentStatisticsQueryRequest extends BaseRequest {

    /**
     * 统计日期开始（可选）
     */
    private LocalDate statDateStart;

    /**
     * 统计日期结束（可选）
     */
    private LocalDate statDateEnd;

    /**
     * 统计小时（可选，0-23，NULL表示日统计）
     */
    @Min(value = 0, message = "统计小时不能小于0")
    @Max(value = 23, message = "统计小时不能大于23")
    private Integer statHour;

    /**
     * 支付方式（可选）
     */
    private PaymentTypeEnum payType;

    /**
     * 支付场景（可选）
     */
    private PaymentSceneEnum payScene;

    /**
     * 商户ID（可选）
     */
    @Positive(message = "商户ID必须为正数")
    private Long merchantId;

    /**
     * 商户名称（可选）
     */
    @Size(max = 128, message = "商户名称长度不能超过128个字符")
    private String merchantName;

    /**
     * 用户类型（可选）
     */
    @Size(max = 20, message = "用户类型长度不能超过20个字符")
    private String userType;

    /**
     * 商品类型（可选）
     */
    @Size(max = 50, message = "商品类型长度不能超过50个字符")
    private String productType;

    /**
     * 是否包含小时数据（可选，默认false）
     */
    private Boolean includeHourly = false;

    /**
     * 排序字段（可选，默认stat_date）
     */
    @Size(max = 50, message = "排序字段长度不能超过50个字符")
    private String orderBy = "stat_date";

    /**
     * 排序方向（可选，默认DESC）
     */
    @Pattern(regexp = "^(ASC|DESC)$", message = "排序方向只能是ASC或DESC")
    private String orderDirection = "DESC";
} 
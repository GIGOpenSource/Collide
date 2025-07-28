package com.gig.collide.api.payment.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 支付统计排行榜请求
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class PaymentStatisticsRankingRequest extends BaseRequest {

    /**
     * 开始时间
     */
    @NotNull(message = "开始时间不能为空")
    private Long startTime;

    /**
     * 结束时间
     */
    @NotNull(message = "结束时间不能为空")
    private Long endTime;

    /**
     * 排行榜类型（USER、PAYMENT_METHOD、MERCHANT等）
     */
    private String rankingType = "USER";

    /**
     * 排序字段（AMOUNT、COUNT、SUCCESS_RATE等）
     */
    private String sortField = "AMOUNT";

    /**
     * 排序方向（ASC、DESC）
     */
    private String sortOrder = "DESC";

    /**
     * 返回条数
     */
    @Min(value = 1, message = "返回条数最小为1")
    @Max(value = 100, message = "返回条数最大为100")
    private Integer limit = 10;
} 
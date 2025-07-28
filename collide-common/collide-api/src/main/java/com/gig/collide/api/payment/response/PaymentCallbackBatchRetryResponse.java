package com.gig.collide.api.payment.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 支付回调批量重试响应
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
public class PaymentCallbackBatchRetryResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 总处理数量
     */
    private Integer totalCount;

    /**
     * 成功重试数量
     */
    private Integer successCount;

    /**
     * 失败重试数量
     */
    private Integer failedCount;

    /**
     * 跳过数量（已达最大重试次数等）
     */
    private Integer skippedCount;

    /**
     * 批量处理开始时间
     */
    private String batchStartTime;

    /**
     * 批量处理结束时间
     */
    private String batchEndTime;

    /**
     * 批量处理耗时（毫秒）
     */
    private Long batchProcessTimeMs;

    /**
     * 成功重试的回调ID列表
     */
    private List<Long> successCallbackIds;

    /**
     * 失败重试的回调ID列表
     */
    private List<Long> failedCallbackIds;

    /**
     * 跳过的回调ID列表
     */
    private List<Long> skippedCallbackIds;

    /**
     * 批量处理详情信息
     */
    private String batchDetails;
} 
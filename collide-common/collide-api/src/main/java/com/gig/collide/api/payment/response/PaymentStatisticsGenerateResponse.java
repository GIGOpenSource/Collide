package com.gig.collide.api.payment.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 支付统计生成响应
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
public class PaymentStatisticsGenerateResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 统计日期
     */
    private String statDate;

    /**
     * 生成的统计记录数量
     */
    private Integer generatedRecordCount;

    /**
     * 是否包含小时统计
     */
    private Boolean includeHourly;

    /**
     * 生成开始时间
     */
    private String generateStartTime;

    /**
     * 生成结束时间
     */
    private String generateEndTime;

    /**
     * 生成耗时（毫秒）
     */
    private Long generateTimeMs;

    /**
     * 是否强制重新生成
     */
    private Boolean forceRegenerate;

    /**
     * 处理的原始数据条数
     */
    private Long processedRawDataCount;

    /**
     * 生成详情信息
     */
    private String generateDetails;
} 
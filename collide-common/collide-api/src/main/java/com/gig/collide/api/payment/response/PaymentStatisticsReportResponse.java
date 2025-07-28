package com.gig.collide.api.payment.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * 支付统计报告响应
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
public class PaymentStatisticsReportResponse extends BaseResponse {

    /**
     * 报告ID
     */
    private String reportId;

    /**
     * 报告内容
     */
    private String reportContent;

    /**
     * 报告格式
     */
    private String reportFormat;

    /**
     * 报告文件名
     */
    private String reportFileName;

    /**
     * 报告摘要
     */
    private Map<String, Object> reportSummary;

    /**
     * 生成时间
     */
    private Long generateTime;

    /**
     * 报告大小（字节）
     */
    private Long reportSize;

    /**
     * 下载链接
     */
    private String downloadUrl;
} 
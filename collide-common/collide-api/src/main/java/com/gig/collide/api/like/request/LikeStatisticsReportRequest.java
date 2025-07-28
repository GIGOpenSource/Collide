package com.gig.collide.api.like.request;

import com.gig.collide.base.request.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 点赞统计报告请求
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "点赞统计报告请求")
public class LikeStatisticsReportRequest extends BaseRequest {

    /**
     * 报告类型
     */
    @Schema(description = "报告类型")
    private String reportType = "SUMMARY";

    /**
     * 报告格式
     */
    @Schema(description = "报告格式")
    private String format = "JSON";
} 
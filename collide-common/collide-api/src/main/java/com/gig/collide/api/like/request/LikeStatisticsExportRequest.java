package com.gig.collide.api.like.request;

import com.gig.collide.base.request.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 点赞统计导出请求
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
@Schema(description = "点赞统计导出请求")
public class LikeStatisticsExportRequest extends BaseRequest {

    /**
     * 导出格式
     */
    @Schema(description = "导出格式：CSV/EXCEL/JSON")
    private String format = "CSV";

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private LocalDateTime endTime;
} 
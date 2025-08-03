package com.gig.collide.api.ads.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

/**
 * 广告投放查询请求
 *
 * @author GIG Team
 * @version 2.0.0
 * @since 2024-01-16
 */
@Data
@Schema(description = "广告投放查询请求")
public class AdCampaignQueryRequest {

    @Schema(description = "当前页码", example = "1")
    @Min(value = 1, message = "页码必须大于0")
    private Integer currentPage = 1;

    @Schema(description = "每页数量", example = "10")
    @Min(value = 1, message = "每页数量必须大于0")
    @Max(value = 100, message = "每页数量不能超过100")
    private Integer pageSize = 10;

    @Schema(description = "投放名称", example = "春节促销")
    @Size(max = 100, message = "投放名称长度不能超过100字符")
    private String campaignName;

    @Schema(description = "广告模板ID", example = "1")
    private Long adId;

    @Schema(description = "广告位ID", example = "1")
    private Long placementId;

    @Schema(description = "状态", example = "active", allowableValues = {"active", "paused", "completed"})
    @Pattern(regexp = "^(active|paused|completed)$", message = "状态必须是active、paused或completed")
    private String status;

    @Schema(description = "开始日期", example = "2024-02-01")
    private LocalDate startDate;

    @Schema(description = "结束日期", example = "2024-02-28")
    private LocalDate endDate;

    @Schema(description = "最小权重", example = "50")
    @Min(value = 1, message = "最小权重不能小于1")
    private Integer minWeight;

    @Schema(description = "最大权重", example = "500")
    @Max(value = 10000, message = "最大权重不能大于10000")
    private Integer maxWeight;

    @Schema(description = "排序字段", example = "create_time")
    @Size(max = 50, message = "排序字段长度不能超过50字符")
    private String orderBy;

    @Schema(description = "排序方向", example = "desc", allowableValues = {"asc", "desc"})
    @Pattern(regexp = "^(asc|desc)$", message = "排序方向必须是asc或desc")
    private String orderDirection = "desc";
}
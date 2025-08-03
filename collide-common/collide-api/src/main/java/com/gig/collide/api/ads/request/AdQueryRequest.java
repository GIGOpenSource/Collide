package com.gig.collide.api.ads.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 查询广告请求 - 极简版
 * 
 * @author GIG Team
 * @version 3.0.0 (极简版)
 * @since 2024-01-16
 */
@Data
@Schema(description = "查询广告请求")
public class AdQueryRequest {

    @Schema(description = "广告名称 (模糊搜索)", example = "商品")
    private String adName;

    @Pattern(regexp = "^(banner|sidebar|popup|modal)$", message = "广告类型只能是: banner, sidebar, popup, modal")
    @Schema(description = "广告类型", example = "banner", allowableValues = {"banner", "sidebar", "popup", "modal"})
    private String adType;

    @Schema(description = "是否启用 (1:启用 0:禁用)", example = "1")
    private Integer isActive;

    @Min(value = 1, message = "当前页码不能小于1")
    @Schema(description = "当前页码", example = "1")
    private Integer currentPage = 1;

    @Min(value = 1, message = "每页大小不能小于1")
    @Max(value = 100, message = "每页大小不能超过100")
    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;
}
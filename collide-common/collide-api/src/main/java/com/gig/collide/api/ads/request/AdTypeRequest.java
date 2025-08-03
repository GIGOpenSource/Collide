package com.gig.collide.api.ads.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 根据类型获取广告请求 - 极简版
 * 
 * @author GIG Team
 * @version 3.0.0 (极简版)
 * @since 2024-01-16
 */
@Data
@Schema(description = "根据类型获取广告请求")
public class AdTypeRequest {

    @NotBlank(message = "广告类型不能为空")
    @Pattern(regexp = "^(banner|sidebar|popup|modal)$", message = "广告类型只能是: banner, sidebar, popup, modal")
    @Schema(description = "广告类型", example = "banner", allowableValues = {"banner", "sidebar", "popup", "modal"})
    private String adType;

    @Min(value = 1, message = "返回数量不能小于1")
    @Max(value = 50, message = "返回数量不能超过50")
    @Schema(description = "返回数量 (默认10)", example = "10")
    private Integer limit = 10;

    @Schema(description = "是否随机返回", example = "true")
    private Boolean random = false;
}
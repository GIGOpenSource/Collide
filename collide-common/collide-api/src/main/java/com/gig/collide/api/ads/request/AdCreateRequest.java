package com.gig.collide.api.ads.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建广告请求 - 极简版
 * 
 * @author GIG Team
 * @version 3.0.0 (极简版)
 * @since 2024-01-16
 */
@Data
@Schema(description = "创建广告请求")
public class AdCreateRequest {

    @NotBlank(message = "广告名称不能为空")
    @Size(max = 200, message = "广告名称不能超过200个字符")
    @Schema(description = "广告名称", example = "首页推荐商品广告")
    private String adName;

    @NotBlank(message = "广告标题不能为空")
    @Size(max = 300, message = "广告标题不能超过300个字符")
    @Schema(description = "广告标题", example = "限时特惠 - 精选商品低至3折")
    private String adTitle;

    @Size(max = 500, message = "广告描述不能超过500个字符")
    @Schema(description = "广告描述", example = "这是一个精美的商品推广广告")
    private String adDescription;

    @NotBlank(message = "广告类型不能为空")
    @Pattern(regexp = "^(banner|sidebar|popup|modal)$", message = "广告类型只能是: banner, sidebar, popup, modal")
    @Schema(description = "广告类型", example = "banner", allowableValues = {"banner", "sidebar", "popup", "modal"})
    private String adType;

    @NotBlank(message = "图片链接不能为空")
    @Size(max = 500, message = "图片链接不能超过500个字符")
    @Schema(description = "图片链接", example = "https://example.com/images/ad.jpg")
    private String imageUrl;

    @NotBlank(message = "点击链接不能为空")
    @Size(max = 500, message = "点击链接不能超过500个字符")
    @Schema(description = "点击链接", example = "https://example.com/products/1")
    private String clickUrl;

    @Schema(description = "是否启用 (1:启用 0:禁用)", example = "1")
    private Integer isActive = 1;

    @Schema(description = "排序权重 (数值越大越靠前)", example = "100")
    private Integer sortOrder = 0;
}
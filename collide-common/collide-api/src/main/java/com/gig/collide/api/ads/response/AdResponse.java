package com.gig.collide.api.ads.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 广告响应 - 极简版
 * 
 * @author GIG Team
 * @version 3.0.0 (极简版)
 * @since 2024-01-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "广告响应")
public class AdResponse {

    @Schema(description = "广告ID", example = "1")
    private Long id;

    @Schema(description = "广告名称", example = "首页推荐商品广告")
    private String adName;

    @Schema(description = "广告标题", example = "限时特惠 - 精选商品低至3折")
    private String adTitle;

    @Schema(description = "广告描述", example = "这是一个精美的商品推广广告")
    private String adDescription;

    @Schema(description = "广告类型", example = "banner")
    private String adType;

    @Schema(description = "图片链接", example = "https://example.com/images/ad.jpg")
    private String imageUrl;

    @Schema(description = "点击链接", example = "https://example.com/products/1")
    private String clickUrl;

    @Schema(description = "是否启用 (1:启用 0:禁用)", example = "1")
    private Integer isActive;

    @Schema(description = "排序权重", example = "100")
    private Integer sortOrder;

    @Schema(description = "创建时间", example = "2024-01-16T10:30:00")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2024-01-16T10:30:00")
    private LocalDateTime updateTime;
}
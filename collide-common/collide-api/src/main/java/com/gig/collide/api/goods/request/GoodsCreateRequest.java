package com.gig.collide.api.goods.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品创建请求DTO
 * 用于创建新商品的参数传递
 *
 * @author GIG Team
 * @version 2.0.0 (扩展版)
 * @since 2024-01-31
 */
@Data
@Accessors(chain = true)
@Schema(description = "商品创建请求")
public class GoodsCreateRequest {

    @NotBlank(message = "商品名称不能为空")
    @Size(max = 200, message = "商品名称长度不能超过200字符")
    @Schema(description = "商品名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Size(max = 2000, message = "商品描述长度不能超过2000字符")
    @Schema(description = "商品描述")
    private String description;

    @NotNull(message = "分类ID不能为空")
    @Positive(message = "分类ID必须为正数")
    @Schema(description = "分类ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long categoryId;

    @Schema(description = "分类名称（可选，系统会自动填充）")
    private String categoryName;

    @NotBlank(message = "商品类型不能为空")
    @Pattern(regexp = "^(coin|goods|subscription|content)$",
             message = "商品类型只能是: coin、goods、subscription、content")
    @Schema(description = "商品类型", 
            allowableValues = {"coin", "goods", "subscription", "content"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String goodsType;

    @DecimalMin(value = "0.01", message = "现金价格必须大于0.01")
    @Digits(integer = 8, fraction = 2, message = "价格格式不正确")
    @Schema(description = "现金价格（内容类型可为空）")
    private BigDecimal price;

    @DecimalMin(value = "0.01", message = "原价必须大于0.01")
    @Digits(integer = 8, fraction = 2, message = "原价格式不正确")
    @Schema(description = "原价")
    private BigDecimal originalPrice;

    @Min(value = 1, message = "金币价格必须大于0")
    @Schema(description = "金币价格（内容类型专用）")
    private Long coinPrice;

    @Min(value = 1, message = "金币数量必须大于0")
    @Schema(description = "金币数量（金币类商品专用）")
    private Long coinAmount;

    @Schema(description = "关联内容ID（内容类型专用）")
    private Long contentId;

    @Size(max = 200, message = "内容标题长度不能超过200字符")
    @Schema(description = "内容标题（内容类型专用）")
    private String contentTitle;

    @Min(value = 1, message = "订阅时长必须大于0天")
    @Schema(description = "订阅时长（天数，订阅类型专用）")
    private Integer subscriptionDuration;

    @Size(max = 50, message = "订阅类型长度不能超过50字符")
    @Schema(description = "订阅类型（订阅类型专用）")
    private String subscriptionType;

    @Min(value = -1, message = "库存数量不能小于-1")
    @Schema(description = "库存数量（-1表示无限库存，默认虚拟商品为-1）")
    private Integer stock;

    @Size(max = 500, message = "封面图URL长度不能超过500字符")
    @Schema(description = "商品封面图URL")
    private String coverUrl;

    @Schema(description = "商品图片URL列表")
    private List<String> images;

    @NotNull(message = "商家ID不能为空")
    @Positive(message = "商家ID必须为正数")
    @Schema(description = "商家ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long sellerId;

    @Size(max = 100, message = "商家名称长度不能超过100字符")
    @Schema(description = "商家名称（可选，系统会自动填充）")
    private String sellerName;

    @Pattern(regexp = "^(active|inactive)$", message = "状态只能是: active、inactive")
    @Schema(description = "商品状态", allowableValues = {"active", "inactive"}, defaultValue = "active")
    private String status = "active";

    /**
     * 验证商品类型相关字段
     */
    public void validateTypeSpecificFields() {
        switch (goodsType) {
            case "coin":
                if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("金币充值包必须设置现金价格");
                }
                if (coinAmount == null || coinAmount <= 0) {
                    throw new IllegalArgumentException("金币充值包必须设置金币数量");
                }
                break;
                
            case "content":
                if (coinPrice == null || coinPrice <= 0) {
                    throw new IllegalArgumentException("付费内容必须设置金币价格");
                }
                if (contentId == null) {
                    throw new IllegalArgumentException("付费内容必须关联内容ID");
                }
                break;
                
            case "subscription":
                if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("订阅服务必须设置现金价格");
                }
                if (subscriptionDuration == null || subscriptionDuration <= 0) {
                    throw new IllegalArgumentException("订阅服务必须设置订阅时长");
                }
                break;
                
            case "goods":
                if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("实体商品必须设置现金价格");
                }
                break;
        }
    }

    /**
     * 是否为虚拟商品
     */
    public boolean isVirtual() {
        return "coin".equals(goodsType) || "subscription".equals(goodsType) || "content".equals(goodsType);
    }
}
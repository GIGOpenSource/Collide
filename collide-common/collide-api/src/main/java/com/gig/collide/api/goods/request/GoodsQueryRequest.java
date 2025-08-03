package com.gig.collide.api.goods.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 商品查询请求DTO
 * 用于商品列表查询的参数传递
 *
 * @author GIG Team
 * @version 2.0.0 (扩展版)
 * @since 2024-01-31
 */
@Data
@Accessors(chain = true)
@Schema(description = "商品查询请求")
public class GoodsQueryRequest {

    @Min(value = 1, message = "当前页码必须大于0")
    @Schema(description = "当前页码", defaultValue = "1")
    private Integer currentPage = 1;

    @Min(value = 1, message = "页面大小必须大于0")
    @Schema(description = "页面大小", defaultValue = "20")
    private Integer pageSize = 20;

    @Pattern(regexp = "^(coin|goods|subscription|content)$", 
             message = "商品类型只能是: coin、goods、subscription、content")
    @Schema(description = "商品类型", allowableValues = {"coin", "goods", "subscription", "content"})
    private String goodsType;

    @Pattern(regexp = "^(active|inactive|sold_out)$", 
             message = "商品状态只能是: active、inactive、sold_out")
    @Schema(description = "商品状态", allowableValues = {"active", "inactive", "sold_out"})
    private String status;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "商家ID")
    private Long sellerId;

    @Schema(description = "搜索关键词（商品名称、描述）")
    private String keyword;

    @Schema(description = "最低价格")
    private BigDecimal minPrice;

    @Schema(description = "最高价格")
    private BigDecimal maxPrice;

    @Schema(description = "最低金币价格（内容类商品）")
    private Long minCoinPrice;

    @Schema(description = "最高金币价格（内容类商品）")
    private Long maxCoinPrice;

    @Pattern(regexp = "^(create_time|sales_count|view_count|price|coin_price)$",
             message = "排序字段只能是: create_time、sales_count、view_count、price、coin_price")
    @Schema(description = "排序字段", 
            allowableValues = {"create_time", "sales_count", "view_count", "price", "coin_price"},
            defaultValue = "create_time")
    private String sortField = "create_time";

    @Pattern(regexp = "^(ASC|DESC)$", message = "排序方向只能是: ASC、DESC")
    @Schema(description = "排序方向", allowableValues = {"ASC", "DESC"}, defaultValue = "DESC")
    private String sortDirection = "DESC";

    @Schema(description = "是否包含已下架商品", defaultValue = "false")
    private Boolean includeInactive = false;

    @Schema(description = "是否只查询有库存商品", defaultValue = "false")
    private Boolean onlyInStock = false;

    @Schema(description = "是否只查询虚拟商品", defaultValue = "false")
    private Boolean onlyVirtual = false;

    @Schema(description = "是否只查询实体商品", defaultValue = "false")
    private Boolean onlyPhysical = false;

    /**
     * 获取有效的价格范围（根据商品类型）
     */
    public Object[] getEffectivePriceRange() {
        if ("content".equals(goodsType)) {
            return new Object[]{minCoinPrice, maxCoinPrice};
        } else {
            return new Object[]{minPrice, maxPrice};
        }
    }

    /**
     * 获取有效的商品状态列表
     */
    public String[] getEffectiveStatuses() {
        if (includeInactive) {
            return new String[]{"active", "inactive", "sold_out"};
        } else {
            return new String[]{"active"};
        }
    }

    /**
     * 获取有效的商品类型列表
     */
    public String[] getEffectiveGoodsTypes() {
        if (onlyVirtual) {
            return new String[]{"coin", "subscription", "content"};
        } else if (onlyPhysical) {
            return new String[]{"goods"};
        } else if (goodsType != null) {
            return new String[]{goodsType};
        } else {
            return new String[]{"coin", "goods", "subscription", "content"};
        }
    }

    /**
     * 验证查询参数
     */
    public void validateParams() {
        if (onlyVirtual && onlyPhysical) {
            throw new IllegalArgumentException("不能同时只查询虚拟商品和实体商品");
        }
        
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("最低价格不能大于最高价格");
        }
        
        if (minCoinPrice != null && maxCoinPrice != null && minCoinPrice > maxCoinPrice) {
            throw new IllegalArgumentException("最低金币价格不能大于最高金币价格");
        }
        
        if (currentPage != null && currentPage <= 0) {
            throw new IllegalArgumentException("页码必须大于0");
        }
        
        if (pageSize != null && (pageSize <= 0 || pageSize > 100)) {
            throw new IllegalArgumentException("页面大小必须在1-100之间");
        }
    }
}
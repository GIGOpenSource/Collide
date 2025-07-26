package com.gig.collide.api.goods.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

/**
 * 商品更新请求
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
@Schema(description = "商品更新请求")
public class GoodsUpdateRequest {

    @Schema(description = "商品ID", required = true)
    private Long goodsId;

    @Schema(description = "商品名称")
    private String name;

    @Schema(description = "商品描述")
    private String description;

    @Schema(description = "商品价格")
    private BigDecimal price;

    @Schema(description = "商品库存")
    private Integer stock;

    @Schema(description = "商品主图")
    private String mainImage;

    @Schema(description = "商品详情图片")
    private String detailImages;

    @Schema(description = "是否推荐")
    private Boolean recommended;

    @Schema(description = "是否热门")
    private Boolean hot;

    @Schema(description = "排序值")
    private Integer sortOrder;
    
    @Schema(description = "幂等键（可选，防止重复更新）")
    private String idempotentKey;

    public GoodsUpdateRequest() {}

    // Getters and Setters
    public Long getGoodsId() { return goodsId; }
    public void setGoodsId(Long goodsId) { this.goodsId = goodsId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public String getMainImage() { return mainImage; }
    public void setMainImage(String mainImage) { this.mainImage = mainImage; }

    public String getDetailImages() { return detailImages; }
    public void setDetailImages(String detailImages) { this.detailImages = detailImages; }

    public Boolean getRecommended() { return recommended; }
    public void setRecommended(Boolean recommended) { this.recommended = recommended; }

    public Boolean getHot() { return hot; }
    public void setHot(Boolean hot) { this.hot = hot; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    
    public String getIdempotentKey() { return idempotentKey; }
    public void setIdempotentKey(String idempotentKey) { this.idempotentKey = idempotentKey; }

    @Override
    public String toString() {
        return "GoodsUpdateRequest{" +
                "goodsId=" + goodsId +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}
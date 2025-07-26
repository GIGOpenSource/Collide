package com.gig.collide.api.goods.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * 商品创建请求
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
@Schema(description = "商品创建请求")
public class GoodsCreateRequest {
    
    @Schema(description = "商品名称", required = true)
    private String name;
    
    @Schema(description = "商品描述")
    private String description;
    
    @Schema(description = "商品类型", required = true, allowableValues = {"COIN", "SUBSCRIPTION"})
    private String type;
    
    @Schema(description = "商品价格（元）", required = true)
    private BigDecimal price;
    
    @Schema(description = "商品图片URL")
    private String imageUrl;
    
    @Schema(description = "库存数量（-1表示无限库存）", defaultValue = "-1")
    private Integer stock;
    
    @Schema(description = "订阅周期天数（订阅类商品使用）")
    private Integer subscriptionDays;
    
    @Schema(description = "金币数量（金币类商品购买后获得的金币数）")
    private Integer coinAmount;
    
    @Schema(description = "是否推荐", defaultValue = "false")
    private Boolean recommended;
    
    @Schema(description = "是否热门", defaultValue = "false")
    private Boolean hot;
    
    @Schema(description = "创建者ID", required = true)
    private Long creatorId;
    
    @Schema(description = "幂等键（可选，防止重复创建）")
    private String idempotentKey;
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    
    public Integer getSubscriptionDays() { return subscriptionDays; }
    public void setSubscriptionDays(Integer subscriptionDays) { this.subscriptionDays = subscriptionDays; }
    
    public Integer getCoinAmount() { return coinAmount; }
    public void setCoinAmount(Integer coinAmount) { this.coinAmount = coinAmount; }
    
    public Boolean getRecommended() { return recommended; }
    public void setRecommended(Boolean recommended) { this.recommended = recommended; }
    
    public Boolean getHot() { return hot; }
    public void setHot(Boolean hot) { this.hot = hot; }
    
    public Long getCreatorId() { return creatorId; }
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }
    
    public String getIdempotentKey() { return idempotentKey; }
    public void setIdempotentKey(String idempotentKey) { this.idempotentKey = idempotentKey; }
}
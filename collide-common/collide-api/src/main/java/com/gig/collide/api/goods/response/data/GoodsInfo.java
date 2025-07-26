package com.gig.collide.api.goods.response.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品信息DTO
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
@Schema(description = "商品信息")
public class GoodsInfo {
    
    @Schema(description = "商品ID")
    private Long goodsId;
    
    @Schema(description = "商品名称")
    private String name;
    
    @Schema(description = "商品描述")
    private String description;
    
    @Schema(description = "商品类型", allowableValues = {"COIN", "SUBSCRIPTION"})
    private String type;
    
    @Schema(description = "商品状态", allowableValues = {"DRAFT", "ON_SALE", "OFF_SALE", "SOLD_OUT", "DISABLED"})
    private String status;
    
    @Schema(description = "商品价格（元）")
    private BigDecimal price;
    
    @Schema(description = "商品图片URL")
    private String imageUrl;
    
    @Schema(description = "库存数量（-1表示无限库存）")
    private Integer stock;
    
    @Schema(description = "已售数量")
    private Integer soldCount;
    
    @Schema(description = "订阅周期天数（订阅类商品使用）")
    private Integer subscriptionDays;
    
    @Schema(description = "金币数量（金币类商品购买后获得的金币数）")
    private Integer coinAmount;
    
    @Schema(description = "是否推荐")
    private Boolean recommended;
    
    @Schema(description = "是否热门")
    private Boolean hot;
    
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    
    @Schema(description = "创建者ID")
    private Long creatorId;
    
    // Getters and Setters
    public Long getGoodsId() { return goodsId; }
    public void setGoodsId(Long goodsId) { this.goodsId = goodsId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    
    public Integer getSoldCount() { return soldCount; }
    public void setSoldCount(Integer soldCount) { this.soldCount = soldCount; }
    
    public Integer getSubscriptionDays() { return subscriptionDays; }
    public void setSubscriptionDays(Integer subscriptionDays) { this.subscriptionDays = subscriptionDays; }
    
    public Integer getCoinAmount() { return coinAmount; }
    public void setCoinAmount(Integer coinAmount) { this.coinAmount = coinAmount; }
    
    public Boolean getRecommended() { return recommended; }
    public void setRecommended(Boolean recommended) { this.recommended = recommended; }
    
    public Boolean getHot() { return hot; }
    public void setHot(Boolean hot) { this.hot = hot; }
    
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    
    public Long getCreatorId() { return creatorId; }
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }
} 
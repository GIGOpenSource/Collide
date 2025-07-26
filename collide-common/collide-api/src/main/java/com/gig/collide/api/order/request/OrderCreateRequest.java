package com.gig.collide.api.order.request;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 订单创建请求
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
@Schema(description = "订单创建请求")
public class OrderCreateRequest {
    
    @Schema(description = "用户ID", required = true)
    private Long userId;
    
    @Schema(description = "商品ID", required = true)
    private Long goodsId;
    
    @Schema(description = "购买数量", required = true, defaultValue = "1")
    private Integer quantity;
    
    @Schema(description = "备注")
    private String remark;
    
    public OrderCreateRequest() {}
    
    public OrderCreateRequest(Long userId, Long goodsId, Integer quantity) {
        this.userId = userId;
        this.goodsId = goodsId;
        this.quantity = quantity;
    }
    
    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Long getGoodsId() { return goodsId; }
    public void setGoodsId(Long goodsId) { this.goodsId = goodsId; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
} 
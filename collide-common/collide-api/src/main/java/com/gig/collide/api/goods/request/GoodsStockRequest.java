package com.gig.collide.api.goods.request;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 商品库存更新请求
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
@Schema(description = "商品库存更新请求")
public class GoodsStockRequest {

    @Schema(description = "商品ID", required = true)
    private Long goodsId;

    @Schema(description = "库存数量", required = true)
    private Integer stock;

    @Schema(description = "操作类型", allowableValues = {"SET", "INCREASE", "DECREASE"})
    private String operationType;

    @Schema(description = "操作原因")
    private String reason;
    
    @Schema(description = "幂等键（可选，防止重复操作）")
    private String idempotentKey;

    public GoodsStockRequest() {}

    // Getters and Setters
    public Long getGoodsId() { return goodsId; }
    public void setGoodsId(Long goodsId) { this.goodsId = goodsId; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public String getOperationType() { return operationType; }
    public void setOperationType(String operationType) { this.operationType = operationType; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    public String getIdempotentKey() { return idempotentKey; }
    public void setIdempotentKey(String idempotentKey) { this.idempotentKey = idempotentKey; }

    @Override
    public String toString() {
        return "GoodsStockRequest{" +
                "goodsId=" + goodsId +
                ", stock=" + stock +
                ", operationType='" + operationType + '\'' +
                '}';
    }
}
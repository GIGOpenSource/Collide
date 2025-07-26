package com.gig.collide.api.goods.response;

import com.gig.collide.base.response.BaseResponse;

/**
 * 商品操作响应
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
public class GoodsResponse extends BaseResponse {
    
    /**
     * 商品ID
     */
    private Long goodsId;
    
    /**
     * 操作消息
     */
    private String message;
    
    public GoodsResponse() {
        super();
    }
    
    public static GoodsResponse success(Long goodsId) {
        GoodsResponse response = new GoodsResponse();
        response.setSuccess(true);
        response.setGoodsId(goodsId);
        return response;
    }
    
    public static GoodsResponse success(Long goodsId, String message) {
        GoodsResponse response = success(goodsId);
        response.setMessage(message);
        return response;
    }
    
    public static GoodsResponse error(String code, String message) {
        GoodsResponse response = new GoodsResponse();
        response.setSuccess(false);
        response.setResponseCode(code);
        response.setResponseMessage(message);
        return response;
    }
    
    public Long getGoodsId() {
        return goodsId;
    }
    
    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
} 
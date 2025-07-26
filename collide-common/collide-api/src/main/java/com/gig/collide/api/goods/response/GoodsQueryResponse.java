package com.gig.collide.api.goods.response;

import com.gig.collide.base.response.BaseResponse;

/**
 * 商品查询响应
 *
 * @author Collide Team
 * @since 2024-01-01
 */
public class GoodsQueryResponse<T> extends BaseResponse {
    
    /**
     * 响应数据
     */
    private T data;
    
    public GoodsQueryResponse() {
        super();
    }
    
    public static <T> GoodsQueryResponse<T> success(T data) {
        GoodsQueryResponse<T> response = new GoodsQueryResponse<>();
        response.setSuccess(true);
        response.setData(data);
        return response;
    }
    
    public static <T> GoodsQueryResponse<T> error(String code, String message) {
        GoodsQueryResponse<T> response = new GoodsQueryResponse<>();
        response.setSuccess(false);
        response.setResponseCode(code);
        response.setResponseMessage(message);
        return response;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
} 
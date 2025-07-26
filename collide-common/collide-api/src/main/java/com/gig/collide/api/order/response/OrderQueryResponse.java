package com.gig.collide.api.order.response;

import com.gig.collide.base.response.BaseResponse;

/**
 * 订单查询响应
 *
 * @author Collide Team
 * @since 2024-01-01
 */
public class OrderQueryResponse<T> extends BaseResponse {
    
    /**
     * 响应数据
     */
    private T data;
    
    public OrderQueryResponse() {
        super();
    }
    
    public static <T> OrderQueryResponse<T> success(T data) {
        OrderQueryResponse<T> response = new OrderQueryResponse<>();
        response.setSuccess(true);
        response.setData(data);
        return response;
    }
    
    public static <T> OrderQueryResponse<T> error(String code, String message) {
        OrderQueryResponse<T> response = new OrderQueryResponse<>();
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
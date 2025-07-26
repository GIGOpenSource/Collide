package com.gig.collide.api.order.response;

import com.gig.collide.base.response.BaseResponse;

/**
 * 订单操作响应
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
public class OrderResponse extends BaseResponse {
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 操作消息
     */
    private String message;
    
    /**
     * 支付链接（支付时返回）
     */
    private String paymentUrl;
    
    public OrderResponse() {
        super();
    }
    
    public static OrderResponse success(String orderNo) {
        OrderResponse response = new OrderResponse();
        response.setSuccess(true);
        response.setOrderNo(orderNo);
        return response;
    }
    
    public static OrderResponse success(String orderNo, String message) {
        OrderResponse response = success(orderNo);
        response.setMessage(message);
        return response;
    }
    
    public static OrderResponse successWithPayment(String orderNo, String paymentUrl) {
        OrderResponse response = success(orderNo);
        response.setPaymentUrl(paymentUrl);
        return response;
    }
    
    public static OrderResponse error(String code, String message) {
        OrderResponse response = new OrderResponse();
        response.setSuccess(false);
        response.setResponseCode(code);
        response.setResponseMessage(message);
        return response;
    }
    
    // Getters and Setters
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getPaymentUrl() { return paymentUrl; }
    public void setPaymentUrl(String paymentUrl) { this.paymentUrl = paymentUrl; }
} 
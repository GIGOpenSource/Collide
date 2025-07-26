package com.gig.collide.api.payment.response;

import com.gig.collide.base.response.BaseResponse;

/**
 * 支付操作响应
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
public class PaymentResponse extends BaseResponse {
    
    /**
     * 支付ID
     */
    private String paymentId;
    
    /**
     * 支付链接（用于跳转支付页面）
     */
    private String paymentUrl;
    
    /**
     * 二维码链接（扫码支付）
     */
    private String qrCodeUrl;
    
    /**
     * 操作消息
     */
    private String message;
    
    /**
     * 支付状态
     */
    private String status;
    
    public PaymentResponse() {
        super();
    }
    
    public static PaymentResponse success(String paymentId) {
        PaymentResponse response = new PaymentResponse();
        response.setSuccess(true);
        response.setPaymentId(paymentId);
        return response;
    }
    
    public static PaymentResponse successWithUrl(String paymentId, String paymentUrl) {
        PaymentResponse response = success(paymentId);
        response.setPaymentUrl(paymentUrl);
        return response;
    }
    
    public static PaymentResponse success(String paymentId, String message) {
        PaymentResponse response = success(paymentId);
        response.setMessage(message);
        return response;
    }
    
    public static PaymentResponse error(String code, String message) {
        PaymentResponse response = new PaymentResponse();
        response.setSuccess(false);
        response.setResponseCode(code);
        response.setResponseMessage(message);
        return response;
    }
    
    // Getters and Setters
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    
    public String getPaymentUrl() { return paymentUrl; }
    public void setPaymentUrl(String paymentUrl) { this.paymentUrl = paymentUrl; }
    
    public String getQrCodeUrl() { return qrCodeUrl; }
    public void setQrCodeUrl(String qrCodeUrl) { this.qrCodeUrl = qrCodeUrl; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
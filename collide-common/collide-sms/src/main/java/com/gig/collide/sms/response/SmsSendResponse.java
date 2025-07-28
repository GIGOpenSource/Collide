package com.gig.collide.sms.response;

import lombok.Data;

/**
 * 短信发送响应
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
public class SmsSendResponse {

    /**
     * 是否发送成功
     */
    private Boolean success;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 错误代码
     */
    private String errorCode;

    /**
     * 短信服务商的消息ID
     */
    private String messageId;

    /**
     * 手机号码
     */
    private String phoneNumber;

    /**
     * 创建成功响应
     */
    public static SmsSendResponse success(String phoneNumber, String messageId) {
        SmsSendResponse response = new SmsSendResponse();
        response.setSuccess(true);
        response.setPhoneNumber(phoneNumber);
        response.setResponseMessageId(messageId);
        response.setResponseMessage("短信发送成功");
        return response;
    }

    /**
     * 创建失败响应
     */
    public static SmsSendResponse failure(String phoneNumber, String errorCode, String message) {
        SmsSendResponse response = new SmsSendResponse();
        response.setSuccess(false);
        response.setPhoneNumber(phoneNumber);
        response.setErrorCode(errorCode);
        response.setResponseMessage(message);
        return response;
    }
} 
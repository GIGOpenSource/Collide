package com.gig.collide.api.pay.constant;

public enum PayChannel {

    /**
     * 支付宝
     */
    ALIPAY("支付宝"),
    /**
     * 微信
     */
    WECHAT("微信"),
    /**
     * Custom
     */
    CUSTOM("Custom"),
    /**
     * MOCK
     */
    MOCK("MOCK");

    private String value;
    PayChannel(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}

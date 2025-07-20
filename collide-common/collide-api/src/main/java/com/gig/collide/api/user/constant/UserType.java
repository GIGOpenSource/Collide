package com.gig.collide.api.user.constant;

public enum UserType {

    /**
     * 普通用户
     */
    CUSTOMER("普通用户"),

    /**
     * 付费用户
     */
    PRO("付费用户"),

    /**
     * 平台
     */
    PLATFORM("平台");

    private String desc;

    UserType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

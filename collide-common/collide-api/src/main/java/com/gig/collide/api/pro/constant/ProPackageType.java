package com.gig.collide.api.pro.constant;

/**
 * 付费用户套餐类型
 * @author GIG
 */
public enum ProPackageType {

    /**
     * 月费套餐
     */
    MONTHLY("月费套餐"),

    /**
     * 季费套餐
     */
    QUARTERLY("季费套餐"),

    /**
     * 年费套餐
     */
    YEARLY("年费套餐"),

    /**
     * 终身套餐
     */
    LIFETIME("终身套餐");

    private final String desc;

    ProPackageType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
} 
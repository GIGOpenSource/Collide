package com.gig.collide.api.artist.constant;

/**
 * 博主分类枚举
 * @author GIG
 */
public enum ArtistCategory {

    /**
     * 科技数码
     */
    TECHNOLOGY("科技数码"),

    /**
     * 生活方式
     */
    LIFESTYLE("生活方式"),

    /**
     * 美食烹饪
     */
    FOOD("美食烹饪"),

    /**
     * 时尚美妆
     */
    FASHION("时尚美妆"),

    /**
     * 旅行摄影
     */
    TRAVEL("旅行摄影"),

    /**
     * 健身运动
     */
    FITNESS("健身运动"),

    /**
     * 教育学习
     */
    EDUCATION("教育学习"),

    /**
     * 娱乐影音
     */
    ENTERTAINMENT("娱乐影音"),

    /**
     * 艺术创作
     */
    ART("艺术创作"),

    /**
     * 商业财经
     */
    BUSINESS("商业财经"),

    /**
     * 医疗健康
     */
    HEALTH("医疗健康"),

    /**
     * 汽车出行
     */
    AUTOMOTIVE("汽车出行"),

    /**
     * 家居装修
     */
    HOME("家居装修"),

    /**
     * 育儿亲子
     */
    PARENTING("育儿亲子"),

    /**
     * 宠物动物
     */
    PETS("宠物动物"),

    /**
     * 游戏电竞
     */
    GAMING("游戏电竞"),

    /**
     * 其他
     */
    OTHER("其他");

    private final String desc;

    ArtistCategory(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 是否为专业类别
     */
    public boolean isProfessional() {
        return this == TECHNOLOGY || this == EDUCATION || this == BUSINESS || 
               this == HEALTH || this == AUTOMOTIVE;
    }

    /**
     * 是否为创意类别
     */
    public boolean isCreative() {
        return this == ART || this == FASHION || this == FOOD || 
               this == TRAVEL || this == ENTERTAINMENT;
    }

    /**
     * 是否为生活类别
     */
    public boolean isLifestyle() {
        return this == LIFESTYLE || this == FITNESS || this == HOME || 
               this == PARENTING || this == PETS;
    }
} 
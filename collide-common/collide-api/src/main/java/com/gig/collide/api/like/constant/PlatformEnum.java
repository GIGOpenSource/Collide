package com.gig.collide.api.like.constant;

/**
 * 平台类型枚举
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public enum PlatformEnum {

    /**
     * Web网页端
     */
    WEB("Web网页端"),
    
    /**
     * App移动端
     */
    APP("App移动端"),
    
    /**
     * H5页面
     */
    H5("H5页面"),
    
    /**
     * 小程序
     */
    MINI_PROGRAM("小程序"),
    
    /**
     * API接口
     */
    API("API接口"),
    
    /**
     * 其他
     */
    OTHER("其他");

    private final String description;

    PlatformEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 是否是移动端平台
     */
    public boolean isMobile() {
        return this == APP || this == H5 || this == MINI_PROGRAM;
    }

    /**
     * 是否是桌面端平台
     */
    public boolean isDesktop() {
        return this == WEB;
    }
} 
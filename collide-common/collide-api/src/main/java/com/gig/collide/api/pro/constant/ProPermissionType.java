package com.gig.collide.api.pro.constant;

/**
 * 付费权限类型
 * @author GIG
 */
public enum ProPermissionType {

    /**
     * 高级搜索权限
     */
    ADVANCED_SEARCH("高级搜索权限"),

    /**
     * 批量操作权限
     */
    BATCH_OPERATION("批量操作权限"),

    /**
     * 数据导出权限
     */
    DATA_EXPORT("数据导出权限"),

    /**
     * 高级分析权限
     */
    ADVANCED_ANALYTICS("高级分析权限"),

    /**
     * 自定义配置权限
     */
    CUSTOM_CONFIG("自定义配置权限"),

    /**
     * 优先级支持权限
     */
    PRIORITY_SUPPORT("优先级支持权限"),

    /**
     * API调用权限
     */
    API_ACCESS("API调用权限"),

    /**
     * 高级关注权限（特别关注等）
     */
    ADVANCED_FOLLOW("高级关注权限"),

    /**
     * 数据备份权限
     */
    DATA_BACKUP("数据备份权限"),

    /**
     * 高级通知权限
     */
    ADVANCED_NOTIFICATION("高级通知权限");

    private final String desc;

    ProPermissionType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
} 
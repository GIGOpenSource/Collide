package com.gig.collide.api.common.enums;

/**
 * 通用状态枚举
 * 定义系统中常用的状态值，供各业务域复用
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public enum BaseStatus {
    /**
     * 启用/激活
     */
    ACTIVE("active", "启用"),
    
    /**
     * 禁用/非激活
     */
    INACTIVE("inactive", "禁用"),
    
    /**
     * 已删除
     */
    DELETED("deleted", "已删除"),
    
    /**
     * 草稿状态
     */
    DRAFT("draft", "草稿"),
    
    /**
     * 审核中
     */
    PENDING("pending", "审核中"),
    
    /**
     * 审核通过
     */
    APPROVED("approved", "审核通过"),
    
    /**
     * 审核拒绝
     */
    REJECTED("rejected", "审核拒绝"),
    
    /**
     * 已发布
     */
    PUBLISHED("published", "已发布");

    private final String code;
    private final String description;

    BaseStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据code获取枚举值
     */
    public static BaseStatus fromCode(String code) {
        for (BaseStatus status : BaseStatus.values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown BaseStatus code: " + code);
    }
} 
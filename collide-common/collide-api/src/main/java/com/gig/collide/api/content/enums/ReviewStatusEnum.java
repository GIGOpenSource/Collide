package com.gig.collide.api.content.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 审核状态枚举
 * 定义内容的审核状态
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum ReviewStatusEnum {

    /**
     * 待审核
     */
    PENDING("PENDING", "待审核", "内容等待审核员审核"),

    /**
     * 审核通过
     */
    APPROVED("APPROVED", "审核通过", "内容已通过审核"),

    /**
     * 审核拒绝
     */
    REJECTED("REJECTED", "审核拒绝", "内容审核未通过");

    /**
     * 状态代码
     */
    private final String code;

    /**
     * 状态名称
     */
    private final String name;

    /**
     * 状态描述
     */
    private final String description;

    /**
     * 根据代码获取枚举值
     *
     * @param code 状态代码
     * @return 对应的枚举值，未找到返回null
     */
    public static ReviewStatusEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (ReviewStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断是否已审核
     *
     * @return true如果已审核（通过或拒绝）
     */
    public boolean isReviewed() {
        return this == APPROVED || this == REJECTED;
    }

    /**
     * 判断是否审核通过
     *
     * @return true如果审核通过
     */
    public boolean isApproved() {
        return this == APPROVED;
    }

    /**
     * 判断是否审核拒绝
     *
     * @return true如果审核拒绝
     */
    public boolean isRejected() {
        return this == REJECTED;
    }
} 
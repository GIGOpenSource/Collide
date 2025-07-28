package com.gig.collide.api.comment.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 审核状态枚举
 * 定义评论的审核状态
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum AuditStatusEnum {

    /**
     * 审核通过
     */
    PASS("PASS", "审核通过", "评论审核通过"),

    /**
     * 审核拒绝
     */
    REJECT("REJECT", "审核拒绝", "评论审核被拒绝"),

    /**
     * 待审核
     */
    PENDING("PENDING", "待审核", "评论等待审核");

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
    public static AuditStatusEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (AuditStatusEnum status : values()) {
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
    public boolean isAudited() {
        return this == PASS || this == REJECT;
    }

    /**
     * 判断是否审核通过
     *
     * @return true如果审核通过
     */
    public boolean isPass() {
        return this == PASS;
    }

    /**
     * 判断是否审核拒绝
     *
     * @return true如果审核拒绝
     */
    public boolean isReject() {
        return this == REJECT;
    }

    /**
     * 判断是否待审核
     *
     * @return true如果待审核
     */
    public boolean isPending() {
        return this == PENDING;
    }
} 
package com.gig.collide.api.comment.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 举报类型枚举
 * 定义评论的举报类型
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum ReportTypeEnum {

    /**
     * 垃圾信息
     */
    SPAM("SPAM", "垃圾信息", "发布垃圾信息、广告等"),

    /**
     * 辱骂攻击
     */
    ABUSE("ABUSE", "辱骂攻击", "包含辱骂、人身攻击等内容"),

    /**
     * 色情内容
     */
    PORN("PORN", "色情内容", "包含色情、低俗等不当内容"),

    /**
     * 暴力内容
     */
    VIOLENCE("VIOLENCE", "暴力内容", "包含暴力、血腥等内容"),

    /**
     * 其他违规
     */
    OTHER("OTHER", "其他违规", "其他违反社区规则的行为");

    /**
     * 类型代码
     */
    private final String code;

    /**
     * 类型名称
     */
    private final String name;

    /**
     * 类型描述
     */
    private final String description;

    /**
     * 根据代码获取枚举值
     *
     * @param code 类型代码
     * @return 对应的枚举值，未找到返回null
     */
    public static ReportTypeEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (ReportTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 判断是否为严重举报类型
     *
     * @return true如果是严重举报类型
     */
    public boolean isSeriousType() {
        return this == PORN || this == VIOLENCE;
    }

    /**
     * 判断是否为内容违规举报
     *
     * @return true如果是内容违规举报
     */
    public boolean isContentViolation() {
        return this == SPAM || this == PORN || this == VIOLENCE;
    }

    /**
     * 判断是否为行为违规举报
     *
     * @return true如果是行为违规举报
     */
    public boolean isBehaviorViolation() {
        return this == ABUSE || this == OTHER;
    }
} 
package com.gig.collide.api.content.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 内容操作类型枚举
 */
@Getter
@AllArgsConstructor
public enum ContentOperateTypeEnum {

    /**
     * 创建内容
     */
    CREATE("create", "创建内容"),

    /**
     * 编辑内容
     */
    UPDATE("update", "编辑内容"),

    /**
     * 删除内容
     */
    DELETE("delete", "删除内容"),

    /**
     * 提交审核
     */
    SUBMIT_REVIEW("submit_review", "提交审核"),

    /**
     * 开始审核
     */
    START_REVIEW("start_review", "开始审核"),

    /**
     * 审核通过
     */
    APPROVE("approve", "审核通过"),

    /**
     * 审核拒绝
     */
    REJECT("reject", "审核拒绝"),

    /**
     * 发布内容
     */
    PUBLISH("publish", "发布内容"),

    /**
     * 下架内容
     */
    UNPUBLISH("unpublish", "下架内容"),

    /**
     * 重新提交审核
     */
    RESUBMIT("resubmit", "重新提交审核"),

    /**
     * 查看内容
     */
    VIEW("view", "查看内容"),

    /**
     * 分享内容
     */
    SHARE("share", "分享内容"),

    /**
     * 点赞内容
     */
    LIKE("like", "点赞内容"),

    /**
     * 取消点赞
     */
    UNLIKE("unlike", "取消点赞"),

    /**
     * 收藏内容
     */
    COLLECT("collect", "收藏内容"),

    /**
     * 取消收藏
     */
    UNCOLLECT("uncollect", "取消收藏");

    /**
     * 操作码
     */
    private final String code;

    /**
     * 描述
     */
    private final String desc;

    /**
     * 根据code获取枚举
     */
    public static ContentOperateTypeEnum getByCode(String code) {
        for (ContentOperateTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
} 
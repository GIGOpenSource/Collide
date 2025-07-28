package com.gig.collide.api.content.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 分享平台枚举
 * 定义支持的分享平台
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum SharePlatformEnum {

    /**
     * 微信分享
     */
    WECHAT("WECHAT", "微信", "微信好友和朋友圈分享"),

    /**
     * 微博分享
     */
    WEIBO("WEIBO", "微博", "新浪微博分享"),

    /**
     * QQ分享
     */
    QQ("QQ", "QQ", "QQ好友和QQ空间分享"),

    /**
     * 链接分享
     */
    LINK("LINK", "链接", "复制链接分享");

    /**
     * 平台代码
     */
    private final String code;

    /**
     * 平台名称
     */
    private final String name;

    /**
     * 平台描述
     */
    private final String description;

    /**
     * 根据代码获取枚举值
     *
     * @param code 平台代码
     * @return 对应的枚举值，未找到返回null
     */
    public static SharePlatformEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (SharePlatformEnum platform : values()) {
            if (platform.getCode().equals(code)) {
                return platform;
            }
        }
        return null;
    }

    /**
     * 判断是否为社交平台
     *
     * @return true如果是社交平台
     */
    public boolean isSocialPlatform() {
        return this == WECHAT || this == WEIBO || this == QQ;
    }
} 
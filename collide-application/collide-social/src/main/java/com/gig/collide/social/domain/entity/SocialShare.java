package com.gig.collide.social.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分享实体
 * 对应表: t_social_share
 * 
 * @author GIG Team
 * @since 2024-01-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_social_share")
public class SocialShare extends SocialInteraction {

    @TableField("share_type")
    private Integer shareType; // 分享类型

    @TableField("share_platform")
    private String sharePlatform; // 分享平台

    @TableField("share_comment")
    private String shareComment; // 分享时的评论

    // 分享类型枚举
    public enum ShareType {
        WECHAT(1, "微信"),
        QQ(2, "QQ"),
        WEIBO(3, "微博"),
        COPY_LINK(4, "复制链接"),
        SYSTEM_INTERNAL(5, "系统内分享");

        private final int code;
        private final String desc;

        ShareType(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() { return code; }
        public String getDesc() { return desc; }
    }
}
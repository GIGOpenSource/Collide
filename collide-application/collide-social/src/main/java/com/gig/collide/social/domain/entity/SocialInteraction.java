package com.gig.collide.social.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 社交互动基础实体
 * 为点赞、收藏、分享等互动提供通用字段
 * 
 * @author GIG Team
 * @since 2024-01-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class SocialInteraction {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("content_id")
    private Long contentId;

    @TableField("content_owner_id")
    private Long contentOwnerId;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // 互动类型枚举
    public enum InteractionType {
        LIKE(1, "点赞"),
        FAVORITE(2, "收藏"),
        SHARE(3, "分享"),
        COMMENT(4, "评论");

        private final int code;
        private final String desc;

        InteractionType(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() { return code; }
        public String getDesc() { return desc; }
    }
}
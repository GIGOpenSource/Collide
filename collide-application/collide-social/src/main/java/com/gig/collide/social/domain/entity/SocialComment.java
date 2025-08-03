package com.gig.collide.social.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 评论实体
 * 对应表: t_social_comment
 * 
 * @author GIG Team
 * @since 2024-01-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_social_comment")
public class SocialComment extends SocialInteraction {

    @TableField("parent_comment_id")
    private Long parentCommentId; // 父评论ID(0为顶级评论)

    @TableField("reply_to_user_id")
    private Long replyToUserId; // 回复的用户ID

    @TableField("comment_text")
    private String commentText;

    @TableField("comment_images")
    private String commentImages; // JSON格式

    @TableField("like_count")
    private Integer likeCount; // 评论点赞数

    @TableField("reply_count")
    private Integer replyCount; // 回复数量

    @TableField("status")
    private Integer status; // 1-正常,0-已删除

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 状态枚举
    public enum Status {
        DELETED(0, "已删除"),
        NORMAL(1, "正常");

        private final int code;
        private final String desc;

        Status(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() { return code; }
        public String getDesc() { return desc; }
    }
}
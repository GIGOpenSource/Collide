package com.gig.collide.social.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 社交内容实体
 * 对应表: t_social_content
 * 
 * @author GIG Team
 * @since 2024-01-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_social_content")
public class SocialContent {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("category_id")
    private Long categoryId;

    @TableField("category_path")
    private String categoryPath;

    @TableField("content_type")
    private Integer contentType; // 1-短视频,2-长视频,3-图片,4-文字

    @TableField("title")
    private String title;

    @TableField("description")
    private String description;

    @TableField("media_urls")
    private String mediaUrls; // JSON格式

    @TableField("cover_url")
    private String coverUrl;

    @TableField("duration")
    private Integer duration; // 视频时长(秒)

    @TableField("media_info")
    private String mediaInfo; // JSON格式

    // 付费相关
    @TableField("is_paid")
    private Integer isPaid; // 0-免费,1-付费

    @TableField("price")
    private Integer price; // 价格(金币)

    @TableField("free_duration")
    private Integer freeDuration; // 免费试看时长(秒)

    @TableField("purchase_count")
    private Integer purchaseCount;

    // 统计字段 - 这是我们重点关注的
    @TableField("like_count")
    private Integer likeCount;

    @TableField("comment_count")
    private Integer commentCount;

    @TableField("share_count")
    private Integer shareCount;

    @TableField("favorite_count")
    private Integer favoriteCount;

    @TableField("view_count")
    private Integer viewCount;

    @TableField("recommend_score")
    private BigDecimal recommendScore;

    @TableField("quality_score")
    private BigDecimal qualityScore;

    @TableField("status")
    private Integer status; // 1-正常,2-审核中,0-已删除

    @TableField("privacy")
    private Integer privacy; // 1-公开,2-仅关注者,3-私密

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 内容类型枚举
    public enum ContentType {
        SHORT_VIDEO(1, "短视频"),
        LONG_VIDEO(2, "长视频"),
        IMAGE(3, "图片"),
        TEXT(4, "文字");

        private final int code;
        private final String desc;

        ContentType(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() { return code; }
        public String getDesc() { return desc; }
    }

    // 状态枚举
    public enum Status {
        DELETED(0, "已删除"),
        NORMAL(1, "正常"),
        AUDITING(2, "审核中");

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
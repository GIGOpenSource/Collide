package com.gig.collide.comment.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.gig.collide.api.comment.constant.CommentStatus;
import com.gig.collide.api.comment.constant.CommentType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 评论实体
 * 对应 t_comment 表
 * 完全去连表化设计，统计字段冗余存储
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@TableName("t_comment")
public class Comment {

    /**
     * 评论ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 评论类型
     */
    @TableField("comment_type")
    private CommentType commentType;

    /**
     * 目标ID（内容ID等）
     */
    @TableField("target_id")
    private Long targetId;

    /**
     * 父评论ID
     */
    @TableField("parent_comment_id")
    private Long parentCommentId;

    /**
     * 根评论ID（用于树形结构）
     */
    @TableField("root_comment_id")
    private Long rootCommentId;

    /**
     * 评论内容
     */
    @TableField("content")
    private String content;

    /**
     * 评论用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 用户昵称（冗余存储，避免连表查询）
     */
    @TableField("user_nickname")
    private String userNickname;

    /**
     * 用户头像（冗余存储，避免连表查询）
     */
    @TableField("user_avatar")
    private String userAvatar;

    /**
     * 用户认证状态（冗余存储）
     */
    @TableField("user_verified")
    private Boolean userVerified;

    /**
     * 回复目标用户ID
     */
    @TableField("reply_to_user_id")
    private Long replyToUserId;

    /**
     * 回复目标用户昵称（冗余存储）
     */
    @TableField("reply_to_user_nickname")
    private String replyToUserNickname;

    /**
     * 评论状态
     */
    @TableField("status")
    private CommentStatus status;

    /**
     * 点赞数（冗余字段，避免连表查询）
     */
    @TableField("like_count")
    private Integer likeCount;

    /**
     * 回复数（冗余字段，避免连表查询）
     */
    @TableField("reply_count")
    private Integer replyCount;

    /**
     * 举报数（冗余字段）
     */
    @TableField("report_count")
    private Integer reportCount;

    /**
     * 是否置顶
     */
    @TableField("is_pinned")
    private Boolean isPinned;

    /**
     * 是否热门评论
     */
    @TableField("is_hot")
    private Boolean isHot;

    /**
     * 是否精华评论
     */
    @TableField("is_essence")
    private Boolean isEssence;

    /**
     * 评论质量分数（0-5.00）
     */
    @TableField("quality_score")
    private BigDecimal qualityScore;

    /**
     * IP地址
     */
    @TableField("ip_address")
    private String ipAddress;

    /**
     * 设备信息
     */
    @TableField("device_info")
    private String deviceInfo;

    /**
     * 地理位置
     */
    @TableField("location")
    private String location;

    /**
     * 提及的用户ID列表
     */
    @TableField(value = "mention_user_ids", typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private List<Long> mentionUserIds;

    /**
     * 评论图片列表
     */
    @TableField(value = "images", typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private List<String> images;

    /**
     * 扩展数据
     */
    @TableField(value = "extra_data", typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private Map<String, Object> extraData;

    /**
     * 审核状态
     */
    @TableField("audit_status")
    private String auditStatus;

    /**
     * 审核原因
     */
    @TableField("audit_reason")
    private String auditReason;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
} 
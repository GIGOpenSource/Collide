package com.gig.collide.search.domain.entity;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 搜索评论索引表实体
 * 
 * @author Collide Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_search_comment_index")
public class SearchCommentIndex implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 索引ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 评论ID
     */
    @TableField("comment_id")
    private Long commentId;

    /**
     * 评论内容
     */
    @TableField("content")
    private String content;

    /**
     * 评论类型：CONTENT-内容评论，DYNAMIC-动态评论
     */
    @TableField("comment_type")
    private String commentType;

    /**
     * 目标对象ID（内容ID、动态ID等）
     */
    @TableField("target_id")
    private Long targetId;

    /**
     * 父评论ID，0表示根评论
     */
    @TableField("parent_comment_id")
    private Long parentCommentId;

    /**
     * 根评论ID，0表示本身就是根评论
     */
    @TableField("root_comment_id")
    private Long rootCommentId;

    /**
     * 评论用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 用户昵称
     */
    @TableField("user_nickname")
    private String userNickname;

    /**
     * 用户头像
     */
    @TableField("user_avatar")
    private String userAvatar;

    /**
     * 用户角色
     */
    @TableField("user_role")
    private String userRole;

    /**
     * 用户认证状态：0-未认证，1-已认证
     */
    @TableField("user_verified")
    private Integer userVerified;

    /**
     * 目标内容标题
     */
    @TableField("target_title")
    private String targetTitle;

    /**
     * 目标内容类型
     */
    @TableField("target_type")
    private String targetType;

    /**
     * 目标内容作者ID
     */
    @TableField("target_author_id")
    private Long targetAuthorId;

    /**
     * 目标内容作者昵称
     */
    @TableField("target_author_nickname")
    private String targetAuthorNickname;

    /**
     * 回复目标用户ID
     */
    @TableField("reply_to_user_id")
    private Long replyToUserId;

    /**
     * 回复目标用户昵称
     */
    @TableField("reply_to_user_nickname")
    private String replyToUserNickname;

    /**
     * 评论状态：NORMAL-正常，HIDDEN-隐藏，DELETED-已删除，PENDING-待审核
     */
    @TableField("status")
    private String status;

    /**
     * 审核状态：PASS-通过，REJECT-拒绝，PENDING-待审核
     */
    @TableField("audit_status")
    private String auditStatus;

    /**
     * 是否置顶：0-否，1-是
     */
    @TableField("is_pinned")
    private Integer isPinned;

    /**
     * 是否热门：0-否，1-是
     */
    @TableField("is_hot")
    private Integer isHot;

    /**
     * 是否精华：0-否，1-是
     */
    @TableField("is_essence")
    private Integer isEssence;

    /**
     * 点赞数
     */
    @TableField("like_count")
    private Integer likeCount;

    /**
     * 回复数
     */
    @TableField("reply_count")
    private Integer replyCount;

    /**
     * 举报数
     */
    @TableField("report_count")
    private Integer reportCount;

    /**
     * 评论质量分数（0-5.00）
     */
    @TableField("quality_score")
    private BigDecimal qualityScore;

    /**
     * 热度评分
     */
    @TableField("popularity_score")
    private BigDecimal popularityScore;

    /**
     * 搜索权重
     */
    @TableField("search_weight")
    private Double searchWeight;

    /**
     * IP地址
     */
    @TableField("ip_address")
    private String ipAddress;

    /**
     * 地理位置
     */
    @TableField("location")
    private String location;

    /**
     * 设备信息
     */
    @TableField("device_info")
    private String deviceInfo;

    /**
     * 提及的用户ID列表
     */
    @TableField("mention_user_ids")
    private String mentionUserIds;

    /**
     * 评论图片列表
     */
    @TableField("images")
    private String images;

    /**
     * 关键词（用于全文搜索）
     */
    @TableField("keywords")
    private String keywords;

    /**
     * 评论时间（冗余字段）
     */
    @TableField("comment_time")
    private LocalDateTime commentTime;

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
     * 逻辑删除：0-未删除，1-已删除
     */
    @TableField("deleted")
    @TableLogic
    private Integer deleted;

    /**
     * 获取提及用户ID数组
     */
    public Long[] getMentionUserIdsArray() {
        if (mentionUserIds == null) {
            return new Long[0];
        }
        return JSON.parseObject(mentionUserIds, Long[].class);
    }

    /**
     * 设置提及用户ID数组
     */
    public void setMentionUserIdsArray(Long[] mentionUserIdsArray) {
        this.mentionUserIds = mentionUserIdsArray == null ? null : JSON.toJSONString(mentionUserIdsArray);
    }

    /**
     * 获取图片URL数组
     */
    public String[] getImagesArray() {
        if (images == null) {
            return new String[0];
        }
        return JSON.parseObject(images, String[].class);
    }

    /**
     * 设置图片URL数组
     */
    public void setImagesArray(String[] imagesArray) {
        this.images = imagesArray == null ? null : JSON.toJSONString(imagesArray);
    }
} 
package com.gig.collide.social.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 社交动态互动记录实体
 * 
 * <p>记录用户对动态的各种互动行为，支持完全去连表化查询</p>
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_social_post_interaction")
public class SocialPostInteraction implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 互动记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 动态ID
     */
    @TableField("post_id")
    private Long postId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 互动类型(LIKE-点赞,FAVORITE-收藏,SHARE-转发,VIEW-浏览)
     */
    @TableField("interaction_type")
    private String interactionType;

    /**
     * 互动状态(0-取消,1-有效)
     */
    @TableField("interaction_status")
    private Integer interactionStatus;

    // === 冗余用户信息 (避免连表查询) ===

    /**
     * 用户昵称(冗余)
     */
    @TableField("user_nickname")
    private String userNickname;

    /**
     * 用户头像(冗余)
     */
    @TableField("user_avatar")
    private String userAvatar;

    // === 冗余动态信息 (避免连表查询) ===

    /**
     * 动态作者ID(冗余)
     */
    @TableField("post_author_id")
    private Long postAuthorId;

    /**
     * 动态类型(冗余)
     */
    @TableField("post_type")
    private String postType;

    /**
     * 动态标题或前50字符(冗余)
     */
    @TableField("post_title")
    private String postTitle;

    // === 扩展信息 ===

    /**
     * 互动内容(如转发评论)
     */
    @TableField("interaction_content")
    private String interactionContent;

    /**
     * 设备信息
     */
    @TableField("device_info")
    private String deviceInfo;

    /**
     * IP地址
     */
    @TableField("ip_address")
    private String ipAddress;

    // === 时间信息 ===

    /**
     * 创建时间
     */
    @TableField("created_time")
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField("updated_time")
    private LocalDateTime updatedTime;

    // === 通用字段 ===

    /**
     * 逻辑删除标志
     */
    @TableField("deleted")
    private Integer deleted;

    // === 互动类型常量 ===
    
    /**
     * 互动类型：点赞
     */
    public static final String INTERACTION_TYPE_LIKE = "LIKE";
    
    /**
     * 互动类型：收藏
     */
    public static final String INTERACTION_TYPE_FAVORITE = "FAVORITE";
    
    /**
     * 互动类型：转发
     */
    public static final String INTERACTION_TYPE_SHARE = "SHARE";
    
    /**
     * 互动类型：浏览
     */
    public static final String INTERACTION_TYPE_VIEW = "VIEW";
    
    // === 互动状态常量 ===
    
    /**
     * 互动状态：取消
     */
    public static final Integer INTERACTION_STATUS_CANCELLED = 0;
    
    /**
     * 互动状态：有效
     */
    public static final Integer INTERACTION_STATUS_ACTIVE = 1;

    // === 便捷方法 ===

    /**
     * 是否为有效互动
     */
    public boolean isActive() {
        return INTERACTION_STATUS_ACTIVE.equals(this.interactionStatus);
    }

    /**
     * 是否为点赞互动
     */
    public boolean isLike() {
        return INTERACTION_TYPE_LIKE.equals(this.interactionType);
    }

    /**
     * 是否为收藏互动
     */
    public boolean isFavorite() {
        return INTERACTION_TYPE_FAVORITE.equals(this.interactionType);
    }

    /**
     * 是否为转发互动
     */
    public boolean isShare() {
        return INTERACTION_TYPE_SHARE.equals(this.interactionType);
    }

    /**
     * 是否为浏览互动
     */
    public boolean isView() {
        return INTERACTION_TYPE_VIEW.equals(this.interactionType);
    }

    /**
     * 设置为有效状态
     */
    public SocialPostInteraction setAsActive() {
        this.interactionStatus = INTERACTION_STATUS_ACTIVE;
        return this;
    }

    /**
     * 设置为取消状态
     */
    public SocialPostInteraction setAsCancelled() {
        this.interactionStatus = INTERACTION_STATUS_CANCELLED;
        return this;
    }

    /**
     * 创建点赞互动记录
     */
    public static SocialPostInteraction createLikeInteraction(Long postId, Long userId, 
                                                             String userNickname, String userAvatar,
                                                             Long postAuthorId, String postType, String postTitle) {
        return new SocialPostInteraction()
                .setPostId(postId)
                .setUserId(userId)
                .setInteractionType(INTERACTION_TYPE_LIKE)
                .setInteractionStatus(INTERACTION_STATUS_ACTIVE)
                .setUserNickname(userNickname)
                .setUserAvatar(userAvatar)
                .setPostAuthorId(postAuthorId)
                .setPostType(postType)
                .setPostTitle(postTitle)
                .setCreatedTime(LocalDateTime.now())
                .setUpdatedTime(LocalDateTime.now())
                .setDeleted(0);
    }

    /**
     * 创建收藏互动记录
     */
    public static SocialPostInteraction createFavoriteInteraction(Long postId, Long userId,
                                                                 String userNickname, String userAvatar,
                                                                 Long postAuthorId, String postType, String postTitle) {
        return new SocialPostInteraction()
                .setPostId(postId)
                .setUserId(userId)
                .setInteractionType(INTERACTION_TYPE_FAVORITE)
                .setInteractionStatus(INTERACTION_STATUS_ACTIVE)
                .setUserNickname(userNickname)
                .setUserAvatar(userAvatar)
                .setPostAuthorId(postAuthorId)
                .setPostType(postType)
                .setPostTitle(postTitle)
                .setCreatedTime(LocalDateTime.now())
                .setUpdatedTime(LocalDateTime.now())
                .setDeleted(0);
    }

    /**
     * 创建转发互动记录
     */
    public static SocialPostInteraction createShareInteraction(Long postId, Long userId,
                                                              String userNickname, String userAvatar,
                                                              Long postAuthorId, String postType, String postTitle,
                                                              String shareContent) {
        return new SocialPostInteraction()
                .setPostId(postId)
                .setUserId(userId)
                .setInteractionType(INTERACTION_TYPE_SHARE)
                .setInteractionStatus(INTERACTION_STATUS_ACTIVE)
                .setUserNickname(userNickname)
                .setUserAvatar(userAvatar)
                .setPostAuthorId(postAuthorId)
                .setPostType(postType)
                .setPostTitle(postTitle)
                .setInteractionContent(shareContent)
                .setCreatedTime(LocalDateTime.now())
                .setUpdatedTime(LocalDateTime.now())
                .setDeleted(0);
    }

    /**
     * 创建浏览互动记录
     */
    public static SocialPostInteraction createViewInteraction(Long postId, Long userId,
                                                             String userNickname, String userAvatar,
                                                             Long postAuthorId, String postType, String postTitle,
                                                             String deviceInfo, String ipAddress) {
        return new SocialPostInteraction()
                .setPostId(postId)
                .setUserId(userId)
                .setInteractionType(INTERACTION_TYPE_VIEW)
                .setInteractionStatus(INTERACTION_STATUS_ACTIVE)
                .setUserNickname(userNickname)
                .setUserAvatar(userAvatar)
                .setPostAuthorId(postAuthorId)
                .setPostType(postType)
                .setPostTitle(postTitle)
                .setDeviceInfo(deviceInfo)
                .setIpAddress(ipAddress)
                .setCreatedTime(LocalDateTime.now())
                .setUpdatedTime(LocalDateTime.now())
                .setDeleted(0);
    }
} 
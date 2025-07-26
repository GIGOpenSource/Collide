package com.gig.collide.like.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 统一点赞实体 - 去连表化设计
 * 处理所有类型的点赞（内容、评论、社交动态等）
 * 包含冗余字段避免连表查询
 * 
 * @author Collide
 * @since 2.0.0
 */
@Data
@TableName("t_like")
public class Like {
    
    /**
     * 点赞ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;
    
    /**
     * 目标对象ID（内容ID、评论ID等）
     */
    @TableField("target_id")
    private Long targetId;
    
    /**
     * 目标类型：CONTENT、COMMENT、USER、GOODS
     */
    @TableField("target_type")
    private String targetType;
    
    /**
     * 操作类型：1-点赞，0-取消，-1-点踩
     */
    @TableField("action_type")
    private Integer actionType;
    
    // ========== 冗余用户信息（避免连表查询） ==========
    
    /**
     * 用户昵称（冗余字段）
     * 避免查询时关联用户表
     */
    @TableField("user_nickname")
    private String userNickname;
    
    /**
     * 用户头像URL（冗余字段）
     * 避免查询时关联用户表
     */
    @TableField("user_avatar")
    private String userAvatar;
    
    // ========== 冗余目标信息（避免连表查询） ==========
    
    /**
     * 目标对象标题（冗余字段）
     * 避免查询时关联目标表
     */
    @TableField("target_title")
    private String targetTitle;
    
    /**
     * 目标对象作者ID（冗余字段）
     * 避免查询时关联目标表
     */
    @TableField("target_author_id")
    private Long targetAuthorId;
    
    // ========== 追踪信息 ==========
    
    /**
     * IP地址
     */
    @TableField("ip_address")
    private String ipAddress;
    
    /**
     * 设备信息JSON
     */
    @TableField("device_info")
    private String deviceInfo;
    
    /**
     * 操作平台：WEB、APP、H5
     */
    @TableField("platform")
    private String platform;
    
    // ========== 时间字段 ==========
    
    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
    
    // ========== 状态字段 ==========
    
    /**
     * 状态：1-正常，0-无效
     */
    @TableField("status")
    private Integer status;
    
    /**
     * 逻辑删除标记：0-正常，1-删除
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
    
    // ========== 业务方法 ==========
    
    /**
     * 是否点赞
     */
    public boolean isLiked() {
        return actionType != null && actionType == 1;
    }
    
    /**
     * 是否点踩
     */
    public boolean isDisliked() {
        return actionType != null && actionType == -1;
    }
    
    /**
     * 是否取消操作
     */
    public boolean isCancelled() {
        return actionType != null && actionType == 0;
    }
    
    /**
     * 获取操作类型描述
     */
    public String getActionTypeDescription() {
        if (actionType == null) {
            return "UNKNOWN";
        }
        return switch (actionType) {
            case 1 -> "LIKED";
            case -1 -> "DISLIKED";
            case 0 -> "CANCELLED";
            default -> "UNKNOWN";
        };
    }
    
    /**
     * 是否有效状态
     */
    public boolean isValid() {
        return status != null && status == 1 && 
               deleted != null && deleted == 0;
    }
    
    /**
     * 创建点赞记录的静态工厂方法
     */
    public static Like createLike(Long userId, Long targetId, String targetType, Integer actionType) {
        Like like = new Like();
        like.setUserId(userId);
        like.setTargetId(targetId);
        like.setTargetType(targetType);
        like.setActionType(actionType);
        like.setStatus(1);
        like.setDeleted(0);
        like.setCreatedTime(LocalDateTime.now());
        like.setUpdatedTime(LocalDateTime.now());
        return like;
    }
    
    /**
     * 创建包含冗余信息的点赞记录
     */
    public static Like createLikeWithRedundantInfo(Long userId, String userNickname, String userAvatar,
                                                  Long targetId, String targetType, String targetTitle, Long targetAuthorId,
                                                  Integer actionType, String ipAddress, String platform) {
        Like like = createLike(userId, targetId, targetType, actionType);
        like.setUserNickname(userNickname);
        like.setUserAvatar(userAvatar);
        like.setTargetTitle(targetTitle);
        like.setTargetAuthorId(targetAuthorId);
        like.setIpAddress(ipAddress);
        like.setPlatform(platform);
        return like;
    }
    
    /**
     * 更新冗余用户信息
     */
    public void updateUserInfo(String nickname, String avatar) {
        this.userNickname = nickname;
        this.userAvatar = avatar;
        this.updatedTime = LocalDateTime.now();
    }
    
    /**
     * 更新冗余目标信息
     */
    public void updateTargetInfo(String title, Long authorId) {
        this.targetTitle = title;
        this.targetAuthorId = authorId;
        this.updatedTime = LocalDateTime.now();
    }
    
    /**
     * 转换为字符串（用于日志和调试）
     */
    @Override
    public String toString() {
        return "Like{" +
                "id=" + id +
                ", userId=" + userId +
                ", userNickname='" + userNickname + '\'' +
                ", targetId=" + targetId +
                ", targetType='" + targetType + '\'' +
                ", targetTitle='" + targetTitle + '\'' +
                ", actionType=" + actionType +
                ", platform='" + platform + '\'' +
                ", createdTime=" + createdTime +
                ", status=" + status +
                ", deleted=" + deleted +
                '}';
    }
} 
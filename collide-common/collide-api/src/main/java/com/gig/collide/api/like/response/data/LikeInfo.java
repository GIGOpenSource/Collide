package com.gig.collide.api.like.response.data;

import com.gig.collide.api.like.constant.ActionTypeEnum;
import com.gig.collide.api.like.constant.LikeStatusEnum;
import com.gig.collide.api.like.constant.PlatformEnum;
import com.gig.collide.api.like.constant.TargetTypeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 点赞信息传输对象
 * 对应 t_like 表结构
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class LikeInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 点赞记录ID
     */
    private Long id;

    /**
     * 点赞用户ID
     */
    private Long userId;

    /**
     * 目标对象ID（内容ID/评论ID等）
     */
    private Long targetId;

    /**
     * 目标类型
     */
    private TargetTypeEnum targetType;

    /**
     * 操作类型
     */
    private ActionTypeEnum actionType;

    /**
     * 用户昵称（冗余字段）
     */
    private String userNickname;

    /**
     * 用户头像URL（冗余字段）
     */
    private String userAvatar;

    /**
     * 目标对象标题（冗余字段）
     */
    private String targetTitle;

    /**
     * 目标对象作者ID（冗余字段）
     */
    private Long targetAuthorId;

    /**
     * 操作IP地址
     */
    private String ipAddress;

    /**
     * 设备信息JSON
     */
    private String deviceInfo;

    /**
     * 平台
     */
    private PlatformEnum platform;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

    /**
     * 状态
     */
    private LikeStatusEnum status;

    /**
     * 删除标记
     */
    private Boolean deleted;

    /**
     * 检查是否是点赞操作
     */
    public boolean isLike() {
        return ActionTypeEnum.LIKE.equals(this.actionType);
    }

    /**
     * 检查是否是点踩操作
     */
    public boolean isDislike() {
        return ActionTypeEnum.DISLIKE.equals(this.actionType);
    }

    /**
     * 检查是否是有效记录
     */
    public boolean isValid() {
        return LikeStatusEnum.NORMAL.equals(this.status) && 
               (deleted == null || !deleted);
    }
} 
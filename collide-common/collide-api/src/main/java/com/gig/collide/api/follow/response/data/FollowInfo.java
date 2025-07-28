package com.gig.collide.api.follow.response.data;

import com.gig.collide.api.follow.constant.FollowStatus;
import com.gig.collide.api.follow.constant.FollowType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 关注信息传输对象
 * 完整的关注关系信息，对应 t_follow 表结构
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Schema(description = "关注信息")
public class FollowInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关注ID，主键
     */
    @Schema(description = "关注ID")
    private Long id;

    /**
     * 关注者用户ID
     */
    @Schema(description = "关注者用户ID")
    private Long followerUserId;

    /**
     * 被关注者用户ID
     */
    @Schema(description = "被关注者用户ID")
    private Long followedUserId;

    /**
     * 关注类型
     */
    @Schema(description = "关注类型")
    private FollowType followType;

    /**
     * 状态
     */
    @Schema(description = "关注状态")
    private FollowStatus status;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updatedTime;

    /**
     * 逻辑删除标记
     */
    @Schema(description = "是否已删除")
    private Boolean deleted;

    // ===================== 业务方法 =====================

    /**
     * 是否为特别关注
     *
     * @return true-特别关注，false-普通关注
     */
    public boolean isSpecialFollow() {
        return followType != null && followType.isSpecial();
    }

    /**
     * 是否为正常状态
     *
     * @return true-正常，false-非正常
     */
    public boolean isNormalStatus() {
        return status != null && status.isNormal();
    }

    /**
     * 是否为已屏蔽状态
     *
     * @return true-已屏蔽，false-非已屏蔽
     */
    public boolean isBlockedStatus() {
        return status != null && status.isBlocked();
    }

    /**
     * 是否为有效的关注关系
     *
     * @return true-有效，false-无效
     */
    public boolean isValidFollow() {
        return status != null && status.isActive() && !Boolean.TRUE.equals(deleted);
    }

    /**
     * 获取关注类型编码
     *
     * @return 类型编码
     */
    public Integer getFollowTypeCode() {
        return followType != null ? followType.getCode() : null;
    }

    /**
     * 获取状态编码
     *
     * @return 状态编码
     */
    public Integer getStatusCode() {
        return status != null ? status.getCode() : null;
    }
} 
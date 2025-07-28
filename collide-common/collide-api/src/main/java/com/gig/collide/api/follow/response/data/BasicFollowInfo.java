package com.gig.collide.api.follow.response.data;

import com.gig.collide.api.follow.constant.FollowStatus;
import com.gig.collide.api.follow.constant.FollowType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础关注信息传输对象
 * 用于公开显示的关注信息，不包含敏感字段
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
@Schema(description = "基础关注信息")
public class BasicFollowInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关注ID
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
     * 状态（仅显示正常状态的关注）
     */
    @Schema(description = "关注状态")
    private FollowStatus status;

    /**
     * 创建时间
     */
    @Schema(description = "关注时间")
    private LocalDateTime createdTime;

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
     * 获取关注类型描述
     *
     * @return 类型描述
     */
    public String getFollowTypeDescription() {
        return followType != null ? followType.getDescription() : null;
    }

    // ===================== 静态工厂方法 =====================

    /**
     * 从完整关注信息创建基础关注信息
     *
     * @param followInfo 完整关注信息
     * @return 基础关注信息
     */
    public static BasicFollowInfo fromFollowInfo(FollowInfo followInfo) {
        if (followInfo == null) {
            return null;
        }
        
        BasicFollowInfo basicInfo = new BasicFollowInfo();
        basicInfo.setId(followInfo.getId());
        basicInfo.setFollowerUserId(followInfo.getFollowerUserId());
        basicInfo.setFollowedUserId(followInfo.getFollowedUserId());
        basicInfo.setFollowType(followInfo.getFollowType());
        basicInfo.setStatus(followInfo.getStatus());
        basicInfo.setCreatedTime(followInfo.getCreatedTime());
        
        return basicInfo;
    }
} 
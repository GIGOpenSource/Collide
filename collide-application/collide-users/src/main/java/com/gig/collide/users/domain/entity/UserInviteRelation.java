package com.gig.collide.users.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 用户邀请关系实体类
 * 对应数据库表：t_user_invite_relation
 * 处理邀请层级关系，支持多级邀请分析
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Data
@Accessors(chain = true)
@TableName("t_user_invite_relation")
public class UserInviteRelation {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 被邀请用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 邀请人ID
     */
    @TableField("inviter_id")
    private Long inviterId;

    /**
     * 使用的邀请码
     */
    @TableField("invite_code")
    private String inviteCode;

    /**
     * 邀请层级（1-直接邀请，2-二级邀请，以此类推）
     */
    @TableField("invite_level")
    private Integer inviteLevel;

    /**
     * 邀请时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // ================================ 业务方法 ================================

    /**
     * 创建邀请关系
     *
     * @param userId 被邀请用户ID
     * @param inviterId 邀请人ID
     * @param inviteCode 邀请码
     * @param inviteLevel 邀请层级
     * @return 邀请关系实体
     */
    public static UserInviteRelation create(Long userId, Long inviterId, String inviteCode, Integer inviteLevel) {
        return new UserInviteRelation()
                .setUserId(userId)
                .setInviterId(inviterId)
                .setInviteCode(inviteCode)
                .setInviteLevel(inviteLevel);
    }

    /**
     * 创建直接邀请关系
     */
    public static UserInviteRelation createDirectInvite(Long userId, Long inviterId, String inviteCode) {
        return create(userId, inviterId, inviteCode, 1);
    }

    /**
     * 判断是否为直接邀请
     */
    public boolean isDirectInvite() {
        return inviteLevel != null && inviteLevel == 1;
    }

    /**
     * 获取邀请层级描述
     */
    public String getInviteLevelDesc() {
        if (inviteLevel == null) return "未知";
        return switch (inviteLevel) {
            case 1 -> "直接邀请";
            case 2 -> "二级邀请";
            case 3 -> "三级邀请";
            default -> inviteLevel + "级邀请";
        };
    }
} 
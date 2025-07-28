package com.gig.collide.users.domain.entity;

import com.gig.collide.datasource.domain.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户邀请关系实体
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@TableName("t_user_invite_relation")
public class UserInviteRelation extends BaseEntity {

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
     * 邀请层级
     */
    @TableField("invite_level")
    private Integer inviteLevel;
} 
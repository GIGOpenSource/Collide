package com.gig.collide.social.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.gig.collide.datasource.domain.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 社交互动记录实体类
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("t_social_post_interaction")
public class SocialPostInteraction extends BaseEntity {

    private static final long serialVersionUID = 1L;

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
     * 互动类型
     */
    @TableField("interaction_type")
    private String interactionType;

    /**
     * 互动状态
     */
    @TableField("interaction_status")
    private Integer interactionStatus;

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

    /**
     * 互动数据(如转发评论内容)
     */
    @TableField("interaction_data")
    private String interactionData;
} 
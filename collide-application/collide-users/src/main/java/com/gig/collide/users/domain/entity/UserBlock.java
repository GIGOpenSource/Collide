package com.gig.collide.users.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户拉黑实体 - 简洁版
 * 基于简洁版t_user_block表设计，无连表设计
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
@TableName("t_user_block")
public class UserBlock {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 拉黑者用户ID
     */
    private Long userId;

    /**
     * 被拉黑用户ID
     */
    private Long blockedUserId;

    /**
     * 拉黑者用户名（冗余存储，避免连表）
     */
    private String userUsername;

    /**
     * 被拉黑用户名（冗余存储，避免连表）
     */
    private String blockedUsername;

    /**
     * 拉黑状态：active、cancelled
     */
    private String status;

    /**
     * 拉黑原因
     */
    private String reason;

    /**
     * 拉黑时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
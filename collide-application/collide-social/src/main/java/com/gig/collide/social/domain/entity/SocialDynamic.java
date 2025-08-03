package com.gig.collide.social.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 社交动态实体 - 简洁版
 * 对应t_social_dynamic表
 *
 * @author GIG Team
 * @version 2.0.0
 */
@Data
@TableName("t_social_dynamic")
public class SocialDynamic {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 动态内容
     */
    private String content;

    /**
     * 动态类型：text、image、video、share
     */
    private String dynamicType;

    /**
     * 图片列表，JSON格式
     */
    private String images;

    /**
     * 视频URL
     */
    private String videoUrl;

    /**
     * 用户信息（冗余字段，避免连表）
     */
    private Long userId;
    private String userNickname;
    private String userAvatar;

    /**
     * 分享相关（如果是分享动态）
     */
    private String shareTargetType;
    private Long shareTargetId;
    private String shareTargetTitle;

    /**
     * 统计字段（冗余存储，避免聚合查询）
     */
    private Long likeCount;
    private Long commentCount;
    private Long shareCount;

    /**
     * 状态：normal、hidden、deleted
     */
    private String status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
} 
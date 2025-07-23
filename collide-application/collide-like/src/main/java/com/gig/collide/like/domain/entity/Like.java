package com.gig.collide.like.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 统一点赞实体
 * 处理所有类型的点赞（内容、评论、社交动态等）
 * 
 * @author Collide
 * @since 1.0.0
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
     * 目标类型：CONTENT、COMMENT、SOCIAL_POST、USER
     */
    @TableField("target_type")
    private String targetType;
    
    /**
     * 操作类型：1-点赞，0-取消，-1-点踩
     */
    @TableField("action_type")
    private Integer actionType;
    
    /**
     * IP地址
     */
    @TableField("ip_address")
    private String ipAddress;
    
    /**
     * 设备信息
     */
    @TableField("device_info")
    private String deviceInfo;
    
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
    
    /**
     * 逻辑删除标记
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
} 
package com.gig.collide.tag.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户兴趣标签关联表实体
 * 
 * @author GIG Team
 * @version 1.0.0
 * @since 2024-12-19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_user_interest_tag")
public class UserInterestTag implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID，主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID（仅存储ID，不做外键关联）
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 标签ID（仅存储ID，不做外键关联）
     */
    @TableField("tag_id")
    private Long tagId;

    /**
     * 兴趣分数（0-100）
     */
    @TableField("interest_score")
    private BigDecimal interestScore;

    /**
     * 兴趣来源：manual、behavior、system
     */
    @TableField("source")
    private String source;

    /**
     * 状态：active、inactive
     */
    @TableField("status")
    private String status;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 版本号，用于乐观锁
     */
    @Version
    @TableField("version")
    private Integer version;
} 
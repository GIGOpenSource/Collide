package com.gig.collide.tag.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户兴趣标签实体类
 * 对应数据库表：t_user_interest_tag
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-12-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_user_interest_tag")
public class UserInterestTagEntity {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 标签ID
     */
    @TableField("tag_id")
    private Long tagId;

    /**
     * 兴趣分数（0-5分）
     */
    @TableField("interest_score")
    private BigDecimal interestScore;

    /**
     * 来源：manual-手动添加、behavior-行为分析、recommendation-推荐算法
     */
    @TableField("source")
    private String source;

    /**
     * 状态：active-活跃、inactive-非活跃
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
     * 版本号（乐观锁）
     */
    @Version
    @TableField("version")
    private Integer version;
} 
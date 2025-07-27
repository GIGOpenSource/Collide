package com.gig.collide.business.domain.tag.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户兴趣标签实体
 *
 * @author GIG Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_user_interest_tag")
public class UserInterestTag implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 兴趣分数
     */
    @TableField("interest_score")
    private BigDecimal interestScore;

    /**
     * 来源
     */
    @TableField("source")
    private String source;

    /**
     * 状态
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
} 
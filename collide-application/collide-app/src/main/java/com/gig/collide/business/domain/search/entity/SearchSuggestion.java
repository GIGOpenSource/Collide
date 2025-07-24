package com.gig.collide.business.domain.search.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 搜索建议实体
 * 对应 t_search_suggestion 表
 *
 * @author GIG Team
 */
@Data
@TableName("t_search_suggestion")
public class SearchSuggestion {

    /**
     * 建议ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 建议关键词
     */
    @TableField("keyword")
    private String keyword;

    /**
     * 建议类型
     */
    @TableField("suggestion_type")
    private String suggestionType;

    /**
     * 搜索次数
     */
    @TableField("search_count")
    private Long searchCount;

    /**
     * 权重（用于排序）
     */
    @TableField("weight")
    private Double weight;

    /**
     * 状态
     */
    @TableField("status")
    private Integer status;

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
package com.gig.collide.business.domain.search.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 搜索统计实体
 * 对应 t_search_statistics 表
 *
 * @author GIG Team
 */
@Data
@TableName("t_search_statistics")
public class SearchStatistics {

    /**
     * 统计ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 搜索关键词
     */
    @TableField("keyword")
    private String keyword;

    /**
     * 搜索次数
     */
    @TableField("search_count")
    private Long searchCount;

    /**
     * 搜索用户数
     */
    @TableField("user_count")
    private Long userCount;

    /**
     * 最后搜索时间
     */
    @TableField("last_search_time")
    private LocalDateTime lastSearchTime;

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
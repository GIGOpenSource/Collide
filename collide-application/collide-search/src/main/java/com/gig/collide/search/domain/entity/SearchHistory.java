package com.gig.collide.search.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 搜索历史实体 - 简洁版
 * 对应t_search_history表
 *
 * @author GIG Team
 * @version 2.0.0
 */
@Data
@TableName("t_search_history")
public class SearchHistory {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 搜索类型：content、goods、user
     */
    private String searchType;

    /**
     * 搜索结果数量
     */
    private Integer resultCount;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
} 
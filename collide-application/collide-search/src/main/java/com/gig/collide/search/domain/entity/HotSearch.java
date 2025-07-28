package com.gig.collide.search.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 热门搜索实体 - 简洁版
 * 对应t_hot_search表
 *
 * @author GIG Team
 * @version 2.0.0
 */
@Data
@TableName("t_hot_search")
public class HotSearch {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 搜索次数
     */
    private Long searchCount;

    /**
     * 趋势分数
     */
    private BigDecimal trendScore;

    /**
     * 状态：active、inactive
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
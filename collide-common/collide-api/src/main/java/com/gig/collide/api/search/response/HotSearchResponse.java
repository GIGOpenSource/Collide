package com.gig.collide.api.search.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 热门搜索响应 - 简洁版
 * 基于t_hot_search表结构
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
public class HotSearchResponse {

    private Long id;

    private String keyword;

    private Long searchCount;

    private BigDecimal trendScore;

    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 排名位置
     */
    private Integer rank;
} 
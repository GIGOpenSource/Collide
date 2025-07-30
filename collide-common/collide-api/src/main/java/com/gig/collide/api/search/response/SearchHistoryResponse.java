package com.gig.collide.api.search.response;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 搜索历史响应 - 简洁版
 * 基于t_search_history表结构
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
public class SearchHistoryResponse implements Serializable {

    private Long id;

    private Long userId;

    private String keyword;

    private String searchType;

    private Integer resultCount;

    private LocalDateTime createTime;
} 
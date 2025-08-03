package com.gig.collide.search.infrastructure.cache;

import java.util.concurrent.TimeUnit;

/**
 * 搜索模块缓存常量定义 - 缓存增强版
 * 对齐content/social模块设计风格，提供统一的缓存配置
 * 
 * @author GIG Team
 * @version 2.0.0 (缓存增强版)
 * @since 2024-01-16
 */
public class SearchCacheConstant {

    // =================== 搜索结果缓存 ===================
    
    /**
     * 搜索结果缓存名称
     */
    public static final String SEARCH_RESULT_CACHE = "search:result";
    
    /**
     * 搜索结果缓存Key
     */
    public static final String SEARCH_RESULT_KEY = "'search:result:' + #request.keyword + ':' + #request.searchType + ':' + #request.pageNum + ':' + #request.pageSize";
    
    /**
     * 搜索结果缓存过期时间（分钟）
     */
    public static final int SEARCH_RESULT_EXPIRE = 30;

    // =================== 搜索历史缓存 ===================
    
    /**
     * 搜索历史缓存名称
     */
    public static final String SEARCH_HISTORY_CACHE = "search:history";
    
    /**
     * 搜索历史缓存Key
     */
    public static final String SEARCH_HISTORY_KEY = "'search:history:' + #request.userId + ':' + #request.currentPage + ':' + #request.pageSize";
    
    /**
     * 搜索历史缓存过期时间（分钟）
     */
    public static final int SEARCH_HISTORY_EXPIRE = 60;

    // =================== 热门搜索缓存 ===================
    
    /**
     * 热门搜索缓存名称
     */
    public static final String HOT_SEARCH_CACHE = "search:hot";
    
    /**
     * 热门搜索缓存Key
     */
    public static final String HOT_SEARCH_KEY = "'search:hot:' + #limit";
    
    /**
     * 热门搜索缓存过期时间（分钟）
     */
    public static final int HOT_SEARCH_EXPIRE = 15;

    // =================== 热门搜索按类型缓存 ===================
    
    /**
     * 按类型热门搜索缓存名称
     */
    public static final String HOT_SEARCH_TYPE_CACHE = "search:hot:type";
    
    /**
     * 按类型热门搜索缓存Key
     */
    public static final String HOT_SEARCH_TYPE_KEY = "'search:hot:type:' + #searchType + ':' + #limit";
    
    /**
     * 按类型热门搜索缓存过期时间（分钟）
     */
    public static final int HOT_SEARCH_TYPE_EXPIRE = 15;

    // =================== 搜索建议缓存 ===================
    
    /**
     * 搜索建议缓存名称
     */
    public static final String SEARCH_SUGGESTION_CACHE = "search:suggestion";
    
    /**
     * 搜索建议缓存Key
     */
    public static final String SEARCH_SUGGESTION_KEY = "'search:suggestion:' + #keyword + ':' + #limit";
    
    /**
     * 搜索建议缓存过期时间（分钟）
     */
    public static final int SEARCH_SUGGESTION_EXPIRE = 120;

    // =================== 用户搜索偏好缓存 ===================
    
    /**
     * 用户搜索偏好缓存名称
     */
    public static final String USER_PREFERENCE_CACHE = "search:user:preference";
    
    /**
     * 用户搜索偏好缓存Key
     */
    public static final String USER_PREFERENCE_KEY = "'search:user:preference:' + #userId";
    
    /**
     * 用户搜索偏好缓存过期时间（分钟）
     */
    public static final int USER_PREFERENCE_EXPIRE = 60;

    // =================== 混合搜索缓存 ===================
    
    /**
     * Tag混合搜索缓存名称
     */
    public static final String TAG_MIXED_SEARCH_CACHE = "search:tag:mixed";
    
    /**
     * Tag混合搜索缓存Key
     */
    public static final String TAG_MIXED_SEARCH_KEY = "'search:tag:mixed:' + #tag + ':' + #currentPage + ':' + #pageSize";
    
    /**
     * Tag混合搜索缓存过期时间（分钟）
     */
    public static final int TAG_MIXED_SEARCH_EXPIRE = 20;

    // =================== 用户Tag搜索缓存 ===================
    
    /**
     * 用户Tag搜索缓存名称
     */
    public static final String USER_TAG_SEARCH_CACHE = "search:user:tag";
    
    /**
     * 用户Tag搜索缓存Key
     */
    public static final String USER_TAG_SEARCH_KEY = "'search:user:tag:' + #tag + ':' + #currentPage + ':' + #pageSize";
    
    /**
     * 用户Tag搜索缓存过期时间（分钟）
     */
    public static final int USER_TAG_SEARCH_EXPIRE = 20;

    // =================== 内容Tag搜索缓存 ===================
    
    /**
     * 内容Tag搜索缓存名称
     */
    public static final String CONTENT_TAG_SEARCH_CACHE = "search:content:tag";
    
    /**
     * 内容Tag搜索缓存Key
     */
    public static final String CONTENT_TAG_SEARCH_KEY = "'search:content:tag:' + #tag + ':' + #currentPage + ':' + #pageSize";
    
    /**
     * 内容Tag搜索缓存过期时间（分钟）
     */
    public static final int CONTENT_TAG_SEARCH_EXPIRE = 20;

    // =================== 搜索统计缓存 ===================
    
    /**
     * 搜索统计缓存名称
     */
    public static final String SEARCH_STATISTICS_CACHE = "search:statistics";
    
    /**
     * 搜索统计缓存Key
     */
    public static final String SEARCH_STATISTICS_KEY = "'search:statistics:' + #keyword + ':' + #searchType";
    
    /**
     * 搜索统计缓存过期时间（分钟）
     */
    public static final int SEARCH_STATISTICS_EXPIRE = 5;

    // =================== 缓存性能配置 ===================
    
    /**
     * 默认时间单位
     */
    public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MINUTES;
    
    /**
     * 缓存预热间隔（分钟）
     */
    public static final int CACHE_WARMUP_INTERVAL = 30;
    
    /**
     * 搜索缓存容量
     */
    public static final int SEARCH_CACHE_SIZE = 10000;
    
    /**
     * 最大缓存项数量
     */
    public static final int MAX_CACHE_ITEMS = 50000;

    // =================== 业务常量 ===================
    
    /**
     * 默认搜索结果页大小
     */
    public static final int DEFAULT_PAGE_SIZE = 20;
    
    /**
     * 最大搜索结果页大小
     */
    public static final int MAX_PAGE_SIZE = 100;
    
    /**
     * 默认热门搜索返回数量
     */
    public static final int DEFAULT_HOT_SEARCH_LIMIT = 10;
    
    /**
     * 最大热门搜索返回数量
     */
    public static final int MAX_HOT_SEARCH_LIMIT = 50;
    
    /**
     * 默认搜索建议返回数量
     */
    public static final int DEFAULT_SUGGESTION_LIMIT = 10;
    
    /**
     * 最大搜索建议返回数量
     */
    public static final int MAX_SUGGESTION_LIMIT = 20;
}
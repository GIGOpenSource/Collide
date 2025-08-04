package com.gig.collide.favorite.infrastructure.cache;

import java.util.concurrent.TimeUnit;

/**
 * 收藏模块缓存常量 - 缓存增强版
 * 对齐follow模块缓存设计风格，提供统一的缓存配置管理
 * 
 * @author GIG Team
 * @version 2.0.0 (缓存增强版)
 * @since 2024-01-16
 */
public class FavoriteCacheConstant {

    // =================== 收藏状态缓存 ===================
    
    /**
     * 收藏状态缓存名称
     */
    public static final String FAVORITE_STATUS_CACHE = "favorite:status";
    
    /**
     * 用户收藏状态缓存Key
     */
    public static final String USER_FAVORITE_STATUS_KEY = "'favorite:status:' + #userId + ':' + #favoriteType + ':' + #targetId";
    
    /**
     * 收藏关系检查缓存Key
     */
    public static final String FAVORITE_RELATION_KEY = "'favorite:relation:' + #userId + ':' + #favoriteType + ':' + #targetId";
    
    /**
     * 收藏状态缓存过期时间（分钟）
     */
    public static final int FAVORITE_STATUS_EXPIRE = 15;

    // =================== 收藏数量缓存 ===================
    
    /**
     * 收藏数量缓存名称
     */
    public static final String FAVORITE_COUNT_CACHE = "favorite:count";
    
    /**
     * 目标被收藏数量缓存Key
     */
    public static final String TARGET_FAVORITE_COUNT_KEY = "'favorite:count:' + #favoriteType + ':' + #targetId";
    
    /**
     * 用户收藏数量缓存Key
     */
    public static final String USER_FAVORITE_COUNT_KEY = "'favorite:count:user:' + #userId + ':' + #favoriteType";
    
    /**
     * 收藏数量缓存过期时间（分钟）
     */
    public static final int FAVORITE_COUNT_EXPIRE = 30;

    // =================== 收藏记录缓存 ===================
    
    /**
     * 收藏记录缓存名称
     */
    public static final String FAVORITE_RECORDS_CACHE = "favorite:records";
    
    /**
     * 收藏记录列表缓存Key
     */
    public static final String FAVORITE_RECORDS_KEY = "'favorite:records:' + #request.pageNum + ':' + #request.pageSize + ':' + #request.userId + ':' + #request.favoriteType + ':' + #request.targetId";
    
    /**
     * 收藏记录缓存过期时间（分钟）
     */
    public static final int FAVORITE_RECORDS_EXPIRE = 10;

    // =================== 用户收藏列表缓存 ===================
    
    /**
     * 用户收藏列表缓存名称
     */
    public static final String USER_FAVORITES_CACHE = "favorite:user";
    
    /**
     * 用户收藏列表缓存Key
     */
    public static final String USER_FAVORITES_KEY = "'favorite:user:' + #userId + ':' + #favoriteType + ':' + #pageNum + ':' + #pageSize";
    
    /**
     * 用户收藏列表缓存过期时间（分钟）
     */
    public static final int USER_FAVORITES_EXPIRE = 20;

    // =================== 目标收藏用户列表缓存 ===================
    
    /**
     * 目标收藏用户列表缓存名称
     */
    public static final String TARGET_FAVORITES_CACHE = "favorite:target";
    
    /**
     * 目标收藏用户列表缓存Key
     */
    public static final String TARGET_FAVORITES_KEY = "'favorite:target:' + #favoriteType + ':' + #targetId + ':' + #pageNum + ':' + #pageSize";
    
    /**
     * 作者收藏作品缓存Key
     */
    public static final String AUTHOR_FAVORITES_KEY = "'favorite:author:' + #targetAuthorId + ':' + #favoriteType + ':' + #currentPage + ':' + #pageSize";
    
    /**
     * 目标收藏用户列表缓存过期时间（分钟）
     */
    public static final int TARGET_FAVORITES_EXPIRE = 25;

    // =================== 批量收藏状态缓存 ===================
    
    /**
     * 批量收藏状态缓存名称
     */
    public static final String BATCH_FAVORITE_STATUS_CACHE = "favorite:batch:status";
    
    /**
     * 批量收藏状态缓存Key
     */
    public static final String BATCH_FAVORITE_STATUS_KEY = "'favorite:batch:' + #userId + ':' + #favoriteType + ':' + #targetIds.size()";
    
    /**
     * 批量收藏状态缓存过期时间（分钟）
     */
    public static final int BATCH_FAVORITE_STATUS_EXPIRE = 5;

    // =================== 收藏统计缓存 ===================
    
    /**
     * 收藏统计缓存名称
     */
    public static final String FAVORITE_STATISTICS_CACHE = "favorite:statistics";
    
    /**
     * 用户收藏统计缓存Key
     */
    public static final String USER_FAVORITE_STATISTICS_KEY = "'favorite:stats:user:' + #userId";
    
    /**
     * 热门收藏内容缓存Key
     */
    public static final String POPULAR_FAVORITES_KEY = "'favorite:stats:popular:' + #favoriteType + ':' + #pageNum + ':' + #pageSize";
    
    /**
     * 收藏统计缓存过期时间（分钟）
     */
    public static final int FAVORITE_STATISTICS_EXPIRE = 60;

    // =================== 搜索缓存 ===================
    
    /**
     * 收藏搜索缓存名称
     */
    public static final String FAVORITE_SEARCH_CACHE = "favorite:search";
    
    /**
     * 收藏搜索缓存Key
     */
    public static final String FAVORITE_SEARCH_KEY = "'favorite:search:' + #userId + ':' + #titleKeyword + ':' + #favoriteType + ':' + #pageNum + ':' + #pageSize";
    
    /**
     * 收藏搜索缓存过期时间（分钟）
     */
    public static final int FAVORITE_SEARCH_EXPIRE = 8;

    // =================== 收藏详情缓存 ===================
    
    /**
     * 收藏详情缓存名称
     */
    public static final String FAVORITE_DETAIL_CACHE = "favorite:detail";
    
    /**
     * 收藏详情缓存Key
     */
    public static final String FAVORITE_DETAIL_KEY = "'favorite:detail:' + #userId + ':' + #favoriteType + ':' + #targetId";
    
    /**
     * 收藏详情缓存过期时间（分钟）
     */
    public static final int FAVORITE_DETAIL_EXPIRE = 20;

    // =================== 内容收藏检测缓存 🔥 特殊功能 ===================
    
    /**
     * 内容收藏检测缓存名称
     */
    public static final String CONTENT_FAVORITE_DETECTION_CACHE = "favorite:content:detection";
    
    /**
     * 内容是否被收藏检测缓存Key
     */
    public static final String CONTENT_IS_FAVORITED_KEY = "'favorite:content:favorited:' + #contentId + ':' + #checkUserId";
    
    /**
     * 内容收藏者检测缓存Key
     */
    public static final String CONTENT_FAVORITERS_KEY = "'favorite:content:favoriters:' + #contentId + ':' + #pageNum + ':' + #pageSize";
    
    /**
     * 批量内容收藏检测缓存Key
     */
    public static final String BATCH_CONTENT_FAVORITE_KEY = "'favorite:content:batch:' + #userId + ':' + #contentIds.size()";
    
    /**
     * 内容收藏热度检测缓存Key
     */
    public static final String CONTENT_FAVORITE_POPULARITY_KEY = "'favorite:content:popularity:' + #contentId + ':' + #timeRange";
    
    /**
     * 内容收藏检测缓存过期时间（分钟）
     */
    public static final int CONTENT_FAVORITE_DETECTION_EXPIRE = 12;

    // =================== 公共配置 ===================
    
    /**
     * 默认时间单位
     */
    public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MINUTES;
    
    /**
     * 缓存预热间隔（分钟）
     */
    public static final int CACHE_WARMUP_INTERVAL = 15;
    
    /**
     * 收藏缓存大小
     */
    public static final int FAVORITE_CACHE_SIZE = 10000;
    
    /**
     * 最大缓存项数
     */
    public static final int MAX_CACHE_ITEMS = 50000;
    
    /**
     * 默认分页大小
     */
    public static final int DEFAULT_PAGE_SIZE = 20;
    
    /**
     * 最大分页大小
     */
    public static final int MAX_PAGE_SIZE = 100;
    
    /**
     * 最大批量大小
     */
    public static final int MAX_BATCH_SIZE = 100;

    // =================== 业务常量 ===================
    
    /**
     * 收藏类型：内容
     */
    public static final String FAVORITE_TYPE_CONTENT = "CONTENT";
    
    /**
     * 收藏类型：商品
     */
    public static final String FAVORITE_TYPE_GOODS = "GOODS";
    
    /**
     * 收藏类型：用户
     */
    public static final String FAVORITE_TYPE_USER = "USER";
    
    /**
     * 收藏类型：动态
     */
    public static final String FAVORITE_TYPE_DYNAMIC = "DYNAMIC";
    
    /**
     * 收藏类型：评论
     */
    public static final String FAVORITE_TYPE_COMMENT = "COMMENT";
    
    /**
     * 收藏状态：激活
     */
    public static final String FAVORITE_STATUS_ACTIVE = "active";
    
    /**
     * 收藏状态：取消
     */
    public static final String FAVORITE_STATUS_CANCELLED = "cancelled";
    
    /**
     * 默认统计天数
     */
    public static final int DEFAULT_STATS_DAYS = 7;
    
    /**
     * 热门内容阈值
     */
    public static final int POPULAR_CONTENT_THRESHOLD = 50;
}
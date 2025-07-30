package com.gig.collide.like.infrastructure.cache;

import java.util.concurrent.TimeUnit;

/**
 * 点赞模块缓存常量定义 - 缓存增强版
 * 对齐order模块设计风格，提供统一的缓存配置
 * 
 * @author GIG Team
 * @version 2.0.0 (缓存增强版)
 * @since 2024-01-16
 */
public class LikeCacheConstant {

    // =================== 点赞状态缓存 ===================
    
    /**
     * 点赞状态缓存名称
     */
    public static final String LIKE_STATUS_CACHE = "like:status";
    
    /**
     * 用户点赞状态缓存Key
     */
    public static final String USER_LIKE_STATUS_KEY = "'like:status:' + #userId + ':' + #likeType + ':' + #targetId";
    
    /**
     * 点赞状态缓存过期时间（分钟）
     */
    public static final int LIKE_STATUS_EXPIRE = 15;

    // =================== 点赞数量缓存 ===================
    
    /**
     * 点赞数量缓存名称
     */
    public static final String LIKE_COUNT_CACHE = "like:count";
    
    /**
     * 目标对象点赞数量缓存Key
     */
    public static final String TARGET_LIKE_COUNT_KEY = "'like:count:' + #likeType + ':' + #targetId";
    
    /**
     * 用户点赞数量缓存Key
     */
    public static final String USER_LIKE_COUNT_KEY = "'like:count:user:' + #userId + ':' + #likeType";
    
    /**
     * 点赞数量缓存过期时间（分钟）
     */
    public static final int LIKE_COUNT_EXPIRE = 30;

    // =================== 点赞记录缓存 ===================
    
    /**
     * 点赞记录缓存名称
     */
    public static final String LIKE_RECORDS_CACHE = "like:records";
    
    /**
     * 点赞记录分页缓存Key
     */
    public static final String LIKE_RECORDS_KEY = "'like:records:' + #request.pageNum + ':' + #request.pageSize + ':' + #request.userId + ':' + #request.likeType + ':' + #request.targetId";
    
    /**
     * 点赞记录缓存过期时间（分钟）
     */
    public static final int LIKE_RECORDS_EXPIRE = 10;

    // =================== 批量点赞状态缓存 ===================
    
    /**
     * 批量点赞状态缓存名称
     */
    public static final String BATCH_LIKE_STATUS_CACHE = "like:batch:status";
    
    /**
     * 批量点赞状态缓存Key
     */
    public static final String BATCH_LIKE_STATUS_KEY = "'like:batch:' + #userId + ':' + #likeType + ':' + #targetIds.size()";
    
    /**
     * 批量点赞状态缓存过期时间（分钟）
     */
    public static final int BATCH_LIKE_STATUS_EXPIRE = 5;

    // =================== 点赞统计缓存 ===================
    
    /**
     * 点赞统计缓存名称
     */
    public static final String LIKE_STATISTICS_CACHE = "like:statistics";
    
    /**
     * 用户总点赞统计缓存Key
     */
    public static final String USER_TOTAL_LIKES_KEY = "'like:stats:user:' + #userId";
    
    /**
     * 热门点赞内容缓存Key
     */
    public static final String HOT_LIKED_CONTENT_KEY = "'like:stats:hot:' + #likeType + ':' + #timeRange";
    
    /**
     * 点赞统计缓存过期时间（分钟）
     */
    public static final int LIKE_STATISTICS_EXPIRE = 60;

    // =================== 点赞操作缓存 ===================
    
    /**
     * 点赞操作记录缓存名称
     */
    public static final String LIKE_OPERATION_CACHE = "like:operation";
    
    /**
     * 点赞操作记录缓存Key
     */
    public static final String LIKE_OPERATION_KEY = "'like:op:' + #userId + ':' + #likeType + ':' + #targetId";
    
    /**
     * 点赞操作缓存过期时间（分钟）
     */
    public static final int LIKE_OPERATION_EXPIRE = 5;

    // =================== 点赞活动缓存 ===================
    
    /**
     * 点赞活动缓存名称
     */
    public static final String LIKE_ACTIVITY_CACHE = "like:activity";
    
    /**
     * 用户点赞活动缓存Key
     */
    public static final String USER_LIKE_ACTIVITY_KEY = "'like:activity:user:' + #userId + ':' + #timeRange";
    
    /**
     * 目标对象点赞活动缓存Key
     */
    public static final String TARGET_LIKE_ACTIVITY_KEY = "'like:activity:target:' + #targetId + ':' + #likeType";
    
    /**
     * 点赞活动缓存过期时间（分钟）
     */
    public static final int LIKE_ACTIVITY_EXPIRE = 20;

    // =================== 缓存性能配置 ===================
    
    /**
     * 默认时间单位
     */
    public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MINUTES;
    
    /**
     * 缓存预热间隔（分钟）
     */
    public static final int CACHE_WARMUP_INTERVAL = 15;
    
    /**
     * 点赞缓存容量
     */
    public static final int LIKE_CACHE_SIZE = 10000;
    
    /**
     * 最大缓存项数量
     */
    public static final int MAX_CACHE_ITEMS = 50000;

    // =================== 业务常量 ===================
    
    /**
     * 默认分页大小
     */
    public static final int DEFAULT_PAGE_SIZE = 20;
    
    /**
     * 最大分页大小
     */
    public static final int MAX_PAGE_SIZE = 100;
    
    /**
     * 批量操作最大数量
     */
    public static final int MAX_BATCH_SIZE = 100;
    
    /**
     * 点赞类型：内容点赞
     */
    public static final String LIKE_TYPE_CONTENT = "CONTENT";
    
    /**
     * 点赞类型：评论点赞
     */
    public static final String LIKE_TYPE_COMMENT = "COMMENT";
    
    /**
     * 点赞类型：动态点赞
     */
    public static final String LIKE_TYPE_DYNAMIC = "DYNAMIC";
    
    /**
     * 点赞状态：活跃
     */
    public static final String LIKE_STATUS_ACTIVE = "active";
    
    /**
     * 点赞状态：已取消
     */
    public static final String LIKE_STATUS_CANCELLED = "cancelled";
    
    /**
     * 默认统计时间范围（天）
     */
    public static final int DEFAULT_STATS_DAYS = 7;
    
    /**
     * 热门内容阈值
     */
    public static final int HOT_CONTENT_THRESHOLD = 100;
}
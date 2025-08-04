package com.gig.collide.follow.infrastructure.cache;

import java.util.concurrent.TimeUnit;

/**
 * 关注模块缓存常量定义 - 缓存增强版
 * 对齐goods模块设计风格，提供统一的缓存配置
 *
 * @author GIG Team
 * @version 2.0.0 (缓存增强版)
 * @since 2024-01-16
 */
public class FollowCacheConstant {

    // =================== 关注关系缓存 ===================

    /**
     * 关注关系缓存名称
     */
    public static final String FOLLOW_RELATION_CACHE = "follow:relation";

    /**
     * 关注关系检测缓存Key
     */
    public static final String FOLLOW_RELATION_KEY = "'follow:relation:' + #followerId + ':' + #followeeId";

    /**
     * 关注关系缓存过期时间（分钟）
     */
    public static final int FOLLOW_RELATION_EXPIRE = 30;

    // =================== 关注者列表缓存 ===================

    /**
     * 关注者列表缓存名称
     */
    public static final String FOLLOWERS_LIST_CACHE = "follow:followers";

    /**
     * 关注者分页列表缓存Key
     */
    public static final String FOLLOWERS_LIST_KEY = "'follow:followers:' + #followeeId + ':' + #currentPage + ':' + #pageSize";

    /**
     * 关注者列表缓存过期时间（分钟）
     */
    public static final int FOLLOWERS_LIST_EXPIRE = 15;

    // =================== 被关注者列表缓存 ===================

    /**
     * 被关注者列表缓存名称
     */
    public static final String FOLLOWEES_LIST_CACHE = "follow:followees";

    /**
     * 被关注者分页列表缓存Key
     */
    public static final String FOLLOWEES_LIST_KEY = "'follow:followees:' + #followerId + ':' + #currentPage + ':' + #pageSize";

    /**
     * 被关注者列表缓存过期时间（分钟）
     */
    public static final int FOLLOWEES_LIST_EXPIRE = 15;

    // =================== 关注统计缓存 ===================

    /**
     * 关注统计缓存名称
     */
    public static final String FOLLOW_STATISTICS_CACHE = "follow:statistics";

    /**
     * 用户关注数量缓存Key
     */
    public static final String USER_FOLLOWEE_COUNT_KEY = "'follow:stats:followee:' + #userId";

    /**
     * 用户粉丝数量缓存Key
     */
    public static final String USER_FOLLOWER_COUNT_KEY = "'follow:stats:follower:' + #userId";

    /**
     * 用户关注统计缓存Key
     */
    public static final String USER_FOLLOW_STATS_KEY = "'follow:stats:user:' + #userId";

    /**
     * 关注统计缓存过期时间（分钟）
     */
    public static final int FOLLOW_STATISTICS_EXPIRE = 30;

    // =================== 互关检测缓存 ===================

    /**
     * 互关检测缓存名称
     */
    public static final String MUTUAL_FOLLOW_CACHE = "follow:mutual";

    /**
     * 互关关系缓存Key
     */
    public static final String MUTUAL_FOLLOW_KEY = "'follow:mutual:' + #userId + ':' + #currentPage + ':' + #pageSize";

    /**
     * 互关检测缓存过期时间（分钟）
     */
    public static final int MUTUAL_FOLLOW_EXPIRE = 20;

    // =================== 关注状态缓存 ===================

    /**
     * 关注状态缓存名称
     */
    public static final String FOLLOW_STATUS_CACHE = "follow:status";

    /**
     * 用户关注状态缓存Key
     */
    public static final String USER_FOLLOW_STATUS_KEY = "'follow:status:' + #followerId + ':' + #followeeId";

    /**
     * 批量关注状态缓存Key
     */
    public static final String BATCH_FOLLOW_STATUS_KEY = "'follow:status:batch:' + #followerId + ':' + #followeeIds.size()";

    /**
     * 关注状态缓存过期时间（分钟）
     */
    public static final int FOLLOW_STATUS_EXPIRE = 15;

    // =================== 关注活动缓存 ===================

    /**
     * 关注活动缓存名称
     */
    public static final String FOLLOW_ACTIVITY_CACHE = "follow:activity";

    /**
     * 用户关注活动缓存Key
     */
    public static final String USER_FOLLOW_ACTIVITY_KEY = "'follow:activity:user:' + #userId + ':' + #timeRange";

    /**
     * 热门被关注用户缓存Key
     */
    public static final String HOT_FOLLOWEES_KEY = "'follow:activity:hot:' + #timeRange + ':' + #limit";

    /**
     * 关注活动缓存过期时间（分钟）
     */
    public static final int FOLLOW_ACTIVITY_EXPIRE = 60;

    // =================== 关注推荐缓存 ===================

    /**
     * 关注推荐缓存名称
     */
    public static final String FOLLOW_RECOMMEND_CACHE = "follow:recommend";

    /**
     * 用户关注推荐缓存Key
     */
    public static final String USER_FOLLOW_RECOMMEND_KEY = "'follow:recommend:user:' + #userId + ':' + #limit";

    /**
     * 共同关注缓存Key
     */
    public static final String COMMON_FOLLOWS_KEY = "'follow:recommend:common:' + #userId1 + ':' + #userId2";

    /**
     * 关注推荐缓存过期时间（分钟）
     */
    public static final int FOLLOW_RECOMMEND_EXPIRE = 120;

    // =================== 关注操作缓存 ===================

    /**
     * 关注操作记录缓存名称
     */
    public static final String FOLLOW_OPERATION_CACHE = "follow:operation";

    /**
     * 关注操作记录缓存Key
     */
    public static final String FOLLOW_OPERATION_KEY = "'follow:op:' + #followerId + ':' + #followeeId";

    /**
     * 关注操作缓存过期时间（分钟）
     */
    public static final int FOLLOW_OPERATION_EXPIRE = 5;

    // =================== 关注通知缓存 ===================

    /**
     * 关注通知缓存名称
     */
    public static final String FOLLOW_NOTIFICATION_CACHE = "follow:notification";

    /**
     * 用户关注通知缓存Key
     */
    public static final String USER_FOLLOW_NOTIFICATION_KEY = "'follow:notification:user:' + #userId";

    /**
     * 关注通知缓存过期时间（分钟）
     */
    public static final int FOLLOW_NOTIFICATION_EXPIRE = 30;

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
     * 关注缓存容量
     */
    public static final int FOLLOW_CACHE_SIZE = 10000;

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
    public static final int MAX_BATCH_SIZE = 500;

    /**
     * 关注状态：活跃
     */
    public static final String FOLLOW_STATUS_ACTIVE = "active";

    /**
     * 关注状态：已取消
     */
    public static final String FOLLOW_STATUS_CANCELLED = "cancelled";

    /**
     * 关注状态：已屏蔽
     */
    public static final String FOLLOW_STATUS_BLOCKED = "blocked";

    /**
     * 默认统计时间范围（天）
     */
    public static final int DEFAULT_STATS_DAYS = 7;

    /**
     * 热门用户阈值
     */
    public static final int HOT_USER_THRESHOLD = 1000;

    /**
     * 推荐关注数量
     */
    public static final int DEFAULT_RECOMMEND_SIZE = 10;

    /**
     * 最大推荐关注数量
     */
    public static final int MAX_RECOMMEND_SIZE = 50;

    /**
     * 互关检测优先级
     */
    public static final int MUTUAL_FOLLOW_PRIORITY = 1;

    /**
     * 关注活跃度计算周期（小时）
     */
    public static final int ACTIVITY_CALCULATION_HOURS = 24;

    /**
     * 关注通知聚合时间（分钟）
     */
    public static final int NOTIFICATION_AGGREGATE_MINUTES = 30;
}
package com.gig.collide.social.infrastructure.cache;

/**
 * 社交模块缓存常量
 * 
 * @author GIG Team
 * @version 1.0.0
 */
public class SocialCacheConstant {

    // =================== 内容相关缓存 ===================

    /**
     * 内容详情缓存名称
     */
    public static final String CONTENT_DETAIL_CACHE = "social:content:detail";

    /**
     * 内容详情缓存KEY
     */
    public static final String CONTENT_DETAIL_KEY = "#contentId";

    /**
     * 内容详情缓存过期时间（分钟）
     */
    public static final int CONTENT_DETAIL_CACHE_EXPIRE = 30;

    /**
     * 热门内容缓存名称
     */
    public static final String HOT_CONTENT_CACHE = "social:content:hot";

    /**
     * 热门内容缓存KEY
     */
    public static final String HOT_CONTENT_KEY = "#currentPage + '_' + #pageSize";

    /**
     * 热门内容缓存过期时间（分钟）
     */
    public static final int HOT_CONTENT_CACHE_EXPIRE = 10;

    /**
     * 最新内容缓存名称
     */
    public static final String LATEST_CONTENT_CACHE = "social:content:latest";

    /**
     * 最新内容缓存KEY
     */
    public static final String LATEST_CONTENT_KEY = "#currentPage + '_' + #pageSize";

    /**
     * 最新内容缓存过期时间（分钟）
     */
    public static final int LATEST_CONTENT_CACHE_EXPIRE = 5;

    /**
     * 用户内容缓存名称
     */
    public static final String USER_CONTENT_CACHE = "social:user:content";

    /**
     * 用户内容缓存KEY
     */
    public static final String USER_CONTENT_KEY = "#userId + '_' + #currentPage + '_' + #pageSize";

    /**
     * 用户内容缓存过期时间（分钟）
     */
    public static final int USER_CONTENT_CACHE_EXPIRE = 15;

    /**
     * 分类内容缓存名称
     */
    public static final String CATEGORY_CONTENT_CACHE = "social:category:content";

    /**
     * 分类内容缓存KEY
     */
    public static final String CATEGORY_CONTENT_KEY = "#categoryId + '_' + #currentPage + '_' + #pageSize";

    /**
     * 分类内容缓存过期时间（分钟）
     */
    public static final int CATEGORY_CONTENT_CACHE_EXPIRE = 20;

    // =================== 统计相关缓存 ===================

    /**
     * 内容统计缓存名称
     */
    public static final String CONTENT_STATS_CACHE = "social:content:stats";

    /**
     * 内容统计缓存KEY
     */
    public static final String CONTENT_STATS_KEY = "#contentId";

    /**
     * 内容统计缓存过期时间（分钟）
     */
    public static final int CONTENT_STATS_CACHE_EXPIRE = 10;

    /**
     * 用户内容数量缓存名称
     */
    public static final String USER_CONTENT_COUNT_CACHE = "social:user:content:count";

    /**
     * 用户内容数量缓存KEY
     */
    public static final String USER_CONTENT_COUNT_KEY = "#userId";

    /**
     * 用户内容数量缓存过期时间（分钟）
     */
    public static final int USER_CONTENT_COUNT_CACHE_EXPIRE = 30;

    // =================== 互动相关缓存 ===================

    /**
     * 用户互动状态缓存名称
     */
    public static final String USER_INTERACTION_STATUS_CACHE = "social:user:interaction:status";

    /**
     * 用户互动状态缓存KEY
     */
    public static final String USER_INTERACTION_STATUS_KEY = "#userId + '_' + #contentId";

    /**
     * 用户互动状态缓存过期时间（分钟）
     */
    public static final int USER_INTERACTION_STATUS_CACHE_EXPIRE = 15;

    /**
     * 内容点赞列表缓存名称
     */
    public static final String CONTENT_LIKES_CACHE = "social:content:likes";

    /**
     * 内容点赞列表缓存KEY
     */
    public static final String CONTENT_LIKES_KEY = "#contentId + '_' + #currentPage + '_' + #pageSize";

    /**
     * 内容点赞列表缓存过期时间（分钟）
     */
    public static final int CONTENT_LIKES_CACHE_EXPIRE = 10;

    /**
     * 用户收藏列表缓存名称
     */
    public static final String USER_FAVORITES_CACHE = "social:user:favorites";

    /**
     * 用户收藏列表缓存KEY
     */
    public static final String USER_FAVORITES_KEY = "#userId + '_' + #currentPage + '_' + #pageSize";

    /**
     * 用户收藏列表缓存过期时间（分钟）
     */
    public static final int USER_FAVORITES_CACHE_EXPIRE = 20;

    /**
     * 内容评论列表缓存名称
     */
    public static final String CONTENT_COMMENTS_CACHE = "social:content:comments";

    /**
     * 内容评论列表缓存KEY
     */
    public static final String CONTENT_COMMENTS_KEY = "#contentId + '_' + #currentPage + '_' + #pageSize";

    /**
     * 内容评论列表缓存过期时间（分钟）
     */
    public static final int CONTENT_COMMENTS_CACHE_EXPIRE = 5;
}
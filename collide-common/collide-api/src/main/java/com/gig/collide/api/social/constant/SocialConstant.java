package com.gig.collide.api.social.constant;

/**
 * 社交模块常量类
 * 统一管理社交相关的业务常量
 *
 * @author Collide Team
 * @version 1.0.0
 * @since 2024-01-01
 */
public class SocialConstant {

    // ===================== 发布限制常量 =====================

    /**
     * 用户每日最大发布动态数量
     */
    public static final int MAX_DAILY_POSTS = 50;

    /**
     * 动态内容最大长度
     */
    public static final int MAX_POST_CONTENT_LENGTH = 5000;

    /**
     * 动态标题最大长度
     */
    public static final int MAX_POST_TITLE_LENGTH = 200;

    /**
     * 动态最大媒体文件数量
     */
    public static final int MAX_MEDIA_FILES = 9;

    /**
     * 动态最大话题标签数量
     */
    public static final int MAX_TOPIC_TAGS = 10;

    /**
     * 动态最大提及用户数量
     */
    public static final int MAX_MENTIONED_USERS = 20;

    // ===================== 互动限制常量 =====================

    /**
     * 用户每日最大点赞数量
     */
    public static final int MAX_DAILY_LIKES = 1000;

    /**
     * 用户每日最大评论数量
     */
    public static final int MAX_DAILY_COMMENTS = 200;

    /**
     * 用户每日最大转发数量
     */
    public static final int MAX_DAILY_SHARES = 100;

    /**
     * 用户每日最大收藏数量
     */
    public static final int MAX_DAILY_FAVORITES = 500;

    // ===================== 分页常量 =====================

    /**
     * 默认页面大小
     */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /**
     * 最大页面大小
     */
    public static final int MAX_PAGE_SIZE = 100;

    /**
     * 热门动态默认数量
     */
    public static final int DEFAULT_HOT_POSTS_LIMIT = 50;

    /**
     * 推荐动态默认数量
     */
    public static final int DEFAULT_RECOMMEND_POSTS_LIMIT = 20;

    // ===================== 缓存时间常量（分钟） =====================

    /**
     * 动态详情缓存时间
     */
    public static final int POST_DETAIL_CACHE_MINUTES = 30;

    /**
     * 用户动态列表缓存时间
     */
    public static final int USER_POSTS_CACHE_MINUTES = 10;

    /**
     * 热门动态缓存时间
     */
    public static final int HOT_POSTS_CACHE_MINUTES = 15;

    /**
     * 互动状态缓存时间
     */
    public static final int INTERACTION_STATUS_CACHE_MINUTES = 30;

    /**
     * 统计信息缓存时间
     */
    public static final int STATISTICS_CACHE_MINUTES = 60;

    // ===================== 业务规则常量 =====================

    /**
     * 热度分数权重 - 点赞
     */
    public static final double HOT_SCORE_LIKE_WEIGHT = 1.0;

    /**
     * 热度分数权重 - 评论
     */
    public static final double HOT_SCORE_COMMENT_WEIGHT = 2.0;

    /**
     * 热度分数权重 - 转发
     */
    public static final double HOT_SCORE_SHARE_WEIGHT = 3.0;

    /**
     * 热度分数权重 - 收藏
     */
    public static final double HOT_SCORE_FAVORITE_WEIGHT = 1.5;

    /**
     * 热度分数衰减系数（每小时）
     */
    public static final double HOT_SCORE_DECAY_FACTOR = 0.1;

    /**
     * 浏览记录保留天数
     */
    public static final int VIEW_RECORD_RETENTION_DAYS = 30;

    /**
     * 动态最大可见性范围（公里）
     */
    public static final double MAX_LOCATION_RADIUS_KM = 100.0;

    // ===================== 状态常量 =====================

    /**
     * 动态状态 - 草稿
     */
    public static final String POST_STATUS_DRAFT = "draft";

    /**
     * 动态状态 - 已发布
     */
    public static final String POST_STATUS_PUBLISHED = "published";

    /**
     * 动态状态 - 已删除
     */
    public static final String POST_STATUS_DELETED = "deleted";

    /**
     * 可见性 - 公开
     */
    public static final String VISIBILITY_PUBLIC = "public";

    /**
     * 可见性 - 仅关注者
     */
    public static final String VISIBILITY_FOLLOWERS = "followers";

    /**
     * 可见性 - 仅自己
     */
    public static final String VISIBILITY_PRIVATE = "private";

    // ===================== 错误码常量 =====================

    /**
     * 每日发布限制错误码
     */
    public static final String ERROR_DAILY_POSTS_LIMIT = "DAILY_POSTS_LIMIT_EXCEEDED";

    /**
     * 内容长度限制错误码
     */
    public static final String ERROR_CONTENT_TOO_LONG = "CONTENT_TOO_LONG";

    /**
     * 权限不足错误码
     */
    public static final String ERROR_PERMISSION_DENIED = "PERMISSION_DENIED";

    /**
     * 动态不存在错误码
     */
    public static final String ERROR_POST_NOT_FOUND = "POST_NOT_FOUND";

    /**
     * 重复互动错误码
     */
    public static final String ERROR_DUPLICATE_INTERACTION = "DUPLICATE_INTERACTION";
} 
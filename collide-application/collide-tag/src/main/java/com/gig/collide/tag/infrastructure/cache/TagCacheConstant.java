package com.gig.collide.tag.infrastructure.cache;

/**
 * 标签模块缓存常量
 * 
 * @author GIG Team
 * @version 1.0.0
 */
public class TagCacheConstant {

    // =================== 标签相关缓存 ===================

    /**
     * 标签详情缓存名称
     */
    public static final String TAG_DETAIL_CACHE = "tag:detail";

    /**
     * 标签详情缓存KEY
     */
    public static final String TAG_DETAIL_KEY = "#tagId";

    /**
     * 标签详情缓存过期时间（分钟）
     */
    public static final int TAG_DETAIL_CACHE_EXPIRE = 30;

    /**
     * 热门标签缓存名称
     */
    public static final String HOT_TAGS_CACHE = "tag:hot";

    /**
     * 热门标签缓存KEY
     */
    public static final String HOT_TAGS_KEY = "#limit";

    /**
     * 热门标签缓存过期时间（分钟）
     */
    public static final int HOT_TAGS_CACHE_EXPIRE = 10;

    /**
     * 推荐标签缓存名称
     */
    public static final String RECOMMEND_TAGS_CACHE = "tag:recommend";

    /**
     * 推荐标签缓存KEY
     */
    public static final String RECOMMEND_TAGS_KEY = "#limit";

    /**
     * 推荐标签缓存过期时间（分钟）
     */
    public static final int RECOMMEND_TAGS_CACHE_EXPIRE = 15;

    // =================== 用户标签相关缓存 ===================

    /**
     * 用户关注标签缓存名称
     */
    public static final String USER_FOLLOWED_TAGS_CACHE = "user:tag:followed";

    /**
     * 用户关注标签缓存KEY
     */
    public static final String USER_FOLLOWED_TAGS_KEY = "#userId";

    /**
     * 用户关注标签缓存过期时间（分钟）
     */
    public static final int USER_FOLLOWED_TAGS_CACHE_EXPIRE = 20;

    /**
     * 用户关注标签数量缓存名称
     */
    public static final String USER_TAG_COUNT_CACHE = "user:tag:count";

    /**
     * 用户关注标签数量缓存KEY
     */
    public static final String USER_TAG_COUNT_KEY = "#userId";

    /**
     * 用户关注标签数量缓存过期时间（分钟）
     */
    public static final int USER_TAG_COUNT_CACHE_EXPIRE = 30;

    /**
     * 用户标签推荐缓存名称
     */
    public static final String USER_TAG_RECOMMEND_CACHE = "user:tag:recommend";

    /**
     * 用户标签推荐缓存KEY
     */
    public static final String USER_TAG_RECOMMEND_KEY = "#userId + '_' + #limit";

    /**
     * 用户标签推荐缓存过期时间（分钟）
     */
    public static final int USER_TAG_RECOMMEND_CACHE_EXPIRE = 30;

    // =================== 内容标签相关缓存 ===================

    /**
     * 内容标签缓存名称
     */
    public static final String CONTENT_TAGS_CACHE = "content:tags";

    /**
     * 内容标签缓存KEY
     */
    public static final String CONTENT_TAGS_KEY = "#contentId";

    /**
     * 内容标签缓存过期时间（分钟）
     */
    public static final int CONTENT_TAGS_CACHE_EXPIRE = 15;

    /**
     * 标签内容缓存名称
     */
    public static final String TAG_CONTENTS_CACHE = "tag:contents";

    /**
     * 标签内容缓存KEY
     */
    public static final String TAG_CONTENTS_KEY = "#tagId + '_' + #limit";

    /**
     * 标签内容缓存过期时间（分钟）
     */
    public static final int TAG_CONTENTS_CACHE_EXPIRE = 10;

    /**
     * 用户内容推荐缓存名称
     */
    public static final String USER_CONTENT_RECOMMEND_CACHE = "user:content:recommend";

    /**
     * 用户内容推荐缓存KEY
     */
    public static final String USER_CONTENT_RECOMMEND_KEY = "#userId + '_' + #limit";

    /**
     * 用户内容推荐缓存过期时间（分钟）
     */
    public static final int USER_CONTENT_RECOMMEND_CACHE_EXPIRE = 20;

    // =================== 统计相关缓存 ===================

    /**
     * 标签统计缓存名称
     */
    public static final String TAG_STATISTICS_CACHE = "tag:statistics";

    /**
     * 标签统计缓存KEY
     */
    public static final String TAG_STATISTICS_KEY = "#tagId";

    /**
     * 标签统计缓存过期时间（分钟）
     */
    public static final int TAG_STATISTICS_CACHE_EXPIRE = 60;

    /**
     * 用户标签统计缓存名称
     */
    public static final String USER_TAG_STATISTICS_CACHE = "user:tag:statistics";

    /**
     * 用户标签统计缓存KEY
     */
    public static final String USER_TAG_STATISTICS_KEY = "#userId";

    /**
     * 用户标签统计缓存过期时间（分钟）
     */
    public static final int USER_TAG_STATISTICS_CACHE_EXPIRE = 60;
}
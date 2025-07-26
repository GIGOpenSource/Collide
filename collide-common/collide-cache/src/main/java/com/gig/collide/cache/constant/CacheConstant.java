package com.gig.collide.cache.constant;

/**
 * 缓存常量类
 * 统一管理 Collide 项目的缓存名称和配置
 *
 * @author Collide Team
 */
public class CacheConstant {

    /**
     * 缓存key分隔符
     */
    public static final String CACHE_KEY_SEPARATOR = ":";

    // ========== 用户相关缓存 ==========
    /**
     * 用户基础信息缓存
     */
    public static final String USER_INFO_CACHE = ":user:cache:info:";
    
    /**
     * 用户扩展信息缓存
     */
    public static final String USER_PROFILE_CACHE = ":user:cache:profile:";
    
    /**
     * 用户名查询缓存
     */
    public static final String USER_USERNAME_CACHE = ":user:cache:username:";
    
    /**
     * 邮箱查询缓存
     */
    public static final String USER_EMAIL_CACHE = ":user:cache:email:";
    
    /**
     * 手机号查询缓存
     */
    public static final String USER_PHONE_CACHE = ":user:cache:phone:";

    // ========== 内容相关缓存 ==========
    /**
     * 内容信息缓存
     */
    public static final String CONTENT_INFO_CACHE = ":content:cache:info:";
    
    /**
     * 内容列表缓存
     */
    public static final String CONTENT_LIST_CACHE = ":content:cache:list:";
    
    /**
     * 热门内容缓存
     */
    public static final String CONTENT_HOT_CACHE = ":content:cache:hot:";

    // ========== 关注相关缓存 ==========
    /**
     * 关注关系缓存
     */
    public static final String FOLLOW_RELATION_CACHE = ":follow:cache:relation:";
    
    /**
     * 关注统计缓存
     */
    public static final String FOLLOW_STATISTICS_CACHE = ":follow:cache:statistics:";

    // ========== 评论相关缓存 ==========
    /**
     * 评论列表缓存
     */
    public static final String COMMENT_LIST_CACHE = ":comment:cache:list:";

    // ========== 点赞相关缓存 ==========
    /**
     * 用户点赞状态缓存
     */
    public static final String LIKE_USER_STATUS_CACHE = ":like:cache:user:status:";
    
    /**
     * 点赞统计信息缓存
     */
    public static final String LIKE_STATISTICS_CACHE = ":like:cache:statistics:";
    
    /**
     * 用户点赞历史缓存
     */
    public static final String LIKE_USER_HISTORY_CACHE = ":like:cache:user:history:";
    
    /**
     * 热门点赞内容缓存
     */
    public static final String LIKE_HOT_CONTENT_CACHE = ":like:cache:hot:content:";
    
    /**
     * 活跃点赞用户缓存
     */
    public static final String LIKE_ACTIVE_USERS_CACHE = ":like:cache:active:users:";

    // ========== 缓存时间配置（分钟） ==========
    /**
     * 用户信息缓存时间 - 60分钟
     */
    public static final int USER_CACHE_EXPIRE = 60;
    
    /**
     * 用户查询缓存时间 - 120分钟
     */
    public static final int USER_QUERY_CACHE_EXPIRE = 120;
    
    /**
     * 内容信息缓存时间 - 30分钟
     */
    public static final int CONTENT_CACHE_EXPIRE = 30;
    
    /**
     * 内容列表缓存时间 - 10分钟
     */
    public static final int CONTENT_LIST_CACHE_EXPIRE = 10;
    
    /**
     * 关注关系缓存时间 - 30分钟
     */
    public static final int FOLLOW_CACHE_EXPIRE = 30;
    
    /**
     * 关注统计缓存时间 - 60分钟
     */
    public static final int FOLLOW_STATISTICS_CACHE_EXPIRE = 60;
    
    /**
     * 点赞状态缓存时间 - 15分钟
     */
    public static final int LIKE_STATUS_CACHE_EXPIRE = 15;
    
    /**
     * 点赞统计缓存时间 - 30分钟
     */
    public static final int LIKE_STATISTICS_CACHE_EXPIRE = 30;
    
    /**
     * 点赞历史缓存时间 - 5分钟
     */
    public static final int LIKE_HISTORY_CACHE_EXPIRE = 5;
    
    /**
     * 本地缓存时间 - 10分钟
     */
    public static final int LOCAL_CACHE_EXPIRE = 10;
}

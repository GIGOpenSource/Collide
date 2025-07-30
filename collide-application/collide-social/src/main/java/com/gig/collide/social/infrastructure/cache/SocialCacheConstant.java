package com.gig.collide.social.infrastructure.cache;

import java.util.concurrent.TimeUnit;

/**
 * 社交动态模块缓存常量配置
 * 对齐content模块设计风格
 * 
 * @author GIG Team
 * @version 2.0.0 (缓存增强版)
 * @since 2024-01-16
 */
public class SocialCacheConstant {

    // =================== 缓存名称常量 ===================
    
    /**
     * 动态详情缓存
     */
    public static final String DYNAMIC_DETAIL_CACHE = "social:dynamic:detail";
    
    /**
     * 动态列表缓存
     */
    public static final String DYNAMIC_LIST_CACHE = "social:dynamic:list";
    
    /**
     * 最新动态缓存
     */
    public static final String DYNAMIC_LATEST_CACHE = "social:dynamic:latest";
    
    /**
     * 用户动态缓存
     */
    public static final String DYNAMIC_USER_CACHE = "social:dynamic:user";
    
    /**
     * 动态统计缓存
     */
    public static final String DYNAMIC_STATISTICS_CACHE = "social:dynamic:statistics";
    
    /**
     * 动态点赞记录缓存
     */
    public static final String DYNAMIC_LIKES_CACHE = "social:dynamic:likes";
    
    /**
     * 动态评论记录缓存
     */
    public static final String DYNAMIC_COMMENTS_CACHE = "social:dynamic:comments";
    
    /**
     * 热门动态缓存
     */
    public static final String DYNAMIC_HOT_CACHE = "social:dynamic:hot";
    
    /**
     * 用户点赞状态缓存
     */
    public static final String USER_LIKE_STATUS_CACHE = "social:user:like:status";
    
    /**
     * 用户互动记录聚合缓存
     */
    public static final String USER_INTERACTIONS_CACHE = "social:user:interactions";

    // =================== 缓存过期时间常量 ===================
    
    /**
     * 动态详情缓存过期时间 - 60分钟
     */
    public static final int DYNAMIC_DETAIL_EXPIRE = 60;
    
    /**
     * 动态列表缓存过期时间 - 30分钟
     */
    public static final int DYNAMIC_LIST_EXPIRE = 30;
    
    /**
     * 最新动态缓存过期时间 - 5分钟
     */
    public static final int DYNAMIC_LATEST_EXPIRE = 5;
    
    /**
     * 用户动态缓存过期时间 - 30分钟
     */
    public static final int DYNAMIC_USER_EXPIRE = 30;
    
    /**
     * 动态统计缓存过期时间 - 10分钟
     */
    public static final int DYNAMIC_STATISTICS_EXPIRE = 10;
    
    /**
     * 动态点赞记录缓存过期时间 - 15分钟
     */
    public static final int DYNAMIC_LIKES_EXPIRE = 15;
    
    /**
     * 动态评论记录缓存过期时间 - 20分钟
     */
    public static final int DYNAMIC_COMMENTS_EXPIRE = 20;
    
    /**
     * 热门动态缓存过期时间 - 15分钟
     */
    public static final int DYNAMIC_HOT_EXPIRE = 15;
    
    /**
     * 用户点赞状态缓存过期时间 - 30分钟
     */
    public static final int USER_LIKE_STATUS_EXPIRE = 30;
    
    /**
     * 用户互动记录缓存过期时间 - 10分钟
     */
    public static final int USER_INTERACTIONS_EXPIRE = 10;

    // =================== 缓存时间单位 ===================
    
    /**
     * 默认时间单位 - 分钟
     */
    public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MINUTES;

    // =================== 缓存键模板 ===================
    
    /**
     * 动态详情缓存键模板
     */
    public static final String DYNAMIC_DETAIL_KEY = "'social:dynamic:detail:' + #dynamicId + ':' + #includeDeleted";
    
    /**
     * 动态列表缓存键模板  
     */
    public static final String DYNAMIC_LIST_KEY = "'social:dynamic:list:' + #request.currentPage + ':' + #request.pageSize + ':' + (#request.dynamicType ?: 'all') + ':' + (#request.status ?: 'all')";
    
    /**
     * 最新动态缓存键模板
     */
    public static final String DYNAMIC_LATEST_KEY = "'social:dynamic:latest:' + #currentPage + ':' + #pageSize + ':' + (#dynamicType ?: 'all')";
    
    /**
     * 用户动态缓存键模板
     */
    public static final String DYNAMIC_USER_KEY = "'social:dynamic:user:' + #userId + ':' + #currentPage + ':' + #pageSize + ':' + (#dynamicType ?: 'all')";
    
    /**
     * 动态统计缓存键模板
     */
    public static final String DYNAMIC_STATISTICS_KEY = "'social:dynamic:statistics:' + #dynamicId";
    
    /**
     * 动态点赞记录缓存键模板
     */
    public static final String DYNAMIC_LIKES_KEY = "'social:dynamic:likes:' + #dynamicId + ':' + #currentPage + ':' + #pageSize";
    
    /**
     * 动态评论记录缓存键模板
     */
    public static final String DYNAMIC_COMMENTS_KEY = "'social:dynamic:comments:' + #dynamicId + ':' + #currentPage + ':' + #pageSize";
    
    /**
     * 热门动态缓存键模板
     */
    public static final String DYNAMIC_HOT_KEY = "'social:dynamic:hot:' + (#dynamicType ?: 'all') + ':' + (#timeRange ?: '7') + ':' + #currentPage + ':' + #pageSize";
    
    /**
     * 用户点赞状态缓存键模板
     */
    public static final String USER_LIKE_STATUS_KEY = "'social:user:like:status:' + #userId + ':' + #dynamicId";
    
    /**
     * 用户互动记录缓存键模板
     */
    public static final String USER_INTERACTIONS_KEY = "'social:user:interactions:' + #userId + ':' + #currentPage + ':' + #pageSize";

    // =================== 缓存失效键模板 ===================
    
    /**
     * 动态相关缓存失效键模板
     */
    public static final String DYNAMIC_INVALIDATE_KEY = "'social:dynamic:*:' + #dynamicId + '*'";
    
    /**
     * 用户动态缓存失效键模板
     */
    public static final String USER_DYNAMIC_INVALIDATE_KEY = "'social:dynamic:user:' + #userId + '*'";
    
    /**
     * 最新动态缓存失效键模板
     */
    public static final String LATEST_DYNAMIC_INVALIDATE_KEY = "'social:dynamic:latest:*'";
    
    /**
     * 热门动态缓存失效键模板
     */
    public static final String HOT_DYNAMIC_INVALIDATE_KEY = "'social:dynamic:hot:*'";

    private SocialCacheConstant() {
        // 工具类，禁止实例化
    }
}
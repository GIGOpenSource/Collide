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

    // ========== 社交相关缓存 ==========
    /**
     * 用户关注列表缓存
     */
    public static final String SOCIAL_FOLLOWING_USERS_CACHE = ":social:cache:following:users:";
    
    /**
     * 社交动态信息缓存
     */
    public static final String SOCIAL_POST_INFO_CACHE = ":social:cache:post:info:";
    
    /**
     * 用户时间线缓存
     */
    public static final String SOCIAL_USER_TIMELINE_CACHE = ":social:cache:user:timeline:";
    
    /**
     * 推荐时间线缓存
     */
    public static final String SOCIAL_RECOMMEND_TIMELINE_CACHE = ":social:cache:recommend:timeline:";
    
    /**
     * 热门动态缓存
     */
    public static final String SOCIAL_HOT_POSTS_CACHE = ":social:cache:hot:posts:";
    
    /**
     * 社交互动信息缓存
     */
    public static final String SOCIAL_INTERACTION_CACHE = ":social:cache:interaction:";
    
    /**
     * 用户互动状态缓存
     */
    public static final String SOCIAL_USER_INTERACTION_STATUS_CACHE = ":social:cache:user:interaction:status:";
    
    /**
     * 社交统计信息缓存
     */
    public static final String SOCIAL_STATISTICS_CACHE = ":social:cache:statistics:";
    
    /**
     * 用户动态列表缓存
     */
    public static final String SOCIAL_USER_POSTS_CACHE = ":social:cache:user:posts:";
    
    /**
     * 动态互动用户列表缓存
     */
    public static final String SOCIAL_POST_INTERACTION_USERS_CACHE = ":social:cache:post:interaction:users:";
    
    /**
     * 附近动态缓存
     */
    public static final String SOCIAL_NEARBY_POSTS_CACHE = ":social:cache:nearby:posts:";

    // ========== 标签相关缓存 ==========
    /**
     * 标签详细信息缓存
     */
    public static final String TAG_INFO_CACHE = ":tag:cache:info:";
    
    /**
     * 标签类型查询缓存
     */
    public static final String TAG_TYPE_CACHE = ":tag:cache:type:";
    
    /**
     * 热门标签缓存
     */
    public static final String TAG_HOT_CACHE = ":tag:cache:hot:";
    
    /**
     * 标签搜索缓存
     */
    public static final String TAG_SEARCH_CACHE = ":tag:cache:search:";
    
    /**
     * 用户兴趣标签缓存
     */
    public static final String TAG_USER_INTEREST_CACHE = ":tag:cache:user:interest:";
    
    /**
     * 标签推荐缓存
     */
    public static final String TAG_RECOMMEND_CACHE = ":tag:cache:recommend:";

    // ========== 搜索相关缓存 ==========
    /**
     * 搜索历史缓存
     */
    public static final String SEARCH_HISTORY_CACHE = ":search:cache:history:";
    
    /**
     * 搜索统计缓存
     */
    public static final String SEARCH_STATISTICS_CACHE = ":search:cache:statistics:";
    
    /**
     * 搜索建议缓存
     */
    public static final String SEARCH_SUGGESTION_CACHE = ":search:cache:suggestion:";
    
    /**
     * 搜索结果缓存
     */
    public static final String SEARCH_RESULT_CACHE = ":search:cache:result:";
    
    /**
     * 热门关键词缓存
     */
    public static final String SEARCH_HOT_KEYWORDS_CACHE = ":search:cache:hot:keywords:";
    
    /**
     * 推荐内容缓存
     */
    public static final String SEARCH_RECOMMEND_CONTENT_CACHE = ":search:cache:recommend:content:";
    
    /**
     * 用户搜索索引缓存
     */
    public static final String SEARCH_USER_INDEX_CACHE = ":search:cache:user:index:";
    
    /**
     * 内容搜索索引缓存
     */
    public static final String SEARCH_CONTENT_INDEX_CACHE = ":search:cache:content:index:";
    
    /**
     * 评论搜索索引缓存
     */
    public static final String SEARCH_COMMENT_INDEX_CACHE = ":search:cache:comment:index:";

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
    
    /**
     * 社交关注列表缓存时间 - 60分钟
     */
    public static final int SOCIAL_FOLLOWING_CACHE_EXPIRE = 60;
    
    /**
     * 社交动态信息缓存时间 - 30分钟
     */
    public static final int SOCIAL_POST_CACHE_EXPIRE = 30;
    
    /**
     * 社交时间线缓存时间 - 15分钟
     */
    public static final int SOCIAL_TIMELINE_CACHE_EXPIRE = 15;
    
    /**
     * 社交互动缓存时间 - 30分钟
     */
    public static final int SOCIAL_INTERACTION_CACHE_EXPIRE = 30;
    
    /**
     * 社交统计缓存时间 - 60分钟
     */
    public static final int SOCIAL_STATISTICS_CACHE_EXPIRE = 60;
    
    /**
     * 用户动态列表缓存时间 - 10分钟
     */
    public static final int SOCIAL_USER_POSTS_CACHE_EXPIRE = 10;
    
    /**
     * 附近动态缓存时间 - 5分钟
     */
    public static final int SOCIAL_NEARBY_POSTS_CACHE_EXPIRE = 5;
    
    /**
     * 标签信息缓存时间 - 60分钟
     */
    public static final int TAG_CACHE_EXPIRE = 60;
    
    /**
     * 标签类型查询缓存时间 - 120分钟
     */
    public static final int TAG_TYPE_CACHE_EXPIRE = 120;
    
    /**
     * 热门标签缓存时间 - 30分钟
     */
    public static final int TAG_HOT_CACHE_EXPIRE = 30;
    
    /**
     * 标签搜索缓存时间 - 10分钟
     */
    public static final int TAG_SEARCH_CACHE_EXPIRE = 10;
    
    /**
     * 用户兴趣标签缓存时间 - 30分钟
     */
    public static final int TAG_USER_INTEREST_CACHE_EXPIRE = 30;
    
    /**
     * 标签推荐缓存时间 - 15分钟
     */
    public static final int TAG_RECOMMEND_CACHE_EXPIRE = 15;
    
    /**
     * 搜索历史缓存时间 - 60分钟
     */
    public static final int SEARCH_HISTORY_CACHE_EXPIRE = 60;
    
    /**
     * 搜索统计缓存时间 - 30分钟
     */
    public static final int SEARCH_STATISTICS_CACHE_EXPIRE = 30;
    
    /**
     * 搜索建议缓存时间 - 20分钟
     */
    public static final int SEARCH_SUGGESTION_CACHE_EXPIRE = 20;
    
    /**
     * 搜索结果缓存时间 - 15分钟
     */
    public static final int SEARCH_RESULT_CACHE_EXPIRE = 15;
    
    /**
     * 热门关键词缓存时间 - 30分钟
     */
    public static final int SEARCH_HOT_KEYWORDS_CACHE_EXPIRE = 30;
    
    /**
     * 推荐内容缓存时间 - 20分钟
     */
    public static final int SEARCH_RECOMMEND_CONTENT_CACHE_EXPIRE = 20;
    
    /**
     * 搜索索引缓存时间 - 45分钟
     */
    public static final int SEARCH_INDEX_CACHE_EXPIRE = 45;
}

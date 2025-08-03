package com.gig.collide.tag.infrastructure.cache;

import java.util.concurrent.TimeUnit;

/**
 * 标签模块缓存常量配置
 * 
 * @author GIG Team
 * @version 2.0.0
 * @since 2024-01-01
 */
public class TagCacheConstant {

    // =================== 缓存名称常量 ===================
    
    /**
     * 标签详情缓存
     */
    public static final String TAG_DETAIL_CACHE = "tag:detail";
    
    /**
     * 标签列表缓存
     */
    public static final String TAG_LIST_CACHE = "tag:list";
    
    /**
     * 标签类型缓存
     */
    public static final String TAG_TYPE_CACHE = "tag:type";
    
    /**
     * 热门标签缓存
     */
    public static final String TAG_HOT_CACHE = "tag:hot";
    
    /**
     * 搜索结果缓存
     */
    public static final String TAG_SEARCH_CACHE = "tag:search";
    
    /**
     * 用户兴趣标签缓存
     */
    public static final String TAG_USER_INTEREST_CACHE = "tag:user:interest";
    
    /**
     * 内容标签缓存
     */
    public static final String TAG_CONTENT_CACHE = "tag:content";

    // =================== 缓存过期时间常量 ===================
    
    /**
     * 标签详情缓存过期时间 - 120分钟
     */
    public static final int TAG_DETAIL_EXPIRE = 120;
    
    /**
     * 标签列表缓存过期时间 - 30分钟
     */
    public static final int TAG_LIST_EXPIRE = 30;
    
    /**
     * 标签类型缓存过期时间 - 60分钟
     */
    public static final int TAG_TYPE_EXPIRE = 60;
    
    /**
     * 热门标签缓存过期时间 - 15分钟
     */
    public static final int TAG_HOT_EXPIRE = 15;
    
    /**
     * 搜索结果缓存过期时间 - 10分钟
     */
    public static final int TAG_SEARCH_EXPIRE = 10;
    
    /**
     * 用户兴趣标签缓存过期时间 - 30分钟
     */
    public static final int TAG_USER_INTEREST_EXPIRE = 30;
    
    /**
     * 内容标签缓存过期时间 - 45分钟
     */
    public static final int TAG_CONTENT_EXPIRE = 45;

    // =================== 缓存时间单位 ===================
    
    /**
     * 默认时间单位 - 分钟
     */
    public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MINUTES;

    // =================== 缓存键模板 ===================
    
    /**
     * 标签详情缓存键模板
     */
    public static final String TAG_DETAIL_KEY = "'tag:detail:' + #tagId";
    
    /**
     * 标签列表缓存键模板
     */
    public static final String TAG_LIST_KEY = "'tag:list:' + #request.currentPage + ':' + #request.pageSize + ':' + (#request.tagType ?: 'all') + ':' + (#request.status ?: 'all')";
    
    /**
     * 标签类型缓存键模板
     */
    public static final String TAG_TYPE_KEY = "'tag:type:' + #tagType";
    
    /**
     * 热门标签缓存键模板
     */
    public static final String TAG_HOT_KEY = "'tag:hot:' + (#limit ?: '10')";
    
    /**
     * 搜索结果缓存键模板
     */
    public static final String TAG_SEARCH_KEY = "'tag:search:' + #keyword + ':' + (#limit ?: '10')";
    
    /**
     * 用户兴趣标签缓存键模板
     */
    public static final String TAG_USER_INTEREST_KEY = "'tag:user:interest:' + #userId";
    
    /**
     * 内容标签缓存键模板
     */
    public static final String TAG_CONTENT_KEY = "'tag:content:' + #contentId";

    // =================== 缓存失效键模板 ===================
    
    /**
     * 标签相关缓存失效键模板
     */
    public static final String TAG_INVALIDATE_KEY = "'tag:*:' + #tagId + '*'";
    
    /**
     * 用户兴趣标签缓存失效键模板
     */
    public static final String TAG_USER_INTEREST_INVALIDATE_KEY = "'tag:user:interest:' + #userId + '*'";
    
    /**
     * 内容标签缓存失效键模板
     */
    public static final String TAG_CONTENT_INVALIDATE_KEY = "'tag:content:' + #contentId + '*'";

    private TagCacheConstant() {
        // 工具类，禁止实例化
    }
}
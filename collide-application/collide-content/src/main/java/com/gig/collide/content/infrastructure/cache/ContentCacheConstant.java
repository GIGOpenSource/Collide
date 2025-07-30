package com.gig.collide.content.infrastructure.cache;

import java.util.concurrent.TimeUnit;

/**
 * 内容模块缓存常量配置
 * 
 * @author Collide
 * @version 2.0.0
 * @since 2024-01-01
 */
public class ContentCacheConstant {

    // =================== 缓存名称常量 ===================
    
    /**
     * 内容详情缓存
     */
    public static final String CONTENT_DETAIL_CACHE = "content:detail";
    
    /**
     * 内容列表缓存
     */
    public static final String CONTENT_LIST_CACHE = "content:list";
    
    /**
     * 章节详情缓存
     */
    public static final String CHAPTER_DETAIL_CACHE = "content:chapter:detail";
    
    /**
     * 章节列表缓存
     */
    public static final String CHAPTER_LIST_CACHE = "content:chapter:list";
    
    /**
     * 内容统计缓存
     */
    public static final String CONTENT_STATISTICS_CACHE = "content:statistics";
    
    /**
     * 热门内容缓存
     */
    public static final String CONTENT_POPULAR_CACHE = "content:popular";
    
    /**
     * 最新内容缓存
     */
    public static final String CONTENT_LATEST_CACHE = "content:latest";
    
    /**
     * 作者内容缓存
     */
    public static final String CONTENT_BY_AUTHOR_CACHE = "content:author";
    
    /**
     * 分类内容缓存
     */
    public static final String CONTENT_BY_CATEGORY_CACHE = "content:category";
    
    /**
     * 搜索结果缓存
     */
    public static final String CONTENT_SEARCH_CACHE = "content:search";

    // =================== 缓存过期时间常量 ===================
    
    /**
     * 内容详情缓存过期时间 - 60分钟
     */
    public static final int CONTENT_DETAIL_EXPIRE = 60;
    
    /**
     * 内容列表缓存过期时间 - 30分钟
     */
    public static final int CONTENT_LIST_EXPIRE = 30;
    
    /**
     * 章节详情缓存过期时间 - 120分钟
     */
    public static final int CHAPTER_DETAIL_EXPIRE = 120;
    
    /**
     * 章节列表缓存过期时间 - 60分钟
     */
    public static final int CHAPTER_LIST_EXPIRE = 60;
    
    /**
     * 统计信息缓存过期时间 - 10分钟
     */
    public static final int STATISTICS_EXPIRE = 10;
    
    /**
     * 热门内容缓存过期时间 - 15分钟
     */
    public static final int POPULAR_CONTENT_EXPIRE = 15;
    
    /**
     * 最新内容缓存过期时间 - 5分钟
     */
    public static final int LATEST_CONTENT_EXPIRE = 5;
    
    /**
     * 作者内容缓存过期时间 - 30分钟
     */
    public static final int AUTHOR_CONTENT_EXPIRE = 30;
    
    /**
     * 分类内容缓存过期时间 - 45分钟
     */
    public static final int CATEGORY_CONTENT_EXPIRE = 45;
    
    /**
     * 搜索结果缓存过期时间 - 15分钟
     */
    public static final int SEARCH_RESULT_EXPIRE = 15;

    // =================== 缓存时间单位 ===================
    
    /**
     * 默认时间单位 - 分钟
     */
    public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MINUTES;

    // =================== 缓存键模板 ===================
    
    /**
     * 内容详情缓存键模板
     */
    public static final String CONTENT_DETAIL_KEY = "'content:detail:' + #contentId + ':' + #includeOffline";
    
    /**
     * 内容列表缓存键模板  
     */
    public static final String CONTENT_LIST_KEY = "'content:list:' + #request.currentPage + ':' + #request.pageSize + ':' + (#request.contentType ?: 'all') + ':' + (#request.status ?: 'all')";
    
    /**
     * 章节详情缓存键模板
     */
    public static final String CHAPTER_DETAIL_KEY = "'content:chapter:detail:' + #chapterId";
    
    /**
     * 章节列表缓存键模板
     */
    public static final String CHAPTER_LIST_KEY = "'content:chapter:list:' + #contentId + ':' + #currentPage + ':' + #pageSize + ':' + (#status ?: 'all')";
    
    /**
     * 内容统计缓存键模板
     */
    public static final String CONTENT_STATISTICS_KEY = "'content:statistics:' + #contentId";
    
    /**
     * 热门内容缓存键模板
     */
    public static final String POPULAR_CONTENT_KEY = "'content:popular:' + (#contentType ?: 'all') + ':' + (#timeRange ?: '7') + ':' + #currentPage + ':' + #pageSize";
    
    /**
     * 最新内容缓存键模板
     */
    public static final String LATEST_CONTENT_KEY = "'content:latest:' + (#contentType ?: 'all') + ':' + #currentPage + ':' + #pageSize";
    
    /**
     * 作者内容缓存键模板
     */
    public static final String AUTHOR_CONTENT_KEY = "'content:author:' + #authorId + ':' + (#contentType ?: 'all') + ':' + (#status ?: 'all') + ':' + #currentPage + ':' + #pageSize";
    
    /**
     * 分类内容缓存键模板
     */
    public static final String CATEGORY_CONTENT_KEY = "'content:category:' + #categoryId + ':' + (#contentType ?: 'all') + ':' + #currentPage + ':' + #pageSize";
    
    /**
     * 搜索结果缓存键模板
     */
    public static final String SEARCH_CONTENT_KEY = "'content:search:' + #keyword + ':' + (#contentType ?: 'all') + ':' + #currentPage + ':' + #pageSize";

    // =================== 缓存失效键模板 ===================
    
    /**
     * 内容相关缓存失效键模板
     */
    public static final String CONTENT_INVALIDATE_KEY = "'content:*:' + #contentId + '*'";
    
    /**
     * 章节相关缓存失效键模板
     */
    public static final String CHAPTER_INVALIDATE_KEY = "'content:chapter:*:' + #contentId + '*'";
    
    /**
     * 作者内容缓存失效键模板
     */
    public static final String AUTHOR_CONTENT_INVALIDATE_KEY = "'content:author:' + #authorId + '*'";
    
    /**
     * 分类内容缓存失效键模板
     */
    public static final String CATEGORY_CONTENT_INVALIDATE_KEY = "'content:category:' + #categoryId + '*'";

    private ContentCacheConstant() {
        // 工具类，禁止实例化
    }
}
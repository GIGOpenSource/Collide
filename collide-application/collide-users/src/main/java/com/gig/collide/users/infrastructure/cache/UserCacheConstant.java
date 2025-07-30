package com.gig.collide.users.infrastructure.cache;

import java.util.concurrent.TimeUnit;

/**
 * 用户模块缓存常量配置
 * 
 * @author GIG Team
 * @version 2.0.0
 * @since 2024-01-01
 */
public class UserCacheConstant {

    // =================== 缓存名称常量 ===================
    
    /**
     * 用户详情缓存
     */
    public static final String USER_DETAIL_CACHE = "user:detail";
    
    /**
     * 用户列表缓存
     */
    public static final String USER_LIST_CACHE = "user:list";
    
    /**
     * 用户名查询缓存
     */
    public static final String USER_USERNAME_CACHE = "user:username";
    
    /**
     * 用户统计缓存
     */
    public static final String USER_STATISTICS_CACHE = "user:statistics";
    
    /**
     * 钱包详情缓存
     */
    public static final String WALLET_DETAIL_CACHE = "user:wallet:detail";
    
    /**
     * 钱包余额缓存
     */
    public static final String WALLET_BALANCE_CACHE = "user:wallet:balance";

    // =================== 缓存过期时间常量 ===================
    
    /**
     * 用户详情缓存过期时间 - 120分钟
     */
    public static final int USER_DETAIL_EXPIRE = 120;
    
    /**
     * 用户列表缓存过期时间 - 30分钟
     */
    public static final int USER_LIST_EXPIRE = 30;
    
    /**
     * 用户名查询缓存过期时间 - 60分钟
     */
    public static final int USERNAME_EXPIRE = 60;
    
    /**
     * 用户统计缓存过期时间 - 15分钟
     */
    public static final int USER_STATISTICS_EXPIRE = 15;
    
    /**
     * 钱包详情缓存过期时间 - 60分钟
     */
    public static final int WALLET_DETAIL_EXPIRE = 60;
    
    /**
     * 钱包余额缓存过期时间 - 30分钟
     */
    public static final int WALLET_BALANCE_EXPIRE = 30;

    // =================== 缓存时间单位 ===================
    
    /**
     * 默认时间单位 - 分钟
     */
    public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MINUTES;

    // =================== 缓存键模板 ===================
    
    /**
     * 用户详情缓存键模板
     */
    public static final String USER_DETAIL_KEY = "'user:detail:' + #userId";
    
    /**
     * 用户列表缓存键模板
     */
    public static final String USER_LIST_KEY = "'user:list:' + #request.currentPage + ':' + #request.pageSize + ':' + (#request.status ?: 'all')";
    
    /**
     * 用户名查询缓存键模板
     */
    public static final String USERNAME_KEY = "'user:username:' + #username";
    
    /**
     * 用户统计缓存键模板
     */
    public static final String USER_STATISTICS_KEY = "'user:statistics:' + #userId + ':' + #statsType";
    
    /**
     * 钱包详情缓存键模板
     */
    public static final String WALLET_DETAIL_KEY = "'user:wallet:detail:' + #userId";
    
    /**
     * 钱包余额缓存键模板
     */
    public static final String WALLET_BALANCE_KEY = "'user:wallet:balance:' + #userId";

    // =================== 缓存失效键模板 ===================
    
    /**
     * 用户相关缓存失效键模板
     */
    public static final String USER_INVALIDATE_KEY = "'user:*:' + #userId + '*'";
    
    /**
     * 钱包相关缓存失效键模板
     */
    public static final String WALLET_INVALIDATE_KEY = "'user:wallet:*:' + #userId + '*'";

    private UserCacheConstant() {
        // 工具类，禁止实例化
    }
}
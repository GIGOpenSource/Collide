package com.gig.collide.users.infrastructure.cache;

import java.util.concurrent.TimeUnit;

/**
 * 用户模块缓存常量配置 - 支持6表结构
 * 
 * @author GIG Team
 * @version 2.0.0
 * @since 2024-01-01
 */
public class UserCacheConstant {

    // =================== 用户核心缓存（t_user表） ===================
    
    /**
     * 用户核心详情缓存
     */
    public static final String USER_CORE_DETAIL_CACHE = "user:core:detail";
    
    /**
     * 用户核心列表缓存
     */
    public static final String USER_CORE_LIST_CACHE = "user:core:list";
    
    /**
     * 用户名查询缓存
     */
    public static final String USER_CORE_USERNAME_CACHE = "user:core:username";
    
    /**
     * 邮箱查询缓存
     */
    public static final String USER_EMAIL_CACHE = "user:core:email";
    
    /**
     * 手机号查询缓存
     */
    public static final String USER_PHONE_CACHE = "user:core:phone";

    // =================== 用户资料缓存（t_user_profile表） ===================
    
    /**
     * 用户资料详情缓存
     */
    public static final String USER_PROFILE_DETAIL_CACHE = "user:profile:detail";
    
    /**
     * 用户资料列表缓存
     */
    public static final String USER_PROFILE_LIST_CACHE = "user:profile:list";
    
    /**
     * 昵称查询缓存
     */
    public static final String USER_NICKNAME_CACHE = "user:profile:nickname";

    // =================== 用户统计缓存（t_user_stats表） ===================
    
    /**
     * 用户统计详情缓存
     */
    public static final String USER_STATS_DETAIL_CACHE = "user:stats:detail";
    
    /**
     * 用户统计列表缓存
     */
    public static final String USER_STATS_LIST_CACHE = "user:stats:list";
    
    /**
     * 用户统计排行榜缓存
     */
    public static final String USER_STATS_RANKING_CACHE = "user:stats:ranking";

    // =================== 用户角色缓存（t_user_role表） ===================
    
    /**
     * 用户角色详情缓存
     */
    public static final String USER_ROLE_DETAIL_CACHE = "user:role:detail";
    
    /**
     * 用户角色列表缓存
     */
    public static final String USER_ROLE_LIST_CACHE = "user:role:list";
    
    /**
     * 用户角色-用户关系缓存
     */
    public static final String USER_ROLE_USER_CACHE = "user:role:user";
    
    /**
     * 角色用户列表缓存
     */
    public static final String ROLE_USERS_CACHE = "user:role:users";

    // =================== 用户钱包缓存（t_user_wallet表） ===================
    
    /**
     * 用户钱包详情缓存
     */
    public static final String USER_WALLET_DETAIL_CACHE = "user:wallet:detail";
    
    /**
     * 用户钱包列表缓存
     */
    public static final String USER_WALLET_LIST_CACHE = "user:wallet:list";
    
    /**
     * 钱包余额缓存
     */
    public static final String WALLET_BALANCE_CACHE = "user:wallet:balance";
    
    /**
     * 金币余额缓存
     */
    public static final String WALLET_COIN_CACHE = "user:wallet:coin";

    // =================== 用户拉黑缓存（t_user_block表） ===================
    
    /**
     * 用户拉黑详情缓存
     */
    public static final String USER_BLOCK_DETAIL_CACHE = "user:block:detail";
    
    /**
     * 用户拉黑关系缓存
     */
    public static final String USER_BLOCK_RELATION_CACHE = "user:block:relation";
    
    /**
     * 用户拉黑列表缓存
     */
    public static final String USER_BLOCK_LIST_CACHE = "user:block:list";

    // =================== 缓存过期时间常量 ===================
    
    /**
     * 用户核心详情缓存过期时间 - 120分钟
     */
    public static final int USER_CORE_DETAIL_EXPIRE = 120;
    
    /**
     * 用户核心列表缓存过期时间 - 30分钟
     */
    public static final int USER_CORE_LIST_EXPIRE = 30;
    
    /**
     * 用户名查询缓存过期时间 - 240分钟（较长，用户名不常变更）
     */
    public static final int USER_CORE_USERNAME_EXPIRE = 240;
    
    /**
     * 邮箱查询缓存过期时间 - 240分钟
     */
    public static final int EMAIL_EXPIRE = 240;
    
    /**
     * 手机号查询缓存过期时间 - 240分钟
     */
    public static final int PHONE_EXPIRE = 240;
    
    /**
     * 用户资料详情缓存过期时间 - 60分钟
     */
    public static final int USER_PROFILE_DETAIL_EXPIRE = 60;
    
    /**
     * 用户资料列表缓存过期时间 - 20分钟
     */
    public static final int USER_PROFILE_LIST_EXPIRE = 20;
    
    /**
     * 昵称查询缓存过期时间 - 60分钟
     */
    public static final int NICKNAME_EXPIRE = 60;
    
    /**
     * 用户统计详情缓存过期时间 - 15分钟（更新较频繁）
     */
    public static final int USER_STATS_DETAIL_EXPIRE = 15;
    
    /**
     * 用户统计列表缓存过期时间 - 20分钟
     */
    public static final int USER_STATS_LIST_EXPIRE = 20;
    
    /**
     * 统计排行榜缓存过期时间 - 30分钟
     */
    public static final int USER_STATS_RANKING_EXPIRE = 30;
    
    /**
     * 统计排行榜缓存过期时间 - 30分钟
     */
    public static final int STATS_RANKING_EXPIRE = 30;
    
    /**
     * 用户角色详情缓存过期时间 - 180分钟（角色变更较少）
     */
    public static final int USER_ROLE_DETAIL_EXPIRE = 180;
    
    /**
     * 用户角色列表缓存过期时间 - 60分钟
     */
    public static final int USER_ROLE_LIST_EXPIRE = 60;
    
    /**
     * 用户角色-用户关系缓存过期时间 - 120分钟
     */
    public static final int USER_ROLE_USER_EXPIRE = 120;
    
    /**
     * 角色用户列表缓存过期时间 - 60分钟
     */
    public static final int ROLE_USERS_EXPIRE = 60;
    
    /**
     * 用户钱包详情缓存过期时间 - 30分钟
     */
    public static final int USER_WALLET_DETAIL_EXPIRE = 30;
    
    /**
     * 用户钱包列表缓存过期时间 - 20分钟
     */
    public static final int USER_WALLET_LIST_EXPIRE = 20;
    
    /**
     * 钱包余额缓存过期时间 - 10分钟（更新频繁）
     */
    public static final int WALLET_BALANCE_EXPIRE = 10;
    
    /**
     * 金币余额缓存过期时间 - 10分钟
     */
    public static final int WALLET_COIN_EXPIRE = 10;
    
    /**
     * 用户拉黑详情缓存过期时间 - 60分钟
     */
    public static final int USER_BLOCK_DETAIL_EXPIRE = 60;
    
    /**
     * 用户拉黑关系缓存过期时间 - 90分钟
     */
    public static final int USER_BLOCK_RELATION_EXPIRE = 90;
    
    /**
     * 用户拉黑列表缓存过期时间 - 30分钟
     */
    public static final int USER_BLOCK_LIST_EXPIRE = 30;

    // =================== 缓存时间单位 ===================
    
    /**
     * 默认时间单位 - 分钟
     */
    public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MINUTES;

    // =================== 缓存键模板 ===================
    
    /**
     * 用户核心详情缓存键模板
     */
    public static final String USER_CORE_DETAIL_KEY = "'user:core:detail:' + #userId";
    
    /**
     * 用户核心列表缓存键模板
     */
    public static final String USER_CORE_LIST_KEY = "'user:core:list:' + #request.currentPage + ':' + #request.pageSize + ':' + (#request.status ?: 'all')";
    
    /**
     * 用户名查询缓存键模板
     */
    public static final String USER_CORE_USERNAME_KEY = "'user:core:username:' + #username";
    
    /**
     * 邮箱查询缓存键模板
     */
    public static final String EMAIL_KEY = "'user:core:email:' + #email";
    
    /**
     * 手机号查询缓存键模板
     */
    public static final String PHONE_KEY = "'user:core:phone:' + #phone";
    
    /**
     * 用户资料详情缓存键模板
     */
    public static final String USER_PROFILE_DETAIL_KEY = "'user:profile:detail:' + #userId";
    
    /**
     * 用户资料列表缓存键模板
     */
    public static final String USER_PROFILE_LIST_KEY = "'user:profile:list:' + #request.currentPage + ':' + #request.pageSize";
    
    /**
     * 昵称查询缓存键模板
     */
    public static final String NICKNAME_KEY = "'user:profile:nickname:' + #nickname";
    
    /**
     * 用户统计详情缓存键模板
     */
    public static final String USER_STATS_DETAIL_KEY = "'user:stats:detail:' + #userId";
    
    /**
     * 用户统计列表缓存键模板
     */
    public static final String USER_STATS_LIST_KEY = "'user:stats:list:' + #request.currentPage + ':' + #request.pageSize";
    
    /**
     * 统计排行榜缓存键模板
     */
    public static final String STATS_RANKING_KEY = "'user:stats:ranking:' + #type + ':' + #limit";
    
    /**
     * 用户角色详情缓存键模板
     */
    public static final String USER_ROLE_DETAIL_KEY = "'user:role:detail:' + #id";
    
    /**
     * 用户角色列表缓存键模板
     */
    public static final String USER_ROLE_LIST_KEY = "'user:role:list:' + #request.currentPage + ':' + #request.pageSize";
    
    /**
     * 用户角色-用户关系缓存键模板
     */
    public static final String USER_ROLE_USER_KEY = "'user:role:user:' + #userId";
    
    /**
     * 角色用户列表缓存键模板
     */
    public static final String ROLE_USERS_KEY = "'user:role:users:' + #role + ':' + #includeExpired";
    
    /**
     * 用户钱包详情缓存键模板
     */
    public static final String USER_WALLET_DETAIL_KEY = "'user:wallet:detail:' + #userId";
    
    /**
     * 用户钱包列表缓存键模板
     */
    public static final String USER_WALLET_LIST_KEY = "'user:wallet:list:' + #request.currentPage + ':' + #request.pageSize";
    
    /**
     * 钱包余额缓存键模板
     */
    public static final String WALLET_BALANCE_KEY = "'user:wallet:balance:' + #userId";
    
    /**
     * 金币余额缓存键模板
     */
    public static final String WALLET_COIN_KEY = "'user:wallet:coin:' + #userId";
    
    /**
     * 用户拉黑详情缓存键模板
     */
    public static final String USER_BLOCK_DETAIL_KEY = "'user:block:detail:' + #id";
    
    /**
     * 用户拉黑关系缓存键模板
     */
    public static final String USER_BLOCK_RELATION_KEY = "'user:block:relation:' + #userId + ':' + #blockedUserId";
    
    /**
     * 用户拉黑列表缓存键模板
     */
    public static final String USER_BLOCK_LIST_KEY = "'user:block:list:' + #userId + ':' + #currentPage + ':' + #pageSize";
    
    /**
     * 用户被拉黑列表缓存键模板
     */
    public static final String USER_BLOCKED_LIST_KEY = "'user:blocked:list:' + #blockedUserId + ':' + #currentPage + ':' + #pageSize";
    
    /**
     * 用户拉黑查询缓存键模板
     */
    public static final String USER_BLOCK_QUERY_KEY = "'user:block:query:' + #request.currentPage + ':' + #request.pageSize";

    // =================== 缓存失效键模板 ===================
    
    /**
     * 用户核心相关缓存失效键模板
     */
    public static final String USER_CORE_INVALIDATE_KEY = "'user:core:*:' + #userId + '*'";
    
    /**
     * 用户资料相关缓存失效键模板
     */
    public static final String USER_PROFILE_INVALIDATE_KEY = "'user:profile:*:' + #userId + '*'";
    
    /**
     * 用户统计相关缓存失效键模板
     */
    public static final String USER_STATS_INVALIDATE_KEY = "'user:stats:*:' + #userId + '*'";
    
    /**
     * 用户角色相关缓存失效键模板
     */
    public static final String USER_ROLE_INVALIDATE_KEY = "'user:role:*:' + #userId + '*'";
    
    /**
     * 钱包相关缓存失效键模板
     */
    public static final String WALLET_INVALIDATE_KEY = "'user:wallet:*:' + #userId + '*'";
    
    /**
     * 用户拉黑相关缓存失效键模板
     */
    public static final String USER_BLOCK_INVALIDATE_KEY = "'user:block:*:' + #userId + '*'";
    
    /**
     * 全局用户缓存失效键模板（谨慎使用）
     */
    public static final String ALL_USER_INVALIDATE_KEY = "'user:*:' + #userId + '*'";

    private UserCacheConstant() {
        // 工具类，禁止实例化
    }
}
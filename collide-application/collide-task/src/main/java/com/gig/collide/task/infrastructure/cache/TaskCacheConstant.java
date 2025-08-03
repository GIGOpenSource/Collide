package com.gig.collide.task.infrastructure.cache;

import java.util.concurrent.TimeUnit;

/**
 * Task模块缓存常量配置 - 签到专用版
 * 基于简化的签到系统设计缓存策略
 * 
 * @author GIG Team
 * @version 1.0.0
 * @since 2024-01-16
 */
public class TaskCacheConstant {

    // =================== 任务模板缓存 ===================

    /**
     * 任务模板详情缓存
     */
    public static final String TASK_TEMPLATE_DETAIL_CACHE = "task:template:detail";

    /**
     * 任务模板详情缓存Key
     */
    public static final String TASK_TEMPLATE_DETAIL_KEY = "'task_template_' + #id";

    /**
     * 可用任务模板列表缓存
     */
    public static final String TASK_TEMPLATE_LIST_CACHE = "task:template:available";

    /**
     * 每日签到任务模板缓存
     */
    public static final String DAILY_CHECKIN_TEMPLATE_CACHE = "task:template:daily_checkin";

    // =================== 用户签到缓存 ===================

    /**
     * 用户今日签到状态缓存
     */
    public static final String USER_TODAY_CHECKIN_CACHE = "task:user:today_checkin";

    /**
     * 用户今日签到状态缓存Key
     */
    public static final String USER_TODAY_CHECKIN_KEY = "'user_checkin_' + #userId + '_' + T(java.time.LocalDate).now()";

    /**
     * 用户连续签到天数缓存
     */
    public static final String USER_CONTINUOUS_DAYS_CACHE = "task:user:continuous_days";

    /**
     * 用户连续签到天数缓存Key
     */
    public static final String USER_CONTINUOUS_DAYS_KEY = "'user_continuous_' + #userId";

    /**
     * 用户签到记录列表缓存
     */
    public static final String USER_CHECKIN_RECORDS_CACHE = "task:user:checkin_records";

    /**
     * 用户签到统计缓存
     */
    public static final String USER_CHECKIN_STATS_CACHE = "task:user:checkin_stats";

    /**
     * 用户签到统计缓存Key
     */
    public static final String USER_CHECKIN_STATS_KEY = "'user_stats_' + #userId";

    // =================== 缓存TTL配置 ===================

    /**
     * 任务模板缓存过期时间：30分钟
     */
    public static final int TASK_TEMPLATE_CACHE_EXPIRE = 30;

    /**
     * 用户签到状态缓存过期时间：60分钟
     */
    public static final int USER_CHECKIN_STATUS_EXPIRE = 60;

    /**
     * 用户签到记录缓存过期时间：10分钟
     */
    public static final int USER_CHECKIN_RECORDS_EXPIRE = 10;

    /**
     * 用户签到统计缓存过期时间：5分钟
     */
    public static final int USER_CHECKIN_STATS_EXPIRE = 5;
}
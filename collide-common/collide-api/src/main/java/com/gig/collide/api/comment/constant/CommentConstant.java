package com.gig.collide.api.comment.constant;

/**
 * 评论模块常量类
 * 定义评论相关的业务常量
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public final class CommentConstant {

    private CommentConstant() {
        // 工具类，不允许实例化
    }

    // ===================== 评论类型相关常量 =====================

    /** 内容评论类型 */
    public static final String COMMENT_TYPE_CONTENT = "CONTENT";
    
    /** 动态评论类型 */
    public static final String COMMENT_TYPE_DYNAMIC = "DYNAMIC";

    // ===================== 评论状态相关常量 =====================

    /** 正常状态 */
    public static final String COMMENT_STATUS_NORMAL = "NORMAL";
    
    /** 隐藏状态 */
    public static final String COMMENT_STATUS_HIDDEN = "HIDDEN";
    
    /** 已删除状态 */
    public static final String COMMENT_STATUS_DELETED = "DELETED";
    
    /** 待审核状态 */
    public static final String COMMENT_STATUS_PENDING = "PENDING";

    // ===================== 审核状态相关常量 =====================

    /** 审核通过 */
    public static final String AUDIT_STATUS_PASS = "PASS";
    
    /** 审核拒绝 */
    public static final String AUDIT_STATUS_REJECT = "REJECT";
    
    /** 待审核 */
    public static final String AUDIT_STATUS_PENDING = "PENDING";

    // ===================== 点赞类型相关常量 =====================

    /** 点赞类型 */
    public static final String LIKE_TYPE_LIKE = "LIKE";
    
    /** 点踩类型 */
    public static final String LIKE_TYPE_DISLIKE = "DISLIKE";

    // ===================== 举报类型相关常量 =====================

    /** 垃圾信息举报 */
    public static final String REPORT_TYPE_SPAM = "SPAM";
    
    /** 辱骂举报 */
    public static final String REPORT_TYPE_ABUSE = "ABUSE";
    
    /** 色情举报 */
    public static final String REPORT_TYPE_PORN = "PORN";
    
    /** 暴力举报 */
    public static final String REPORT_TYPE_VIOLENCE = "VIOLENCE";
    
    /** 其他举报 */
    public static final String REPORT_TYPE_OTHER = "OTHER";

    // ===================== 举报处理状态常量 =====================

    /** 待处理 */
    public static final String REPORT_STATUS_PENDING = "PENDING";
    
    /** 已处理 */
    public static final String REPORT_STATUS_PROCESSED = "PROCESSED";
    
    /** 已驳回 */
    public static final String REPORT_STATUS_REJECTED = "REJECTED";

    // ===================== 敏感词类型常量 =====================

    /** 政治敏感词 */
    public static final String SENSITIVE_WORD_TYPE_POLITICS = "POLITICS";
    
    /** 色情敏感词 */
    public static final String SENSITIVE_WORD_TYPE_PORN = "PORN";
    
    /** 暴力敏感词 */
    public static final String SENSITIVE_WORD_TYPE_VIOLENCE = "VIOLENCE";
    
    /** 辱骂敏感词 */
    public static final String SENSITIVE_WORD_TYPE_ABUSE = "ABUSE";

    // ===================== 敏感词处理动作常量 =====================

    /** 替换处理 */
    public static final String SENSITIVE_WORD_ACTION_REPLACE = "REPLACE";
    
    /** 拦截处理 */
    public static final String SENSITIVE_WORD_ACTION_BLOCK = "BLOCK";
    
    /** 审核处理 */
    public static final String SENSITIVE_WORD_ACTION_AUDIT = "AUDIT";

    // ===================== 操作类型常量 =====================

    /** 点赞操作 */
    public static final String OPERATION_LIKE = "like";
    
    /** 取消点赞操作 */
    public static final String OPERATION_UNLIKE = "unlike";
    
    /** 点踩操作 */
    public static final String OPERATION_DISLIKE = "dislike";
    
    /** 取消点踩操作 */
    public static final String OPERATION_UNDISLIKE = "undislike";
    
    /** 举报操作 */
    public static final String OPERATION_REPORT = "report";
    
    /** 置顶操作 */
    public static final String OPERATION_PIN = "pin";
    
    /** 取消置顶操作 */
    public static final String OPERATION_UNPIN = "unpin";

    // ===================== 排序相关常量 =====================

    /** 按创建时间排序 */
    public static final String SORT_BY_CREATE_TIME = "create_time";
    
    /** 按更新时间排序 */
    public static final String SORT_BY_UPDATE_TIME = "update_time";
    
    /** 按点赞数排序 */
    public static final String SORT_BY_LIKE_COUNT = "like_count";
    
    /** 按回复数排序 */
    public static final String SORT_BY_REPLY_COUNT = "reply_count";
    
    /** 按热度分数排序 */
    public static final String SORT_BY_HOT_SCORE = "hot_score";
    
    /** 按质量分数排序 */
    public static final String SORT_BY_QUALITY_SCORE = "quality_score";

    // ===================== 业务限制常量 =====================

    /** 评论内容最大长度 */
    public static final int MAX_CONTENT_LENGTH = 2000;
    
    /** 举报原因最大长度 */
    public static final int MAX_REPORT_REASON_LENGTH = 500;
    
    /** 举报详情最大长度 */
    public static final int MAX_REPORT_CONTENT_LENGTH = 2000;
    
    /** 审核原因最大长度 */
    public static final int MAX_AUDIT_REASON_LENGTH = 500;
    
    /** 评论图片最大数量 */
    public static final int MAX_COMMENT_IMAGES = 9;
    
    /** 提及用户最大数量 */
    public static final int MAX_MENTION_USERS = 10;

    // ===================== 默认值常量 =====================

    /** 默认页面大小 */
    public static final int DEFAULT_PAGE_SIZE = 20;
    
    /** 最大页面大小 */
    public static final int MAX_PAGE_SIZE = 100;
    
    /** 根评论父ID */
    public static final long ROOT_PARENT_ID = 0L;
    
    /** 默认质量分数 */
    public static final double DEFAULT_QUALITY_SCORE = 0.0;
    
    /** 最大质量分数 */
    public static final double MAX_QUALITY_SCORE = 5.0;

    // ===================== 层级相关常量 =====================

    /** 根评论层级 */
    public static final int ROOT_COMMENT_LEVEL = 0;
    
    /** 回复评论层级 */
    public static final int REPLY_COMMENT_LEVEL = 1;
    
    /** 最大回复层级 */
    public static final int MAX_REPLY_LEVEL = 2;

    // ===================== 缓存相关常量 =====================

    /** 评论缓存前缀 */
    public static final String CACHE_PREFIX_COMMENT = "comment:";
    
    /** 评论统计缓存前缀 */
    public static final String CACHE_PREFIX_COMMENT_STATS = "comment:stats:";
    
    /** 评论树缓存前缀 */
    public static final String CACHE_PREFIX_COMMENT_TREE = "comment:tree:";
    
    /** 评论缓存时间（秒） */
    public static final int CACHE_EXPIRE_SECONDS = 3600;

    // ===================== 热度计算权重常量 =====================

    /** 点赞权重 */
    public static final double HOT_SCORE_LIKE_WEIGHT = 2.0;
    
    /** 回复权重 */
    public static final double HOT_SCORE_REPLY_WEIGHT = 3.0;
    
    /** 置顶权重 */
    public static final double HOT_SCORE_PINNED_WEIGHT = 100.0;
    
    /** 热门权重 */
    public static final double HOT_SCORE_HOT_WEIGHT = 50.0;
    
    /** 精华权重 */
    public static final double HOT_SCORE_ESSENCE_WEIGHT = 30.0;
    
    /** 时间衰减权重 */
    public static final double HOT_SCORE_TIME_WEIGHT = -0.1;
} 
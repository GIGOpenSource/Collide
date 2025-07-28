package com.gig.collide.api.content.constant;

/**
 * 内容模块常量类
 * 定义内容相关的业务常量
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public final class ContentConstant {

    private ContentConstant() {
        // 工具类，不允许实例化
    }

    // ===================== 内容类型相关常量 =====================

    /** 小说类型 */
    public static final String CONTENT_TYPE_NOVEL = "NOVEL";
    
    /** 漫画类型 */
    public static final String CONTENT_TYPE_COMIC = "COMIC";
    
    /** 短视频类型 */
    public static final String CONTENT_TYPE_SHORT_VIDEO = "SHORT_VIDEO";
    
    /** 长视频类型 */
    public static final String CONTENT_TYPE_LONG_VIDEO = "LONG_VIDEO";
    
    /** 文章类型 */
    public static final String CONTENT_TYPE_ARTICLE = "ARTICLE";
    
    /** 音频类型 */
    public static final String CONTENT_TYPE_AUDIO = "AUDIO";

    // ===================== 内容状态相关常量 =====================

    /** 草稿状态 */
    public static final String CONTENT_STATUS_DRAFT = "DRAFT";
    
    /** 待审核状态 */
    public static final String CONTENT_STATUS_PENDING = "PENDING";
    
    /** 已发布状态 */
    public static final String CONTENT_STATUS_PUBLISHED = "PUBLISHED";
    
    /** 审核拒绝状态 */
    public static final String CONTENT_STATUS_REJECTED = "REJECTED";
    
    /** 下线状态 */
    public static final String CONTENT_STATUS_OFFLINE = "OFFLINE";

    // ===================== 审核状态相关常量 =====================

    /** 待审核 */
    public static final String REVIEW_STATUS_PENDING = "PENDING";
    
    /** 审核通过 */
    public static final String REVIEW_STATUS_APPROVED = "APPROVED";
    
    /** 审核拒绝 */
    public static final String REVIEW_STATUS_REJECTED = "REJECTED";

    // ===================== 分享平台相关常量 =====================

    /** 微信分享 */
    public static final String SHARE_PLATFORM_WECHAT = "WECHAT";
    
    /** 微博分享 */
    public static final String SHARE_PLATFORM_WEIBO = "WEIBO";
    
    /** QQ分享 */
    public static final String SHARE_PLATFORM_QQ = "QQ";
    
    /** 链接分享 */
    public static final String SHARE_PLATFORM_LINK = "LINK";

    // ===================== 统计操作类型常量 =====================

    /** 点赞操作 */
    public static final String OPERATION_LIKE = "like";
    
    /** 取消点赞操作 */
    public static final String OPERATION_UNLIKE = "unlike";
    
    /** 收藏操作 */
    public static final String OPERATION_FAVORITE = "favorite";
    
    /** 取消收藏操作 */
    public static final String OPERATION_UNFAVORITE = "unfavorite";
    
    /** 分享操作 */
    public static final String OPERATION_SHARE = "share";
    
    /** 浏览操作 */
    public static final String OPERATION_VIEW = "view";

    // ===================== 排序相关常量 =====================

    /** 按创建时间排序 */
    public static final String SORT_BY_CREATE_TIME = "create_time";
    
    /** 按发布时间排序 */
    public static final String SORT_BY_PUBLISH_TIME = "published_time";
    
    /** 按更新时间排序 */
    public static final String SORT_BY_UPDATE_TIME = "update_time";
    
    /** 按浏览量排序 */
    public static final String SORT_BY_VIEW_COUNT = "view_count";
    
    /** 按点赞数排序 */
    public static final String SORT_BY_LIKE_COUNT = "like_count";
    
    /** 按收藏数排序 */
    public static final String SORT_BY_FAVORITE_COUNT = "favorite_count";
    
    /** 按权重分数排序 */
    public static final String SORT_BY_WEIGHT_SCORE = "weight_score";

    // ===================== 业务限制常量 =====================

    /** 标题最大长度 */
    public static final int MAX_TITLE_LENGTH = 200;
    
    /** 描述最大长度 */
    public static final int MAX_DESCRIPTION_LENGTH = 1000;
    
    /** 标签最大数量 */
    public static final int MAX_TAG_COUNT = 10;
    
    /** 标签名称最大长度 */
    public static final int MAX_TAG_NAME_LENGTH = 20;
    
    /** 分享文案最大长度 */
    public static final int MAX_SHARE_TEXT_LENGTH = 500;
    
    /** 审核意见最大长度 */
    public static final int MAX_REVIEW_COMMENT_LENGTH = 1000;

    // ===================== 默认值常量 =====================

    /** 默认页面大小 */
    public static final int DEFAULT_PAGE_SIZE = 20;
    
    /** 最大页面大小 */
    public static final int MAX_PAGE_SIZE = 100;
    
    /** 默认权重分数 */
    public static final double DEFAULT_WEIGHT_SCORE = 0.0;

    // ===================== 功能开关常量 =====================

    /** 允许评论 */
    public static final boolean ALLOW_COMMENT_DEFAULT = true;
    
    /** 允许分享 */
    public static final boolean ALLOW_SHARE_DEFAULT = true;
    
    /** 推荐内容标记 */
    public static final boolean IS_RECOMMENDED_DEFAULT = false;
    
    /** 置顶内容标记 */
    public static final boolean IS_PINNED_DEFAULT = false;
}
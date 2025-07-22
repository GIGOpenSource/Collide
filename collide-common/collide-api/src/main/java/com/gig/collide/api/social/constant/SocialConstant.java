package com.gig.collide.api.social.constant;

/**
 * 社交相关常量
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public class SocialConstant {

    /**
     * 动态类型常量
     */
    public static class PostType {
        /** 原创动态 */
        public static final String ORIGINAL = "original";
        /** 转发动态 */
        public static final String REPOST = "repost";
        /** 回复动态 */
        public static final String REPLY = "reply";
    }

    /**
     * 可见性常量
     */
    public static class Visibility {
        /** 公开 */
        public static final String PUBLIC = "public";
        /** 仅关注者可见 */
        public static final String FOLLOWERS_ONLY = "followers_only";
        /** 仅自己可见 */
        public static final String PRIVATE = "private";
        /** 部分可见 */
        public static final String PARTIAL = "partial";
    }

    /**
     * 互动类型常量
     */
    public static class InteractionType {
        /** 点赞 */
        public static final String LIKE = "like";
        /** 评论 */
        public static final String COMMENT = "comment";
        /** 转发 */
        public static final String REPOST = "repost";
        /** 收藏 */
        public static final String FAVORITE = "favorite";
        /** 分享 */
        public static final String SHARE = "share";
    }

    /**
     * 通知类型常量
     */
    public static class NotificationType {
        /** 被点赞 */
        public static final String LIKED = "liked";
        /** 被评论 */
        public static final String COMMENTED = "commented";
        /** 被转发 */
        public static final String REPOSTED = "reposted";
        /** 被关注 */
        public static final String FOLLOWED = "followed";
        /** 被@提及 */
        public static final String MENTIONED = "mentioned";
        /** 系统通知 */
        public static final String SYSTEM = "system";
    }

    /**
     * 时间线类型常量
     */
    public static class TimelineType {
        /** 主时间线 */
        public static final String HOME = "home";
        /** 关注时间线 */
        public static final String FOLLOWING = "following";
        /** 推荐时间线 */
        public static final String RECOMMENDED = "recommended";
        /** 热门时间线 */
        public static final String TRENDING = "trending";
        /** 个人时间线 */
        public static final String PERSONAL = "personal";
    }

    /**
     * 关注状态常量
     */
    public static class FollowStatus {
        /** 未关注 */
        public static final String NOT_FOLLOWING = "not_following";
        /** 已关注 */
        public static final String FOLLOWING = "following";
        /** 互相关注 */
        public static final String MUTUAL = "mutual";
        /** 被拉黑 */
        public static final String BLOCKED = "blocked";
    }
} 
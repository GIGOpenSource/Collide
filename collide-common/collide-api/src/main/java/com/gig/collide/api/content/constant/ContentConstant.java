package com.gig.collide.api.content.constant;

/**
 * 内容相关常量
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public class ContentConstant {

    /**
     * 内容类型常量
     */
    public static class Type {
        /** 文本内容 */
        public static final String TEXT = "text";
        /** 图片内容 */
        public static final String IMAGE = "image";
        /** 视频内容 */
        public static final String VIDEO = "video";
        /** 音频内容 */
        public static final String AUDIO = "audio";
        /** 链接分享 */
        public static final String LINK = "link";
        /** 混合内容 */
        public static final String MIXED = "mixed";
    }

    /**
     * 内容状态常量
     */
    public static class Status {
        /** 草稿 */
        public static final String DRAFT = "draft";
        /** 待审核 */
        public static final String PENDING = "pending";
        /** 已发布 */
        public static final String PUBLISHED = "published";
        /** 审核拒绝 */
        public static final String REJECTED = "rejected";
        /** 已下架 */
        public static final String REMOVED = "removed";
        /** 已删除 */
        public static final String DELETED = "deleted";
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
        /** 付费可见 */
        public static final String PAID = "paid";
    }

    /**
     * 审核相关常量
     */
    public static class Audit {
        /** 自动审核通过 */
        public static final String AUTO_APPROVED = "auto_approved";
        /** 人工审核通过 */
        public static final String MANUAL_APPROVED = "manual_approved";
        /** 审核拒绝 - 违规内容 */
        public static final String REJECTED_VIOLATION = "rejected_violation";
        /** 审核拒绝 - 低质量内容 */
        public static final String REJECTED_LOW_QUALITY = "rejected_low_quality";
    }

    /**
     * 文件上传限制
     */
    public static class FileLimit {
        /** 图片最大大小（MB） */
        public static final int MAX_IMAGE_SIZE_MB = 10;
        /** 视频最大大小（MB） */
        public static final int MAX_VIDEO_SIZE_MB = 100;
        /** 音频最大大小（MB） */
        public static final int MAX_AUDIO_SIZE_MB = 50;
        
        /** 支持的图片格式 */
        public static final String[] SUPPORTED_IMAGE_FORMATS = {"jpg", "jpeg", "png", "gif", "webp"};
        /** 支持的视频格式 */
        public static final String[] SUPPORTED_VIDEO_FORMATS = {"mp4", "avi", "mov", "wmv", "flv"};
        /** 支持的音频格式 */
        public static final String[] SUPPORTED_AUDIO_FORMATS = {"mp3", "wav", "aac", "ogg"};
    }
} 
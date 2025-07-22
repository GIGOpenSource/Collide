package com.gig.collide.base.exception;

/**
 * Content 模块错误码定义
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public enum ContentErrorCode implements ErrorCode {

    // ========== 内容基础错误码 ==========
    CONTENT_NOT_FOUND("CONTENT_001", "内容不存在"),
    CONTENT_ACCESS_DENIED("CONTENT_002", "无访问权限"),
    CONTENT_EDIT_NOT_ALLOWED("CONTENT_003", "内容不可编辑"),
    CONTENT_PUBLISH_NOT_ALLOWED("CONTENT_004", "内容不可发布"),
    CONTENT_ALREADY_PUBLISHED("CONTENT_005", "内容已发布"),
    CONTENT_DELETE_NOT_ALLOWED("CONTENT_006", "内容不可删除"),

    // ========== 内容参数错误码 ==========
    CONTENT_TITLE_REQUIRED("CONTENT_101", "标题不能为空"),
    CONTENT_TITLE_TOO_LONG("CONTENT_102", "标题过长"),
    CONTENT_TYPE_INVALID("CONTENT_103", "内容类型无效"),
    CONTENT_CATEGORY_INVALID("CONTENT_104", "分类无效"),
    CONTENT_TAG_TOO_MANY("CONTENT_105", "标签过多"),
    CONTENT_DATA_INVALID("CONTENT_106", "内容数据格式无效"),
    CONTENT_COVER_INVALID("CONTENT_107", "封面图片无效"),

    // ========== 内容状态错误码 ==========
    CONTENT_STATUS_INVALID("CONTENT_201", "内容状态无效"),
    CONTENT_REVIEW_STATUS_INVALID("CONTENT_202", "审核状态无效"),
    CONTENT_ALREADY_REVIEWED("CONTENT_203", "内容已审核"),
    CONTENT_REVIEW_FAILED("CONTENT_204", "内容审核失败"),
    CONTENT_NOT_IN_DRAFT("CONTENT_205", "内容不是草稿状态"),
    CONTENT_NOT_PUBLISHED("CONTENT_206", "内容未发布"),

    // ========== 内容交互错误码 ==========
    CONTENT_ALREADY_LIKED("CONTENT_301", "已经点赞"),
    CONTENT_ALREADY_FAVORITED("CONTENT_302", "已经收藏"),
    CONTENT_LIKE_FAILED("CONTENT_303", "点赞失败"),
    CONTENT_FAVORITE_FAILED("CONTENT_304", "收藏失败"),
    CONTENT_SHARE_NOT_ALLOWED("CONTENT_305", "不允许分享"),
    CONTENT_COMMENT_NOT_ALLOWED("CONTENT_306", "不允许评论"),
    CONTENT_INTERACTION_FAILED("CONTENT_307", "互动操作失败"),

    // ========== 内容权限错误码 ==========
    CONTENT_AUTHOR_MISMATCH("CONTENT_401", "作者不匹配"),
    CONTENT_PERMISSION_DENIED("CONTENT_402", "无操作权限"),
    CONTENT_REVIEW_PERMISSION_DENIED("CONTENT_403", "无审核权限"),
    CONTENT_MANAGE_PERMISSION_DENIED("CONTENT_404", "无管理权限"),

    // ========== 内容业务错误码 ==========
    CONTENT_CREATE_FAILED("CONTENT_501", "内容创建失败"),
    CONTENT_UPDATE_FAILED("CONTENT_502", "内容更新失败"),
    CONTENT_DELETE_FAILED("CONTENT_503", "内容删除失败"),
    CONTENT_PUBLISH_FAILED("CONTENT_504", "内容发布失败"),
    CONTENT_QUERY_FAILED("CONTENT_505", "内容查询失败"),
    CONTENT_STATISTICS_FAILED("CONTENT_506", "统计查询失败"),

    // ========== 系统错误码 ==========
    CONTENT_SYSTEM_ERROR("CONTENT_999", "系统内部错误");

    private final String code;
    private final String message;

    ContentErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", code, message);
    }
} 
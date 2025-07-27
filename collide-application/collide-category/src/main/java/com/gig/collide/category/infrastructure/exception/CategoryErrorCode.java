package com.gig.collide.category.infrastructure.exception;

import com.gig.collide.base.exception.ErrorCode;

/**
 * 分类模块错误码定义
 * 基于Code项目标准化思想，定义分类模块专用错误码
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public enum CategoryErrorCode implements ErrorCode {

    // ========== 分类基础错误码 ==========
    CATEGORY_NOT_FOUND("CATEGORY_NOT_FOUND", "分类不存在"),
    CATEGORY_NAME_EXISTS("CATEGORY_NAME_EXISTS", "该层级下分类名称已存在"),
    CATEGORY_HAS_CHILDREN("CATEGORY_HAS_CHILDREN", "无法删除包含子分类的分类"),
    CATEGORY_HAS_CONTENT("CATEGORY_HAS_CONTENT", "无法删除包含内容的分类"),
    CATEGORY_DELETE_FAILED("CATEGORY_DELETE_FAILED", "删除分类失败"),
    CATEGORY_UPDATE_CONFLICT("CATEGORY_UPDATE_CONFLICT", "分类更新冲突，请重试"),

    // ========== 分类参数错误码 ==========
    CATEGORY_NAME_EMPTY("CATEGORY_NAME_EMPTY", "分类名称不能为空"),
    CATEGORY_NAME_TOO_LONG("CATEGORY_NAME_TOO_LONG", "分类名称不能超过50个字符"),
    CATEGORY_DESCRIPTION_TOO_LONG("CATEGORY_DESCRIPTION_TOO_LONG", "分类描述不能超过500个字符"),
    CATEGORY_PARENT_NOT_EXISTS("CATEGORY_PARENT_NOT_EXISTS", "父分类不存在"),
    CATEGORY_LEVEL_EXCEED("CATEGORY_LEVEL_EXCEED", "分类层级不能超过5层"),
    CATEGORY_CIRCULAR_REFERENCE("CATEGORY_CIRCULAR_REFERENCE", "不能将分类移动到自己的子分类下"),
    CATEGORY_STATUS_INVALID("CATEGORY_STATUS_INVALID", "分类状态无效"),

    // ========== 通用错误码 ==========
    INVALID_PARAMETER("INVALID_PARAMETER", "参数无效"),
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", "资源不存在"),
    OPERATION_FAILED("OPERATION_FAILED", "操作失败"),
    SYSTEM_ERROR("SYSTEM_ERROR", "系统内部错误");

    private final String code;
    private final String message;

    CategoryErrorCode(String code, String message) {
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
} 
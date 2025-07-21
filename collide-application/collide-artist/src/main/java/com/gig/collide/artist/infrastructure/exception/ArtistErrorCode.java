package com.gig.collide.artist.infrastructure.exception;

import com.gig.collide.base.exception.ErrorCode;

/**
 * 博主模块错误码
 * @author GIG
 */
public enum ArtistErrorCode implements ErrorCode {

    /**
     * 博主不存在
     */
    ARTIST_NOT_EXIST("ARTIST_NOT_EXIST", "博主不存在"),

    /**
     * 用户已经是博主
     */
    USER_ALREADY_ARTIST("USER_ALREADY_ARTIST", "用户已经是博主"),

    /**
     * 博主名称已存在
     */
    ARTIST_NAME_EXISTS("ARTIST_NAME_EXISTS", "博主名称已存在"),

    /**
     * 博主状态不允许操作
     */
    ARTIST_STATUS_NOT_ALLOWED("ARTIST_STATUS_NOT_ALLOWED", "博主状态不允许该操作"),

    /**
     * 申请不存在
     */
    APPLICATION_NOT_EXIST("APPLICATION_NOT_EXIST", "申请不存在"),

    /**
     * 申请状态不允许操作
     */
    APPLICATION_STATUS_NOT_ALLOWED("APPLICATION_STATUS_NOT_ALLOWED", "申请状态不允许该操作"),

    /**
     * 用户有进行中的申请
     */
    USER_HAS_ONGOING_APPLICATION("USER_HAS_ONGOING_APPLICATION", "用户有进行中的申请"),

    /**
     * 申请材料不完整
     */
    APPLICATION_INCOMPLETE("APPLICATION_INCOMPLETE", "申请材料不完整"),

    /**
     * 审核权限不足
     */
    REVIEW_PERMISSION_DENIED("REVIEW_PERMISSION_DENIED", "审核权限不足"),

    /**
     * 博主操作失败
     */
    ARTIST_OPERATION_FAILED("ARTIST_OPERATION_FAILED", "博主操作失败"),

    /**
     * 申请操作失败
     */
    APPLICATION_OPERATION_FAILED("APPLICATION_OPERATION_FAILED", "申请操作失败"),

    /**
     * 统计数据更新失败
     */
    STATISTICS_UPDATE_FAILED("STATISTICS_UPDATE_FAILED", "统计数据更新失败"),

    /**
     * 等级计算失败
     */
    LEVEL_CALCULATION_FAILED("LEVEL_CALCULATION_FAILED", "等级计算失败"),

    /**
     * 认证操作失败
     */
    VERIFICATION_OPERATION_FAILED("VERIFICATION_OPERATION_FAILED", "认证操作失败");

    private final String code;
    private final String message;

    ArtistErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
} 
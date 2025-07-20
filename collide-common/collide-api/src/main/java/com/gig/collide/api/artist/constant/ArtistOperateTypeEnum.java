package com.gig.collide.api.artist.constant;

/**
 * 博主操作类型枚举
 * @author GIG
 */
public enum ArtistOperateTypeEnum {

    /**
     * 申请成为博主
     */
    APPLY,

    /**
     * 补充申请材料
     */
    SUPPLEMENT,

    /**
     * 撤回申请
     */
    WITHDRAW,

    /**
     * 重新申请
     */
    REAPPLY,

    /**
     * 审核操作
     */
    REVIEW,

    /**
     * 激活博主
     */
    ACTIVATE,

    /**
     * 暂停博主
     */
    SUSPEND,

    /**
     * 禁用博主
     */
    DISABLE,

    /**
     * 恢复博主
     */
    RESTORE,

    /**
     * 注销博主
     */
    CANCEL,

    /**
     * 更新博主信息
     */
    UPDATE_INFO,

    /**
     * 更新认证信息
     */
    UPDATE_VERIFICATION,

    /**
     * 重置博主状态
     */
    RESET_STATUS;
} 
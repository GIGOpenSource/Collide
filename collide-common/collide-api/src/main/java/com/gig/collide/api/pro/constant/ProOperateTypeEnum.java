package com.gig.collide.api.pro.constant;

/**
 * Pro用户操作类型枚举
 * @author GIG
 */
public enum ProOperateTypeEnum {

    /**
     * 升级到付费用户
     */
    UPGRADE_TO_PRO,

    /**
     * 降级到普通用户
     */
    DOWNGRADE,

    /**
     * 付费续费
     */
    RENEW_PRO,

    /**
     * 暂停付费
     */
    SUSPEND_PRO,

    /**
     * 恢复付费
     */
    RESUME_PRO,

    /**
     * 激活付费权限
     */
    ACTIVATE_PERMISSION,

    /**
     * 停用付费权限
     */
    DEACTIVATE_PERMISSION,

    /**
     * 权限配置变更
     */
    PERMISSION_CONFIG_CHANGE,

    /**
     * 自动权限开通
     */
    AUTO_PERMISSION_GRANT;
} 
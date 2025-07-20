package com.gig.collide.api.pro.constant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Pro权限配置
 * @author GIG
 */
@Getter
@Setter
@NoArgsConstructor
public class ProPermissionConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 配置版本
     */
    private String version;

    /**
     * 配置名称
     */
    private String configName;

    /**
     * 配置描述
     */
    private String description;

    /**
     * 套餐权限配置映射
     * key: 套餐类型, value: 权限配置列表
     */
    private Map<ProPackageType, List<PermissionConfigItem>> packagePermissions;

    /**
     * 默认权限配置（用户升级时自动开通）
     */
    private List<ProPermissionType> defaultPermissions;

    /**
     * 是否启用
     */
    private Boolean enabled = true;

    /**
     * 权限配置项
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class PermissionConfigItem implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 权限类型
         */
        private ProPermissionType permissionType;

        /**
         * 是否默认开通
         */
        private Boolean defaultEnabled = false;

        /**
         * 使用次数限制（-1表示无限制）
         */
        private Integer usageLimit = -1;

        /**
         * 有效期天数（-1表示跟随套餐）
         */
        private Integer validDays = -1;

        /**
         * 权限优先级
         */
        private Integer priority = 0;

        /**
         * 扩展配置
         */
        private Map<String, Object> extConfig;
    }
} 
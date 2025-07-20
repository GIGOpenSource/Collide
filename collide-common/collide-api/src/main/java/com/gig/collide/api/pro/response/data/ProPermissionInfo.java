package com.gig.collide.api.pro.response.data;

import com.gig.collide.api.pro.constant.ProPermissionType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Pro权限信息
 * @author GIG
 */
@Getter
@Setter
@NoArgsConstructor
public class ProPermissionInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 已激活的权限列表
     */
    private List<ProPermissionType> activePermissions;

    /**
     * 可用的权限列表（根据套餐配置）
     */
    private List<ProPermissionType> availablePermissions;

    /**
     * 权限详情列表
     */
    private List<ProPermissionDetail> permissionDetails;

    /**
     * 权限配置版本
     */
    private String configVersion;

    /**
     * 最后更新时间
     */
    private Date lastUpdated;

    /**
     * Pro权限详情
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ProPermissionDetail implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 权限类型
         */
        private ProPermissionType permissionType;

        /**
         * 权限名称
         */
        private String permissionName;

        /**
         * 权限描述
         */
        private String permissionDesc;

        /**
         * 是否激活
         */
        private Boolean isActive;

        /**
         * 激活时间
         */
        private Date activatedTime;

        /**
         * 到期时间
         */
        private Date expiredTime;

        /**
         * 使用次数限制
         */
        private Integer usageLimit;

        /**
         * 已使用次数
         */
        private Integer usedCount;
    }
} 
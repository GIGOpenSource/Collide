package com.gig.collide.api.pro.service;

import com.gig.collide.api.pro.constant.ProPackageType;
import com.gig.collide.api.pro.constant.ProPermissionConfig;
import com.gig.collide.api.pro.constant.ProPermissionType;

import java.util.List;

/**
 * Pro权限配置服务接口（内部服务，不对外暴露）
 * @author GIG
 */
public interface ProPermissionConfigService {

    /**
     * 获取套餐默认权限配置
     * @param packageType 套餐类型
     * @param configVersion 配置版本
     * @return 默认权限列表
     */
    List<ProPermissionType> getDefaultPermissions(ProPackageType packageType, String configVersion);

    /**
     * 获取套餐所有可用权限配置
     * @param packageType 套餐类型
     * @param configVersion 配置版本
     * @return 可用权限列表
     */
    List<ProPermissionType> getAvailablePermissions(ProPackageType packageType, String configVersion);

    /**
     * 获取权限配置详情
     * @param packageType 套餐类型
     * @param permissionType 权限类型
     * @param configVersion 配置版本
     * @return 权限配置项
     */
    ProPermissionConfig.PermissionConfigItem getPermissionConfig(
            ProPackageType packageType, 
            ProPermissionType permissionType, 
            String configVersion);

    /**
     * 获取当前生效的配置版本
     * @return 配置版本
     */
    String getCurrentConfigVersion();

    /**
     * 用户升级时自动开通权限
     * @param userId 用户ID
     * @param packageType 套餐类型
     * @param configVersion 配置版本
     * @return 开通的权限列表
     */
    List<ProPermissionType> autoGrantPermissionsOnUpgrade(Long userId, ProPackageType packageType, String configVersion);

    /**
     * 用户降级时自动回收权限
     * @param userId 用户ID
     * @return 回收的权限列表
     */
    List<ProPermissionType> autoRevokePermissionsOnDowngrade(Long userId);

    /**
     * 检查用户是否有指定权限
     * @param userId 用户ID
     * @param permissionType 权限类型
     * @return 是否有权限
     */
    boolean hasPermission(Long userId, ProPermissionType permissionType);

    /**
     * 验证权限使用（如有使用次数限制）
     * @param userId 用户ID
     * @param permissionType 权限类型
     * @return 是否可以使用
     */
    boolean validatePermissionUsage(Long userId, ProPermissionType permissionType);

    /**
     * 记录权限使用
     * @param userId 用户ID
     * @param permissionType 权限类型
     */
    void recordPermissionUsage(Long userId, ProPermissionType permissionType);
} 
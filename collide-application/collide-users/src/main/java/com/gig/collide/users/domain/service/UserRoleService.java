package com.gig.collide.users.domain.service;

import com.gig.collide.base.response.PageResponse;
import com.gig.collide.users.domain.entity.UserRole;
import com.gig.collide.api.user.request.users.role.UserRoleQueryRequest;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户角色领域服务接口 - 对应 t_user_role 表
 * 负责用户角色权限管理
 * 
 * @author GIG Team
 * @version 2.0.0
 */
public interface UserRoleService {

    /**
     * 创建用户角色
     */
    UserRole createRole(UserRole userRole);

    /**
     * 更新用户角色
     */
    UserRole updateRole(UserRole userRole);

    /**
     * 根据ID查询用户角色
     */
    UserRole getRoleById(Integer id);

    /**
     * 根据用户ID查询有效角色
     */
    List<UserRole> getActiveRolesByUserId(Long userId);

    /**
     * 根据用户ID查询所有角色（包括过期）
     */
    List<UserRole> getAllRolesByUserId(Long userId);

    /**
     * 根据角色类型查询用户列表
     */
    List<UserRole> getUsersByRole(String role, Boolean includeExpired, int page, int size);

    /**
     * 检查用户是否拥有指定角色
     */
    boolean checkUserHasRole(Long userId, String role);

    /**
     * 检查用户是否拥有有效的指定角色
     */
    boolean checkUserHasActiveRole(Long userId, String role);

    /**
     * 分配角色给用户
     */
    UserRole assignRoleToUser(Long userId, String role, LocalDateTime expireTime);

    /**
     * 撤销用户角色
     */
    boolean revokeUserRole(Long userId, String role);

    /**
     * 撤销用户所有角色
     */
    boolean revokeAllUserRoles(Long userId);

    /**
     * 更新角色过期时间
     */
    boolean updateExpireTime(Integer id, LocalDateTime expireTime);

    /**
     * 延长角色有效期
     */
    boolean extendExpireTime(Integer id, Integer days);

    /**
     * 设置角色为永久有效
     */
    boolean setPermanentRole(Integer id);

    /**
     * 查询即将过期的角色
     */
    List<UserRole> getExpiringRoles(Integer days);

    /**
     * 查询已过期的角色
     */
    List<UserRole> getExpiredRoles();

    /**
     * 批量更新过期角色状态
     */
    boolean batchUpdateExpiredRoles();

    /**
     * 分页查询用户角色
     */
    PageResponse<UserRole> queryRoles(UserRoleQueryRequest request);

    /**
     * 检查用户是否为管理员
     */
    boolean isAdmin(Long userId);

    /**
     * 检查用户是否为VIP
     */
    boolean isVip(Long userId);

    /**
     * 检查用户是否为博主
     */
    boolean isBlogger(Long userId);

    /**
     * 获取用户最高权限角色
     */
    UserRole getHighestRole(Long userId);

    /**
     * 批量分配角色
     */
    List<UserRole> batchAssignRoles(List<Long> userIds, String role, LocalDateTime expireTime);

    /**
     * 批量撤销角色
     */
    boolean batchRevokeRoles(List<Long> userIds, String role);

    /**
     * 删除用户角色
     */
    boolean deleteRole(Integer id);

    /**
     * 删除用户所有角色
     */
    boolean deleteAllUserRoles(Long userId);

    /**
     * 初始化用户默认角色
     */
    UserRole initializeDefaultRole(Long userId);
}
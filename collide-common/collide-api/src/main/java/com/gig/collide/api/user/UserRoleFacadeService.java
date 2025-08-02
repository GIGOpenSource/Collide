package com.gig.collide.api.user;

import com.gig.collide.api.user.request.users.role.UserRoleCreateRequest;
import com.gig.collide.api.user.request.users.role.UserRoleUpdateRequest;
import com.gig.collide.api.user.request.users.role.UserRoleQueryRequest;
import com.gig.collide.api.user.response.users.role.UserRoleResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import java.time.LocalDateTime;

/**
 * 用户角色服务接口 - 对应 t_user_role 表
 * 负责用户角色管理功能
 * 
 * @author GIG Team
 * @version 2.0.0
 */
public interface UserRoleFacadeService {

    /**
     * 为用户分配角色
     * 
     * @param request 用户角色创建请求
     * @return 创建结果
     */
    Result<UserRoleResponse> assignRole(UserRoleCreateRequest request);

    /**
     * 更新用户角色信息
     * 
     * @param request 用户角色更新请求
     * @return 更新结果
     */
    Result<UserRoleResponse> updateRole(UserRoleUpdateRequest request);

    /**
     * 根据用户ID查询所有角色
     * 
     * @param userId 用户ID
     * @return 用户角色列表
     */
    Result<java.util.List<UserRoleResponse>> getRolesByUserId(Long userId);

    /**
     * 根据角色查询用户列表
     * 
     * @param role 角色：user、blogger、admin、vip
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 用户角色列表
     */
    Result<PageResponse<UserRoleResponse>> getUsersByRole(String role, Integer currentPage, Integer pageSize);

    /**
     * 检查用户是否具有指定角色
     * 
     * @param userId 用户ID
     * @param role 角色
     * @return 是否具有角色
     */
    Result<Boolean> hasRole(Long userId, String role);

    /**
     * 检查用户角色是否有效（未过期）
     * 
     * @param userId 用户ID
     * @param role 角色
     * @return 是否有效
     */
    Result<Boolean> isRoleValid(Long userId, String role);

    /**
     * 获取用户的有效角色列表
     * 
     * @param userId 用户ID
     * @return 有效角色列表
     */
    Result<java.util.List<String>> getValidRoles(Long userId);

    /**
     * 获取用户的最高权限角色
     * 
     * @param userId 用户ID
     * @return 最高权限角色
     */
    Result<String> getHighestRole(Long userId);

    /**
     * 撤销用户角色
     * 
     * @param userId 用户ID
     * @param role 角色
     * @return 撤销结果
     */
    Result<Void> revokeRole(Long userId, String role);

    /**
     * 批量撤销用户的所有角色
     * 
     * @param userId 用户ID
     * @return 撤销结果
     */
    Result<Void> revokeAllRoles(Long userId);

    /**
     * 为用户分配VIP角色
     * 
     * @param userId 用户ID
     * @param expireTime VIP过期时间
     * @return 分配结果
     */
    Result<UserRoleResponse> assignVipRole(Long userId, LocalDateTime expireTime);

    /**
     * 续费VIP角色
     * 
     * @param userId 用户ID
     * @param extendDays 延长天数
     * @return 续费结果
     */
    Result<UserRoleResponse> renewVipRole(Long userId, Integer extendDays);

    /**
     * 检查VIP角色是否过期
     * 
     * @param userId 用户ID
     * @return 是否过期
     */
    Result<Boolean> isVipExpired(Long userId);

    /**
     * 获取即将过期的VIP用户列表
     * 
     * @param days 提前多少天
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return VIP用户列表
     */
    Result<PageResponse<UserRoleResponse>> getExpiringVipUsers(Integer days, Integer currentPage, Integer pageSize);

    /**
     * 清理过期角色
     * 
     * @return 清理数量
     */
    Result<Integer> cleanExpiredRoles();

    /**
     * 分页查询用户角色
     * 
     * @param request 查询请求
     * @return 分页角色列表
     */
    Result<PageResponse<UserRoleResponse>> queryRoles(UserRoleQueryRequest request);

    /**
     * 获取角色统计信息
     * 
     * @return 角色统计数据
     */
    Result<java.util.Map<String, Object>> getRoleStatistics();

    /**
     * 批量分配角色
     * 
     * @param userIds 用户ID列表
     * @param role 角色
     * @param expireTime 过期时间（可选）
     * @return 分配结果
     */
    Result<Integer> batchAssignRole(java.util.List<Long> userIds, String role, LocalDateTime expireTime);

    /**
     * 批量撤销角色
     * 
     * @param userIds 用户ID列表
     * @param role 角色
     * @return 撤销结果
     */
    Result<Integer> batchRevokeRole(java.util.List<Long> userIds, String role);
}
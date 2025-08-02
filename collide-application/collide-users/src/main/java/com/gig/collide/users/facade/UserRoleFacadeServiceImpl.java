package com.gig.collide.users.facade;

import com.gig.collide.api.user.UserRoleFacadeService;
import com.gig.collide.api.user.request.role.UserRoleCreateRequest;
import com.gig.collide.api.user.request.role.UserRoleQueryRequest;
import com.gig.collide.api.user.request.role.UserRoleUpdateRequest;
import com.gig.collide.api.user.response.role.UserRoleResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.users.domain.entity.UserRole;
import com.gig.collide.users.domain.service.UserRoleService;
import com.gig.collide.users.infrastructure.cache.UserCacheConstant;
import com.gig.collide.web.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.apache.dubbo.config.annotation.DubboService;
import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheUpdate;
import com.alicp.jetcache.anno.CacheType;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户角色门面服务实现 - 对应 t_user_role 表
 * Dubbo独立微服务提供者 - 负责用户角色权限管理
 * 
 * @author GIG Team
 * @version 2.0.0 (Dubbo微服务版 - 6表架构)
 */
@Slf4j
@DubboService(version = "1.0.0", interfaceClass = UserRoleFacadeService.class)
@RequiredArgsConstructor
public class UserRoleFacadeServiceImpl implements UserRoleFacadeService {

    private final UserRoleService userRoleService;

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_ROLE_LIST_CACHE)
    public Result<UserRoleResponse> assignRole(UserRoleCreateRequest request) {
        try {
            log.info("分配用户角色请求: userId={}, role={}", request.getUserId(), request.getRole());
            
            UserRole savedRole = userRoleService.assignRoleToUser(
                request.getUserId(), 
                request.getRole(), 
                request.getExpireTime()
            );
            UserRoleResponse response = convertToResponse(savedRole);
            
            log.info("用户角色分配成功: userId={}, role={}", savedRole.getUserId(), savedRole.getRole());
            return Result.success(response);
        } catch (Exception e) {
            log.error("分配用户角色失败", e);
            return Result.error("ROLE_ASSIGN_ERROR", "分配用户角色失败: " + e.getMessage());
        }
    }

    @Override
    @CacheUpdate(name = UserCacheConstant.USER_ROLE_DETAIL_CACHE,
                 key = UserCacheConstant.USER_ROLE_DETAIL_KEY,
                 value = "#result.data")
    @CacheInvalidate(name = UserCacheConstant.USER_ROLE_LIST_CACHE)
    public Result<UserRoleResponse> updateRole(UserRoleUpdateRequest request) {
        try {
            log.info("更新用户角色请求: id={}", request.getId());
            
            UserRole userRole = userRoleService.getRoleById(request.getId());
            if (userRole == null) {
                return Result.error("ROLE_NOT_FOUND", "用户角色不存在");
            }
            
            BeanUtils.copyProperties(request, userRole);
            UserRole updatedRole = userRoleService.updateRole(userRole);
            UserRoleResponse response = convertToResponse(updatedRole);
            
            log.info("用户角色更新成功: id={}", updatedRole.getId());
            return Result.success(response);
        } catch (Exception e) {
            log.error("更新用户角色失败", e);
            return Result.error("ROLE_UPDATE_ERROR", "更新用户角色失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = UserCacheConstant.USER_ROLE_USER_CACHE,
            key = UserCacheConstant.USER_ROLE_USER_KEY,
            expire = UserCacheConstant.USER_ROLE_USER_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<List<UserRoleResponse>> getRolesByUserId(Long userId) {
        try {
            List<UserRole> roles = userRoleService.getAllRolesByUserId(userId);
            List<UserRoleResponse> responses = roles.stream()
                    .map(this::convertToResponse)
                    .toList();
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("查询用户所有角色失败", e);
            return Result.error("GET_ROLES_BY_USER_ERROR", "查询用户所有角色失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = UserCacheConstant.ROLE_USERS_CACHE,
            key = UserCacheConstant.ROLE_USERS_KEY,
            expire = UserCacheConstant.ROLE_USERS_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<PageResponse<UserRoleResponse>> getUsersByRole(String role, Integer currentPage, Integer pageSize) {
        try {
            List<UserRole> roles = userRoleService.getUsersByRole(role, false, currentPage, pageSize);
            List<UserRoleResponse> responses = roles.stream()
                    .map(this::convertToResponse)
                    .toList();
            
            // 构造分页结果
            PageResponse<UserRoleResponse> result = new PageResponse<>();
            result.setDatas(responses);
            result.setCurrentPage(currentPage);
            result.setPageSize(pageSize);
            result.setTotal((long) responses.size());
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据角色查询用户失败", e);
            return Result.error("GET_USERS_BY_ROLE_ERROR", "根据角色查询用户失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> hasRole(Long userId, String role) {
        try {
            boolean hasRole = userRoleService.checkUserHasRole(userId, role);
            return Result.success(hasRole);
        } catch (Exception e) {
            log.error("检查用户角色失败", e);
            return Result.error("CHECK_USER_ROLE_ERROR", "检查用户角色失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> isRoleValid(Long userId, String role) {
        try {
            boolean hasActiveRole = userRoleService.checkUserHasActiveRole(userId, role);
            return Result.success(hasActiveRole);
        } catch (Exception e) {
            log.error("检查用户有效角色失败", e);
            return Result.error("CHECK_USER_ACTIVE_ROLE_ERROR", "检查用户有效角色失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = UserCacheConstant.USER_ROLE_USER_CACHE,
            key = UserCacheConstant.USER_ROLE_USER_KEY,
            expire = UserCacheConstant.USER_ROLE_USER_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<List<String>> getValidRoles(Long userId) {
        try {
            List<UserRole> roles = userRoleService.getActiveRolesByUserId(userId);
            List<String> roleNames = roles.stream()
                    .map(UserRole::getRole)
                    .collect(Collectors.toList());
            
            return Result.success(roleNames);
        } catch (Exception e) {
            log.error("查询用户有效角色失败", e);
            return Result.error("GET_VALID_ROLES_ERROR", "查询用户有效角色失败: " + e.getMessage());
        }
    }

    @Override
    public Result<String> getHighestRole(Long userId) {
        try {
            List<UserRole> roles = userRoleService.getActiveRolesByUserId(userId);
            if (roles.isEmpty()) {
                return Result.success("user"); // 默认角色
            }
            
            // 角色优先级：admin > vip > blogger > user
            String highestRole = "user";
            for (UserRole role : roles) {
                String roleName = role.getRole();
                if ("admin".equals(roleName)) {
                    highestRole = "admin";
                    break; // admin是最高级别
                } else if ("vip".equals(roleName) && !"admin".equals(highestRole)) {
                    highestRole = "vip";
                } else if ("blogger".equals(roleName) && "user".equals(highestRole)) {
                    highestRole = "blogger";
                }
            }
            
            return Result.success(highestRole);
        } catch (Exception e) {
            log.error("获取用户最高角色失败", e);
            return Result.error("GET_HIGHEST_ROLE_ERROR", "获取用户最高角色失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_ROLE_USER_CACHE)
    public Result<Void> revokeRole(Long userId, String role) {
        try {
            boolean success = userRoleService.revokeUserRole(userId, role);
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("REVOKE_ROLE_ERROR", "撤销用户角色失败");
            }
        } catch (Exception e) {
            log.error("撤销用户角色失败", e);
            return Result.error("REVOKE_ROLE_ERROR", "撤销用户角色失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_ROLE_USER_CACHE)
    public Result<Void> revokeAllRoles(Long userId) {
        try {
            boolean success = userRoleService.revokeAllUserRoles(userId);
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("REVOKE_ALL_ROLES_ERROR", "撤销用户所有角色失败");
            }
        } catch (Exception e) {
            log.error("撤销用户所有角色失败", e);
            return Result.error("REVOKE_ALL_ROLES_ERROR", "撤销用户所有角色失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_ROLE_USER_CACHE)
    public Result<UserRoleResponse> assignVipRole(Long userId, LocalDateTime expireTime) {
        try {
            UserRole userRole = userRoleService.assignRoleToUser(userId, "VIP", expireTime);
            if (userRole == null) {
                return Result.error("ASSIGN_VIP_ERROR", "分配VIP角色失败：用户已拥有该角色");
            }
            
            UserRoleResponse response = convertToResponse(userRole);
            return Result.success(response);
        } catch (Exception e) {
            log.error("分配VIP角色失败", e);
            return Result.error("ASSIGN_VIP_ERROR", "分配VIP角色失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_ROLE_USER_CACHE)
    public Result<UserRoleResponse> renewVipRole(Long userId, Integer extendDays) {
        try {
            LocalDateTime expireTime = LocalDateTime.now().plusDays(extendDays);
            UserRole role = userRoleService.assignRoleToUser(userId, "VIP", expireTime);
            if (role != null) {
                UserRoleResponse response = convertToResponse(role);
                return Result.success(response);
            } else {
                return Result.error("RENEW_VIP_ERROR", "续费VIP失败");
            }
        } catch (Exception e) {
            log.error("续费VIP失败", e);
            return Result.error("RENEW_VIP_ERROR", "续费VIP失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> isVipExpired(Long userId) {
        try {
            // 检查VIP角色是否过期
            boolean hasActiveVip = userRoleService.checkUserHasActiveRole(userId, "VIP");
            return Result.success(!hasActiveVip);
        } catch (Exception e) {
            log.error("检查VIP是否过期失败", e);
            return Result.error("CHECK_VIP_EXPIRED_ERROR", "检查VIP过期状态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<UserRoleResponse>> getExpiringVipUsers(Integer days, Integer currentPage, Integer pageSize) {
        try {
            List<UserRole> roles = userRoleService.getExpiringRoles(days);
            List<UserRole> vipRoles = roles.stream()
                    .filter(role -> "VIP".equals(role.getRole()))
                    .toList();
            
            List<UserRoleResponse> responses = vipRoles.stream()
                    .map(this::convertToResponse)
                    .toList();
            
            PageResponse<UserRoleResponse> result = new PageResponse<>();
            result.setDatas(responses);
            result.setCurrentPage(currentPage);
            result.setPageSize(pageSize);
            result.setTotal((long) vipRoles.size());
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询即将过期VIP用户失败", e);
            return Result.error("GET_EXPIRING_VIP_ERROR", "查询即将过期VIP用户失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Integer> cleanExpiredRoles() {
        try {
            boolean success = userRoleService.batchUpdateExpiredRoles();
            // 简化实现，返回固定数量
            return Result.success(success ? 1 : 0);
        } catch (Exception e) {
            log.error("清理过期角色失败", e);
            return Result.error("CLEAN_EXPIRED_ROLES_ERROR", "清理过期角色失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = UserCacheConstant.USER_ROLE_LIST_CACHE,
            key = UserCacheConstant.USER_ROLE_LIST_KEY,
            expire = UserCacheConstant.USER_ROLE_LIST_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<PageResponse<UserRoleResponse>> queryRoles(UserRoleQueryRequest request) {
        try {
            log.debug("分页查询用户角色: currentPage={}, pageSize={}", request.getCurrentPage(), request.getPageSize());
            
            PageResponse<UserRole> pageResult = userRoleService.queryRoles(request);
            
            List<UserRoleResponse> responses = pageResult.getDatas().stream()
                    .map(this::convertToResponse)
                    .toList();
            
            PageResponse<UserRoleResponse> result = new PageResponse<>();
            result.setDatas(responses);
            result.setCurrentPage(pageResult.getCurrentPage());
            result.setPageSize(pageResult.getPageSize());
            result.setTotal(pageResult.getTotal());
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询用户角色列表失败", e);
            return Result.error("ROLE_LIST_QUERY_ERROR", "查询用户角色列表失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = UserCacheConstant.USER_ROLE_LIST_CACHE,
            key = "role_statistics",
            expire = UserCacheConstant.USER_ROLE_LIST_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<Map<String, Object>> getRoleStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 简化统计实现
            List<UserRole> adminRoles = userRoleService.getUsersByRole("admin", true, 1, 100);
            List<UserRole> vipRoles = userRoleService.getUsersByRole("vip", true, 1, 100);
            List<UserRole> bloggerRoles = userRoleService.getUsersByRole("blogger", true, 1, 100);
            List<UserRole> userRoles = userRoleService.getUsersByRole("user", true, 1, 100);
            
            statistics.put("adminCount", adminRoles.size());
            statistics.put("vipCount", vipRoles.size());
            statistics.put("bloggerCount", bloggerRoles.size());
            statistics.put("userCount", userRoles.size());
            statistics.put("totalRoles", adminRoles.size() + vipRoles.size() + bloggerRoles.size() + userRoles.size());
            statistics.put("timestamp", System.currentTimeMillis());
            
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取角色统计失败", e);
            return Result.error("GET_ROLE_STATISTICS_ERROR", "获取角色统计失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Integer> batchAssignRole(List<Long> userIds, String role, LocalDateTime expireTime) {
        try {
            List<UserRole> roles = userRoleService.batchAssignRoles(userIds, role, expireTime);
            return Result.success(roles.size());
        } catch (Exception e) {
            log.error("批量分配角色失败", e);
            return Result.error("BATCH_ASSIGN_ROLE_ERROR", "批量分配角色失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Integer> batchRevokeRole(List<Long> userIds, String role) {
        try {
            boolean success = userRoleService.batchRevokeRoles(userIds, role);
            // 简化实现，返回处理的用户数量
            return Result.success(success ? userIds.size() : 0);
        } catch (Exception e) {
            log.error("批量撤销角色失败", e);
            return Result.error("BATCH_REVOKE_ROLE_ERROR", "批量撤销角色失败: " + e.getMessage());
        }
    }

    /**
     * 转换为用户角色响应对象
     */
    private UserRoleResponse convertToResponse(UserRole role) {
        UserRoleResponse response = new UserRoleResponse();
        BeanUtils.copyProperties(role, response);
        return response;
    }
}
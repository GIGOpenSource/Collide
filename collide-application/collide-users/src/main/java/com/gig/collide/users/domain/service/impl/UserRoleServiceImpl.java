package com.gig.collide.users.domain.service.impl;

import com.gig.collide.api.user.request.users.role.UserRoleQueryRequest;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.users.domain.entity.UserRole;
import com.gig.collide.users.domain.service.UserRoleService;
import com.gig.collide.users.infrastructure.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户角色领域服务实现 - 对应 t_user_role 表
 * 负责用户角色权限管理
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleMapper roleMapper;

    @Override
    @Transactional
    public UserRole createRole(UserRole userRole) {
        try {
            userRole.initDefaults();
            roleMapper.insert(userRole);
            
            log.info("用户角色创建成功: userId={}, role={}", userRole.getUserId(), userRole.getRole());
            return userRole;
        } catch (Exception e) {
            log.error("创建用户角色失败: userId={}, role={}", userRole.getUserId(), userRole.getRole(), e);
            throw new RuntimeException("创建用户角色失败", e);
        }
    }

    @Override
    @Transactional
    public UserRole updateRole(UserRole userRole) {
        try {
            userRole.updateModifyTime();
            roleMapper.updateById(userRole);
            
            log.info("用户角色更新成功: id={}, userId={}", userRole.getId(), userRole.getUserId());
            return userRole;
        } catch (Exception e) {
            log.error("更新用户角色失败: id={}", userRole.getId(), e);
            throw new RuntimeException("更新用户角色失败", e);
        }
    }

    @Override
    public UserRole getRoleById(Integer id) {
        return roleMapper.findById(id);
    }

    @Override
    public List<UserRole> getActiveRolesByUserId(Long userId) {
        return roleMapper.findActiveRolesByUserId(userId);
    }

    @Override
    public List<UserRole> getAllRolesByUserId(Long userId) {
        return roleMapper.findAllRolesByUserId(userId);
    }

    @Override
    public List<UserRole> getUsersByRole(String role, Boolean includeExpired, int page, int size) {
        try {
            int offset = (page - 1) * size;
            return roleMapper.findUsersByRole(role, includeExpired, offset, size);
        } catch (Exception e) {
            log.error("根据角色查询用户失败: role={}", role, e);
            throw new RuntimeException("查询用户失败", e);
        }
    }

    @Override
    public boolean checkUserHasRole(Long userId, String role) {
        try {
            int count = roleMapper.checkUserHasRole(userId, role);
            return count > 0;
        } catch (Exception e) {
            log.error("检查用户角色失败: userId={}, role={}", userId, role, e);
            return false;
        }
    }

    @Override
    public boolean checkUserHasActiveRole(Long userId, String role) {
        try {
            int count = roleMapper.checkUserHasActiveRole(userId, role);
            return count > 0;
        } catch (Exception e) {
            log.error("检查用户有效角色失败: userId={}, role={}", userId, role, e);
            return false;
        }
    }

    @Override
    @Transactional
    public UserRole assignRoleToUser(Long userId, String role, LocalDateTime expireTime) {
        try {
            // 检查用户是否已有该角色
            if (checkUserHasActiveRole(userId, role)) {
                log.info("用户已拥有该角色: userId={}, role={}", userId, role);
                return null;
            }

            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRole(role);
            userRole.setExpireTime(expireTime);
            
            return createRole(userRole);
        } catch (Exception e) {
            log.error("分配角色失败: userId={}, role={}", userId, role, e);
            throw new RuntimeException("分配角色失败", e);
        }
    }

    @Override
    @Transactional
    public boolean revokeUserRole(Long userId, String role) {
        try {
            int result = roleMapper.revokeUserRole(userId, role);
            if (result > 0) {
                log.info("撤销用户角色成功: userId={}, role={}", userId, role);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("撤销用户角色失败: userId={}, role={}", userId, role, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean revokeAllUserRoles(Long userId) {
        try {
            int result = roleMapper.revokeAllUserRoles(userId);
            if (result > 0) {
                log.info("撤销用户所有角色成功: userId={}", userId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("撤销用户所有角色失败: userId={}", userId, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateExpireTime(Integer id, LocalDateTime expireTime) {
        try {
            int result = roleMapper.updateExpireTime(id, expireTime);
            if (result > 0) {
                log.info("角色过期时间更新成功: id={}", id);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("更新角色过期时间失败: id={}", id, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean extendExpireTime(Integer id, Integer days) {
        try {
            int result = roleMapper.extendExpireTime(id, days);
            if (result > 0) {
                log.info("角色有效期延长成功: id={}, days={}", id, days);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("延长角色有效期失败: id={}, days={}", id, days, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean setPermanentRole(Integer id) {
        try {
            UserRole role = getRoleById(id);
            if (role != null) {
                role.setPermanent();
                updateRole(role);
                log.info("角色设置为永久有效: id={}", id);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("设置永久角色失败: id={}", id, e);
            return false;
        }
    }

    @Override
    public List<UserRole> getExpiringRoles(Integer days) {
        try {
            return roleMapper.findExpiringRoles(days);
        } catch (Exception e) {
            log.error("查询即将过期角色失败: days={}", days, e);
            throw new RuntimeException("查询即将过期角色失败", e);
        }
    }

    @Override
    public List<UserRole> getExpiredRoles() {
        try {
            return roleMapper.findExpiredRoles();
        } catch (Exception e) {
            log.error("查询已过期角色失败", e);
            throw new RuntimeException("查询已过期角色失败", e);
        }
    }

    @Override
    @Transactional
    public boolean batchUpdateExpiredRoles() {
        try {
            int result = roleMapper.batchUpdateExpiredRoles();
            log.info("批量更新过期角色状态成功: count={}", result);
            return result > 0;
        } catch (Exception e) {
            log.error("批量更新过期角色状态失败", e);
            return false;
        }
    }

    @Override
    public PageResponse<UserRole> queryRoles(UserRoleQueryRequest request) {
        try {
            int offset = (request.getCurrentPage() - 1) * request.getPageSize();
            
            List<UserRole> roles = roleMapper.findRolesByCondition(
                    request.getUserId(),
                    request.getRole(),
                    request.getIncludeExpired(),
                    request.getExpireTimeStart(),
                    request.getExpireTimeEnd(),
                    request.getSortField(),
                    request.getSortDirection(),
                    offset,
                    request.getPageSize()
            );
            
            Long total = roleMapper.countRolesByCondition(
                    request.getUserId(),
                    request.getRole(),
                    request.getIncludeExpired(),
                    request.getExpireTimeStart(),
                    request.getExpireTimeEnd()
            );
            
            PageResponse<UserRole> result = new PageResponse<>();
            result.setDatas(roles);
            result.setCurrentPage(request.getCurrentPage());
            result.setPageSize(request.getPageSize());
            result.setTotal(total);
            
            return result;
        } catch (Exception e) {
            log.error("查询用户角色列表失败", e);
            throw new RuntimeException("查询用户角色失败", e);
        }
    }

    @Override
    public boolean isAdmin(Long userId) {
        return checkUserHasActiveRole(userId, "admin");
    }

    @Override
    public boolean isVip(Long userId) {
        return checkUserHasActiveRole(userId, "vip");
    }

    @Override
    public boolean isBlogger(Long userId) {
        return checkUserHasActiveRole(userId, "blogger");
    }

    @Override
    public UserRole getHighestRole(Long userId) {
        try {
            List<UserRole> roles = getActiveRolesByUserId(userId);
            
            // 按角色优先级排序：admin > vip > blogger > user
            for (UserRole role : roles) {
                if (role.isAdmin()) return role;
            }
            for (UserRole role : roles) {
                if (role.isVip()) return role;
            }
            for (UserRole role : roles) {
                if (role.isBlogger()) return role;
            }
            
            // 返回第一个角色或null
            return roles.isEmpty() ? null : roles.get(0);
        } catch (Exception e) {
            log.error("获取用户最高权限角色失败: userId={}", userId, e);
            return null;
        }
    }

    @Override
    @Transactional
    public List<UserRole> batchAssignRoles(List<Long> userIds, String role, LocalDateTime expireTime) {
        return userIds.stream()
                .map(userId -> assignRoleToUser(userId, role, expireTime))
                .filter(userRole -> userRole != null)
                .toList();
    }

    @Override
    @Transactional
    public boolean batchRevokeRoles(List<Long> userIds, String role) {
        return userIds.stream()
                .allMatch(userId -> revokeUserRole(userId, role));
    }

    @Override
    @Transactional
    public boolean deleteRole(Integer id) {
        try {
            int result = roleMapper.deleteById(id);
            if (result > 0) {
                log.warn("用户角色被删除: id={}", id);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("删除用户角色失败: id={}", id, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteAllUserRoles(Long userId) {
        try {
            int result = roleMapper.deleteByUserId(userId);
            if (result > 0) {
                log.warn("用户所有角色被删除: userId={}", userId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("删除用户所有角色失败: userId={}", userId, e);
            return false;
        }
    }

    @Override
    @Transactional
    public UserRole initializeDefaultRole(Long userId) {
        try {
            // 初始化为普通用户角色
            return assignRoleToUser(userId, "user", null);
        } catch (Exception e) {
            log.error("初始化用户默认角色失败: userId={}", userId, e);
            throw new RuntimeException("初始化用户角色失败", e);
        }
    }
}
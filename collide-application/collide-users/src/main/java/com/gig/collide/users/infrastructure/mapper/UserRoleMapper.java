package com.gig.collide.users.infrastructure.mapper;

import com.gig.collide.users.domain.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户角色Mapper接口 - 对应 t_user_role 表
 * 负责用户角色权限管理
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Mapper
public interface UserRoleMapper {

    /**
     * 根据ID查询用户角色
     */
    UserRole findById(@Param("id") Integer id);

    /**
     * 根据用户ID查询有效角色
     */
    List<UserRole> findActiveRolesByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询所有角色（包括过期）
     */
    List<UserRole> findAllRolesByUserId(@Param("userId") Long userId);

    /**
     * 根据角色类型查询用户列表
     */
    List<UserRole> findUsersByRole(@Param("role") String role,
                                  @Param("includeExpired") Boolean includeExpired,
                                  @Param("offset") Integer offset,
                                  @Param("size") Integer size);

    /**
     * 检查用户是否拥有指定角色
     */
    int checkUserHasRole(@Param("userId") Long userId, @Param("role") String role);

    /**
     * 检查用户是否拥有有效的指定角色
     */
    int checkUserHasActiveRole(@Param("userId") Long userId, @Param("role") String role);

    /**
     * 分页查询用户角色
     */
    List<UserRole> findRolesByCondition(@Param("userId") Long userId,
                                       @Param("role") String role,
                                       @Param("includeExpired") Boolean includeExpired,
                                       @Param("expireTimeStart") LocalDateTime expireTimeStart,
                                       @Param("expireTimeEnd") LocalDateTime expireTimeEnd,
                                       @Param("sortField") String sortField,
                                       @Param("sortDirection") String sortDirection,
                                       @Param("offset") Integer offset,
                                       @Param("size") Integer size);

    /**
     * 统计用户角色数量
     */
    Long countRolesByCondition(@Param("userId") Long userId,
                              @Param("role") String role,
                              @Param("includeExpired") Boolean includeExpired,
                              @Param("expireTimeStart") LocalDateTime expireTimeStart,
                              @Param("expireTimeEnd") LocalDateTime expireTimeEnd);

    /**
     * 查询即将过期的角色
     */
    List<UserRole> findExpiringRoles(@Param("days") Integer days);

    /**
     * 查询已过期的角色
     */
    List<UserRole> findExpiredRoles();

    /**
     * 插入用户角色
     */
    int insert(UserRole userRole);

    /**
     * 更新用户角色
     */
    int updateById(UserRole userRole);

    /**
     * 更新角色过期时间
     */
    int updateExpireTime(@Param("id") Integer id, @Param("expireTime") LocalDateTime expireTime);

    /**
     * 延长角色有效期
     */
    int extendExpireTime(@Param("id") Integer id, @Param("days") Integer days);

    /**
     * 批量更新过期角色状态
     */
    int batchUpdateExpiredRoles();

    /**
     * 撤销用户角色（设置为过期）
     */
    int revokeUserRole(@Param("userId") Long userId, @Param("role") String role);

    /**
     * 撤销用户所有角色
     */
    int revokeAllUserRoles(@Param("userId") Long userId);

    /**
     * 删除用户角色
     */
    int deleteById(@Param("id") Integer id);

    /**
     * 删除用户所有角色
     */
    int deleteByUserId(@Param("userId") Long userId);
}
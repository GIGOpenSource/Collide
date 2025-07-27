package com.gig.collide.users.domain.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.users.domain.entity.UserUnified;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 用户统一仓储接口（去连表设计）
 * 定义用户相关的数据访问方法，所有操作基于单表
 *
 * @author GIG Team
 * @version 2.0 (重构版本)
 * @since 2024-01-01
 */
public interface UserUnifiedRepository {

    // ================================ 基础CRUD操作 ================================

    /**
     * 保存用户信息
     *
     * @param user 用户实体
     * @return 保存后的用户实体
     */
    UserUnified save(UserUnified user);

    /**
     * 根据ID查询用户
     *
     * @param userId 用户ID
     * @return 用户实体（Optional）
     */
    Optional<UserUnified> findById(Long userId);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户实体（Optional）
     */
    Optional<UserUnified> findByUsername(String username);

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户实体（Optional）
     */
    Optional<UserUnified> findByEmail(String email);

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户实体（Optional）
     */
    Optional<UserUnified> findByPhone(String phone);

    /**
     * 根据邀请码查询用户
     *
     * @param inviteCode 邀请码
     * @return 用户实体（Optional）
     */
    Optional<UserUnified> findByInviteCode(String inviteCode);

    /**
     * 删除用户（逻辑删除）
     *
     * @param userId 用户ID
     */
    void deleteById(Long userId);

    // ================================ 存在性检查 ================================

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     *
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 检查手机号是否存在
     *
     * @param phone 手机号
     * @return 是否存在
     */
    boolean existsByPhone(String phone);

    /**
     * 检查邀请码是否存在
     *
     * @param inviteCode 邀请码
     * @return 是否存在
     */
    boolean existsByInviteCode(String inviteCode);

    // ================================ 搜索和分页查询 ================================

    /**
     * 搜索用户（单表搜索，支持关键词匹配）
     *
     * @param keyword 搜索关键词
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 搜索结果
     */
    IPage<UserUnified> searchUsers(String keyword, Integer pageNum, Integer pageSize);

    /**
     * 分页查询用户列表
     *
     * @param pageNum 页码
     * @param pageSize 页大小
     * @param usernameKeyword 用户名关键词
     * @param status 状态过滤
     * @param role 角色过滤
     * @return 分页结果
     */
    IPage<UserUnified> pageQuery(Integer pageNum, Integer pageSize, 
                                String usernameKeyword, String status, String role);

    // ================================ 博主相关查询 ================================

    /**
     * 根据博主状态分页查询用户
     *
     * @param bloggerStatus 博主状态
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 分页结果
     */
    IPage<UserUnified> findByBloggerStatus(String bloggerStatus, Integer pageNum, Integer pageSize);

    /**
     * 统计各种博主状态的用户数量
     *
     * @param bloggerStatus 博主状态
     * @return 数量
     */
    long countByBloggerStatus(String bloggerStatus);

    // ================================ 邀请相关查询 ================================

    /**
     * 根据邀请人ID分页查询被邀请用户
     *
     * @param inviterId 邀请人ID
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 被邀请用户分页结果
     */
    IPage<UserUnified> findByInviterId(Long inviterId, Integer pageNum, Integer pageSize);

    /**
     * 获取邀请排行榜
     *
     * @param limit 限制条数
     * @return 邀请排行榜
     */
    List<UserUnified> getInviteRanking(int limit);

    // ================================ 统计字段更新 ================================

    /**
     * 更新用户统计字段
     *
     * @param userId 用户ID
     * @param field 统计字段
     * @param increment 增量
     * @return 是否更新成功
     */
    boolean updateStatisticsField(Long userId, String field, long increment);

    /**
     * 批量更新用户统计字段（性能优化版本）
     *
     * @param userIds 用户ID列表
     * @param field 统计字段
     * @param increment 增量
     * @return 成功更新的用户数量
     */
    int batchUpdateStatisticsField(List<Long> userIds, String field, long increment);

    // ================================ 缓存优化方法 ================================

    /**
     * 获取用户关键信息（用于缓存）
     * 只返回必要字段，减少内存占用
     *
     * @param userId 用户ID
     * @return 用户关键信息
     */
    Optional<UserUnified> findKeyInfoById(Long userId);

    // ================================ 统计相关方法 ================================

    /**
     * 获取活跃用户统计
     *
     * @param days 统计天数
     * @return 活跃用户数
     */
    long countActiveUsers(int days);

    /**
     * 获取用户全局统计信息
     *
     * @return 统计信息Map
     */
    Map<String, Long> getUserStatistics();

    // ================================ 批量操作方法 ================================

    /**
     * 批量保存用户
     *
     * @param users 用户列表
     * @return 保存成功的数量
     */
    int batchSave(List<UserUnified> users);

    /**
     * 批量更新用户状态
     *
     * @param userIds 用户ID列表
     * @param status 新状态
     * @return 更新成功的数量
     */
    int batchUpdateStatus(List<Long> userIds, String status);

    // ================================ 幂等性操作方法 ================================

    /**
     * 幂等性更新用户状态
     * 使用乐观锁机制，确保状态更新的幂等性
     *
     * @param userId 用户ID
     * @param expectedStatus 期望的当前状态
     * @param newStatus 新状态
     * @param version 当前版本号
     * @return 是否更新成功
     */
    boolean updateStatusIdempotent(Long userId, String expectedStatus, String newStatus, Integer version);

    /**
     * 幂等性更新用户角色
     * 使用乐观锁机制，确保角色更新的幂等性
     *
     * @param userId 用户ID
     * @param expectedRole 期望的当前角色
     * @param newRole 新角色
     * @param version 当前版本号
     * @return 是否更新成功
     */
    boolean updateRoleIdempotent(Long userId, String expectedRole, String newRole, Integer version);

    /**
     * 幂等性更新博主认证状态
     * 使用乐观锁机制，确保博主状态更新的幂等性
     *
     * @param userId 用户ID
     * @param expectedBloggerStatus 期望的当前博主状态
     * @param newBloggerStatus 新博主状态
     * @param version 当前版本号
     * @return 是否更新成功
     */
    boolean updateBloggerStatusIdempotent(Long userId, String expectedBloggerStatus, 
                                         String newBloggerStatus, Integer version);
} 
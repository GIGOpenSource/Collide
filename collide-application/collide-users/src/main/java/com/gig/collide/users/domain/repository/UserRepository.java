package com.gig.collide.users.domain.repository;

import com.gig.collide.users.domain.entity.User;

import java.util.Optional;

/**
 * 用户仓储接口
 * 定义用户领域对象的持久化契约
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public interface UserRepository {

    /**
     * 保存用户
     *
     * @param user 用户实体
     * @return 保存后的用户实体
     */
    User save(User user);

    /**
     * 根据用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户实体
     */
    Optional<User> findById(Long userId);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户实体
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户实体
     */
    Optional<User> findByEmail(String email);

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户实体
     */
    Optional<User> findByPhone(String phone);

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
     * 删除用户
     *
     * @param userId 用户ID
     */
    void deleteById(Long userId);

    /**
     * 分页查询用户列表
     *
     * @param pageNum         页码（从1开始）
     * @param pageSize        每页大小
     * @param usernameKeyword 用户名关键词
     * @param status          用户状态
     * @param role            用户角色
     * @return 分页结果
     */
    com.baomidou.mybatisplus.core.metadata.IPage<User> pageQuery(
            Integer pageNum, Integer pageSize, String usernameKeyword, 
            String status, String role);
} 
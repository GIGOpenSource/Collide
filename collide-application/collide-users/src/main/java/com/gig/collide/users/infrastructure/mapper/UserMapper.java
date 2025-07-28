package com.gig.collide.users.infrastructure.mapper;

import com.gig.collide.users.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户Mapper接口 - 简洁版
 * 基于简洁版t_user表设计
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Mapper
public interface UserMapper {

    /**
     * 根据ID查询用户
     */
    User findById(@Param("id") Long id);

    /**
     * 根据用户名查询用户
     */
    User findByUsername(@Param("username") String username);

    /**
     * 根据邮箱查询用户
     */
    User findByEmail(@Param("email") String email);

    /**
     * 根据手机号查询用户
     */
    User findByPhone(@Param("phone") String phone);

    /**
     * 分页查询用户列表
     */
    List<User> findUsersByCondition(@Param("username") String username,
                                   @Param("nickname") String nickname,
                                   @Param("email") String email,
                                   @Param("phone") String phone,
                                   @Param("role") String role,
                                   @Param("status") String status,
                                   @Param("offset") Integer offset,
                                   @Param("size") Integer size);

    /**
     * 统计用户数量
     */
    Long countUsersByCondition(@Param("username") String username,
                              @Param("nickname") String nickname,
                              @Param("email") String email,
                              @Param("phone") String phone,
                              @Param("role") String role,
                              @Param("status") String status);

    /**
     * 插入用户
     */
    int insert(User user);

    /**
     * 更新用户信息
     */
    int updateById(User user);

    /**
     * 更新用户状态
     */
    int updateUserStatus(@Param("userId") Long userId, @Param("status") String status);

    /**
     * 更新最后登录时间
     */
    int updateLastLoginTime(@Param("userId") Long userId);

    /**
     * 更新用户统计数据
     */
    int updateUserStats(@Param("userId") Long userId, 
                       @Param("statsType") String statsType, 
                       @Param("increment") Integer increment);

    /**
     * 删除用户（逻辑删除）
     */
    int deleteById(@Param("userId") Long userId);
} 
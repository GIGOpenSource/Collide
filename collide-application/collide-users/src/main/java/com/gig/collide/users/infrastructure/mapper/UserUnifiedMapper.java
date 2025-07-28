package com.gig.collide.users.infrastructure.mapper;

import com.gig.collide.users.domain.entity.UserUnified;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户统一信息Mapper
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Mapper
public interface UserUnifiedMapper extends BaseMapper<UserUnified> {

    /**
     * 根据用户名查询用户
     * 
     * @param username 用户名
     * @return 用户信息
     */
    UserUnified findByUsername(@Param("username") String username);

    /**
     * 根据邮箱查询用户
     * 
     * @param email 邮箱
     * @return 用户信息
     */
    UserUnified findByEmail(@Param("email") String email);

    /**
     * 根据手机号查询用户
     * 
     * @param phone 手机号
     * @return 用户信息
     */
    UserUnified findByPhone(@Param("phone") String phone);

    /**
     * 根据邀请码查询用户
     * 
     * @param inviteCode 邀请码
     * @return 用户信息
     */
    UserUnified findByInviteCode(@Param("inviteCode") String inviteCode);

    /**
     * 根据手机号和密码查询用户
     * 
     * @param phone 手机号
     * @param passwordHash 密码哈希
     * @return 用户信息
     */
    UserUnified findByPhoneAndPassword(@Param("phone") String phone, @Param("passwordHash") String passwordHash);

    /**
     * 更新用户统计信息
     * 
     * @param userId 用户ID
     * @param fieldName 字段名
     * @param incrementValue 增量值
     * @return 影响行数
     */
    int updateUserStatistics(@Param("userId") Long userId, @Param("fieldName") String fieldName, @Param("incrementValue") Integer incrementValue);

    /**
     * 更新最后登录时间
     * 
     * @param userId 用户ID
     * @return 影响行数
     */
    int updateLastLoginTime(@Param("userId") Long userId);
} 
package com.gig.collide.users.infrastructure.mapper;

import com.gig.collide.users.domain.entity.UserCore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户核心Mapper接口 - 对应 t_user 表
 * 负责用户基础信息和认证相关功能
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Mapper
public interface UserCoreMapper {

    /**
     * 根据ID查询用户
     */
    UserCore findById(@Param("id") Long id);

    /**
     * 根据用户名查询用户
     */
    UserCore findByUsername(@Param("username") String username);

    /**
     * 根据邮箱查询用户
     */
    UserCore findByEmail(@Param("email") String email);

    /**
     * 根据手机号查询用户
     */
    UserCore findByPhone(@Param("phone") String phone);

    /**
     * 检查用户名是否存在
     */
    int checkUsernameExists(@Param("username") String username);

    /**
     * 检查邮箱是否存在
     */
    int checkEmailExists(@Param("email") String email);

    /**
     * 检查手机号是否存在
     */
    int checkPhoneExists(@Param("phone") String phone);

    /**
     * 分页查询用户核心信息
     */
    List<UserCore> findUsersByCondition(@Param("username") String username,
                                       @Param("email") String email,
                                       @Param("phone") String phone,
                                       @Param("status") Integer status,
                                       @Param("createTimeStart") String createTimeStart,
                                       @Param("createTimeEnd") String createTimeEnd,
                                       @Param("offset") Integer offset,
                                       @Param("size") Integer size);

    /**
     * 统计用户数量
     */
    Long countUsersByCondition(@Param("username") String username,
                              @Param("email") String email,
                              @Param("phone") String phone,
                              @Param("status") Integer status,
                              @Param("createTimeStart") String createTimeStart,
                              @Param("createTimeEnd") String createTimeEnd);

    /**
     * 批量查询用户核心信息
     */
    List<UserCore> findUsersByIds(@Param("userIds") List<Long> userIds);

    /**
     * 插入用户
     */
    int insert(UserCore user);

    /**
     * 更新用户信息
     */
    int updateById(UserCore user);

    /**
     * 更新用户状态
     */
    int updateUserStatus(@Param("userId") Long userId, @Param("status") Integer status);

    /**
     * 修改用户密码
     */
    int updatePassword(@Param("userId") Long userId, @Param("passwordHash") String passwordHash);

    /**
     * 验证用户密码
     */
    String getPasswordHash(@Param("userId") Long userId);

    /**
     * 删除用户（物理删除，谨慎使用）
     */
    int deleteById(@Param("userId") Long userId);
}
package com.gig.collide.api.user.service;

import com.gig.collide.api.user.request.*;
import com.gig.collide.api.user.response.*;
import com.gig.collide.api.user.response.data.UserUnifiedInfo;
import com.gig.collide.api.user.response.data.BasicUserUnifiedInfo;
import com.gig.collide.base.response.PageResponse;

/**
 * 用户门面服务接口
 * 提供用户核心业务功能
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
public interface UserFacadeService {

    /**
     * 用户统一信息查询
     * 
     * @param userQueryRequest 查询请求
     * @return 查询响应
     */
    UserUnifiedQueryResponse<UserUnifiedInfo> queryUser(UserUnifiedQueryRequest userQueryRequest);

    /**
     * 基础用户信息查询（不包含敏感信息）
     * 
     * @param userQueryRequest 查询请求
     * @return 基础用户信息响应
     */
    UserUnifiedQueryResponse<BasicUserUnifiedInfo> queryBasicUser(UserUnifiedQueryRequest userQueryRequest);

    /**
     * 分页查询用户信息
     * 
     * @param userQueryRequest 分页查询请求
     * @return 分页响应
     */
    PageResponse<UserUnifiedInfo> pageQueryUsers(UserUnifiedQueryRequest userQueryRequest);

    /**
     * 用户注册
     * 
     * @param registerRequest 注册请求
     * @return 注册响应
     */
    UserUnifiedRegisterResponse register(UserUnifiedRegisterRequest registerRequest);

    /**
     * 用户信息修改
     * 
     * @param modifyRequest 修改请求
     * @return 修改响应
     */
    UserUnifiedModifyResponse modify(UserUnifiedModifyRequest modifyRequest);

    /**
     * 用户激活
     * 
     * @param activateRequest 激活请求
     * @return 激活响应
     */
    UserUnifiedActivateResponse activate(UserUnifiedActivateRequest activateRequest);

    /**
     * 申请博主认证
     * 
     * @param applyRequest 申请请求
     * @return 申请响应
     */
    UserUnifiedBloggerApplyResponse applyBlogger(UserUnifiedBloggerApplyRequest applyRequest);

    /**
     * 获取用户详细信息（包含博主信息）
     * 
     * @param userId 用户ID
     * @return 用户详细信息
     */
    UserUnifiedQueryResponse<UserUnifiedInfo> getUserWithBloggerInfo(Long userId);

    /**
     * 检查用户名是否可用
     * 
     * @param username 用户名
     * @return 是否可用
     */
    Boolean checkUsernameAvailable(String username);

    /**
     * 检查邮箱是否可用
     * 
     * @param email 邮箱
     * @return 是否可用
     */
    Boolean checkEmailAvailable(String email);

    /**
     * 检查手机号是否可用
     * 
     * @param phone 手机号
     * @return 是否可用
     */
    Boolean checkPhoneAvailable(String phone);

    /**
     * 生成用户邀请码
     * 
     * @param userId 用户ID
     * @return 邀请码
     */
    String generateInviteCode(Long userId);
} 
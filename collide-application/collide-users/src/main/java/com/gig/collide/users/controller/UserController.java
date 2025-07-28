package com.gig.collide.users.controller;

import com.gig.collide.api.user.request.*;
import com.gig.collide.api.user.response.*;
import com.gig.collide.api.user.response.data.UserUnifiedInfo;
import com.gig.collide.api.user.response.data.BasicUserUnifiedInfo;
import com.gig.collide.users.facade.UserFacadeServiceImpl;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.base.response.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 用户控制器
 * 提供HTTP API接口
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserFacadeServiceImpl userFacadeService;

    /**
     * 查询用户详细信息
     * 
     * @param userQueryRequest 查询请求
     * @return 用户详细信息
     */
    @PostMapping("/query")
    public UserUnifiedQueryResponse<UserUnifiedInfo> queryUser(@Valid @RequestBody UserUnifiedQueryRequest userQueryRequest) {
        return userFacadeService.queryUser(userQueryRequest);
    }

    /**
     * 查询用户基础信息
     * 
     * @param userQueryRequest 查询请求
     * @return 用户基础信息
     */
    @PostMapping("/query/basic")
    public UserUnifiedQueryResponse<BasicUserUnifiedInfo> queryBasicUser(@Valid @RequestBody UserUnifiedQueryRequest userQueryRequest) {
        return userFacadeService.queryBasicUser(userQueryRequest);
    }

    /**
     * 分页查询用户
     * 
     * @param userQueryRequest 分页查询请求
     * @return 分页结果
     */
    @PostMapping("/page")
    public PageResponse<UserUnifiedInfo> pageQueryUsers(@Valid @RequestBody UserUnifiedQueryRequest userQueryRequest) {
        return userFacadeService.pageQueryUsers(userQueryRequest);
    }

    /**
     * 用户注册
     * 
     * @param registerRequest 注册请求
     * @return 注册结果
     */
    @PostMapping("/register")
    public UserUnifiedRegisterResponse register(@Valid @RequestBody UserUnifiedRegisterRequest registerRequest) {
        return userFacadeService.register(registerRequest);
    }

    /**
     * 用户信息修改
     * 
     * @param modifyRequest 修改请求
     * @return 修改结果
     */
    @PutMapping("/modify")
    public UserUnifiedModifyResponse modify(@Valid @RequestBody UserUnifiedModifyRequest modifyRequest) {
        return userFacadeService.modify(modifyRequest);
    }

    /**
     * 用户激活
     * 
     * @param activateRequest 激活请求
     * @return 激活结果
     */
    @PostMapping("/activate")
    public UserUnifiedActivateResponse activate(@Valid @RequestBody UserUnifiedActivateRequest activateRequest) {
        return userFacadeService.activate(activateRequest);
    }

    /**
     * 申请博主认证
     * 
     * @param applyRequest 申请请求
     * @return 申请结果
     */
    @PostMapping("/apply-blogger")
    public UserUnifiedBloggerApplyResponse applyBlogger(@Valid @RequestBody UserUnifiedBloggerApplyRequest applyRequest) {
        return userFacadeService.applyBlogger(applyRequest);
    }

    /**
     * 获取用户详细信息（包含博主信息）
     * 
     * @param userId 用户ID
     * @return 用户详细信息
     */
    @GetMapping("/{userId}/blogger-info")
    public UserUnifiedQueryResponse<UserUnifiedInfo> getUserWithBloggerInfo(@PathVariable Long userId) {
        return userFacadeService.getUserWithBloggerInfo(userId);
    }

    /**
     * 检查用户名是否可用
     * 
     * @param username 用户名
     * @return 是否可用
     */
    @GetMapping("/check/username/{username}")
    public BaseResponse checkUsernameAvailable(@PathVariable String username) {
        BaseResponse response = new BaseResponse();
        Boolean available = userFacadeService.checkUsernameAvailable(username);
        response.setSuccess(available);
        response.setMessage(available ? "用户名可用" : "用户名已存在");
        return response;
    }

    /**
     * 检查邮箱是否可用
     * 
     * @param email 邮箱
     * @return 是否可用
     */
    @GetMapping("/check/email/{email}")
    public BaseResponse checkEmailAvailable(@PathVariable String email) {
        BaseResponse response = new BaseResponse();
        Boolean available = userFacadeService.checkEmailAvailable(email);
        response.setSuccess(available);
        response.setMessage(available ? "邮箱可用" : "邮箱已存在");
        return response;
    }

    /**
     * 检查手机号是否可用
     * 
     * @param phone 手机号
     * @return 是否可用
     */
    @GetMapping("/check/phone/{phone}")
    public BaseResponse checkPhoneAvailable(@PathVariable String phone) {
        BaseResponse response = new BaseResponse();
        Boolean available = userFacadeService.checkPhoneAvailable(phone);
        response.setSuccess(available);
        response.setMessage(available ? "手机号可用" : "手机号已存在");
        return response;
    }

    /**
     * 生成用户邀请码
     * 
     * @param userId 用户ID
     * @return 邀请码
     */
    @PostMapping("/{userId}/generate-invite-code")
    public BaseResponse generateInviteCode(@PathVariable Long userId) {
        BaseResponse response = new BaseResponse();
        String inviteCode = userFacadeService.generateInviteCode(userId);
        if (inviteCode != null) {
            response.setSuccess(true);
            response.setResponseCode("SUCCESS");
            response.setResponseMessage("邀请码生成成功: " + inviteCode);
        } else {
            response.setSuccess(false);
            response.setResponseCode("GENERATE_FAILED");
            response.setResponseMessage("邀请码生成失败");
        }
        return response;
    }
} 
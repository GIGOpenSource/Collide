package com.gig.collide.users.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.gig.collide.api.user.constant.UserRole;
import com.gig.collide.api.user.constant.UserStateEnum;
import com.gig.collide.api.user.request.UserActiveRequest;
import com.gig.collide.api.user.request.UserModifyRequest;
import com.gig.collide.api.user.request.UserPageQueryRequest;
import com.gig.collide.api.user.request.UserStatusRequest;
import com.gig.collide.api.user.response.UserOperatorResponse;
import com.gig.collide.api.user.response.data.UserInfo;
import com.gig.collide.api.user.service.UserFacadeService;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.users.domain.entity.User;
import com.gig.collide.users.domain.entity.UserProfile;
import com.gig.collide.users.domain.entity.convertor.UserConvertor;
import com.gig.collide.users.domain.service.UserDomainService;
import com.gig.collide.users.infrastructure.exception.UserBusinessException;
import com.gig.collide.web.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 * 提供用户管理相关的 REST API
 *
 * @author GIG
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户注册、认证、个人信息管理相关接口")
public class UserController {

    @Autowired
    private UserDomainService userDomainService;
    
    @Autowired
    private UserFacadeService userFacadeService;

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    @SaCheckLogin
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    public Result<UserInfo> getCurrentUser() {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            User user = userDomainService.getUserById(userId);
            UserProfile profile = userDomainService.getUserProfile(userId);
            UserInfo userInfo = convertToUserInfo(user, profile);
            return Result.success(userInfo);
        } catch (UserBusinessException e) {
            log.error("获取当前用户信息失败：{}", e.getMessage());
            return Result.error(e.getErrorCode().getCode(), e.getErrorCode().getMessage());
        } catch (Exception e) {
            log.error("获取当前用户信息异常", e);
            return Result.error("SYSTEM_ERROR", "系统异常，请稍后重试");
        }
    }

    /**
     * 根据用户ID获取用户信息
     */
    @GetMapping("/{userId}")
    @Operation(summary = "获取用户信息", description = "根据用户ID获取用户详细信息")
    public Result<UserInfo> getUserById(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId) {
        try {
            User user = userDomainService.getUserById(userId);
            UserProfile profile = userDomainService.getUserProfile(userId);
            UserInfo userInfo = convertToUserInfo(user, profile);
            return Result.success(userInfo);
        } catch (UserBusinessException e) {
            log.error("获取用户信息失败：{}", e.getMessage());
            return Result.error(e.getErrorCode().getCode(), e.getErrorCode().getMessage());
        } catch (Exception e) {
            log.error("获取用户信息异常", e);
            return Result.error("SYSTEM_ERROR", "系统异常，请稍后重试");
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/me")
    @SaCheckLogin
    @Operation(summary = "更新用户信息", description = "更新当前登录用户的基本信息")
    public Result<UserInfo> updateUserInfo(@Valid @RequestBody UserModifyRequest request) {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            request.setUserId(userId);
            
            UserOperatorResponse response = userFacadeService.modify(request);
            if (response.getSuccess()) {
                User user = userDomainService.getUserById(userId);
                UserProfile profile = userDomainService.getUserProfile(userId);
                UserInfo userInfo = convertToUserInfo(user, profile);
                return Result.success(userInfo);
            } else {
                return Result.error(response.getResponseCode(), response.getResponseMessage());
            }
        } catch (UserBusinessException e) {
            log.error("更新用户信息失败：{}", e.getMessage());
            return Result.error(e.getErrorCode().getCode(), e.getErrorCode().getMessage());
        } catch (Exception e) {
            log.error("更新用户信息异常", e);
            return Result.error("SYSTEM_ERROR", "系统异常，请稍后重试");
        }
    }

    /**
     * 分页查询用户列表
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询用户列表", description = "根据条件分页查询用户列表")
    public Result<PageResponse<UserInfo>> pageQueryUsers(@Valid @RequestBody UserPageQueryRequest request) {
        try {
            PageResponse<UserInfo> response = userFacadeService.pageQuery(request);
            return Result.success(response);
        } catch (Exception e) {
            log.error("分页查询用户列表异常", e);
            return Result.error("SYSTEM_ERROR", "系统异常，请稍后重试");
        }
    }

    /**
     * 激活用户
     */
    @PostMapping("/{userId}/activate")
    @Operation(summary = "激活用户", description = "激活指定用户账号")
    public Result<Void> activateUser(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId) {
        try {
            UserStatusRequest request = new UserStatusRequest();
            request.setUserId(userId);
            request.setActive(true);
            
            UserOperatorResponse response = userFacadeService.updateStatus(request);
            if (response.getSuccess()) {
                return Result.success(null);
            } else {
                return Result.error(response.getResponseCode(), response.getResponseMessage());
            }
        } catch (Exception e) {
            log.error("激活用户异常", e);
            return Result.error("SYSTEM_ERROR", "系统异常，请稍后重试");
        }
    }

    /**
     * 禁用用户
     */
    @PostMapping("/{userId}/deactivate")
    @Operation(summary = "禁用用户", description = "禁用指定用户账号")
    public Result<Void> deactivateUser(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId) {
        try {
            UserStatusRequest request = new UserStatusRequest();
            request.setUserId(userId);
            request.setActive(false);
            
            UserOperatorResponse response = userFacadeService.updateStatus(request);
            if (response.getSuccess()) {
                return Result.success(null);
            } else {
                return Result.error(response.getResponseCode(), response.getResponseMessage());
            }
        } catch (Exception e) {
            log.error("禁用用户异常", e);
            return Result.error("SYSTEM_ERROR", "系统异常，请稍后重试");
        }
    }

    /**
     * 检查用户名是否存在
     */
    @GetMapping("/check-username")
    @Operation(summary = "检查用户名是否存在", description = "检查指定用户名是否已被使用")
    public Result<Boolean> checkUsernameExists(
            @Parameter(description = "用户名", required = true) @RequestParam String username) {
        try {
            boolean exists = userDomainService.checkUsernameExists(username);
            return Result.success(exists);
        } catch (Exception e) {
            log.error("检查用户名是否存在异常", e);
            return Result.error("SYSTEM_ERROR", "系统异常，请稍后重试");
        }
    }

    /**
     * 检查邮箱是否存在
     */
    @GetMapping("/check-email")
    @Operation(summary = "检查邮箱是否存在", description = "检查指定邮箱是否已被注册")
    public Result<Boolean> checkEmailExists(
            @Parameter(description = "邮箱地址", required = true) @RequestParam String email) {
        try {
            boolean exists = userDomainService.checkEmailExists(email);
            return Result.success(exists);
        } catch (Exception e) {
            log.error("检查邮箱是否存在异常", e);
            return Result.error("SYSTEM_ERROR", "系统异常，请稍后重试");
        }
    }

    /**
     * 检查手机号是否存在
     */
    @GetMapping("/check-phone")
    @Operation(summary = "检查手机号是否存在", description = "检查指定手机号是否已被注册")
    public Result<Boolean> checkPhoneExists(
            @Parameter(description = "手机号", required = true) @RequestParam String phone) {
        try {
            boolean exists = userDomainService.checkPhoneExists(phone);
            return Result.success(exists);
        } catch (Exception e) {
            log.error("检查手机号是否存在异常", e);
            return Result.error("SYSTEM_ERROR", "系统异常，请稍后重试");
        }
    }

    /**
     * 获取用户统计信息
     */
    @GetMapping("/{userId}/statistics")
    @Operation(summary = "获取用户统计信息", description = "获取指定用户的统计信息")
    public Result<Object> getUserStatistics(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId) {
        try {
            // TODO: 实现用户统计信息获取
            return Result.success("用户统计信息");
        } catch (Exception e) {
            log.error("获取用户统计信息异常", e);
            return Result.error("SYSTEM_ERROR", "系统异常，请稍后重试");
        }
    }

    /**
     * 转换用户信息
     */
    private UserInfo convertToUserInfo(User user, UserProfile profile) {
        if (user == null) {
            return null;
        }
        
        UserInfo userInfo = UserConvertor.INSTANCE.mapToVo(user);
        if (profile != null) {
            // User实体中已有nickname和avatar，不需要从profile中获取
            userInfo.setBio(profile.getBio());
            userInfo.setGender(profile.getGender().name()); // 转换为字符串
            userInfo.setBirthday(profile.getBirthday());
            userInfo.setLocation(profile.getLocation());
            // UserProfile中没有website字段，暂时设为null
            userInfo.setWebsite(null);
        }
        
        return userInfo;
    }
}

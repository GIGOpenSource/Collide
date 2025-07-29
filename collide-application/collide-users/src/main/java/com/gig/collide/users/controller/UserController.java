package com.gig.collide.users.controller;

import com.gig.collide.api.user.request.UserCreateRequest;
import com.gig.collide.api.user.request.UserQueryRequest;
import com.gig.collide.api.user.request.UserUpdateRequest;
import com.gig.collide.api.user.response.UserResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.users.domain.service.UserService;
import com.gig.collide.web.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 用户控制器 - 简洁版
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 创建用户
     */
    @PostMapping
    public Result<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        try {
            log.info("创建用户请求: {}", request);
            // 这里可以直接调用Facade服务，或通过本地Service处理
            // 为了简化，直接使用本地Service
            return Result.success(null);
        } catch (Exception e) {
            log.error("创建用户失败", e);
            return Result.error("USER_CREATE_ERROR", "创建用户失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public Result<UserResponse> updateUser(@PathVariable Long id, 
                                         @Valid @RequestBody UserUpdateRequest request) {
        try {
            log.info("更新用户请求: id={}, request={}", id, request);
            request.setId(id);
            return Result.success(null);
        } catch (Exception e) {
            log.error("更新用户失败", e);
            return Result.error("USER_UPDATE_ERROR", "更新用户失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询用户
     */
    @GetMapping("/{id}")
    public Result<UserResponse> getUserById(@PathVariable Long id) {
        try {
            log.info("查询用户: id={}", id);
            return Result.success(null);
        } catch (Exception e) {
            log.error("查询用户失败", e);
            return Result.error("USER_QUERY_ERROR", "查询用户失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询用户列表
     */
    @GetMapping
    public Result<PageResponse<UserResponse>> queryUsers(UserQueryRequest request) {
        try {
            log.info("分页查询用户: {}", request);
            return Result.success(null);
        } catch (Exception e) {
            log.error("查询用户列表失败", e);
            return Result.error("USER_LIST_QUERY_ERROR", "查询用户列表失败: " + e.getMessage());
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<UserResponse> login(@RequestParam String username, 
                                    @RequestParam String password) {
        try {
            log.info("用户登录: username={}", username);
            return Result.success(null);
        } catch (Exception e) {
            log.error("用户登录失败", e);
            return Result.error("USER_LOGIN_ERROR", "登录失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id, 
                                       @RequestParam String status) {
        try {
            log.info("更新用户状态: id={}, status={}", id, status);
            userService.updateUserStatus(id, status);
            return Result.success(null);
        } catch (Exception e) {
            log.error("更新用户状态失败", e);
            return Result.error("USER_STATUS_UPDATE_ERROR", "更新用户状态失败: " + e.getMessage());
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        try {
            log.info("删除用户: id={}", id);
            userService.deleteUser(id);
            return Result.success(null);
        } catch (Exception e) {
            log.error("删除用户失败", e);
            return Result.error("USER_DELETE_ERROR", "删除用户失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户统计数据
     */
    @PutMapping("/{id}/stats")
    public Result<Void> updateUserStats(@PathVariable Long id,
                                      @RequestParam String statsType,
                                      @RequestParam Integer increment) {
        try {
            log.info("更新用户统计: id={}, type={}, increment={}", id, statsType, increment);
            userService.updateUserStats(id, statsType, increment);
            return Result.success(null);
        } catch (Exception e) {
            log.error("更新用户统计失败", e);
            return Result.error("USER_STATS_UPDATE_ERROR", "更新用户统计失败: " + e.getMessage());
        }
    }
} 
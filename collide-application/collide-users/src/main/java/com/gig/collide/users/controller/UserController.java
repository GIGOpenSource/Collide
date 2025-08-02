package com.gig.collide.users.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.gig.collide.api.user.UserFacadeService;
import com.gig.collide.api.user.UserProfileFacadeService;
import com.gig.collide.api.user.UserStatsFacadeService;
import com.gig.collide.api.user.request.users.main.UserCoreCreateRequest;
import com.gig.collide.api.user.request.users.main.UserCoreUpdateRequest;
import com.gig.collide.api.user.request.users.main.UserCoreQueryRequest;
import com.gig.collide.api.user.request.users.profile.UserProfileCreateRequest;
import com.gig.collide.api.user.request.users.profile.UserProfileUpdateRequest;
import com.gig.collide.api.user.request.users.profile.UserProfileQueryRequest;
import com.gig.collide.api.user.request.users.stats.UserStatsCreateRequest;
import com.gig.collide.api.user.request.users.stats.UserStatsUpdateRequest;
import com.gig.collide.api.user.request.users.stats.UserStatsQueryRequest;
import com.gig.collide.api.user.response.users.main.UserCoreResponse;
import com.gig.collide.api.user.response.users.profile.UserProfileResponse;
import com.gig.collide.api.user.response.users.stats.UserStatsResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 用户控制器 - 统一用户管理
 * 提供用户核心信息、个人资料和统计数据的HTTP接口
 * 
 * @author GIG Team
 * @version 2.0.0
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "用户管理", description = "用户核心信息、个人资料和统计数据管理接口")
public class UserController {

    @DubboReference
    private UserFacadeService userFacadeService;

    @DubboReference
    private UserProfileFacadeService userProfileFacadeService;

    @DubboReference
    private UserStatsFacadeService userStatsFacadeService;

    // =================== 用户核心信息管理 ===================

    /**
     * 创建用户
     */
    @PostMapping
    @Operation(summary = "创建用户", description = "创建新用户账户")
    public Result<UserCoreResponse> createUser(@Valid @RequestBody UserCoreCreateRequest request) {
        try {
            log.info("REST创建用户: username={}", request.getUsername());
            return userFacadeService.createUser(request);
        } catch (Exception e) {
            log.error("创建用户异常", e);
            return Result.error("CREATE_USER_ERROR", "创建用户失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户核心信息
     */
    @PutMapping("/{userId}")
    @SaCheckLogin
    @Operation(summary = "更新用户信息", description = "更新用户核心信息")
    public Result<UserCoreResponse> updateUser(
            @PathVariable("userId") @NotNull @Min(1) Long userId,
            @Valid @RequestBody UserCoreUpdateRequest request) {
        try {
            // 检查权限：只能修改自己的信息或管理员操作
            Long currentUserId = StpUtil.getLoginIdAsLong();
            if (!currentUserId.equals(userId) && !StpUtil.hasRole("admin")) {
                return Result.error("PERMISSION_DENIED", "无权限修改其他用户信息");
            }
            
            request.setUserId(userId);
            log.info("REST更新用户信息: userId={}", userId);
            return userFacadeService.updateUser(request);
        } catch (Exception e) {
            log.error("更新用户信息异常", e);
            return Result.error("UPDATE_USER_ERROR", "更新用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询用户
     */
    @GetMapping("/{userId}")
    @Operation(summary = "查询用户信息", description = "根据用户ID查询用户核心信息")
    public Result<UserCoreResponse> getUserById(
            @PathVariable("userId") @NotNull @Min(1) Long userId) {
        try {
            log.debug("REST查询用户: userId={}", userId);
            return userFacadeService.getUserById(userId);
        } catch (Exception e) {
            log.error("查询用户异常", e);
            return Result.error("GET_USER_ERROR", "查询用户失败: " + e.getMessage());
        }
    }

    /**
     * 根据用户名查询用户
     */
    @GetMapping("/username/{username}")
    @Operation(summary = "根据用户名查询", description = "根据用户名查询用户信息")
    public Result<UserCoreResponse> getUserByUsername(
            @PathVariable("username") @NotBlank String username) {
        try {
            log.debug("REST根据用户名查询用户: username={}", username);
            return userFacadeService.getUserByUsername(username);
        } catch (Exception e) {
            log.error("根据用户名查询用户异常", e);
            return Result.error("GET_USER_BY_USERNAME_ERROR", "查询用户失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询用户
     */
    @PostMapping("/query")
    @Operation(summary = "分页查询用户", description = "根据条件分页查询用户列表")
    public Result<PageResponse<UserCoreResponse>> queryUsers(@Valid @RequestBody UserCoreQueryRequest request) {
        try {
            log.debug("REST分页查询用户: page={}, size={}", request.getCurrentPage(), request.getPageSize());
            return userFacadeService.queryUsers(request);
        } catch (Exception e) {
            log.error("分页查询用户异常", e);
            return Result.error("QUERY_USERS_ERROR", "查询用户列表失败: " + e.getMessage());
        }
    }

    /**
     * 批量查询用户
     */
    @PostMapping("/batch")
    @Operation(summary = "批量查询用户", description = "根据用户ID列表批量查询用户信息")
    public Result<List<UserCoreResponse>> batchGetUsers(@RequestBody @NotNull List<Long> userIds) {
        try {
            log.debug("REST批量查询用户: count={}", userIds.size());
            return userFacadeService.batchGetUsers(userIds);
        } catch (Exception e) {
            log.error("批量查询用户异常", e);
            return Result.error("BATCH_GET_USERS_ERROR", "批量查询用户失败: " + e.getMessage());
        }
    }

    /**
     * 检查用户名是否存在
     */
    @GetMapping("/check/username/{username}")
    @Operation(summary = "检查用户名", description = "检查用户名是否已存在")
    public Result<Boolean> checkUsernameExists(@PathVariable("username") @NotBlank String username) {
        try {
            log.debug("REST检查用户名存在性: username={}", username);
            return userFacadeService.checkUsernameExists(username);
        } catch (Exception e) {
            log.error("检查用户名异常", e);
            return Result.error("CHECK_USERNAME_ERROR", "检查用户名失败: " + e.getMessage());
        }
    }

    /**
     * 检查邮箱是否存在
     */
    @GetMapping("/check/email/{email}")
    @Operation(summary = "检查邮箱", description = "检查邮箱是否已存在")
    public Result<Boolean> checkEmailExists(@PathVariable("email") @NotBlank String email) {
        try {
            log.debug("REST检查邮箱存在性: email={}", email);
            return userFacadeService.checkEmailExists(email);
        } catch (Exception e) {
            log.error("检查邮箱异常", e);
            return Result.error("CHECK_EMAIL_ERROR", "检查邮箱失败: " + e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @PutMapping("/{userId}/password")
    @SaCheckLogin
    @Operation(summary = "修改密码", description = "用户修改自己的密码")
    public Result<Void> changePassword(
            @PathVariable("userId") @NotNull @Min(1) Long userId,
            @RequestParam("oldPassword") @NotBlank String oldPassword,
            @RequestParam("newPassword") @NotBlank String newPassword) {
        try {
            // 检查权限：只能修改自己的密码
            Long currentUserId = StpUtil.getLoginIdAsLong();
            if (!currentUserId.equals(userId)) {
                return Result.error("PERMISSION_DENIED", "只能修改自己的密码");
            }
            
            log.info("REST修改密码: userId={}", userId);
            return userFacadeService.changePassword(userId, oldPassword, newPassword);
        } catch (Exception e) {
            log.error("修改密码异常", e);
            return Result.error("CHANGE_PASSWORD_ERROR", "修改密码失败: " + e.getMessage());
        }
    }

    // =================== 用户资料管理 ===================

    /**
     * 创建用户资料
     */
    @PostMapping("/{userId}/profile")
    @SaCheckLogin
    @Operation(summary = "创建用户资料", description = "为用户创建个人资料")
    public Result<UserProfileResponse> createProfile(
            @PathVariable("userId") @NotNull @Min(1) Long userId,
            @Valid @RequestBody UserProfileCreateRequest request) {
        try {
            // 检查权限：只能创建自己的资料或管理员操作
            Long currentUserId = StpUtil.getLoginIdAsLong();
            if (!currentUserId.equals(userId) && !StpUtil.hasRole("admin")) {
                return Result.error("PERMISSION_DENIED", "无权限创建其他用户资料");
            }
            
            request.setUserId(userId);
            log.info("REST创建用户资料: userId={}", userId);
            return userProfileFacadeService.createProfile(request);
        } catch (Exception e) {
            log.error("创建用户资料异常", e);
            return Result.error("CREATE_PROFILE_ERROR", "创建用户资料失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户资料
     */
    @PutMapping("/{userId}/profile")
    @SaCheckLogin
    @Operation(summary = "更新用户资料", description = "更新用户个人资料")
    public Result<UserProfileResponse> updateProfile(
            @PathVariable("userId") @NotNull @Min(1) Long userId,
            @Valid @RequestBody UserProfileUpdateRequest request) {
        try {
            // 检查权限：只能修改自己的资料或管理员操作
            Long currentUserId = StpUtil.getLoginIdAsLong();
            if (!currentUserId.equals(userId) && !StpUtil.hasRole("admin")) {
                return Result.error("PERMISSION_DENIED", "无权限修改其他用户资料");
            }
            
            request.setUserId(userId);
            log.info("REST更新用户资料: userId={}", userId);
            return userProfileFacadeService.updateProfile(request);
        } catch (Exception e) {
            log.error("更新用户资料异常", e);
            return Result.error("UPDATE_PROFILE_ERROR", "更新用户资料失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户资料
     */
    @GetMapping("/{userId}/profile")
    @Operation(summary = "获取用户资料", description = "根据用户ID获取个人资料")
    public Result<UserProfileResponse> getProfile(@PathVariable("userId") @NotNull @Min(1) Long userId) {
        try {
            log.debug("REST获取用户资料: userId={}", userId);
            return userProfileFacadeService.getProfileByUserId(userId);
        } catch (Exception e) {
            log.error("获取用户资料异常", e);
            return Result.error("GET_PROFILE_ERROR", "获取用户资料失败: " + e.getMessage());
        }
    }

    /**
     * 更新头像
     */
    @PutMapping("/{userId}/profile/avatar")
    @SaCheckLogin
    @Operation(summary = "更新头像", description = "更新用户头像")
    public Result<Void> updateAvatar(
            @PathVariable("userId") @NotNull @Min(1) Long userId,
            @RequestParam("avatarUrl") @NotBlank String avatarUrl) {
        try {
            // 检查权限：只能修改自己的头像或管理员操作
            Long currentUserId = StpUtil.getLoginIdAsLong();
            if (!currentUserId.equals(userId) && !StpUtil.hasRole("admin")) {
                return Result.error("PERMISSION_DENIED", "无权限修改其他用户头像");
            }
            
            log.info("REST更新用户头像: userId={}", userId);
            return userProfileFacadeService.updateAvatar(userId, avatarUrl);
        } catch (Exception e) {
            log.error("更新头像异常", e);
            return Result.error("UPDATE_AVATAR_ERROR", "更新头像失败: " + e.getMessage());
        }
    }

    /**
     * 更新昵称
     */
    @PutMapping("/{userId}/profile/nickname")
    @SaCheckLogin
    @Operation(summary = "更新昵称", description = "更新用户昵称")
    public Result<Void> updateNickname(
            @PathVariable("userId") @NotNull @Min(1) Long userId,
            @RequestParam("nickname") @NotBlank String nickname) {
        try {
            // 检查权限：只能修改自己的昵称或管理员操作
            Long currentUserId = StpUtil.getLoginIdAsLong();
            if (!currentUserId.equals(userId) && !StpUtil.hasRole("admin")) {
                return Result.error("PERMISSION_DENIED", "无权限修改其他用户昵称");
            }
            
            log.info("REST更新用户昵称: userId={}, nickname={}", userId, nickname);
            return userProfileFacadeService.updateNickname(userId, nickname);
        } catch (Exception e) {
            log.error("更新昵称异常", e);
            return Result.error("UPDATE_NICKNAME_ERROR", "更新昵称失败: " + e.getMessage());
        }
    }

    /**
     * 搜索用户资料
     */
    @GetMapping("/profiles/search")
    @Operation(summary = "搜索用户资料", description = "根据昵称关键词搜索用户资料")
    public Result<PageResponse<UserProfileResponse>> searchProfiles(
            @RequestParam("keyword") @NotBlank String keyword,
            @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        try {
            log.debug("REST搜索用户资料: keyword={}, page={}", keyword, currentPage);
            return userProfileFacadeService.searchByNickname(keyword, currentPage, pageSize);
        } catch (Exception e) {
            log.error("搜索用户资料异常", e);
            return Result.error("SEARCH_PROFILES_ERROR", "搜索用户资料失败: " + e.getMessage());
        }
    }

    // =================== 用户统计管理 ===================

    /**
     * 获取用户统计数据
     */
    @GetMapping("/{userId}/stats")
    @Operation(summary = "获取统计数据", description = "获取用户统计数据")
    public Result<UserStatsResponse> getStats(@PathVariable("userId") @NotNull @Min(1) Long userId) {
        try {
            log.debug("REST获取用户统计: userId={}", userId);
            return userStatsFacadeService.getStatsByUserId(userId);
        } catch (Exception e) {
            log.error("获取用户统计异常", e);
            return Result.error("GET_STATS_ERROR", "获取统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取粉丝排行榜
     */
    @GetMapping("/ranking/followers")
    @Operation(summary = "粉丝排行榜", description = "获取粉丝数排行榜")
    public Result<List<UserStatsResponse>> getFollowerRanking(
            @RequestParam(value = "limit", defaultValue = "10") @Min(1) Integer limit) {
        try {
            log.debug("REST获取粉丝排行榜: limit={}", limit);
            return userStatsFacadeService.getFollowerRanking(limit);
        } catch (Exception e) {
            log.error("获取粉丝排行榜异常", e);
            return Result.error("GET_FOLLOWER_RANKING_ERROR", "获取粉丝排行榜失败: " + e.getMessage());
        }
    }

    /**
     * 获取内容排行榜
     */
    @GetMapping("/ranking/content")
    @Operation(summary = "内容排行榜", description = "获取内容数排行榜")
    public Result<List<UserStatsResponse>> getContentRanking(
            @RequestParam(value = "limit", defaultValue = "10") @Min(1) Integer limit) {
        try {
            log.debug("REST获取内容排行榜: limit={}", limit);
            return userStatsFacadeService.getContentRanking(limit);
        } catch (Exception e) {
            log.error("获取内容排行榜异常", e);
            return Result.error("GET_CONTENT_RANKING_ERROR", "获取内容排行榜失败: " + e.getMessage());
        }
    }

    /**
     * 获取平台统计数据
     */
    @GetMapping("/platform/stats")
    @Operation(summary = "平台统计", description = "获取平台总体统计数据")
    public Result<Map<String, Object>> getPlatformStats() {
        try {
            log.debug("REST获取平台统计数据");
            return userStatsFacadeService.getPlatformStats();
        } catch (Exception e) {
            log.error("获取平台统计异常", e);
            return Result.error("GET_PLATFORM_STATS_ERROR", "获取平台统计失败: " + e.getMessage());
        }
    }

    // =================== 当前用户相关接口 ===================

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    @SaCheckLogin
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的完整信息")
    public Result<Map<String, Object>> getCurrentUserInfo() {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            log.debug("REST获取当前用户信息: userId={}", userId);
            
            // 获取用户核心信息
            Result<UserCoreResponse> userResult = userFacadeService.getUserById(userId);
            if (!userResult.getSuccess()) {
                return Result.error(userResult.getCode(), userResult.getMessage());
            }
            
            // 获取用户资料
            Result<UserProfileResponse> profileResult = userProfileFacadeService.getProfileByUserId(userId);
            
            // 获取用户统计
            Result<UserStatsResponse> statsResult = userStatsFacadeService.getStatsByUserId(userId);
            
            // 组装返回数据
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("user", userResult.getData());
            result.put("profile", profileResult.getSuccess() ? profileResult.getData() : null);
            result.put("stats", statsResult.getSuccess() ? statsResult.getData() : null);
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取当前用户信息异常", e);
            return Result.error("GET_CURRENT_USER_ERROR", "获取当前用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 更新当前用户资料
     */
    @PutMapping("/me/profile")
    @SaCheckLogin
    @Operation(summary = "更新个人资料", description = "更新当前用户的个人资料")
    public Result<UserProfileResponse> updateMyProfile(@Valid @RequestBody UserProfileUpdateRequest request) {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            request.setUserId(userId);
            log.info("REST更新个人资料: userId={}", userId);
            return userProfileFacadeService.updateProfile(request);
        } catch (Exception e) {
            log.error("更新个人资料异常", e);
            return Result.error("UPDATE_MY_PROFILE_ERROR", "更新个人资料失败: " + e.getMessage());
        }
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查用户服务状态")
    public Result<String> health() {
        return Result.success("User Service v2.0 - 运行正常");
    }
}
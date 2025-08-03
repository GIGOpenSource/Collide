package com.gig.collide.users.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.gig.collide.api.user.UserBlockFacadeService;
import com.gig.collide.api.user.UserRoleFacadeService;
import com.gig.collide.api.user.UserFacadeService;
import com.gig.collide.api.user.request.block.UserBlockCreateRequest;
import com.gig.collide.api.user.request.block.UserBlockQueryRequest;
import com.gig.collide.api.user.request.role.UserRoleCreateRequest;
import com.gig.collide.api.user.request.role.UserRoleUpdateRequest;
import com.gig.collide.api.user.request.role.UserRoleQueryRequest;
import com.gig.collide.api.user.response.block.UserBlockResponse;
import com.gig.collide.api.user.response.role.UserRoleResponse;
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

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户管理控制器
 * 提供用户管理相关的HTTP接口（管理员功能）
 * 包括用户拉黑、角色管理、状态管理等
 * 
 * @author GIG Team
 * @version 2.0.0
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Validated
@SaCheckLogin
@SaCheckRole("admin")
@Tag(name = "用户管理", description = "用户管理相关接口（管理员功能）")
public class UserAdminController {

    @DubboReference
    private UserFacadeService userFacadeService;

    @DubboReference
    private UserBlockFacadeService userBlockFacadeService;

    @DubboReference
    private UserRoleFacadeService userRoleFacadeService;

    // =================== 用户状态管理 ===================

    /**
     * 更新用户状态
     */
    @PutMapping("/{userId}/status")
    @Operation(summary = "更新用户状态", description = "管理员更新用户状态（正常/暂停/封禁等）")
    public Result<Void> updateUserStatus(
            @PathVariable("userId") @NotNull @Min(1) Long userId,
            @RequestParam("status") @NotNull Integer status,
            @RequestParam(value = "reason", required = false) String reason) {
        try {
            Long operatorId = StpUtil.getLoginIdAsLong();
            log.info("ADMIN更新用户状态: userId={}, status={}, operatorId={}", userId, status, operatorId);
            return userFacadeService.updateUserStatus(userId, status);
        } catch (Exception e) {
            log.error("更新用户状态异常", e);
            return Result.error("UPDATE_USER_STATUS_ERROR", "更新用户状态失败: " + e.getMessage());
        }
    }

    /**
     * 重置用户密码
     */
    @PutMapping("/{userId}/password/reset")
    @Operation(summary = "重置密码", description = "管理员重置用户密码")
    public Result<Void> resetPassword(
            @PathVariable("userId") @NotNull @Min(1) Long userId,
            @RequestParam("newPassword") @NotBlank String newPassword) {
        try {
            Long operatorId = StpUtil.getLoginIdAsLong();
            log.info("ADMIN重置用户密码: userId={}, operatorId={}", userId, operatorId);
            return userFacadeService.resetPassword(userId, newPassword);
        } catch (Exception e) {
            log.error("重置用户密码异常", e);
            return Result.error("RESET_PASSWORD_ERROR", "重置密码失败: " + e.getMessage());
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{userId}")
    @Operation(summary = "删除用户", description = "管理员删除用户（物理删除，谨慎使用）")
    public Result<Void> deleteUser(@PathVariable("userId") @NotNull @Min(1) Long userId) {
        try {
            Long operatorId = StpUtil.getLoginIdAsLong();
            log.warn("ADMIN删除用户: userId={}, operatorId={}", userId, operatorId);
            return userFacadeService.deleteUser(userId);
        } catch (Exception e) {
            log.error("删除用户异常", e);
            return Result.error("DELETE_USER_ERROR", "删除用户失败: " + e.getMessage());
        }
    }

    // =================== 用户拉黑管理 ===================

    /**
     * 拉黑用户
     */
    @PostMapping("/{userId}/block")
    @Operation(summary = "拉黑用户", description = "管理员拉黑用户")
    public Result<UserBlockResponse> blockUser(
            @PathVariable("userId") @NotNull @Min(1) Long userId,
            @Valid @RequestBody UserBlockCreateRequest request) {
        try {
            Long operatorId = StpUtil.getLoginIdAsLong();
            request.setUserId(operatorId); // 操作者ID
            request.setBlockedUserId(userId); // 被拉黑用户ID
            
            log.info("ADMIN拉黑用户: userId={}, blockedUserId={}, operatorId={}", 
                    request.getUserId(), userId, operatorId);
            return userBlockFacadeService.blockUser(request);
        } catch (Exception e) {
            log.error("拉黑用户异常", e);
            return Result.error("BLOCK_USER_ERROR", "拉黑用户失败: " + e.getMessage());
        }
    }

    /**
     * 取消拉黑
     */
    @DeleteMapping("/{userId}/block/{blockedUserId}")
    @Operation(summary = "取消拉黑", description = "管理员取消用户拉黑")
    public Result<Void> unblockUser(
            @PathVariable("userId") @NotNull @Min(1) Long userId,
            @PathVariable("blockedUserId") @NotNull @Min(1) Long blockedUserId) {
        try {
            Long operatorId = StpUtil.getLoginIdAsLong();
            log.info("ADMIN取消拉黑: userId={}, blockedUserId={}, operatorId={}", 
                    userId, blockedUserId, operatorId);
            return userBlockFacadeService.unblockUser(userId, blockedUserId);
        } catch (Exception e) {
            log.error("取消拉黑异常", e);
            return Result.error("UNBLOCK_USER_ERROR", "取消拉黑失败: " + e.getMessage());
        }
    }

    /**
     * 查询拉黑记录
     */
    @PostMapping("/blocks/query")
    @Operation(summary = "查询拉黑记录", description = "管理员查询用户拉黑记录")
    public Result<PageResponse<UserBlockResponse>> queryBlocks(@Valid @RequestBody UserBlockQueryRequest request) {
        try {
            log.debug("ADMIN查询拉黑记录: page={}, size={}", request.getCurrentPage(), request.getPageSize());
            return userBlockFacadeService.queryBlocks(request);
        } catch (Exception e) {
            log.error("查询拉黑记录异常", e);
            return Result.error("QUERY_BLOCKS_ERROR", "查询拉黑记录失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户拉黑列表
     */
    @GetMapping("/{userId}/blocks")
    @Operation(summary = "获取拉黑列表", description = "获取用户的拉黑列表")
    public Result<PageResponse<UserBlockResponse>> getUserBlocks(
            @PathVariable("userId") @NotNull @Min(1) Long userId,
            @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        try {
            log.debug("ADMIN获取用户拉黑列表: userId={}, page={}", userId, currentPage);
            return userBlockFacadeService.getUserBlockList(userId, currentPage, pageSize);
        } catch (Exception e) {
            log.error("获取用户拉黑列表异常", e);
            return Result.error("GET_USER_BLOCKS_ERROR", "获取拉黑列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取被拉黑列表
     */
    @GetMapping("/{userId}/blocked")
    @Operation(summary = "获取被拉黑列表", description = "获取用户的被拉黑列表")
    public Result<PageResponse<UserBlockResponse>> getUserBlocked(
            @PathVariable("userId") @NotNull @Min(1) Long userId,
            @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        try {
            log.debug("ADMIN获取用户被拉黑列表: userId={}, page={}", userId, currentPage);
            return userBlockFacadeService.getUserBlockedList(userId, currentPage, pageSize);
        } catch (Exception e) {
            log.error("获取用户被拉黑列表异常", e);
            return Result.error("GET_USER_BLOCKED_ERROR", "获取被拉黑列表失败: " + e.getMessage());
        }
    }

    // =================== 用户角色管理 ===================

    /**
     * 分配用户角色
     */
    @PostMapping("/{userId}/roles")
    @Operation(summary = "分配用户角色", description = "管理员为用户分配角色")
    public Result<UserRoleResponse> assignRole(
            @PathVariable("userId") @NotNull @Min(1) Long userId,
            @Valid @RequestBody UserRoleCreateRequest request) {
        try {
            Long operatorId = StpUtil.getLoginIdAsLong();
            request.setUserId(userId);
            // 注意：assignBy字段不在Request中，而是在Service层处理
            
            log.info("ADMIN分配用户角色: userId={}, role={}, operatorId={}", 
                    userId, request.getRole(), operatorId);
            return userRoleFacadeService.assignRole(request);
        } catch (Exception e) {
            log.error("分配用户角色异常", e);
            return Result.error("ASSIGN_ROLE_ERROR", "分配角色失败: " + e.getMessage());
        }
    }

    /**
     * 撤销用户角色
     */
    @DeleteMapping("/{userId}/roles/{role}")
    @Operation(summary = "撤销用户角色", description = "管理员撤销用户角色")
    public Result<Void> revokeRole(
            @PathVariable("userId") @NotNull @Min(1) Long userId,
            @PathVariable("role") @NotBlank String role,
            @RequestParam(value = "reason", required = false) String reason) {
        try {
            Long operatorId = StpUtil.getLoginIdAsLong();
            log.info("ADMIN撤销用户角色: userId={}, role={}, operatorId={}", 
                    userId, role, operatorId);
            return userRoleFacadeService.revokeRole(userId, role);
        } catch (Exception e) {
            log.error("撤销用户角色异常", e);
            return Result.error("REVOKE_ROLE_ERROR", "撤销角色失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户角色
     */
    @PutMapping("/{userId}/roles/{roleId}")
    @Operation(summary = "更新用户角色", description = "管理员更新用户角色信息")
    public Result<UserRoleResponse> updateRole(
            @PathVariable("userId") @NotNull @Min(1) Long userId,
            @PathVariable("roleId") @NotNull @Min(1) Integer roleId,
            @Valid @RequestBody UserRoleUpdateRequest request) {
        try {
            Long operatorId = StpUtil.getLoginIdAsLong();
            request.setUserId(userId);
            request.setId(roleId);
            
            log.info("ADMIN更新用户角色: userId={}, roleId={}, operatorId={}", 
                    userId, roleId, operatorId);
            return userRoleFacadeService.updateRole(request);
        } catch (Exception e) {
            log.error("更新用户角色异常", e);
            return Result.error("UPDATE_ROLE_ERROR", "更新角色失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户角色列表
     */
    @GetMapping("/{userId}/roles")
    @Operation(summary = "获取用户角色", description = "获取用户的角色列表")
    public Result<List<UserRoleResponse>> getUserRoles(@PathVariable("userId") @NotNull @Min(1) Long userId) {
        try {
            log.debug("ADMIN获取用户角色: userId={}", userId);
            return userRoleFacadeService.getRolesByUserId(userId);
        } catch (Exception e) {
            log.error("获取用户角色异常", e);
            return Result.error("GET_USER_ROLES_ERROR", "获取用户角色失败: " + e.getMessage());
        }
    }

    /**
     * 查询角色记录
     */
    @PostMapping("/roles/query")
    @Operation(summary = "查询角色记录", description = "管理员查询用户角色记录")
    public Result<PageResponse<UserRoleResponse>> queryRoles(@Valid @RequestBody UserRoleQueryRequest request) {
        try {
            log.debug("ADMIN查询角色记录: page={}, size={}", request.getCurrentPage(), request.getPageSize());
            return userRoleFacadeService.queryRoles(request);
        } catch (Exception e) {
            log.error("查询角色记录异常", e);
            return Result.error("QUERY_ROLES_ERROR", "查询角色记录失败: " + e.getMessage());
        }
    }

    /**
     * 批量分配角色
     */
    @PostMapping("/roles/batch-assign")
    @Operation(summary = "批量分配角色", description = "管理员批量为用户分配角色")
    public Result<Integer> batchAssignRole(
            @RequestParam("userIds") @NotNull List<Long> userIds,
            @RequestParam("role") @NotBlank String role,
            @RequestParam(value = "expireTime", required = false) 
            @Parameter(description = "过期时间，格式：yyyy-MM-dd HH:mm:ss") String expireTime) {
        try {
            Long operatorId = StpUtil.getLoginIdAsLong();
            log.info("ADMIN批量分配角色: userCount={}, role={}, operatorId={}", 
                    userIds.size(), role, operatorId);
            
            LocalDateTime expiry = null;
            if (expireTime != null && !expireTime.isEmpty()) {
                expiry = LocalDateTime.parse(expireTime.replace(" ", "T"));
            }
            
            return userRoleFacadeService.batchAssignRole(userIds, role, expiry);
        } catch (Exception e) {
            log.error("批量分配角色异常", e);
            return Result.error("BATCH_ASSIGN_ROLE_ERROR", "批量分配角色失败: " + e.getMessage());
        }
    }

    /**
     * 批量撤销角色
     */
    @PostMapping("/roles/batch-revoke")
    @Operation(summary = "批量撤销角色", description = "管理员批量撤销用户角色")
    public Result<Integer> batchRevokeRole(
            @RequestParam("userIds") @NotNull List<Long> userIds,
            @RequestParam("role") @NotBlank String role) {
        try {
            Long operatorId = StpUtil.getLoginIdAsLong();
            log.info("ADMIN批量撤销角色: userCount={}, role={}, operatorId={}", 
                    userIds.size(), role, operatorId);
            return userRoleFacadeService.batchRevokeRole(userIds, role);
        } catch (Exception e) {
            log.error("批量撤销角色异常", e);
            return Result.error("BATCH_REVOKE_ROLE_ERROR", "批量撤销角色失败: " + e.getMessage());
        }
    }

    // =================== 统计和监控 ===================

    /**
     * 清理已取消的拉黑记录
     */
    @DeleteMapping("/blocks/cleanup")
    @Operation(summary = "清理拉黑记录", description = "清理已取消的拉黑记录")
    public Result<Integer> cleanCancelledBlocks(
            @RequestParam(value = "days", defaultValue = "30") Integer days) {
        try {
            log.info("ADMIN清理拉黑记录: days={}", days);
            return userBlockFacadeService.cleanCancelledBlocks(days);
        } catch (Exception e) {
            log.error("清理拉黑记录异常", e);
            return Result.error("CLEAN_BLOCKS_ERROR", "清理拉黑记录失败: " + e.getMessage());
        }
    }

    /**
     * 获取角色统计
     */
    @GetMapping("/roles/statistics")
    @Operation(summary = "角色统计", description = "获取用户角色统计信息")
    public Result<java.util.Map<String, Object>> getRoleStatistics() {
        try {
            log.debug("ADMIN获取角色统计");
            return userRoleFacadeService.getRoleStatistics();
        } catch (Exception e) {
            log.error("获取角色统计异常", e);
            return Result.error("GET_ROLE_STATISTICS_ERROR", "获取角色统计失败: " + e.getMessage());
        }
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    @Operation(summary = "管理服务健康检查", description = "检查用户管理服务状态")
    public Result<String> health() {
        return Result.success("User Admin Service v2.0 - 运行正常");
    }
}
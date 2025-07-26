package com.gig.collide.admin.controller;

import com.gig.collide.admin.service.AdminSystemService;
import com.gig.collide.web.vo.Result;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

/**
 * 管理后台 - 系统管理控制器
 * 
 * 提供管理后台的系统级功能：
 * - 管理员登录认证
 * - 系统监控和健康检查
 * - 配置管理
 * - 日志管理
 * - 缓存管理
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/system")
@RequiredArgsConstructor
@Tag(name = "管理后台-系统管理", description = "系统管理和监控接口")
public class AdminSystemController {
    
    private final AdminSystemService adminSystemService;
    
    /**
     * 管理员登录
     */
    @PostMapping("/login")
    @Operation(summary = "管理员登录", description = "管理员用户名密码登录")
    public Result<Map<String, Object>> login(
            @Parameter(description = "登录请求", required = true)
            @Valid @RequestBody Map<String, String> loginRequest) {
        
        try {
            String username = loginRequest.get("username");
            String password = loginRequest.get("password");
            String captcha = loginRequest.get("captcha");
            
            log.info("[管理后台] 管理员登录，用户名：{}", username);
            
            Map<String, Object> result = adminSystemService.login(username, password, captcha);
            
            log.info("[管理后台] 管理员登录成功，用户名：{}", username);
            return Result.success(result);
            
        } catch (IllegalArgumentException e) {
            log.warn("[管理后台] 管理员登录失败：{}", e.getMessage());
            return Result.error("LOGIN_FAILED", e.getMessage());
        } catch (Exception e) {
            log.error("[管理后台] 管理员登录异常", e);
            return Result.error("LOGIN_ERROR", "登录失败，请稍后重试");
        }
    }
    
    /**
     * 管理员退出登录
     */
    @PostMapping("/logout")
    @Operation(summary = "管理员退出", description = "管理员退出登录")
    public Result<Void> logout() {
        
        try {
            adminSystemService.logout();
            
            log.info("[管理后台] 管理员退出成功");
            return Result.success(null);
            
        } catch (Exception e) {
            log.error("[管理后台] 管理员退出异常", e);
            return Result.error("LOGOUT_ERROR", "退出失败");
        }
    }
    
    /**
     * 获取当前登录管理员信息
     */
    @GetMapping("/current-user")
    @Operation(summary = "获取当前用户", description = "获取当前登录的管理员信息")
    public Result<Map<String, Object>> getCurrentUser() {
        
        try {
            Map<String, Object> userInfo = adminSystemService.getCurrentUser();
            
            return Result.success(userInfo);
            
        } catch (Exception e) {
            log.error("[管理后台] 获取当前用户信息失败", e);
            return Result.error("GET_USER_ERROR", "获取用户信息失败");
        }
    }
    
    /**
     * 系统健康检查
     */
    @GetMapping("/health")
    @Operation(summary = "系统健康检查", description = "检查系统各组件运行状态")
    public Result<Map<String, Object>> healthCheck() {
        
        try {
            Map<String, Object> healthStatus = adminSystemService.getSystemHealth();
            
            return Result.success(healthStatus);
            
        } catch (Exception e) {
            log.error("[管理后台] 系统健康检查失败", e);
            return Result.error("HEALTH_CHECK_ERROR", "健康检查失败");
        }
    }
    
    /**
     * 系统监控信息
     */
    @GetMapping("/monitor")
    @Operation(summary = "系统监控", description = "获取系统运行监控信息")
    public Result<Map<String, Object>> getSystemMonitor() {
        
        try {
            Map<String, Object> monitorData = adminSystemService.getSystemMonitor();
            
            return Result.success(monitorData);
            
        } catch (Exception e) {
            log.error("[管理后台] 获取系统监控信息失败", e);
            return Result.error("MONITOR_ERROR", "获取监控信息失败");
        }
    }
    
    /**
     * 获取系统配置
     */
    @GetMapping("/config")
    @Operation(summary = "获取系统配置", description = "获取系统配置信息")
    public Result<Map<String, Object>> getSystemConfig() {
        
        try {
            Map<String, Object> config = adminSystemService.getSystemConfig();
            
            return Result.success(config);
            
        } catch (Exception e) {
            log.error("[管理后台] 获取系统配置失败", e);
            return Result.error("GET_CONFIG_ERROR", "获取系统配置失败");
        }
    }
    
    /**
     * 更新系统配置
     */
    @PostMapping("/config")
    @Operation(summary = "更新系统配置", description = "更新系统配置信息")
    public Result<Void> updateSystemConfig(
            @Parameter(description = "配置更新请求", required = true)
            @Valid @RequestBody Map<String, Object> configRequest) {
        
        try {
            log.info("[管理后台] 更新系统配置，配置项数量：{}", configRequest.size());
            
            adminSystemService.updateSystemConfig(configRequest);
            
            log.info("[管理后台] 系统配置更新成功");
            return Result.success(null);
            
        } catch (Exception e) {
            log.error("[管理后台] 更新系统配置失败", e);
            return Result.error("UPDATE_CONFIG_ERROR", "更新配置失败：" + e.getMessage());
        }
    }
    
    /**
     * 缓存管理
     */
    @PostMapping("/cache/{action}")
    @Operation(summary = "缓存管理", description = "清空、刷新缓存")
    public Result<Map<String, Object>> manageCache(
            @Parameter(description = "缓存操作", required = true)
            @PathVariable String action,
            
            @Parameter(description = "缓存名称（可选）")
            @RequestParam(required = false) String cacheName) {
        
        try {
            log.info("[管理后台] 缓存管理，操作：{}，缓存名：{}", action, cacheName);
            
            Map<String, Object> result = adminSystemService.manageCache(action, cacheName);
            
            log.info("[管理后台] 缓存操作完成，操作：{}", action);
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("[管理后台] 缓存管理失败，操作：{}", action, e);
            return Result.error("CACHE_MANAGE_ERROR", "缓存操作失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取操作日志
     */
    @GetMapping("/logs")
    @Operation(summary = "获取操作日志", description = "获取管理员操作日志")
    public Result<Map<String, Object>> getOperationLogs(
            @Parameter(description = "页码", example = "1") 
            @RequestParam(defaultValue = "1") Integer pageNo,
            
            @Parameter(description = "每页大小", example = "20") 
            @RequestParam(defaultValue = "20") Integer pageSize,
            
            @Parameter(description = "操作模块") 
            @RequestParam(required = false) String module,
            
            @Parameter(description = "操作类型") 
            @RequestParam(required = false) String operation,
            
            @Parameter(description = "操作用户") 
            @RequestParam(required = false) String operator,
            
            @Parameter(description = "开始时间") 
            @RequestParam(required = false) String startTime,
            
            @Parameter(description = "结束时间") 
            @RequestParam(required = false) String endTime) {
        
        try {
            log.info("[管理后台] 获取操作日志，页码：{}，模块：{}，操作：{}", pageNo, module, operation);
            
            Map<String, Object> queryParams = Map.of(
                "pageNo", pageNo,
                "pageSize", pageSize,
                "module", module != null ? module : "",
                "operation", operation != null ? operation : "",
                "operator", operator != null ? operator : "",
                "startTime", startTime != null ? startTime : "",
                "endTime", endTime != null ? endTime : ""
            );
            
            Map<String, Object> result = adminSystemService.getOperationLogs(queryParams);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("[管理后台] 获取操作日志失败", e);
            return Result.error("GET_LOGS_ERROR", "获取操作日志失败：" + e.getMessage());
        }
    }
    
    /**
     * 数据库备份
     */
    @PostMapping("/backup")
    @Operation(summary = "数据库备份", description = "创建数据库备份")
    public Result<Map<String, String>> createBackup(
            @Parameter(description = "备份名称") 
            @RequestParam(required = false) String backupName,
            
            @Parameter(description = "备份描述") 
            @RequestParam(required = false) String description) {
        
        try {
            log.info("[管理后台] 创建数据库备份，名称：{}，描述：{}", backupName, description);
            
            Map<String, String> result = adminSystemService.createBackup(backupName, description);
            
            log.info("[管理后台] 数据库备份创建成功，文件：{}", result.get("backupFile"));
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("[管理后台] 创建数据库备份失败", e);
            return Result.error("BACKUP_ERROR", "创建备份失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取备份列表
     */
    @GetMapping("/backups")
    @Operation(summary = "获取备份列表", description = "获取数据库备份文件列表")
    public Result<java.util.List<Map<String, Object>>> getBackupList() {
        
        try {
            java.util.List<Map<String, Object>> backups = adminSystemService.getBackupList();
            
            return Result.success(backups);
            
        } catch (Exception e) {
            log.error("[管理后台] 获取备份列表失败", e);
            return Result.error("GET_BACKUPS_ERROR", "获取备份列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 系统统计仪表板
     */
    @GetMapping("/dashboard")
    @Operation(summary = "系统仪表板", description = "获取系统总体统计信息")
    public Result<Map<String, Object>> getDashboard() {
        
        try {
            Map<String, Object> dashboard = adminSystemService.getDashboardData();
            
            return Result.success(dashboard);
            
        } catch (Exception e) {
            log.error("[管理后台] 获取仪表板数据失败", e);
            return Result.error("DASHBOARD_ERROR", "获取仪表板数据失败：" + e.getMessage());
        }
    }
    
    /**
     * 生成验证码
     */
    @GetMapping("/captcha")
    @Operation(summary = "生成验证码", description = "生成登录验证码")
    public Result<Map<String, String>> generateCaptcha() {
        
        try {
            Map<String, String> captcha = adminSystemService.generateCaptcha();
            
            return Result.success(captcha);
            
        } catch (Exception e) {
            log.error("[管理后台] 生成验证码失败", e);
            return Result.error("CAPTCHA_ERROR", "生成验证码失败");
        }
    }
} 
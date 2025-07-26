package com.gig.collide.admin.service;

import java.util.List;
import java.util.Map;

/**
 * 管理后台 - 系统服务接口
 * 
 * 提供系统级功能：
 * - 管理员认证
 * - 系统监控
 * - 配置管理
 * - 缓存管理  
 * - 日志管理
 * - 数据备份
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
public interface AdminSystemService {
    
    /**
     * 管理员登录
     */
    Map<String, Object> login(String username, String password, String captcha);
    
    /**
     * 管理员退出登录
     */
    void logout();
    
    /**
     * 获取当前登录管理员信息
     */
    Map<String, Object> getCurrentUser();
    
    /**
     * 系统健康检查
     */
    Map<String, Object> getSystemHealth();
    
    /**
     * 获取系统监控信息
     */
    Map<String, Object> getSystemMonitor();
    
    /**
     * 获取系统配置
     */
    Map<String, Object> getSystemConfig();
    
    /**
     * 更新系统配置
     */
    void updateSystemConfig(Map<String, Object> configRequest);
    
    /**
     * 缓存管理
     */
    Map<String, Object> manageCache(String action, String cacheName);
    
    /**
     * 获取操作日志
     */
    Map<String, Object> getOperationLogs(Map<String, Object> queryParams);
    
    /**
     * 创建数据库备份
     */
    Map<String, String> createBackup(String backupName, String description);
    
    /**
     * 获取备份列表
     */
    List<Map<String, Object>> getBackupList();
    
    /**
     * 获取系统仪表板数据
     */
    Map<String, Object> getDashboardData();
    
    /**
     * 生成验证码
     */
    Map<String, String> generateCaptcha();
}
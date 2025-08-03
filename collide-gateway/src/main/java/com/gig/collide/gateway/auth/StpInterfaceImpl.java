package com.gig.collide.gateway.auth;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.gig.collide.api.user.constant.UserStatusConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 自定义权限验证接口扩展 - 简洁版
 * Sa-Token 获取权限信息接口的实现类
 * 基于简洁版用户API重构
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Slf4j
@Component
public class StpInterfaceImpl implements StpInterface {
    
    /**
     * 返回一个账号所拥有的权限码集合 
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        List<String> permissions = new ArrayList<>();
        
        try {
            // 从Session中获取用户信息
            Object userInfoObj = StpUtil.getSessionByLoginId(loginId).get("userInfo");
            
            if (userInfoObj == null) {
                log.warn("用户 {} 的权限信息未找到，返回基础权限", loginId);
                permissions.add("basic");
                return permissions;
            }

            // 基于简洁版用户结构获取权限
            if (userInfoObj instanceof Map<?, ?> userInfo) {
                String role = (String) userInfo.get("role");
                String status = (String) userInfo.get("status");
                
                // 检查用户状态
                if (!UserStatusConstant.isValidStatusString(status)) {
                    log.warn("用户 {} 状态异常: {}", loginId, status);
                    return permissions; // 返回空权限列表
                }
                
                // 基础权限：所有活跃用户都有
                permissions.add("basic");
                
                // 基于角色分配权限
                switch (role != null ? role : "user") {
                    case "admin":
                        permissions.addAll(Arrays.asList("admin", "blogger", "vip", "user_manage", "content_manage"));
                        break;
                    case "blogger":
                        permissions.addAll(Arrays.asList("blogger", "content_create", "content_manage"));
                        break;
                    case "vip":
                        permissions.add("vip");
                        break;
                    case "user":
                    default:
                        // 普通用户只有基础权限
                        break;
                }
                
                log.debug("用户 {} (角色: {}) 的权限列表：{}", loginId, role, permissions);
            }
            
        } catch (Exception e) {
            log.error("获取用户 {} 权限信息时发生异常：", loginId, e);
            // 异常情况下给予基础权限
            permissions.add("basic");
        }
        
        return permissions;
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        List<String> roles = new ArrayList<>();
        
        try {
            // 从Session中获取用户角色信息
            Object userInfoObj = StpUtil.getSessionByLoginId(loginId).get("userInfo");
            
            if (userInfoObj == null) {
                log.warn("用户 {} 的角色信息未找到，返回默认角色", loginId);
                roles.add("user");
                return roles;
            }

            // 基于简洁版用户结构获取角色
            if (userInfoObj instanceof Map<?, ?> userInfo) {
                String role = (String) userInfo.get("role");
                String status = (String) userInfo.get("status");
                
                // 检查用户状态
                if (!UserStatusConstant.isValidStatusString(status)) {
                    log.warn("用户 {} 状态异常: {}，不分配任何角色", loginId, status);
                    return roles; // 返回空角色列表
                }
                
                // 设置主要角色
                String userRole = role != null ? role : "user";
                roles.add(userRole);
                
                // 角色继承：高级角色包含低级角色
                switch (userRole) {
                    case "admin":
                        roles.addAll(Arrays.asList("blogger", "vip", "user"));
                        break;
                    case "blogger":
                        roles.add("user");
                        break;
                    case "vip":
                        roles.add("user");
                        break;
                    case "user":
                    default:
                        // 普通用户只有user角色
                        break;
                }
                
                log.debug("用户 {} (主角色: {}) 的角色列表：{}", loginId, userRole, roles);
            }
            
        } catch (Exception e) {
            log.error("获取用户 {} 角色信息时发生异常：", loginId, e);
            // 异常情况下给予默认角色
            roles.add("user");
        }
        
        return roles;
    }
}

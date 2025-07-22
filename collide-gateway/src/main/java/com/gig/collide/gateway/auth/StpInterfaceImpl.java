package com.gig.collide.gateway.auth;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义权限验证接口扩展 
 * Sa-Token 获取权限信息接口的实现类
 * 
 * @author GIG
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
            // 从Session中获取用户信息（这里需要在登录时存储用户权限信息）
            Object userInfoObj = StpUtil.getSessionByLoginId(loginId).get("userInfo");
            
            if (userInfoObj == null) {
                log.warn("用户 {} 的权限信息未找到，返回空权限列表", loginId);
                return permissions;
            }

            // TODO: 根据实际的用户信息结构获取权限
            // 这里需要在重新设计用户服务后实现具体逻辑
            // 示例权限结构：
            // - basic: 基本权限（所有登录用户）
            // - vip: VIP权限
            // - blogger: 博主权限  
            // - admin: 管理员权限
            
            // 临时实现：所有登录用户都有基本权限
            permissions.add("basic");
            
            // 根据用户信息判断其他权限
            // 这里需要在用户服务完成后实现具体逻辑
            
            log.debug("用户 {} 的权限列表：{}", loginId, permissions);
            
        } catch (Exception e) {
            log.error("获取用户 {} 权限信息时发生异常：", loginId, e);
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
                log.warn("用户 {} 的角色信息未找到，返回空角色列表", loginId);
                return roles;
            }

            // TODO: 根据实际的用户信息结构获取角色
            // 这里需要在重新设计用户服务后实现具体逻辑
            // 示例角色结构：
            // - user: 普通用户
            // - vip: VIP用户
            // - blogger: 博主
            // - admin: 管理员
            
            // 临时实现：默认为普通用户
            roles.add("user");
            
            // 根据用户信息判断其他角色
            // 这里需要在用户服务完成后实现具体逻辑
            
            log.debug("用户 {} 的角色列表：{}", loginId, roles);
            
        } catch (Exception e) {
            log.error("获取用户 {} 角色信息时发生异常：", loginId, e);
        }
        
        return roles;
    }
}

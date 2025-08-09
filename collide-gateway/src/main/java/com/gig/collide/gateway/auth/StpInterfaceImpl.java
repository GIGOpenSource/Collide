package com.gig.collide.gateway.auth;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Collections;

/**
 * 自定义权限验证接口扩展 - Collide用户服务版
 * Sa-Token 获取权限信息接口的实现类
 * 基于Collide用户微服务的角色和权限体系
 *
 * @author GIG Team
 * @version 2.0.0
 */
@Slf4j
@Component
public class StpInterfaceImpl implements StpInterface {

    /**
     * 返回一个账号所拥有的权限码集合
     * 基于Collide用户服务的角色体系设计
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

            // 基于Collide用户结构获取权限
            if (userInfoObj instanceof Map<?, ?> userInfo) {
                // 安全获取 roles 列表
                List<String> roles = Collections.emptyList();
                Object rolesObj = userInfo.get("roles");
                if (rolesObj instanceof List<?>) {
                    List<?> rawRoles = (List<?>) rolesObj;
                    List<String> tmp = new ArrayList<>(rawRoles.size());
                    for (Object r : rawRoles) {
                        if (r != null) tmp.add(String.valueOf(r));
                    }
                    roles = tmp;
                }
                String status = (String) userInfo.get("status");
                Object vipExpireTimeObj = userInfo.get("vip_expire_time");

                // 检查用户状态
                if (!"active".equals(status)) {
                    log.warn("用户 {} 状态异常: {}", loginId, status);
                    return permissions; // 返回空权限列表
                }

                // 基础权限：所有活跃用户都有
                permissions.add("basic");
                permissions.add("user_info_read");    // 读取基础用户信息

                // 基于角色分配权限
                for (String role : roles) {
                    switch (role) {
                        case "admin":
                            permissions.addAll(Arrays.asList("admin", "user_manage", "system_manage", "blogger", "vip", "user_info_edit", "wallet_manage"));
                            break;
                        case "blogger":
                            permissions.addAll(Arrays.asList("blogger", "user_info_edit", "wallet_manage"));
                            break;
                        case "vip":
                            permissions.addAll(Arrays.asList("vip", "user_info_edit", "wallet_manage"));
                            break;
                        case "user":
                        default:
                            permissions.addAll(Arrays.asList("user_info_edit", "wallet_view"));
                            break;
                    }
                }

                // 检查VIP权限（基于过期时间）
                if (vipExpireTimeObj != null && hasValidVipAccess(vipExpireTimeObj)) {
                    if (!permissions.contains("vip")) {
                        permissions.add("vip");
                        permissions.add("wallet_manage");
                    }
                }

                log.debug("用户 {} (角色: {}) 的权限列表：{}", loginId, roles, permissions);
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
     * 基于Collide用户服务的角色继承体系
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        try {
            // 从Session中获取用户角色信息
            Object userInfoObj = StpUtil.getSessionByLoginId(loginId).get("userInfo");

            if (userInfoObj == null) {
                log.warn("用户 {} 的角色信息未找到，返回默认角色", loginId);
                return Collections.singletonList("user");
            }

            // 基于Collide用户结构获取角色
            if (userInfoObj instanceof Map<?, ?> userInfo) {
                // 安全获取 roles 列表
                List<String> roles = Collections.emptyList();
                Object rolesObj = userInfo.get("roles");
                if (rolesObj instanceof List<?>) {
                    List<?> rawRoles = (List<?>) rolesObj;
                    List<String> tmp = new ArrayList<>(rawRoles.size());
                    for (Object r : rawRoles) {
                        if (r != null) tmp.add(String.valueOf(r));
                    }
                    roles = tmp;
                }
                String status = (String) userInfo.get("status");
                Object vipExpireTimeObj = userInfo.get("vip_expire_time");

                // 检查用户状态
                if (!"active".equals(status)) {
                    log.warn("用户 {} 状态异常: {}，不分配任何角色", loginId, status);
                    return Collections.emptyList();
                }

                // 动态VIP角色检查（基于过期时间）
                if (hasValidVipAccess(vipExpireTimeObj) && !roles.contains("vip")) {
                    roles.add("vip");
                    log.debug("用户 {} 通过VIP过期时间检查，添加VIP角色", loginId);
                }

                log.debug("用户 {} 的角色列表：{}", loginId, roles);
                return roles;
            }

        } catch (Exception e) {
            log.error("获取用户 {} 角色信息时发生异常：", loginId, e);
            // 异常情况下给予默认角色
            return Collections.singletonList("user");
        }

        return Collections.singletonList("user");
    }

    /**
     * 检查用户是否有有效的VIP权限
     * 基于vip_expire_time字段判断
     */
    private boolean hasValidVipAccess(Object vipExpireTimeObj) {
        if (vipExpireTimeObj == null) {
            return false;
        }

        try {
            // 这里需要根据实际的时间格式进行解析
            // 假设vip_expire_time是ISO格式的字符串或者时间戳
            if (vipExpireTimeObj instanceof String) {
                LocalDateTime vipExpireTime = LocalDateTime.parse((String) vipExpireTimeObj);
                return vipExpireTime.isAfter(LocalDateTime.now());
            } else if (vipExpireTimeObj instanceof Long) {
                // 如果是时间戳格式
                LocalDateTime vipExpireTime = LocalDateTime.ofEpochSecond((Long) vipExpireTimeObj, 0,
                        java.time.ZoneOffset.systemDefault().getRules().getOffset(java.time.Instant.now()));
                return vipExpireTime.isAfter(LocalDateTime.now());
            }
        } catch (Exception e) {
            log.warn("解析VIP过期时间失败: {}", vipExpireTimeObj, e);
        }

        return false;
    }
}
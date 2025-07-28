package com.gig.collide.api.user.request.condition;

import lombok.*;

import java.util.List;

/**
 * 用户操作日志-IP地址查询条件
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserOperateLogIpAddressQueryCondition implements UserQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * IP地址列表（支持多IP查询）
     */
    private List<String> ipAddresses;

    /**
     * IP地址段（支持CIDR格式，如：192.168.1.0/24）
     */
    private String ipSegment;
} 
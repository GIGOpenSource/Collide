package com.gig.collide.api.user.response.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户操作日志信息传输对象
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class UserOperateLogInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日志ID
     */
    private Long logId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 操作类型
     */
    private String operateType;

    /**
     * 操作描述
     */
    private String operateDesc;

    /**
     * IP地址（脱敏）
     */
    private String ipAddress;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 操作结果（成功/失败）
     */
    private String result;

    /**
     * 操作时间
     */
    private LocalDateTime operateTime;

    /**
     * 地理位置
     */
    private String location;

    /**
     * 设备信息
     */
    private String deviceInfo;
} 
package com.gig.collide.api.user.response;

import com.gig.collide.api.user.response.data.UserOperateLogInfo;
import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 用户操作日志创建响应
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
public class UserOperateLogCreateResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 创建的日志信息
     */
    private UserOperateLogInfo logInfo;

    /**
     * 日志ID
     */
    private Long logId;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 操作提示信息
     */
    private String message;
} 
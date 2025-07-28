package com.gig.collide.api.user.response;

import com.gig.collide.api.user.response.data.UserUnifiedInfo;
import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 用户统一信息修改响应
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
public class UserUnifiedModifyResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 修改后的用户信息
     */
    private UserUnifiedInfo userInfo;

    /**
     * 修改字段列表
     */
    private String[] modifiedFields;

    /**
     * 操作提示信息
     */
    private String message;
} 
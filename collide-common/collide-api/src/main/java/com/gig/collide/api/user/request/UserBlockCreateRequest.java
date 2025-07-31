package com.gig.collide.api.user.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户拉黑创建请求 - 简洁版
 * 支持Dubbo序列化传输
 * 
 * @author GIG Team
 * @version 2.0.0 (支持Dubbo序列化)
 */
@Data
public class UserBlockCreateRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @NotNull(message = "被拉黑用户ID不能为空")
    private Long blockedUserId;

    @Size(max = 200, message = "拉黑原因长度不能超过200字符")
    private String reason;
}
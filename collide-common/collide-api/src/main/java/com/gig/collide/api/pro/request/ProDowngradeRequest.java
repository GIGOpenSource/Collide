package com.gig.collide.api.pro.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 用户降级请求
 * @author GIG
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProDowngradeRequest extends BaseRequest {

    /**
     * 用户ID
     */
    @NotNull(message = "userId不能为空")
    private Long userId;

    /**
     * 降级原因
     */
    private String reason;

    /**
     * 操作人ID（管理员操作时使用）
     */
    private Long operatorId;

    /**
     * 是否保留部分权限
     */
    private Boolean retainSomePermissions = false;
} 
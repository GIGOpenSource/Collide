package com.gig.collide.api.pro.request;

import com.gig.collide.api.pro.constant.ProPermissionType;
import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Pro权限激活请求
 * @author GIG
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProPermissionActivateRequest extends BaseRequest {

    /**
     * 用户ID
     */
    @NotNull(message = "userId不能为空")
    private Long userId;

    /**
     * 权限类型列表
     */
    @NotNull(message = "权限类型不能为空")
    private List<ProPermissionType> permissionTypes;

    /**
     * 操作人ID（管理员操作时使用）
     */
    private Long operatorId;

    /**
     * 激活原因
     */
    private String reason;

    /**
     * 是否自动激活（系统自动开通）
     */
    private Boolean autoActivate = false;
} 
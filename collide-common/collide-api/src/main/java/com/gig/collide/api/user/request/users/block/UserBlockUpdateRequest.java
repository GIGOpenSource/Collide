package com.gig.collide.api.user.request.users.block;

import java.io.Serializable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 用户拉黑更新请求 - 对应 t_user_block �?
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBlockUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 拉黑记录ID
     */
    @NotNull(message = "拉黑记录ID不能为空")
    private Long id;

    /**
     * 拉黑者用户ID
     */
    @NotNull(message = "拉黑者用户ID不能为空")
    private Long userId;

    /**
     * 被拉黑用户ID
     */
    @NotNull(message = "被拉黑用户ID不能为空")
    private Long blockedUserId;

    /**
     * 拉黑状态：active、cancelled
     */
    private String status;

    /**
     * 拉黑原因
     */
    @Size(max = 200, message = "拉黑原因长度不能超过200个字符")
    private String reason;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getBlockedUserId() {
        return blockedUserId;
    }

    public void setBlockedUserId(Long blockedUserId) {
        this.blockedUserId = blockedUserId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

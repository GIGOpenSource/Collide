package com.gig.collide.api.user.response.users.block;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 用户拉黑响应 - 对应 t_user_block �?
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBlockResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 拉黑记录ID
     */
    private Long id;

    /**
     * 拉黑者用户ID
     */
    private Long userId;

    /**
     * 被拉黑用户ID
     */
    private Long blockedUserId;

    /**
     * 拉黑者用户名
     */
    private String userUsername;

    /**
     * 被拉黑用户名
     */
    private String blockedUsername;

    /**
     * 拉黑状态：active、cancelled
     */
    private String status;

    /**
     * 状态描�?
     */
    private String statusDesc;

    /**
     * 拉黑原因
     */
    private String reason;

    /**
     * 拉黑时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 拉黑天数
     */
    private Long blockDays;


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

    public String getUserUsername() {
        return userUsername;
    }

    public void setUserUsername(String userUsername) {
        this.userUsername = userUsername;
    }

    public String getBlockedUsername() {
        return blockedUsername;
    }

    public void setBlockedUsername(String blockedUsername) {
        this.blockedUsername = blockedUsername;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        this.statusDesc = "active".equals(status) ? "已拉黑" : "cancelled".equals(status) ? "已取消" : "未知";
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间并自动计算拉黑天数
     */
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
        // 计算拉黑天数
        if (createTime != null) {
            this.blockDays = java.time.Duration.between(createTime, LocalDateTime.now()).toDays();
        }
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Long getBlockDays() {
        return blockDays;
    }

    public void setBlockDays(Long blockDays) {
        this.blockDays = blockDays;
    }

    @Override
    public String toString() {
        return "UserBlockResponse{" +
                "id=" + id +
                ", userId=" + userId +
                ", blockedUserId=" + blockedUserId +
                ", userUsername='" + userUsername + '\'' +
                ", blockedUsername='" + blockedUsername + '\'' +
                ", status='" + status + '\'' +
                ", statusDesc='" + statusDesc + '\'' +
                ", reason='" + reason + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", blockDays=" + blockDays +
                '}';
    }
}

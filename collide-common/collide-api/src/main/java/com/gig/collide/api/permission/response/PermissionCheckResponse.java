package com.gig.collide.api.permission.response;

import com.gig.collide.base.response.BaseResponse;

/**
 * 权限检查响应
 * 返回用户是否有观看内容的权限及相关信息
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
public class PermissionCheckResponse extends BaseResponse {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 内容ID  
     */
    private Long contentId;
    
    /**
     * 是否有权限观看
     */
    private Boolean hasPermission;
    
    /**
     * 权限类型
     * NORMAL_USER_PAID, NORMAL_USER_FREE, VIP_USER_PAID, VIP_USER_FREE
     */
    private String permissionType;
    
    /**
     * 权限来源（如何获得的权限）
     * ORDER_PURCHASE - 通过订单购买获得
     * VIP_PRIVILEGE - VIP特权
     * FREE_CONTENT - 免费内容
     * SUBSCRIPTION - 订阅获得
     */
    private String permissionSource;
    
    /**
     * 关联的订单号（如果通过购买获得权限）
     */
    private String relatedOrderNo;
    
    /**
     * 权限过期时间（对于有时限的权限）
     */
    private java.time.LocalDateTime expireTime;
    
    /**
     * 拒绝原因（无权限时的原因）
     */
    private String deniedReason;
    
    /**
     * 提示信息（如何获得权限的提示）
     */
    private String hint;
    
    public PermissionCheckResponse() {
        super();
    }
    
    public static PermissionCheckResponse allowed(Long userId, Long contentId, String permissionType) {
        PermissionCheckResponse response = new PermissionCheckResponse();
        response.setSuccess(true);
        response.setUserId(userId);
        response.setContentId(contentId);
        response.setHasPermission(true);
        response.setPermissionType(permissionType);
        return response;
    }
    
    public static PermissionCheckResponse allowedWithSource(Long userId, Long contentId, 
                                                          String permissionType, String permissionSource) {
        PermissionCheckResponse response = allowed(userId, contentId, permissionType);
        response.setPermissionSource(permissionSource);
        return response;
    }
    
    public static PermissionCheckResponse denied(Long userId, Long contentId, String reason) {
        PermissionCheckResponse response = new PermissionCheckResponse();
        response.setSuccess(true);
        response.setUserId(userId);
        response.setContentId(contentId);  
        response.setHasPermission(false);
        response.setDeniedReason(reason);
        return response;
    }
    
    public static PermissionCheckResponse deniedWithHint(Long userId, Long contentId, 
                                                       String reason, String hint) {
        PermissionCheckResponse response = denied(userId, contentId, reason);
        response.setHint(hint);
        return response;
    }
    
    public static PermissionCheckResponse error(String code, String message) {
        PermissionCheckResponse response = new PermissionCheckResponse();
        response.setSuccess(false);
        response.setResponseCode(code);
        response.setResponseMessage(message);
        return response;
    }
    
    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Long getContentId() { return contentId; }
    public void setContentId(Long contentId) { this.contentId = contentId; }
    
    public Boolean getHasPermission() { return hasPermission; }
    public void setHasPermission(Boolean hasPermission) { this.hasPermission = hasPermission; }
    
    public String getPermissionType() { return permissionType; }
    public void setPermissionType(String permissionType) { this.permissionType = permissionType; }
    
    public String getPermissionSource() { return permissionSource; }
    public void setPermissionSource(String permissionSource) { this.permissionSource = permissionSource; }
    
    public String getRelatedOrderNo() { return relatedOrderNo; }
    public void setRelatedOrderNo(String relatedOrderNo) { this.relatedOrderNo = relatedOrderNo; }
    
    public java.time.LocalDateTime getExpireTime() { return expireTime; }
    public void setExpireTime(java.time.LocalDateTime expireTime) { this.expireTime = expireTime; }
    
    public String getDeniedReason() { return deniedReason; }
    public void setDeniedReason(String deniedReason) { this.deniedReason = deniedReason; }
    
    public String getHint() { return hint; }
    public void setHint(String hint) { this.hint = hint; }
} 
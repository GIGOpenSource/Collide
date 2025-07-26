package com.gig.collide.api.permission.request;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 权限检查请求
 * 用于检查用户是否有观看特定内容的权限
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
@Schema(description = "权限检查请求")
public class PermissionCheckRequest {
    
    @Schema(description = "用户ID", required = true)
    private Long userId;
    
    @Schema(description = "内容ID", required = true)
    private Long contentId;
    
    @Schema(description = "内容类型", allowableValues = {"FREE", "PAID"})
    private String contentType;
    
    @Schema(description = "用户类型", allowableValues = {"NORMAL", "VIP"})
    private String userType;
    
    @Schema(description = "是否检查订单关联（检查用户是否通过购买获得权限）", defaultValue = "true")
    private Boolean checkOrderAssociation;
    
    public PermissionCheckRequest() {}
    
    public PermissionCheckRequest(Long userId, Long contentId) {
        this.userId = userId;
        this.contentId = contentId;
        this.checkOrderAssociation = true;
    }
    
    public PermissionCheckRequest(Long userId, Long contentId, String contentType, String userType) {
        this.userId = userId;
        this.contentId = contentId;
        this.contentType = contentType;
        this.userType = userType;
        this.checkOrderAssociation = true;
    }
    
    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Long getContentId() { return contentId; }
    public void setContentId(Long contentId) { this.contentId = contentId; }
    
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    
    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
    
    public Boolean getCheckOrderAssociation() { return checkOrderAssociation; }
    public void setCheckOrderAssociation(Boolean checkOrderAssociation) { this.checkOrderAssociation = checkOrderAssociation; }
} 
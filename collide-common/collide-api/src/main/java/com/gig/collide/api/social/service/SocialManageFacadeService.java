package com.gig.collide.api.social.service;

import com.gig.collide.api.social.response.SocialPostOperationResponse;
import com.gig.collide.api.social.response.SocialPostQueryResponse;
import com.gig.collide.api.social.response.data.SocialPostInfo;
import com.gig.collide.api.social.response.data.SocialInteractionInfo;
import com.gig.collide.api.social.constant.PostStatusEnum;
import com.gig.collide.api.social.constant.InteractionTypeEnum;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 社交管理门面服务接口
 * @author GIG
 * @version 1.0.0
 * @since 2024-01-01
 */
public interface SocialManageFacadeService {
    
    /**
     * 管理员强制删除动态
     */
    SocialPostOperationResponse forceDeletePost(Long postId, Long adminId, String reason);
    
    /**
     * 管理员恢复被删除的动态
     */
    SocialPostOperationResponse restorePost(Long postId, Long adminId, String reason);
    
    /**
     * 管理员修改动态状态
     */
    SocialPostOperationResponse changePostStatus(Long postId, PostStatusEnum newStatus, Long adminId, String reason);
    
    /**
     * 管理员批量删除动态
     */
    SocialPostOperationResponse batchDeletePosts(List<Long> postIds, Long adminId, String reason);
    
    /**
     * 管理员批量修改动态状态
     */
    SocialPostOperationResponse batchChangePostStatus(List<Long> postIds, PostStatusEnum newStatus, 
                                                     Long adminId, String reason);
    
    /**
     * 获取待审核动态列表
     */
    SocialPostQueryResponse<List<SocialPostInfo>> getPendingPosts(Integer pageNum, Integer pageSize);
    
    /**
     * 获取被举报动态列表
     */
    SocialPostQueryResponse<List<SocialPostInfo>> getReportedPosts(Integer pageNum, Integer pageSize);
    
    /**
     * 获取违规动态列表
     */
    SocialPostQueryResponse<List<SocialPostInfo>> getViolationPosts(Integer pageNum, Integer pageSize);
    
    /**
     * 审核动态内容
     */
    SocialPostOperationResponse reviewPost(Long postId, Long adminId, Boolean approved, String reviewComment);
    
    /**
     * 批量审核动态
     */
    SocialPostOperationResponse batchReviewPosts(List<Long> postIds, Long adminId, 
                                                Boolean approved, String reviewComment);
    
    /**
     * 设置动态为热门
     */
    SocialPostOperationResponse setPostAsHot(Long postId, Long adminId, String reason);
    
    /**
     * 取消动态热门状态
     */
    SocialPostOperationResponse unsetPostAsHot(Long postId, Long adminId, String reason);
    
    /**
     * 置顶动态
     */
    SocialPostOperationResponse pinPost(Long postId, Long adminId, LocalDateTime expireTime);
    
    /**
     * 取消置顶
     */
    SocialPostOperationResponse unpinPost(Long postId, Long adminId);
    
    /**
     * 禁止用户发布动态
     */
    SocialPostOperationResponse banUserFromPosting(Long userId, Long adminId, String reason, 
                                                   LocalDateTime expireTime);
    
    /**
     * 解除用户发布禁令
     */
    SocialPostOperationResponse unbanUserFromPosting(Long userId, Long adminId, String reason);
    
    /**
     * 清理异常互动数据
     */
    void cleanAbnormalInteractions(Long postId, InteractionTypeEnum interactionType);
    
    /**
     * 重新计算动态统计数据
     */
    void recalculatePostStatistics(Long postId);
    
    /**
     * 批量重新计算统计数据
     */
    void batchRecalculatePostStatistics(List<Long> postIds);
    
    /**
     * 同步用户信息到动态记录
     */
    void syncUserInfoToPosts(Long userId);
    
    /**
     * 获取动态管理日志
     */
    SocialPostQueryResponse<List<PostManageLog>> getPostManageLogs(Long postId, Integer pageNum, Integer pageSize);
    
    /**
     * 获取用户管理日志
     */
    SocialPostQueryResponse<List<UserManageLog>> getUserManageLogs(Long userId, Integer pageNum, Integer pageSize);
    
    /**
     * 获取管理员操作日志
     */
    SocialPostQueryResponse<List<AdminOperationLog>> getAdminOperationLogs(Long adminId, LocalDateTime startTime, 
                                                                           LocalDateTime endTime, 
                                                                           Integer pageNum, Integer pageSize);
    
    /**
     * 导出动态数据
     */
    String exportPostData(List<Long> postIds, String format);
    
    /**
     * 导出用户互动数据
     */
    String exportUserInteractionData(Long userId, LocalDateTime startTime, LocalDateTime endTime, String format);
    
    /**
     * 数据清理
     */
    void cleanExpiredData(Integer retentionDays);
    
    /**
     * 数据备份
     */
    String backupData(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取系统健康状态
     */
    SystemHealthStatus getSystemHealthStatus();
    
    /**
     * 动态管理日志
     */
    class PostManageLog {
        private Long id;
        private Long postId;
        private Long adminId;
        private String adminName;
        private String operationType;
        private String reason;
        private PostStatusEnum oldStatus;
        private PostStatusEnum newStatus;
        private LocalDateTime operationTime;
        
        // getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getPostId() { return postId; }
        public void setPostId(Long postId) { this.postId = postId; }
        public Long getAdminId() { return adminId; }
        public void setAdminId(Long adminId) { this.adminId = adminId; }
        public String getAdminName() { return adminName; }
        public void setAdminName(String adminName) { this.adminName = adminName; }
        public String getOperationType() { return operationType; }
        public void setOperationType(String operationType) { this.operationType = operationType; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        public PostStatusEnum getOldStatus() { return oldStatus; }
        public void setOldStatus(PostStatusEnum oldStatus) { this.oldStatus = oldStatus; }
        public PostStatusEnum getNewStatus() { return newStatus; }
        public void setNewStatus(PostStatusEnum newStatus) { this.newStatus = newStatus; }
        public LocalDateTime getOperationTime() { return operationTime; }
        public void setOperationTime(LocalDateTime operationTime) { this.operationTime = operationTime; }
    }
    
    /**
     * 用户管理日志
     */
    class UserManageLog {
        private Long id;
        private Long userId;
        private String username;
        private Long adminId;
        private String adminName;
        private String operationType;
        private String reason;
        private LocalDateTime operationTime;
        private LocalDateTime expireTime;
        
        // getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public Long getAdminId() { return adminId; }
        public void setAdminId(Long adminId) { this.adminId = adminId; }
        public String getAdminName() { return adminName; }
        public void setAdminName(String adminName) { this.adminName = adminName; }
        public String getOperationType() { return operationType; }
        public void setOperationType(String operationType) { this.operationType = operationType; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        public LocalDateTime getOperationTime() { return operationTime; }
        public void setOperationTime(LocalDateTime operationTime) { this.operationTime = operationTime; }
        public LocalDateTime getExpireTime() { return expireTime; }
        public void setExpireTime(LocalDateTime expireTime) { this.expireTime = expireTime; }
    }
    
    /**
     * 管理员操作日志
     */
    class AdminOperationLog {
        private Long id;
        private Long adminId;
        private String adminName;
        private String operationType;
        private String targetType;
        private Long targetId;
        private String operationDetail;
        private LocalDateTime operationTime;
        private String ipAddress;
        
        // getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getAdminId() { return adminId; }
        public void setAdminId(Long adminId) { this.adminId = adminId; }
        public String getAdminName() { return adminName; }
        public void setAdminName(String adminName) { this.adminName = adminName; }
        public String getOperationType() { return operationType; }
        public void setOperationType(String operationType) { this.operationType = operationType; }
        public String getTargetType() { return targetType; }
        public void setTargetType(String targetType) { this.targetType = targetType; }
        public Long getTargetId() { return targetId; }
        public void setTargetId(Long targetId) { this.targetId = targetId; }
        public String getOperationDetail() { return operationDetail; }
        public void setOperationDetail(String operationDetail) { this.operationDetail = operationDetail; }
        public LocalDateTime getOperationTime() { return operationTime; }
        public void setOperationTime(LocalDateTime operationTime) { this.operationTime = operationTime; }
        public String getIpAddress() { return ipAddress; }
        public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    }
    
    /**
     * 系统健康状态
     */
    class SystemHealthStatus {
        private String status; // HEALTHY, WARNING, ERROR
        private Long totalPosts;
        private Long totalUsers;
        private Long activeUsers;
        private Double systemLoad;
        private Long databaseConnections;
        private Long cacheHitRate;
        private List<HealthMetric> metrics;
        
        // getters and setters
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Long getTotalPosts() { return totalPosts; }
        public void setTotalPosts(Long totalPosts) { this.totalPosts = totalPosts; }
        public Long getTotalUsers() { return totalUsers; }
        public void setTotalUsers(Long totalUsers) { this.totalUsers = totalUsers; }
        public Long getActiveUsers() { return activeUsers; }
        public void setActiveUsers(Long activeUsers) { this.activeUsers = activeUsers; }
        public Double getSystemLoad() { return systemLoad; }
        public void setSystemLoad(Double systemLoad) { this.systemLoad = systemLoad; }
        public Long getDatabaseConnections() { return databaseConnections; }
        public void setDatabaseConnections(Long databaseConnections) { this.databaseConnections = databaseConnections; }
        public Long getCacheHitRate() { return cacheHitRate; }
        public void setCacheHitRate(Long cacheHitRate) { this.cacheHitRate = cacheHitRate; }
        public List<HealthMetric> getMetrics() { return metrics; }
        public void setMetrics(List<HealthMetric> metrics) { this.metrics = metrics; }
    }
    
    /**
     * 健康指标
     */
    class HealthMetric {
        private String name;
        private String value;
        private String status;
        private String description;
        
        // getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
} 
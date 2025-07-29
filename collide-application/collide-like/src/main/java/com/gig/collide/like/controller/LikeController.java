package com.gig.collide.like.controller;

import com.gig.collide.like.domain.service.LikeService;
import com.gig.collide.like.domain.entity.Like;
import com.gig.collide.web.vo.Result;
import com.gig.collide.base.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import java.util.List;
import java.util.Map;

/**
 * 点赞REST控制器 - 简洁版
 * 提供HTTP接口，使用内部DTO处理请求响应
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/like")
@RequiredArgsConstructor
@Tag(name = "点赞管理", description = "点赞相关的API接口")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @PostMapping("/add")
    @Operation(summary = "添加点赞", description = "用户对内容、评论或动态进行点赞")
    public Result<LikeDTO> addLike(@RequestBody LikeCreateDTO request) {
        try {
            log.info("HTTP添加点赞: userId={}, likeType={}, targetId={}", 
                    request.getUserId(), request.getLikeType(), request.getTargetId());

            // 转换为实体
            Like like = convertCreateDTOToEntity(request);
            
            // 调用业务逻辑
            Like savedLike = likeService.addLike(like);
            
            // 转换响应
            LikeDTO response = convertToDTO(savedLike);
            
            return Result.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("点赞参数验证失败: {}", e.getMessage());
            return Result.error("LIKE_PARAM_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("添加点赞失败", e);
            return Result.error("LIKE_ADD_ERROR", "添加点赞失败: " + e.getMessage());
        }
    }

    @PostMapping("/cancel")
    @Operation(summary = "取消点赞", description = "取消用户的点赞")
    public Result<Void> cancelLike(@RequestBody LikeCancelDTO request) {
        try {
            log.info("HTTP取消点赞: userId={}, likeType={}, targetId={}", 
                    request.getUserId(), request.getLikeType(), request.getTargetId());

            boolean success = likeService.cancelLike(
                    request.getUserId(), 
                    request.getLikeType(), 
                    request.getTargetId()
            );
            
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("LIKE_CANCEL_FAILED", "取消点赞失败");
            }
        } catch (Exception e) {
            log.error("取消点赞失败", e);
            return Result.error("LIKE_CANCEL_ERROR", "取消点赞失败: " + e.getMessage());
        }
    }

    @PostMapping("/toggle")
    @Operation(summary = "切换点赞状态", description = "如果已点赞则取消，如果未点赞则添加")
    public Result<LikeDTO> toggleLike(@RequestBody LikeToggleDTO request) {
        try {
            log.info("HTTP切换点赞状态: userId={}, likeType={}, targetId={}", 
                    request.getUserId(), request.getLikeType(), request.getTargetId());

            // 转换为实体
            Like like = convertToggleDTOToEntity(request);
            
            // 调用业务逻辑
            Like resultLike = likeService.toggleLike(like);
            
            if (resultLike != null) {
                LikeDTO response = convertToDTO(resultLike);
                return Result.success(response);
            } else {
                return Result.success(null);
            }
        } catch (Exception e) {
            log.error("切换点赞状态失败", e);
            return Result.error("LIKE_TOGGLE_ERROR", "切换点赞状态失败: " + e.getMessage());
        }
    }

    @GetMapping("/check")
    @Operation(summary = "检查点赞状态", description = "检查用户是否已对目标对象点赞")
    public Result<Boolean> checkLikeStatus(
            @Parameter(description = "用户ID") @RequestParam @NotNull Long userId,
            @Parameter(description = "点赞类型") @RequestParam @NotBlank String likeType,
            @Parameter(description = "目标对象ID") @RequestParam @NotNull Long targetId) {
        try {
            boolean isLiked = likeService.checkLikeStatus(userId, likeType, targetId);
            return Result.success(isLiked);
        } catch (Exception e) {
            log.error("检查点赞状态失败", e);
            return Result.error("LIKE_CHECK_ERROR", "检查点赞状态失败: " + e.getMessage());
        }
    }

    @GetMapping("/query")
    @Operation(summary = "分页查询点赞记录", description = "根据条件分页查询点赞记录")
    public Result<PageResponse<LikeDTO>> queryLikes(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") @Min(1) Integer pageNum,
            @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") @Min(1) Integer pageSize,
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "点赞类型") @RequestParam(required = false) String likeType,
            @Parameter(description = "目标对象ID") @RequestParam(required = false) Long targetId,
            @Parameter(description = "目标作者ID") @RequestParam(required = false) Long targetAuthorId,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "create_time") String orderBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "DESC") String orderDirection) {
        try {
            var likePage = likeService.queryLikes(pageNum, pageSize, userId, likeType,
                    targetId, targetAuthorId, status, orderBy, orderDirection);

            // 转换分页响应
            PageResponse<LikeDTO> pageResponse = new PageResponse<>();
            List<LikeDTO> responseList = likePage.getRecords().stream()
                    .map(this::convertToDTO)
                    .toList();

            pageResponse.setDatas(responseList);
            pageResponse.setTotal(likePage.getTotal());
            pageResponse.setCurrentPage((int) likePage.getCurrent());
            pageResponse.setPageSize((int) likePage.getSize());
            pageResponse.setTotalPage((int) likePage.getPages());

            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("分页查询点赞记录失败", e);
            return Result.error("LIKE_QUERY_ERROR", "查询点赞记录失败: " + e.getMessage());
        }
    }

    @GetMapping("/count")
    @Operation(summary = "获取点赞数量", description = "获取目标对象的点赞数量")
    public Result<Long> getLikeCount(
            @Parameter(description = "点赞类型") @RequestParam @NotBlank String likeType,
            @Parameter(description = "目标对象ID") @RequestParam @NotNull Long targetId) {
        try {
            Long count = likeService.getLikeCount(likeType, targetId);
            return Result.success(count);
        } catch (Exception e) {
            log.error("获取点赞数量失败", e);
            return Result.error("LIKE_COUNT_ERROR", "获取点赞数量失败: " + e.getMessage());
        }
    }

    @GetMapping("/user/count")
    @Operation(summary = "获取用户点赞数量", description = "获取用户的点赞数量")
    public Result<Long> getUserLikeCount(
            @Parameter(description = "用户ID") @RequestParam @NotNull Long userId,
            @Parameter(description = "点赞类型") @RequestParam(required = false) String likeType) {
        try {
            Long count = likeService.getUserLikeCount(userId, likeType);
            return Result.success(count);
        } catch (Exception e) {
            log.error("获取用户点赞数量失败", e);
            return Result.error("USER_LIKE_COUNT_ERROR", "获取用户点赞数量失败: " + e.getMessage());
        }
    }

    @PostMapping("/batch/check")
    @Operation(summary = "批量检查点赞状态", description = "批量检查用户对多个目标对象的点赞状态")
    public Result<Map<Long, Boolean>> batchCheckLikeStatus(@RequestBody BatchCheckDTO request) {
        try {
            Map<Long, Boolean> statusMap = likeService.batchCheckLikeStatus(
                    request.getUserId(), 
                    request.getLikeType(), 
                    request.getTargetIds()
            );
            return Result.success(statusMap);
        } catch (Exception e) {
            log.error("批量检查点赞状态失败", e);
            return Result.error("BATCH_CHECK_ERROR", "批量检查点赞状态失败: " + e.getMessage());
        }
    }

    // =================== 内部DTO类定义 ===================
    
    /**
     * 点赞创建DTO - 内部使用
     */
    public static class LikeCreateDTO {
        private String likeType;
        private Long targetId;
        private Long userId;
        private String targetTitle;
        private Long targetAuthorId;
        private String userNickname;
        private String userAvatar;
        
        // Getters and Setters
        public String getLikeType() { return likeType; }
        public void setLikeType(String likeType) { this.likeType = likeType; }
        public Long getTargetId() { return targetId; }
        public void setTargetId(Long targetId) { this.targetId = targetId; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getTargetTitle() { return targetTitle; }
        public void setTargetTitle(String targetTitle) { this.targetTitle = targetTitle; }
        public Long getTargetAuthorId() { return targetAuthorId; }
        public void setTargetAuthorId(Long targetAuthorId) { this.targetAuthorId = targetAuthorId; }
        public String getUserNickname() { return userNickname; }
        public void setUserNickname(String userNickname) { this.userNickname = userNickname; }
        public String getUserAvatar() { return userAvatar; }
        public void setUserAvatar(String userAvatar) { this.userAvatar = userAvatar; }
    }

    /**
     * 点赞取消DTO - 内部使用
     */
    public static class LikeCancelDTO {
        private String likeType;
        private Long targetId;
        private Long userId;
        
        // Getters and Setters
        public String getLikeType() { return likeType; }
        public void setLikeType(String likeType) { this.likeType = likeType; }
        public Long getTargetId() { return targetId; }
        public void setTargetId(Long targetId) { this.targetId = targetId; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
    }

    /**
     * 点赞切换DTO - 内部使用
     */
    public static class LikeToggleDTO {
        private String likeType;
        private Long targetId;
        private Long userId;
        private String targetTitle;
        private Long targetAuthorId;
        private String userNickname;
        private String userAvatar;
        
        // Getters and Setters  
        public String getLikeType() { return likeType; }
        public void setLikeType(String likeType) { this.likeType = likeType; }
        public Long getTargetId() { return targetId; }
        public void setTargetId(Long targetId) { this.targetId = targetId; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getTargetTitle() { return targetTitle; }
        public void setTargetTitle(String targetTitle) { this.targetTitle = targetTitle; }
        public Long getTargetAuthorId() { return targetAuthorId; }
        public void setTargetAuthorId(Long targetAuthorId) { this.targetAuthorId = targetAuthorId; }
        public String getUserNickname() { return userNickname; }
        public void setUserNickname(String userNickname) { this.userNickname = userNickname; }
        public String getUserAvatar() { return userAvatar; }
        public void setUserAvatar(String userAvatar) { this.userAvatar = userAvatar; }
    }

    /**
     * 批量检查DTO - 内部使用
     */
    public static class BatchCheckDTO {
        private Long userId;
        private String likeType;
        private List<Long> targetIds;
        
        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getLikeType() { return likeType; }
        public void setLikeType(String likeType) { this.likeType = likeType; }
        public List<Long> getTargetIds() { return targetIds; }
        public void setTargetIds(List<Long> targetIds) { this.targetIds = targetIds; }
    }

    /**
     * 点赞响应DTO - 内部使用
     */
    public static class LikeDTO {
        private Long id;
        private String likeType;
        private Long targetId;
        private Long userId;
        private String targetTitle;
        private Long targetAuthorId;
        private String userNickname;
        private String userAvatar;
        private String status;
        private java.time.LocalDateTime createTime;
        private java.time.LocalDateTime updateTime;
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getLikeType() { return likeType; }
        public void setLikeType(String likeType) { this.likeType = likeType; }
        public Long getTargetId() { return targetId; }
        public void setTargetId(Long targetId) { this.targetId = targetId; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getTargetTitle() { return targetTitle; }
        public void setTargetTitle(String targetTitle) { this.targetTitle = targetTitle; }
        public Long getTargetAuthorId() { return targetAuthorId; }
        public void setTargetAuthorId(Long targetAuthorId) { this.targetAuthorId = targetAuthorId; }
        public String getUserNickname() { return userNickname; }
        public void setUserNickname(String userNickname) { this.userNickname = userNickname; }
        public String getUserAvatar() { return userAvatar; }
        public void setUserAvatar(String userAvatar) { this.userAvatar = userAvatar; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public java.time.LocalDateTime getCreateTime() { return createTime; }
        public void setCreateTime(java.time.LocalDateTime createTime) { this.createTime = createTime; }
        public java.time.LocalDateTime getUpdateTime() { return updateTime; }
        public void setUpdateTime(java.time.LocalDateTime updateTime) { this.updateTime = updateTime; }
    }

    // =================== 私有辅助方法 ===================

    private Like convertCreateDTOToEntity(LikeCreateDTO dto) {
        Like like = new Like();
        like.setLikeType(dto.getLikeType());
        like.setTargetId(dto.getTargetId());
        like.setUserId(dto.getUserId());
        like.setTargetTitle(dto.getTargetTitle());
        like.setTargetAuthorId(dto.getTargetAuthorId());
        like.setUserNickname(dto.getUserNickname());
        like.setUserAvatar(dto.getUserAvatar());
        return like;
    }

    private Like convertToggleDTOToEntity(LikeToggleDTO dto) {
        Like like = new Like();
        like.setLikeType(dto.getLikeType());
        like.setTargetId(dto.getTargetId());
        like.setUserId(dto.getUserId());
        like.setTargetTitle(dto.getTargetTitle());
        like.setTargetAuthorId(dto.getTargetAuthorId());
        like.setUserNickname(dto.getUserNickname());
        like.setUserAvatar(dto.getUserAvatar());
        return like;
    }

    private LikeDTO convertToDTO(Like like) {
        LikeDTO dto = new LikeDTO();
        dto.setId(like.getId());
        dto.setLikeType(like.getLikeType());
        dto.setTargetId(like.getTargetId());
        dto.setUserId(like.getUserId());
        dto.setTargetTitle(like.getTargetTitle());
        dto.setTargetAuthorId(like.getTargetAuthorId());
        dto.setUserNickname(like.getUserNickname());
        dto.setUserAvatar(like.getUserAvatar());
        dto.setStatus(like.getStatus());
        dto.setCreateTime(like.getCreateTime());
        dto.setUpdateTime(like.getUpdateTime());
        return dto;
    }
}
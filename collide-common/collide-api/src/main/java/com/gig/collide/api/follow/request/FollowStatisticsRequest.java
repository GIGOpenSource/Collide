package com.gig.collide.api.follow.request;

import com.gig.collide.base.request.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 关注统计查询请求
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "关注统计查询请求")
public class FollowStatisticsRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID（查询指定用户的统计信息）
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 用户ID列表（批量查询）
     */
    @Schema(description = "用户ID列表")
    private List<Long> userIds;

    /**
     * 是否包含详细信息
     */
    @Schema(description = "是否包含详细信息")
    private Boolean includeDetail = false;

    /**
     * 统计开始时间（可选）
     */
    @Schema(description = "统计开始时间")
    private LocalDateTime startTime;

    /**
     * 统计结束时间（可选）
     */
    @Schema(description = "统计结束时间")
    private LocalDateTime endTime;

    /**
     * 是否重新计算统计信息
     */
    @Schema(description = "是否重新计算统计信息")
    private Boolean recalculate = false;

    // ===================== 静态工厂方法 =====================

    /**
     * 查询单个用户统计信息
     *
     * @param userId 用户ID
     * @return 统计查询请求
     */
    public static FollowStatisticsRequest forUser(Long userId) {
        FollowStatisticsRequest request = new FollowStatisticsRequest();
        request.setUserId(userId);
        return request;
    }

    /**
     * 批量查询用户统计信息
     *
     * @param userIds 用户ID列表
     * @return 统计查询请求
     */
    public static FollowStatisticsRequest forUsers(List<Long> userIds) {
        FollowStatisticsRequest request = new FollowStatisticsRequest();
        request.setUserIds(userIds);
        return request;
    }

    /**
     * 查询用户详细统计信息
     *
     * @param userId 用户ID
     * @return 统计查询请求
     */
    public static FollowStatisticsRequest forUserDetail(Long userId) {
        FollowStatisticsRequest request = new FollowStatisticsRequest();
        request.setUserId(userId);
        request.setIncludeDetail(true);
        return request;
    }

    /**
     * 重新计算用户统计信息
     *
     * @param userId 用户ID
     * @return 统计查询请求
     */
    public static FollowStatisticsRequest recalculateUser(Long userId) {
        FollowStatisticsRequest request = new FollowStatisticsRequest();
        request.setUserId(userId);
        request.setRecalculate(true);
        return request;
    }

    /**
     * 查询时间范围内的统计信息
     *
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计查询请求
     */
    public static FollowStatisticsRequest forTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        FollowStatisticsRequest request = new FollowStatisticsRequest();
        request.setUserId(userId);
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        return request;
    }

    // ===================== 业务方法 =====================

    /**
     * 是否为单用户查询
     *
     * @return true-单用户查询，false-批量查询
     */
    public boolean isSingleUserQuery() {
        return userId != null;
    }

    /**
     * 是否为批量查询
     *
     * @return true-批量查询，false-单用户查询
     */
    public boolean isBatchQuery() {
        return userIds != null && !userIds.isEmpty();
    }

    /**
     * 是否有时间范围限制
     *
     * @return true-有时间范围，false-无时间范围
     */
    public boolean hasTimeRange() {
        return startTime != null && endTime != null;
    }

    /**
     * 验证请求有效性
     *
     * @return true-有效，false-无效
     */
    public boolean isValid() {
        return isSingleUserQuery() || isBatchQuery();
    }
} 
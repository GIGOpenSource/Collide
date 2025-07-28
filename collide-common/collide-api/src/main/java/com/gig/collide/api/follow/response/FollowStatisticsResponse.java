package com.gig.collide.api.follow.response;

import com.gig.collide.api.follow.response.data.FollowStatisticsInfo;
import com.gig.collide.base.response.BaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * 关注统计响应
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
@Schema(description = "关注统计响应")
public class FollowStatisticsResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 单个用户统计信息
     */
    @Schema(description = "单个用户统计信息")
    private FollowStatisticsInfo statisticsInfo;

    /**
     * 批量用户统计信息（用户ID -> 统计信息）
     */
    @Schema(description = "批量用户统计信息")
    private Map<Long, FollowStatisticsInfo> statisticsMap;

    /**
     * 统计信息列表
     */
    @Schema(description = "统计信息列表")
    private List<FollowStatisticsInfo> statisticsList;

    /**
     * 是否重新计算了统计信息
     */
    @Schema(description = "是否重新计算了统计信息")
    private Boolean isRecalculated;

    /**
     * 统计汇总信息
     */
    @Schema(description = "统计汇总信息")
    private StatisticsSummary summary;

    // ===================== 内部类 =====================

    /**
     * 统计汇总信息
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "统计汇总信息")
    public static class StatisticsSummary {
        /**
         * 总用户数
         */
        @Schema(description = "总用户数")
        private Long totalUsers;

        /**
         * 总关注数
         */
        @Schema(description = "总关注数")
        private Long totalFollowings;

        /**
         * 总粉丝数
         */
        @Schema(description = "总粉丝数")
        private Long totalFollowers;

        /**
         * 活跃用户数（关注数或粉丝数>100）
         */
        @Schema(description = "活跃用户数")
        private Long activeUsers;

        /**
         * 热门用户数（粉丝数>1000）
         */
        @Schema(description = "热门用户数")
        private Long popularUsers;

        /**
         * 大V用户数（粉丝数>10000）
         */
        @Schema(description = "大V用户数")
        private Long bigVUsers;
    }

    // ===================== 静态工厂方法 =====================

    /**
     * 成功响应 - 单个用户统计
     *
     * @param statisticsInfo 统计信息
     * @return 统计响应
     */
    public static FollowStatisticsResponse success(FollowStatisticsInfo statisticsInfo) {
        FollowStatisticsResponse response = new FollowStatisticsResponse();
        response.setStatisticsInfo(statisticsInfo);
        response.setSuccess(true);
        return response;
    }

    /**
     * 成功响应 - 批量用户统计
     *
     * @param statisticsMap 统计信息映射
     * @return 统计响应
     */
    public static FollowStatisticsResponse success(Map<Long, FollowStatisticsInfo> statisticsMap) {
        FollowStatisticsResponse response = new FollowStatisticsResponse();
        response.setStatisticsMap(statisticsMap);
        response.setSuccess(true);
        return response;
    }

    /**
     * 成功响应 - 统计信息列表
     *
     * @param statisticsList 统计信息列表
     * @return 统计响应
     */
    public static FollowStatisticsResponse successList(List<FollowStatisticsInfo> statisticsList) {
        FollowStatisticsResponse response = new FollowStatisticsResponse();
        response.setStatisticsList(statisticsList);
        response.setSuccess(true);
        return response;
    }

    /**
     * 重新计算成功响应
     *
     * @param statisticsInfo 重新计算后的统计信息
     * @return 统计响应
     */
    public static FollowStatisticsResponse recalculatedSuccess(FollowStatisticsInfo statisticsInfo) {
        FollowStatisticsResponse response = new FollowStatisticsResponse();
        response.setStatisticsInfo(statisticsInfo);
        response.setIsRecalculated(true);
        response.setSuccess(true);
        response.setMessage("统计信息已重新计算");
        return response;
    }

    /**
     * 带汇总信息的响应
     *
     * @param statisticsList 统计信息列表
     * @param summary 汇总信息
     * @return 统计响应
     */
    public static FollowStatisticsResponse successWithSummary(List<FollowStatisticsInfo> statisticsList, StatisticsSummary summary) {
        FollowStatisticsResponse response = new FollowStatisticsResponse();
        response.setStatisticsList(statisticsList);
        response.setSummary(summary);
        response.setSuccess(true);
        return response;
    }

    /**
     * 用户不存在响应
     *
     * @param userId 用户ID
     * @return 统计响应
     */
    public static FollowStatisticsResponse userNotFound(Long userId) {
        FollowStatisticsResponse response = new FollowStatisticsResponse();
        response.setSuccess(false);
        response.setMessage("用户 " + userId + " 不存在");
        return response;
    }

    /**
     * 失败响应
     *
     * @param message 错误消息
     * @return 统计响应
     */
    public static FollowStatisticsResponse error(String message) {
        FollowStatisticsResponse response = new FollowStatisticsResponse();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }

    // ===================== 业务方法 =====================

    /**
     * 是否为单用户统计
     *
     * @return true-单用户统计，false-批量统计
     */
    public boolean isSingleUserStatistics() {
        return statisticsInfo != null;
    }

    /**
     * 是否为批量统计
     *
     * @return true-批量统计，false-单用户统计
     */
    public boolean isBatchStatistics() {
        return statisticsMap != null || statisticsList != null;
    }

    /**
     * 是否重新计算了统计信息
     *
     * @return true-重新计算，false-使用缓存
     */
    public boolean isRecalculated() {
        return Boolean.TRUE.equals(isRecalculated);
    }

    /**
     * 是否有汇总信息
     *
     * @return true-有汇总，false-无汇总
     */
    public boolean hasSummary() {
        return summary != null;
    }

    /**
     * 获取统计用户数量
     *
     * @return 统计用户数量
     */
    public int getStatisticsCount() {
        if (statisticsInfo != null) {
            return 1;
        }
        if (statisticsMap != null) {
            return statisticsMap.size();
        }
        if (statisticsList != null) {
            return statisticsList.size();
        }
        return 0;
    }
} 
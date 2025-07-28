package com.gig.collide.api.follow.response;

import com.gig.collide.base.response.BaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * 关注查询响应
 *
 * @param <T> 关注信息类型
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "关注查询响应")
public class FollowQueryResponse<T> extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 单个关注信息
     */
    @Schema(description = "单个关注信息")
    private T followInfo;

    /**
     * 关注信息列表
     */
    @Schema(description = "关注信息列表")
    private List<T> followList;

    /**
     * 总数量
     */
    @Schema(description = "总数量")
    private Long totalCount;

    /**
     * 是否还有更多数据
     */
    @Schema(description = "是否还有更多数据")
    private Boolean hasMore;

    // ===================== 静态工厂方法 =====================

    /**
     * 成功响应 - 单个关注信息
     *
     * @param followInfo 关注信息
     * @param <T> 关注信息类型
     * @return 查询响应
     */
    public static <T> FollowQueryResponse<T> success(T followInfo) {
        FollowQueryResponse<T> response = new FollowQueryResponse<>();
        response.setFollowInfo(followInfo);
        response.setSuccess(true);
        return response;
    }

    /**
     * 成功响应 - 关注信息列表
     *
     * @param followList 关注信息列表
     * @param <T> 关注信息类型
     * @return 查询响应
     */
    public static <T> FollowQueryResponse<T> success(List<T> followList) {
        FollowQueryResponse<T> response = new FollowQueryResponse<>();
        response.setFollowList(followList);
        response.setTotalCount(followList != null ? (long) followList.size() : 0L);
        response.setSuccess(true);
        return response;
    }

    /**
     * 成功响应 - 带总数的关注信息列表
     *
     * @param followList 关注信息列表
     * @param totalCount 总数量
     * @param <T> 关注信息类型
     * @return 查询响应
     */
    public static <T> FollowQueryResponse<T> success(List<T> followList, Long totalCount) {
        FollowQueryResponse<T> response = new FollowQueryResponse<>();
        response.setFollowList(followList);
        response.setTotalCount(totalCount);
        response.setHasMore(followList != null && followList.size() < totalCount);
        response.setSuccess(true);
        return response;
    }

    /**
     * 空结果响应
     *
     * @param <T> 关注信息类型
     * @return 查询响应
     */
    public static <T> FollowQueryResponse<T> empty() {
        FollowQueryResponse<T> response = new FollowQueryResponse<>();
        response.setTotalCount(0L);
        response.setHasMore(false);
        response.setSuccess(true);
        return response;
    }

    /**
     * 失败响应
     *
     * @param message 错误消息
     * @param <T> 关注信息类型
     * @return 查询响应
     */
    public static <T> FollowQueryResponse<T> failure(String message) {
        FollowQueryResponse<T> response = new FollowQueryResponse<>();
        response.setSuccess(false);
        response.setResponseMessage(message);
        return response;
    }

    // ===================== 业务方法 =====================

    /**
     * 是否为单个结果
     *
     * @return true-单个结果，false-列表结果
     */
    public boolean isSingleResult() {
        return followInfo != null;
    }

    /**
     * 是否为列表结果
     *
     * @return true-列表结果，false-单个结果
     */
    public boolean isListResult() {
        return followList != null;
    }

    /**
     * 是否为空结果
     *
     * @return true-空结果，false-有结果
     */
    public boolean isEmpty() {
        return followInfo == null && (followList == null || followList.isEmpty());
    }

    /**
     * 获取结果数量
     *
     * @return 结果数量
     */
    public int getResultCount() {
        if (followInfo != null) {
            return 1;
        }
        return followList != null ? followList.size() : 0;
    }

}
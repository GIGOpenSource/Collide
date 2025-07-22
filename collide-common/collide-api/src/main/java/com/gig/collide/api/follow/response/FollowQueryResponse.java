package com.gig.collide.api.follow.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 关注查询响应
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class FollowQueryResponse<T> extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 查询结果数据
     */
    private T data;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 创建成功响应
     *
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 查询响应
     */
    public static <T> FollowQueryResponse<T> success(T data) {
        FollowQueryResponse<T> response = new FollowQueryResponse<>();
        response.setSuccess(true);
        response.setData(data);
        return response;
    }

    /**
     * 创建成功响应（带总数）
     *
     * @param data 响应数据
     * @param total 总记录数
     * @param <T> 数据类型
     * @return 查询响应
     */
    public static <T> FollowQueryResponse<T> success(T data, Long total) {
        FollowQueryResponse<T> response = new FollowQueryResponse<>();
        response.setSuccess(true);
        response.setData(data);
        response.setTotal(total);
        return response;
    }
} 
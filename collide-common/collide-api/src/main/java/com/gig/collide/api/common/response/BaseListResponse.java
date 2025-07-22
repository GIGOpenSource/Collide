package com.gig.collide.api.common.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 通用列表响应基类
 * 所有返回列表数据的响应都继承此类
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseListResponse<T> extends BaseResponse {

    /**
     * 数据列表
     */
    private List<T> data;

    /**
     * 总数量
     */
    private Long total;

    /**
     * 创建成功的列表响应
     */
    public static <T> BaseListResponse<T> success(List<T> data, Long total) {
        BaseListResponse<T> response = new BaseListResponse<>();
        response.setSuccess(true);
        response.setData(data);
        response.setTotal(total);
        return response;
    }

    /**
     * 创建成功的列表响应（不带总数）
     */
    public static <T> BaseListResponse<T> success(List<T> data) {
        BaseListResponse<T> response = new BaseListResponse<>();
        response.setSuccess(true);
        response.setData(data);
        response.setTotal(data != null ? (long) data.size() : 0L);
        return response;
    }

    /**
     * 创建失败的列表响应
     */
    public static <T> BaseListResponse<T> error(String code, String message) {
        BaseListResponse<T> response = new BaseListResponse<>();
        response.setSuccess(false);
        response.setResponseCode(code);
        response.setResponseMessage(message);
        return response;
    }
} 
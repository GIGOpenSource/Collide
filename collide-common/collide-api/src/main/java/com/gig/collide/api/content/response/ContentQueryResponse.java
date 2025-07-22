package com.gig.collide.api.content.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * 内容查询响应类
 * 用于单个内容查询和内容统计查询的响应
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class ContentQueryResponse<T> extends BaseResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应数据
     */
    private T data;

    public ContentQueryResponse(T data) {
        super();
        this.data = data;
        this.setSuccess(true);
    }

    /**
     * 创建成功响应
     *
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 响应对象
     */
    public static <T> ContentQueryResponse<T> success(T data) {
        return new ContentQueryResponse<>(data);
    }

    /**
     * 创建失败响应
     *
     * @param errorCode 错误码
     * @param errorMessage 错误信息
     * @param <T> 数据类型
     * @return 响应对象
     */
    public static <T> ContentQueryResponse<T> error(String errorCode, String errorMessage) {
        ContentQueryResponse<T> response = new ContentQueryResponse<>();
        response.setSuccess(false);
        response.setResponseCode(errorCode);
        response.setResponseMessage(errorMessage);
        return response;
    }
} 
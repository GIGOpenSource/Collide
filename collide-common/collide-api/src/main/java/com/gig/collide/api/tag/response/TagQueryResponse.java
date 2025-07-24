package com.gig.collide.api.tag.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 标签查询响应
 *
 * @author GIG Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagQueryResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 查询是否成功
     */
    private Boolean success;

    /**
     * 错误码
     */
    private String errorCode;

    /**
     * 错误消息
     */
    private String errorMessage;

    /**
     * 查询结果数据
     */
    private T data;

    public static <T> TagQueryResponse<T> success(T data) {
        return TagQueryResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    public static <T> TagQueryResponse<T> error(String errorCode, String errorMessage) {
        return TagQueryResponse.<T>builder()
                .success(false)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build();
    }
} 
package com.gig.collide.api.category.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 分类操作响应
 *
 * @author GIG Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryOperatorResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 操作是否成功
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
     * 分类ID（创建时返回）
     */
    private Long categoryId;

    public static CategoryOperatorResponse success(Long categoryId) {
        return CategoryOperatorResponse.builder()
                .success(true)
                .categoryId(categoryId)
                .build();
    }

    public static CategoryOperatorResponse success() {
        return CategoryOperatorResponse.builder()
                .success(true)
                .build();
    }

    public static CategoryOperatorResponse error(String errorCode, String errorMessage) {
        return CategoryOperatorResponse.builder()
                .success(false)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build();
    }
} 
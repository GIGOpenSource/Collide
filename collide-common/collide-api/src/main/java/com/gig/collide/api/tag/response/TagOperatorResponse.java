package com.gig.collide.api.tag.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 标签操作响应
 *
 * @author GIG Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagOperatorResponse implements Serializable {

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
     * 标签ID（创建时返回）
     */
    private Long tagId;

    public static TagOperatorResponse success(Long tagId) {
        return TagOperatorResponse.builder()
                .success(true)
                .tagId(tagId)
                .build();
    }

    public static TagOperatorResponse success() {
        return TagOperatorResponse.builder()
                .success(true)
                .build();
    }

    public static TagOperatorResponse error(String errorCode, String errorMessage) {
        return TagOperatorResponse.builder()
                .success(false)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build();
    }
} 
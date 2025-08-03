package com.gig.collide.api.social.request;

import com.gig.collide.base.request.PageRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 互动查询请求
 * 
 * @author GIG Team
 * @since 2024-01-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InteractionQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "内容ID不能为空")
    private Long contentId;

    private Long userId; // 查询指定用户的互动

    private Integer interactionType; // 互动类型: 1-点赞, 2-收藏, 3-分享, 4-评论

    private Long parentCommentId; // 查询评论回复时使用

    private String sortBy = "create_time"; // 排序字段

    private String sortOrder = "DESC"; // 排序方向

    // 互动类型枚举
    public enum InteractionType {
        LIKE(1, "点赞"),
        FAVORITE(2, "收藏"),
        SHARE(3, "分享"),
        COMMENT(4, "评论");

        private final int code;
        private final String desc;

        InteractionType(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() { return code; }
        public String getDesc() { return desc; }
    }
}
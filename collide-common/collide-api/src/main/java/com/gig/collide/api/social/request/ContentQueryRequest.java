package com.gig.collide.api.social.request;

import com.gig.collide.base.request.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 内容查询请求
 * 
 * @author GIG Team
 * @since 2024-01-16
 */
@Data  
@EqualsAndHashCode(callSuper = true)
public class ContentQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId; // 查询指定用户的内容

    private Long categoryId; // 查询指定分类的内容

    private Integer contentType; // 内容类型过滤

    private String keyword; // 搜索关键词

    private Integer queryType; // 查询类型: 1-最新, 2-热门, 3-搜索

    private Long viewerUserId; // 查看者ID（用于权限检查）

    private String sortBy = "create_time"; // 排序字段

    private String sortOrder = "DESC"; // 排序方向

    // 查询类型枚举
    public enum QueryType {
        LATEST(1, "最新"),
        HOT(2, "热门"),
        SEARCH(3, "搜索");

        private final int code;
        private final String desc;

        QueryType(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() { return code; }
        public String getDesc() { return desc; }
    }
}
package com.gig.collide.api.tag.request;

import com.gig.collide.base.request.PageRequest;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

/**
 * 标签查询请求
 * 
 * @author GIG Team
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TagQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标签名称（模糊查询）
     */
    @Size(max = 50, message = "标签名称长度不能超过50个字符")
    private String tagName;

    /**
     * 标签状态：1-启用 0-禁用
     */
    private Integer status;

    /**
     * 最小权重
     */
    private Integer minWeight;

    /**
     * 最大权重
     */
    private Integer maxWeight;

    /**
     * 最小关注数
     */
    private Long minFollowCount;

    /**
     * 最大关注数
     */
    private Long maxFollowCount;

    /**
     * 最小内容数
     */
    private Long minContentCount;

    /**
     * 最大内容数
     */
    private Long maxContentCount;

    /**
     * 排序字段：weight, hotness, follow_count, content_count, create_time
     */
    private String sortField;

    /**
     * 排序方向：ASC, DESC
     */
    private String sortDirection = "DESC";
}
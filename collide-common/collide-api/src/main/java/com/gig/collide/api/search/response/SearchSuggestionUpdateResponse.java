package com.gig.collide.api.search.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 搜索建议更新响应
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
public class SearchSuggestionUpdateResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 建议ID
     */
    private Long suggestionId;

    /**
     * 建议关键词
     */
    private String keyword;

    /**
     * 更新前权重
     */
    private Integer beforeWeight;

    /**
     * 更新后权重
     */
    private Integer afterWeight;

    /**
     * 更新前启用状态
     */
    private Boolean beforeEnabled;

    /**
     * 更新后启用状态
     */
    private Boolean afterEnabled;

    /**
     * 更新时间
     */
    private String updateTime;

    /**
     * 更新人ID
     */
    private Long updaterId;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 更新是否成功
     */
    private Boolean updateSuccess;

    /**
     * 更新字段列表
     */
    private java.util.List<String> updatedFields;

    /**
     * 更新备注
     */
    private String remark;
} 
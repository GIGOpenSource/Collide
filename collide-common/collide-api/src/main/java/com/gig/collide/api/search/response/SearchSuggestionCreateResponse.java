package com.gig.collide.api.search.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 搜索建议创建响应
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
public class SearchSuggestionCreateResponse extends BaseResponse {

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
     * 建议类型
     */
    private String suggestionType;

    /**
     * 内容类型
     */
    private String contentType;

    /**
     * 搜索类型
     */
    private String searchType;

    /**
     * 权重
     */
    private Integer weight;

    /**
     * 显示文本
     */
    private String displayText;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 创建人ID
     */
    private Long creatorId;

    /**
     * 创建是否成功
     */
    private Boolean createSuccess;

    /**
     * 备注
     */
    private String remark;
} 
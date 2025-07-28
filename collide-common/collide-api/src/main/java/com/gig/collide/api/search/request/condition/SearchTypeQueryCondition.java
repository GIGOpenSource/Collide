package com.gig.collide.api.search.request.condition;

import com.gig.collide.api.search.constant.SearchTypeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 搜索类型查询条件
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class SearchTypeQueryCondition extends SearchQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 搜索类型
     */
    private SearchTypeEnum searchType;

    /**
     * 搜索类型字符串（兼容性字段）
     */
    private String searchTypeStr;

    public SearchTypeQueryCondition(SearchTypeEnum searchType) {
        this.searchType = searchType;
        this.searchTypeStr = searchType != null ? searchType.name() : null;
    }

    public SearchTypeQueryCondition(String searchTypeStr) {
        this.searchTypeStr = searchTypeStr;
        try {
            this.searchType = SearchTypeEnum.valueOf(searchTypeStr);
        } catch (Exception e) {
            this.searchType = null;
        }
    }

    @Override
    public String getConditionType() {
        return "SEARCH_TYPE";
    }

    @Override
    public boolean isValid() {
        return searchType != null || searchTypeStr != null;
    }
} 
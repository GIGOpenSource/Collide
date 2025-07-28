package com.gig.collide.api.search.request.condition;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * 关键词查询条件
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class KeywordQueryCondition extends SearchQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 搜索关键词
     */
    @NotBlank(message = "搜索关键词不能为空")
    @Size(min = 1, max = 255, message = "搜索关键词长度必须在1-255个字符之间")
    private String keyword;

    /**
     * 是否完全匹配
     */
    private Boolean exactMatch = false;

    /**
     * 是否忽略大小写
     */
    private Boolean ignoreCase = true;

    /**
     * 搜索字段范围
     */
    private String[] searchFields;

    public KeywordQueryCondition(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String getConditionType() {
        return "KEYWORD";
    }

    @Override
    public boolean isValid() {
        return StringUtils.isNotBlank(keyword);
    }
} 
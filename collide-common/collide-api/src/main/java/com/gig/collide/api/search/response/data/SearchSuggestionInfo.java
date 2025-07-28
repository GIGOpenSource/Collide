package com.gig.collide.api.search.response.data;

import com.gig.collide.api.search.constant.SuggestionTypeEnum;
import com.gig.collide.api.search.constant.SearchStatusEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 搜索建议信息传输对象
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class SearchSuggestionInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 建议ID
     */
    private Long id;

    /**
     * 建议关键词
     */
    private String keyword;

    /**
     * 建议类型
     */
    private SuggestionTypeEnum suggestionType;

    /**
     * 原始文本
     */
    private String originalText;

    /**
     * 高亮文本
     */
    private String highlightText;

    /**
     * 关联目标ID
     */
    private Long targetId;

    /**
     * 关联目标类型
     */
    private String targetType;

    /**
     * 关联目标标题
     */
    private String targetTitle;

    /**
     * 关联目标头像URL
     */
    private String targetAvatar;

    /**
     * 关联目标描述
     */
    private String targetDescription;

    /**
     * 搜索次数
     */
    private Long searchCount;

    /**
     * 点击次数
     */
    private Long clickCount;

    /**
     * 权重
     */
    private Double weight;

    /**
     * 相关度评分
     */
    private Double relevanceScore;

    /**
     * 质量评分
     */
    private Double qualityScore;

    /**
     * 是否手动配置
     */
    private Boolean isManual;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 状态
     */
    private SearchStatusEnum status;

    /**
     * 分类
     */
    private String category;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 扩展数据
     */
    private Map<String, Object> extraData;

    /**
     * 生效开始时间
     */
    private LocalDateTime startTime;

    /**
     * 生效结束时间
     */
    private LocalDateTime endTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    // ===================== 便捷方法 =====================

    /**
     * 检查建议是否有效
     */
    public boolean isValid() {
        if (status == null || !status.isAvailable()) {
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (startTime != null && now.isBefore(startTime)) {
            return false;
        }
        
        if (endTime != null && now.isAfter(endTime)) {
            return false;
        }
        
        return true;
    }

    /**
     * 检查是否为实体相关建议
     */
    public boolean isEntityRelated() {
        return suggestionType != null && suggestionType.isEntityRelated();
    }

    /**
     * 检查是否支持高亮
     */
    public boolean supportHighlight() {
        return suggestionType != null && suggestionType.supportHighlight();
    }

    /**
     * 获取点击率
     */
    public Double getClickRate() {
        if (searchCount == null || searchCount == 0) {
            return 0.0;
        }
        return clickCount != null ? (double) clickCount / searchCount : 0.0;
    }

    /**
     * 获取状态描述
     */
    public String getStatusDescription() {
        return status != null ? status.getDescription() : "未知";
    }
} 
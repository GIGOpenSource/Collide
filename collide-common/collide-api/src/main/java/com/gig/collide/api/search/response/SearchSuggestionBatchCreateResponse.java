package com.gig.collide.api.search.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 搜索建议批量创建响应
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
public class SearchSuggestionBatchCreateResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 批量任务ID
     */
    private String batchTaskId;

    /**
     * 总提交数量
     */
    private Integer totalSubmitted;

    /**
     * 成功创建数量
     */
    private Integer successCount;

    /**
     * 失败数量
     */
    private Integer failureCount;

    /**
     * 跳过数量（重复）
     */
    private Integer skippedCount;

    /**
     * 成功创建的建议列表
     */
    private List<CreatedSuggestion> successList;

    /**
     * 失败的建议列表
     */
    private List<FailedSuggestion> failureList;

    /**
     * 跳过的建议列表
     */
    private List<SkippedSuggestion> skippedList;

    /**
     * 创建开始时间
     */
    private String createStartTime;

    /**
     * 创建结束时间
     */
    private String createEndTime;

    /**
     * 创建耗时（毫秒）
     */
    private Long createDuration;

    /**
     * 创建人ID
     */
    private Long creatorId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 成功创建的建议
     */
    @Getter
    @Setter
    @ToString
    public static class CreatedSuggestion {
        private Long suggestionId;
        private String keyword;
        private String suggestionType;
        private Integer weight;
        private String createTime;
    }

    /**
     * 失败的建议
     */
    @Getter
    @Setter
    @ToString
    public static class FailedSuggestion {
        private String keyword;
        private String suggestionType;
        private String failureReason;
        private String errorCode;
    }

    /**
     * 跳过的建议
     */
    @Getter
    @Setter
    @ToString
    public static class SkippedSuggestion {
        private String keyword;
        private String suggestionType;
        private String skipReason;
        private Long existingSuggestionId;
    }
} 
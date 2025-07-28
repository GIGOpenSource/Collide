package com.gig.collide.api.search.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 搜索建议导入响应
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
public class SearchSuggestionImportResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 导入任务ID
     */
    private String importTaskId;

    /**
     * 导入类型
     */
    private String importType;

    /**
     * 数据格式
     */
    private String dataFormat;

    /**
     * 导入状态（processing/completed/failed）
     */
    private String importStatus;

    /**
     * 导入进度（0-100）
     */
    private Integer progress;

    /**
     * 总记录数
     */
    private Integer totalRecords;

    /**
     * 成功导入数量
     */
    private Integer successCount;

    /**
     * 失败数量
     */
    private Integer failureCount;

    /**
     * 跳过数量
     */
    private Integer skippedCount;

    /**
     * 导入结果摘要
     */
    private ImportSummary summary;

    /**
     * 失败记录列表（前100条）
     */
    private List<FailedRecord> failedRecords;

    /**
     * 导入开始时间
     */
    private String importStartTime;

    /**
     * 导入结束时间
     */
    private String importEndTime;

    /**
     * 导入耗时（毫秒）
     */
    private Long importDuration;

    /**
     * 导入人ID
     */
    private Long importerId;

    /**
     * 是否异步处理
     */
    private Boolean asyncProcess;

    /**
     * 回调URL
     */
    private String callbackUrl;

    /**
     * 错误日志文件下载链接
     */
    private String errorLogUrl;

    /**
     * 备注
     */
    private String remark;

    /**
     * 导入结果摘要
     */
    @Getter
    @Setter
    @ToString
    public static class ImportSummary {
        private Double successRate;
        private String mostCommonErrorType;
        private String mostPopularSuggestionType;
        private Integer avgWeight;
        private Integer duplicateCount;
        private Integer validationErrorCount;
        private Integer businessRuleErrorCount;
    }

    /**
     * 失败记录
     */
    @Getter
    @Setter
    @ToString
    public static class FailedRecord {
        private Integer lineNumber;
        private String keyword;
        private String suggestionType;
        private String errorType;
        private String errorMessage;
        private String rawData;
    }
} 
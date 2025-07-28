package com.gig.collide.api.search.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

/**
 * 搜索分析报告请求
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SearchAnalysisReportRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 报告类型（必填）：overview/trending/user_behavior/keyword_analysis/performance
     */
    @NotBlank(message = "报告类型不能为空")
    @Pattern(regexp = "^(overview|trending|user_behavior|keyword_analysis|performance)$", 
             message = "报告类型只能是overview、trending、user_behavior、keyword_analysis或performance")
    private String reportType;

    /**
     * 报告时间范围（必填）：today/week/month/quarter/year/custom
     */
    @NotBlank(message = "报告时间范围不能为空")
    @Pattern(regexp = "^(today|week|month|quarter|year|custom)$", 
             message = "报告时间范围只能是today、week、month、quarter、year或custom")
    private String timeRange;

    /**
     * 开始日期（时间范围为custom时必填，格式：YYYY-MM-DD）
     */
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "开始日期格式必须为YYYY-MM-DD")
    private String startDate;

    /**
     * 结束日期（时间范围为custom时必填，格式：YYYY-MM-DD）
     */
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "结束日期格式必须为YYYY-MM-DD")
    private String endDate;

    /**
     * 分析维度（可选）：keyword/user/content/time/device
     */
    private List<String> dimensions;

    /**
     * 指定用户ID（用户行为分析时可选）
     */
    private Long userId;

    /**
     * 指定关键词列表（关键词分析时可选）
     */
    private List<String> keywords;

    /**
     * 内容类型过滤（可选）
     */
    private List<String> contentTypes;

    /**
     * 搜索类型过滤（可选）
     */
    private List<String> searchTypes;

    /**
     * 报告格式（可选）：json/excel/pdf
     */
    @Pattern(regexp = "^(json|excel|pdf)$", message = "报告格式只能是json、excel或pdf")
    private String format = "json";

    /**
     * 是否包含图表数据
     */
    private Boolean includeCharts = true;

    /**
     * 是否包含明细数据
     */
    private Boolean includeDetails = false;

    /**
     * Top排行榜数量限制（可选，默认为10）
     */
    private Integer topLimit = 10;

    /**
     * 请求人ID（必填）
     */
    @NotNull(message = "请求人ID不能为空")
    private Long requesterId;

    /**
     * 报告标题（可选）
     */
    @Size(max = 100, message = "报告标题长度不能超过100个字符")
    private String title;

    /**
     * 报告描述（可选）
     */
    @Size(max = 500, message = "报告描述长度不能超过500个字符")
    private String description;
} 
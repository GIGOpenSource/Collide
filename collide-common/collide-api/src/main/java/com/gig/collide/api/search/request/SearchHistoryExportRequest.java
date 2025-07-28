package com.gig.collide.api.search.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

/**
 * 搜索历史导出请求
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
public class SearchHistoryExportRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID（必填）
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 导出格式（可选）：excel/csv/json
     */
    @Pattern(regexp = "^(excel|csv|json)$", message = "导出格式只能是excel、csv或json")
    private String format = "excel";

    /**
     * 开始时间（可选，格式：YYYY-MM-DD HH:mm:ss）
     */
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$", 
             message = "开始时间格式必须为YYYY-MM-DD HH:mm:ss")
    private String startTime;

    /**
     * 结束时间（可选，格式：YYYY-MM-DD HH:mm:ss）
     */
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$", 
             message = "结束时间格式必须为YYYY-MM-DD HH:mm:ss")
    private String endTime;

    /**
     * 关键词过滤（可选）
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
     * 导出字段（可选，默认导出所有字段）
     */
    private List<String> exportFields;

    /**
     * 排序字段（可选）
     */
    private String sortField = "search_time";

    /**
     * 排序方向（可选）：asc/desc
     */
    @Pattern(regexp = "^(asc|desc)$", message = "排序方向只能是asc或desc")
    private String sortOrder = "desc";

    /**
     * 最大导出记录数（可选，默认为10000）
     */
    private Integer maxRecords = 10000;

    /**
     * 文件名（可选）
     */
    @Size(max = 100, message = "文件名长度不能超过100个字符")
    private String fileName;

    /**
     * 导出备注（可选）
     */
    @Size(max = 200, message = "导出备注长度不能超过200个字符")
    private String remark;
} 
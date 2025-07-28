package com.gig.collide.api.search.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

/**
 * 搜索历史批量删除请求
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
public class SearchHistoryBatchDeleteRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID（必填）
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 删除类型（必填）：by_ids/by_keywords/by_time_range/all
     */
    @NotNull(message = "删除类型不能为空")
    @Pattern(regexp = "^(by_ids|by_keywords|by_time_range|all)$", 
             message = "删除类型只能是by_ids、by_keywords、by_time_range或all")
    private String deleteType;

    /**
     * 历史记录ID列表（删除类型为by_ids时必填）
     */
    private List<Long> historyIds;

    /**
     * 关键词列表（删除类型为by_keywords时必填）
     */
    private List<String> keywords;

    /**
     * 开始时间（删除类型为by_time_range时必填，格式：YYYY-MM-DD HH:mm:ss）
     */
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$", 
             message = "开始时间格式必须为YYYY-MM-DD HH:mm:ss")
    private String startTime;

    /**
     * 结束时间（删除类型为by_time_range时必填，格式：YYYY-MM-DD HH:mm:ss）
     */
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$", 
             message = "结束时间格式必须为YYYY-MM-DD HH:mm:ss")
    private String endTime;

    /**
     * 内容类型过滤（可选）
     */
    private List<String> contentTypes;

    /**
     * 搜索类型过滤（可选）
     */
    private List<String> searchTypes;

    /**
     * 删除原因（可选）
     */
    @Size(max = 200, message = "删除原因长度不能超过200个字符")
    private String reason;

    /**
     * 是否需要确认（默认为true）
     */
    private Boolean needConfirm = true;
} 
package com.gig.collide.api.search.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 搜索统计更新请求
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
public class SearchStatisticsUpdateRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 统计记录ID（必填）
     */
    @NotNull(message = "统计记录ID不能为空")
    private Long statisticsId;

    /**
     * 搜索关键词（必填）
     */
    @NotBlank(message = "搜索关键词不能为空")
    @Size(max = 100, message = "搜索关键词长度不能超过100个字符")
    private String keyword;

    /**
     * 内容类型（可选）
     */
    private String contentType;

    /**
     * 搜索类型（可选）
     */
    private String searchType;

    /**
     * 搜索次数增量（可选，默认为1）
     */
    @Min(value = 0, message = "搜索次数增量不能小于0")
    private Integer searchCountIncrement = 1;

    /**
     * 用户数增量（可选，默认为0）
     */
    @Min(value = 0, message = "用户数增量不能小于0")
    private Integer userCountIncrement = 0;

    /**
     * 点击次数增量（可选，默认为0）
     */
    @Min(value = 0, message = "点击次数增量不能小于0")
    private Integer clickCountIncrement = 0;

    /**
     * 转化次数增量（可选，默认为0）
     */
    @Min(value = 0, message = "转化次数增量不能小于0")
    private Integer conversionCountIncrement = 0;

    /**
     * 平均停留时长（毫秒）
     */
    @Min(value = 0, message = "平均停留时长不能小于0")
    private Long avgDurationMs;

    /**
     * 热度评分（可选）
     */
    @Min(value = 0, message = "热度评分不能小于0")
    private Double hotScore;

    /**
     * 操作人ID（必填）
     */
    @NotNull(message = "操作人ID不能为空")
    private Long operatorId;

    /**
     * 版本号（乐观锁）
     */
    private Integer version;

    /**
     * 更新备注（可选）
     */
    @Size(max = 200, message = "更新备注长度不能超过200个字符")
    private String remark;
} 
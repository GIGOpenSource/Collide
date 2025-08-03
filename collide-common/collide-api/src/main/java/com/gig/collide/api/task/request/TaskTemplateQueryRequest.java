package com.gig.collide.api.task.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.time.LocalDate;

/**
 * 任务模板查询请求DTO - 优化版
 * 使用数字常量替代字符串枚举，提升查询性能
 * 
 * @author GIG Team
 * @version 3.0.0 (优化版)  
 * @since 2024-01-16
 */
@Data
@Accessors(chain = true)
@Schema(description = "任务模板查询请求")
public class TaskTemplateQueryRequest {

    @Schema(description = "任务名称（模糊搜索）")
    private String taskName;

    @Schema(description = "任务类型: 1-每日任务, 2-周常任务, 3-月度任务, 4-成就任务", 
            allowableValues = {"1", "2", "3", "4"})
    private Integer taskType;

    @Schema(description = "任务分类: 1-登录, 2-内容, 3-社交, 4-消费, 5-邀请", 
            allowableValues = {"1", "2", "3", "4", "5"})
    private Integer taskCategory;

    @Schema(description = "任务动作: 1-登录, 2-发布内容, 3-点赞, 4-评论, 5-分享, 6-购买, 7-邀请用户", 
            allowableValues = {"1", "2", "3", "4", "5", "6", "7"})
    private Integer taskAction;

    @Schema(description = "是否启用")
    private Boolean isActive;

    @Schema(description = "开始日期（查询范围）")
    private LocalDate startDate;

    @Schema(description = "结束日期（查询范围）")
    private LocalDate endDate;

    @Schema(description = "排序字段", allowableValues = {"id", "taskName", "taskType", "sortOrder", "createTime"})
    private String orderBy = "sortOrder";

    @Schema(description = "排序方向", allowableValues = {"ASC", "DESC"})
    private String orderDirection = "ASC";

    @Min(value = 1, message = "页码必须大于0")
    @Schema(description = "当前页码", defaultValue = "1")
    private Integer currentPage = 1;

    @Min(value = 1, message = "页面大小必须大于0")
    @Max(value = 100, message = "页面大小不能超过100")
    @Schema(description = "页面大小", defaultValue = "20")
    private Integer pageSize = 20;
}
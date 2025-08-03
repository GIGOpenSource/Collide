package com.gig.collide.api.task.request;

import com.gig.collide.base.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 用户任务查询请求DTO - 签到专用
 * 用于查询用户签到记录的分页请求
 * 
 * @author GIG Team
 * @version 1.0.0
 * @since 2024-01-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Schema(description = "用户任务查询请求")
public class UserTaskQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID", example = "1001")
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "任务模板ID", example = "1")
    private Long taskTemplateId;

    @Schema(description = "开始日期", example = "2024-01-01")
    private LocalDate startDate;

    @Schema(description = "结束日期", example = "2024-01-31")
    private LocalDate endDate;

    @Schema(description = "连续签到天数范围-最小值", example = "7")
    private Integer minContinuousDays;

    @Schema(description = "连续签到天数范围-最大值", example = "30")
    private Integer maxContinuousDays;
}
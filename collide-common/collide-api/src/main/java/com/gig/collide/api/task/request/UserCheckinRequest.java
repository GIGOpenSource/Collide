package com.gig.collide.api.task.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 用户签到请求DTO
 * 简洁的签到请求数据传输对象
 * 
 * @author GIG Team
 * @version 1.0.0
 * @since 2024-01-16
 */
@Data
@Accessors(chain = true)
@Schema(description = "用户签到请求")
public class UserCheckinRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID", example = "1001")
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "任务模板ID", example = "1", defaultValue = "1")
    private Long taskTemplateId = 1L; // 默认每日签到任务

    @Schema(description = "签到IP地址", example = "192.168.1.100")
    private String checkinIp;
}
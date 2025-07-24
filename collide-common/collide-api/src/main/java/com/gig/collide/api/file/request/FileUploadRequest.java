package com.gig.collide.api.file.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 文件上传请求
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@Schema(description = "文件上传请求")
public class FileUploadRequest {

    /**
     * 文件类型分类
     * avatar: 头像
     * content: 内容图片/视频
     * attachment: 附件
     */
    @NotBlank(message = "文件类型不能为空")
    @Pattern(regexp = "^(avatar|content|attachment)$", message = "文件类型只能是 avatar、content 或 attachment")
    @Schema(description = "文件类型", example = "avatar", allowableValues = {"avatar", "content", "attachment"})
    private String fileType;

    /**
     * 文件名称（原始文件名）
     */
    @NotBlank(message = "文件名不能为空")
    @Schema(description = "文件名称", example = "profile.jpg")
    private String fileName;

    /**
     * 业务ID（可选，如用户ID、内容ID等）
     */
    @Schema(description = "业务ID", example = "12345")
    private Long businessId;

    /**
     * 文件描述（可选）
     */
    @Schema(description = "文件描述", example = "用户头像")
    private String description;
} 
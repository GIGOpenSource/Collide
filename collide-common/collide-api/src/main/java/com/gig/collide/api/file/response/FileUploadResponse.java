package com.gig.collide.api.file.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 文件上传响应
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文件上传响应")
public class FileUploadResponse {

    /**
     * 文件ID（系统生成的唯一标识）
     */
    @Schema(description = "文件ID", example = "20240101123456789")
    private String fileId;

    /**
     * 文件访问URL
     */
    @Schema(description = "文件访问URL", example = "https://cdn.collide.com/avatar/20240101/profile.jpg")
    private String fileUrl;

    /**
     * 文件相对路径
     */
    @Schema(description = "文件相对路径", example = "avatar/20240101/profile.jpg")
    private String filePath;

    /**
     * 原始文件名
     */
    @Schema(description = "原始文件名", example = "profile.jpg")
    private String originalFileName;

    /**
     * 文件类型
     */
    @Schema(description = "文件类型", example = "avatar")
    private String fileType;

    /**
     * 文件大小（字节）
     */
    @Schema(description = "文件大小", example = "1024000")
    private Long fileSize;

    /**
     * 文件MIME类型
     */
    @Schema(description = "文件MIME类型", example = "image/jpeg")
    private String mimeType;

    /**
     * 上传时间
     */
    @Schema(description = "上传时间")
    private LocalDateTime uploadTime;

    /**
     * 上传用户ID
     */
    @Schema(description = "上传用户ID", example = "12345")
    private Long uploadUserId;
} 
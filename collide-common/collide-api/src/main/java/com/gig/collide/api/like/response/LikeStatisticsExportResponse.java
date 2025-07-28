package com.gig.collide.api.like.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 点赞统计导出响应
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@Schema(description = "点赞统计导出响应")
public class LikeStatisticsExportResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 导出文件URL
     */
    @Schema(description = "导出文件URL")
    private String fileUrl;

    /**
     * 文件名
     */
    @Schema(description = "文件名")
    private String fileName;

    /**
     * 文件大小
     */
    @Schema(description = "文件大小")
    private Long fileSize;

    /**
     * 创建成功响应
     */
    public static LikeStatisticsExportResponse success(String fileUrl, String fileName, Long fileSize) {
        LikeStatisticsExportResponse response = new LikeStatisticsExportResponse();
        response.setFileUrl(fileUrl);
        response.setFileName(fileName);
        response.setFileSize(fileSize);
        response.setResponseCode("SUCCESS");
        response.setResponseMessage("导出成功");
        return response;
    }

    /**
     * 创建失败响应
     */
    public static LikeStatisticsExportResponse error(String errorCode, String errorMessage) {
        LikeStatisticsExportResponse response = new LikeStatisticsExportResponse();
        response.setResponseCode(errorCode);
        response.setResponseMessage(errorMessage);
        return response;
    }
} 
package com.gig.collide.api.like.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 点赞统计报告响应
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@Schema(description = "点赞统计报告响应")
public class LikeStatisticsReportResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 报告内容
     */
    @Schema(description = "报告内容")
    private String reportContent;

    /**
     * 报告文件URL
     */
    @Schema(description = "报告文件URL")
    private String reportFileUrl;

    /**
     * 创建成功响应
     */
    public static LikeStatisticsReportResponse success(String reportContent, String reportFileUrl) {
        LikeStatisticsReportResponse response = new LikeStatisticsReportResponse();
        response.setReportContent(reportContent);
        response.setReportFileUrl(reportFileUrl);
        response.setResponseCode("SUCCESS");
        response.setResponseMessage("报告生成成功");
        return response;
    }

    /**
     * 创建失败响应
     */
    public static LikeStatisticsReportResponse error(String errorCode, String errorMessage) {
        LikeStatisticsReportResponse response = new LikeStatisticsReportResponse();
        response.setResponseCode(errorCode);
        response.setResponseMessage(errorMessage);
        return response;
    }
} 
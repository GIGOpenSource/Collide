package com.gig.collide.api.search.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 搜索历史导出响应
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
public class SearchHistoryExportResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 导出任务ID
     */
    private String exportTaskId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 导出格式
     */
    private String format;

    /**
     * 导出文件名
     */
    private String fileName;

    /**
     * 导出文件大小（字节）
     */
    private Long fileSize;

    /**
     * 导出记录数
     */
    private Integer recordCount;

    /**
     * 导出状态（processing/completed/failed）
     */
    private String exportStatus;

    /**
     * 导出进度（0-100）
     */
    private Integer progress;

    /**
     * 文件下载链接
     */
    private String downloadUrl;

    /**
     * 文件过期时间
     */
    private String expireTime;

    /**
     * 导出开始时间
     */
    private String exportStartTime;

    /**
     * 导出完成时间
     */
    private String exportEndTime;

    /**
     * 导出耗时（毫秒）
     */
    private Long exportDuration;

    /**
     * 导出失败原因（如果失败）
     */
    private String failureReason;

    /**
     * 备注
     */
    private String remark;
} 
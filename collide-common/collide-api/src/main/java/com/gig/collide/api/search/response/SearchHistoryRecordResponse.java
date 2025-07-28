package com.gig.collide.api.search.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 搜索历史记录响应
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
public class SearchHistoryRecordResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 历史记录ID
     */
    private Long historyId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 内容类型
     */
    private String contentType;

    /**
     * 搜索类型
     */
    private String searchType;

    /**
     * 搜索结果数量
     */
    private Integer resultCount;

    /**
     * 搜索时间
     */
    private String searchTime;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 搜索会话ID
     */
    private String sessionId;

    /**
     * 来源页面
     */
    private String sourcePage;

    /**
     * 记录是否成功
     */
    private Boolean recordSuccess;

    /**
     * 记录时间
     */
    private String recordTime;

    /**
     * 备注信息
     */
    private String remark;
} 
package com.gig.collide.business.domain.search.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 搜索历史实体
 * 对应 t_search_history 表
 *
 * @author GIG Team
 */
@Data
@TableName("t_search_history")
public class SearchHistory {

    /**
     * 搜索历史ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 搜索关键词
     */
    @TableField("keyword")
    private String keyword;

    /**
     * 搜索类型
     */
    @TableField("search_type")
    private String searchType;

    /**
     * 内容类型过滤
     */
    @TableField("content_type")
    private String contentType;

    /**
     * 搜索结果数量
     */
    @TableField("result_count")
    private Long resultCount;

    /**
     * 搜索耗时（毫秒）
     */
    @TableField("search_time")
    private Long searchTime;

    /**
     * IP地址
     */
    @TableField("ip_address")
    private String ipAddress;

    /**
     * 设备信息
     */
    @TableField("device_info")
    private String deviceInfo;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
} 
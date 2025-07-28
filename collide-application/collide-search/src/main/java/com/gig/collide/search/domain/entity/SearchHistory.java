package com.gig.collide.search.domain.entity;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 搜索历史表实体
 * 
 * @author Collide Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_search_history")
public class SearchHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 搜索历史ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID（可为空，支持匿名搜索）
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 用户昵称（冗余字段，去连表化设计）
     */
    @TableField("user_nickname")
    private String userNickname;

    /**
     * 用户头像URL（冗余字段，去连表化设计）
     */
    @TableField("user_avatar")
    private String userAvatar;

    /**
     * 用户角色（冗余字段）：user/vip/blogger/admin
     */
    @TableField("user_role")
    private String userRole;

    /**
     * 搜索关键词
     */
    @TableField("keyword")
    private String keyword;

    /**
     * 搜索类型：ALL-综合搜索, USER-用户搜索, CONTENT-内容搜索, COMMENT-评论搜索
     */
    @TableField("search_type")
    private String searchType;

    /**
     * 内容类型过滤：NOVEL/COMIC/SHORT_VIDEO/LONG_VIDEO/ARTICLE/AUDIO
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
     * 用户代理信息
     */
    @TableField("user_agent")
    private String userAgent;

    /**
     * 应用版本
     */
    @TableField("app_version")
    private String appVersion;

    /**
     * 搜索过滤条件，JSON格式
     */
    @TableField("filters")
    private String filters;

    /**
     * 排序类型：RELEVANCE-相关度，TIME-时间，POPULARITY-热度
     */
    @TableField("sort_type")
    private String sortType;

    /**
     * 扩展数据
     */
    @TableField("extra_data")
    private String extraData;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 逻辑删除：0-未删除，1-已删除
     */
    @TableField("deleted")
    @TableLogic
    private Integer deleted;

    /**
     * 获取过滤条件对象
     */
    public <T> T getFiltersObject(Class<T> clazz) {
        if (filters == null) {
            return null;
        }
        return JSON.parseObject(filters, clazz);
    }

    /**
     * 设置过滤条件对象
     */
    public void setFiltersObject(Object filtersObject) {
        this.filters = filtersObject == null ? null : JSON.toJSONString(filtersObject);
    }

    /**
     * 获取扩展数据对象
     */
    public <T> T getExtraDataObject(Class<T> clazz) {
        if (extraData == null) {
            return null;
        }
        return JSON.parseObject(extraData, clazz);
    }

    /**
     * 设置扩展数据对象
     */
    public void setExtraDataObject(Object extraDataObject) {
        this.extraData = extraDataObject == null ? null : JSON.toJSONString(extraDataObject);
    }
} 
package com.gig.collide.api.search.response.data;

import com.gig.collide.api.search.constant.SearchTypeEnum;
import com.gig.collide.api.search.constant.ContentTypeEnum;
import com.gig.collide.api.search.constant.SortTypeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 搜索历史信息传输对象
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class SearchHistoryInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 搜索历史ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String userNickname;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户角色
     */
    private String userRole;

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 搜索类型
     */
    private SearchTypeEnum searchType;

    /**
     * 内容类型
     */
    private ContentTypeEnum contentType;

    /**
     * 排序类型
     */
    private SortTypeEnum sortType;

    /**
     * 搜索结果数量
     */
    private Long resultCount;

    /**
     * 搜索耗时（毫秒）
     */
    private Long searchTime;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 设备信息
     */
    private String deviceInfo;

    /**
     * 用户代理信息
     */
    private String userAgent;

    /**
     * 应用版本
     */
    private String appVersion;

    /**
     * 搜索过滤条件
     */
    private Map<String, Object> filters;

    /**
     * 扩展数据
     */
    private Map<String, Object> extraData;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 是否被删除
     */
    private Boolean deleted;

    // ===================== 便捷方法 =====================

    /**
     * 检查是否为匿名搜索
     */
    public boolean isAnonymous() {
        return userId == null;
    }

    /**
     * 获取格式化的搜索耗时
     */
    public String getFormattedSearchTime() {
        if (searchTime == null) {
            return "未知";
        }
        if (searchTime < 1000) {
            return searchTime + "ms";
        } else {
            return String.format("%.2fs", searchTime / 1000.0);
        }
    }

    /**
     * 获取搜索类型描述
     */
    public String getSearchTypeDescription() {
        return searchType != null ? searchType.getDescription() : "未知";
    }
} 
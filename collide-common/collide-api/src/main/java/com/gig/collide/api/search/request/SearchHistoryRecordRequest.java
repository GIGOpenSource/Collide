package com.gig.collide.api.search.request;

import com.gig.collide.api.search.constant.ContentTypeEnum;
import com.gig.collide.api.search.constant.SearchTypeEnum;
import com.gig.collide.api.search.constant.SortTypeEnum;
import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.Map;

/**
 * 搜索历史记录请求
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SearchHistoryRecordRequest extends BaseRequest {

    /**
     * 用户ID（可为空，支持匿名搜索）
     */
    private Long userId;

    /**
     * 搜索关键词（必填）
     */
    @NotBlank(message = "搜索关键词不能为空")
    @Size(min = 1, max = 255, message = "搜索关键词长度必须在1-255个字符之间")
    private String keyword;

    /**
     * 搜索类型
     */
    private SearchTypeEnum searchType = SearchTypeEnum.ALL;

    /**
     * 内容类型过滤
     */
    private ContentTypeEnum contentType;

    /**
     * 排序类型
     */
    private SortTypeEnum sortType = SortTypeEnum.RELEVANCE;

    /**
     * 搜索结果数量
     */
    @Min(value = 0, message = "搜索结果数量不能为负数")
    private Long resultCount = 0L;

    /**
     * 搜索耗时（毫秒）
     */
    @Min(value = 0, message = "搜索耗时不能为负数")
    private Long searchTime = 0L;

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
     * 搜索过滤条件（JSON格式）
     */
    private Map<String, Object> filters;

    /**
     * 扩展数据
     */
    private Map<String, Object> extraData;

    /**
     * 用户昵称（冗余字段，避免连表查询）
     */
    private String userNickname;

    /**
     * 用户头像（冗余字段）
     */
    private String userAvatar;

    /**
     * 用户角色（冗余字段）
     */
    private String userRole;

    // ===================== 便捷构造器 =====================

    /**
     * 创建匿名搜索记录
     */
    public SearchHistoryRecordRequest(String keyword, SearchTypeEnum searchType) {
        this.keyword = keyword;
        this.searchType = searchType;
    }

    /**
     * 创建用户搜索记录
     */
    public SearchHistoryRecordRequest(Long userId, String keyword, SearchTypeEnum searchType) {
        this.userId = userId;
        this.keyword = keyword;
        this.searchType = searchType;
    }

    // ===================== 便捷方法 =====================

    /**
     * 设置搜索结果
     */
    public SearchHistoryRecordRequest withResult(Long resultCount, Long searchTime) {
        this.resultCount = resultCount;
        this.searchTime = searchTime;
        return this;
    }

    /**
     * 设置设备信息
     */
    public SearchHistoryRecordRequest withDevice(String deviceInfo, String userAgent, String appVersion) {
        this.deviceInfo = deviceInfo;
        this.userAgent = userAgent;
        this.appVersion = appVersion;
        return this;
    }

    /**
     * 设置用户信息（冗余字段）
     */
    public SearchHistoryRecordRequest withUserInfo(String userNickname, String userAvatar, String userRole) {
        this.userNickname = userNickname;
        this.userAvatar = userAvatar;
        this.userRole = userRole;
        return this;
    }

    /**
     * 检查是否为匿名搜索
     */
    public boolean isAnonymous() {
        return userId == null;
    }
} 
package com.gig.collide.api.search.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 搜索建议点击记录请求
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SearchSuggestionClickRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 建议ID（必填）
     */
    @NotNull(message = "建议ID不能为空")
    private Long suggestionId;

    /**
     * 建议关键词（必填）
     */
    @NotBlank(message = "建议关键词不能为空")
    @Size(max = 100, message = "建议关键词长度不能超过100个字符")
    private String keyword;

    /**
     * 用户ID（可选）
     */
    private Long userId;

    /**
     * 建议类型（可选）
     */
    private String suggestionType;

    /**
     * 内容类型（可选）
     */
    private String contentType;

    /**
     * 搜索类型（可选）
     */
    private String searchType;

    /**
     * 点击位置（可选）
     */
    private Integer clickPosition;

    /**
     * 搜索会话ID（可选）
     */
    private String sessionId;

    /**
     * 来源页面（可选）
     */
    @Size(max = 200, message = "来源页面长度不能超过200个字符")
    private String sourcePage;

    /**
     * 来源URL（可选）
     */
    @Size(max = 500, message = "来源URL长度不能超过500个字符")
    private String sourceUrl;

    /**
     * 设备类型（可选）：mobile/tablet/desktop
     */
    private String deviceType;

    /**
     * 用户代理（可选）
     */
    @Size(max = 500, message = "用户代理长度不能超过500个字符")
    private String userAgent;

    /**
     * IP地址（可选）
     */
    private String ipAddress;

    /**
     * 地理位置（可选）
     */
    private String location;

    /**
     * 点击时间戳（可选，格式：YYYY-MM-DD HH:mm:ss）
     */
    private String clickTime;

    /**
     * 扩展信息（可选）
     */
    @Size(max = 1000, message = "扩展信息长度不能超过1000个字符")
    private String extraInfo;
} 
package com.gig.collide.api.order.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 内容访问权限查询请求
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class OrderContentAccessRequest extends BaseRequest {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 内容ID
     */
    @NotNull(message = "内容ID不能为空")
    private Long contentId;

    /**
     * 内容类型: VIDEO-视频, ARTICLE-文章, LIVE-直播, COURSE-课程
     */
    private String contentType;

    /**
     * 检查权限类型: READ-查看权限, DOWNLOAD-下载权限, SHARE-分享权限
     */
    private String checkType = "READ";

    /**
     * 客户端IP地址
     */
    private String clientIp;

    /**
     * 设备信息
     */
    private String deviceInfo;
} 
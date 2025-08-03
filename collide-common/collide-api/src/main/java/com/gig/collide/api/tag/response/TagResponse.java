package com.gig.collide.api.tag.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 标签响应DTO
 * 
 * @author GIG Team
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "标签信息")
public class TagResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标签ID
     */
    @Schema(description = "标签ID", example = "1")
    private Long id;

    /**
     * 标签名称
     */
    @Schema(description = "标签名称", example = "技术")
    private String tagName;

    /**
     * 标签描述
     */
    @Schema(description = "标签描述", example = "科技相关内容")
    private String tagDescription;

    /**
     * 标签图标URL
     */
    @Schema(description = "标签图标URL", example = "https://example.com/icons/tech.png")
    private String tagIcon;

    /**
     * 权重（1-100）
     */
    @Schema(description = "权重", example = "80")
    private Integer weight;

    /**
     * 热度值
     */
    @Schema(description = "热度值", example = "1520")
    private Long hotness;

    /**
     * 关注人数
     */
    @Schema(description = "关注人数", example = "1245")
    private Long followCount;

    /**
     * 内容数量
     */
    @Schema(description = "内容数量", example = "856")
    private Long contentCount;

    /**
     * 状态：1-启用 0-禁用
     */
    @Schema(description = "状态", example = "1")
    private Integer status;

    /**
     * 状态描述
     */
    @Schema(description = "状态描述", example = "启用")
    private String statusDesc;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2024-01-16T10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间", example = "2024-01-16T15:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 用户是否已关注（查询时可选填充）
     */
    @Schema(description = "用户是否已关注", example = "true")
    private Boolean isFollowed;

    /**
     * 推荐分数（推荐查询时使用）
     */
    @Schema(description = "推荐分数", example = "85.6")
    private Double recommendScore;

    /**
     * 获取状态描述
     */
    public String getStatusDesc() {
        if (this.status == null) {
            return "未知";
        }
        return this.status == 1 ? "启用" : "禁用";
    }
}
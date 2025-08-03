package com.gig.collide.api.tag.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 内容标签关系响应DTO
 * 
 * @author GIG Team
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "内容标签关系信息")
public class ContentTagResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关系ID
     */
    @Schema(description = "关系ID", example = "2001")
    private Long id;

    /**
     * 内容ID
     */
    @Schema(description = "内容ID", example = "789012")
    private Long contentId;

    /**
     * 标签ID
     */
    @Schema(description = "标签ID", example = "1")
    private Long tagId;

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
     * 标签权重
     */
    @Schema(description = "标签权重", example = "80")
    private Integer tagWeight;

    /**
     * 标签热度
     */
    @Schema(description = "标签热度", example = "1520")
    private Long tagHotness;

    /**
     * 打标签时间
     */
    @Schema(description = "打标签时间", example = "2024-01-16T10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tagTime;
}
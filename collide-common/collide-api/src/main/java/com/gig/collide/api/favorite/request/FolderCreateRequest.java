package com.gig.collide.api.favorite.request;

import com.gig.collide.api.favorite.constant.FolderType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * 收藏夹创建请求
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@Schema(description = "收藏夹创建请求")
public class FolderCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏夹名称
     */
    @NotBlank(message = "收藏夹名称不能为空")
    @Size(max = 50, message = "收藏夹名称不能超过50个字符")
    @Schema(description = "收藏夹名称", example = "我的文章收藏")
    private String folderName;

    /**
     * 收藏夹描述
     */
    @Size(max = 200, message = "收藏夹描述不能超过200个字符")
    @Schema(description = "收藏夹描述", example = "收藏的优质文章")
    private String description;

    /**
     * 收藏夹类型
     */
    @NotNull(message = "收藏夹类型不能为空")
    @Schema(description = "收藏夹类型", example = "CUSTOM")
    private FolderType folderType;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", example = "123")
    private Long userId;

    /**
     * 收藏夹封面图片
     */
    @Schema(description = "收藏夹封面图片", example = "https://example.com/cover.jpg")
    private String coverImage;

    /**
     * 排序权重
     */
    @Schema(description = "排序权重", example = "10")
    private Integer sortOrder;
} 
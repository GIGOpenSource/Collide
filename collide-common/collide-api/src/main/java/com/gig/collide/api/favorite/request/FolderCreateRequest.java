package com.gig.collide.api.favorite.request;

import com.gig.collide.api.favorite.constant.FolderType;
import com.gig.collide.base.request.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 收藏夹创建请求
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "收藏夹创建请求")
public class FolderCreateRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏夹名称
     */
    @NotBlank(message = "收藏夹名称不能为空")
    @Size(max = 100, message = "收藏夹名称长度不能超过100字符")
    @Schema(description = "收藏夹名称", required = true)
    private String folderName;

    /**
     * 收藏夹描述
     */
    @Size(max = 500, message = "收藏夹描述长度不能超过500字符")
    @Schema(description = "收藏夹描述")
    private String description;

    /**
     * 收藏夹类型
     */
    @NotNull(message = "收藏夹类型不能为空")
    @Schema(description = "收藏夹类型", required = true)
    private FolderType folderType;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", required = true)
    private Long userId;

    /**
     * 收藏夹封面图片
     */
    @Size(max = 500, message = "收藏夹封面图片URL长度不能超过500字符")
    @Schema(description = "收藏夹封面图片")
    private String coverImage;

    /**
     * 排序权重
     */
    @Schema(description = "排序权重", example = "10")
    private Integer sortOrder = 10;
} 
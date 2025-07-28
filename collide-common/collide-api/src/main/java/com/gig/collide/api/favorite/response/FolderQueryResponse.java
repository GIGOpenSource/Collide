package com.gig.collide.api.favorite.response;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.base.response.BaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 收藏夹查询响应
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
@Schema(description = "收藏夹查询响应")
public class FolderQueryResponse<T> extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 分页数据
     */
    @Schema(description = "分页数据")
    private IPage<T> page;

    /**
     * 查询总数
     */
    @Schema(description = "查询总数")
    private Long totalCount;

    // ===================== 静态工厂方法 =====================

    /**
     * 成功响应
     *
     * @param page 分页数据
     * @return 查询响应
     */
    public static <T> FolderQueryResponse<T> success(IPage<T> page) {
        FolderQueryResponse<T> response = new FolderQueryResponse<>();
        response.setPage(page);
        response.setTotalCount(page.getTotal());
        response.setSuccess(true);
        return response;
    }

    /**
     * 失败响应
     *
     * @param message 错误消息
     * @return 查询响应
     */
    public static <T> FolderQueryResponse<T> failure(String message) {
        FolderQueryResponse<T> response = new FolderQueryResponse<>();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }
} 
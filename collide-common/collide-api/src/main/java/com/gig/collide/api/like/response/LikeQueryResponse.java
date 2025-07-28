package com.gig.collide.api.like.response;

import com.gig.collide.api.like.response.data.LikeInfo;
import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 点赞查询响应
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@Schema(description = "点赞查询响应")
public class LikeQueryResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 点赞记录列表
     */
    @Schema(description = "点赞记录列表")
    private List<LikeInfo> likeInfos;

    /**
     * 总数量
     */
    @Schema(description = "总数量")
    private Long total;

    /**
     * 当前页码
     */
    @Schema(description = "当前页码")
    private Integer currentPage;

    /**
     * 每页大小
     */
    @Schema(description = "每页大小")
    private Integer pageSize;

    /**
     * 总页数
     */
    @Schema(description = "总页数")
    private Integer totalPages;

    /**
     * 是否有下一页
     */
    @Schema(description = "是否有下一页")
    private Boolean hasNext;

    /**
     * 创建成功响应
     */
    public static LikeQueryResponse success(List<LikeInfo> likeInfos, Long total, Integer currentPage, Integer pageSize) {
        LikeQueryResponse response = new LikeQueryResponse();
        response.setLikeInfos(likeInfos);
        response.setTotal(total);
        response.setCurrentPage(currentPage);
        response.setPageSize(pageSize);
        response.setTotalPages((int) Math.ceil((double) total / pageSize));
        response.setHasNext(currentPage * pageSize < total);
        response.setResponseCode("SUCCESS");
        response.setResponseMessage("查询成功");
        return response;
    }

    /**
     * 创建失败响应
     */
    public static LikeQueryResponse error(String errorCode, String errorMessage) {
        LikeQueryResponse response = new LikeQueryResponse();
        response.setResponseCode(errorCode);
        response.setResponseMessage(errorMessage);
        return response;
    }
} 
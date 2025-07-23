package com.gig.collide.api.like.response;

import com.gig.collide.api.like.response.data.LikeInfo;
import com.gig.collide.base.response.PageResponse;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 点赞查询响应
 * 
 * @author Collide
 * @since 1.0.0
 */
@Schema(description = "点赞查询响应")
public class LikeQueryResponse extends PageResponse<LikeInfo> {
    
    /**
     * 创建成功响应
     */
    public static LikeQueryResponse success(java.util.List<LikeInfo> data, 
                                           long total, 
                                           int pageSize, 
                                           int currentPage) {
        LikeQueryResponse response = new LikeQueryResponse();
        response.setData(data);
        response.setTotal(total);
        response.setPageSize(pageSize);
        response.setCurrentPage(currentPage);
        response.setCode("SUCCESS");
        response.setMessage("查询成功");
        return response;
    }
    
    /**
     * 创建失败响应
     */
    public static LikeQueryResponse error(String errorCode, String errorMessage) {
        LikeQueryResponse response = new LikeQueryResponse();
        response.setCode(errorCode);
        response.setMessage(errorMessage);
        return response;
    }
} 
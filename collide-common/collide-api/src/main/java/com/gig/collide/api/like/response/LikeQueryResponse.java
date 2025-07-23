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
        response.setSuccess(true);
        response.setDatas(data);
        response.setTotal((int) total);  // long转int
        response.setPageSize(pageSize);
        response.setCurrentPage(currentPage);
        response.setTotalPage((pageSize + (int) total - 1) / pageSize);  // 计算总页数
        response.setResponseCode("SUCCESS");
        response.setResponseMessage("查询成功");
        return response;
    }
    
    /**
     * 创建失败响应
     */
    public static LikeQueryResponse error(String errorCode, String errorMessage) {
        LikeQueryResponse response = new LikeQueryResponse();
        response.setSuccess(false);
        response.setResponseCode(errorCode);
        response.setResponseMessage(errorMessage);
        response.setDatas(java.util.Collections.emptyList());  // 设置空数据
        response.setTotal(0);
        response.setPageSize(0);
        response.setCurrentPage(0);
        response.setTotalPage(0);
        return response;
    }
} 
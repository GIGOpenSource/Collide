package com.gig.collide.api.social.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 社交动态查询响应
 * @author GIG
 * @version 1.0.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SocialPostQueryResponse<T> extends BaseResponse {
    
    /**
     * 动态数据
     */
    private T data;
    
    /**
     * 分页信息
     */
    private PageInfo pageInfo;


    private String Message;

    /**
     * 分页信息内部类
     */
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageInfo {
        /**
         * 当前页码
         */
        private Integer pageNum;
        
        /**
         * 每页数量
         */
        private Integer pageSize;
        
        /**
         * 总记录数
         */
        private Long total;
        
        /**
         * 总页数
         */
        private Integer totalPages;
        
        /**
         * 是否有下一页
         */
        private Boolean hasNext;
        
        /**
         * 是否有上一页
         */
        private Boolean hasPrevious;
        
        /**
         * 计算总页数
         */
        public void calculateTotalPages() {
            if (total != null && pageSize != null && pageSize > 0) {
                this.totalPages = (int) Math.ceil((double) total / pageSize);
                this.hasNext = pageNum != null && pageNum < totalPages;
                this.hasPrevious = pageNum != null && pageNum > 1;
            }
        }
    }
    
    /**
     * 构造成功响应
     */
    public static <T> SocialPostQueryResponse<T> success(T data) {
        SocialPostQueryResponse<T> response = new SocialPostQueryResponse<>();
        response.setSuccess(true);
        response.setMessage("查询成功");
        response.setData(data);
        return response;
    }
    
    /**
     * 构造带分页的成功响应
     */
    public static <T> SocialPostQueryResponse<T> success(T data, Integer pageNum, Integer pageSize, Long total) {
        SocialPostQueryResponse<T> response = success(data);
        PageInfo pageInfo = new PageInfo(pageNum, pageSize, total, null, null, null);
        pageInfo.calculateTotalPages();
        response.setPageInfo(pageInfo);
        return response;
    }
    
    /**
     * 构造列表响应
     */
    public static <T> SocialPostQueryResponse<List<T>> successList(List<T> data, Integer pageNum, Integer pageSize, Long total) {
        return success(data, pageNum, pageSize, total);
    }
    
    /**
     * 构造失败响应
     */
    public static <T> SocialPostQueryResponse<T> failure(String message) {
        SocialPostQueryResponse<T> response = new SocialPostQueryResponse<>();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }
} 
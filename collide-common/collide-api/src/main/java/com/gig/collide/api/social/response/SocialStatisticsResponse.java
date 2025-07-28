package com.gig.collide.api.social.response;

import com.gig.collide.api.social.response.data.SocialStatisticsInfo;
import com.gig.collide.base.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 社交统计响应
 * @author GIG
 * @version 1.0.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SocialStatisticsResponse extends BaseResponse {
    
    /**
     * 统计信息
     */
    private SocialStatisticsInfo statisticsInfo;

    private String Message;
    
    /**
     * 构造统计成功响应
     */
    public static SocialStatisticsResponse success(SocialStatisticsInfo statisticsInfo) {
        SocialStatisticsResponse response = new SocialStatisticsResponse();
        response.setSuccess(true);
        response.setMessage("统计查询成功");
        response.setStatisticsInfo(statisticsInfo);
        return response;
    }
    
    /**
     * 构造统计失败响应
     */
    public static SocialStatisticsResponse failure(String message) {
        SocialStatisticsResponse response = new SocialStatisticsResponse();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }
} 
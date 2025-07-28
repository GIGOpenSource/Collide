package com.gig.collide.api.search.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 搜索统计更新响应
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
public class SearchStatisticsUpdateResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 统计记录ID
     */
    private Long statisticsId;

    /**
     * 关键词
     */
    private String keyword;

    /**
     * 更新前的搜索次数
     */
    private Long beforeSearchCount;

    /**
     * 更新后的搜索次数
     */
    private Long afterSearchCount;

    /**
     * 更新前的用户数
     */
    private Long beforeUserCount;

    /**
     * 更新后的用户数
     */
    private Long afterUserCount;

    /**
     * 更新前的热度评分
     */
    private Double beforeHotScore;

    /**
     * 更新后的热度评分
     */
    private Double afterHotScore;

    /**
     * 更新时间
     */
    private String updateTime;

    /**
     * 更新人ID
     */
    private Long updaterId;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 更新是否成功
     */
    private Boolean updateSuccess;

    /**
     * 更新备注
     */
    private String remark;
} 
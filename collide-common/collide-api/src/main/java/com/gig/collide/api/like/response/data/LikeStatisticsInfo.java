package com.gig.collide.api.like.response.data;

import com.gig.collide.api.like.constant.TargetTypeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 点赞统计信息传输对象
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class LikeStatisticsInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 统计记录ID
     */
    private Long id;

    /**
     * 目标对象ID
     */
    private Long targetId;

    /**
     * 目标类型
     */
    private TargetTypeEnum targetType;

    /**
     * 总点赞数
     */
    private Long totalLikeCount;

    /**
     * 总点踩数
     */
    private Long totalDislikeCount;

    /**
     * 今日点赞数
     */
    private Long todayLikeCount;

    /**
     * 本周点赞数
     */
    private Long weekLikeCount;

    /**
     * 本月点赞数
     */
    private Long monthLikeCount;

    /**
     * 点赞率
     */
    private Double likeRate;

    /**
     * 最后点赞时间
     */
    private LocalDateTime lastLikeTime;

    /**
     * 统计日期
     */
    private LocalDateTime statDate;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
} 
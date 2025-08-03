package com.gig.collide.task.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.task.domain.entity.UserCheckinRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 用户签到记录Mapper接口 - 对应 t_user_checkin_record 表
 * 负责用户签到行为和奖励记录管理
 * 
 * @author GIG Team
 * @version 1.0.0
 */
@Mapper
public interface UserCheckinRecordMapper extends BaseMapper<UserCheckinRecord> {

    /**
     * 根据用户ID和日期查询签到记录
     */
    UserCheckinRecord findByUserIdAndDate(@Param("userId") Long userId, 
                                         @Param("checkinDate") LocalDate checkinDate);

    /**
     * 检查用户今日是否已签到
     */
    boolean checkTodayCheckin(@Param("userId") Long userId, 
                             @Param("checkinDate") LocalDate checkinDate);

    /**
     * 获取用户最近的签到记录
     * 用于计算连续签到天数
     */
    UserCheckinRecord getLatestCheckinRecord(@Param("userId") Long userId);

    /**
     * 计算用户连续签到天数
     * 从最近一次签到向前计算连续天数
     */
    Integer calculateContinuousCheckinDays(@Param("userId") Long userId);

    /**
     * 分页查询用户签到记录
     */
    List<UserCheckinRecord> findUserCheckinRecords(@Param("userId") Long userId,
                                                   @Param("taskTemplateId") Long taskTemplateId,
                                                   @Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate,
                                                   @Param("minContinuousDays") Integer minContinuousDays,
                                                   @Param("maxContinuousDays") Integer maxContinuousDays,
                                                   @Param("offset") Integer offset,
                                                   @Param("size") Integer size);

    /**
     * 统计用户签到记录数量
     */
    Long countUserCheckinRecords(@Param("userId") Long userId,
                                @Param("taskTemplateId") Long taskTemplateId,
                                @Param("startDate") LocalDate startDate,
                                @Param("endDate") LocalDate endDate,
                                @Param("minContinuousDays") Integer minContinuousDays,
                                @Param("maxContinuousDays") Integer maxContinuousDays);

    /**
     * 查询用户指定月份的签到记录
     */
    List<UserCheckinRecord> findMonthlyCheckinRecords(@Param("userId") Long userId,
                                                      @Param("year") Integer year,
                                                      @Param("month") Integer month);

    /**
     * 统计用户总签到次数
     */
    Integer countUserTotalCheckins(@Param("userId") Long userId);

    /**
     * 统计用户本月签到次数
     */
    Integer countUserMonthlyCheckins(@Param("userId") Long userId,
                                    @Param("year") Integer year,
                                    @Param("month") Integer month);

    /**
     * 查询用户历史最长连续签到天数
     */
    Integer getMaxContinuousCheckinDays(@Param("userId") Long userId);

    /**
     * 统计用户累计获得金币
     */
    Long getTotalRewardCoins(@Param("userId") Long userId);

    /**
     * 批量查询用户签到记录
     */
    List<UserCheckinRecord> findByUserIds(@Param("userIds") List<Long> userIds,
                                         @Param("checkinDate") LocalDate checkinDate);

    /**
     * 查询指定日期的所有签到记录
     * 用于管理员统计
     */
    List<UserCheckinRecord> findByCheckinDate(@Param("checkinDate") LocalDate checkinDate,
                                             @Param("offset") Integer offset,
                                             @Param("size") Integer size);

    /**
     * 统计指定日期的签到用户数
     */
    Long countCheckinUsersByDate(@Param("checkinDate") LocalDate checkinDate);
}
package com.gig.collide.users.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.users.domain.entity.UserInviteRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户邀请关系数据访问映射器
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Mapper
public interface UserInviteRelationMapper extends BaseMapper<UserInviteRelation> {

    /**
     * 根据邀请人ID查询被邀请用户关系
     *
     * @param inviterId 邀请人ID
     * @param inviteLevel 邀请层级（可为null）
     * @param offset 偏移量
     * @param limit 限制条数
     * @return 邀请关系列表
     */
    List<UserInviteRelation> selectByInviterId(@Param("inviterId") Long inviterId,
                                              @Param("inviteLevel") Integer inviteLevel,
                                              @Param("offset") int offset,
                                              @Param("limit") int limit);

    /**
     * 根据被邀请用户ID查询邀请关系
     *
     * @param userId 被邀请用户ID
     * @return 邀请关系
     */
    UserInviteRelation selectByUserId(@Param("userId") Long userId);

    /**
     * 统计邀请人的各级邀请数量
     *
     * @param inviterId 邀请人ID
     * @return 各级邀请统计
     */
    List<Map<String, Object>> countInvitesByLevel(@Param("inviterId") Long inviterId);

    /**
     * 获取邀请链路（递归查询上级邀请关系）
     *
     * @param userId 用户ID
     * @param maxLevel 最大层级
     * @return 邀请链路
     */
    List<UserInviteRelation> selectInviteChain(@Param("userId") Long userId, 
                                              @Param("maxLevel") int maxLevel);

    /**
     * 获取邀请排行榜
     *
     * @param inviteLevel 邀请层级
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制条数
     * @return 邀请排行榜
     */
    List<Map<String, Object>> getInviteRanking(@Param("inviteLevel") Integer inviteLevel,
                                              @Param("startTime") LocalDateTime startTime,
                                              @Param("endTime") LocalDateTime endTime,
                                              @Param("limit") int limit);

    /**
     * 统计时间段内的邀请数据
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 邀请统计数据
     */
    Map<String, Object> getInviteStatistics(@Param("startTime") LocalDateTime startTime,
                                           @Param("endTime") LocalDateTime endTime);

    /**
     * 检查是否存在邀请关系
     *
     * @param userId 被邀请用户ID
     * @param inviterId 邀请人ID
     * @return 是否存在
     */
    boolean existsInviteRelation(@Param("userId") Long userId, @Param("inviterId") Long inviterId);
} 
package com.gig.collide.users.infrastructure.mapper;

import com.gig.collide.users.domain.entity.UserInviteRelation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户邀请关系Mapper
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Mapper
public interface UserInviteRelationMapper extends BaseMapper<UserInviteRelation> {

    /**
     * 根据用户ID查询邀请关系
     * 
     * @param userId 用户ID
     * @return 邀请关系
     */
    UserInviteRelation findByUserId(@Param("userId") Long userId);

    /**
     * 根据邀请人ID查询被邀请用户列表
     * 
     * @param inviterId 邀请人ID
     * @return 被邀请用户列表
     */
    List<UserInviteRelation> findByInviterId(@Param("inviterId") Long inviterId);

    /**
     * 统计邀请人的邀请数量
     * 
     * @param inviterId 邀请人ID
     * @return 邀请数量
     */
    Integer countByInviterId(@Param("inviterId") Long inviterId);

    /**
     * 根据邀请码查询邀请关系
     * 
     * @param inviteCode 邀请码
     * @return 邀请关系列表
     */
    List<UserInviteRelation> findByInviteCode(@Param("inviteCode") String inviteCode);
} 
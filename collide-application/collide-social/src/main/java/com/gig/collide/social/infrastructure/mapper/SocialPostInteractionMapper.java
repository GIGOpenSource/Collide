package com.gig.collide.social.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.social.domain.entity.SocialPostInteraction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 社交动态互动记录 Mapper 接口
 * 
 * <p>完全去连表化设计，所有查询都是单表操作</p>
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Mapper
public interface SocialPostInteractionMapper extends BaseMapper<SocialPostInteraction> {

    /**
     * 查询用户对动态的互动状态（无连表）
     *
     * @param userId 用户ID
     * @param postId 动态ID
     * @param interactionType 互动类型
     * @return 互动记录
     */
    SocialPostInteraction selectUserInteraction(@Param("userId") Long userId,
                                               @Param("postId") Long postId,
                                               @Param("interactionType") String interactionType);

    /**
     * 批量查询用户对多个动态的互动状态（无连表）
     *
     * @param userId 用户ID
     * @param postIds 动态ID列表
     * @param interactionType 互动类型
     * @return 互动记录列表
     */
    List<SocialPostInteraction> selectUserInteractionsBatch(@Param("userId") Long userId,
                                                           @Param("postIds") List<Long> postIds,
                                                           @Param("interactionType") String interactionType);

    /**
     * 分页查询动态的互动记录（无连表）
     *
     * @param page 分页参数
     * @param postId 动态ID
     * @param interactionType 互动类型（可选）
     * @param interactionStatus 互动状态（可选）
     * @return 分页结果
     */
    IPage<SocialPostInteraction> selectPostInteractionsPage(Page<SocialPostInteraction> page,
                                                           @Param("postId") Long postId,
                                                           @Param("interactionType") String interactionType,
                                                           @Param("interactionStatus") Integer interactionStatus);

    /**
     * 分页查询用户的互动历史（无连表）
     *
     * @param page 分页参数
     * @param userId 用户ID
     * @param interactionType 互动类型（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 分页结果
     */
    IPage<SocialPostInteraction> selectUserInteractionHistoryPage(Page<SocialPostInteraction> page,
                                                                 @Param("userId") Long userId,
                                                                 @Param("interactionType") String interactionType,
                                                                 @Param("startTime") LocalDateTime startTime,
                                                                 @Param("endTime") LocalDateTime endTime);

    /**
     * 分页查询作者收到的互动（无连表）
     *
     * @param page 分页参数
     * @param postAuthorId 动态作者ID
     * @param interactionType 互动类型（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 分页结果
     */
    IPage<SocialPostInteraction> selectAuthorReceivedInteractionsPage(Page<SocialPostInteraction> page,
                                                                     @Param("postAuthorId") Long postAuthorId,
                                                                     @Param("interactionType") String interactionType,
                                                                     @Param("startTime") LocalDateTime startTime,
                                                                     @Param("endTime") LocalDateTime endTime);

    /**
     * 统计动态的互动数量（无连表）
     *
     * @param postId 动态ID
     * @param interactionType 互动类型
     * @param interactionStatus 互动状态
     * @return 互动数量
     */
    Long countPostInteractions(@Param("postId") Long postId,
                              @Param("interactionType") String interactionType,
                              @Param("interactionStatus") Integer interactionStatus);

    /**
     * 统计用户的互动数量（无连表）
     *
     * @param userId 用户ID
     * @param interactionType 互动类型
     * @param interactionStatus 互动状态
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 互动数量
     */
    Long countUserInteractions(@Param("userId") Long userId,
                              @Param("interactionType") String interactionType,
                              @Param("interactionStatus") Integer interactionStatus,
                              @Param("startTime") LocalDateTime startTime,
                              @Param("endTime") LocalDateTime endTime);

    /**
     * 更新或插入互动记录（幂等操作）
     *
     * @param interaction 互动记录
     * @return 影响行数
     */
    int upsertInteraction(SocialPostInteraction interaction);

    /**
     * 取消用户对动态的特定互动（无连表）
     *
     * @param userId 用户ID
     * @param postId 动态ID
     * @param interactionType 互动类型
     * @return 影响行数
     */
    int cancelUserInteraction(@Param("userId") Long userId,
                             @Param("postId") Long postId,
                             @Param("interactionType") String interactionType);

    /**
     * 批量更新用户信息冗余字段（无连表）
     *
     * @param userId 用户ID
     * @param userNickname 用户昵称
     * @param userAvatar 用户头像
     * @return 影响行数
     */
    int batchUpdateUserInfo(@Param("userId") Long userId,
                           @Param("userNickname") String userNickname,
                           @Param("userAvatar") String userAvatar);

    /**
     * 批量更新动态信息冗余字段（无连表）
     *
     * @param postId 动态ID
     * @param postAuthorId 动态作者ID
     * @param postType 动态类型
     * @param postTitle 动态标题
     * @return 影响行数
     */
    int batchUpdatePostInfo(@Param("postId") Long postId,
                           @Param("postAuthorId") Long postAuthorId,
                           @Param("postType") String postType,
                           @Param("postTitle") String postTitle);

    /**
     * 清理过期的浏览记录（无连表）
     *
     * @param expireTime 过期时间
     * @return 清理行数
     */
    int cleanExpiredViewRecords(@Param("expireTime") LocalDateTime expireTime);

    /**
     * 查询活跃用户互动排行榜（无连表）
     *
     * @param interactionType 互动类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 排行榜数量
     * @return 用户互动统计列表
     */
    List<UserInteractionStats> selectActiveUserRanking(@Param("interactionType") String interactionType,
                                                       @Param("startTime") LocalDateTime startTime,
                                                       @Param("endTime") LocalDateTime endTime,
                                                       @Param("limit") Integer limit);

    /**
     * 用户互动统计结果类
     */
    class UserInteractionStats {
        private Long userId;
        private String userNickname;
        private String userAvatar;
        private Long interactionCount;
        private LocalDateTime lastInteractionTime;

        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public String getUserNickname() { return userNickname; }
        public void setUserNickname(String userNickname) { this.userNickname = userNickname; }

        public String getUserAvatar() { return userAvatar; }
        public void setUserAvatar(String userAvatar) { this.userAvatar = userAvatar; }

        public Long getInteractionCount() { return interactionCount; }
        public void setInteractionCount(Long interactionCount) { this.interactionCount = interactionCount; }

        public LocalDateTime getLastInteractionTime() { return lastInteractionTime; }
        public void setLastInteractionTime(LocalDateTime lastInteractionTime) { this.lastInteractionTime = lastInteractionTime; }
    }
} 
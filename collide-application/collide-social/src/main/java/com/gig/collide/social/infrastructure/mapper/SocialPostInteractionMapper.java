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
 * 社交互动记录Mapper接口
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Mapper
public interface SocialPostInteractionMapper extends BaseMapper<SocialPostInteraction> {

    /**
     * 查询用户对动态的互动记录
     */
    SocialPostInteraction selectUserInteraction(@Param("postId") Long postId, @Param("userId") Long userId, 
                                                @Param("interactionType") String interactionType);

    /**
     * 分页查询动态的互动记录
     */
    IPage<SocialPostInteraction> selectPostInteractions(Page<SocialPostInteraction> page, @Param("postId") Long postId, 
                                                        @Param("interactionType") String interactionType, 
                                                        @Param("interactionStatus") Integer interactionStatus);

    /**
     * 分页查询用户的互动历史
     */
    IPage<SocialPostInteraction> selectUserInteractions(Page<SocialPostInteraction> page, @Param("userId") Long userId, 
                                                        @Param("interactionType") String interactionType,
                                                        @Param("interactionStatus") Integer interactionStatus);

    /**
     * 统计动态的互动数量
     */
    Long countPostInteractions(@Param("postId") Long postId, @Param("interactionType") String interactionType, 
                              @Param("interactionStatus") Integer interactionStatus);

    /**
     * 统计用户的互动数量
     */
    Long countUserInteractions(@Param("userId") Long userId, @Param("interactionType") String interactionType,
                              @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 批量查询多个动态的互动数量
     */
    List<PostInteractionCount> batchCountPostInteractions(@Param("postIds") List<Long> postIds, 
                                                          @Param("interactionType") String interactionType);

    /**
     * 查询动态作者收到的互动
     */
    IPage<SocialPostInteraction> selectAuthorReceivedInteractions(Page<SocialPostInteraction> page, 
                                                                 @Param("authorId") Long authorId,
                                                                 @Param("interactionType") String interactionType,
                                                                 @Param("startTime") LocalDateTime startTime);

    /**
     * 更新互动状态
     */
    int updateInteractionStatus(@Param("id") Long id, @Param("interactionStatus") Integer interactionStatus);

    /**
     * 批量更新用户信息
     */
    int batchUpdateUserInfo(@Param("userId") Long userId, @Param("userNickname") String userNickname, 
                           @Param("userAvatar") String userAvatar);

    /**
     * 删除过期的浏览记录
     */
    int deleteExpiredViewRecords(@Param("expiredTime") LocalDateTime expiredTime);

    /**
     * 查询热门互动用户
     */
    List<UserInteractionStats> selectTopInteractionUsers(@Param("startTime") LocalDateTime startTime, 
                                                         @Param("endTime") LocalDateTime endTime, 
                                                         @Param("limit") Integer limit);

    /**
     * 动态互动统计内部类
     */
    class PostInteractionCount {
        private Long postId;
        private Long count;
        
        public Long getPostId() { return postId; }
        public void setPostId(Long postId) { this.postId = postId; }
        public Long getCount() { return count; }
        public void setCount(Long count) { this.count = count; }
    }

    /**
     * 用户互动统计内部类
     */
    class UserInteractionStats {
        private Long userId;
        private String userNickname;
        private String userAvatar;
        private Long totalInteractions;
        private Long likeCount;
        private Long favoriteCount;
        private Long shareCount;
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getUserNickname() { return userNickname; }
        public void setUserNickname(String userNickname) { this.userNickname = userNickname; }
        public String getUserAvatar() { return userAvatar; }
        public void setUserAvatar(String userAvatar) { this.userAvatar = userAvatar; }
        public Long getTotalInteractions() { return totalInteractions; }
        public void setTotalInteractions(Long totalInteractions) { this.totalInteractions = totalInteractions; }
        public Long getLikeCount() { return likeCount; }
        public void setLikeCount(Long likeCount) { this.likeCount = likeCount; }
        public Long getFavoriteCount() { return favoriteCount; }
        public void setFavoriteCount(Long favoriteCount) { this.favoriteCount = favoriteCount; }
        public Long getShareCount() { return shareCount; }
        public void setShareCount(Long shareCount) { this.shareCount = shareCount; }
    }
} 
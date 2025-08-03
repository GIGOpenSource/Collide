package com.gig.collide.social.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.social.domain.entity.*;
import org.apache.ibatis.annotations.*;

/**
 * 社交互动Mapper - 统一处理点赞、收藏、分享、评论
 * 
 * @author GIG Team
 * @since 2024-01-16
 */
@Mapper
public interface SocialInteractionMapper {

    // ========== 点赞相关 ==========
    
    @Select("SELECT COUNT(*) FROM t_social_like WHERE user_id = #{userId} AND content_id = #{contentId}")
    int countLike(@Param("userId") Long userId, @Param("contentId") Long contentId);

    @Insert("INSERT INTO t_social_like (user_id, content_id, content_owner_id, like_type, create_time) " +
            "VALUES (#{userId}, #{contentId}, #{contentOwnerId}, #{likeType}, NOW())")
    int insertLike(@Param("userId") Long userId, @Param("contentId") Long contentId, 
                   @Param("contentOwnerId") Long contentOwnerId, @Param("likeType") Integer likeType);

    @Delete("DELETE FROM t_social_like WHERE user_id = #{userId} AND content_id = #{contentId}")
    int deleteLike(@Param("userId") Long userId, @Param("contentId") Long contentId);

    @Select("SELECT * FROM t_social_like WHERE content_id = #{contentId} ORDER BY create_time DESC")
    IPage<SocialLike> selectContentLikes(Page<SocialLike> page, @Param("contentId") Long contentId);

    // ========== 收藏相关 ==========
    
    @Select("SELECT COUNT(*) FROM t_social_favorite WHERE user_id = #{userId} AND content_id = #{contentId}")
    int countFavorite(@Param("userId") Long userId, @Param("contentId") Long contentId);

    @Insert("INSERT INTO t_social_favorite (user_id, content_id, content_owner_id, folder_id, folder_name, create_time) " +
            "VALUES (#{userId}, #{contentId}, #{contentOwnerId}, #{folderId}, #{folderName}, NOW())")
    int insertFavorite(@Param("userId") Long userId, @Param("contentId") Long contentId, 
                       @Param("contentOwnerId") Long contentOwnerId, @Param("folderId") Long folderId, 
                       @Param("folderName") String folderName);

    @Delete("DELETE FROM t_social_favorite WHERE user_id = #{userId} AND content_id = #{contentId}")
    int deleteFavorite(@Param("userId") Long userId, @Param("contentId") Long contentId);

    @Select("SELECT * FROM t_social_favorite WHERE user_id = #{userId} ORDER BY create_time DESC")
    IPage<SocialFavorite> selectUserFavorites(Page<SocialFavorite> page, @Param("userId") Long userId);

    // ========== 分享相关 ==========
    
    @Insert("INSERT INTO t_social_share (user_id, content_id, content_owner_id, share_type, share_platform, share_comment, create_time) " +
            "VALUES (#{userId}, #{contentId}, #{contentOwnerId}, #{shareType}, #{sharePlatform}, #{shareComment}, NOW())")
    int insertShare(@Param("userId") Long userId, @Param("contentId") Long contentId, 
                    @Param("contentOwnerId") Long contentOwnerId, @Param("shareType") Integer shareType, 
                    @Param("sharePlatform") String sharePlatform, @Param("shareComment") String shareComment);

    @Select("SELECT * FROM t_social_share WHERE content_id = #{contentId} ORDER BY create_time DESC")
    IPage<SocialShare> selectContentShares(Page<SocialShare> page, @Param("contentId") Long contentId);

    // ========== 评论相关 ==========
    
    @Select("SELECT COUNT(*) FROM t_social_comment WHERE user_id = #{userId} AND content_id = #{contentId} AND status = 1")
    int countComment(@Param("userId") Long userId, @Param("contentId") Long contentId);

    @Select("SELECT * FROM t_social_comment WHERE content_id = #{contentId} AND status = 1 AND parent_comment_id = 0 ORDER BY create_time DESC")
    IPage<SocialComment> selectContentComments(Page<SocialComment> page, @Param("contentId") Long contentId);

    @Select("SELECT * FROM t_social_comment WHERE parent_comment_id = #{parentCommentId} AND status = 1 ORDER BY create_time ASC")
    IPage<SocialComment> selectCommentReplies(Page<SocialComment> page, @Param("parentCommentId") Long parentCommentId);

    // ========== 批量状态查询 ==========
    
    @Select({
        "SELECT ",
        "  EXISTS(SELECT 1 FROM t_social_like WHERE user_id = #{userId} AND content_id = #{contentId}) as liked,",
        "  EXISTS(SELECT 1 FROM t_social_favorite WHERE user_id = #{userId} AND content_id = #{contentId}) as favorited,",
        "  EXISTS(SELECT 1 FROM t_social_comment WHERE user_id = #{userId} AND content_id = #{contentId} AND status = 1) as commented"
    })
    @Results({
        @Result(property = "liked", column = "liked"),
        @Result(property = "favorited", column = "favorited"),
        @Result(property = "commented", column = "commented")
    })
    SocialInteractionService.UserInteractionStatus getUserInteractionStatus(@Param("userId") Long userId, @Param("contentId") Long contentId);
}
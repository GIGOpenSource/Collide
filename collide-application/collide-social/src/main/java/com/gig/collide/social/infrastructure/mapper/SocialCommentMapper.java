package com.gig.collide.social.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.social.domain.entity.SocialComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 评论Mapper
 * 
 * @author GIG Team
 * @since 2024-01-16
 */
@Mapper
public interface SocialCommentMapper extends BaseMapper<SocialComment> {

    @Update("UPDATE t_social_comment SET status = 0 WHERE id = #{commentId} AND user_id = #{userId}")
    int softDeleteComment(@Param("commentId") Long commentId, @Param("userId") Long userId);

    @Update("UPDATE t_social_comment SET reply_count = reply_count + 1 WHERE id = #{parentCommentId}")
    int incrementReplyCount(@Param("parentCommentId") Long parentCommentId);

    @Update("UPDATE t_social_comment SET reply_count = CASE WHEN reply_count > 0 THEN reply_count - 1 ELSE 0 END WHERE id = #{parentCommentId}")
    int decrementReplyCount(@Param("parentCommentId") Long parentCommentId);
}
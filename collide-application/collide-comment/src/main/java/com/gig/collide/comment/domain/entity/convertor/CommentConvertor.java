package com.gig.collide.comment.domain.entity.convertor;

import com.gig.collide.api.comment.response.data.CommentInfo;
import com.gig.collide.comment.domain.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 评论实体转换器
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Mapper
public interface CommentConvertor {

    CommentConvertor INSTANCE = Mappers.getMapper(CommentConvertor.class);

    /**
     * 实体转评论信息
     *
     * @param comment 评论实体
     * @return 评论信息VO
     */
    @Mapping(source = "id", target = "commentId")
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "nickname", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "replyToUsername", ignore = true)
    @Mapping(target = "replyToNickname", ignore = true)
    @Mapping(target = "isLiked", ignore = true)
    @Mapping(target = "isDeletable", ignore = true)
    @Mapping(target = "children", ignore = true)
    CommentInfo convertToCommentInfo(Comment comment);

    /**
     * 实体列表转评论信息列表
     *
     * @param comments 评论实体列表
     * @return 评论信息VO列表
     */
    List<CommentInfo> convertToCommentInfoList(List<Comment> comments);
} 
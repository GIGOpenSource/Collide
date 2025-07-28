package com.gig.collide.social.domain.entity.convertor;

import com.gig.collide.api.social.request.SocialPostCreateRequest;
import com.gig.collide.api.social.request.SocialPostUpdateRequest;
import com.gig.collide.api.social.response.data.SocialPostInfo;
import com.gig.collide.api.social.response.data.BasicSocialPostInfo;
import com.gig.collide.api.social.response.data.SocialInteractionInfo;
import com.gig.collide.api.social.constant.PostTypeEnum;
import com.gig.collide.api.social.constant.PostStatusEnum;
import com.gig.collide.api.social.constant.VisibilityEnum;
import com.gig.collide.api.social.constant.InteractionTypeEnum;
import com.gig.collide.social.domain.entity.SocialPost;
import com.gig.collide.social.domain.entity.SocialPostInteraction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 社交动态实体转换器
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Mapper
public interface SocialPostConvertor {

    SocialPostConvertor INSTANCE = Mappers.getMapper(SocialPostConvertor.class);

    /**
     * 创建请求转实体
     */
    @Mapping(target = "likeCount", constant = "0L")
    @Mapping(target = "commentCount", constant = "0L")
    @Mapping(target = "shareCount", constant = "0L")
    @Mapping(target = "viewCount", constant = "0L")
    @Mapping(target = "favoriteCount", constant = "0L")
    @Mapping(target = "hotScore", expression = "java(new java.math.BigDecimal(\"0.0\"))")
    @Mapping(target = "publishedTime", ignore = true)
    @Mapping(target = "auditStatus", constant = "PENDING")
    @Mapping(target = "auditTime", ignore = true)
    @Mapping(target = "auditRemark", ignore = true)
    SocialPost fromCreateRequest(SocialPostCreateRequest request);

    /**
     * 更新请求应用到实体
     */
    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "authorUsername", ignore = true)
    @Mapping(target = "authorNickname", ignore = true)
    @Mapping(target = "authorAvatar", ignore = true)
    @Mapping(target = "authorVerified", ignore = true)
    @Mapping(target = "publishedTime", ignore = true)
    @Mapping(target = "likeCount", ignore = true)
    @Mapping(target = "commentCount", ignore = true)
    @Mapping(target = "shareCount", ignore = true)
    @Mapping(target = "viewCount", ignore = true)
    @Mapping(target = "favoriteCount", ignore = true)
    @Mapping(target = "hotScore", ignore = true)
    @Mapping(target = "auditStatus", ignore = true)
    @Mapping(target = "auditTime", ignore = true)
    @Mapping(target = "auditRemark", ignore = true)
    void updateEntityFromRequest(SocialPostUpdateRequest request, @MappingTarget SocialPost entity);

    /**
     * 实体转详细信息DTO
     */
    @Mapping(target = "mediaUrls", source = "mediaUrls")
    @Mapping(target = "topics", source = "tags")
    @Mapping(target = "mentionedUserIds", ignore = true)
    @Mapping(target = "allowComments", constant = "true")
    @Mapping(target = "allowShares", constant = "true")
    @Mapping(target = "longitude", ignore = true)
    @Mapping(target = "latitude", ignore = true)
    @Mapping(target = "isLiked", ignore = true)
    @Mapping(target = "isFavorited", ignore = true)
    @Mapping(target = "isShared", ignore = true)
    SocialPostInfo toDetailInfo(SocialPost entity);

    /**
     * 实体转基础信息DTO
     */
    @Mapping(target = "firstMediaUrl", expression = "java(entity.getMediaUrls() != null && !entity.getMediaUrls().isEmpty() ? entity.getMediaUrls().get(0) : null)")
    @Mapping(target = "mediaCount", expression = "java(entity.getMediaUrls() != null ? entity.getMediaUrls().size() : 0)")
    @Mapping(target = "topics", source = "tags")
    @Mapping(target = "isLiked", ignore = true)
    @Mapping(target = "isFavorited", ignore = true)
    BasicSocialPostInfo toBasicInfo(SocialPost entity);

    /**
     * 实体列表转基础信息DTO列表
     */
    List<BasicSocialPostInfo> toBasicInfoList(List<SocialPost> entities);

    /**
     * 实体列表转详细信息DTO列表
     */
    List<SocialPostInfo> toDetailInfoList(List<SocialPost> entities);

    /**
     * 互动实体转互动信息DTO
     */
    @Mapping(target = "postAuthorId", ignore = true)
    @Mapping(target = "postType", ignore = true)
    @Mapping(target = "postTitle", ignore = true)
    @Mapping(target = "interactionContent", ignore = true)
    @Mapping(target = "interactionStatus", ignore = true)
    @Mapping(target = "createdTime", source = "gmtCreate")
    @Mapping(target = "updatedTime", source = "gmtModified")
    SocialInteractionInfo interactionToInfo(SocialPostInteraction interaction);

    // 别名方法 - 兼容编译器调用（使用qualified方法避免歧义）
    @org.mapstruct.Named("createRequestToEntity")
    default SocialPost createRequestToEntity(SocialPostCreateRequest request) {
        return fromCreateRequest(request);
    }

    @org.mapstruct.Named("entityToInfo") 
    default SocialPostInfo entityToInfo(SocialPost entity) {
        return toDetailInfo(entity);
    }

    @org.mapstruct.Named("entityToBasicInfo")
    default BasicSocialPostInfo entityToBasicInfo(SocialPost entity) {
        return toBasicInfo(entity);
    }

    // 枚举映射的默认方法
    default String mapPostTypeEnum(PostTypeEnum postType) {
        return postType != null ? postType.name() : null;
    }

    default PostTypeEnum mapPostTypeString(String postType) {
        try {
            return postType != null ? PostTypeEnum.valueOf(postType) : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    default String mapPostStatusEnum(PostStatusEnum status) {
        return status != null ? status.name() : null;
    }

    default PostStatusEnum mapPostStatusString(String status) {
        try {
            return status != null ? PostStatusEnum.valueOf(status) : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    default String mapVisibilityEnum(VisibilityEnum visibility) {
        return visibility != null ? visibility.name() : null;
    }

    default VisibilityEnum mapVisibilityString(String visibility) {
        try {
            return visibility != null ? VisibilityEnum.valueOf(visibility) : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // 处理布尔值到整数的转换
    default Integer mapBooleanToInteger(Boolean value) {
        if (value == null) return null;
        return value ? 1 : 0;
    }

    default Boolean mapIntegerToBoolean(Integer value) {
        if (value == null) return null;
        return value != 0;
    }
} 
package com.gig.collide.like.infrastructure.converter;

import com.gig.collide.api.like.response.data.LikeInfo;
import com.gig.collide.like.domain.entity.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * 点赞实体转换器
 * 
 * @author Collide
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeConverter {
    
    /**
     * 实体转换为信息DTO
     */
    @Mapping(source = "createdTime", target = "likedTime")
    @Mapping(source = "targetType", target = "likeType")
    @Mapping(source = "actionType", target = "status", qualifiedByName = "actionTypeToStatus")
    @Mapping(target = "userNickname", ignore = true)
    @Mapping(target = "userAvatar", ignore = true)
    LikeInfo toInfo(Like like);
    
    /**
     * 批量转换实体为信息DTO
     */
    List<LikeInfo> toInfoList(List<Like> likes);
    
    /**
     * 操作类型转换为状态描述
     */
    @org.mapstruct.Named("actionTypeToStatus")
    default String actionTypeToStatus(Integer actionType) {
        if (actionType == null) {
            return "UNLIKED";
        }
        return switch (actionType) {
            case 1 -> "LIKED";
            case -1 -> "DISLIKED";
            default -> "UNLIKED";
        };
    }
} 
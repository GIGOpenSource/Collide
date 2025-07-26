package com.gig.collide.like.infrastructure.converter;

import com.gig.collide.api.like.response.data.LikeInfo;
import com.gig.collide.like.domain.entity.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * 点赞实体转换器 - 标准化设计
 * 基于去连表化设计，充分利用冗余字段
 * 
 * @author Collide
 * @since 2.0.0
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeConverter {
    
    /**
     * 实体转换为信息DTO（去连表化版本）
     * 直接使用实体中的冗余字段，无需额外查询
     */
    @Mapping(source = "id", target = "likeId")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "targetId", target = "targetId")
    @Mapping(source = "targetType", target = "likeType")
    @Mapping(source = "actionType", target = "status", qualifiedByName = "actionTypeToStatus")
    @Mapping(source = "createdTime", target = "likedTime")
    @Mapping(source = "updatedTime", target = "updatedTime")
    
    // 去连表化：直接使用冗余的用户信息字段
    @Mapping(source = "userNickname", target = "userNickname")
    @Mapping(source = "userAvatar", target = "userAvatar")
    
    // 去连表化：直接使用冗余的目标信息字段
    @Mapping(source = "targetTitle", target = "targetTitle")
    @Mapping(source = "targetAuthorId", target = "targetAuthorId")
    
    // 额外的跟踪信息
    @Mapping(source = "platform", target = "platform")
    @Mapping(source = "ipAddress", target = "ipAddress")
    LikeInfo toInfo(Like like);
    
    /**
     * 批量转换实体为信息DTO
     * 充分利用去连表化设计，无需额外数据库查询
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
            case 0 -> "CANCELLED";
            default -> "UNKNOWN";
        };
    }
    
    /**
     * 状态描述转换为操作类型
     */
    @org.mapstruct.Named("statusToActionType")
    default Integer statusToActionType(String status) {
        if (status == null) {
            return 0;
        }
        return switch (status.toUpperCase()) {
            case "LIKED" -> 1;
            case "DISLIKED" -> -1;
            case "CANCELLED", "UNLIKED" -> 0;
            default -> 0;
        };
    }
    
    /**
     * 简化的转换方法 - 仅包含核心信息
     */
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "targetId", target = "targetId")
    @Mapping(source = "targetType", target = "likeType")
    @Mapping(source = "actionType", target = "status", qualifiedByName = "actionTypeToStatus")
    @Mapping(source = "userNickname", target = "userNickname")
    @Mapping(source = "createdTime", target = "likedTime")
    LikeInfo toSimpleInfo(Like like);
    
    /**
     * 批量简化转换
     */
    List<LikeInfo> toSimpleInfoList(List<Like> likes);
} 
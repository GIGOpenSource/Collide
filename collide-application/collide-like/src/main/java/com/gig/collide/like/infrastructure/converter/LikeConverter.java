package com.gig.collide.like.infrastructure.converter;

import com.gig.collide.api.like.response.data.LikeInfo;
import com.gig.collide.like.domain.entity.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 点赞转换器 - 标准化设计
 * 
 * 基于去连表化设计，充分利用冗余字段：
 * - 用户信息：userNickname, userAvatar
 * - 目标信息：targetTitle, targetAuthorId  
 * - 扩展信息：platform, ipAddress
 * 
 * @author Collide Team
 * @since 2.0.0
 */
@Mapper(componentModel = "spring")
public interface LikeConverter {
    
    /**
     * 转换实体为信息DTO
     * 充分利用去连表化设计的冗余字段，无需额外数据库查询
     */
    @org.mapstruct.Named("toInfoMethod")
    @Mapping(source = "id", target = "id")
    @Mapping(source = "userId", target = "userId") 
    @Mapping(source = "targetId", target = "targetId")
    @Mapping(source = "targetType", target = "likeType", qualifiedByName = "targetTypeToLikeType")
    @Mapping(source = "actionType", target = "status", qualifiedByName = "actionTypeToStatus")
    @Mapping(source = "updatedTime", target = "likedTime")
    
    // 去连表化的冗余字段，直接映射
    @Mapping(source = "userNickname", target = "userNickname")
    @Mapping(source = "userAvatar", target = "userAvatar")
    
    // 额外的跟踪信息
    @Mapping(source = "ipAddress", target = "ipAddress")
    @Mapping(source = "deviceInfo", target = "deviceInfo")
    LikeInfo toInfo(Like like);
    
    /**
     * 批量转换实体为信息DTO
     * 充分利用去连表化设计，无需额外数据库查询
     */
    @org.mapstruct.IterableMapping(qualifiedByName = "toInfoMethod")
    List<LikeInfo> toInfoList(List<Like> likes);
    
    /**
     * 转换实体为简化信息DTO（用于列表显示）
     */
    @org.mapstruct.Named("toSimpleInfoMethod")
    @Mapping(source = "id", target = "id")
    @Mapping(source = "userId", target = "userId") 
    @Mapping(source = "targetId", target = "targetId")
    @Mapping(source = "targetType", target = "likeType", qualifiedByName = "targetTypeToLikeType")
    @Mapping(source = "actionType", target = "status", qualifiedByName = "actionTypeToStatus")
    @Mapping(source = "updatedTime", target = "likedTime")
    @Mapping(source = "userNickname", target = "userNickname")
    @Mapping(source = "userAvatar", target = "userAvatar")
    // 简化版本不包含设备信息
    @Mapping(target = "ipAddress", ignore = true)
    @Mapping(target = "deviceInfo", ignore = true)
    LikeInfo toSimpleInfo(Like like);
    
    /**
     * 批量转换为简化信息DTO列表
     */
    @org.mapstruct.IterableMapping(qualifiedByName = "toSimpleInfoMethod")
    List<LikeInfo> toSimpleInfoList(List<Like> likes);
    
    /**
     * 目标类型转换为点赞类型
     */
    @org.mapstruct.Named("targetTypeToLikeType")
    default LikeType targetTypeToLikeType(String targetType) {
        if (targetType == null) {
            return null;
        }
        return switch (targetType.toUpperCase()) {
            case "CONTENT" -> LikeType.CONTENT;
            case "COMMENT" -> LikeType.COMMENT;  
            case "USER" -> LikeType.USER;
            default -> null;
        };
    }
    
    /**
     * 操作类型转换为状态描述
     */
    @org.mapstruct.Named("actionTypeToStatus")
    default LikeStatus actionTypeToStatus(Integer actionType) {
        if (actionType == null) {
            return LikeStatus.UNLIKED;
        }
        return switch (actionType) {
            case 1 -> LikeStatus.LIKED;
            case -1 -> LikeStatus.DISLIKED;
            case 0 -> LikeStatus.UNLIKED;
            default -> LikeStatus.UNLIKED;
        };
    }
    
    /**
     * 状态转换为操作类型（反向转换）
     */
    @org.mapstruct.Named("statusToActionType")
    default Integer statusToActionType(LikeStatus status) {
        if (status == null) {
            return 0;
        }
        return switch (status) {
            case LIKED -> 1;
            case DISLIKED -> -1;
            case UNLIKED -> 0;
        };
    }
} 
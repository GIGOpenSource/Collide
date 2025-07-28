package com.gig.collide.social.domain.entity.convertor;

import com.gig.collide.api.social.request.SocialInteractionRequest;
import com.gig.collide.api.social.response.data.SocialInteractionInfo;
import com.gig.collide.api.social.constant.InteractionTypeEnum;
import com.gig.collide.api.social.constant.InteractionStatusEnum;
import com.gig.collide.social.domain.entity.SocialPostInteraction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 社交互动实体转换器
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Mapper
public interface SocialInteractionConvertor {

    SocialInteractionConvertor INSTANCE = Mappers.getMapper(SocialInteractionConvertor.class);

    /**
     * 请求转实体
     */
    @Mapping(target = "interactionStatus", constant = "1")
    @Mapping(target = "userNickname", ignore = true)
    @Mapping(target = "userAvatar", ignore = true)
    SocialPostInteraction fromRequest(SocialInteractionRequest request);

    /**
     * 实体转响应信息
     */
    SocialInteractionInfo toInfo(SocialPostInteraction entity);

    /**
     * 实体列表转响应信息列表
     */
    List<SocialInteractionInfo> toInfoList(List<SocialPostInteraction> entities);

    // 枚举映射的默认方法
    default String mapInteractionTypeEnum(InteractionTypeEnum interactionType) {
        return interactionType != null ? interactionType.name() : null;
    }

    default InteractionTypeEnum mapInteractionTypeString(String interactionType) {
        try {
            return interactionType != null ? InteractionTypeEnum.valueOf(interactionType) : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    default Integer mapInteractionStatusEnum(InteractionStatusEnum status) {
        if (status == null) return null;
        return status == InteractionStatusEnum.ACTIVE ? 1 : 0;
    }

    default InteractionStatusEnum mapInteractionStatusInteger(Integer status) {
        if (status == null || status == 0) {
            return InteractionStatusEnum.CANCELLED;
        }
        return InteractionStatusEnum.ACTIVE;
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
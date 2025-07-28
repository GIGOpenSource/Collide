package com.gig.collide.users.domain.entity.convertor;

import com.gig.collide.api.user.response.data.UserUnifiedInfo;
import com.gig.collide.api.user.response.data.BasicUserUnifiedInfo;
import com.gig.collide.users.domain.entity.UserUnified;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 用户统一信息转换器
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserUnifiedConvertor {

    UserUnifiedConvertor INSTANCE = Mappers.getMapper(UserUnifiedConvertor.class);

    /**
     * 转换为完整的用户信息VO
     * 
     * @param userUnified 用户实体
     * @return 用户信息VO
     */
    @Named("toUserUnifiedInfo")
    @Mapping(target = "userId", source = "id")
    UserUnifiedInfo toUserUnifiedInfo(UserUnified userUnified);

    /**
     * 转换为基础用户信息VO（不包含敏感信息）
     * 
     * @param userUnified 用户实体
     * @return 基础用户信息VO
     */
    @Named("toBasicUserUnifiedInfo")
    @Mapping(target = "userId", source = "id")
    BasicUserUnifiedInfo toBasicUserUnifiedInfo(UserUnified userUnified);

    /**
     * 转换为用户实体
     * 
     * @param userInfo 用户信息VO
     * @return 用户实体
     */
    @Mapping(target = "id", source = "userId")
    UserUnified mapToEntity(UserUnifiedInfo userInfo);

    /**
     * 批量转换为用户信息VO列表
     * 
     * @param userUnifiedList 用户实体列表
     * @return 用户信息VO列表
     */
    @Named("toUserUnifiedInfoList")
    List<UserUnifiedInfo> toUserUnifiedInfoList(List<UserUnified> userUnifiedList);

    /**
     * 批量转换为基础用户信息VO列表
     * 
     * @param userUnifiedList 用户实体列表
     * @return 基础用户信息VO列表
     */
    @Named("toBasicUserUnifiedInfoList")
    List<BasicUserUnifiedInfo> toBasicUserUnifiedInfoList(List<UserUnified> userUnifiedList);
} 
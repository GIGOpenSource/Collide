package com.gig.collide.users.domain.entity.convertor;

import com.gig.collide.api.user.response.data.BasicUserInfo;
import com.gig.collide.api.user.response.data.UserInfo;
import com.gig.collide.users.domain.entity.UserUnified;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.IterableMapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 用户实体转换器
 * 使用 MapStruct 进行 User 实体和 UserInfo DTO 之间的转换
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserConvertor {

    UserConvertor INSTANCE = Mappers.getMapper(UserConvertor.class);

    /**
     * 转换为完整的 UserInfo VO
     *
     * @param user 用户实体
     * @return UserInfo DTO
     */
    @Named("mapToUserInfo")
    @Mapping(target = "userId", source = "id")
    @Mapping(target = "nickName", source = "nickname")
    @Mapping(target = "profilePhotoUrl", source = "avatar")
    @Mapping(target = "followerCount", source = "followerCount")
    @Mapping(target = "followingCount", source = "followingCount")
    @Mapping(target = "contentCount", source = "contentCount")
    @Mapping(target = "likeCount", source = "likeCount")
    @Mapping(target = "bio", source = "bio")
    @Mapping(target = "gender", source = "gender", qualifiedByName = "genderToString")
    @Mapping(target = "birthday", source = "birthday")
    @Mapping(target = "location", source = "location")
    UserInfo mapToVo(UserUnified user);

    /**
     * 转换为简单的 BasicUserInfo VO
     *
     * @param user 用户实体
     * @return BasicUserInfo DTO
     */
    @Named("mapToBasicUserInfo")
    @Mapping(target = "userId", source = "id")
    @Mapping(target = "nickName", source = "nickname")
    @Mapping(target = "profilePhotoUrl", source = "avatar")
    BasicUserInfo mapToBasicVo(UserUnified user);

    /**
     * 转换为用户实体
     *
     * @param userInfo UserInfo DTO
     * @return 用户实体
     */
    @Mapping(target = "id", source = "userId")
    @Mapping(target = "nickname", source = "nickName")
    @Mapping(target = "avatar", source = "profilePhotoUrl")
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "salt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    UserUnified mapToEntity(UserInfo userInfo);

    /**
     * 批量转换为 UserInfo VO 列表
     *
     * @param users 用户实体列表
     * @return UserInfo DTO 列表
     */
    @IterableMapping(qualifiedByName = "mapToUserInfo")
    List<UserInfo> mapToUserInfoList(List<UserUnified> users);

    /**
     * 批量转换为 BasicUserInfo VO 列表
     *
     * @param users 用户实体列表
     * @return BasicUserInfo DTO 列表
     */
    @IterableMapping(qualifiedByName = "mapToBasicUserInfo")
    List<BasicUserInfo> mapToBasicUserInfoList(List<UserUnified> users);

    /**
     * 将UserUnified转换为完整的UserInfo（兼容性方法）
     *
     * @param userUnified 统一用户实体
     * @return UserInfo DTO
     */
    default UserInfo mapToUserInfo(UserUnified userUnified) {
        if (userUnified == null) {
            return null;
        }
        
        // 直接使用主映射方法，因为UserUnified已经包含了所有字段
        return mapToVo(userUnified);
    }

    /**
     * Gender枚举转换为字符串
     */
    @Named("genderToString")
    default String genderToString(UserUnified.Gender gender) {
        return gender != null ? gender.name() : null;
    }
} 
package com.gig.collide.users.domain.entity.convertor;

import com.gig.collide.api.user.response.data.BasicUserInfo;
import com.gig.collide.api.user.response.data.UserInfo;
import com.gig.collide.users.domain.entity.User;
import com.gig.collide.users.domain.entity.UserProfile;
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
    @Mapping(target = "bio", ignore = true)
    @Mapping(target = "gender", ignore = true)
    @Mapping(target = "birthday", ignore = true)
    @Mapping(target = "location", ignore = true)
    UserInfo mapToVo(User user);

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
    BasicUserInfo mapToBasicVo(User user);

    /**
     * 转换为用户实体
     *
     * @param userInfo UserInfo DTO
     * @return User 实体
     */
    @Mapping(target = "id", source = "userId")
    @Mapping(target = "nickname", source = "nickName")
    @Mapping(target = "avatar", source = "profilePhotoUrl")
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "salt", ignore = true)
    User mapToEntity(UserInfo userInfo);

    /**
     * 批量转换为 UserInfo VO 列表
     *
     * @param users 用户实体列表
     * @return UserInfo DTO 列表
     */
    @IterableMapping(qualifiedByName = "mapToUserInfo")
    List<UserInfo> mapToUserInfoList(List<User> users);

    /**
     * 批量转换为 BasicUserInfo VO 列表
     *
     * @param users 用户实体列表
     * @return BasicUserInfo DTO 列表
     */
    @IterableMapping(qualifiedByName = "mapToBasicUserInfo")
    List<BasicUserInfo> mapToBasicUserInfoList(List<User> users);

    /**
     * 合并 User 和 UserProfile 转换为 UserInfo
     *
     * @param user 用户基础信息
     * @param userProfile 用户扩展信息
     * @return UserInfo DTO
     */
    default UserInfo mapToUserInfo(User user, UserProfile userProfile) {
        if (user == null) {
            return null;
        }
        
        UserInfo userInfo = mapToVo(user);
        
        if (userProfile != null) {
            userInfo.setBio(userProfile.getBio());
            userInfo.setGender(userProfile.getGender() != null ? userProfile.getGender().toString() : null);
            userInfo.setBirthday(userProfile.getBirthday());
            userInfo.setLocation(userProfile.getLocation());
        }
        
        return userInfo;
    }

    /**
     * 性别枚举转换为字符串
     *
     * @param gender 性别枚举
     * @return 性别字符串
     */
    @Named("genderToString")
    default String genderToString(UserProfile.Gender gender) {
        return gender != null ? gender.toString() : null;
    }
} 
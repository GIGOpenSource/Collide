package com.gig.collide.users.infrastructure.mapper;

import com.gig.collide.users.domain.entity.UserProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 用户资料Mapper接口 - 对应 t_user_profile 表
 * 负责用户个人资料信息管理
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Mapper
public interface UserProfileMapper {

    /**
     * 根据用户ID查询用户资料
     */
    UserProfile findByUserId(@Param("userId") Long userId);

    /**
     * 批量查询用户资料
     */
    List<UserProfile> findByUserIds(@Param("userIds") List<Long> userIds);

    /**
     * 根据昵称模糊查询用户资料
     */
    List<UserProfile> findByNicknameLike(@Param("nickname") String nickname,
                                        @Param("offset") Integer offset,
                                        @Param("size") Integer size);

    /**
     * 根据所在地查询用户资料
     */
    List<UserProfile> findByLocation(@Param("location") String location,
                                    @Param("offset") Integer offset,
                                    @Param("size") Integer size);

    /**
     * 分页查询用户资料
     */
    List<UserProfile> findProfilesByCondition(@Param("nickname") String nickname,
                                             @Param("gender") Integer gender,
                                             @Param("location") String location,
                                             @Param("offset") Integer offset,
                                             @Param("size") Integer size);

    /**
     * 统计用户资料数量
     */
    Long countProfilesByCondition(@Param("nickname") String nickname,
                                 @Param("gender") Integer gender,
                                 @Param("location") String location);

    /**
     * 检查昵称是否可用
     */
    int checkNicknameAvailable(@Param("nickname") String nickname, 
                              @Param("excludeUserId") Long excludeUserId);

    /**
     * 插入用户资料
     */
    int insert(UserProfile userProfile);

    /**
     * 更新用户资料
     */
    int updateByUserId(UserProfile userProfile);

    /**
     * 更新用户头像
     */
    int updateAvatar(@Param("userId") Long userId, @Param("avatar") String avatar);

    /**
     * 更新用户昵称
     */
    int updateNickname(@Param("userId") Long userId, @Param("nickname") String nickname);

    /**
     * 更新用户生日
     */
    int updateBirthday(@Param("userId") Long userId, @Param("birthday") LocalDate birthday);

    /**
     * 更新用户性别
     */
    int updateGender(@Param("userId") Long userId, @Param("gender") Integer gender);

    /**
     * 更新用户所在地
     */
    int updateLocation(@Param("userId") Long userId, @Param("location") String location);

    /**
     * 更新用户个人简介
     */
    int updateBio(@Param("userId") Long userId, @Param("bio") String bio);

    /**
     * 检查用户资料是否存在
     */
    int checkProfileExists(@Param("userId") Long userId);

    /**
     * 删除用户资料
     */
    int deleteByUserId(@Param("userId") Long userId);
}
package com.gig.collide.tag.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.tag.infrastructure.entity.UserInterestTagEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户兴趣标签 Mapper 接口
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-12-19
 */
@Mapper
public interface UserInterestTagMapper extends BaseMapper<UserInterestTagEntity> {

    /**
     * 根据用户ID获取兴趣标签列表
     *
     * @param userId 用户ID
     * @param status 状态
     * @return 用户兴趣标签列表
     */
    List<UserInterestTagEntity> selectByUserId(@Param("userId") Long userId, @Param("status") String status);

    /**
     * 根据标签ID获取感兴趣的用户列表
     *
     * @param tagId  标签ID
     * @param status 状态
     * @param limit  限制数量
     * @return 用户兴趣标签列表
     */
    List<UserInterestTagEntity> selectByTagId(@Param("tagId") Long tagId,
                                              @Param("status") String status,
                                              @Param("limit") Integer limit);

    /**
     * 查询用户对指定标签的兴趣记录
     *
     * @param userId 用户ID
     * @param tagId  标签ID
     * @return 用户兴趣标签实体
     */
    UserInterestTagEntity selectByUserIdAndTagId(@Param("userId") Long userId, @Param("tagId") Long tagId);

    /**
     * 批量插入用户兴趣标签
     *
     * @param entities 用户兴趣标签列表
     * @return 影响行数
     */
    int batchInsert(@Param("entities") List<UserInterestTagEntity> entities);

    /**
     * 批量删除用户兴趣标签
     *
     * @param userId 用户ID
     * @param tagIds 标签ID列表
     * @return 影响行数
     */
    int batchDeleteByUserIdAndTagIds(@Param("userId") Long userId, @Param("tagIds") List<Long> tagIds);

    /**
     * 更新用户对标签的兴趣分数
     *
     * @param userId        用户ID
     * @param tagId         标签ID
     * @param interestScore 兴趣分数
     * @return 影响行数
     */
    int updateInterestScore(@Param("userId") Long userId,
                            @Param("tagId") Long tagId,
                            @Param("interestScore") BigDecimal interestScore);

    /**
     * 获取用户的高兴趣标签
     *
     * @param userId    用户ID
     * @param minScore  最小兴趣分数
     * @param limit     限制数量
     * @return 用户兴趣标签列表
     */
    List<UserInterestTagEntity> selectHighInterestTags(@Param("userId") Long userId,
                                                        @Param("minScore") BigDecimal minScore,
                                                        @Param("limit") Integer limit);

    /**
     * 清空用户的所有兴趣标签
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteAllByUserId(@Param("userId") Long userId);
} 
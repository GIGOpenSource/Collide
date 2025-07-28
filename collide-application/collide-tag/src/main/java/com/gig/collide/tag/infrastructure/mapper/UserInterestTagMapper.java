package com.gig.collide.tag.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.tag.domain.entity.UserInterestTag;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户兴趣标签Mapper接口
 * 
 * @author GIG Team
 * @version 1.0.0
 * @since 2024-12-19
 */
@Mapper
public interface UserInterestTagMapper extends BaseMapper<UserInterestTag> {

    /**
     * 根据用户ID查询兴趣标签
     * 
     * @param userId 用户ID
     * @param status 状态
     * @return 用户兴趣标签列表
     */
    @Select("SELECT * FROM t_user_interest_tag WHERE user_id = #{userId} AND status = #{status} ORDER BY interest_score DESC")
    List<UserInterestTag> selectByUserId(@Param("userId") Long userId, @Param("status") String status);

    /**
     * 根据标签ID查询感兴趣的用户
     * 
     * @param tagId 标签ID
     * @param status 状态
     * @return 用户兴趣标签列表
     */
    @Select("SELECT * FROM t_user_interest_tag WHERE tag_id = #{tagId} AND status = #{status} ORDER BY interest_score DESC")
    List<UserInterestTag> selectByTagId(@Param("tagId") Long tagId, @Param("status") String status);

    /**
     * 根据用户ID和标签ID查询兴趣标签
     * 
     * @param userId 用户ID
     * @param tagId 标签ID
     * @return 用户兴趣标签
     */
    @Select("SELECT * FROM t_user_interest_tag WHERE user_id = #{userId} AND tag_id = #{tagId} LIMIT 1")
    UserInterestTag selectByUserIdAndTagId(@Param("userId") Long userId, @Param("tagId") Long tagId);

    /**
     * 根据用户ID批量删除兴趣标签
     * 
     * @param userId 用户ID
     * @return 删除行数
     */
    @Delete("DELETE FROM t_user_interest_tag WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 根据标签ID批量删除兴趣标签
     * 
     * @param tagId 标签ID
     * @return 删除行数
     */
    @Delete("DELETE FROM t_user_interest_tag WHERE tag_id = #{tagId}")
    int deleteByTagId(@Param("tagId") Long tagId);

    /**
     * 根据用户ID和标签ID列表查询兴趣标签
     * 
     * @param userId 用户ID
     * @param tagIds 标签ID列表
     * @return 用户兴趣标签列表
     */
    @Select("<script>" +
            "SELECT * FROM t_user_interest_tag WHERE user_id = #{userId} AND tag_id IN " +
            "<foreach collection='tagIds' item='tagId' open='(' separator=',' close=')'>" +
            "#{tagId}" +
            "</foreach>" +
            "</script>")
    List<UserInterestTag> selectByUserIdAndTagIds(@Param("userId") Long userId, @Param("tagIds") List<Long> tagIds);

    /**
     * 查询用户对特定类型标签的平均兴趣分数
     * 
     * @param userId 用户ID
     * @param tagType 标签类型
     * @param status 状态
     * @return 平均兴趣分数
     */
    @Select("SELECT AVG(uit.interest_score) FROM t_user_interest_tag uit " +
            "INNER JOIN t_tag t ON uit.tag_id = t.id " +
            "WHERE uit.user_id = #{userId} AND uit.status = #{status} AND t.tag_type = #{tagType}")
    BigDecimal selectAvgInterestScoreByType(@Param("userId") Long userId, @Param("tagType") String tagType, @Param("status") String status);

    /**
     * 统计用户兴趣标签数量
     * 
     * @param userId 用户ID
     * @param status 状态
     * @return 兴趣标签数量
     */
    @Select("SELECT COUNT(*) FROM t_user_interest_tag WHERE user_id = #{userId} AND status = #{status}")
    Long countByUserId(@Param("userId") Long userId, @Param("status") String status);

    /**
     * 统计标签的感兴趣用户数量
     * 
     * @param tagId 标签ID
     * @param status 状态
     * @return 感兴趣用户数量
     */
    @Select("SELECT COUNT(*) FROM t_user_interest_tag WHERE tag_id = #{tagId} AND status = #{status}")
    Long countByTagId(@Param("tagId") Long tagId, @Param("status") String status);
} 
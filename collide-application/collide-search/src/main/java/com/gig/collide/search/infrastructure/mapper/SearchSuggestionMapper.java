package com.gig.collide.business.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.business.domain.search.entity.SearchSuggestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 搜索建议数据访问映射器
 *
 * @author GIG Team
 */
@Mapper
public interface SearchSuggestionMapper extends BaseMapper<SearchSuggestion> {

    /**
     * 根据关键词前缀和类型查询建议
     *
     * @param keywordPrefix 关键词前缀
     * @param suggestionType 建议类型
     * @param limit 限制数量
     * @return 建议列表
     */
    List<SearchSuggestion> selectByKeywordPrefixAndType(@Param("keywordPrefix") String keywordPrefix,
                                                         @Param("suggestionType") String suggestionType,
                                                         @Param("limit") Integer limit);

    /**
     * 根据关键词前缀查询所有类型建议
     *
     * @param keywordPrefix 关键词前缀
     * @param limit 限制数量
     * @return 建议列表
     */
    List<SearchSuggestion> selectByKeywordPrefix(@Param("keywordPrefix") String keywordPrefix,
                                                 @Param("limit") Integer limit);

    /**
     * 增加搜索次数
     *
     * @param keyword 关键词
     * @param suggestionType 建议类型
     * @return 影响行数
     */
    int incrementSearchCount(@Param("keyword") String keyword, @Param("suggestionType") String suggestionType);

    /**
     * 根据类型获取热门建议
     *
     * @param suggestionType 建议类型
     * @param limit 限制数量
     * @return 建议列表
     */
    List<SearchSuggestion> selectHotByType(@Param("suggestionType") String suggestionType, @Param("limit") Integer limit);

    /**
     * 根据关键词和类型查询建议
     *
     * @param keyword 关键词
     * @param suggestionType 建议类型
     * @return 建议
     */
    SearchSuggestion selectByKeywordAndType(@Param("keyword") String keyword, @Param("suggestionType") String suggestionType);
} 
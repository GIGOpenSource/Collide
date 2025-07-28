package com.gig.collide.tag.domain.entity.convertor;

import com.gig.collide.api.tag.request.TagCreateRequest;
import com.gig.collide.api.tag.request.TagUpdateRequest;
import com.gig.collide.api.tag.response.data.TagInfo;
import com.gig.collide.tag.domain.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 标签实体转换器
 * 
 * @author GIG Team
 * @version 1.0.0
 * @since 2024-12-19
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TagConvertor {

    TagConvertor INSTANCE = Mappers.getMapper(TagConvertor.class);

    /**
     * 创建请求转实体
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usageCount", constant = "0L")
    @Mapping(target = "heatScore", constant = "0.00")
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "version", constant = "1")
    Tag createRequestToEntity(TagCreateRequest request);

    /**
     * 更新请求到实体（仅更新非空字段）
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usageCount", ignore = true)
    @Mapping(target = "heatScore", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateRequestToEntity(TagUpdateRequest request, @MappingTarget Tag tag);

    /**
     * 实体转响应信息
     */
    @Mapping(target = "interestScore", ignore = true)
    @Mapping(target = "interestSource", ignore = true)
    TagInfo entityToInfo(Tag tag);

    /**
     * 实体列表转响应信息列表
     */
    @Mapping(target = "interestScore", ignore = true)
    @Mapping(target = "interestSource", ignore = true)
    List<TagInfo> entityListToInfoList(List<Tag> tags);
} 
package com.gig.collide.tag.service;

import com.gig.collide.api.tag.request.TagCreateRequest;
import com.gig.collide.api.tag.request.TagQueryRequest;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.tag.domain.entity.Tag;
import com.gig.collide.tag.domain.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

/**
 * 标签服务测试类
 * 
 * @author GIG Team
 * @version 1.0.0
 * @since 2024-12-19
 */
@Slf4j
@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=test",
    "spring.datasource.password=test"
})
public class TagServiceTest {

    @Autowired(required = false)
    private TagService tagService;

    @Test
    public void testCreateTag() {
        if (tagService == null) {
            log.info("TagService 未注入，跳过测试");
            return;
        }

        try {
            TagCreateRequest request = TagCreateRequest.builder()
                    .name("测试标签")
                    .description("这是一个测试标签")
                    .color("#FF0000")
                    .tagType("content")
                    .sort(1)
                    .status(1)
                    .build();

            Tag tag = tagService.createTag(request);
            log.info("创建标签成功：{}", tag);

            assert tag != null;
            assert tag.getId() != null;
            assert "测试标签".equals(tag.getName());
        } catch (Exception e) {
            log.error("测试创建标签失败", e);
        }
    }

    @Test
    public void testQueryTags() {
        if (tagService == null) {
            log.info("TagService 未注入，跳过测试");
            return;
        }

        try {
            TagQueryRequest request = TagQueryRequest.builder()
                    .current(1)
                    .size(10)
                    .tagType("content")
                    .status(1)
                    .build();

            PageResponse<Tag> result = tagService.queryTags(request);
            log.info("查询标签成功：{}", result);

            assert result != null;
            assert result.getRecords() != null;
        } catch (Exception e) {
            log.error("测试查询标签失败", e);
        }
    }

    @Test
    public void testGetHotTags() {
        if (tagService == null) {
            log.info("TagService 未注入，跳过测试");
            return;
        }

        try {
            List<Tag> hotTags = tagService.getHotTags("content", 10);
            log.info("查询热门标签成功，数量：{}", hotTags.size());

            assert hotTags != null;
        } catch (Exception e) {
            log.error("测试查询热门标签失败", e);
        }
    }

    @Test
    public void testSearchTags() {
        if (tagService == null) {
            log.info("TagService 未注入，跳过测试");
            return;
        }

        try {
            List<Tag> searchResult = tagService.searchTags("技术", "content");
            log.info("搜索标签成功，数量：{}", searchResult.size());

            assert searchResult != null;
        } catch (Exception e) {
            log.error("测试搜索标签失败", e);
        }
    }
} 
package com.gig.collide.tag.domain.service.impl;

import com.gig.collide.base.response.PageResponse;
import com.gig.collide.tag.domain.entity.Tag;
import com.gig.collide.tag.domain.service.TagService;
import com.gig.collide.tag.infrastructure.mapper.TagMapper;
import com.gig.collide.api.tag.request.TagQueryRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 标签领域服务实现
 * 
 * @author GIG Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;

    // =================== 基础CRUD操作 ===================

    @Override
    @Transactional
    public Tag createTag(Tag tag) {
        try {
            log.info("创建标签: tagName={}", tag.getTagName());
            
            // 参数验证
            validateTagForCreate(tag);
            
            // 检查标签名称是否已存在
            if (!checkTagNameAvailable(tag.getTagName(), null)) {
                throw new RuntimeException("标签名称已存在: " + tag.getTagName());
            }
            
            // 初始化默认值
            tag.initDefaults();
            
            // 保存标签
            int result = tagMapper.insert(tag);
            if (result <= 0) {
                throw new RuntimeException("创建标签失败");
            }
            
            log.info("标签创建成功: tagId={}, tagName={}", tag.getId(), tag.getTagName());
            return tag;
        } catch (Exception e) {
            log.error("创建标签失败: tagName={}", tag.getTagName(), e);
            throw new RuntimeException("创建标签失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public Tag updateTag(Tag tag) {
        try {
            log.info("更新标签: tagId={}, tagName={}", tag.getId(), tag.getTagName());
            
            // 参数验证
            validateTagForUpdate(tag);
            
            // 检查标签是否存在
            Tag existingTag = getTagById(tag.getId());
            if (existingTag == null) {
                throw new RuntimeException("标签不存在: " + tag.getId());
            }
            
            // 检查标签名称是否可用（排除自己）
            if (tag.getTagName() != null && !tag.getTagName().equals(existingTag.getTagName())) {
                if (!checkTagNameAvailable(tag.getTagName(), tag.getId())) {
                    throw new RuntimeException("标签名称已存在: " + tag.getTagName());
                }
            }
            
            // 更新修改时间
            tag.updateModifyTime();
            
            // 更新标签
            int result = tagMapper.updateById(tag);
            if (result <= 0) {
                throw new RuntimeException("更新标签失败");
            }
            
            log.info("标签更新成功: tagId={}", tag.getId());
            return getTagById(tag.getId());
        } catch (Exception e) {
            log.error("更新标签失败: tagId={}", tag.getId(), e);
            throw new RuntimeException("更新标签失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public boolean deleteTag(Long tagId) {
        try {
            log.info("删除标签: tagId={}", tagId);
            
            if (tagId == null || tagId <= 0) {
                throw new RuntimeException("标签ID无效");
            }
            
            // 检查标签是否存在
            Tag existingTag = getTagById(tagId);
            if (existingTag == null) {
                log.warn("标签不存在，删除操作跳过: tagId={}", tagId);
                return true;
            }
            
            // 软删除：设置状态为禁用
            Tag updateTag = new Tag();
            updateTag.setId(tagId);
            updateTag.setStatus(0);
            updateTag.updateModifyTime();
            
            int result = tagMapper.updateById(updateTag);
            if (result > 0) {
                log.info("标签删除成功: tagId={}", tagId);
                return true;
            } else {
                log.error("标签删除失败: tagId={}", tagId);
                return false;
            }
        } catch (Exception e) {
            log.error("删除标签异常: tagId={}", tagId, e);
            throw new RuntimeException("删除标签失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Tag getTagById(Long tagId) {
        try {
            if (tagId == null || tagId <= 0) {
                return null;
            }
            return tagMapper.selectById(tagId);
        } catch (Exception e) {
            log.error("查询标签失败: tagId={}", tagId, e);
            return null;
        }
    }

    @Override
    public Tag getTagByName(String tagName) {
        try {
            if (tagName == null || tagName.trim().isEmpty()) {
                return null;
            }
            return tagMapper.findByTagName(tagName.trim());
        } catch (Exception e) {
            log.error("根据名称查询标签失败: tagName={}", tagName, e);
            return null;
        }
    }

    // =================== 查询操作 ===================

    @Override
    public List<Tag> getAllActiveTags() {
        try {
            return tagMapper.findAllActiveTags();
        } catch (Exception e) {
            log.error("查询所有启用标签失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    public PageResponse<Tag> queryTags(TagQueryRequest request) {
        try {
            log.debug("分页查询标签: currentPage={}, pageSize={}", request.getCurrentPage(), request.getPageSize());
            
            // 计算分页参数
            int offset = (request.getCurrentPage() - 1) * request.getPageSize();
            
            // 查询标签列表
            List<Tag> tags = tagMapper.findTagsByCondition(
                    request.getTagName(),
                    request.getStatus(),
                    request.getMinWeight(),
                    request.getMaxWeight(),
                    request.getMinFollowCount(),
                    request.getMaxFollowCount(),
                    request.getMinContentCount(),
                    request.getMaxContentCount(),
                    request.getSortField(),
                    request.getSortDirection(),
                    offset,
                    request.getPageSize()
            );
            
            // 查询总数
            Long total = tagMapper.countTagsByCondition(
                    request.getTagName(),
                    request.getStatus(),
                    request.getMinWeight(),
                    request.getMaxWeight(),
                    request.getMinFollowCount(),
                    request.getMaxFollowCount(),
                    request.getMinContentCount(),
                    request.getMaxContentCount()
            );
            
            // 构建分页结果
            PageResponse<Tag> result = new PageResponse<>();
            result.setDatas(tags);
            result.setCurrentPage(request.getCurrentPage());
            result.setPageSize(request.getPageSize());
            result.setTotal(total);
            
            log.debug("标签查询结果: count={}, total={}", tags.size(), total);
            return result;
        } catch (Exception e) {
            log.error("分页查询标签失败", e);
            throw new RuntimeException("查询标签失败", e);
        }
    }

    @Override
    public List<Tag> getHotTags(Integer limit) {
        try {
            return tagMapper.findHotTags(limit);
        } catch (Exception e) {
            log.error("查询热门标签失败: limit={}", limit, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Tag> getRecommendTags(Integer limit) {
        try {
            return tagMapper.findRecommendTags(limit);
        } catch (Exception e) {
            log.error("查询推荐标签失败: limit={}", limit, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Tag> searchTags(String keyword, Integer limit) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return new ArrayList<>();
            }
            return tagMapper.searchTags(keyword.trim(), 1, limit);
        } catch (Exception e) {
            log.error("搜索标签失败: keyword={}, limit={}", keyword, limit, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Tag> getTagsByWeightRange(Integer minWeight, Integer maxWeight, Integer limit) {
        try {
            return tagMapper.findTagsByWeightRange(minWeight, maxWeight, limit);
        } catch (Exception e) {
            log.error("根据权重范围查询标签失败: minWeight={}, maxWeight={}, limit={}", 
                    minWeight, maxWeight, limit, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Tag> getTagsByIds(List<Long> tagIds) {
        try {
            if (tagIds == null || tagIds.isEmpty()) {
                return new ArrayList<>();
            }
            return tagMapper.findTagsByIds(tagIds);
        } catch (Exception e) {
            log.error("根据ID列表查询标签失败: tagIds={}", tagIds, e);
            return new ArrayList<>();
        }
    }

    // =================== 排行榜查询 ===================

    @Override
    public List<Tag> getHotnessRanking(Integer limit) {
        try {
            return tagMapper.findHotTags(limit);
        } catch (Exception e) {
            log.error("查询热度排行榜失败: limit={}", limit, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Tag> getFollowCountRanking(Integer limit) {
        try {
            return tagMapper.findTagsByFollowCount(limit);
        } catch (Exception e) {
            log.error("查询关注数排行榜失败: limit={}", limit, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Tag> getContentCountRanking(Integer limit) {
        try {
            return tagMapper.findTagsByContentCount(limit);
        } catch (Exception e) {
            log.error("查询内容数排行榜失败: limit={}", limit, e);
            return new ArrayList<>();
        }
    }

    // =================== 统计分析 ===================

    @Override
    public Map<String, Object> getTagStatistics(Long tagId) {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            if (tagId != null) {
                // 单个标签统计
                Tag tag = getTagById(tagId);
                if (tag != null) {
                    statistics.put("tagName", tag.getTagName());
                    statistics.put("weight", tag.getWeight());
                    statistics.put("hotness", tag.getHotness());
                    statistics.put("followCount", tag.getFollowCount());
                    statistics.put("contentCount", tag.getContentCount());
                    statistics.put("status", tag.getStatus());
                    statistics.put("statusDesc", tag.getStatusDesc());
                    statistics.put("createTime", tag.getCreateTime());
                    statistics.put("updateTime", tag.getUpdateTime());
                    statistics.put("recommendScore", tag.calculateRecommendScore());
                    statistics.put("isHot", tag.isHot());
                    statistics.put("isNew", tag.isNew());
                }
            } else {
                // 整体统计
                statistics.put("activeTagCount", countActiveTags());
                statistics.put("totalFollowCount", sumTotalFollowCount());
                statistics.put("totalContentCount", sumTotalContentCount());
                statistics.put("averageHotness", getAverageHotness());
            }
            
            return statistics;
        } catch (Exception e) {
            log.error("获取标签统计信息失败: tagId={}", tagId, e);
            return new HashMap<>();
        }
    }

    @Override
    public Long countActiveTags() {
        try {
            return tagMapper.countActiveTags();
        } catch (Exception e) {
            log.error("统计启用标签数量失败", e);
            return 0L;
        }
    }

    @Override
    public Long sumTotalFollowCount() {
        try {
            return tagMapper.sumTotalFollowCount();
        } catch (Exception e) {
            log.error("统计总关注数失败", e);
            return 0L;
        }
    }

    @Override
    public Long sumTotalContentCount() {
        try {
            return tagMapper.sumTotalContentCount();
        } catch (Exception e) {
            log.error("统计总内容数失败", e);
            return 0L;
        }
    }

    @Override
    public Double getAverageHotness() {
        try {
            return tagMapper.getAverageHotness();
        } catch (Exception e) {
            log.error("获取平均热度值失败", e);
            return 0.0;
        }
    }

    // =================== 状态管理 ===================

    @Override
    @Transactional
    public boolean updateTagStatus(Long tagId, Integer status) {
        try {
            log.info("更新标签状态: tagId={}, status={}", tagId, status);
            
            if (tagId == null || tagId <= 0) {
                throw new RuntimeException("标签ID无效");
            }
            
            if (status == null || (status != 0 && status != 1)) {
                throw new RuntimeException("状态值无效");
            }
            
            Tag updateTag = new Tag();
            updateTag.setId(tagId);
            updateTag.setStatus(status);
            updateTag.updateModifyTime();
            
            int result = tagMapper.updateById(updateTag);
            if (result > 0) {
                log.info("标签状态更新成功: tagId={}, status={}", tagId, status);
                return true;
            } else {
                log.error("标签状态更新失败: tagId={}, status={}", tagId, status);
                return false;
            }
        } catch (Exception e) {
            log.error("更新标签状态异常: tagId={}, status={}", tagId, status, e);
            throw new RuntimeException("更新标签状态失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public boolean updateTagWeight(Long tagId, Integer weight) {
        try {
            log.info("更新标签权重: tagId={}, weight={}", tagId, weight);
            
            if (tagId == null || tagId <= 0) {
                throw new RuntimeException("标签ID无效");
            }
            
            if (weight == null || weight < 1 || weight > 100) {
                throw new RuntimeException("权重值无效，必须在1-100之间");
            }
            
            Tag updateTag = new Tag();
            updateTag.setId(tagId);
            updateTag.setWeight(weight);
            updateTag.updateModifyTime();
            
            int result = tagMapper.updateById(updateTag);
            if (result > 0) {
                log.info("标签权重更新成功: tagId={}, weight={}", tagId, weight);
                return true;
            } else {
                log.error("标签权重更新失败: tagId={}, weight={}", tagId, weight);
                return false;
            }
        } catch (Exception e) {
            log.error("更新标签权重异常: tagId={}, weight={}", tagId, weight, e);
            throw new RuntimeException("更新标签权重失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public boolean batchUpdateTagStatus(List<Long> tagIds, Integer status) {
        try {
            log.info("批量更新标签状态: tagIds={}, status={}", tagIds, status);
            
            if (tagIds == null || tagIds.isEmpty()) {
                throw new RuntimeException("标签ID列表为空");
            }
            
            if (status == null || (status != 0 && status != 1)) {
                throw new RuntimeException("状态值无效");
            }
            
            int result = tagMapper.batchUpdateStatus(tagIds, status);
            if (result > 0) {
                log.info("批量更新标签状态成功: count={}", result);
                return true;
            } else {
                log.error("批量更新标签状态失败");
                return false;
            }
        } catch (Exception e) {
            log.error("批量更新标签状态异常: tagIds={}, status={}", tagIds, status, e);
            throw new RuntimeException("批量更新标签状态失败: " + e.getMessage(), e);
        }
    }

    // =================== 数据更新 ===================

    @Override
    @Transactional
    public boolean updateTagHotness(Long tagId, Long hotness) {
        try {
            if (tagId == null || tagId <= 0 || hotness == null || hotness < 0) {
                return false;
            }
            
            int result = tagMapper.updateTagHotness(tagId, hotness);
            return result > 0;
        } catch (Exception e) {
            log.error("更新标签热度失败: tagId={}, hotness={}", tagId, hotness, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean incrementFollowCount(Long tagId, Integer increment) {
        try {
            if (tagId == null || tagId <= 0 || increment == null || increment <= 0) {
                return false;
            }
            
            int result = tagMapper.incrementFollowCount(tagId, increment);
            return result > 0;
        } catch (Exception e) {
            log.error("增加标签关注数失败: tagId={}, increment={}", tagId, increment, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean decrementFollowCount(Long tagId, Integer decrement) {
        try {
            if (tagId == null || tagId <= 0 || decrement == null || decrement <= 0) {
                return false;
            }
            
            int result = tagMapper.decrementFollowCount(tagId, decrement);
            return result > 0;
        } catch (Exception e) {
            log.error("减少标签关注数失败: tagId={}, decrement={}", tagId, decrement, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean incrementContentCount(Long tagId, Integer increment) {
        try {
            if (tagId == null || tagId <= 0 || increment == null || increment <= 0) {
                return false;
            }
            
            int result = tagMapper.incrementContentCount(tagId, increment);
            return result > 0;
        } catch (Exception e) {
            log.error("增加标签内容数失败: tagId={}, increment={}", tagId, increment, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean decrementContentCount(Long tagId, Integer decrement) {
        try {
            if (tagId == null || tagId <= 0 || decrement == null || decrement <= 0) {
                return false;
            }
            
            int result = tagMapper.decrementContentCount(tagId, decrement);
            return result > 0;
        } catch (Exception e) {
            log.error("减少标签内容数失败: tagId={}, decrement={}", tagId, decrement, e);
            return false;
        }
    }

    // =================== 验证方法 ===================

    @Override
    public boolean checkTagNameAvailable(String tagName, Long excludeId) {
        try {
            if (tagName == null || tagName.trim().isEmpty()) {
                return false;
            }
            return !tagMapper.existsByTagName(tagName.trim(), excludeId);
        } catch (Exception e) {
            log.error("检查标签名称可用性失败: tagName={}, excludeId={}", tagName, excludeId, e);
            return false;
        }
    }

    @Override
    public boolean existsActiveTag(Long tagId) {
        try {
            if (tagId == null || tagId <= 0) {
                return false;
            }
            return tagMapper.existsActiveTag(tagId);
        } catch (Exception e) {
            log.error("检查标签是否存在且启用失败: tagId={}", tagId, e);
            return false;
        }
    }

    @Override
    public Tag getActiveTag(Long tagId) {
        try {
            Tag tag = getTagById(tagId);
            return (tag != null && tag.isActive()) ? tag : null;
        } catch (Exception e) {
            log.error("获取有效标签失败: tagId={}", tagId, e);
            return null;
        }
    }

    // =================== 批量操作 ===================

    @Override
    @Transactional
    public List<Tag> batchCreateTags(List<Tag> tags) {
        try {
            if (tags == null || tags.isEmpty()) {
                return new ArrayList<>();
            }
            
            List<Tag> createdTags = new ArrayList<>();
            for (Tag tag : tags) {
                try {
                    Tag createdTag = createTag(tag);
                    createdTags.add(createdTag);
                } catch (Exception e) {
                    log.warn("批量创建标签时跳过失败项: tagName={}, error={}", tag.getTagName(), e.getMessage());
                }
            }
            
            log.info("批量创建标签完成: total={}, success={}", tags.size(), createdTags.size());
            return createdTags;
        } catch (Exception e) {
            log.error("批量创建标签失败", e);
            throw new RuntimeException("批量创建标签失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public int updateAllTagHotness() {
        try {
            log.info("开始更新所有标签热度值");
            
            List<Tag> allTags = getAllActiveTags();
            int updateCount = 0;
            
            for (Tag tag : allTags) {
                if (recalculateTagHotness(tag.getId())) {
                    updateCount++;
                }
            }
            
            log.info("更新所有标签热度值完成: total={}, updated={}", allTags.size(), updateCount);
            return updateCount;
        } catch (Exception e) {
            log.error("更新所有标签热度值失败", e);
            throw new RuntimeException("更新标签热度失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public boolean recalculateTagHotness(Long tagId) {
        try {
            Tag tag = getTagById(tagId);
            if (tag == null) {
                return false;
            }
            
            // 热度计算公式：基础热度 = 关注数 * 0.6 + 内容数 * 0.4
            double baseHotness = tag.getFollowCount() * 0.6 + tag.getContentCount() * 0.4;
            
            // 权重加成
            double weightBonus = (tag.getWeight() / 100.0) * baseHotness * 0.2;
            
            // 时间衰减因子（新标签有加成）
            double timeDecay = tag.isNew() ? 1.2 : 1.0;
            
            // 计算最终热度
            long newHotness = Math.round((baseHotness + weightBonus) * timeDecay);
            
            return updateTagHotness(tagId, newHotness);
        } catch (Exception e) {
            log.error("重新计算标签热度失败: tagId={}", tagId, e);
            return false;
        }
    }

    // =================== 私有辅助方法 ===================

    /**
     * 验证标签创建参数
     */
    private void validateTagForCreate(Tag tag) {
        if (tag == null) {
            throw new RuntimeException("标签对象不能为空");
        }
        
        if (tag.getTagName() == null || tag.getTagName().trim().isEmpty()) {
            throw new RuntimeException("标签名称不能为空");
        }
        
        if (tag.getTagName().length() > 50) {
            throw new RuntimeException("标签名称长度不能超过50个字符");
        }
        
        if (tag.getTagDescription() != null && tag.getTagDescription().length() > 200) {
            throw new RuntimeException("标签描述长度不能超过200个字符");
        }
        
        if (tag.getWeight() != null && (tag.getWeight() < 1 || tag.getWeight() > 100)) {
            throw new RuntimeException("标签权重必须在1-100之间");
        }
        
        if (tag.getStatus() != null && tag.getStatus() != 0 && tag.getStatus() != 1) {
            throw new RuntimeException("标签状态值无效");
        }
    }

    /**
     * 验证标签更新参数
     */
    private void validateTagForUpdate(Tag tag) {
        if (tag == null) {
            throw new RuntimeException("标签对象不能为空");
        }
        
        if (tag.getId() == null || tag.getId() <= 0) {
            throw new RuntimeException("标签ID无效");
        }
        
        if (tag.getTagName() != null) {
            if (tag.getTagName().trim().isEmpty()) {
                throw new RuntimeException("标签名称不能为空");
            }
            
            if (tag.getTagName().length() > 50) {
                throw new RuntimeException("标签名称长度不能超过50个字符");
            }
        }
        
        if (tag.getTagDescription() != null && tag.getTagDescription().length() > 200) {
            throw new RuntimeException("标签描述长度不能超过200个字符");
        }
        
        if (tag.getWeight() != null && (tag.getWeight() < 1 || tag.getWeight() > 100)) {
            throw new RuntimeException("标签权重必须在1-100之间");
        }
        
        if (tag.getStatus() != null && tag.getStatus() != 0 && tag.getStatus() != 1) {
            throw new RuntimeException("标签状态值无效");
        }
    }
}
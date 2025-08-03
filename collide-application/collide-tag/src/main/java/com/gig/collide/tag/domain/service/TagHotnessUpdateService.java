package com.gig.collide.tag.domain.service;

import com.gig.collide.tag.domain.service.TagService;
import com.gig.collide.tag.domain.service.UserTagService;
import com.gig.collide.tag.domain.service.ContentTagService;
import com.gig.collide.tag.domain.entity.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 标签热度更新服务
 * 
 * @author GIG Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TagHotnessUpdateService {

    private final TagService tagService;
    private final UserTagService userTagService;
    private final ContentTagService contentTagService;

    /**
     * 每小时更新标签热度
     */
    @Scheduled(cron = "0 0 * * * ?") // 每小时执行一次
    public void updateTagHotnessHourly() {
        try {
            log.info("开始执行每小时标签热度更新任务");
            
            // 获取需要更新的活跃标签
            List<Tag> activeTags = tagService.getAllActiveTags();
            
            int updateCount = 0;
            for (Tag tag : activeTags) {
                try {
                    if (updateTagHotnessValue(tag)) {
                        updateCount++;
                    }
                } catch (Exception e) {
                    log.warn("更新标签热度失败: tagId={}, tagName={}", tag.getId(), tag.getTagName(), e);
                }
            }
            
            log.info("每小时标签热度更新任务完成: total={}, updated={}", activeTags.size(), updateCount);
        } catch (Exception e) {
            log.error("每小时标签热度更新任务执行失败", e);
        }
    }

    /**
     * 每日更新标签热度（更全面的更新）
     */
    @Scheduled(cron = "0 30 2 * * ?") // 每天凌晨2:30执行
    public void updateTagHotnessDaily() {
        try {
            log.info("开始执行每日标签热度更新任务");
            
            // 更新所有标签的热度值
            int updateCount = tagService.updateAllTagHotness();
            
            log.info("每日标签热度更新任务完成: updated={}", updateCount);
        } catch (Exception e) {
            log.error("每日标签热度更新任务执行失败", e);
        }
    }

    /**
     * 每周进行标签热度的深度分析和调整
     */
    @Scheduled(cron = "0 0 3 ? * SUN") // 每周日凌晨3点执行
    public void updateTagHotnessWeekly() {
        try {
            log.info("开始执行每周标签热度深度更新任务");
            
            List<Tag> allTags = tagService.getAllActiveTags();
            int updateCount = 0;
            
            for (Tag tag : allTags) {
                try {
                    if (updateTagHotnessWithTimeDecay(tag)) {
                        updateCount++;
                    }
                } catch (Exception e) {
                    log.warn("更新标签热度（含时间衰减）失败: tagId={}, tagName={}", tag.getId(), tag.getTagName(), e);
                }
            }
            
            log.info("每周标签热度深度更新任务完成: total={}, updated={}", allTags.size(), updateCount);
        } catch (Exception e) {
            log.error("每周标签热度深度更新任务执行失败", e);
        }
    }

    /**
     * 更新单个标签的热度值
     * 
     * @param tag 标签实体
     * @return 是否更新成功
     */
    private boolean updateTagHotnessValue(Tag tag) {
        try {
            // 获取最新的关注数和内容数
            Long followerCount = userTagService.getTagFollowerCount(tag.getId());
            Long contentCount = contentTagService.countContentsByTag(tag.getId());
            
            // 热度计算公式：关注数权重60%，内容数权重40%
            double baseHotness = followerCount * 0.6 + contentCount * 0.4;
            
            // 权重加成：高权重标签有额外加成
            double weightBonus = (tag.getWeight() / 100.0) * baseHotness * 0.2;
            
            // 活跃度加成：最近7天有新关注或新内容的标签
            double activityBonus = calculateActivityBonus(tag.getId());
            
            // 计算最终热度
            long newHotness = Math.round(baseHotness + weightBonus + activityBonus);
            
            // 确保热度不为负数
            newHotness = Math.max(0, newHotness);
            
            // 更新标签热度
            boolean updated = tagService.updateTagHotness(tag.getId(), newHotness);
            
            if (updated) {
                log.debug("标签热度更新成功: tagId={}, tagName={}, oldHotness={}, newHotness={}", 
                        tag.getId(), tag.getTagName(), tag.getHotness(), newHotness);
            }
            
            return updated;
        } catch (Exception e) {
            log.error("更新标签热度值失败: tagId={}", tag.getId(), e);
            return false;
        }
    }

    /**
     * 带时间衰减的标签热度更新
     * 
     * @param tag 标签实体
     * @return 是否更新成功
     */
    private boolean updateTagHotnessWithTimeDecay(Tag tag) {
        try {
            // 基础热度计算
            Long followerCount = userTagService.getTagFollowerCount(tag.getId());
            Long contentCount = contentTagService.countContentsByTag(tag.getId());
            
            double baseHotness = followerCount * 0.6 + contentCount * 0.4;
            
            // 权重加成
            double weightBonus = (tag.getWeight() / 100.0) * baseHotness * 0.2;
            
            // 时间衰减：根据标签创建时间计算衰减因子
            double timeDecayFactor = calculateTimeDecayFactor(tag);
            
            // 活跃度加成
            double activityBonus = calculateActivityBonus(tag.getId());
            
            // 社交网络效应：考虑关注者的质量
            double socialNetworkEffect = calculateSocialNetworkEffect(tag.getId());
            
            // 综合计算最终热度
            double finalHotness = (baseHotness + weightBonus + activityBonus + socialNetworkEffect) * timeDecayFactor;
            
            long newHotness = Math.round(Math.max(0, finalHotness));
            
            boolean updated = tagService.updateTagHotness(tag.getId(), newHotness);
            
            if (updated) {
                log.debug("标签热度更新成功（含时间衰减）: tagId={}, tagName={}, oldHotness={}, newHotness={}", 
                        tag.getId(), tag.getTagName(), tag.getHotness(), newHotness);
            }
            
            return updated;
        } catch (Exception e) {
            log.error("更新标签热度值失败（含时间衰减）: tagId={}", tag.getId(), e);
            return false;
        }
    }

    /**
     * 计算活跃度加成
     * 
     * @param tagId 标签ID
     * @return 活跃度加成分数
     */
    private double calculateActivityBonus(Long tagId) {
        try {
            // 获取最近7天的新关注者
            List<Long> recentFollowers = userTagService.getTagRecentFollowers(tagId, 7, 100);
            
            // 获取最近7天的新内容
            List<Long> recentContents = contentTagService.getTagLatestContents(tagId, 7, 100);
            
            // 活跃度加成：新关注者和新内容的综合考量
            double activityScore = recentFollowers.size() * 2.0 + recentContents.size() * 1.5;
            
            // 限制活跃度加成的最大值
            return Math.min(activityScore, 100.0);
        } catch (Exception e) {
            log.error("计算活跃度加成失败: tagId={}", tagId, e);
            return 0.0;
        }
    }

    /**
     * 计算时间衰减因子
     * 
     * @param tag 标签实体
     * @return 时间衰减因子（0.5-1.2）
     */
    private double calculateTimeDecayFactor(Tag tag) {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime createTime = tag.getCreateTime();
            
            long daysSinceCreation = java.time.Duration.between(createTime, now).toDays();
            
            if (daysSinceCreation <= 7) {
                // 新标签（7天内）：热度加成
                return 1.2;
            } else if (daysSinceCreation <= 30) {
                // 较新标签（30天内）：正常热度
                return 1.0;
            } else if (daysSinceCreation <= 90) {
                // 中等年龄标签（90天内）：轻微衰减
                return 0.9;
            } else if (daysSinceCreation <= 365) {
                // 较老标签（1年内）：中等衰减
                return 0.8;
            } else {
                // 老标签（1年以上）：较强衰减
                return 0.7;
            }
        } catch (Exception e) {
            log.error("计算时间衰减因子失败: tagId={}", tag.getId(), e);
            return 1.0; // 默认无衰减
        }
    }

    /**
     * 计算社交网络效应
     * 
     * @param tagId 标签ID
     * @return 社交网络效应分数
     */
    private double calculateSocialNetworkEffect(Long tagId) {
        try {
            // 获取标签的关注者
            List<Long> followers = userTagService.getTagFollowers(tagId, 100);
            
            if (followers.isEmpty()) {
                return 0.0;
            }
            
            double socialScore = 0.0;
            
            // 计算关注者的"影响力"（基于他们关注的其他标签数量）
            for (Long userId : followers) {
                Integer userTagCount = userTagService.getUserFollowedTagCount(userId);
                
                // 关注标签较多的用户被认为是更活跃的用户，其关注有更高的价值
                if (userTagCount > 5) {
                    socialScore += 1.5; // 活跃用户加成
                } else if (userTagCount > 2) {
                    socialScore += 1.0; // 普通用户
                } else {
                    socialScore += 0.5; // 新用户或不活跃用户
                }
            }
            
            // 平均化并限制最大值
            double averageSocialScore = socialScore / followers.size();
            return Math.min(averageSocialScore * 10, 50.0); // 最大加成50分
        } catch (Exception e) {
            log.error("计算社交网络效应失败: tagId={}", tagId, e);
            return 0.0;
        }
    }

    /**
     * 手动触发标签热度更新（用于测试或管理员操作）
     * 
     * @param tagId 标签ID，为null时更新所有标签
     * @return 更新的标签数量
     */
    public int manualUpdateTagHotness(Long tagId) {
        try {
            log.info("手动触发标签热度更新: tagId={}", tagId);
            
            if (tagId != null) {
                Tag tag = tagService.getTagById(tagId);
                if (tag != null && updateTagHotnessValue(tag)) {
                    log.info("手动更新标签热度成功: tagId={}", tagId);
                    return 1;
                } else {
                    log.warn("手动更新标签热度失败: tagId={}", tagId);
                    return 0;
                }
            } else {
                // 更新所有标签
                return tagService.updateAllTagHotness();
            }
        } catch (Exception e) {
            log.error("手动更新标签热度失败: tagId={}", tagId, e);
            return 0;
        }
    }

    /**
     * 获取标签热度更新统计信息
     * 
     * @return 统计信息
     */
    public String getHotnessUpdateStatistics() {
        try {
            Long totalTags = tagService.countActiveTags();
            Double averageHotness = tagService.getAverageHotness();
            
            return String.format("标签热度统计 - 总标签数: %d, 平均热度: %.2f", 
                    totalTags, averageHotness);
        } catch (Exception e) {
            log.error("获取标签热度统计信息失败", e);
            return "统计信息获取失败";
        }
    }
}
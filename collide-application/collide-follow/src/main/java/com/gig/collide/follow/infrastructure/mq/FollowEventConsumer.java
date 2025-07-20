package com.gig.collide.follow.infrastructure.mq;

import com.alibaba.fastjson.JSON;
import com.gig.collide.follow.domain.event.FollowEvent;
import com.gig.collide.follow.infrastructure.mapper.FollowStatisticsMapper;
import com.gig.collide.stream.consumer.AbstractStreamConsumer;
import com.gig.collide.stream.param.MessageBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

/**
 * 关注事件消费者
 * @author GIG
 */
@Slf4j
public class FollowEventConsumer extends AbstractStreamConsumer {

    private FollowStatisticsMapper followStatisticsMapper;
    private FollowCacheService followCacheService;

    public FollowEventConsumer(FollowStatisticsMapper followStatisticsMapper, 
                              FollowCacheService followCacheService) {
        this.followStatisticsMapper = followStatisticsMapper;
        this.followCacheService = followCacheService;
    }

    /**
     * 关注事件消费者
     */
    @Bean
    public Consumer<Message<MessageBody>> followEventConsumer() {
        return message -> {
            try {
                FollowEvent event = getMessage(message, FollowEvent.class);
                log.info("接收到关注事件: eventType={}, eventId={}, follower={}, followed={}", 
                        event.getEventType(), event.getEventId(), 
                        event.getFollowerUserId(), event.getFollowedUserId());

                // 处理事件
                processFollowEvent(event);

            } catch (Exception e) {
                log.error("处理关注事件失败: error={}", e.getMessage(), e);
                throw e; // 重新抛出异常，触发重试机制
            }
        };
    }

    /**
     * 处理关注事件
     */
    private void processFollowEvent(FollowEvent event) {
        switch (event.getEventType()) {
            case FOLLOW:
                handleFollowEvent(event);
                break;
            case UNFOLLOW:
                handleUnfollowEvent(event);
                break;
            case STATISTICS_UPDATE:
                handleStatisticsUpdateEvent(event);
                break;
            default:
                log.warn("未知的关注事件类型: {}", event.getEventType());
        }
    }

    /**
     * 处理关注事件
     */
    private void handleFollowEvent(FollowEvent event) {
        Long followerUserId = event.getFollowerUserId();
        Long followedUserId = event.getFollowedUserId();

        try {
            // 1. 异步更新统计信息
            updateFollowStatistics(followerUserId, followedUserId, true);

            // 2. 更新缓存
            followCacheService.updateFollowStatusCache(followerUserId, followedUserId, true);
            followCacheService.invalidateUserStatisticsCache(followerUserId);
            followCacheService.invalidateUserStatisticsCache(followedUserId);

            // 3. 可以在这里添加其他异步处理
            // 例如：推送通知、更新用户推荐等

            log.info("关注事件处理完成: follower={}, followed={}", followerUserId, followedUserId);

        } catch (Exception e) {
            log.error("处理关注事件失败: follower={}, followed={}, error={}", 
                    followerUserId, followedUserId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 处理取消关注事件
     */
    private void handleUnfollowEvent(FollowEvent event) {
        Long followerUserId = event.getFollowerUserId();
        Long followedUserId = event.getFollowedUserId();

        try {
            // 1. 异步更新统计信息
            updateFollowStatistics(followerUserId, followedUserId, false);

            // 2. 更新缓存
            followCacheService.updateFollowStatusCache(followerUserId, followedUserId, false);
            followCacheService.invalidateUserStatisticsCache(followerUserId);
            followCacheService.invalidateUserStatisticsCache(followedUserId);

            log.info("取消关注事件处理完成: follower={}, followed={}", followerUserId, followedUserId);

        } catch (Exception e) {
            log.error("处理取消关注事件失败: follower={}, followed={}, error={}", 
                    followerUserId, followedUserId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 处理统计更新事件
     */
    private void handleStatisticsUpdateEvent(FollowEvent event) {
        // 这里可以处理一些定时的统计任务
        // 例如：重新计算热门用户、推荐算法数据更新等
        log.info("统计更新事件处理: eventId={}", event.getEventId());
    }

    /**
     * 更新关注统计
     */
    private void updateFollowStatistics(Long followerUserId, Long followedUserId, boolean isFollow) {
        try {
            // 初始化统计记录（如果不存在）
            initStatisticsIfNotExists(followerUserId);
            initStatisticsIfNotExists(followedUserId);
            
            if (isFollow) {
                // 关注：增加关注者的关注数，增加被关注者的粉丝数
                followStatisticsMapper.incrementFollowingCount(followerUserId);
                followStatisticsMapper.incrementFollowerCount(followedUserId);
            } else {
                // 取消关注：减少关注者的关注数，减少被关注者的粉丝数
                followStatisticsMapper.decrementFollowingCount(followerUserId);
                followStatisticsMapper.decrementFollowerCount(followedUserId);
            }
            
            log.info("统计信息更新完成: follower={}, followed={}, isFollow={}", 
                    followerUserId, followedUserId, isFollow);
                    
        } catch (Exception e) {
            log.error("更新关注统计失败: follower={}, followed={}, isFollow={}, error={}", 
                    followerUserId, followedUserId, isFollow, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 初始化用户统计记录
     */
    private void initStatisticsIfNotExists(Long userId) {
        try {
            followStatisticsMapper.initUserStatistics(userId);
        } catch (Exception e) {
            // 忽略重复插入异常
            log.debug("初始化用户统计记录: userId={}, error={}", userId, e.getMessage());
        }
    }
} 
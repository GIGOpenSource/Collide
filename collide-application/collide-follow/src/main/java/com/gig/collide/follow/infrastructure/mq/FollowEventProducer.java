package com.gig.collide.follow.infrastructure.mq;

import com.alibaba.fastjson.JSON;
import com.gig.collide.follow.domain.event.FollowEvent;
import com.gig.collide.stream.producer.StreamProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 关注事件生产者
 * @author GIG
 */
@Slf4j
@Component
@ConditionalOnClass(StreamProducer.class)
public class FollowEventProducer {

    @Autowired(required = false)
    private StreamProducer streamProducer;

    /**
     * MQ Binding 名称
     */
    private static final String FOLLOW_BINDING_NAME = "follow-event-out-0";

    /**
     * 发送关注事件
     */
    public void sendFollowEvent(FollowEvent event) {
        if (streamProducer == null) {
            log.warn("StreamProducer 未初始化，跳过MQ消息发送: eventType={}, follower={}, followed={}", 
                    event.getEventType(), event.getFollowerUserId(), event.getFollowedUserId());
            return;
        }
        
        try {
            String tag = event.getEventType().name();
            String message = JSON.toJSONString(event);
            
            log.info("发送关注事件: eventType={}, follower={}, followed={}", 
                    event.getEventType(), event.getFollowerUserId(), event.getFollowedUserId());
            
            boolean success = streamProducer.send(FOLLOW_BINDING_NAME, tag, message);
            
            if (success) {
                log.info("关注事件发送成功: eventId={}", event.getEventId());
            } else {
                log.error("关注事件发送失败: eventId={}", event.getEventId());
            }
        } catch (Exception e) {
            log.error("发送关注事件异常: eventId={}, error={}", event.getEventId(), e.getMessage(), e);
        }
    }

    /**
     * 发送延迟关注事件（用于统计更新）
     */
    public void sendDelayedFollowEvent(FollowEvent event, int delayLevel) {
        if (streamProducer == null) {
            log.warn("StreamProducer 未初始化，跳过延迟MQ消息发送: eventType={}, delayLevel={}", 
                    event.getEventType(), delayLevel);
            return;
        }
        
        try {
            String tag = event.getEventType().name();
            String message = JSON.toJSONString(event);
            
            log.info("发送延迟关注事件: eventType={}, delayLevel={}, follower={}, followed={}", 
                    event.getEventType(), delayLevel, event.getFollowerUserId(), event.getFollowedUserId());
            
            boolean success = streamProducer.send(FOLLOW_BINDING_NAME, tag, message, delayLevel);
            
            if (success) {
                log.info("延迟关注事件发送成功: eventId={}", event.getEventId());
            } else {
                log.error("延迟关注事件发送失败: eventId={}", event.getEventId());
            }
        } catch (Exception e) {
            log.error("发送延迟关注事件异常: eventId={}, error={}", event.getEventId(), e.getMessage(), e);
        }
    }

    /**
     * 创建关注事件
     */
    public FollowEvent createFollowEvent(FollowEvent.FollowEventType eventType, 
                                        Long followerUserId, 
                                        Long followedUserId) {
        return new FollowEvent()
                .setEventType(eventType)
                .setFollowerUserId(followerUserId)
                .setFollowedUserId(followedUserId)
                .setEventTime(new java.util.Date())
                .setEventId(UUID.randomUUID().toString());
    }
} 
package com.gig.collide.social.domain.service;

import com.gig.collide.mq.producer.StreamProducer;
import com.alibaba.fastjson2.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 社交事件服务
 * 
 * <p>负责发布社交相关的业务事件到消息队列，确保数据的最终一致性</p>
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SocialEventService {
    
    private final StreamProducer streamProducer;
    
    // 消息绑定名称常量（对应 Spring Cloud Stream 绑定）
    private static final String STATISTICS_CHANGED_BINDING = "statistics-changed-out-0";
    private static final String USER_INFO_CHANGED_BINDING = "user-info-changed-out-0";
    private static final String SOCIAL_POST_PUBLISHED_BINDING = "social-post-published-out-0";
    private static final String SOCIAL_POST_DELETED_BINDING = "social-post-deleted-out-0";
    
    /**
     * 发布统计数据变更事件
     *
     * @param targetId 目标ID（动态ID）
     * @param targetType 目标类型（SOCIAL_POST）
     * @param statisticsType 统计类型（LIKE、COMMENT、SHARE、VIEW、FAVORITE）
     * @param delta 变化量（+1、-1）
     */
    public void publishStatisticsChanged(Long targetId, String targetType, String statisticsType, Integer delta) {
        try {
            log.info("发布统计变更事件，目标ID：{}，类型：{}，统计：{}，变化：{}", 
                targetId, targetType, statisticsType, delta);
            
            StatisticsChangedEvent event = StatisticsChangedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .targetId(targetId)
                .targetType(targetType)
                .statisticsType(statisticsType)
                .delta(delta)
                .eventTime(LocalDateTime.now())
                .eventSource("social-service")
                .build();
            
            // 使用标准化 StreamProducer 发送消息
            String messageBody = JSON.toJSONString(event);
            String tag = String.format("%s_%s", targetType, statisticsType);
            
            boolean result = streamProducer.send(STATISTICS_CHANGED_BINDING, tag, messageBody);
            if (result) {
                log.info("统计变更事件发送成功，目标ID：{}，标签：{}", targetId, tag);
            } else {
                log.error("统计变更事件发送失败，目标ID：{}", targetId);
            }
            
        } catch (Exception e) {
            log.error("发布统计变更事件失败，目标ID：{}", targetId, e);
        }
    }
    
    /**
     * 发布动态发布事件
     *
     * @param postId 动态ID
     * @param authorId 作者ID
     * @param postType 动态类型
     * @param content 动态内容
     */
    public void publishSocialPostPublished(Long postId, Long authorId, String postType, String content) {
        try {
            log.info("发布动态发布事件，动态ID：{}，作者ID：{}", postId, authorId);
            
            SocialPostPublishedEvent event = SocialPostPublishedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .postId(postId)
                .authorId(authorId)
                .postType(postType)
                .content(content)
                .publishTime(LocalDateTime.now())
                .eventTime(LocalDateTime.now())
                .eventSource("social-service")
                .build();
            
            // 使用标准化 StreamProducer 发送消息
            String messageBody = JSON.toJSONString(event);
            String tag = String.format("PUBLISHED_%s", postType);
            
            boolean result = streamProducer.send(SOCIAL_POST_PUBLISHED_BINDING, tag, messageBody);
            if (result) {
                log.info("动态发布事件发送成功，动态ID：{}，标签：{}", postId, tag);
            } else {
                log.error("动态发布事件发送失败，动态ID：{}", postId);
            }
            
        } catch (Exception e) {
            log.error("发布动态发布事件失败，动态ID：{}", postId, e);
        }
    }
    
    /**
     * 发布动态删除事件
     *
     * @param postId 动态ID
     * @param authorId 作者ID
     */
    public void publishSocialPostDeleted(Long postId, Long authorId) {
        try {
            log.info("发布动态删除事件，动态ID：{}，作者ID：{}", postId, authorId);
            
            SocialPostDeletedEvent event = SocialPostDeletedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .postId(postId)
                .authorId(authorId)
                .deleteTime(LocalDateTime.now())
                .eventTime(LocalDateTime.now())
                .eventSource("social-service")
                .build();
            
            // 使用标准化 StreamProducer 发送消息
            String messageBody = JSON.toJSONString(event);
            String tag = "DELETED";
            
            boolean result = streamProducer.send(SOCIAL_POST_DELETED_BINDING, tag, messageBody);
            if (result) {
                log.info("动态删除事件发送成功，动态ID：{}，标签：{}", postId, tag);
            } else {
                log.error("动态删除事件发送失败，动态ID：{}", postId);
            }
            
        } catch (Exception e) {
            log.error("发布动态删除事件失败，动态ID：{}", postId, e);
        }
    }
    
    /**
     * 发布用户信息变更事件（当检测到用户信息变更时）
     *
     * @param userId 用户ID
     * @param username 用户名
     * @param nickname 昵称
     * @param avatar 头像
     * @param verified 认证状态
     */
    public void publishUserInfoChanged(Long userId, String username, String nickname, String avatar, Boolean verified) {
        try {
            log.info("发布用户信息变更事件，用户ID：{}", userId);
            
            UserInfoChangedEvent event = UserInfoChangedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .userId(userId)
                .username(username)
                .nickname(nickname)
                .avatar(avatar)
                .verified(verified)
                .eventTime(LocalDateTime.now())
                .eventSource("social-service")
                .build();
            
            // 使用标准化 StreamProducer 发送消息
            String messageBody = JSON.toJSONString(event);
            String tag = "USER_INFO_CHANGED";
            
            boolean result = streamProducer.send(USER_INFO_CHANGED_BINDING, tag, messageBody);
            if (result) {
                log.info("用户信息变更事件发送成功，用户ID：{}，标签：{}", userId, tag);
            } else {
                log.error("用户信息变更事件发送失败，用户ID：{}", userId);
            }
            
        } catch (Exception e) {
            log.error("发布用户信息变更事件失败，用户ID：{}", userId, e);
        }
    }
    
    // ===== 事件数据类 =====
    
    /**
     * 统计变更事件
     */
    public static class StatisticsChangedEvent {
        private String eventId;
        private Long targetId;
        private String targetType;
        private String statisticsType;
        private Integer delta;
        private LocalDateTime eventTime;
        private String eventSource;
        private Map<String, Object> extraData;
        
        public static StatisticsChangedEventBuilder builder() {
            return new StatisticsChangedEventBuilder();
        }
        
        // Getters and Setters
        public String getEventId() { return eventId; }
        public void setEventId(String eventId) { this.eventId = eventId; }
        
        public Long getTargetId() { return targetId; }
        public void setTargetId(Long targetId) { this.targetId = targetId; }
        
        public String getTargetType() { return targetType; }
        public void setTargetType(String targetType) { this.targetType = targetType; }
        
        public String getStatisticsType() { return statisticsType; }
        public void setStatisticsType(String statisticsType) { this.statisticsType = statisticsType; }
        
        public Integer getDelta() { return delta; }
        public void setDelta(Integer delta) { this.delta = delta; }
        
        public LocalDateTime getEventTime() { return eventTime; }
        public void setEventTime(LocalDateTime eventTime) { this.eventTime = eventTime; }
        
        public String getEventSource() { return eventSource; }
        public void setEventSource(String eventSource) { this.eventSource = eventSource; }
        
        public Map<String, Object> getExtraData() { return extraData; }
        public void setExtraData(Map<String, Object> extraData) { this.extraData = extraData; }
        
        public static class StatisticsChangedEventBuilder {
            private String eventId;
            private Long targetId;
            private String targetType;
            private String statisticsType;
            private Integer delta;
            private LocalDateTime eventTime;
            private String eventSource;
            private Map<String, Object> extraData = new HashMap<>();
            
            public StatisticsChangedEventBuilder eventId(String eventId) {
                this.eventId = eventId;
                return this;
            }
            
            public StatisticsChangedEventBuilder targetId(Long targetId) {
                this.targetId = targetId;
                return this;
            }
            
            public StatisticsChangedEventBuilder targetType(String targetType) {
                this.targetType = targetType;
                return this;
            }
            
            public StatisticsChangedEventBuilder statisticsType(String statisticsType) {
                this.statisticsType = statisticsType;
                return this;
            }
            
            public StatisticsChangedEventBuilder delta(Integer delta) {
                this.delta = delta;
                return this;
            }
            
            public StatisticsChangedEventBuilder eventTime(LocalDateTime eventTime) {
                this.eventTime = eventTime;
                return this;
            }
            
            public StatisticsChangedEventBuilder eventSource(String eventSource) {
                this.eventSource = eventSource;
                return this;
            }
            
            public StatisticsChangedEventBuilder extraData(Map<String, Object> extraData) {
                this.extraData = extraData;
                return this;
            }
            
            public StatisticsChangedEvent build() {
                StatisticsChangedEvent event = new StatisticsChangedEvent();
                event.setEventId(this.eventId);
                event.setTargetId(this.targetId);
                event.setTargetType(this.targetType);
                event.setStatisticsType(this.statisticsType);
                event.setDelta(this.delta);
                event.setEventTime(this.eventTime);
                event.setEventSource(this.eventSource);
                event.setExtraData(this.extraData);
                return event;
            }
        }
    }
    
    /**
     * 动态发布事件
     */
    public static class SocialPostPublishedEvent {
        private String eventId;
        private Long postId;
        private Long authorId;
        private String postType;
        private String content;
        private LocalDateTime publishTime;
        private LocalDateTime eventTime;
        private String eventSource;
        
        public static SocialPostPublishedEventBuilder builder() {
            return new SocialPostPublishedEventBuilder();
        }
        
        // Getters and Setters
        public String getEventId() { return eventId; }
        public void setEventId(String eventId) { this.eventId = eventId; }
        
        public Long getPostId() { return postId; }
        public void setPostId(Long postId) { this.postId = postId; }
        
        public Long getAuthorId() { return authorId; }
        public void setAuthorId(Long authorId) { this.authorId = authorId; }
        
        public String getPostType() { return postType; }
        public void setPostType(String postType) { this.postType = postType; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        
        public LocalDateTime getPublishTime() { return publishTime; }
        public void setPublishTime(LocalDateTime publishTime) { this.publishTime = publishTime; }
        
        public LocalDateTime getEventTime() { return eventTime; }
        public void setEventTime(LocalDateTime eventTime) { this.eventTime = eventTime; }
        
        public String getEventSource() { return eventSource; }
        public void setEventSource(String eventSource) { this.eventSource = eventSource; }
        
        public static class SocialPostPublishedEventBuilder {
            private String eventId;
            private Long postId;
            private Long authorId;
            private String postType;
            private String content;
            private LocalDateTime publishTime;
            private LocalDateTime eventTime;
            private String eventSource;
            
            public SocialPostPublishedEventBuilder eventId(String eventId) {
                this.eventId = eventId;
                return this;
            }
            
            public SocialPostPublishedEventBuilder postId(Long postId) {
                this.postId = postId;
                return this;
            }
            
            public SocialPostPublishedEventBuilder authorId(Long authorId) {
                this.authorId = authorId;
                return this;
            }
            
            public SocialPostPublishedEventBuilder postType(String postType) {
                this.postType = postType;
                return this;
            }
            
            public SocialPostPublishedEventBuilder content(String content) {
                this.content = content;
                return this;
            }
            
            public SocialPostPublishedEventBuilder publishTime(LocalDateTime publishTime) {
                this.publishTime = publishTime;
                return this;
            }
            
            public SocialPostPublishedEventBuilder eventTime(LocalDateTime eventTime) {
                this.eventTime = eventTime;
                return this;
            }
            
            public SocialPostPublishedEventBuilder eventSource(String eventSource) {
                this.eventSource = eventSource;
                return this;
            }
            
            public SocialPostPublishedEvent build() {
                SocialPostPublishedEvent event = new SocialPostPublishedEvent();
                event.setEventId(this.eventId);
                event.setPostId(this.postId);
                event.setAuthorId(this.authorId);
                event.setPostType(this.postType);
                event.setContent(this.content);
                event.setPublishTime(this.publishTime);
                event.setEventTime(this.eventTime);
                event.setEventSource(this.eventSource);
                return event;
            }
        }
    }
    
    /**
     * 动态删除事件
     */
    public static class SocialPostDeletedEvent {
        private String eventId;
        private Long postId;
        private Long authorId;
        private LocalDateTime deleteTime;
        private LocalDateTime eventTime;
        private String eventSource;
        
        public static SocialPostDeletedEventBuilder builder() {
            return new SocialPostDeletedEventBuilder();
        }
        
        // Getters and Setters
        public String getEventId() { return eventId; }
        public void setEventId(String eventId) { this.eventId = eventId; }
        
        public Long getPostId() { return postId; }
        public void setPostId(Long postId) { this.postId = postId; }
        
        public Long getAuthorId() { return authorId; }
        public void setAuthorId(Long authorId) { this.authorId = authorId; }
        
        public LocalDateTime getDeleteTime() { return deleteTime; }
        public void setDeleteTime(LocalDateTime deleteTime) { this.deleteTime = deleteTime; }
        
        public LocalDateTime getEventTime() { return eventTime; }
        public void setEventTime(LocalDateTime eventTime) { this.eventTime = eventTime; }
        
        public String getEventSource() { return eventSource; }
        public void setEventSource(String eventSource) { this.eventSource = eventSource; }
        
        public static class SocialPostDeletedEventBuilder {
            private String eventId;
            private Long postId;
            private Long authorId;
            private LocalDateTime deleteTime;
            private LocalDateTime eventTime;
            private String eventSource;
            
            public SocialPostDeletedEventBuilder eventId(String eventId) {
                this.eventId = eventId;
                return this;
            }
            
            public SocialPostDeletedEventBuilder postId(Long postId) {
                this.postId = postId;
                return this;
            }
            
            public SocialPostDeletedEventBuilder authorId(Long authorId) {
                this.authorId = authorId;
                return this;
            }
            
            public SocialPostDeletedEventBuilder deleteTime(LocalDateTime deleteTime) {
                this.deleteTime = deleteTime;
                return this;
            }
            
            public SocialPostDeletedEventBuilder eventTime(LocalDateTime eventTime) {
                this.eventTime = eventTime;
                return this;
            }
            
            public SocialPostDeletedEventBuilder eventSource(String eventSource) {
                this.eventSource = eventSource;
                return this;
            }
            
            public SocialPostDeletedEvent build() {
                SocialPostDeletedEvent event = new SocialPostDeletedEvent();
                event.setEventId(this.eventId);
                event.setPostId(this.postId);
                event.setAuthorId(this.authorId);
                event.setDeleteTime(this.deleteTime);
                event.setEventTime(this.eventTime);
                event.setEventSource(this.eventSource);
                return event;
            }
        }
    }
    
    /**
     * 用户信息变更事件
     */
    public static class UserInfoChangedEvent {
        private String eventId;
        private Long userId;
        private String username;
        private String nickname;
        private String avatar;
        private Boolean verified;
        private LocalDateTime eventTime;
        private String eventSource;
        
        public static UserInfoChangedEventBuilder builder() {
            return new UserInfoChangedEventBuilder();
        }
        
        // Getters and Setters
        public String getEventId() { return eventId; }
        public void setEventId(String eventId) { this.eventId = eventId; }
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        
        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
        
        public Boolean getVerified() { return verified; }
        public void setVerified(Boolean verified) { this.verified = verified; }
        
        public LocalDateTime getEventTime() { return eventTime; }
        public void setEventTime(LocalDateTime eventTime) { this.eventTime = eventTime; }
        
        public String getEventSource() { return eventSource; }
        public void setEventSource(String eventSource) { this.eventSource = eventSource; }
        
        public static class UserInfoChangedEventBuilder {
            private String eventId;
            private Long userId;
            private String username;
            private String nickname;
            private String avatar;
            private Boolean verified;
            private LocalDateTime eventTime;
            private String eventSource;
            
            public UserInfoChangedEventBuilder eventId(String eventId) {
                this.eventId = eventId;
                return this;
            }
            
            public UserInfoChangedEventBuilder userId(Long userId) {
                this.userId = userId;
                return this;
            }
            
            public UserInfoChangedEventBuilder username(String username) {
                this.username = username;
                return this;
            }
            
            public UserInfoChangedEventBuilder nickname(String nickname) {
                this.nickname = nickname;
                return this;
            }
            
            public UserInfoChangedEventBuilder avatar(String avatar) {
                this.avatar = avatar;
                return this;
            }
            
            public UserInfoChangedEventBuilder verified(Boolean verified) {
                this.verified = verified;
                return this;
            }
            
            public UserInfoChangedEventBuilder eventTime(LocalDateTime eventTime) {
                this.eventTime = eventTime;
                return this;
            }
            
            public UserInfoChangedEventBuilder eventSource(String eventSource) {
                this.eventSource = eventSource;
                return this;
            }
            
            public UserInfoChangedEvent build() {
                UserInfoChangedEvent event = new UserInfoChangedEvent();
                event.setEventId(this.eventId);
                event.setUserId(this.userId);
                event.setUsername(this.username);
                event.setNickname(this.nickname);
                event.setAvatar(this.avatar);
                event.setVerified(this.verified);
                event.setEventTime(this.eventTime);
                event.setEventSource(this.eventSource);
                return event;
            }
        }
    }
} 
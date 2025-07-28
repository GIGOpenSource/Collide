package com.gig.collide.social.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.api.social.constant.InteractionTypeEnum;
import com.gig.collide.api.social.request.SocialInteractionRequest;
import com.gig.collide.api.social.request.SocialInteractionQueryRequest;
import com.gig.collide.api.social.response.data.SocialInteractionInfo;
import com.gig.collide.api.user.request.UserUnifiedQueryRequest;
import com.gig.collide.api.user.service.UserFacadeService;
import com.gig.collide.base.exception.BizException;
import com.gig.collide.base.exception.CommonErrorCode;
import com.gig.collide.cache.constant.CacheConstant;
import com.gig.collide.social.domain.entity.SocialPostInteraction;
import com.gig.collide.social.domain.entity.convertor.SocialPostConvertor;
import com.gig.collide.social.domain.service.SocialInteractionService;
import com.gig.collide.social.infrastructure.mapper.SocialPostInteractionMapper;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 社交互动服务实现
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SocialInteractionServiceImpl implements SocialInteractionService {

    @DubboReference(version = "1.0.0")
    private UserFacadeService userFacadeService;

    private final SocialPostInteractionMapper interactionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = CacheConstant.SOCIAL_INTERACTION_CACHE, key = "#request.postId + ':' + #request.userId")
    @CacheInvalidate(name = CacheConstant.SOCIAL_USER_INTERACTION_STATUS_CACHE, key = "#request.userId + ':' + #request.postId")
    @CacheInvalidate(name = CacheConstant.SOCIAL_POST_INTERACTION_USERS_CACHE, key = "#request.postId + ':' + #request.interactionType")
    public SocialPostInteraction executeInteraction(SocialInteractionRequest request) {
        log.info("执行社交互动，请求参数：{}", request);
        
        // 参数验证
        if (request == null || request.getPostId() == null || request.getUserId() == null) {
            throw new BizException("互动参数不能为空", CommonErrorCode.PARAM_INVALID);
        }
        
        // 检查是否已存在相同互动
        SocialPostInteraction existingInteraction = getUserInteraction(
            request.getPostId(), request.getUserId(), request.getInteractionType().name());
        
        if (existingInteraction != null) {
            log.warn("用户已对此动态执行过相同互动，动态ID：{}，用户ID：{}，互动类型：{}", 
                request.getPostId(), request.getUserId(), request.getInteractionType());
            return existingInteraction;
        }
        
        // 创建新的互动记录
        SocialPostInteraction interaction = new SocialPostInteraction();
        interaction.setPostId(request.getPostId());
        interaction.setUserId(request.getUserId());
        interaction.setUserNickname(null);
        interaction.setUserAvatar(null);
        interaction.setInteractionType(request.getInteractionType().name());
        interaction.setDeviceInfo(request.getDeviceInfo());
        interaction.setIpAddress(request.getIpAddress());
        // BaseEntity的gmtCreate会自动填充，无需手动设置
        
        // 插入数据库
        int result = interactionMapper.insert(interaction);
        if (result != 1) {
            throw new BizException("执行互动失败", CommonErrorCode.SYSTEM_ERROR);
        }
        
        log.info("执行社交互动成功，互动ID：{}", interaction.getId());
        return interaction;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = CacheConstant.SOCIAL_INTERACTION_CACHE, key = "#postId + ':' + #userId")
    @CacheInvalidate(name = CacheConstant.SOCIAL_USER_INTERACTION_STATUS_CACHE, key = "#userId + ':' + #postId")
    @CacheInvalidate(name = CacheConstant.SOCIAL_POST_INTERACTION_USERS_CACHE, key = "#postId + ':' + #interactionType")
    public boolean cancelInteraction(Long postId, Long userId, String interactionType) {
        log.info("取消社交互动，动态ID：{}，用户ID：{}，类型：{}", postId, userId, interactionType);
        
        // 查找现有互动记录
        SocialPostInteraction existingInteraction = getUserInteraction(postId, userId, interactionType);
        if (existingInteraction == null) {
            log.warn("未找到对应的互动记录，动态ID：{}，用户ID：{}，类型：{}", postId, userId, interactionType);
            return false;
        }
        
        // 删除互动记录
        int result = interactionMapper.deleteById(existingInteraction.getId());
        
        log.info("取消社交互动{}，动态ID：{}，用户ID：{}", result > 0 ? "成功" : "失败", postId, userId);
        return result > 0;
    }

    @Override
    @Cached(name = CacheConstant.SOCIAL_USER_INTERACTION_STATUS_CACHE, 
            key = "#userId + ':' + #postId + ':' + #interactionType", 
            expire = CacheConstant.SOCIAL_INTERACTION_CACHE_EXPIRE,
            cacheType = CacheType.BOTH)
    public SocialPostInteraction getUserInteraction(Long postId, Long userId, String interactionType) {
        if (postId == null || userId == null || !StringUtils.hasText(interactionType)) {
            return null;
        }
        
        return interactionMapper.selectOne(
            new LambdaQueryWrapper<SocialPostInteraction>()
                .eq(SocialPostInteraction::getPostId, postId)
                .eq(SocialPostInteraction::getUserId, userId)
                .eq(SocialPostInteraction::getInteractionType, interactionType)
                .orderByDesc(SocialPostInteraction::getGmtCreate)
                .last("LIMIT 1")
        );
    }

    @Override
    @Cached(name = CacheConstant.SOCIAL_POST_INTERACTION_USERS_CACHE, 
            key = "#request.postId + ':' + #request.interactionType + ':' + #request.pageNum + ':' + #request.pageSize", 
            expire = CacheConstant.SOCIAL_INTERACTION_CACHE_EXPIRE,
            cacheType = CacheType.BOTH)
    public IPage<SocialInteractionInfo> getPostInteractions(SocialInteractionQueryRequest request) {
        log.debug("查询动态互动记录，request：{}", request);
        
        Page<SocialPostInteraction> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<SocialPostInteraction> wrapper = new LambdaQueryWrapper<SocialPostInteraction>();
        
        // TODO: 这里需要解析request.getCondition()来构建查询条件
        // 现在先简化处理
        wrapper.eq(SocialPostInteraction::getDeleted, 0);
        
        wrapper.orderByDesc(SocialPostInteraction::getGmtCreate);
        
        IPage<SocialPostInteraction> result = interactionMapper.selectPage(page, wrapper);
        
        // 转换为SocialInteractionInfo
        List<SocialInteractionInfo> infoList = result.getRecords()
            .stream()
            .map(SocialPostConvertor.INSTANCE::interactionToInfo)
            .collect(Collectors.toList());
        
        Page<SocialInteractionInfo> infoPage = new Page<>(request.getPageNum(), request.getPageSize(), result.getTotal());
        infoPage.setRecords(infoList);
        
        return infoPage;
    }

    @Override
    public IPage<SocialInteractionInfo> getUserInteractionHistory(SocialInteractionQueryRequest request) {
        log.debug("查询用户互动历史，request：{}", request);
        
        Page<SocialPostInteraction> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<SocialPostInteraction> wrapper = new LambdaQueryWrapper<SocialPostInteraction>();
        
        // TODO: 这里需要解析request.getCondition()来构建查询条件
        // 现在先简化处理
        wrapper.eq(SocialPostInteraction::getDeleted, 0);
        
        wrapper.orderByDesc(SocialPostInteraction::getGmtCreate);
        
        IPage<SocialPostInteraction> result = interactionMapper.selectPage(page, wrapper);
        
        List<SocialInteractionInfo> infoList = result.getRecords()
            .stream()
            .map(SocialPostConvertor.INSTANCE::interactionToInfo)
            .collect(Collectors.toList());
        
        Page<SocialInteractionInfo> infoPage = new Page<>(request.getPageNum(), request.getPageSize(), result.getTotal());
        infoPage.setRecords(infoList);
        
        return infoPage;
    }

    @Override
    public List<SocialPostInteraction> batchGetUserInteractions(List<Long> postIds, Long userId, String interactionType) {
        if (postIds == null || postIds.isEmpty() || userId == null) {
            return List.of();
        }
        
        LambdaQueryWrapper<SocialPostInteraction> wrapper = new LambdaQueryWrapper<SocialPostInteraction>()
            .in(SocialPostInteraction::getPostId, postIds)
            .eq(SocialPostInteraction::getUserId, userId);
        
        if (StringUtils.hasText(interactionType)) {
            wrapper.eq(SocialPostInteraction::getInteractionType, interactionType);
        }
        
        return interactionMapper.selectList(wrapper);
    }

    @Override
    @Cached(name = CacheConstant.SOCIAL_STATISTICS_CACHE, 
            key = "'count:' + #postId + ':' + #interactionType", 
            expire = CacheConstant.SOCIAL_STATISTICS_CACHE_EXPIRE,
            cacheType = CacheType.BOTH)
    public Long countPostInteractions(Long postId, String interactionType) {
        LambdaQueryWrapper<SocialPostInteraction> wrapper = new LambdaQueryWrapper<SocialPostInteraction>()
            .eq(SocialPostInteraction::getPostId, postId);
        
        if (StringUtils.hasText(interactionType)) {
            wrapper.eq(SocialPostInteraction::getInteractionType, interactionType);
        }
        
        return interactionMapper.selectCount(wrapper);
    }

    @Override
    public Long countUserInteractions(Long userId, String interactionType, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<SocialPostInteraction> wrapper = new LambdaQueryWrapper<SocialPostInteraction>()
            .eq(SocialPostInteraction::getUserId, userId);
        
        if (StringUtils.hasText(interactionType)) {
            wrapper.eq(SocialPostInteraction::getInteractionType, interactionType);
        }
        
        if (startTime != null && endTime != null) {
            wrapper.between(SocialPostInteraction::getGmtCreate, java.sql.Timestamp.valueOf(startTime), java.sql.Timestamp.valueOf(endTime));
        }
        
        return interactionMapper.selectCount(wrapper);
    }

    @Override
    @Cached(name = CacheConstant.SOCIAL_POST_INTERACTION_USERS_CACHE, 
            key = "'likes:' + #postId + ':' + #pageNum + ':' + #pageSize", 
            expire = CacheConstant.SOCIAL_INTERACTION_CACHE_EXPIRE,
            cacheType = CacheType.BOTH)
    public IPage<SocialInteractionInfo> getPostLikeUsers(Long postId, Integer pageNum, Integer pageSize) {
        return getPostInteractionUsers(postId, InteractionTypeEnum.LIKE.name(), pageNum, pageSize);
    }

    @Override
    @Cached(name = CacheConstant.SOCIAL_POST_INTERACTION_USERS_CACHE, 
            key = "'favorites:' + #postId + ':' + #pageNum + ':' + #pageSize", 
            expire = CacheConstant.SOCIAL_INTERACTION_CACHE_EXPIRE,
            cacheType = CacheType.BOTH)
    public IPage<SocialInteractionInfo> getPostFavoriteUsers(Long postId, Integer pageNum, Integer pageSize) {
        return getPostInteractionUsers(postId, InteractionTypeEnum.FAVORITE.name(), pageNum, pageSize);
    }

    @Override
    @Cached(name = CacheConstant.SOCIAL_POST_INTERACTION_USERS_CACHE, 
            key = "'shares:' + #postId + ':' + #pageNum + ':' + #pageSize", 
            expire = CacheConstant.SOCIAL_INTERACTION_CACHE_EXPIRE,
            cacheType = CacheType.BOTH)
    public IPage<SocialInteractionInfo> getPostShareUsers(Long postId, Integer pageNum, Integer pageSize) {
        return getPostInteractionUsers(postId, InteractionTypeEnum.SHARE.name(), pageNum, pageSize);
    }

    @Override
    public IPage<SocialInteractionInfo> getReceivedInteractions(Long authorId, String interactionType, 
                                                               Integer pageNum, Integer pageSize) {
        // TODO: 需要联表查询动态的作者ID，这里先返回空列表
        log.debug("查询收到的互动通知暂未实现，作者ID：{}", authorId);
        Page<SocialInteractionInfo> emptyPage = new Page<>(pageNum, pageSize, 0);
        emptyPage.setRecords(List.of());
        return emptyPage;
    }

    @Override
    @CacheInvalidate(name = CacheConstant.SOCIAL_USER_INTERACTION_STATUS_CACHE, key = "#userId + '*'")
    @CacheInvalidate(name = CacheConstant.SOCIAL_POST_INTERACTION_USERS_CACHE, key = "'*'")
    public void batchUpdateUserInfo(Long userId, String userNickname, String userAvatar) {
        log.info("批量更新用户互动信息，用户ID：{}", userId);
        
        LambdaUpdateWrapper<SocialPostInteraction> wrapper = new LambdaUpdateWrapper<SocialPostInteraction>()
            .eq(SocialPostInteraction::getUserId, userId)
            .set(SocialPostInteraction::getUserNickname, userNickname)
            .set(SocialPostInteraction::getUserAvatar, userAvatar);
        
        interactionMapper.update(null, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cleanExpiredViewRecords(Integer daysBack) {
        log.info("清理过期浏览记录，保留天数：{}", daysBack);
        
        LocalDateTime expireTime = LocalDateTime.now().minusDays(daysBack);
        
        LambdaQueryWrapper<SocialPostInteraction> wrapper = new LambdaQueryWrapper<SocialPostInteraction>()
            .eq(SocialPostInteraction::getInteractionType, InteractionTypeEnum.VIEW.name())
            .lt(SocialPostInteraction::getGmtCreate, java.sql.Timestamp.valueOf(expireTime));
        
        interactionMapper.delete(wrapper);
    }

    @Override
    public boolean checkInteractionPermission(Long postId, Long userId, String interactionType) {
        // TODO: 实现权限检查逻辑（如屏蔽关系、隐私设置等）
        return true;
    }

    @Override
    public boolean hasInteracted(Long postId, Long userId, String interactionType) {
        return getUserInteraction(postId, userId, interactionType) != null;
    }

    @Override
    @Cached(name = CacheConstant.SOCIAL_STATISTICS_CACHE, 
            key = "'top:users:' + #startTime + ':' + #endTime + ':' + #limit", 
            expire = CacheConstant.SOCIAL_STATISTICS_CACHE_EXPIRE,
            cacheType = CacheType.BOTH)
    public List<Object> getTopInteractionUsers(LocalDateTime startTime, LocalDateTime endTime, Integer limit) {
        // TODO: 实现热门互动用户排行逻辑
        log.debug("查询热门互动用户排行功能待实现");
        return List.of();
    }

    /**
     * 通用方法：获取动态的特定类型互动用户
     */
    private IPage<SocialInteractionInfo> getPostInteractionUsers(Long postId, String interactionType, 
                                                                Integer pageNum, Integer pageSize) {
        Page<SocialPostInteraction> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SocialPostInteraction> wrapper = new LambdaQueryWrapper<SocialPostInteraction>()
            .eq(SocialPostInteraction::getPostId, postId)
            .eq(SocialPostInteraction::getInteractionType, interactionType)
            .orderByDesc(SocialPostInteraction::getGmtCreate);
        
        IPage<SocialPostInteraction> result = interactionMapper.selectPage(page, wrapper);
        
        List<SocialInteractionInfo> infoList = result.getRecords()
            .stream()
            .map(SocialPostConvertor.INSTANCE::interactionToInfo)
            .collect(Collectors.toList());
        
        Page<SocialInteractionInfo> infoPage = new Page<>(pageNum, pageSize, result.getTotal());
        infoPage.setRecords(infoList);
        
        return infoPage;
    }

    public UserFacadeService getUserFacadeService() {
        return userFacadeService;
    }
}
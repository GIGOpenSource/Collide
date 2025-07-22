package com.gig.collide.content.domain.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.api.content.constant.ContentStatus;
import com.gig.collide.api.content.constant.ContentType;
import com.gig.collide.api.content.constant.ReviewStatus;
import com.gig.collide.base.exception.BizException;
import com.gig.collide.base.exception.CommonErrorCode;
import com.gig.collide.content.domain.entity.Content;
import com.gig.collide.content.infrastructure.mapper.ContentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 内容领域服务
 * 实现内容管理的核心业务逻辑
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContentDomainService {

    private final ContentMapper contentMapper;

    /**
     * 创建内容
     *
     * @param content 内容对象
     * @return 创建后的内容
     */
    @Transactional(rollbackFor = Exception.class)
    public Content createContent(Content content) {
        log.info("创建内容，标题：{}，作者ID：{}", content.getTitle(), content.getAuthorId());

        // 参数验证
        validateContentForCreate(content);

        // 设置默认值
        content.setStatus(ContentStatus.DRAFT);
        content.setReviewStatus(ReviewStatus.PENDING);
        content.setViewCount(0L);
        content.setLikeCount(0L);
        content.setCommentCount(0L);
        content.setShareCount(0L);
        content.setFavoriteCount(0L);
        content.setWeightScore(0.0);
        content.setIsRecommended(false);
        content.setIsPinned(false);
        content.setAllowComment(true);
        content.setAllowShare(true);

        int result = contentMapper.insert(content);
        if (result <= 0) {
            throw new BizException("内容创建失败", CommonErrorCode.OPERATION_FAILED);
        }

        log.info("内容创建成功，内容ID：{}", content.getId());
        return content;
    }

    /**
     * 更新内容
     *
     * @param content 内容对象
     * @return 更新后的内容
     */
    @Transactional(rollbackFor = Exception.class)
    public Content updateContent(Content content) {
        log.info("更新内容，内容ID：{}", content.getId());

        // 验证内容是否存在
        Content existingContent = getContentById(content.getId());
        if (existingContent == null) {
            throw new BizException("内容不存在", CommonErrorCode.RESOURCE_NOT_FOUND);
        }

        // 验证权限（只有作者可以更新）
        if (!Objects.equals(existingContent.getAuthorId(), content.getAuthorId())) {
            throw new BizException("无权限更新此内容", CommonErrorCode.ACCESS_DENIED);
        }

        // 验证内容是否可编辑
        if (!existingContent.isEditable()) {
            throw new BizException("当前状态的内容不可编辑", CommonErrorCode.OPERATION_NOT_ALLOWED);
        }

        // 参数验证
        validateContentForUpdate(content);

        // 更新内容后重置审核状态
        content.setReviewStatus(ReviewStatus.PENDING);
        content.setReviewComment(null);
        content.setReviewerId(null);
        content.setReviewedTime(null);

        int result = contentMapper.updateById(content);
        if (result <= 0) {
            throw new BizException("内容更新失败", CommonErrorCode.OPERATION_FAILED);
        }

        log.info("内容更新成功，内容ID：{}", content.getId());
        return content;
    }

    /**
     * 删除内容
     *
     * @param contentId 内容ID
     * @param operatorId 操作者ID
     * @return 删除后的内容
     */
    @Transactional(rollbackFor = Exception.class)
    public Content deleteContent(Long contentId, Long operatorId) {
        log.info("删除内容，内容ID：{}，操作者ID：{}", contentId, operatorId);

        Content content = getContentById(contentId);
        if (content == null) {
            throw new BizException("内容不存在", CommonErrorCode.RESOURCE_NOT_FOUND);
        }

        if (!Objects.equals(content.getAuthorId(), operatorId)) {
            throw new BizException("无权限删除此内容", CommonErrorCode.ACCESS_DENIED);
        }

        if (!content.isDeletable()) {
            throw new BizException("当前状态的内容不可删除", CommonErrorCode.OPERATION_NOT_ALLOWED);
        }

        if (content.getStatus() == ContentStatus.PUBLISHED) {
            throw new BizException("已发布内容不可删除", CommonErrorCode.OPERATION_NOT_ALLOWED);
        }

        // 逻辑删除（设置删除标记）
        content.setDeleted(1);
        content.setUpdateTime(LocalDateTime.now());

        int result = contentMapper.updateById(content);
        if (result <= 0) {
            throw new BizException("内容删除失败", CommonErrorCode.OPERATION_FAILED);
        }

        log.info("内容删除成功，内容ID：{}", contentId);
        return content;
    }

    /**
     * 发布内容
     *
     * @param contentId 内容ID
     * @param publisherId 发布者ID
     * @return 发布后的内容
     */
    @Transactional(rollbackFor = Exception.class)
    public Content publishContent(Long contentId, Long publisherId) {
        log.info("发布内容，内容ID：{}，发布者ID：{}", contentId, publisherId);

        Content content = getContentById(contentId);
        if (content == null) {
            throw new BizException("内容不存在", CommonErrorCode.RESOURCE_NOT_FOUND);
        }

        // 验证发布权限
        validatePublishPermission(content, publisherId);

        // 验证内容审核状态
        if (content.getReviewStatus() != ReviewStatus.APPROVED) {
            throw new BizException("内容未通过审核，无法发布", CommonErrorCode.OPERATION_FAILED);
        }

        // 更新状态和发布时间
        content.setStatus(ContentStatus.PUBLISHED);
        content.setPublishedTime(LocalDateTime.now());

        int result = contentMapper.updateById(content);
        if (result <= 0) {
            throw new BizException("内容发布失败", CommonErrorCode.OPERATION_FAILED);
        }

        log.info("内容发布成功，内容ID：{}", contentId);
        return content;
    }

    /**
     * 根据ID查询内容
     *
     * @param contentId 内容ID
     * @return 内容对象
     */
    public Content getContentById(Long contentId) {
        if (contentId == null) {
            throw new BizException("内容ID不能为空", CommonErrorCode.PARAM_INVALID);
        }

        return contentMapper.selectById(contentId);
    }

    /**
     * 查询内容（带访问权限检查）
     *
     * @param contentId 内容ID
     * @param userId 用户ID
     * @return 内容对象
     */
    public Content getContentWithPermission(Long contentId, Long userId) {
        Content content = getContentById(contentId);
        if (content == null || content.getDeleted() == 1) {
            throw new BizException("内容不存在", CommonErrorCode.RESOURCE_NOT_FOUND);
        }

        // 权限检查
        if (!hasReadPermission(content, userId)) {
            throw new BizException("无权限访问此内容", CommonErrorCode.ACCESS_DENIED);
        }

        return content;
    }

    /**
     * 分页查询内容列表
     *
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @param contentType 内容类型
     * @param status 内容状态
     * @return 内容分页结果
     */
    public IPage<Content> pageContent(int page, int size, ContentType contentType, ContentStatus status) {
        Page<Content> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Content> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(Content::getDeleted, 0);

        if (contentType != null) {
            queryWrapper.eq(Content::getContentType, contentType);
        }

        if (status != null) {
            queryWrapper.eq(Content::getStatus, status);
        }

        // 按创建时间降序排列
        queryWrapper.orderByDesc(Content::getCreateTime);

        return contentMapper.selectPage(pageParam, queryWrapper);
    }

    /**
     * 查询用户的内容列表
     *
     * @param authorId 作者ID
     * @param page 页码
     * @param size 每页大小
     * @return 内容分页结果
     */
    public IPage<Content> pageContentByAuthor(Long authorId, int page, int size) {
        if (authorId == null) {
            throw new BizException("作者ID不能为空", CommonErrorCode.PARAM_INVALID);
        }

        Page<Content> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Content> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(Content::getAuthorId, authorId)
                    .eq(Content::getDeleted, 0)
                    .orderByDesc(Content::getCreateTime);

        return contentMapper.selectPage(pageParam, queryWrapper);
    }

    /**
     * 审核内容
     *
     * @param contentId 内容ID
     * @param reviewerId 审核员ID
     * @param reviewStatus 审核状态
     * @param reviewComment 审核意见
     * @return 审核后的内容
     */
    @Transactional(rollbackFor = Exception.class)
    public Content reviewContent(Long contentId, Long reviewerId, ReviewStatus reviewStatus, String reviewComment) {
        log.info("审核内容，内容ID：{}，审核员ID：{}，审核状态：{}", contentId, reviewerId, reviewStatus);

        Content content = getContentById(contentId);
        if (content == null) {
            throw new BizException("内容不存在", CommonErrorCode.RESOURCE_NOT_FOUND);
        }

        if (!hasReviewPermission(reviewerId)) {
            throw new BizException("无审核权限", CommonErrorCode.ACCESS_DENIED);
        }

        if (content.getReviewStatus() == ReviewStatus.APPROVED || content.getReviewStatus() == ReviewStatus.REJECTED) {
            throw new BizException("内容已审核，不可重复审核", CommonErrorCode.OPERATION_NOT_ALLOWED);
        }

        // 更新审核信息
        content.setReviewStatus(reviewStatus);
        content.setReviewComment(reviewComment);
        content.setReviewerId(reviewerId);
        content.setReviewedTime(LocalDateTime.now());

        int result = contentMapper.updateById(content);
        if (result <= 0) {
            throw new BizException("内容审核失败", CommonErrorCode.OPERATION_FAILED);
        }

        log.info("内容审核成功，内容ID：{}，审核状态：{}", contentId, reviewStatus);
        return content;
    }

    /**
     * 增加内容访问量
     *
     * @param contentId 内容ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void incrementViewCount(Long contentId) {
        if (contentId == null) {
            return;
        }

        Content content = getContentById(contentId);
        if (content != null && content.getDeleted() == 0) {
            content.setViewCount(content.getViewCount() + 1);
            contentMapper.updateById(content);
        }
    }

    /**
     * 查看内容（增加访问量）
     *
     * @param contentId 内容ID
     * @return 内容对象
     */
    public Content viewContent(Long contentId) {
        Content content = getContentById(contentId);
        if (content != null && content.getDeleted() == 0 && content.getStatus() == ContentStatus.PUBLISHED) {
            // 异步增加访问量
            incrementViewCount(contentId);
        }
        return content;
    }

    /**
     * 获取推荐内容列表
     *
     * @param contentType 内容类型
     * @param page 页码
     * @param size 每页大小
     * @return 内容分页结果
     */
    public IPage<Content> getRecommendedContents(ContentType contentType, int page, int size) {
        Page<Content> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Content> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(Content::getDeleted, 0)
                    .eq(Content::getStatus, ContentStatus.PUBLISHED)
                    .eq(Content::getIsRecommended, true);

        if (contentType != null) {
            queryWrapper.eq(Content::getContentType, contentType);
        }

        // 按权重分数和创建时间排序
        queryWrapper.orderByDesc(Content::getWeightScore, Content::getCreateTime);

        return contentMapper.selectPage(pageParam, queryWrapper);
    }

    /**
     * 获取热门内容列表
     *
     * @param contentType 内容类型
     * @param days 热门时间范围（天数）
     * @param page 页码
     * @param size 每页大小
     * @return 内容分页结果
     */
    public IPage<Content> getHotContents(ContentType contentType, int days, int page, int size) {
        Page<Content> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Content> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(Content::getDeleted, 0)
                    .eq(Content::getStatus, ContentStatus.PUBLISHED);

        if (contentType != null) {
            queryWrapper.eq(Content::getContentType, contentType);
        }

        // 如果指定了天数，则限制时间范围
        if (days > 0) {
            queryWrapper.ge(Content::getCreateTime, 
                java.time.LocalDateTime.now().minusDays(days));
        }

        // 按点赞数、查看数、评论数排序
        queryWrapper.orderByDesc(Content::getLikeCount, Content::getViewCount, Content::getCommentCount);

        return contentMapper.selectPage(pageParam, queryWrapper);
    }

    /**
     * 获取最新内容列表
     *
     * @param contentType 内容类型
     * @param page 页码
     * @param size 每页大小
     * @return 内容分页结果
     */
    public IPage<Content> getLatestContents(ContentType contentType, int page, int size) {
        Page<Content> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Content> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(Content::getDeleted, 0)
                    .eq(Content::getStatus, ContentStatus.PUBLISHED);

        if (contentType != null) {
            queryWrapper.eq(Content::getContentType, contentType);
        }

        // 按发布时间降序排列
        queryWrapper.orderByDesc(Content::getPublishedTime);

        return contentMapper.selectPage(pageParam, queryWrapper);
    }

    /**
     * 搜索内容
     *
     * @param keyword 关键词
     * @param page 页码
     * @param size 每页大小
     * @return 内容分页结果
     */
    public IPage<Content> searchContents(String keyword, int page, int size) {
        Page<Content> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Content> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(Content::getDeleted, 0)
                    .eq(Content::getStatus, ContentStatus.PUBLISHED);

        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> 
                wrapper.like(Content::getTitle, keyword)
                       .or()
                       .like(Content::getDescription, keyword)
            );
        }

        // 按相关性和时间排序
        queryWrapper.orderByDesc(Content::getWeightScore, Content::getCreateTime);

        return contentMapper.selectPage(pageParam, queryWrapper);
    }

    /**
     * 点赞内容
     *
     * @param contentId 内容ID
     * @param userId 用户ID
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean likeContent(Long contentId, Long userId) {
        Content content = getContentById(contentId);
        if (content == null || content.getDeleted() == 1) {
            return false;
        }

        // TODO: 检查用户是否已经点赞，避免重复点赞
        // TODO: 实现具体的点赞逻辑，可能需要单独的点赞记录表

        // 简单实现：直接增加点赞数
        content.setLikeCount(content.getLikeCount() + 1);
        int result = contentMapper.updateById(content);
        
        return result > 0;
    }

    /**
     * 收藏内容
     *
     * @param contentId 内容ID
     * @param userId 用户ID
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean favoriteContent(Long contentId, Long userId) {
        Content content = getContentById(contentId);
        if (content == null || content.getDeleted() == 1) {
            return false;
        }

        // TODO: 检查用户是否已经收藏，避免重复收藏
        // TODO: 实现具体的收藏逻辑，可能需要单独的收藏记录表

        // 简单实现：直接增加收藏数
        content.setFavoriteCount(content.getFavoriteCount() + 1);
        int result = contentMapper.updateById(content);
        
        return result > 0;
    }

    /**
     * 分享内容
     *
     * @param contentId 内容ID
     * @param userId 用户ID
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean shareContent(Long contentId, Long userId) {
        Content content = getContentById(contentId);
        if (content == null || content.getDeleted() == 1) {
            return false;
        }

        // TODO: 实现具体的分享逻辑，可能需要单独的分享记录表

        // 简单实现：直接增加分享数
        content.setShareCount(content.getShareCount() + 1);
        int result = contentMapper.updateById(content);
        
        return result > 0;
    }

    /**
     * 查询用户内容列表（带状态过滤）
     *
     * @param authorId 作者ID
     * @param status 内容状态
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @return 内容分页结果
     */
    public IPage<Content> getUserContents(Long authorId, ContentStatus status, Integer pageNo, Integer pageSize) {
        if (authorId == null) {
            throw new BizException("作者ID不能为空", CommonErrorCode.PARAM_INVALID);
        }

        Page<Content> pageParam = new Page<>(pageNo != null ? pageNo : 1, pageSize != null ? pageSize : 20);
        LambdaQueryWrapper<Content> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(Content::getAuthorId, authorId)
                    .eq(Content::getDeleted, 0);

        if (status != null) {
            queryWrapper.eq(Content::getStatus, status);
        }

        queryWrapper.orderByDesc(Content::getCreateTime);

        return contentMapper.selectPage(pageParam, queryWrapper);
    }

    // ========== 私有方法 ==========

    /**
     * 验证创建内容的参数
     */
    private void validateContentForCreate(Content content) {
        if (content == null) {
            throw new BizException("内容对象不能为空", CommonErrorCode.PARAM_INVALID);
        }
        if (!StringUtils.hasText(content.getTitle())) {
            throw new BizException("内容标题不能为空", CommonErrorCode.PARAM_INVALID);
        }
        if (content.getContentType() == null) {
            throw new BizException("内容类型不能为空", CommonErrorCode.PARAM_INVALID);
        }
        if (content.getAuthorId() == null) {
            throw new BizException("作者ID不能为空", CommonErrorCode.PARAM_INVALID);
        }
        if (content.getTitle().length() > 200) {
            throw new BizException("标题长度不能超过200个字符", CommonErrorCode.PARAM_INVALID);
        }
    }

    /**
     * 验证更新内容的参数
     */
    private void validateContentForUpdate(Content content) {
        if (content == null) {
            throw new BizException("内容对象不能为空", CommonErrorCode.PARAM_INVALID);
        }
        if (content.getId() == null) {
            throw new BizException("内容ID不能为空", CommonErrorCode.PARAM_INVALID);
        }
        if (StringUtils.hasText(content.getTitle()) && content.getTitle().length() > 200) {
            throw new BizException("标题长度不能超过200个字符", CommonErrorCode.PARAM_INVALID);
        }
    }

    /**
     * 验证发布权限
     */
    private void validatePublishPermission(Content content, Long publisherId) {
        // 作者可以发布自己的内容
        if (Objects.equals(content.getAuthorId(), publisherId)) {
            return;
        }

        // TODO: 检查管理员权限
        throw new BizException("无权限发布此内容", CommonErrorCode.ACCESS_DENIED);
    }

    /**
     * 检查内容读取权限
     */
    private boolean hasReadPermission(Content content, Long userId) {
        // 已发布的内容所有人都可以读取
        if (content.getStatus() == ContentStatus.PUBLISHED) {
            return true;
        }

        // 作者可以读取自己的内容
        if (Objects.equals(content.getAuthorId(), userId)) {
            return true;
        }

        // TODO: 检查管理员权限
        return false;
    }

    /**
     * 检查审核权限
     */
    private boolean hasReviewPermission(Long reviewerId) {
        // TODO: 实现审核权限检查逻辑
        return true;
    }
} 
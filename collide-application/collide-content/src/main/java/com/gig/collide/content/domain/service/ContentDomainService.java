package com.gig.collide.content.domain.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.api.content.constant.ContentStatus;
import com.gig.collide.api.content.constant.ContentType;
import com.gig.collide.api.content.constant.ReviewStatus;
import com.gig.collide.base.exception.BizException;
import com.gig.collide.base.exception.ErrorCode;
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
        log.info("创建内容，标题：{}，作者：{}", content.getTitle(), content.getAuthorId());

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
            throw new BizException(ErrorCode.OPERATION_FAILED, "内容创建失败");
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
            throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "内容不存在");
        }

        // 验证权限（只有作者可以更新）
        if (!Objects.equals(existingContent.getAuthorId(), content.getAuthorId())) {
            throw new BizException(ErrorCode.ACCESS_DENIED, "无权限更新此内容");
        }

        // 验证内容是否可编辑
        if (!existingContent.isEditable()) {
            throw new BizException(ErrorCode.OPERATION_NOT_ALLOWED, "当前状态的内容不可编辑");
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
            throw new BizException(ErrorCode.OPERATION_FAILED, "内容更新失败");
        }

        log.info("内容更新成功，内容ID：{}", content.getId());
        return content;
    }

    /**
     * 删除内容
     *
     * @param contentId 内容ID
     * @param authorId  作者ID（用于权限验证）
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteContent(Long contentId, Long authorId) {
        log.info("删除内容，内容ID：{}，作者ID：{}", contentId, authorId);

        Content content = getContentById(contentId);
        if (content == null) {
            throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "内容不存在");
        }

        // 验证权限
        if (!Objects.equals(content.getAuthorId(), authorId)) {
            throw new BizException(ErrorCode.ACCESS_DENIED, "无权限删除此内容");
        }

        int result = contentMapper.deleteById(contentId);
        if (result <= 0) {
            throw new BizException(ErrorCode.OPERATION_FAILED, "内容删除失败");
        }

        log.info("内容删除成功，内容ID：{}", contentId);
    }

    /**
     * 发布内容
     *
     * @param contentId 内容ID
     * @param authorId  作者ID
     * @return 发布后的内容
     */
    @Transactional(rollbackFor = Exception.class)
    public Content publishContent(Long contentId, Long authorId) {
        log.info("发布内容，内容ID：{}，作者ID：{}", contentId, authorId);

        Content content = getContentById(contentId);
        if (content == null) {
            throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "内容不存在");
        }

        // 验证权限
        if (!Objects.equals(content.getAuthorId(), authorId)) {
            throw new BizException(ErrorCode.ACCESS_DENIED, "无权限发布此内容");
        }

        // 验证内容是否可发布
        if (content.getStatus() == ContentStatus.PUBLISHED) {
            throw new BizException(ErrorCode.OPERATION_NOT_ALLOWED, "内容已发布");
        }

        if (content.getReviewStatus() != ReviewStatus.APPROVED) {
            throw new BizException(ErrorCode.OPERATION_NOT_ALLOWED, "内容未通过审核，不可发布");
        }

        // 更新状态和发布时间
        content.setStatus(ContentStatus.PUBLISHED);
        content.setPublishedTime(LocalDateTime.now());

        int result = contentMapper.updateById(content);
        if (result <= 0) {
            throw new BizException(ErrorCode.OPERATION_FAILED, "内容发布失败");
        }

        log.info("内容发布成功，内容ID：{}", contentId);
        return content;
    }

    /**
     * 审核内容
     *
     * @param contentId     内容ID
     * @param reviewStatus  审核状态
     * @param reviewComment 审核意见
     * @param reviewerId    审核员ID
     * @return 审核后的内容
     */
    @Transactional(rollbackFor = Exception.class)
    public Content reviewContent(Long contentId, ReviewStatus reviewStatus, 
                                String reviewComment, Long reviewerId) {
        log.info("审核内容，内容ID：{}，审核状态：{}，审核员：{}", contentId, reviewStatus, reviewerId);

        Content content = getContentById(contentId);
        if (content == null) {
            throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "内容不存在");
        }

        // 更新审核信息
        content.setReviewStatus(reviewStatus);
        content.setReviewComment(reviewComment);
        content.setReviewerId(reviewerId);
        content.setReviewedTime(LocalDateTime.now());

        // 如果审核拒绝，将内容状态改为拒绝
        if (reviewStatus == ReviewStatus.REJECTED) {
            content.setStatus(ContentStatus.REJECTED);
        }

        int result = contentMapper.updateById(content);
        if (result <= 0) {
            throw new BizException(ErrorCode.OPERATION_FAILED, "内容审核失败");
        }

        log.info("内容审核完成，内容ID：{}，审核结果：{}", contentId, reviewStatus);
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
            return null;
        }
        return contentMapper.selectById(contentId);
    }

    /**
     * 查看内容（增加查看数）
     *
     * @param contentId 内容ID
     * @return 内容对象
     */
    @Transactional(rollbackFor = Exception.class)
    public Content viewContent(Long contentId) {
        Content content = getContentById(contentId);
        if (content == null) {
            throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "内容不存在");
        }

        // 只有已发布且审核通过的内容才能查看
        if (!content.isVisible()) {
            throw new BizException(ErrorCode.ACCESS_DENIED, "内容不可见");
        }

        // 增加查看数
        contentMapper.incrementViewCount(contentId);

        // 重新查询返回最新数据
        return getContentById(contentId);
    }

    /**
     * 点赞内容
     *
     * @param contentId 内容ID
     * @param userId    用户ID
     * @return 是否操作成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean likeContent(Long contentId, Long userId) {
        log.info("用户点赞内容，内容ID：{}，用户ID：{}", contentId, userId);

        Content content = getContentById(contentId);
        if (content == null) {
            throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "内容不存在");
        }

        if (!content.isVisible()) {
            throw new BizException(ErrorCode.ACCESS_DENIED, "内容不可见");
        }

        // TODO: 检查用户是否已经点赞过（需要实现用户行为表）
        // 这里简化处理，直接增加点赞数
        contentMapper.incrementLikeCount(contentId);

        log.info("内容点赞成功，内容ID：{}", contentId);
        return true;
    }

    /**
     * 收藏内容
     *
     * @param contentId 内容ID
     * @param userId    用户ID
     * @return 是否操作成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean favoriteContent(Long contentId, Long userId) {
        log.info("用户收藏内容，内容ID：{}，用户ID：{}", contentId, userId);

        Content content = getContentById(contentId);
        if (content == null) {
            throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "内容不存在");
        }

        if (!content.isVisible()) {
            throw new BizException(ErrorCode.ACCESS_DENIED, "内容不可见");
        }

        // TODO: 检查用户是否已经收藏过
        contentMapper.incrementFavoriteCount(contentId);

        log.info("内容收藏成功，内容ID：{}", contentId);
        return true;
    }

    /**
     * 分享内容
     *
     * @param contentId 内容ID
     * @param userId    用户ID
     * @return 是否操作成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean shareContent(Long contentId, Long userId) {
        log.info("用户分享内容，内容ID：{}，用户ID：{}", contentId, userId);

        Content content = getContentById(contentId);
        if (content == null) {
            throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "内容不存在");
        }

        if (!content.isVisible()) {
            throw new BizException(ErrorCode.ACCESS_DENIED, "内容不可见");
        }

        if (!content.getAllowShare()) {
            throw new BizException(ErrorCode.OPERATION_NOT_ALLOWED, "内容不允许分享");
        }

        contentMapper.incrementShareCount(contentId);

        log.info("内容分享成功，内容ID：{}", contentId);
        return true;
    }

    /**
     * 分页查询用户内容
     *
     * @param authorId 作者ID
     * @param status   内容状态
     * @param pageNo   页码
     * @param pageSize 每页大小
     * @return 内容分页结果
     */
    public IPage<Content> getUserContents(Long authorId, ContentStatus status, 
                                         int pageNo, int pageSize) {
        Page<Content> page = new Page<>(pageNo, pageSize);
        return contentMapper.selectByAuthorId(page, authorId, status);
    }

    /**
     * 分页查询推荐内容
     *
     * @param contentType 内容类型
     * @param pageNo      页码
     * @param pageSize    每页大小
     * @return 内容分页结果
     */
    public IPage<Content> getRecommendedContents(ContentType contentType, 
                                                int pageNo, int pageSize) {
        Page<Content> page = new Page<>(pageNo, pageSize);
        return contentMapper.selectRecommendedContents(page, contentType, true);
    }

    /**
     * 分页查询热门内容
     *
     * @param contentType 内容类型
     * @param days        统计天数
     * @param pageNo      页码
     * @param pageSize    每页大小
     * @return 内容分页结果
     */
    public IPage<Content> getHotContents(ContentType contentType, Integer days,
                                        int pageNo, int pageSize) {
        Page<Content> page = new Page<>(pageNo, pageSize);
        return contentMapper.selectHotContents(page, contentType, days);
    }

    /**
     * 分页查询最新内容
     *
     * @param contentType 内容类型
     * @param pageNo      页码
     * @param pageSize    每页大小
     * @return 内容分页结果
     */
    public IPage<Content> getLatestContents(ContentType contentType,
                                           int pageNo, int pageSize) {
        Page<Content> page = new Page<>(pageNo, pageSize);
        return contentMapper.selectLatestContents(page, contentType);
    }

    /**
     * 搜索内容
     *
     * @param keyword  关键词
     * @param pageNo   页码
     * @param pageSize 每页大小
     * @return 内容分页结果
     */
    public IPage<Content> searchContents(String keyword, int pageNo, int pageSize) {
        Page<Content> page = new Page<>(pageNo, pageSize);
        return contentMapper.searchByKeyword(page, keyword, ContentStatus.PUBLISHED);
    }

    // 私有验证方法

    /**
     * 验证创建内容的参数
     */
    private void validateContentForCreate(Content content) {
        if (content.getAuthorId() == null) {
            throw new BizException(ErrorCode.PARAM_INVALID, "作者ID不能为空");
        }

        if (!StringUtils.hasText(content.getTitle())) {
            throw new BizException(ErrorCode.PARAM_INVALID, "标题不能为空");
        }

        if (content.getTitle().length() > 100) {
            throw new BizException(ErrorCode.PARAM_INVALID, "标题长度不能超过100字符");
        }

        if (content.getContentType() == null) {
            throw new BizException(ErrorCode.PARAM_INVALID, "内容类型不能为空");
        }

        if (content.getDescription() != null && content.getDescription().length() > 500) {
            throw new BizException(ErrorCode.PARAM_INVALID, "描述长度不能超过500字符");
        }
    }

    /**
     * 验证更新内容的参数
     */
    private void validateContentForUpdate(Content content) {
        if (content.getId() == null) {
            throw new BizException(ErrorCode.PARAM_INVALID, "内容ID不能为空");
        }

        validateContentForCreate(content);
    }
} 
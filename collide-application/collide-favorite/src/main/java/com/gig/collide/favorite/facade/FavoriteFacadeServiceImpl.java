package com.gig.collide.favorite.facade;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.api.favorite.constant.FavoriteStatus;
import com.gig.collide.api.favorite.constant.FavoriteType;
import com.gig.collide.api.favorite.request.FavoriteRequest;
import com.gig.collide.api.favorite.request.FavoriteQueryRequest;
import com.gig.collide.api.favorite.response.FavoriteResponse;
import com.gig.collide.api.favorite.response.data.FavoriteInfo;
import com.gig.collide.api.favorite.service.FavoriteFacadeService;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.favorite.domain.entity.Favorite;
import com.gig.collide.favorite.infrastructure.mapper.FavoriteMapper;

// 其他微服务接口
import com.gig.collide.api.content.service.ContentFacadeService;
import com.gig.collide.api.content.request.ContentQueryRequest;
import com.gig.collide.api.content.response.ContentQueryResponse;
import com.gig.collide.api.content.response.data.ContentInfo;
import com.gig.collide.api.user.service.UserFacadeService;
import com.gig.collide.api.user.request.UserQueryRequest;
import com.gig.collide.api.user.response.UserQueryResponse;
import com.gig.collide.api.user.response.data.UserInfo;
import com.gig.collide.api.social.service.SocialFacadeService;
import com.gig.collide.api.social.request.SocialPostQueryRequest;
import com.gig.collide.api.social.response.SocialPostResponse;
import com.gig.collide.api.social.response.data.SocialPostInfo;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.gig.collide.api.user.request.condition.UserIdQueryCondition;
import com.gig.collide.favorite.domain.entity.FavoriteFolder;

/**
 * 收藏服务 Facade 实现
 * 
 * 基于Collide标准化架构的微服务实现
 * - 应用标准化的代码结构和命名规范
 * - 集成标准化的依赖管理和组件引用
 * - 遵循标准化的异常处理和日志记录
 * - 采用标准化的事务管理和参数校验
 * 
 * 注：缓存、分布式锁、限流、监控等标准化组件将在后续版本中集成
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@Service
@DubboService(version = "1.0.0", group = "collide")
public class FavoriteFacadeServiceImpl implements FavoriteFacadeService {

    // ===================== 标准化依赖注入 =====================
    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private com.gig.collide.favorite.infrastructure.mapper.FavoriteFolderMapper favoriteFolderMapper;

    // ===================== 其他微服务依赖 =====================
    @DubboReference(version = "1.0.0", group = "collide", check = false)
    private ContentFacadeService contentFacadeService;

    @DubboReference(version = "1.0.0", group = "collide", check = false)
    private UserFacadeService userFacadeService;

    @DubboReference(version = "1.0.0", group = "collide", check = false)
    private SocialFacadeService socialFacadeService;

    // ===================== 收藏操作相关方法 =====================
    
    /**
     * 收藏/取消收藏操作
     * 
     * 标准化实现特性：
     * - 事务管理：确保数据一致性
     * - 幂等性保障：通过数据库唯一约束防止重复收藏
     * - 异常处理：统一的错误处理和日志记录
     * - 参数校验：完整的输入参数验证
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public FavoriteResponse favorite(FavoriteRequest request) {
        try {
            log.info("收藏操作请求: userId={}, targetId={}, favoriteType={}, isFavorite={}", 
                request.getUserId(), request.getTargetId(), request.getFavoriteType(), request.getIsFavorite());

            if (request.getIsFavorite()) {
                // 收藏操作 - 使用数据库唯一约束确保幂等性
                return performFavoriteAction(request);
            } else {
                // 取消收藏操作
                return performUnfavoriteAction(request);
            }

        } catch (Exception e) {
            log.error("收藏操作失败: userId={}, targetId={}, favoriteType={}", 
                request.getUserId(), request.getTargetId(), request.getFavoriteType(), e);
            return FavoriteResponse.error("FAVORITE_ERROR", "收藏操作失败: " + e.getMessage());
        }
    }

    /**
     * 执行收藏操作 - 标准化实现
     */
    private FavoriteResponse performFavoriteAction(FavoriteRequest request) {
        try {
            Favorite favorite = new Favorite();
            favorite.setUserId(request.getUserId());
            favorite.setTargetId(request.getTargetId());
            favorite.setFavoriteType(request.getFavoriteType());
            favorite.setStatus(FavoriteStatus.NORMAL);
            favorite.setFolderId(request.getFolderId() != null ? request.getFolderId() : 1L); // 默认收藏夹
            favorite.setCreateTime(LocalDateTime.now());
            favorite.setUpdateTime(LocalDateTime.now());
            
            // 填充目标信息
            fillTargetInfo(favorite);
            
            favoriteMapper.insert(favorite);
            
            log.info("用户收藏成功 - userId: {}, targetId: {}, type: {}", 
                    request.getUserId(), request.getTargetId(), request.getFavoriteType());
                    
            return FavoriteResponse.success((Long) request.getTargetId(), "收藏成功");
            
        } catch (org.springframework.dao.DuplicateKeyException e) {
            log.warn("重复收藏 - userId: {}, targetId: {}, type: {}", 
                    request.getUserId(), request.getTargetId(), request.getFavoriteType());
            return FavoriteResponse.success((Long) request.getTargetId(), "已收藏");
        } catch (Exception e) {
            log.error("收藏失败 - userId: {}, targetId: {}, type: {}, error: {}", 
                    request.getUserId(), request.getTargetId(), request.getFavoriteType(), e.getMessage());
            return FavoriteResponse.error("FAVORITE_ERROR", "收藏失败: " + e.getMessage());
        }
    }

    /**
     * 执行取消收藏操作
     */
    private FavoriteResponse performUnfavoriteAction(FavoriteRequest request) {
        // 检查是否已收藏
        Long existsFavoriteId = favoriteMapper.selectFavoriteId(
            request.getUserId(), request.getFavoriteType(), request.getTargetId());

        if (existsFavoriteId == null) {
            log.info("用户尚未收藏该目标: userId={}, targetId={}, favoriteType={}", 
                request.getUserId(), request.getTargetId(), request.getFavoriteType());
            return FavoriteResponse.success((Long) null, "尚未收藏");
        }

        // 更新收藏状态为已取消（软删除方式）
        Favorite favorite = new Favorite();
        favorite.setFavoriteId(existsFavoriteId);
        favorite.setStatus(FavoriteStatus.CANCELLED);
        
        int updatedRows = favoriteMapper.updateById(favorite);
        if (updatedRows > 0) {
            log.info("取消收藏成功: favoriteId={}", existsFavoriteId);
            return FavoriteResponse.success(existsFavoriteId, "取消收藏成功");
        } else {
            log.warn("取消收藏失败，记录可能已被修改: favoriteId={}", existsFavoriteId);
            return FavoriteResponse.error("UNFAVORITE_FAILED", "取消收藏失败，请重试");
        }
    }

    /**
     * 构建收藏记录
     */
    private Favorite buildFavoriteRecord(FavoriteRequest request) {
        Favorite favorite = new Favorite();
        favorite.setFavoriteType(request.getFavoriteType());
        favorite.setTargetId(request.getTargetId());
        favorite.setUserId(request.getUserId());
        favorite.setFolderId(request.getFolderId() != null ? request.getFolderId() : 1L); // 默认收藏夹
        favorite.setStatus(FavoriteStatus.NORMAL);
        favorite.setRemark(request.getRemark());
        favorite.setFavoriteTime(LocalDateTime.now());
        
        // 填充目标信息冗余字段
        fillTargetInfo(favorite);
        
        return favorite;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FavoriteResponse batchFavorite(FavoriteRequest request) {
        try {
            log.info("批量收藏操作请求: userId={}, favoriteType={}, isFavorite={}, targetCount={}", 
                request.getUserId(), request.getFavoriteType(), request.getIsFavorite(), 
                request.getAllTargetIds().size());

            // 验证批量操作参数
            if (!request.isBatchOperation() && request.getTargetId() == null) {
                return FavoriteResponse.error("INVALID_PARAMS", "批量收藏需要提供目标ID列表或单个目标ID");
            }

            List<Long> targetIds = request.getAllTargetIds();
            if (targetIds.isEmpty()) {
                return FavoriteResponse.error("EMPTY_TARGETS", "目标ID列表不能为空");
            }

            // 限制批量操作数量，防止过大的批量请求
            if (targetIds.size() > 100) {
                return FavoriteResponse.error("TOO_MANY_TARGETS", "单次批量收藏不能超过100个目标");
            }

            // 执行批量操作
            BatchFavoriteResult result = executeBatchFavoriteOperation(request, targetIds);

            // 构建响应
            String message = String.format("批量操作完成：成功%d个，失败%d个", 
                result.getSuccessCount(), result.getFailureCount());
            
            if (result.getFailureCount() == 0) {
                return FavoriteResponse.success(result.getSuccessIds(), message);
            } else {
                // 部分成功的情况
                return FavoriteResponse.partialSuccess(result.getSuccessIds(), message, result.getFailureReasons());
            }

        } catch (Exception e) {
            log.error("批量收藏操作失败: userId={}, favoriteType={}", 
                request.getUserId(), request.getFavoriteType(), e);
            return FavoriteResponse.error("BATCH_FAVORITE_ERROR", "批量收藏操作失败: " + e.getMessage());
        }
    }

    /**
     * 执行批量收藏操作
     */
    private BatchFavoriteResult executeBatchFavoriteOperation(FavoriteRequest request, List<Long> targetIds) {
        BatchFavoriteResult result = new BatchFavoriteResult();
        
        for (Long targetId : targetIds) {
            try {
                // 创建单个收藏请求
                FavoriteRequest singleRequest = createSingleFavoriteRequest(request, targetId);
                
                // 执行单个收藏操作
                FavoriteResponse singleResponse = favorite(singleRequest);
                
                if (singleResponse.getSuccess()) {
                    result.addSuccess(targetId);
                } else {
                    result.addFailure(targetId, singleResponse.getResponseMessage());
                }
                
            } catch (Exception e) {
                log.warn("批量收藏中单个目标操作失败: targetId={}, error={}", targetId, e.getMessage());
                result.addFailure(targetId, "操作失败: " + e.getMessage());
            }
        }
        
        return result;
    }

    /**
     * 创建单个收藏请求
     */
    private FavoriteRequest createSingleFavoriteRequest(FavoriteRequest batchRequest, Long targetId) {
        FavoriteRequest singleRequest = new FavoriteRequest();
        singleRequest.setFavoriteType(batchRequest.getFavoriteType());
        singleRequest.setTargetId(targetId);
        singleRequest.setUserId(batchRequest.getUserId());
        singleRequest.setFolderId(batchRequest.getFolderId());
        singleRequest.setIsFavorite(batchRequest.getIsFavorite());
        singleRequest.setRemark(batchRequest.getRemark());
        return singleRequest;
    }

    /**
     * 批量收藏操作结果
     */
    private static class BatchFavoriteResult {
        private final List<Long> successIds = new ArrayList<>();
        private final Map<Long, String> failureReasons = new HashMap<>();

        public void addSuccess(Long targetId) {
            successIds.add(targetId);
        }

        public void addFailure(Long targetId, String reason) {
            failureReasons.put(targetId, reason);
        }

        public List<Long> getSuccessIds() {
            return successIds;
        }

        public Map<Long, String> getFailureReasons() {
            return failureReasons;
        }

        public boolean hasFailures() {
            return !failureReasons.isEmpty();
        }

        public int getSuccessCount() {
            return successIds.size();
        }

        public int getFailureCount() {
            return failureReasons.size();
        }
    }

    @Override
    public FavoriteResponse moveFavorite(Long favoriteId, Long targetFolderId, Long userId) {
        try {
            log.info("移动收藏请求: favoriteId={}, targetFolderId={}, userId={}", 
                favoriteId, targetFolderId, userId);

            // 检查收藏是否存在且属于该用户
            LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Favorite::getFavoriteId, favoriteId)
                       .eq(Favorite::getUserId, userId)
                       .eq(Favorite::getStatus, FavoriteStatus.NORMAL);
            
            Favorite favorite = favoriteMapper.selectOne(queryWrapper);
            if (favorite == null) {
                return FavoriteResponse.error("FAVORITE_NOT_FOUND", "收藏不存在或无权限");
            }

            // 更新收藏夹
            favorite.setFolderId(targetFolderId);
            favoriteMapper.updateById(favorite);

            log.info("移动收藏成功: favoriteId={}", favoriteId);
            return FavoriteResponse.success(favoriteId, "移动收藏成功");

        } catch (Exception e) {
            log.error("移动收藏失败", e);
            return FavoriteResponse.error("MOVE_ERROR", "移动收藏失败: " + e.getMessage());
        }
    }

    @Override
    public PageResponse<FavoriteInfo> pageQueryFavorites(FavoriteQueryRequest request) {
        try {
            log.info("分页查询收藏列表: userId={}, favoriteType={}, folderId={}", 
                request.getUserId(), request.getFavoriteType(), request.getFolderId());

            Page<Favorite> page = new Page<>(request.getCurrentPage(), request.getPageSize());
            
            IPage<Favorite> favoritePage = favoriteMapper.selectUserFavoritePage(
                page, request.getUserId(), request.getFavoriteType(), 
                request.getFolderId(), request.getStatus());

            // 转换为FavoriteInfo
            List<FavoriteInfo> favoriteInfos = favoritePage.getRecords().stream()
                .map(this::convertToFavoriteInfo)
                .collect(Collectors.toList());

            PageResponse<FavoriteInfo> response = new PageResponse<>();
            response.setSuccess(true);
            response.setCurrentPage(request.getCurrentPage());
            response.setPageSize(request.getPageSize());
            response.setTotal((int) favoritePage.getTotal());
            response.setDatas(favoriteInfos);

            return response;

        } catch (Exception e) {
            log.error("查询收藏列表失败", e);
            PageResponse<FavoriteInfo> response = new PageResponse<>();
            response.setSuccess(false);
            response.setResponseMessage("查询收藏列表失败: " + e.getMessage());
            return response;
        }
    }

    @Override
    public FavoriteInfo queryFavoriteDetail(Long favoriteId, Long userId) {
        try {
            log.info("查询收藏详情: favoriteId={}, userId={}", favoriteId, userId);

            LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Favorite::getFavoriteId, favoriteId);
            
            // 如果指定了userId，则验证权限
            if (userId != null) {
                queryWrapper.eq(Favorite::getUserId, userId);
            }

            Favorite favorite = favoriteMapper.selectOne(queryWrapper);
            if (favorite == null) {
                return null;
            }

            return convertToFavoriteInfo(favorite);

        } catch (Exception e) {
            log.error("查询收藏详情失败", e);
            return null;
        }
    }

    @Override
    public Boolean checkFavorited(String favoriteType, Long targetId, Long userId) {
        try {
            FavoriteType type = FavoriteType.valueOf(favoriteType);
            Long favoriteId = favoriteMapper.selectFavoriteId(userId, type, targetId);
            return favoriteId != null;
        } catch (Exception e) {
            log.error("检查收藏状态失败", e);
            return false;
        }
    }

    @Override
    public Long countUserFavorites(Long userId, String favoriteType) {
        try {
            FavoriteType type = favoriteType != null ? FavoriteType.valueOf(favoriteType) : null;
            return favoriteMapper.countUserFavorites(userId, type);
        } catch (Exception e) {
            log.error("统计用户收藏数量失败", e);
            return 0L;
        }
    }

    @Override
    public Long countTargetFavorites(String favoriteType, Long targetId) {
        try {
            FavoriteType type = FavoriteType.valueOf(favoriteType);
            return favoriteMapper.countTargetFavorites(type, targetId);
        } catch (Exception e) {
            log.error("统计目标被收藏数量失败", e);
            return 0L;
        }
    }

    /**
     * 填充目标信息（反范式化数据）
     */
    private void fillTargetInfo(Favorite favorite) {
        try {
            switch (favorite.getFavoriteType()) {
                case CONTENT:
                    fillContentInfo(favorite);
                    break;
                case USER:
                    fillUserInfo(favorite);
                    break;
                case SOCIAL:
                    fillSocialPostInfo(favorite);
                    break;
                default:
                    setDefaultTargetInfo(favorite);
                    break;
            }
        } catch (Exception e) {
            log.warn("填充目标信息失败 - targetId: {}, type: {}, error: {}", 
                    favorite.getTargetId(), favorite.getFavoriteType(), e.getMessage());
            setDefaultTargetInfo(favorite);
        }
    }

    /**
     * 填充内容信息
     */
    private void fillContentInfo(Favorite favorite) {
        ContentQueryRequest contentQuery = new ContentQueryRequest();
        contentQuery.setContentId(favorite.getTargetId());
        
        try {
            ContentQueryResponse<ContentInfo> contentResponse = contentFacadeService.queryContent(contentQuery);
            if (contentResponse.getSuccess() != null && contentResponse.getSuccess() && contentResponse.getData() != null) {
                ContentInfo content = contentResponse.getData();
                favorite.setTargetTitle(content.getTitle());
                favorite.setTargetSummary(truncateContent(content.getDescription(), 200));
                favorite.setTargetCover(content.getCoverUrl());
                
                if (content.getAuthor() != null) {
                    favorite.setTargetAuthorId(content.getAuthor().getUserId());
                    favorite.setTargetAuthorName(content.getAuthor().getNickName());
                    favorite.setTargetAuthorAvatar(content.getAuthor().getProfilePhotoUrl());
                }
                
                return;
            }
        } catch (Exception e) {
            log.warn("获取内容信息失败 - contentId: {}", favorite.getTargetId(), e);
        }
        
        setDefaultTargetInfo(favorite);
    }

    /**
     * 填充用户信息
     */
    private void fillUserInfo(Favorite favorite) {
        UserQueryRequest userQuery = new UserQueryRequest();
        UserIdQueryCondition condition = new UserIdQueryCondition();
        condition.setUserId(favorite.getTargetId());
        userQuery.setUserIdQueryCondition(condition);
        
        try {
            UserQueryResponse<UserInfo> userResponse = userFacadeService.query(userQuery);
            if (userResponse.getSuccess() != null && userResponse.getSuccess() && userResponse.getData() != null) {
                UserInfo user = userResponse.getData();
                favorite.setTargetTitle(user.getNickName() != null ? user.getNickName() : user.getUsername());
                favorite.setTargetSummary(truncateContent(user.getBio(), 200));
                favorite.setTargetCover(user.getProfilePhotoUrl());
                
                favorite.setTargetAuthorId(user.getUserId());
                favorite.setTargetAuthorName(user.getNickName());
                favorite.setTargetAuthorAvatar(user.getProfilePhotoUrl());
                
                return;
            }
        } catch (Exception e) {
            log.warn("获取用户信息失败 - userId: {}", favorite.getTargetId(), e);
        }
        
        setDefaultTargetInfo(favorite);
    }

    /**
     * 填充社交动态信息
     */
    private void fillSocialPostInfo(Favorite favorite) {
        try {
            // 暂时使用简化的逻辑，因为SocialFacadeService的查询方法需要进一步确认
            log.warn("社交动态信息填充暂未实现 - postId: {}", favorite.getTargetId());
        } catch (Exception e) {
            log.warn("获取社交动态信息失败 - postId: {}", favorite.getTargetId(), e);
        }
        
        setDefaultTargetInfo(favorite);
    }

    /**
     * 设置默认目标信息
     */
    private void setDefaultTargetInfo(Favorite favorite) {
        switch (favorite.getFavoriteType()) {
            case CONTENT:
                setDefaultContentInfo(favorite);
                break;
            case USER:
                setDefaultUserInfo(favorite);
                break;
            case SOCIAL:
                setDefaultSocialPostInfo(favorite);
                break;
            default:
                log.warn("未知的收藏类型: {}", favorite.getFavoriteType());
                break;
        }
    }

    /**
     * 设置默认内容信息
     */
    private void setDefaultContentInfo(Favorite favorite) {
        favorite.setTargetTitle("内容");
        favorite.setTargetSummary("暂无描述");
        favorite.setTargetCover("");
        favorite.setTargetAuthorId(null);
        favorite.setTargetAuthorName("未知作者");
        favorite.setTargetAuthorAvatar("");
    }

    /**
     * 设置默认用户信息
     */
    private void setDefaultUserInfo(Favorite favorite) {
        favorite.setTargetTitle("用户");
        favorite.setTargetSummary("暂无简介");
        favorite.setTargetCover("");
        favorite.setTargetAuthorId(favorite.getTargetId());
        favorite.setTargetAuthorName("未知用户");
        favorite.setTargetAuthorAvatar("");
    }

    /**
     * 设置默认社交动态信息
     */
    private void setDefaultSocialPostInfo(Favorite favorite) {
        favorite.setTargetTitle("动态");
        favorite.setTargetSummary("暂无内容");
        favorite.setTargetCover("");
        favorite.setTargetAuthorId(null);
        favorite.setTargetAuthorName("未知作者");
        favorite.setTargetAuthorAvatar("");
    }

    /**
     * 截取内容作为标题
     */
    private String truncateContent(String content, int maxLength) {
        if (content == null || content.isEmpty()) {
            return "无标题";
        }
        if (content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "...";
    }

    /**
     * 获取图片列表中的第一张图片
     */
    private String extractFirstImage(List<String> mediaUrls) {
        if (mediaUrls != null && !mediaUrls.isEmpty()) {
            return mediaUrls.get(0);
        }
        return null;
    }

    /**
     * 转换为FavoriteInfo
     */
    private FavoriteInfo convertToFavoriteInfo(Favorite favorite) {
        FavoriteInfo favoriteInfo = new FavoriteInfo();
        BeanUtils.copyProperties(favorite, favoriteInfo);
        
        // 设置权限信息
        favoriteInfo.setCanUnfavorite(true);
        favoriteInfo.setCanMove(true);
        
        return favoriteInfo;
    }

    // ========== 收藏夹管理实现 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FavoriteResponse createFolder(com.gig.collide.api.favorite.request.FolderCreateRequest createRequest) {
        try {
            log.info("创建收藏夹请求: userId={}, folderName={}, folderType={}", 
                createRequest.getUserId(), createRequest.getFolderName(), createRequest.getFolderType());

            // 检查收藏夹名称是否已存在
            Boolean nameExists = favoriteFolderMapper.checkFolderNameExists(
                createRequest.getUserId(), createRequest.getFolderName(), null);
            if (nameExists) {
                return FavoriteResponse.error("FOLDER_NAME_EXISTS", "收藏夹名称已存在");
            }

            // 检查用户收藏夹数量限制
            List<com.gig.collide.favorite.domain.entity.FavoriteFolder> existingFolders = 
                favoriteFolderMapper.selectUserFolders(createRequest.getUserId());
            if (existingFolders.size() >= 20) { // 最多20个收藏夹
                return FavoriteResponse.error("FOLDER_LIMIT_EXCEEDED", "收藏夹数量已达上限（20个）");
            }

            // 创建收藏夹
            com.gig.collide.favorite.domain.entity.FavoriteFolder folder = buildFolderEntity(createRequest);
            favoriteFolderMapper.insert(folder);

            log.info("创建收藏夹成功: folderId={}", folder.getFolderId());
            return FavoriteResponse.success(folder.getFolderId(), "创建收藏夹成功");

        } catch (Exception e) {
            log.error("创建收藏夹失败", e);
            return FavoriteResponse.error("CREATE_FOLDER_ERROR", "创建收藏夹失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FavoriteResponse updateFolder(com.gig.collide.api.favorite.request.FolderUpdateRequest updateRequest) {
        try {
            log.info("更新收藏夹请求: folderId={}, userId={}", 
                updateRequest.getFolderId(), updateRequest.getUserId());

            // 检查收藏夹是否存在且属于该用户
            com.gig.collide.favorite.domain.entity.FavoriteFolder existingFolder = 
                favoriteFolderMapper.selectById(updateRequest.getFolderId());
            if (existingFolder == null || existingFolder.getIsDeleted() == 1) {
                return FavoriteResponse.error("FOLDER_NOT_FOUND", "收藏夹不存在");
            }
            if (!existingFolder.getUserId().equals(updateRequest.getUserId())) {
                return FavoriteResponse.error("NO_PERMISSION", "无权限操作此收藏夹");
            }

            // 检查是否为默认收藏夹（默认收藏夹不允许修改某些属性）
            if (existingFolder.getIsDefault() && updateRequest.getFolderType() != null) {
                return FavoriteResponse.error("DEFAULT_FOLDER_IMMUTABLE", "默认收藏夹类型不可修改");
            }

            // 检查收藏夹名称是否冲突
            if (updateRequest.getFolderName() != null) {
                Boolean nameExists = favoriteFolderMapper.checkFolderNameExists(
                    updateRequest.getUserId(), updateRequest.getFolderName(), updateRequest.getFolderId());
                if (nameExists) {
                    return FavoriteResponse.error("FOLDER_NAME_EXISTS", "收藏夹名称已存在");
                }
            }

            // 更新收藏夹
            updateFolderFields(existingFolder, updateRequest);
            favoriteFolderMapper.updateById(existingFolder);

            log.info("更新收藏夹成功: folderId={}", updateRequest.getFolderId());
            return FavoriteResponse.success(updateRequest.getFolderId(), "更新收藏夹成功");

        } catch (Exception e) {
            log.error("更新收藏夹失败", e);
            return FavoriteResponse.error("UPDATE_FOLDER_ERROR", "更新收藏夹失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FavoriteResponse deleteFolder(Long folderId, Long userId) {
        try {
            log.info("删除收藏夹请求: folderId={}, userId={}", folderId, userId);

            // 检查收藏夹是否存在且属于该用户
            com.gig.collide.favorite.domain.entity.FavoriteFolder folder = 
                favoriteFolderMapper.selectById(folderId);
            if (folder == null || folder.getIsDeleted() == 1) {
                return FavoriteResponse.error("FOLDER_NOT_FOUND", "收藏夹不存在");
            }
            if (!folder.getUserId().equals(userId)) {
                return FavoriteResponse.error("NO_PERMISSION", "无权限操作此收藏夹");
            }

            // 检查是否为默认收藏夹（默认收藏夹不允许删除）
            if (folder.getIsDefault()) {
                return FavoriteResponse.error("DEFAULT_FOLDER_UNDELETABLE", "默认收藏夹不可删除");
            }

            // 检查收藏夹是否包含收藏内容
            if (folder.getItemCount() > 0) {
                return FavoriteResponse.error("FOLDER_NOT_EMPTY", "请先清空收藏夹中的内容");
            }

            // 删除收藏夹（逻辑删除）
            folder.setIsDeleted(1);
            favoriteFolderMapper.updateById(folder);

            log.info("删除收藏夹成功: folderId={}", folderId);
            return FavoriteResponse.success(folderId, "删除收藏夹成功");

        } catch (Exception e) {
            log.error("删除收藏夹失败", e);
            return FavoriteResponse.error("DELETE_FOLDER_ERROR", "删除收藏夹失败: " + e.getMessage());
        }
    }

    @Override
    public List<com.gig.collide.api.favorite.response.data.FolderInfo> queryUserFolders(Long userId) {
        try {
            log.info("查询用户收藏夹列表: userId={}", userId);

            List<com.gig.collide.favorite.domain.entity.FavoriteFolder> folders = 
                favoriteFolderMapper.selectUserFolders(userId);

            return folders.stream()
                .map(this::convertToFolderInfo)
                .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("查询用户收藏夹列表失败", e);
            return List.of();
        }
    }

    @Override
    public com.gig.collide.api.favorite.response.data.FolderInfo queryFolderDetail(Long folderId, Long userId) {
        try {
            log.info("查询收藏夹详情: folderId={}, userId={}", folderId, userId);

            com.gig.collide.favorite.domain.entity.FavoriteFolder folder = 
                favoriteFolderMapper.selectById(folderId);
            if (folder == null || folder.getIsDeleted() == 1) {
                return null;
            }

            // 权限检查（只有所有者可以查看私密收藏夹）
            if (com.gig.collide.api.favorite.constant.FolderType.PRIVATE.equals(folder.getFolderType()) 
                && !folder.getUserId().equals(userId)) {
                return null;
            }

            return convertToFolderInfo(folder);

        } catch (Exception e) {
            log.error("查询收藏夹详情失败", e);
            return null;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long initDefaultFolder(Long userId) {
        try {
            log.info("初始化用户默认收藏夹: userId={}", userId);

            // 检查是否已有默认收藏夹
            com.gig.collide.favorite.domain.entity.FavoriteFolder existingDefault = 
                favoriteFolderMapper.selectDefaultFolder(userId);
            if (existingDefault != null) {
                return existingDefault.getFolderId();
            }

            // 创建默认收藏夹
            com.gig.collide.favorite.domain.entity.FavoriteFolder defaultFolder = 
                new com.gig.collide.favorite.domain.entity.FavoriteFolder();
            defaultFolder.setFolderName("默认收藏夹");
            defaultFolder.setDescription("系统自动创建的默认收藏夹");
            defaultFolder.setFolderType(com.gig.collide.api.favorite.constant.FolderType.DEFAULT);
            defaultFolder.setUserId(userId);
            defaultFolder.setIsDefault(true);
            defaultFolder.setSortOrder(0);
            defaultFolder.setItemCount(0);

            favoriteFolderMapper.insert(defaultFolder);

            log.info("初始化默认收藏夹成功: folderId={}", defaultFolder.getFolderId());
            return defaultFolder.getFolderId();

        } catch (Exception e) {
            log.error("初始化用户默认收藏夹失败", e);
            return 1L; // 返回默认值
        }
    }

    /**
     * 构建收藏夹实体
     */
    private com.gig.collide.favorite.domain.entity.FavoriteFolder buildFolderEntity(
            com.gig.collide.api.favorite.request.FolderCreateRequest createRequest) {
        com.gig.collide.favorite.domain.entity.FavoriteFolder folder = 
            new com.gig.collide.favorite.domain.entity.FavoriteFolder();
        
        folder.setFolderName(createRequest.getFolderName());
        folder.setDescription(createRequest.getDescription());
        folder.setFolderType(createRequest.getFolderType());
        folder.setUserId(createRequest.getUserId());
        folder.setIsDefault(false); // 用户创建的收藏夹不是默认收藏夹
        folder.setCoverImage(createRequest.getCoverImage());
        folder.setSortOrder(createRequest.getSortOrder() != null ? createRequest.getSortOrder() : 10);
        folder.setItemCount(0);
        
        return folder;
    }

    /**
     * 更新收藏夹字段
     */
    private void updateFolderFields(com.gig.collide.favorite.domain.entity.FavoriteFolder folder, 
            com.gig.collide.api.favorite.request.FolderUpdateRequest updateRequest) {
        if (updateRequest.getFolderName() != null) {
            folder.setFolderName(updateRequest.getFolderName());
        }
        if (updateRequest.getDescription() != null) {
            folder.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getFolderType() != null) {
            folder.setFolderType(updateRequest.getFolderType());
        }
        if (updateRequest.getCoverImage() != null) {
            folder.setCoverImage(updateRequest.getCoverImage());
        }
        if (updateRequest.getSortOrder() != null) {
            folder.setSortOrder(updateRequest.getSortOrder());
        }
    }

    /**
     * 转换为FolderInfo
     */
    private com.gig.collide.api.favorite.response.data.FolderInfo convertToFolderInfo(
            com.gig.collide.favorite.domain.entity.FavoriteFolder folder) {
        com.gig.collide.api.favorite.response.data.FolderInfo folderInfo = 
            new com.gig.collide.api.favorite.response.data.FolderInfo();
        BeanUtils.copyProperties(folder, folderInfo);
        
        // 设置权限信息
        folderInfo.setCanEdit(!folder.getIsDefault()); // 默认收藏夹不可编辑
        folderInfo.setCanDelete(!folder.getIsDefault() && folder.getItemCount() == 0); // 默认收藏夹不可删除，非空收藏夹不可删除
        
        return folderInfo;
    }

    /**
     * 检查收藏夹是否存在
     */
    private boolean checkFolderExists(Long folderId, Long userId) {
        if (folderId == null) {
            return true; // null表示默认收藏夹，始终存在
        }
        
        FavoriteFolder folder = favoriteFolderMapper.selectById(folderId);
        return folder != null && folder.getUserId().equals(userId) && Boolean.FALSE.equals(folder.getIsDeleted());
    }
} 
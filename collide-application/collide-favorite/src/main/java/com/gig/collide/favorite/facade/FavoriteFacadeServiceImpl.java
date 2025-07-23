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
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 收藏服务 Facade 实现
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@Service
@DubboService(version = "1.0.0", group = "collide")
public class FavoriteFacadeServiceImpl implements FavoriteFacadeService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FavoriteResponse favorite(FavoriteRequest request) {
        try {
            log.info("收藏操作请求: userId={}, targetId={}, favoriteType={}, isFavorite={}", 
                request.getUserId(), request.getTargetId(), request.getFavoriteType(), request.getIsFavorite());

            // 检查是否已收藏
            Long existsFavoriteId = favoriteMapper.selectFavoriteId(
                request.getUserId(), request.getFavoriteType(), request.getTargetId());

            if (request.getIsFavorite()) {
                // 收藏操作
                if (existsFavoriteId != null) {
                    return FavoriteResponse.success(existsFavoriteId, "已经收藏过了");
                }

                // 创建新收藏
                Favorite favorite = new Favorite();
                favorite.setFavoriteType(request.getFavoriteType());
                favorite.setTargetId(request.getTargetId());
                favorite.setUserId(request.getUserId());
                favorite.setFolderId(request.getFolderId() != null ? request.getFolderId() : 1L); // 默认收藏夹
                favorite.setStatus(FavoriteStatus.NORMAL);
                favorite.setRemark(request.getRemark());
                favorite.setFavoriteTime(LocalDateTime.now());
                
                // TODO: 根据targetId获取目标信息并填充冗余字段
                fillTargetInfo(favorite);

                favoriteMapper.insert(favorite);
                
                log.info("收藏成功: favoriteId={}", favorite.getFavoriteId());
                return FavoriteResponse.success(favorite.getFavoriteId(), "收藏成功");

            } else {
                // 取消收藏操作
                if (existsFavoriteId == null) {
                    return FavoriteResponse.success(null, "尚未收藏");
                }

                // 更新收藏状态为已取消
                Favorite favorite = new Favorite();
                favorite.setFavoriteId(existsFavoriteId);
                favorite.setStatus(FavoriteStatus.CANCELLED);
                favoriteMapper.updateById(favorite);

                log.info("取消收藏成功: favoriteId={}", existsFavoriteId);
                return FavoriteResponse.success(existsFavoriteId, "取消收藏成功");
            }

        } catch (Exception e) {
            log.error("收藏操作失败", e);
            return FavoriteResponse.error("FAVORITE_ERROR", "收藏操作失败: " + e.getMessage());
        }
    }

    @Override
    public FavoriteResponse batchFavorite(FavoriteRequest request) {
        // TODO: 实现批量收藏逻辑
        return FavoriteResponse.error("NOT_IMPLEMENTED", "批量收藏功能暂未实现");
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
     * 填充目标信息（冗余字段）
     * TODO: 根据不同的收藏类型，调用对应的服务获取目标详细信息
     */
    private void fillTargetInfo(Favorite favorite) {
        // 这里应该根据favoriteType调用对应的服务获取详细信息
        switch (favorite.getFavoriteType()) {
            case CONTENT:
                // TODO: 调用内容服务获取内容信息
                favorite.setTargetTitle("示例内容标题");
                favorite.setTargetCover("https://example.com/cover.jpg");
                favorite.setTargetAuthorName("示例作者");
                break;
            case USER:
                // TODO: 调用用户服务获取用户信息
                favorite.setTargetTitle("示例用户昵称");
                favorite.setTargetCover("https://example.com/avatar.jpg");
                break;
            default:
                // 其他类型的处理
                break;
        }
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
} 
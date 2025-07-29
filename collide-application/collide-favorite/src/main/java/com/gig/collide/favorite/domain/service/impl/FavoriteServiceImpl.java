package com.gig.collide.favorite.domain.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.favorite.domain.entity.Favorite;
import com.gig.collide.favorite.domain.service.FavoriteService;
import com.gig.collide.favorite.infrastructure.mapper.FavoriteMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 收藏业务逻辑实现类 - 简洁版
 * 基于favorite-simple.sql的业务逻辑，实现核心收藏功能
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteMapper favoriteMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Favorite addFavorite(Favorite favorite) {
        log.info("添加收藏: userId={}, favoriteType={}, targetId={}", 
                favorite.getUserId(), favorite.getFavoriteType(), favorite.getTargetId());

        // 验证请求参数
        String validationResult = validateFavoriteRequest(favorite);
        if (validationResult != null) {
            throw new IllegalArgumentException(validationResult);
        }

        // 检查是否可以收藏
        String checkResult = checkCanFavorite(favorite.getUserId(), favorite.getFavoriteType(), favorite.getTargetId());
        if (checkResult != null) {
            throw new IllegalStateException(checkResult);
        }

        // 检查是否已存在收藏关系（包括已取消的）
        Favorite existingFavorite = favoriteMapper.findByUserAndTarget(
                favorite.getUserId(), favorite.getFavoriteType(), favorite.getTargetId(), null);

        if (existingFavorite != null) {
            if (existingFavorite.isActive()) {
                throw new IllegalStateException("已经收藏过该对象");
            } else {
                // 重新激活已取消的收藏
                existingFavorite.activate();
                existingFavorite.setUpdateTime(LocalDateTime.now());
                
                // 更新冗余信息
                if (StringUtils.hasText(favorite.getTargetTitle())) {
                    existingFavorite.setTargetTitle(favorite.getTargetTitle());
                }
                if (StringUtils.hasText(favorite.getTargetCover())) {
                    existingFavorite.setTargetCover(favorite.getTargetCover());
                }
                if (favorite.getTargetAuthorId() != null) {
                    existingFavorite.setTargetAuthorId(favorite.getTargetAuthorId());
                }
                if (StringUtils.hasText(favorite.getUserNickname())) {
                    existingFavorite.setUserNickname(favorite.getUserNickname());
                }
                
                int result = favoriteMapper.updateById(existingFavorite);
                if (result > 0) {
                    log.info("重新激活收藏成功: userId={}, favoriteType={}, targetId={}", 
                            favorite.getUserId(), favorite.getFavoriteType(), favorite.getTargetId());
                    return existingFavorite;
                } else {
                    throw new RuntimeException("重新激活收藏失败");
                }
            }
        }

        // 创建新的收藏记录
        favorite.setStatus("active");
        favorite.setCreateTime(LocalDateTime.now());
        favorite.setUpdateTime(LocalDateTime.now());

        int result = favoriteMapper.insert(favorite);
        if (result > 0) {
            log.info("添加收藏成功: userId={}, favoriteType={}, targetId={}", 
                    favorite.getUserId(), favorite.getFavoriteType(), favorite.getTargetId());
            return favorite;
        } else {
            throw new RuntimeException("添加收藏失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeFavorite(Long userId, String favoriteType, Long targetId, String cancelReason, Long operatorId) {
        log.info("取消收藏: userId={}, favoriteType={}, targetId={}", userId, favoriteType, targetId);

        if (userId == null || !StringUtils.hasText(favoriteType) || targetId == null) {
            throw new IllegalArgumentException("用户ID、收藏类型和目标ID不能为空");
        }

        // 检查收藏关系是否存在
        Favorite favorite = favoriteMapper.findByUserAndTarget(userId, favoriteType, targetId, "active");
        if (favorite == null) {
            log.warn("收藏关系不存在或已取消: userId={}, favoriteType={}, targetId={}", userId, favoriteType, targetId);
            return false;
        }

        // 更新状态为cancelled
        int result = favoriteMapper.updateFavoriteStatus(userId, favoriteType, targetId, "cancelled");
        boolean success = result > 0;
        
        if (success) {
            log.info("取消收藏成功: userId={}, favoriteType={}, targetId={}", userId, favoriteType, targetId);
        } else {
            log.error("取消收藏失败: userId={}, favoriteType={}, targetId={}", userId, favoriteType, targetId);
        }
        
        return success;
    }

    @Override
    public boolean checkFavoriteStatus(Long userId, String favoriteType, Long targetId) {
        if (userId == null || !StringUtils.hasText(favoriteType) || targetId == null) {
            return false;
        }
        return favoriteMapper.checkFavoriteExists(userId, favoriteType, targetId, "active");
    }

    @Override
    public Favorite getFavoriteDetail(Long userId, String favoriteType, Long targetId) {
        if (userId == null || !StringUtils.hasText(favoriteType) || targetId == null) {
            return null;
        }
        return favoriteMapper.findByUserAndTarget(userId, favoriteType, targetId, "active");
    }

    @Override
    public IPage<Favorite> queryFavorites(Integer pageNum, Integer pageSize, Long userId, String favoriteType,
                                         Long targetId, String targetTitle, Long targetAuthorId, String userNickname,
                                         String status, String queryType, String orderBy, String orderDirection) {
        log.info("分页查询收藏记录: pageNum={}, pageSize={}, userId={}, favoriteType={}", 
                pageNum, pageSize, userId, favoriteType);

        // 构建分页对象
        Page<Favorite> page = new Page<>(pageNum, pageSize);

        // 调用复合条件查询
        return favoriteMapper.findWithConditions(page, userId, favoriteType, targetId, targetTitle,
                targetAuthorId, userNickname, status, queryType, orderBy, orderDirection);
    }

    @Override
    public IPage<Favorite> getUserFavorites(Long userId, String favoriteType, Integer pageNum, Integer pageSize) {
        Page<Favorite> page = new Page<>(pageNum, pageSize);
        return favoriteMapper.findUserFavorites(page, userId, favoriteType, "active");
    }

    @Override
    public IPage<Favorite> getTargetFavorites(String favoriteType, Long targetId, Integer pageNum, Integer pageSize) {
        Page<Favorite> page = new Page<>(pageNum, pageSize);
        return favoriteMapper.findTargetFavorites(page, favoriteType, targetId, "active");
    }

    @Override
    public Long getUserFavoriteCount(Long userId, String favoriteType) {
        return favoriteMapper.countUserFavorites(userId, favoriteType, "active");
    }

    @Override
    public Long getTargetFavoriteCount(String favoriteType, Long targetId) {
        return favoriteMapper.countTargetFavorites(favoriteType, targetId, "active");
    }

    @Override
    public Map<String, Object> getUserFavoriteStatistics(Long userId) {
        if (userId == null) {
            return new HashMap<>();
        }
        return favoriteMapper.getUserFavoriteStatistics(userId);
    }

    @Override
    public Map<Long, Boolean> batchCheckFavoriteStatus(Long userId, String favoriteType, List<Long> targetIds) {
        if (userId == null || !StringUtils.hasText(favoriteType) || targetIds == null || targetIds.isEmpty()) {
            return new HashMap<>();
        }

        List<Map<String, Object>> results = favoriteMapper.batchCheckFavoriteStatus(userId, favoriteType, targetIds, "active");
        
        return results.stream().collect(Collectors.toMap(
                result -> Long.valueOf(result.get("targetId").toString()),
                result -> Integer.valueOf(result.get("isFavorited").toString()) > 0
        ));
    }

    @Override
    public IPage<Favorite> searchFavoritesByTitle(Long userId, String titleKeyword, String favoriteType, 
                                                 Integer pageNum, Integer pageSize) {
        Page<Favorite> page = new Page<>(pageNum, pageSize);
        return favoriteMapper.searchByTitle(page, userId, titleKeyword, favoriteType, "active");
    }

    @Override
    public IPage<Favorite> getPopularFavorites(String favoriteType, Integer pageNum, Integer pageSize) {
        Page<Favorite> page = new Page<>(pageNum, pageSize);
        return favoriteMapper.findPopularFavorites(page, favoriteType, "active");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateUserInfo(Long userId, String nickname) {
        log.info("更新用户冗余信息: userId={}, nickname={}", userId, nickname);

        if (userId == null) {
            return 0;
        }

        return favoriteMapper.updateUserInfo(userId, nickname);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateTargetInfo(String favoriteType, Long targetId, String title, String cover, Long authorId) {
        log.info("更新目标对象冗余信息: favoriteType={}, targetId={}, title={}", favoriteType, targetId, title);

        if (!StringUtils.hasText(favoriteType) || targetId == null) {
            return 0;
        }

        return favoriteMapper.updateTargetInfo(favoriteType, targetId, title, cover, authorId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cleanCancelledFavorites(Integer days) {
        log.info("清理已取消的收藏记录: days={}", days);

        if (days == null || days <= 0) {
            days = 30; // 默认清理30天前的记录
        }

        int result = favoriteMapper.cleanCancelledFavorites(days);
        log.info("清理完成: 删除{}条已取消的收藏记录", result);
        
        return result;
    }

    @Override
    public IPage<Favorite> getFavoritesByAuthor(Long targetAuthorId, String favoriteType, Integer pageNum, Integer pageSize) {
        Page<Favorite> page = new Page<>(pageNum, pageSize);
        return favoriteMapper.findByAuthor(page, targetAuthorId, favoriteType, "active");
    }

    @Override
    public String validateFavoriteRequest(Favorite favorite) {
        if (favorite == null) {
            return "收藏对象不能为空";
        }

        if (favorite.getUserId() == null) {
            return "用户ID不能为空";
        }

        if (!StringUtils.hasText(favorite.getFavoriteType())) {
            return "收藏类型不能为空";
        }

        if (favorite.getTargetId() == null) {
            return "收藏目标ID不能为空";
        }

        // 验证收藏类型
        if (!"CONTENT".equals(favorite.getFavoriteType()) && !"GOODS".equals(favorite.getFavoriteType())) {
            return "收藏类型必须是CONTENT或GOODS";
        }

        return null; // 验证通过
    }

    @Override
    public String checkCanFavorite(Long userId, String favoriteType, Long targetId) {
        if (userId == null || !StringUtils.hasText(favoriteType) || targetId == null) {
            return "参数不能为空";
        }

        // 可以根据业务需求添加更多限制
        // 例如：检查目标对象是否存在、是否允许收藏等

        return null; // 可以收藏
    }

    @Override
    public boolean existsFavoriteRelation(Long userId, String favoriteType, Long targetId) {
        return favoriteMapper.checkFavoriteExists(userId, favoriteType, targetId, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reactivateFavorite(Long userId, String favoriteType, Long targetId) {
        log.info("重新激活收藏: userId={}, favoriteType={}, targetId={}", userId, favoriteType, targetId);

        if (userId == null || !StringUtils.hasText(favoriteType) || targetId == null) {
            return false;
        }

        int result = favoriteMapper.updateFavoriteStatus(userId, favoriteType, targetId, "active");
        boolean success = result > 0;
        
        if (success) {
            log.info("重新激活收藏成功: userId={}, favoriteType={}, targetId={}", userId, favoriteType, targetId);
        } else {
            log.warn("重新激活收藏失败: userId={}, favoriteType={}, targetId={}", userId, favoriteType, targetId);
        }
        
        return success;
    }
}
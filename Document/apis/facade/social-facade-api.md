# 社交动态门面服务接口文档

## 📋 概述

**接口名称**: `SocialFacadeService`  
**包路径**: `com.gig.collide.api.social.SocialFacadeService`  
**版本**: 3.0.0 (重新设计版)  
**作者**: GIG Team  
**更新时间**: 2024-01-30  

## 🎯 接口设计原则

本接口严格限制在Social模块内，不包含跨模块调用，使用`Result<T>`统一包装返回结果，基于SocialDynamicService的25个核心方法设计。

### 设计特点

- ✅ **严格分层**: 门面层 → Service层 → Mapper层
- ✅ **统一返回**: 所有方法使用 `Result<T>` 包装
- ✅ **完整验证**: 参数验证、权限验证、业务验证
- ✅ **错误规范**: 统一的错误码和错误信息
- ✅ **日志规范**: 完善的操作日志记录

---

## 📦 依赖说明

### 核心依赖

```java
// 请求对象
import com.gig.collide.api.social.request.SocialDynamicCreateRequest;
import com.gig.collide.api.social.request.SocialDynamicQueryRequest;
import com.gig.collide.api.social.request.SocialDynamicUpdateRequest;

// 响应对象
import com.gig.collide.api.social.response.SocialDynamicResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;

// 工具类
import java.time.LocalDateTime;
import java.util.List;
```

---

## 🔧 业务CRUD操作

### 1. 创建动态

```java
/**
 * 创建动态
 * 包含用户验证、内容检查、冗余信息设置
 * 对应Service: createDynamic
 */
Result<SocialDynamicResponse> createDynamic(SocialDynamicCreateRequest request);
```

#### 功能说明
- **参数验证**: 用户ID、动态内容非空验证
- **业务逻辑**: 设置默认值、状态初始化
- **返回结果**: 创建成功的动态完整信息

#### 请求参数结构
```java
public class SocialDynamicCreateRequest {
    private Long userId;           // 用户ID（必填）
    private String content;        // 动态内容（必填）
    private String dynamicType;    // 动态类型：text/image/video/share
    private String mediaUrls;      // 媒体URL列表
    private String location;       // 位置信息
    private String visibility;     // 可见性：public/friends/private
    private String tags;           // 标签列表
    private String shareTargetType; // 分享目标类型
    private Long shareTargetId;    // 分享目标ID
    private String shareTargetTitle; // 分享目标标题
}
```

#### 响应数据结构
```java
public class SocialDynamicResponse {
    private Long id;               // 动态ID
    private Long userId;           // 用户ID
    private String userNickname;   // 用户昵称
    private String userAvatar;     // 用户头像
    private String content;        // 动态内容
    private String dynamicType;    // 动态类型
    private String status;         // 状态
    private Long likeCount;        // 点赞数
    private Long commentCount;     // 评论数
    private Long shareCount;       // 分享数
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
}
```

### 2. 批量创建动态

```java
/**
 * 批量创建动态
 * 用于批量导入或管理员批量操作
 * 对应Service: batchCreateDynamics
 */
Result<Integer> batchCreateDynamics(List<SocialDynamicCreateRequest> requests, Long operatorId);
```

#### 功能说明
- **批量处理**: 支持批量创建多个动态
- **事务保证**: 要么全部成功，要么全部失败
- **返回结果**: 成功创建的动态数量

### 3. 创建分享动态

```java
/**
 * 创建分享动态
 * 专门用于分享其他动态的场景
 * 对应Service: createShareDynamic
 */
Result<SocialDynamicResponse> createShareDynamic(SocialDynamicCreateRequest request);
```

#### 功能说明
- **分享验证**: 验证分享目标是否存在
- **统计更新**: 自动增加原动态的分享数
- **类型设置**: 自动设置动态类型为"share"

### 4. 更新动态内容

```java
/**
 * 更新动态内容
 * 只允许更新内容相关字段，包含权限验证
 */
Result<SocialDynamicResponse> updateDynamic(SocialDynamicUpdateRequest request);
```

#### 功能说明
- **权限验证**: 只能更新自己的动态
- **字段限制**: 仅允许更新内容字段
- **状态检查**: 已删除的动态不允许更新

### 5. 删除动态

```java
/**
 * 删除动态
 * 逻辑删除，包含权限验证
 */
Result<Void> deleteDynamic(Long dynamicId, Long operatorId);
```

#### 功能说明
- **逻辑删除**: 设置状态为"deleted"，不物理删除
- **权限验证**: 只能删除自己的动态
- **关联处理**: 保留统计数据用于分析

### 6. 根据ID查询动态详情

```java
/**
 * 根据ID查询动态详情
 */
Result<SocialDynamicResponse> getDynamicById(Long dynamicId, Boolean includeDeleted);
```

#### 功能说明
- **详情查询**: 获取动态的完整信息
- **删除控制**: 可选择是否包含已删除的动态
- **数据增强**: 包含用户信息、统计信息等

### 7. 分页查询动态列表

```java
/**
 * 分页查询动态列表
 * 支持多条件组合查询
 */
Result<PageResponse<SocialDynamicResponse>> queryDynamics(SocialDynamicQueryRequest request);
```

#### 功能说明
- **多条件查询**: 支持用户ID、类型、状态、关键词等多种条件
- **分页支持**: 标准的分页查询功能
- **排序支持**: 支持按创建时间、点赞数等排序

---

## 🔍 核心查询方法

### 1. 根据用户ID分页查询动态

```java
/**
 * 根据用户ID分页查询动态
 * 对应Service: selectByUserId
 */
Result<PageResponse<SocialDynamicResponse>> selectByUserId(
    Integer currentPage, Integer pageSize, Long userId, String status, String dynamicType);
```

#### 功能说明
- **用户过滤**: 查询指定用户的动态
- **状态筛选**: 可选择查询特定状态的动态
- **类型筛选**: 可选择查询特定类型的动态

### 2. 根据动态类型分页查询

```java
/**
 * 根据动态类型分页查询
 * 对应Service: selectByDynamicType
 */
Result<PageResponse<SocialDynamicResponse>> selectByDynamicType(
    Integer currentPage, Integer pageSize, String dynamicType, String status);
```

### 3. 根据状态分页查询动态

```java
/**
 * 根据状态分页查询动态
 * 对应Service: selectByStatus
 */
Result<PageResponse<SocialDynamicResponse>> selectByStatus(
    Integer currentPage, Integer pageSize, String status);
```

### 4. 获取关注用户的动态流

```java
/**
 * 获取关注用户的动态流
 * 对应Service: selectFollowingDynamics
 */
Result<PageResponse<SocialDynamicResponse>> selectFollowingDynamics(
    Integer currentPage, Integer pageSize, Long userId, String status);
```

#### 功能说明
- **关注流**: 获取用户关注的人发布的动态
- **时间排序**: 按最新时间排序显示
- **状态过滤**: 只显示正常状态的动态

### 5. 搜索动态（按内容搜索）

```java
/**
 * 搜索动态（按内容搜索）
 * 对应Service: searchByContent
 */
Result<PageResponse<SocialDynamicResponse>> searchByContent(
    Integer currentPage, Integer pageSize, String keyword, String status);
```

### 6. 获取热门动态（按互动数排序）

```java
/**
 * 获取热门动态（按互动数排序）
 * 对应Service: selectHotDynamics
 */
Result<PageResponse<SocialDynamicResponse>> selectHotDynamics(
    Integer currentPage, Integer pageSize, String status, String dynamicType);
```

### 7. 根据分享目标查询分享动态

```java
/**
 * 根据分享目标查询分享动态
 * 对应Service: selectByShareTarget
 */
Result<PageResponse<SocialDynamicResponse>> selectByShareTarget(
    Integer currentPage, Integer pageSize, String shareTargetType, Long shareTargetId, String status);
```

---

## 📊 统计计数方法

### 1. 统计用户动态数量

```java
/**
 * 统计用户动态数量
 * 对应Service: countByUserId
 */
Result<Long> countByUserId(Long userId, String status, String dynamicType);
```

#### 功能说明
- **用户统计**: 统计指定用户的动态数量
- **条件过滤**: 可按状态、类型进行过滤统计
- **实时统计**: 返回当前最新的统计数据

### 2. 统计动态类型数量

```java
/**
 * 统计动态类型数量
 * 对应Service: countByDynamicType
 */
Result<Long> countByDynamicType(String dynamicType, String status);
```

### 3. 统计指定时间范围内的动态数量

```java
/**
 * 统计指定时间范围内的动态数量
 * 对应Service: countByTimeRange
 */
Result<Long> countByTimeRange(LocalDateTime startTime, LocalDateTime endTime, String status);
```

#### 功能说明
- **时间范围**: 统计指定时间段内的动态数量
- **数据分析**: 支持运营数据分析和报表统计
- **性能优化**: 使用索引优化的查询方式

---

## 💝 互动统计更新

### 1. 增加点赞数

```java
/**
 * 增加点赞数
 * 对应Service: increaseLikeCount
 */
Result<Integer> increaseLikeCount(Long dynamicId, Long operatorId);
```

#### 功能说明
- **原子操作**: 使用数据库原子操作保证数据一致性
- **权限验证**: 验证操作人权限
- **返回结果**: 返回更新影响的行数

### 2. 减少点赞数

```java
/**
 * 减少点赞数
 * 对应Service: decreaseLikeCount
 */
Result<Integer> decreaseLikeCount(Long dynamicId, Long operatorId);
```

### 3. 增加评论数

```java
/**
 * 增加评论数
 * 对应Service: increaseCommentCount
 */
Result<Integer> increaseCommentCount(Long dynamicId, Long operatorId);
```

### 4. 增加分享数

```java
/**
 * 增加分享数
 * 对应Service: increaseShareCount
 */
Result<Integer> increaseShareCount(Long dynamicId, Long operatorId);
```

### 5. 批量更新统计数据

```java
/**
 * 批量更新统计数据
 * 对应Service: updateStatistics
 */
Result<Integer> updateStatistics(Long dynamicId, Long likeCount, Long commentCount, Long shareCount, Long operatorId);
```

#### 功能说明
- **批量更新**: 一次性更新多个统计字段
- **数据修正**: 用于统计数据的批量修正
- **事务保证**: 保证数据更新的原子性

---

## 🔄 状态管理

### 1. 更新动态状态

```java
/**
 * 更新动态状态
 * 对应Service: updateStatus
 */
Result<Integer> updateStatus(Long dynamicId, String status, Long operatorId);
```

#### 功能说明
- **状态控制**: 支持normal、hidden、deleted等状态
- **权限验证**: 验证操作人是否有权限修改状态
- **状态流转**: 支持状态的合理流转控制

#### 状态说明
| 状态 | 说明 | 用途 |
|------|------|------|
| normal | 正常 | 正常显示的动态 |
| hidden | 隐藏 | 隐藏但不删除，用户可恢复 |
| deleted | 已删除 | 逻辑删除，不对外显示 |

### 2. 批量更新动态状态

```java
/**
 * 批量更新动态状态
 * 对应Service: batchUpdateStatus
 */
Result<Integer> batchUpdateStatus(List<Long> dynamicIds, String status, Long operatorId);
```

#### 功能说明
- **批量处理**: 支持同时更新多个动态的状态
- **管理功能**: 适用于管理员批量操作
- **事务支持**: 保证批量操作的数据一致性

---

## 👤 用户信息同步

### 批量更新用户冗余信息

```java
/**
 * 批量更新用户冗余信息
 * 对应Service: updateUserInfo
 */
Result<Integer> updateUserInfo(Long userId, String userNickname, String userAvatar, Long operatorId);
```

#### 功能说明
- **信息同步**: 当用户信息变更时，同步更新动态中的冗余信息
- **性能优化**: 避免频繁关联查询用户信息
- **数据一致性**: 保证动态中的用户信息与用户表保持一致

#### 使用场景
- 用户修改昵称时自动同步
- 用户修改头像时自动同步
- 定时任务批量同步用户信息

---

## 🗑️ 数据清理

### 物理删除指定状态的历史动态

```java
/**
 * 物理删除指定状态的历史动态
 * 对应Service: deleteByStatusAndTime
 */
Result<Integer> deleteByStatusAndTime(String status, LocalDateTime beforeTime, Integer limit, Long operatorId);
```

#### 功能说明
- **物理删除**: 真正从数据库中删除数据，不可恢复
- **条件删除**: 按状态和时间条件删除
- **限制数量**: 支持限制单次删除的数量，避免长时间锁表

#### ⚠️ 重要提示
- **谨慎操作**: 此操作不可逆，删除后无法恢复
- **权限控制**: 仅管理员可执行此操作
- **备份建议**: 执行前建议做好数据备份

---

## 🌟 特殊查询

### 1. 查询最新动态（全局）

```java
/**
 * 查询最新动态（全局）
 * 对应Service: selectLatestDynamics
 */
Result<List<SocialDynamicResponse>> selectLatestDynamics(Integer limit, String status);
```

#### 功能说明
- **全局最新**: 获取全站最新发布的动态
- **首页展示**: 适用于首页动态流展示
- **数量限制**: 支持限制返回数量，默认10条，最大100条

### 2. 查询用户最新动态

```java
/**
 * 查询用户最新动态
 * 对应Service: selectUserLatestDynamics
 */
Result<List<SocialDynamicResponse>> selectUserLatestDynamics(Long userId, Integer limit, String status);
```

#### 功能说明
- **用户最新**: 获取指定用户最新发布的动态
- **用户主页**: 适用于用户主页展示
- **快速预览**: 快速查看用户最近的活动

### 3. 查询分享动态列表

```java
/**
 * 查询分享动态列表
 * 对应Service: selectShareDynamics
 */
Result<List<SocialDynamicResponse>> selectShareDynamics(String shareTargetType, Integer limit, String status);
```

#### 功能说明
- **分享统计**: 查看某类内容被分享的动态
- **热度分析**: 分析内容的传播情况
- **类型过滤**: 按分享目标类型进行筛选

---

## 🏥 系统健康检查

### 社交动态系统健康检查

```java
/**
 * 社交动态系统健康检查
 */
Result<String> healthCheck();
```

#### 功能说明
- **系统状态**: 检查社交动态系统的运行状态
- **数据库连接**: 验证数据库连接是否正常
- **基础统计**: 返回系统基本统计信息

#### 返回信息示例
```
"社交动态系统运行正常，当前正常文本动态数量: 1250"
```

---

## 📚 错误处理规范

### 统一错误响应格式

```java
{
  "success": false,
  "code": "ERROR_CODE",
  "message": "错误描述信息",
  "data": null,
  "timestamp": "2024-01-30T10:00:00"
}
```

### 错误码定义

| 错误码 | 说明 | 处理建议 |
|--------|------|----------|
| INVALID_PARAM | 参数无效 | 检查请求参数是否正确 |
| DYNAMIC_NOT_FOUND | 动态不存在 | 确认动态ID是否正确 |
| USER_NOT_FOUND | 用户不存在 | 确认用户ID是否正确 |
| NO_PERMISSION | 无权限操作 | 确认操作人权限 |
| DYNAMIC_CREATE_ERROR | 动态创建失败 | 检查创建参数和系统状态 |
| DYNAMIC_UPDATE_ERROR | 动态更新失败 | 检查更新参数和权限 |
| DYNAMIC_DELETE_ERROR | 动态删除失败 | 检查删除权限和动态状态 |
| COUNT_ERROR | 统计查询失败 | 检查查询参数和系统状态 |
| UPDATE_ERROR | 更新操作失败 | 检查更新参数和数据状态 |
| BATCH_CREATE_ERROR | 批量创建失败 | 检查批量数据和系统状态 |
| BATCH_UPDATE_ERROR | 批量更新失败 | 检查批量操作参数 |
| SEARCH_ERROR | 搜索操作失败 | 检查搜索参数和关键词 |
| DELETE_ERROR | 删除操作失败 | 检查删除条件和权限 |
| HEALTH_CHECK_ERROR | 健康检查失败 | 检查系统状态和数据库连接 |

---

## 🔧 最佳实践

### 1. 参数验证

```java
// 必填参数检查
if (userId == null) {
    return Result.error("INVALID_PARAM", "用户ID不能为空");
}

// 参数范围检查
if (limit != null && limit > 100) {
    limit = 100; // 限制最大值
}
```

### 2. 权限验证

```java
// 操作权限检查
if (!dynamic.getUserId().equals(operatorId)) {
    return Result.error("NO_PERMISSION", "只能操作自己的动态");
}
```

### 3. 异常处理

```java
try {
    // 业务逻辑
    SocialDynamic dynamic = socialDynamicService.createDynamic(entity);
    return Result.success(convertToResponse(dynamic));
} catch (Exception e) {
    log.error("创建动态失败", e);
    return Result.error("DYNAMIC_CREATE_ERROR", "创建动态失败: " + e.getMessage());
}
```

### 4. 日志记录

```java
// 操作日志
log.info("创建动态: 用户ID={}, 内容={}", request.getUserId(), request.getContent());

// 调试日志
log.debug("查询动态: 页码={}, 大小={}", currentPage, pageSize);

// 警告日志
log.warn("执行数据清理: 状态={}, 截止时间={}", status, beforeTime);
```

---

## 📈 性能优化建议

### 1. 查询优化
- 合理使用分页，避免大量数据查询
- 利用数据库索引优化查询性能
- 对热门查询使用缓存机制

### 2. 批量操作
- 批量操作时控制单次处理数量
- 使用事务保证数据一致性
- 避免长时间锁表操作

### 3. 缓存策略
- 对热门数据使用缓存
- 合理设置缓存过期时间
- 及时清理无效缓存

---

**文档版本**: 3.0.0  
**最后更新**: 2024-01-30  
**维护团队**: GIG Team  
**接口包路径**: `com.gig.collide.api.social.SocialFacadeService`
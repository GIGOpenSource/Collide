# 标签系统API接口文档

## 概述

标签系统提供了完整的标签管理、内容标签关联和用户兴趣标签功能。系统包含三个主要模块：

1. **标签管理** - 基础标签的增删改查
2. **内容标签管理** - 内容与标签的关联管理
3. **用户兴趣标签管理** - 用户与标签的兴趣关系管理

## API概览

| 模块 | 控制器 | 基础路径 |
|------|--------|----------|
| 标签管理 | TagController | `/api/v1/tags` |
| 内容标签管理 | ContentTagController | `/api/v1/content-tags` |
| 用户兴趣标签管理 | UserInterestTagController | `/api/v1/user-interest-tags` |

---

## 1. 标签管理API (TagController)

### 1.1 标签基础管理

#### 1.1.1 创建标签
- **接口**: `POST /api/v1/tags`
- **描述**: 创建新的标签，包含唯一性验证
- **请求体**: `TagCreateRequest`
- **返回值**: `Result<TagResponse>`

```json
// 请求示例
{
    "name": "Java",
    "tagType": "TECHNOLOGY",
    "description": "Java编程语言",
    "categoryId": 1,
    "operatorId": 1001
}

// 响应示例
{
    "code": 200,
    "message": "成功",
    "data": {
        "id": 1,
        "name": "Java",
        "tagType": "TECHNOLOGY",
        "description": "Java编程语言",
        "categoryId": 1,
        "status": "ACTIVE",
        "usageCount": 0,
        "createTime": "2024-01-30T10:00:00"
    }
}
```

#### 1.1.2 更新标签
- **接口**: `PUT /api/v1/tags/{tagId}`
- **描述**: 更新标签信息
- **路径参数**: 
  - `tagId` (Long): 标签ID
- **请求体**: `TagUpdateRequest`
- **返回值**: `Result<TagResponse>`

#### 1.1.3 删除标签
- **接口**: `DELETE /api/v1/tags/{tagId}`
- **描述**: 逻辑删除标签，包含关联数据检查
- **路径参数**: 
  - `tagId` (Long): 标签ID
- **查询参数**: 
  - `operatorId` (Long): 操作人ID
- **返回值**: `Result<Void>`

#### 1.1.4 查询标签详情
- **接口**: `GET /api/v1/tags/{tagId}`
- **描述**: 根据ID查询标签详细信息
- **路径参数**: 
  - `tagId` (Long): 标签ID
- **返回值**: `Result<TagResponse>`

#### 1.1.5 分页查询标签
- **接口**: `POST /api/v1/tags/query`
- **描述**: 支持多条件组合查询的标签分页列表
- **请求体**: `TagQueryRequest`
- **返回值**: `Result<PageResponse<TagResponse>>`

### 1.2 标签查询功能

#### 1.2.1 根据类型查询标签
- **接口**: `GET /api/v1/tags/type/{tagType}`
- **描述**: 获取指定类型的标签列表
- **路径参数**: 
  - `tagType` (String): 标签类型
- **返回值**: `Result<List<TagResponse>>`

#### 1.2.2 搜索标签
- **接口**: `GET /api/v1/tags/search`
- **描述**: 智能搜索标签，先精确匹配再模糊搜索
- **查询参数**: 
  - `keyword` (String): 搜索关键词
  - `limit` (Integer): 限制数量，默认10
- **返回值**: `Result<List<TagResponse>>`

#### 1.2.3 精确搜索标签
- **接口**: `GET /api/v1/tags/search/exact`
- **描述**: 按名称精确搜索标签
- **查询参数**: 
  - `keyword` (String): 搜索关键词
  - `limit` (Integer): 限制数量，默认10
- **返回值**: `Result<List<TagResponse>>`

#### 1.2.4 获取热门标签
- **接口**: `GET /api/v1/tags/hot`
- **描述**: 按使用次数排序的热门标签
- **查询参数**: 
  - `limit` (Integer): 限制数量，默认20
- **返回值**: `Result<List<TagResponse>>`

#### 1.2.5 根据分类查询标签
- **接口**: `GET /api/v1/tags/category/{categoryId}`
- **描述**: 获取指定分类下的标签列表
- **路径参数**: 
  - `categoryId` (Long): 分类ID
- **返回值**: `Result<List<TagResponse>>`

#### 1.2.6 获取活跃标签
- **接口**: `GET /api/v1/tags/active`
- **描述**: 获取指定分类下的活跃标签
- **查询参数**: 
  - `categoryId` (Long): 分类ID，可选
- **返回值**: `Result<List<TagResponse>>`

### 1.3 标签状态管理

#### 1.3.1 激活标签
- **接口**: `POST /api/v1/tags/{tagId}/activate`
- **描述**: 激活指定的标签
- **路径参数**: 
  - `tagId` (Long): 标签ID
- **查询参数**: 
  - `operatorId` (Long): 操作人ID
- **返回值**: `Result<Void>`

#### 1.3.2 停用标签
- **接口**: `POST /api/v1/tags/{tagId}/deactivate`
- **描述**: 停用指定的标签
- **路径参数**: 
  - `tagId` (Long): 标签ID
- **查询参数**: 
  - `operatorId` (Long): 操作人ID
- **返回值**: `Result<Void>`

#### 1.3.3 批量更新标签状态
- **接口**: `POST /api/v1/tags/batch/status`
- **描述**: 批量更新多个标签的状态
- **查询参数**: 
  - `tagIds` (List<Long>): 标签ID列表
  - `status` (String): 状态
  - `operatorId` (Long): 操作人ID
- **返回值**: `Result<Integer>`

### 1.4 标签使用统计

#### 1.4.1 增加标签使用次数
- **接口**: `POST /api/v1/tags/{tagId}/usage/increase`
- **描述**: 增加指定标签的使用次数
- **路径参数**: 
  - `tagId` (Long): 标签ID
- **返回值**: `Result<Void>`

#### 1.4.2 减少标签使用次数
- **接口**: `POST /api/v1/tags/{tagId}/usage/decrease`
- **描述**: 减少指定标签的使用次数
- **路径参数**: 
  - `tagId` (Long): 标签ID
- **返回值**: `Result<Void>`

#### 1.4.3 获取标签使用统计
- **接口**: `GET /api/v1/tags/stats/usage`
- **描述**: 获取标签使用情况的统计信息
- **查询参数**: 
  - `tagType` (String): 标签类型，可选
  - `limit` (Integer): 限制数量，默认50
- **返回值**: `Result<List<Map<String, Object>>>`

#### 1.4.4 批量获取标签基本信息
- **接口**: `POST /api/v1/tags/summary`
- **描述**: 批量获取多个标签的基本信息
- **请求体**: `List<Long>` - 标签ID列表
- **返回值**: `Result<List<Map<String, Object>>>`

### 1.5 标签检查功能

#### 1.5.1 检查标签是否存在
- **接口**: `GET /api/v1/tags/check/exists`
- **描述**: 检查指定名称和类型的标签是否已存在
- **查询参数**: 
  - `name` (String): 标签名称
  - `tagType` (String): 标签类型
- **返回值**: `Result<Boolean>`

#### 1.5.2 检查标签是否可删除
- **接口**: `GET /api/v1/tags/{tagId}/check/deletable`
- **描述**: 检查标签是否有关联数据，是否可以删除
- **路径参数**: 
  - `tagId` (Long): 标签ID
- **返回值**: `Result<Boolean>`

### 1.6 标签云和推荐

#### 1.6.1 获取标签云
- **接口**: `GET /api/v1/tags/cloud`
- **描述**: 获取标签云数据，包含标签和权重信息
- **查询参数**: 
  - `tagType` (String): 标签类型，可选
  - `limit` (Integer): 限制数量，默认100
- **返回值**: `Result<List<Map<String, Object>>>`

#### 1.6.2 获取相似标签
- **接口**: `GET /api/v1/tags/{tagId}/similar`
- **描述**: 基于标签使用模式和分类获取相似标签
- **路径参数**: 
  - `tagId` (Long): 标签ID
- **查询参数**: 
  - `limit` (Integer): 限制数量，默认10
- **返回值**: `Result<List<TagResponse>>`

### 1.7 数据维护

#### 1.7.1 重新计算标签使用次数
- **接口**: `POST /api/v1/tags/maintenance/recalculate-usage`
- **描述**: 基于实际关联数据重新统计标签使用次数
- **查询参数**: 
  - `operatorId` (Long): 操作人ID
- **返回值**: `Result<Integer>`

#### 1.7.2 合并重复标签
- **接口**: `POST /api/v1/tags/maintenance/merge-duplicates`
- **描述**: 将重复的标签合并到主标签
- **查询参数**: 
  - `mainTagId` (Long): 主标签ID
  - `duplicateTagIds` (List<Long>): 重复标签ID列表
  - `operatorId` (Long): 操作人ID
- **返回值**: `Result<Integer>`

#### 1.7.3 清理无效标签
- **接口**: `POST /api/v1/tags/maintenance/cleanup-unused`
- **描述**: 清理没有任何关联的废弃标签
- **查询参数**: 
  - `operatorId` (Long): 操作人ID
- **返回值**: `Result<Integer>`

### 1.8 系统功能

#### 1.8.1 获取所有标签类型
- **接口**: `GET /api/v1/tags/types`
- **描述**: 获取系统中所有的标签类型列表
- **返回值**: `Result<List<String>>`

#### 1.8.2 获取标签系统统计
- **接口**: `GET /api/v1/tags/system/stats`
- **描述**: 获取标签系统的整体统计信息
- **返回值**: `Result<Map<String, Object>>`

#### 1.8.3 标签系统健康检查
- **接口**: `GET /api/v1/tags/health`
- **描述**: 检查标签系统的健康状态
- **返回值**: `Result<String>`

---

## 2. 内容标签管理API (ContentTagController)

### 2.1 内容标签基础操作

#### 2.1.1 为内容添加标签
- **接口**: `POST /api/v1/content-tags`
- **描述**: 为指定内容添加标签，包含重复检查和标签验证
- **请求体**: `ContentTagRequest`
- **返回值**: `Result<ContentTagResponse>`

```json
// 请求示例
{
    "contentId": 1001,
    "tagId": 1,
    "operatorId": 1001
}

// 响应示例
{
    "code": 200,
    "message": "成功",
    "data": {
        "id": 1,
        "contentId": 1001,
        "tagId": 1,
        "tagName": "Java",
        "createTime": "2024-01-30T10:00:00"
    }
}
```

#### 2.1.2 移除内容标签
- **接口**: `DELETE /api/v1/content-tags/content/{contentId}/tag/{tagId}`
- **描述**: 移除内容的指定标签
- **路径参数**: 
  - `contentId` (Long): 内容ID
  - `tagId` (Long): 标签ID
- **查询参数**: 
  - `operatorId` (Long): 操作人ID
- **返回值**: `Result<Void>`

#### 2.1.3 获取内容的标签列表
- **接口**: `GET /api/v1/content-tags/content/{contentId}`
- **描述**: 获取指定内容的所有关联标签
- **路径参数**: 
  - `contentId` (Long): 内容ID
- **返回值**: `Result<List<ContentTagResponse>>`

#### 2.1.4 获取标签的内容列表
- **接口**: `GET /api/v1/content-tags/tag/{tagId}/contents`
- **描述**: 获取使用指定标签的所有内容
- **路径参数**: 
  - `tagId` (Long): 标签ID
- **返回值**: `Result<List<ContentTagResponse>>`

### 2.2 批量操作

#### 2.2.1 批量为内容添加标签
- **接口**: `POST /api/v1/content-tags/content/{contentId}/batch/add`
- **描述**: 批量为指定内容添加多个标签
- **路径参数**: 
  - `contentId` (Long): 内容ID
- **请求体**: `List<Long>` - 标签ID列表
- **查询参数**: 
  - `operatorId` (Long): 操作人ID
- **返回值**: `Result<Integer>`

#### 2.2.2 批量移除内容标签
- **接口**: `POST /api/v1/content-tags/content/{contentId}/batch/remove`
- **描述**: 批量移除内容的多个标签
- **路径参数**: 
  - `contentId` (Long): 内容ID
- **请求体**: `List<Long>` - 标签ID列表
- **查询参数**: 
  - `operatorId` (Long): 操作人ID
- **返回值**: `Result<Integer>`

#### 2.2.3 替换内容的所有标签
- **接口**: `POST /api/v1/content-tags/content/{contentId}/replace`
- **描述**: 用新的标签列表替换内容的所有标签
- **路径参数**: 
  - `contentId` (Long): 内容ID
- **请求体**: `List<Long>` - 新标签ID列表
- **查询参数**: 
  - `operatorId` (Long): 操作人ID
- **返回值**: `Result<Integer>`

#### 2.2.4 复制内容标签
- **接口**: `POST /api/v1/content-tags/copy`
- **描述**: 将源内容的标签复制到目标内容
- **查询参数**: 
  - `sourceContentId` (Long): 源内容ID
  - `targetContentId` (Long): 目标内容ID
  - `operatorId` (Long): 操作人ID
- **返回值**: `Result<Integer>`

### 2.3 关联查询

#### 2.3.1 获取相关内容
- **接口**: `GET /api/v1/content-tags/content/{contentId}/related`
- **描述**: 基于共同标签获取与指定内容相关的其他内容
- **路径参数**: 
  - `contentId` (Long): 内容ID
- **查询参数**: 
  - `limit` (Integer): 限制数量，默认10
- **返回值**: `Result<List<Long>>`

#### 2.3.2 检查是否有共同标签
- **接口**: `GET /api/v1/content-tags/check/common-tags`
- **描述**: 检查两个内容是否有共同的标签
- **查询参数**: 
  - `contentId1` (Long): 内容1 ID
  - `contentId2` (Long): 内容2 ID
- **返回值**: `Result<Boolean>`

#### 2.3.3 获取共同标签数量
- **接口**: `GET /api/v1/content-tags/count/common-tags`
- **描述**: 获取两个内容的共同标签数量
- **查询参数**: 
  - `contentId1` (Long): 内容1 ID
  - `contentId2` (Long): 内容2 ID
- **返回值**: `Result<Integer>`

### 2.4 统计功能

#### 2.4.1 统计内容标签数量
- **接口**: `GET /api/v1/content-tags/content/{contentId}/count`
- **描述**: 统计指定内容的标签数量
- **路径参数**: 
  - `contentId` (Long): 内容ID
- **返回值**: `Result<Integer>`

#### 2.4.2 统计标签内容数量
- **接口**: `GET /api/v1/content-tags/tag/{tagId}/count`
- **描述**: 统计使用指定标签的内容数量
- **路径参数**: 
  - `tagId` (Long): 标签ID
- **返回值**: `Result<Integer>`

#### 2.4.3 获取最新的内容标签关联
- **接口**: `GET /api/v1/content-tags/recent`
- **描述**: 获取最近创建的内容标签关联记录
- **查询参数**: 
  - `limit` (Integer): 限制数量，默认20
- **返回值**: `Result<List<Map<String, Object>>>`

#### 2.4.4 获取内容标签摘要
- **接口**: `POST /api/v1/content-tags/summary`
- **描述**: 批量获取多个内容的标签关联摘要信息
- **请求体**: `List<Long>` - 内容ID列表
- **返回值**: `Result<List<Map<String, Object>>>`

### 2.5 内容推荐

#### 2.5.1 推荐内容标签
- **接口**: `GET /api/v1/content-tags/content/{contentId}/recommend-tags`
- **描述**: 基于内容特征和相似内容推荐适合的标签
- **路径参数**: 
  - `contentId` (Long): 内容ID
- **查询参数**: 
  - `limit` (Integer): 限制数量，默认10
- **返回值**: `Result<List<Map<String, Object>>>`

#### 2.5.2 内容标签分析
- **接口**: `GET /api/v1/content-tags/content/{contentId}/analysis`
- **描述**: 获取内容标签的统计分析信息
- **路径参数**: 
  - `contentId` (Long): 内容ID
- **返回值**: `Result<Map<String, Object>>`

#### 2.5.3 获取内容标签详细信息
- **接口**: `GET /api/v1/content-tags/content/{contentId}/tags-with-stats`
- **描述**: 获取内容相关的完整标签信息，包含详情和使用统计
- **路径参数**: 
  - `contentId` (Long): 内容ID
- **返回值**: `Result<List<Map<String, Object>>>`

### 2.6 数据维护

#### 2.6.1 清理无效内容标签
- **接口**: `POST /api/v1/content-tags/maintenance/cleanup-invalid`
- **描述**: 清理不存在的内容或标签的关联数据
- **查询参数**: 
  - `operatorId` (Long): 操作人ID
- **返回值**: `Result<Integer>`

#### 2.6.2 内容标签系统健康检查
- **接口**: `GET /api/v1/content-tags/health`
- **描述**: 检查内容标签系统的健康状态
- **返回值**: `Result<String>`

---

## 3. 用户兴趣标签管理API (UserInterestTagController)

### 3.1 用户兴趣标签基础操作

#### 3.1.1 添加用户兴趣标签
- **接口**: `POST /api/v1/user-interest-tags`
- **描述**: 为用户添加兴趣标签，包含重复检查和分数验证
- **请求体**: `UserInterestTagRequest`
- **返回值**: `Result<UserInterestTagResponse>`

```json
// 请求示例
{
    "userId": 1001,
    "tagId": 1,
    "interestScore": 80.5,
    "operatorId": 1001
}

// 响应示例
{
    "code": 200,
    "message": "成功",
    "data": {
        "id": 1,
        "userId": 1001,
        "tagId": 1,
        "tagName": "Java",
        "interestScore": 80.5,
        "status": "ACTIVE",
        "createTime": "2024-01-30T10:00:00"
    }
}
```

#### 3.1.2 移除用户兴趣标签
- **接口**: `DELETE /api/v1/user-interest-tags/user/{userId}/tag/{tagId}`
- **描述**: 移除用户的指定兴趣标签
- **路径参数**: 
  - `userId` (Long): 用户ID
  - `tagId` (Long): 标签ID
- **查询参数**: 
  - `operatorId` (Long): 操作人ID
- **返回值**: `Result<Void>`

#### 3.1.3 更新用户兴趣分数
- **接口**: `PUT /api/v1/user-interest-tags/user/{userId}/tag/{tagId}/score`
- **描述**: 更新用户对指定标签的兴趣分数
- **路径参数**: 
  - `userId` (Long): 用户ID
  - `tagId` (Long): 标签ID
- **查询参数**: 
  - `interestScore` (BigDecimal): 兴趣分数(0.0-100.0)
  - `operatorId` (Long): 操作人ID
- **返回值**: `Result<Void>`

#### 3.1.4 获取用户兴趣标签列表
- **接口**: `GET /api/v1/user-interest-tags/user/{userId}`
- **描述**: 获取指定用户的所有兴趣标签
- **路径参数**: 
  - `userId` (Long): 用户ID
- **返回值**: `Result<List<UserInterestTagResponse>>`

#### 3.1.5 获取用户高分兴趣标签
- **接口**: `GET /api/v1/user-interest-tags/user/{userId}/top`
- **描述**: 获取用户兴趣分数最高的标签列表
- **路径参数**: 
  - `userId` (Long): 用户ID
- **查询参数**: 
  - `limit` (Integer): 限制数量，默认10
- **返回值**: `Result<List<UserInterestTagResponse>>`

#### 3.1.6 获取标签的关注用户
- **接口**: `GET /api/v1/user-interest-tags/tag/{tagId}/followers`
- **描述**: 获取关注指定标签的用户列表
- **路径参数**: 
  - `tagId` (Long): 标签ID
- **返回值**: `Result<List<UserInterestTagResponse>>`

### 3.2 批量操作

#### 3.2.1 批量设置用户兴趣标签
- **接口**: `POST /api/v1/user-interest-tags/user/{userId}/batch/set`
- **描述**: 批量为用户设置多个兴趣标签
- **路径参数**: 
  - `userId` (Long): 用户ID
- **请求体**: `List<Long>` - 标签ID列表
- **查询参数**: 
  - `defaultScore` (BigDecimal): 默认兴趣分数，默认50.0
  - `operatorId` (Long): 操作人ID
- **返回值**: `Result<Integer>`

#### 3.2.2 批量更新用户标签状态
- **接口**: `POST /api/v1/user-interest-tags/user/{userId}/batch/update-status`
- **描述**: 批量更新用户兴趣标签的状态
- **路径参数**: 
  - `userId` (Long): 用户ID
- **请求体**: `List<Long>` - 标签ID列表
- **查询参数**: 
  - `status` (String): 状态
  - `operatorId` (Long): 操作人ID
- **返回值**: `Result<Integer>`

#### 3.2.3 批量激活用户兴趣标签
- **接口**: `POST /api/v1/user-interest-tags/user/{userId}/batch/activate`
- **描述**: 批量激活用户的多个兴趣标签
- **路径参数**: 
  - `userId` (Long): 用户ID
- **请求体**: `List<Long>` - 标签ID列表
- **查询参数**: 
  - `operatorId` (Long): 操作人ID
- **返回值**: `Result<Integer>`

#### 3.2.4 批量停用用户兴趣标签
- **接口**: `POST /api/v1/user-interest-tags/user/{userId}/batch/deactivate`
- **描述**: 批量停用用户的多个兴趣标签
- **路径参数**: 
  - `userId` (Long): 用户ID
- **请求体**: `List<Long>` - 标签ID列表
- **查询参数**: 
  - `operatorId` (Long): 操作人ID
- **返回值**: `Result<Integer>`

### 3.3 兴趣分数管理

#### 3.3.1 切换用户兴趣标签状态
- **接口**: `POST /api/v1/user-interest-tags/user/{userId}/tag/{tagId}/toggle`
- **描述**: 激活或停用用户兴趣标签
- **路径参数**: 
  - `userId` (Long): 用户ID
  - `tagId` (Long): 标签ID
- **查询参数**: 
  - `active` (boolean): 是否激活
  - `operatorId` (Long): 操作人ID
- **返回值**: `Result<Void>`

#### 3.3.2 增加用户兴趣分数
- **接口**: `POST /api/v1/user-interest-tags/user/{userId}/tag/{tagId}/increase-score`
- **描述**: 增加用户对指定标签的兴趣分数
- **路径参数**: 
  - `userId` (Long): 用户ID
  - `tagId` (Long): 标签ID
- **查询参数**: 
  - `increment` (BigDecimal): 增加的分数(0.0-100.0)
  - `operatorId` (Long): 操作人ID
- **返回值**: `Result<Void>`

#### 3.3.3 减少用户兴趣分数
- **接口**: `POST /api/v1/user-interest-tags/user/{userId}/tag/{tagId}/decrease-score`
- **描述**: 减少用户对指定标签的兴趣分数
- **路径参数**: 
  - `userId` (Long): 用户ID
  - `tagId` (Long): 标签ID
- **查询参数**: 
  - `decrement` (BigDecimal): 减少的分数(0.0-100.0)
  - `operatorId` (Long): 操作人ID
- **返回值**: `Result<Void>`

#### 3.3.4 重置用户兴趣分数
- **接口**: `POST /api/v1/user-interest-tags/user/{userId}/tag/{tagId}/reset-score`
- **描述**: 重置用户对指定标签的兴趣分数
- **路径参数**: 
  - `userId` (Long): 用户ID
  - `tagId` (Long): 标签ID
- **查询参数**: 
  - `newScore` (BigDecimal): 新的兴趣分数(0.0-100.0)
  - `operatorId` (Long): 操作人ID
- **返回值**: `Result<Void>`

### 3.4 查询功能

#### 3.4.1 获取用户活跃兴趣标签
- **接口**: `GET /api/v1/user-interest-tags/user/{userId}/active`
- **描述**: 获取用户当前活跃的兴趣标签列表
- **路径参数**: 
  - `userId` (Long): 用户ID
- **返回值**: `Result<List<UserInterestTagResponse>>`

#### 3.4.2 获取用户高兴趣标签
- **接口**: `GET /api/v1/user-interest-tags/user/{userId}/high-interest`
- **描述**: 获取用户兴趣分数高于指定阈值的标签
- **路径参数**: 
  - `userId` (Long): 用户ID
- **查询参数**: 
  - `minScore` (BigDecimal): 最小分数阈值，默认60.0
- **返回值**: `Result<List<UserInterestTagResponse>>`

#### 3.4.3 检查用户是否关注标签
- **接口**: `GET /api/v1/user-interest-tags/check/user/{userId}/tag/{tagId}`
- **描述**: 检查用户是否已经关注指定标签
- **路径参数**: 
  - `userId` (Long): 用户ID
  - `tagId` (Long): 标签ID
- **返回值**: `Result<Boolean>`

### 3.5 统计分析

#### 3.5.1 获取用户兴趣统计
- **接口**: `GET /api/v1/user-interest-tags/user/{userId}/stats`
- **描述**: 获取用户兴趣标签的统计信息
- **路径参数**: 
  - `userId` (Long): 用户ID
- **查询参数**: 
  - `minScore` (BigDecimal): 最小分数阈值，可选
- **返回值**: `Result<List<Map<String, Object>>>`

#### 3.5.2 获取标签热门关注用户
- **接口**: `GET /api/v1/user-interest-tags/tag/{tagId}/hot-users`
- **描述**: 获取关注指定标签的热门用户列表
- **路径参数**: 
  - `tagId` (Long): 标签ID
- **查询参数**: 
  - `limit` (Integer): 限制数量，默认20
- **返回值**: `Result<List<Map<String, Object>>>`

#### 3.5.3 用户兴趣分析
- **接口**: `GET /api/v1/user-interest-tags/user/{userId}/analysis`
- **描述**: 获取用户兴趣标签的统计分析
- **路径参数**: 
  - `userId` (Long): 用户ID
- **返回值**: `Result<Map<String, Object>>`

#### 3.5.4 获取用户标签详细信息
- **接口**: `GET /api/v1/user-interest-tags/user/{userId}/tags-with-interest`
- **描述**: 获取用户相关的完整标签信息，包含详情和兴趣分数
- **路径参数**: 
  - `userId` (Long): 用户ID
- **返回值**: `Result<List<Map<String, Object>>>`

### 3.6 推荐系统

#### 3.6.1 推荐用户标签
- **接口**: `GET /api/v1/user-interest-tags/user/{userId}/recommend-tags`
- **描述**: 基于用户已有兴趣和热门标签推荐可能感兴趣的标签
- **路径参数**: 
  - `userId` (Long): 用户ID
- **查询参数**: 
  - `limit` (Integer): 限制数量，默认10
- **返回值**: `Result<List<Map<String, Object>>>`

#### 3.6.2 推荐相似用户
- **接口**: `GET /api/v1/user-interest-tags/user/{userId}/recommend-similar-users`
- **描述**: 根据用户兴趣推荐具有相似兴趣的用户
- **路径参数**: 
  - `userId` (Long): 用户ID
- **查询参数**: 
  - `limit` (Integer): 限制数量，默认10
- **返回值**: `Result<List<Long>>`

#### 3.6.3 用户兴趣关联分析
- **接口**: `GET /api/v1/user-interest-tags/user/{userId}/interest-correlation`
- **描述**: 获取用户兴趣标签的相关性分析
- **路径参数**: 
  - `userId` (Long): 用户ID
- **返回值**: `Result<Map<String, Object>>`

### 3.7 数据维护

#### 3.7.1 清理无效用户兴趣标签
- **接口**: `POST /api/v1/user-interest-tags/maintenance/cleanup-invalid`
- **描述**: 清理不存在的用户或标签的关联数据
- **查询参数**: 
  - `operatorId` (Long): 操作人ID
- **返回值**: `Result<Integer>`

#### 3.7.2 重新计算用户兴趣分数
- **接口**: `POST /api/v1/user-interest-tags/maintenance/recalculate-scores`
- **描述**: 基于用户行为重新计算兴趣分数
- **查询参数**: 
  - `userId` (Long): 用户ID
  - `operatorId` (Long): 操作人ID
- **返回值**: `Result<Integer>`

#### 3.7.3 用户兴趣标签系统健康检查
- **接口**: `GET /api/v1/user-interest-tags/health`
- **描述**: 检查用户兴趣标签系统的健康状态
- **返回值**: `Result<String>`

---

## 4. 通用返回格式

### 4.1 成功响应
```json
{
    "code": 200,
    "message": "成功",
    "data": {
        // 具体数据
    }
}
```

### 4.2 错误响应
```json
{
    "code": 400,
    "message": "参数错误",
    "data": null
}
```

### 4.3 分页响应
```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "total": 100,
        "current": 1,
        "size": 10,
        "pages": 10,
        "records": [
            // 数据列表
        ]
    }
}
```

## 5. 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未授权 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 6. 注意事项

1. **参数验证**: 所有API都进行严格的参数验证
2. **权限控制**: 需要根据业务需求添加相应的权限验证
3. **幂等性**: 部分操作具有幂等性，重复调用不会产生副作用
4. **事务性**: 批量操作具有事务性，要么全部成功，要么全部失败
5. **缓存策略**: 查询接口建议使用适当的缓存策略提高性能
6. **限流控制**: 对于频繁调用的接口建议加入限流控制
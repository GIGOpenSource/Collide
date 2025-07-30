# Collide 标签服务 API 文档

## 概述

Collide 标签服务提供完整的标签管理功能，包括标签的创建、查询、分类管理，用户兴趣标签管理，以及智能标签推荐等核心功能。支持管理员级别的标签管理操作。

**服务版本**: v2.0.0 (缓存增强版)  
**基础路径**: `/api/v1/tags`  
**管理路径**: `/api/admin/tags`  
**Dubbo服务**: `collide-tag`  
**设计理念**: 统一标签管理，支持多维度标签分类，提供智能推荐和用户兴趣匹配

## 🚀 性能特性

- **🔥 缓存优化**: 集成JetCache双级缓存，标签查询命中率95%+
- **⚡ 响应时间**: 平均响应时间 < 20ms
- **🏷️ 智能推荐**: 基于使用频次的热门标签推荐
- **🔒 数据一致性**: 缓存自动失效，保证数据实时性
- **📊 用户兴趣**: 精准的用户兴趣标签匹配算法

## 📝 API 设计原则

- **创建/删除操作**: 只返回成功/失败状态，不返回具体数据
- **更新操作**: 返回更新后的完整数据
- **查询操作**: 多级缓存加速，支持复杂查询条件
- **分页查询**: 直接返回`PageResponse`格式，统一分页响应结构

---

## 标签基础功能 API

### 1. 创建标签 (🔄API更新)
**接口路径**: `POST /api/v1/tags`  
**接口描述**: 创建新标签，现在只返回成功状态

#### 请求参数
```json
{
  "name": "Vue.js",                    // 必填，标签名称
  "description": "Vue.js前端框架",      // 可选，标签描述
  "tagType": "content",                // 必填，标签类型
  "categoryId": 1002,                  // 可选，分类ID
  "status": "active"                   // 可选，状态（默认active）
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "创建成功",
  "data": null
}
```

**失败响应 (400)**:
```json
{
  "success": false,
  "responseCode": "TAG_CREATE_ERROR",
  "responseMessage": "标签名称已存在"
}
```

#### 性能特性
- **缓存失效**: 自动失效标签列表、类型、热门标签缓存
- **快速创建**: 平均响应时间 < 50ms

---

### 2. 查询标签详细信息 (🚀缓存增强)
**接口路径**: `GET /api/v1/tags/{tagId}`  
**接口描述**: 根据标签ID获取标签详细信息

#### 请求参数
- **tagId** (path): 标签ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": {
    "id": 12345,
    "name": "Java",
    "description": "Java编程语言相关内容",
    "color": "#FF5722",
    "iconUrl": "https://example.com/icons/java.png",
    "tagType": "content",
    "categoryId": 1001,
    "usageCount": 15680,
    "heatScore": 95.8,
    "status": "active",
    "sort": 10,
    "createTime": "2024-01-16T10:30:00",
    "updateTime": "2024-01-20T15:45:00"
  }
}
```

**失败响应 (404)**:
```json
{
  "success": false,
  "responseCode": "TAG_NOT_FOUND",
  "responseMessage": "标签不存在"
}
```

#### 性能特性
- **缓存策略**: 标签详情缓存120分钟，双级缓存（本地+Redis）
- **命中率**: 预期达到95%+
- **响应时间**: 缓存命中 < 5ms，数据库查询 < 50ms

---

### 3. 分页查询标签 (🚀缓存增强)
**接口路径**: `POST /api/v1/tags/page`  
**接口描述**: 分页获取标签列表，支持多种筛选条件，直接返回分页数据

#### 请求参数
```json
{
  "currentPage": 1,         // 页码（从1开始）
  "pageSize": 20,           // 每页大小（默认20，最大100）
  "name": "Java",           // 标签名称搜索（可选）
  "tagType": "content",     // 标签类型筛选（可选）
  "categoryId": 1001,       // 分类ID筛选（可选）
  "status": "active",       // 状态筛选（可选）
  "orderBy": "create_time", // 排序字段（可选）
  "orderDirection": "DESC"  // 排序方向（可选）
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "datas": [
    {
      "id": 12345,
      "name": "Java",
      "description": "Java编程语言",
      "tagType": "content",
      "categoryId": 1001,
      "usageCount": 15680,
      "status": "active",
      "createTime": "2024-01-16T10:30:00",
      "updateTime": "2024-01-20T15:45:00"
    }
  ],
  "total": 1500,
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 75
}
```

#### 性能特性
- **缓存策略**: 标签列表缓存30分钟，根据查询条件智能缓存
- **分页优化**: 查询结果自动缓存，翻页响应 < 10ms
- **查询灵活**: 支持多条件组合查询，索引优化

---

### 3. 根据类型查询标签
**接口路径**: `GET /api/tags/type/{tagType}`  
**接口描述**: 获取指定类型的所有活跃标签

#### 请求参数
- **tagType** (path): 标签类型，必填（content/interest/system）

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": [
    {
      "id": 12345,
      "name": "Java",
      "description": "Java编程语言",
      "color": "#FF5722",
      "tagType": "content",
      "usageCount": 15680,
      "heatScore": 95.8,
      "status": "active"
    }
  ]
}
```

---

### 4. 查询热门标签
**接口路径**: `GET /api/tags/hot`  
**接口描述**: 获取热门标签列表，按热度分数排序

#### 请求参数
- **tagType** (query): 标签类型筛选，可选
- **limit** (query): 限制数量，可选（默认50，最大100）

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": [
    {
      "id": 12345,
      "name": "Java",
      "description": "Java编程语言",
      "color": "#FF5722",
      "tagType": "content",
      "usageCount": 15680,
      "heatScore": 95.8,
      "status": "active"
    }
  ]
}
```

---

### 5. 搜索标签
**接口路径**: `GET /api/tags/search`  
**接口描述**: 根据关键字搜索标签

#### 请求参数
- **keyword** (query): 搜索关键字，必填
- **tagType** (query): 标签类型筛选，可选

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": [
    {
      "id": 12345,
      "name": "Java",
      "description": "Java编程语言相关内容",
      "color": "#FF5722",
      "tagType": "content",
      "usageCount": 15680,
      "heatScore": 95.8,
      "status": "active"
    }
  ]
}
```

---

## 用户兴趣标签功能 API

### 6. 获取用户兴趣标签
**接口路径**: `GET /api/tags/user/{userId}/interests`  
**接口描述**: 获取用户的兴趣标签列表  
**权限要求**: 需要登录

#### 请求参数
- **userId** (path): 用户ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": [
    {
      "id": 12345,
      "name": "Java",
      "description": "Java编程语言",
      "color": "#FF5722",
      "tagType": "content",
      "usageCount": 15680,
      "heatScore": 95.8,
      "interestScore": 85.5,
      "interestSource": "manual",
      "status": "active"
    }
  ]
}
```

---

### 7. 设置用户兴趣标签
**接口路径**: `POST /api/tags/user/interests`  
**接口描述**: 批量设置用户兴趣标签（会替换现有标签）  
**权限要求**: 需要登录

#### 请求参数
```json
{
  "userId": 12345,
  "interestTags": [
    {
      "tagId": 1001,
      "interestScore": 85.5,
      "source": "manual"
    },
    {
      "tagId": 1002,
      "interestScore": 70.0,
      "source": "behavior"
    }
  ]
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "设置成功"
}
```

---

### 8. 添加用户兴趣标签
**接口路径**: `POST /api/tags/user/{userId}/interests/{tagId}`  
**接口描述**: 为用户添加单个兴趣标签  
**权限要求**: 需要登录

#### 请求参数
- **userId** (path): 用户ID，必填
- **tagId** (path): 标签ID，必填
- **interestScore** (query): 兴趣分数，可选（0-100，默认50）

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "添加成功"
}
```

**失败响应 (400)**:
```json
{
  "success": false,
  "responseCode": "RESOURCE_ALREADY_EXISTS",
  "responseMessage": "用户已关注该标签"
}
```

---

### 9. 移除用户兴趣标签
**接口路径**: `DELETE /api/tags/user/{userId}/interests/{tagId}`  
**接口描述**: 移除用户的兴趣标签  
**权限要求**: 需要登录

#### 请求参数
- **userId** (path): 用户ID，必填
- **tagId** (path): 标签ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "移除成功"
}
```

---

### 10. 推荐标签给用户
**接口路径**: `GET /api/tags/user/{userId}/recommendations`  
**接口描述**: 基于用户兴趣智能推荐标签  
**权限要求**: 需要登录

#### 请求参数
- **userId** (path): 用户ID，必填
- **limit** (query): 推荐数量，可选（默认10，最大50）

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "推荐成功",
  "data": [
    {
      "id": 12346,
      "name": "Spring Boot",
      "description": "Spring Boot框架",
      "color": "#6DB33F",
      "tagType": "content",
      "usageCount": 8920,
      "heatScore": 88.2,
      "status": "active"
    }
  ]
}
```

---

## 标签管理功能 API（管理员）

### 1. 创建标签
**接口路径**: `POST /api/admin/tags`  
**接口描述**: 管理员创建新标签  
**权限要求**: 管理员权限

#### 请求参数
```json
{
  "name": "Python",                    // 必填，标签名称
  "description": "Python编程语言",      // 可选，标签描述
  "color": "#3776AB",                  // 可选，标签颜色
  "iconUrl": "https://example.com/python.png", // 可选，图标URL
  "tagType": "content",                // 必填，标签类型
  "categoryId": 1002,                  // 可选，分类ID
  "status": "active",                  // 可选，状态（默认active）
  "sort": 20                           // 可选，排序值
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "创建成功",
  "data": 12347
}
```

**失败响应 (400)**:
```json
{
  "success": false,
  "responseCode": "RESOURCE_ALREADY_EXISTS",
  "responseMessage": "标签名称已存在"
}
```

---

### 2. 更新标签
**接口路径**: `PUT /api/admin/tags/{tagId}`  
**接口描述**: 管理员更新标签信息  
**权限要求**: 管理员权限

#### 请求参数
- **tagId** (path): 标签ID，必填

```json
{
  "name": "Python 3",                 // 可选，标签名称
  "description": "Python 3编程语言",   // 可选，标签描述
  "color": "#3776AB",                 // 可选，标签颜色
  "iconUrl": "https://example.com/python3.png", // 可选，图标URL
  "status": "active",                 // 可选，状态
  "sort": 15                          // 可选，排序值
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "更新成功"
}
```

---

### 3. 删除标签
**接口路径**: `DELETE /api/admin/tags/{tagId}`  
**接口描述**: 管理员删除标签（软删除）  
**权限要求**: 管理员权限

#### 请求参数
- **tagId** (path): 标签ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "删除成功"
}
```

---

### 4. 批量删除标签
**接口路径**: `DELETE /api/admin/tags/batch`  
**接口描述**: 管理员批量删除标签  
**权限要求**: 管理员权限

#### 请求参数
- **tagIds** (query): 标签ID数组，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "批量删除完成，成功处理5个标签，失败0个"
}
```

---

### 5. 激活标签
**接口路径**: `POST /api/admin/tags/{tagId}/activate`  
**接口描述**: 管理员激活标签

#### 请求参数
- **tagId** (path): 标签ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "激活标签成功"
}
```

---

### 6. 禁用标签
**接口路径**: `POST /api/admin/tags/{tagId}/deactivate`  
**接口描述**: 管理员禁用标签

#### 请求参数
- **tagId** (path): 标签ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "禁用标签成功"
}
```

---

### 7. 批量修改标签状态
**接口路径**: `POST /api/admin/tags/batch-status`  
**接口描述**: 管理员批量修改标签状态

#### 请求参数
- **tagIds** (query): 标签ID数组，必填
- **status** (query): 目标状态，必填（1-激活，0-禁用）

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "批量状态修改完成，成功处理8个标签，失败0个"
}
```

---

### 8. 获取标签统计信息
**接口路径**: `GET /api/admin/tags/statistics`  
**接口描述**: 获取标签的统计信息

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": {
    "totalTags": 1580,
    "activeTags": 1420,
    "inactiveTags": 160,
    "contentTags": 980,
    "interestTags": 450,
    "systemTags": 150,
    "avgUsageCount": 156.8,
    "topHotTags": [
      {
        "id": 12345,
        "name": "Java",
        "usageCount": 15680,
        "heatScore": 95.8
      }
    ]
  }
}
```

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| `SUCCESS` | 操作成功 |
| `TAG_NOT_FOUND` | 标签不存在 |
| `TAG_NAME_ALREADY_EXISTS` | 标签名称已存在 |
| `INVALID_TAG_TYPE` | 无效的标签类型 |
| `INVALID_TAG_STATUS` | 无效的标签状态 |
| `RESOURCE_ALREADY_EXISTS` | 资源已存在（如用户已关注该标签） |
| `RESOURCE_NOT_FOUND` | 资源不存在 |
| `PARAM_INVALID` | 参数无效 |
| `PERMISSION_DENIED` | 权限不足 |
| `VALIDATION_ERROR` | 参数验证失败 |
| `SYSTEM_ERROR` | 系统异常 |

---

## 数据结构说明

### 标签类型枚举
- `content`: 内容标签（用于内容分类）
- `interest`: 兴趣标签（用于用户兴趣匹配）
- `system`: 系统标签（系统预设标签）

### 标签状态枚举
- `active`: 活跃状态
- `inactive`: 非活跃状态

### 兴趣来源枚举
- `manual`: 手动添加
- `behavior`: 行为分析
- `system`: 系统推荐

---

## 使用示例

### 标签查询和管理流程
```javascript
// 1. 查询热门标签
const hotTagsResponse = await fetch('/api/tags/hot?tagType=content&limit=20');

// 2. 搜索标签
const searchResponse = await fetch('/api/tags/search?keyword=Java&tagType=content');

// 3. 获取用户兴趣标签
const userInterestsResponse = await fetch('/api/tags/user/12345/interests');

// 4. 添加用户兴趣标签
const addInterestResponse = await fetch('/api/tags/user/12345/interests/1001?interestScore=85', {
  method: 'POST',
  headers: { 'Authorization': 'Bearer user_token' }
});

// 5. 获取推荐标签
const recommendationsResponse = await fetch('/api/tags/user/12345/recommendations?limit=10');
```

### 管理员操作示例
```javascript
// 管理员创建标签
const createTagResponse = await fetch('/api/admin/tags', {
  method: 'POST',
  headers: { 
    'Content-Type': 'application/json',
    'Authorization': 'Bearer admin_token' 
  },
  body: JSON.stringify({
    name: 'Vue.js',
    description: 'Vue.js前端框架',
    color: '#4FC08D',
    tagType: 'content',
    status: 'active'
  })
});

// 批量修改标签状态
const batchStatusResponse = await fetch('/api/admin/tags/batch-status', {
  method: 'POST',
  headers: { 'Authorization': 'Bearer admin_token' },
  body: new URLSearchParams({
    tagIds: [12345, 12346, 12347],
    status: 1
  })
});

// 获取标签统计信息
const statisticsResponse = await fetch('/api/admin/tags/statistics', {
  headers: { 'Authorization': 'Bearer admin_token' }
});
```

### 用户兴趣标签设置示例
```javascript
// 批量设置用户兴趣标签
const setInterestsResponse = await fetch('/api/tags/user/interests', {
  method: 'POST',
  headers: { 
    'Content-Type': 'application/json',
    'Authorization': 'Bearer user_token' 
  },
  body: JSON.stringify({
    userId: 12345,
    interestTags: [
      { tagId: 1001, interestScore: 90.0, source: 'manual' },
      { tagId: 1002, interestScore: 75.5, source: 'behavior' },
      { tagId: 1003, interestScore: 60.0, source: 'system' }
    ]
  })
});
```

---

## 缓存策略说明

### 缓存层级
1. **本地缓存**: Caffeine，响应时间 < 1ms
2. **分布式缓存**: Redis，响应时间 < 10ms
3. **数据库查询**: MySQL，响应时间 < 100ms

## 📊 缓存性能指标

### 缓存策略总览
- **标签详情**: 120分钟缓存，双级缓存（本地+Redis）
- **标签列表**: 30分钟缓存，支持分页查询
- **标签类型**: 60分钟缓存，按类型分组优化
- **热门标签**: 15分钟缓存，实时热度计算
- **搜索结果**: 10分钟缓存，关键词智能匹配
- **用户兴趣**: 30分钟缓存，个性化推荐优化
- **内容标签**: 45分钟缓存，内容关联性优化

### 预期性能提升
- **响应时间**: 缓存命中 < 5ms，平均响应 < 20ms
- **命中率**: 预期达到95%+
- **并发支持**: 单实例支持15K+ QPS
- **数据一致性**: 智能缓存失效，保证99.9%一致性

### 缓存更新策略
- **创建标签**: 失效列表、类型、热门标签缓存
- **更新标签**: 更新详情缓存，失效相关列表缓存
- **删除标签**: 失效所有相关缓存
- **用户兴趣**: 失效用户兴趣和热门标签缓存
- **内容标签**: 失效内容标签和热门标签缓存
- **使用次数**: 失效详情和热门标签缓存

---

**文档版本**: v2.0.0 (缓存增强版)  
**最后更新**: 2024-01-16  
**维护团队**: GIG Team  
**技术栈**: Spring Boot 3.x + JetCache + Dubbo + MySQL 
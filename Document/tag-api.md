# 标签系统API接口文档

## 接口概述

标签系统提供完整的标签管理和用户打标签功能，支持三级标签结构，为内容推荐系统提供数据支持。

## 接口列表

### 1. 标签管理接口

#### 1.1 创建标签
- **接口地址**: `POST /api/tag/create`
- **接口描述**: 创建新标签，支持三级标签结构
- **请求参数**:

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| tagName | string | 是 | 标签名称，最大128字符 |
| description | string | 否 | 标签描述 |
| tagType | enum | 是 | 标签类型（USER/CONTENT/CATEGORY/PERSONAL） |
| level | enum | 是 | 标签层级（LEVEL_1/LEVEL_2/LEVEL_3） |
| parentTagId | long | 否 | 父标签ID，一级标签可为空 |
| color | string | 否 | 标签颜色，格式：#FFFFFF |
| icon | string | 否 | 标签图标 |
| sortOrder | integer | 否 | 排序权重，默认0 |
| creatorId | long | 否 | 创建者ID |
| needReview | boolean | 否 | 是否需要审核，默认false |

- **响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "success": true,
    "timestamp": 1703123456789,
    "data": {
      "tagId": 1001,
      "tagName": "React",
      "tagPath": "/2/5"
    }
  }
}
```

#### 1.2 更新标签
- **接口地址**: `PUT /api/tag/update`
- **接口描述**: 更新标签信息
- **请求参数**:

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| tagId | long | 是 | 标签ID |
| tagName | string | 否 | 标签名称 |
| description | string | 否 | 标签描述 |
| status | enum | 否 | 标签状态（ACTIVE/DISABLED/DRAFT） |
| color | string | 否 | 标签颜色 |
| icon | string | 否 | 标签图标 |
| sortOrder | integer | 否 | 排序权重 |
| updaterId | long | 否 | 更新者ID |
| updateReason | string | 否 | 更新理由 |

- **响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "success": true,
    "timestamp": 1703123456789,
    "affectedRows": 1
  }
}
```

#### 1.3 删除标签
- **接口地址**: `DELETE /api/tag/{tagId}`
- **接口描述**: 删除指定标签
- **路径参数**:

| 参数名 | 类型 | 描述 |
|--------|------|------|
| tagId | long | 标签ID |

- **请求参数**:

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| operatorId | long | 是 | 操作者ID |

#### 1.4 查询标签详情
- **接口地址**: `GET /api/tag/{tagId}`
- **接口描述**: 根据ID查询标签详细信息
- **响应示例**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "tagId": 1001,
    "tagName": "React",
    "description": "React前端框架",
    "tagType": "SYSTEM",
    "status": "ACTIVE",
    "level": "LEVEL_3",
    "parentTagId": 5,
    "parentTagName": "前端",
    "color": "#61DAFB",
    "icon": "react-icon",
    "sortOrder": 2,
    "usageCount": 1520,
    "hotScore": 8.75,
    "creatorId": 0,
    "createdTime": "2023-12-01T10:00:00",
    "tagPath": "/2/5",
    "children": []
  }
}
```

#### 1.5 查询标签树
- **接口地址**: `GET /api/tag/tree`
- **接口描述**: 查询标签树形结构
- **请求参数**:

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| parentTagId | long | 否 | 父标签ID，null查询根标签 |
| includeDisabled | boolean | 否 | 是否包含禁用标签，默认false |

- **响应示例**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": [
    {
      "tagId": 2,
      "tagName": "技术",
      "level": "LEVEL_1",
      "children": [
        {
          "tagId": 5,
          "tagName": "前端",
          "level": "LEVEL_2",
          "children": [
            {
              "tagId": 1001,
              "tagName": "React",
              "level": "LEVEL_3",
              "children": []
            }
          ]
        }
      ]
    }
  ]
}
```

#### 1.6 分页查询标签
- **接口地址**: `GET /api/tag/page`
- **接口描述**: 分页查询标签列表
- **请求参数**:

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| currentPage | integer | 否 | 当前页，默认1 |
| pageSize | integer | 否 | 页面大小，默认10 |
| tagName | string | 否 | 标签名称（模糊查询） |
| tagType | enum | 否 | 标签类型 |
| status | enum | 否 | 标签状态 |
| level | enum | 否 | 标签层级 |
| parentTagId | long | 否 | 父标签ID |
| keyword | string | 否 | 关键词搜索 |

#### 1.7 搜索标签
- **接口地址**: `GET /api/tag/search`
- **接口描述**: 根据关键词搜索标签
- **请求参数**:

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| keyword | string | 是 | 搜索关键词 |
| limit | integer | 否 | 限制数量，默认10 |

#### 1.8 查询热门标签
- **接口地址**: `GET /api/tag/hot`
- **接口描述**: 查询热门标签列表
- **请求参数**:

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| limit | integer | 否 | 限制数量，默认20 |

### 2. 用户打标签接口

#### 2.1 用户打标签
- **接口地址**: `POST /api/tag/user/tag`
- **接口描述**: 用户对内容/商品等对象打标签
- **请求参数**:

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| userId | long | 是 | 用户ID |
| tagIds | array | 是 | 标签ID列表 |
| relationType | enum | 是 | 关联类型（USER/CONTENT/GOODS/COLLECTION） |
| relationObjectId | string | 否 | 关联对象ID |
| weight | decimal | 否 | 标签权重 0.0-1.0，默认1.0 |
| isAutoTag | boolean | 否 | 是否自动标签，默认false |
| tagSource | string | 否 | 标签来源 |
| remark | string | 否 | 备注信息 |
| overrideExisting | boolean | 否 | 是否覆盖已有标签，默认false |

- **响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "success": true,
    "timestamp": 1703123456789,
    "data": {
      "taggedCount": 3,
      "skippedCount": 1,
      "message": "成功打标签3个，跳过重复标签1个"
    }
  }
}
```

#### 2.2 用户取消标签
- **接口地址**: `POST /api/tag/user/untag`
- **接口描述**: 用户取消标签
- **请求参数**:

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| userId | long | 是 | 用户ID |
| tagIds | array | 是 | 标签ID列表 |
| relationType | enum | 否 | 关联类型 |
| relationObjectId | string | 否 | 关联对象ID |
| reason | string | 否 | 取消原因 |
| batchUntag | boolean | 否 | 是否批量取消，默认false |

#### 2.3 查询用户标签
- **接口地址**: `GET /api/tag/user/tags`
- **接口描述**: 查询用户的标签列表
- **请求参数**:

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| userId | long | 是 | 用户ID |
| relationType | enum | 否 | 关联类型 |
| relationObjectId | string | 否 | 关联对象ID |
| onlyAutoTag | boolean | 否 | 是否只查询自动标签 |
| minWeight | decimal | 否 | 最小权重 |
| limit | integer | 否 | 限制数量 |

- **响应示例**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": [
    {
      "userTagId": 10001,
      "userId": 1001,
      "tagId": 5,
      "tagInfo": {
        "tagId": 5,
        "tagName": "前端",
        "tagType": "SYSTEM",
        "level": "LEVEL_2"
      },
      "relationType": "USER",
      "weight": 0.85,
      "usageCount": 15,
      "lastUsedTime": "2023-12-01T15:30:00",
      "createdTime": "2023-11-01T10:00:00",
      "isAutoTag": false,
      "tagSource": "manual"
    }
  ]
}
```

#### 2.4 分页查询用户标签
- **接口地址**: `GET /api/tag/user/tags/page`
- **接口描述**: 分页查询用户标签
- **请求参数**:

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| userId | long | 是 | 用户ID |
| currentPage | integer | 否 | 当前页，默认1 |
| pageSize | integer | 否 | 页面大小，默认10 |

#### 2.5 查询用户对象标签
- **接口地址**: `GET /api/tag/user/object/{relationObjectId}`
- **接口描述**: 查询用户对特定对象的标签
- **路径参数**:

| 参数名 | 类型 | 描述 |
|--------|------|------|
| relationObjectId | string | 关联对象ID |

- **请求参数**:

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| userId | long | 是 | 用户ID |

#### 2.6 检查用户标签状态
- **接口地址**: `GET /api/tag/user/check`
- **接口描述**: 检查用户是否已打某个标签
- **请求参数**:

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| userId | long | 是 | 用户ID |
| tagId | long | 是 | 标签ID |
| relationObjectId | string | 否 | 关联对象ID |

- **响应示例**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "isTagged": true,
    "userTagId": 10001,
    "weight": 0.85,
    "lastUsedTime": "2023-12-01T15:30:00"
  }
}
```

### 3. 标签统计接口

#### 3.1 查询标签统计
- **接口地址**: `GET /api/tag/statistics/{tagId}`
- **接口描述**: 查询标签统计信息
- **响应示例**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "tagId": 1001,
    "tagName": "React",
    "totalUsageCount": 15230,
    "userCount": 8520,
    "contentCount": 3420,
    "todayUsageCount": 156,
    "weekUsageCount": 1024,
    "monthUsageCount": 4562,
    "hotTrend": {
      "2023-12-01": 156,
      "2023-11-30": 142,
      "2023-11-29": 178
    },
    "avgUsageFrequency": 1.79,
    "lastUpdated": "2023-12-01T16:00:00"
  }
}
```

#### 3.2 查询标签使用排行
- **接口地址**: `GET /api/tag/statistics/ranking`
- **接口描述**: 查询标签使用排行榜
- **请求参数**:

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| limit | integer | 否 | 限制数量，默认10 |

### 4. 标签推荐接口

#### 4.1 用户标签推荐
- **接口地址**: `GET /api/tag/recommend/user/{userId}`
- **接口描述**: 根据用户历史行为推荐标签
- **路径参数**:

| 参数名 | 类型 | 描述 |
|--------|------|------|
| userId | long | 用户ID |

- **请求参数**:

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| limit | integer | 否 | 推荐数量，默认10 |

- **响应示例**:
```json
{
  "code": 200,
  "message": "推荐成功",
  "data": [
    {
      "tagId": 1002,
      "tagName": "Vue",
      "tagType": "SYSTEM",
      "level": "LEVEL_3",
      "recommendScore": 0.92,
      "recommendReason": "基于您对React的兴趣",
      "usageCount": 12450
    }
  ]
}
```

#### 4.2 内容标签推荐
- **接口地址**: `POST /api/tag/recommend/content`
- **接口描述**: 根据内容特征推荐标签
- **请求参数**:

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| content | string | 是 | 内容文本 |
| limit | integer | 否 | 推荐数量，默认10 |

#### 4.3 查询用户兴趣标签
- **接口地址**: `GET /api/tag/recommend/interested/{userId}`
- **接口描述**: 查询用户可能感兴趣的标签
- **请求参数**:

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| excludeTagIds | array | 否 | 排除的标签ID列表 |
| limit | integer | 否 | 推荐数量，默认10 |

#### 4.4 查询趋势标签
- **接口地址**: `GET /api/tag/recommend/trending`
- **接口描述**: 查询趋势热门标签
- **请求参数**:

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| relationType | enum | 否 | 关联类型 |
| timeRange | integer | 否 | 时间范围（天），默认7 |
| limit | integer | 否 | 推荐数量，默认10 |

## 错误码说明

| 错误码 | 错误信息 | 描述 |
|--------|----------|------|
| 40001 | 标签名称不能为空 | 标签名称为必填项 |
| 40002 | 标签已存在 | 同层级下已存在相同名称的标签 |
| 40003 | 父标签不存在 | 指定的父标签ID不存在 |
| 40004 | 标签层级错误 | 标签层级设置不正确 |
| 40005 | 用户ID不能为空 | 用户ID为必填项 |
| 40006 | 标签ID不能为空 | 标签ID为必填项 |
| 40007 | 标签不存在 | 指定的标签不存在 |
| 40008 | 标签已被禁用 | 标签状态为DISABLED |
| 40009 | 重复打标签 | 用户已经对该对象打过相同标签 |
| 40010 | 标签权重超出范围 | 权重值必须在0.0-1.0之间 |
| 50001 | 系统内部错误 | 服务器内部错误 |
| 50002 | 数据库操作失败 | 数据库操作异常 |

## 使用示例

### 1. 用户给文章打标签
```javascript
// 用户对文章ID为"article_123"打标签
const response = await fetch('/api/tag/user/tag', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer ' + token
  },
  body: JSON.stringify({
    userId: 1001,
    tagIds: [5, 1001, 1002], // 前端、React、Vue
    relationType: 'CONTENT',
    relationObjectId: 'article_123',
    weight: 0.9,
    tagSource: 'manual'
  })
});
```

### 2. 查询用户的前端技术标签
```javascript
// 查询用户在前端技术方面的标签
const response = await fetch('/api/tag/user/tags?' + new URLSearchParams({
  userId: 1001,
  relationType: 'USER',
  keyword: '前端',
  minWeight: 0.5
}));
```

### 3. 根据内容推荐标签
```javascript
// 为文章内容推荐合适的标签
const response = await fetch('/api/tag/recommend/content', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    content: 'React是一个用于构建用户界面的JavaScript库...',
    limit: 5
  })
});
```

## 注意事项

### 1. 标签管理
- **标签命名**: 标签名称长度不超过128字符，不允许特殊字符
- **层级限制**: 最多支持三级标签，不能超过层级限制
- **审核机制**: 用户创建的标签可能需要审核才能生效
- **删除限制**: 已被使用的标签不能直接删除，需要先处理关联关系

### 2. 用户打标签
- **权限控制**: 用户只能对自己有权限的内容打标签
- **标签验证**: 打标签前会验证标签的有效性和状态
- **重复处理**: 相同用户对相同对象的相同标签会被去重
- **权重计算**: 系统会根据用户行为自动调整标签权重

### 3. 性能优化
- **缓存机制**: 热门标签和标签树结构会被缓存
- **批量操作**: 支持批量打标签和查询，提高操作效率
- **分页查询**: 大量数据查询请使用分页接口
- **异步处理**: 统计数据更新采用异步处理，可能有延迟

### 4. 数据一致性
- **事务保证**: 标签操作在事务中执行，保证数据一致性
- **乐观锁**: 使用乐观锁防止并发修改冲突
- **软删除**: 标签采用逻辑删除，保证历史数据完整性 
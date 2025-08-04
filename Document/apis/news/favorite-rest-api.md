# 收藏管理 REST API 文档

## 概述

收藏管理 REST API 基于 `favorite-simple.sql` 的单表设计，提供完整的收藏功能服务。通过 HTTP 接口实现收藏的增删改查、统计分析、搜索排序等功能。

**基础信息：**
- 服务路径：`/api/v1/favorite`
- 版本：v2.0.0 (简洁版)
- 认证方式：Token Authentication
- 响应格式：JSON

**支持的收藏类型：**
- `CONTENT`: 内容收藏（小说、漫画、视频等）
- `GOODS`: 商品收藏

---

## 目录

1. [基础收藏操作](#基础收藏操作)
2. [收藏查询](#收藏查询)
3. [收藏列表](#收藏列表)
4. [统计信息](#统计信息)
5. [搜索功能](#搜索功能)
6. [管理功能](#管理功能)
7. [数据模型](#数据模型)
8. [错误码](#错误码)

---

## 基础收藏操作

### 1.1 添加收藏

**接口说明：** 用户收藏内容或商品，支持冗余存储用户和目标对象信息

**请求信息：**
```
POST /api/v1/favorite/add
Content-Type: application/json
```

**请求参数：**
```json
{
  "userId": 1001,
  "favoriteType": "CONTENT",
  "targetId": 2001,
  "targetTitle": "示例内容标题",
  "targetCover": "https://example.com/cover.jpg",
  "targetAuthorId": 3001,
  "userNickname": "示例用户"
}
```

**响应示例：**
```json
{
  "success": true,
  "code": "200",
  "message": "操作成功",
  "data": {
    "id": 10001,
    "userId": 1001,
    "favoriteType": "CONTENT",
    "targetId": 2001,
    "targetTitle": "示例内容标题",
    "targetCover": "https://example.com/cover.jpg",
    "targetAuthorId": 3001,
    "userNickname": "示例用户",
    "status": "active",
    "createTime": "2024-01-01T10:30:00",
    "updateTime": "2024-01-01T10:30:00"
  }
}
```

### 1.2 取消收藏

**接口说明：** 取消收藏，将状态更新为 cancelled

**请求信息：**
```
POST /api/v1/favorite/remove
Content-Type: application/json
```

**请求参数：**
```json
{
  "userId": 1001,
  "favoriteType": "CONTENT",
  "targetId": 2001,
  "cancelReason": "不再感兴趣",
  "operatorId": 1001
}
```

**响应示例：**
```json
{
  "success": true,
  "code": "200",
  "message": "取消收藏成功",
  "data": null
}
```

---

## 收藏查询

### 2.1 检查收藏状态

**接口说明：** 检查用户是否已收藏指定对象

**请求信息：**
```
GET /api/v1/favorite/check?userId=1001&favoriteType=CONTENT&targetId=2001
```

**请求参数：**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Long | 是 | 用户ID |
| favoriteType | String | 是 | 收藏类型 |
| targetId | Long | 是 | 目标ID |

**响应示例：**
```json
{
  "success": true,
  "code": "200",
  "message": "操作成功",
  "data": true
}
```

### 2.2 获取收藏详情

**接口说明：** 获取收藏的详细信息

**请求信息：**
```
GET /api/v1/favorite/detail?userId=1001&favoriteType=CONTENT&targetId=2001
```

**请求参数：**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Long | 是 | 用户ID |
| favoriteType | String | 是 | 收藏类型 |
| targetId | Long | 是 | 目标ID |

**响应示例：**
```json
{
  "success": true,
  "code": "200",
  "message": "操作成功",
  "data": {
    "id": 10001,
    "userId": 1001,
    "favoriteType": "CONTENT",
    "targetId": 2001,
    "targetTitle": "示例内容标题",
    "targetCover": "https://example.com/cover.jpg",
    "targetAuthorId": 3001,
    "userNickname": "示例用户",
    "status": "active",
    "createTime": "2024-01-01T10:30:00",
    "updateTime": "2024-01-01T10:30:00"
  }
}
```

### 2.3 分页查询收藏记录

**接口说明：** 支持多种条件的收藏记录分页查询

**请求信息：**
```
GET /api/v1/favorite/query?userId=1001&favoriteType=CONTENT&currentPage=1&pageSize=20
```

**请求参数：**
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| userId | Long | 否 | - | 用户ID |
| favoriteType | String | 否 | - | 收藏类型 |
| targetId | Long | 否 | - | 目标ID |
| targetTitle | String | 否 | - | 目标标题关键词 |
| targetAuthorId | Long | 否 | - | 目标作者ID |
| userNickname | String | 否 | - | 用户昵称关键词 |
| status | String | 否 | - | 收藏状态 |
| queryType | String | 否 | - | 查询类型 |
| orderBy | String | 否 | - | 排序字段 |
| orderDirection | String | 否 | - | 排序方向 |
| currentPage | Integer | 否 | 1 | 页码 |
| pageSize | Integer | 否 | 20 | 每页大小 |

**响应示例：**
```json
{
  "success": true,
  "code": "200",
  "message": "操作成功",
  "data": {
    "datas": [
      {
        "id": 10001,
        "userId": 1001,
        "favoriteType": "CONTENT",
        "targetId": 2001,
        "targetTitle": "示例内容标题",
        "status": "active",
        "createTime": "2024-01-01T10:30:00"
      }
    ],
    "total": 100,
    "currentPage": 1,
    "pageSize": 20,
    "totalPage": 5
  }
}
```

---

## 收藏列表

### 3.1 获取用户收藏列表

**接口说明：** 查询某用户的所有收藏

**请求信息：**
```
GET /api/v1/favorite/user?userId=1001&favoriteType=CONTENT&currentPage=1&pageSize=20
```

**请求参数：**
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| userId | Long | 是 | - | 用户ID |
| favoriteType | String | 否 | - | 收藏类型（可选） |
| currentPage | Integer | 否 | 1 | 页码 |
| pageSize | Integer | 否 | 20 | 页面大小 |

**响应示例：**
```json
{
  "success": true,
  "code": "200",
  "message": "操作成功",
  "data": {
    "datas": [
      {
        "id": 10001,
        "userId": 1001,
        "favoriteType": "CONTENT",
        "targetId": 2001,
        "targetTitle": "示例内容标题",
        "targetCover": "https://example.com/cover.jpg",
        "status": "active",
        "createTime": "2024-01-01T10:30:00"
      }
    ],
    "total": 50,
    "currentPage": 1,
    "pageSize": 20,
    "totalPage": 3
  }
}
```

### 3.2 获取目标对象收藏列表

**接口说明：** 查询收藏某个对象的所有用户

**请求信息：**
```
GET /api/v1/favorite/target?favoriteType=CONTENT&targetId=2001&currentPage=1&pageSize=20
```

**请求参数：**
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| favoriteType | String | 是 | - | 收藏类型 |
| targetId | Long | 是 | - | 目标ID |
| currentPage | Integer | 否 | 1 | 页码 |
| pageSize | Integer | 否 | 20 | 页面大小 |

---

## 统计信息

### 4.1 获取用户收藏数量

**接口说明：** 统计用户收藏的数量

**请求信息：**
```
GET /api/v1/favorite/user/count?userId=1001&favoriteType=CONTENT
```

**请求参数：**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Long | 是 | 用户ID |
| favoriteType | String | 否 | 收藏类型（可选） |

**响应示例：**
```json
{
  "success": true,
  "code": "200",
  "message": "操作成功",
  "data": 25
}
```

### 4.2 获取目标对象被收藏数量

**接口说明：** 统计某个对象被收藏的次数

**请求信息：**
```
GET /api/v1/favorite/target/count?favoriteType=CONTENT&targetId=2001
```

**请求参数：**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| favoriteType | String | 是 | 收藏类型 |
| targetId | Long | 是 | 目标ID |

**响应示例：**
```json
{
  "success": true,
  "code": "200",
  "message": "操作成功",
  "data": 128
}
```

### 4.3 获取用户收藏统计信息

**接口说明：** 包含各类型收藏数量统计

**请求信息：**
```
GET /api/v1/favorite/user/statistics?userId=1001
```

**响应示例：**
```json
{
  "success": true,
  "code": "200",
  "message": "操作成功",
  "data": {
    "totalCount": 30,
    "contentCount": 25,
    "goodsCount": 5,
    "recentCount": 3,
    "statistics": {
      "thisWeek": 2,
      "thisMonth": 8,
      "thisYear": 30
    }
  }
}
```

### 4.4 批量检查收藏状态

**接口说明：** 检查用户对多个目标对象的收藏状态

**请求信息：**
```
POST /api/v1/favorite/batch-check?userId=1001&favoriteType=CONTENT
Content-Type: application/json
```

**请求参数：**
| 参数 | 类型 | 位置 | 必填 | 说明 |
|------|------|------|------|------|
| userId | Long | Query | 是 | 用户ID |
| favoriteType | String | Query | 是 | 收藏类型 |
| targetIds | List&lt;Long&gt; | Body | 是 | 目标ID列表 |

**请求体示例：**
```json
[2001, 2002, 2003, 2004, 2005]
```

**响应示例：**
```json
{
  "success": true,
  "code": "200",
  "message": "操作成功",
  "data": {
    "2001": true,
    "2002": false,
    "2003": true,
    "2004": false,
    "2005": true
  }
}
```

---

## 搜索功能

### 5.1 根据标题搜索收藏

**接口说明：** 根据收藏对象标题进行模糊搜索

**请求信息：**
```
GET /api/v1/favorite/search?userId=1001&titleKeyword=示例&favoriteType=CONTENT&currentPage=1&pageSize=20
```

**请求参数：**
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| userId | Long | 是 | - | 用户ID |
| titleKeyword | String | 是 | - | 标题关键词 |
| favoriteType | String | 否 | - | 收藏类型（可选） |
| currentPage | Integer | 否 | 1 | 页码 |
| pageSize | Integer | 否 | 20 | 页面大小 |

**响应示例：**
```json
{
  "success": true,
  "code": "200",
  "message": "操作成功",
  "data": {
    "datas": [
      {
        "id": 10001,
        "userId": 1001,
        "favoriteType": "CONTENT",
        "targetId": 2001,
        "targetTitle": "示例内容标题",
        "targetCover": "https://example.com/cover.jpg",
        "status": "active",
        "createTime": "2024-01-01T10:30:00"
      }
    ],
    "total": 10,
    "currentPage": 1,
    "pageSize": 20,
    "totalPage": 1
  }
}
```

### 5.2 获取热门收藏对象

**接口说明：** 查询被收藏次数最多的对象

**请求信息：**
```
GET /api/v1/favorite/popular?favoriteType=CONTENT&currentPage=1&pageSize=20
```

**请求参数：**
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| favoriteType | String | 是 | - | 收藏类型 |
| currentPage | Integer | 否 | 1 | 页码 |
| pageSize | Integer | 否 | 20 | 页面大小 |

---

## 管理功能

### 6.1 清理已取消的收藏记录

**接口说明：** 物理删除 cancelled 状态的记录（管理员功能）

**请求信息：**
```
POST /api/v1/favorite/clean?days=30
```

**请求参数：**
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| days | Integer | 否 | 30 | 保留天数 |

**响应示例：**
```json
{
  "success": true,
  "code": "200",
  "message": "操作成功",
  "data": 15
}
```

### 6.2 更新用户信息

**接口说明：** 当用户信息变更时，同步更新收藏表中的冗余信息

**请求信息：**
```
PUT /api/v1/favorite/user/info?userId=1001&nickname=新昵称
```

### 6.3 更新目标对象信息

**接口说明：** 当目标对象信息变更时，同步更新收藏表中的冗余信息

**请求信息：**
```
PUT /api/v1/favorite/target/info?favoriteType=CONTENT&targetId=2001&title=新标题
```

### 6.4 根据作者查询收藏作品

**接口说明：** 查询某作者的作品被收藏情况

**请求信息：**
```
GET /api/v1/favorite/author?targetAuthorId=3001&favoriteType=CONTENT&currentPage=1&pageSize=20
```

### 6.5 检查收藏关系是否存在

**接口说明：** 包括已取消的收藏关系

**请求信息：**
```
GET /api/v1/favorite/relation/exists?userId=1001&favoriteType=CONTENT&targetId=2001
```

### 6.6 重新激活已取消的收藏

**接口说明：** 将 cancelled 状态的收藏重新设置为 active

**请求信息：**
```
POST /api/v1/favorite/reactivate?userId=1001&favoriteType=CONTENT&targetId=2001
```

### 6.7 验证收藏请求参数

**接口说明：** 校验请求参数的有效性

**请求信息：**
```
POST /api/v1/favorite/validate
Content-Type: application/json
```

### 6.8 检查是否可以收藏

**接口说明：** 检查业务规则是否允许收藏

**请求信息：**
```
GET /api/v1/favorite/can-favorite?userId=1001&favoriteType=CONTENT&targetId=2001
```

---

## 数据模型

### FavoriteResponse

```json
{
  "id": "Long - 收藏记录ID",
  "userId": "Long - 用户ID",
  "favoriteType": "String - 收藏类型(CONTENT/GOODS)",
  "targetId": "Long - 目标对象ID",
  "targetTitle": "String - 目标对象标题",
  "targetCover": "String - 目标对象封面",
  "targetAuthorId": "Long - 目标对象作者ID",
  "userNickname": "String - 用户昵称",
  "status": "String - 状态(active/cancelled)",
  "createTime": "LocalDateTime - 创建时间",
  "updateTime": "LocalDateTime - 更新时间"
}
```

### FavoriteCreateRequest

```json
{
  "userId": "Long - 用户ID",
  "favoriteType": "String - 收藏类型",
  "targetId": "Long - 目标对象ID",
  "targetTitle": "String - 目标对象标题",
  "targetCover": "String - 目标对象封面",
  "targetAuthorId": "Long - 目标对象作者ID",
  "userNickname": "String - 用户昵称"
}
```

### FavoriteDeleteRequest

```json
{
  "userId": "Long - 用户ID",
  "favoriteType": "String - 收藏类型",
  "targetId": "Long - 目标对象ID",
  "cancelReason": "String - 取消原因",
  "operatorId": "Long - 操作人ID"
}
```

### PageResponse&lt;T&gt;

```json
{
  "datas": "List<T> - 数据列表",
  "total": "Long - 总记录数",
  "currentPage": "Integer - 当前页码",
  "pageSize": "Integer - 每页大小",
  "totalPage": "Integer - 总页数"
}
```

---

## 错误码

| 错误码 | 说明 |
|--------|------|
| ADD_FAVORITE_ERROR | 添加收藏失败 |
| REMOVE_FAVORITE_ERROR | 取消收藏失败 |
| CHECK_FAVORITE_ERROR | 检查收藏状态失败 |
| GET_FAVORITE_ERROR | 获取收藏详情失败 |
| QUERY_FAVORITES_ERROR | 查询收藏记录失败 |
| GET_USER_FAVORITES_ERROR | 获取用户收藏列表失败 |
| GET_TARGET_FAVORITES_ERROR | 获取目标收藏列表失败 |
| GET_USER_COUNT_ERROR | 获取用户收藏数量失败 |
| GET_TARGET_COUNT_ERROR | 获取目标被收藏数量失败 |
| GET_USER_STATISTICS_ERROR | 获取用户收藏统计失败 |
| BATCH_CHECK_ERROR | 批量检查收藏状态失败 |
| SEARCH_FAVORITES_ERROR | 搜索收藏失败 |
| GET_POPULAR_ERROR | 获取热门收藏失败 |
| CLEAN_FAVORITES_ERROR | 清理收藏记录失败 |
| UPDATE_USER_INFO_ERROR | 更新用户信息失败 |
| UPDATE_TARGET_INFO_ERROR | 更新目标对象信息失败 |
| GET_FAVORITES_BY_AUTHOR_ERROR | 查询作者收藏作品失败 |
| CHECK_RELATION_ERROR | 检查收藏关系失败 |
| REACTIVATE_ERROR | 重新激活收藏失败 |
| VALIDATION_ERROR | 验证收藏请求失败 |
| CHECK_PERMISSION_ERROR | 检查收藏权限失败 |

---

## 使用示例

### JavaScript 示例

```javascript
// 添加收藏
const addFavorite = async (userId, favoriteType, targetId) => {
  const response = await fetch('/api/v1/favorite/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + token
    },
    body: JSON.stringify({
      userId,
      favoriteType,
      targetId,
      targetTitle: '示例标题',
      userNickname: '用户昵称'
    })
  });
  return response.json();
};

// 检查收藏状态
const checkFavorite = async (userId, favoriteType, targetId) => {
  const response = await fetch(
    `/api/v1/favorite/check?userId=${userId}&favoriteType=${favoriteType}&targetId=${targetId}`,
    {
      headers: {
        'Authorization': 'Bearer ' + token
      }
    }
  );
  return response.json();
};

// 获取用户收藏列表
const getUserFavorites = async (userId, favoriteType, page = 1, size = 20) => {
  const params = new URLSearchParams({
    userId,
    currentPage: page,
    pageSize: size
  });
  if (favoriteType) params.append('favoriteType', favoriteType);
  
  const response = await fetch(`/api/v1/favorite/user?${params}`, {
    headers: {
      'Authorization': 'Bearer ' + token
    }
  });
  return response.json();
};
```

### cURL 示例

```bash
# 添加收藏
curl -X POST "http://localhost:8080/api/v1/favorite/add" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-token" \
  -d '{
    "userId": 1001,
    "favoriteType": "CONTENT",
    "targetId": 2001,
    "targetTitle": "示例标题"
  }'

# 检查收藏状态
curl "http://localhost:8080/api/v1/favorite/check?userId=1001&favoriteType=CONTENT&targetId=2001" \
  -H "Authorization: Bearer your-token"

# 获取用户收藏列表
curl "http://localhost:8080/api/v1/favorite/user?userId=1001&currentPage=1&pageSize=20" \
  -H "Authorization: Bearer your-token"
```

---

## 注意事项

1. **认证要求**: 所有接口都需要有效的认证 Token
2. **分页限制**: 单次查询最大返回 100 条记录
3. **频率限制**: 建议每秒不超过 10 次请求
4. **数据一致性**: 涉及状态变更的操作具有事务保障
5. **缓存策略**: 查询接口具有分布式缓存，数据实时性为秒级
6. **收藏类型**: 目前支持 CONTENT 和 GOODS 两种类型
7. **冗余字段**: 支持存储目标对象和用户的快照信息，提高查询性能

---

**最后更新时间**: 2024-01-01  
**文档版本**: v2.0.0
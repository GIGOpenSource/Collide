# 点赞模块 REST API 文档

## 接口概述

点赞模块提供完整的点赞功能，支持对内容、评论、动态的点赞操作，包含分页查询、统计分析、批量操作等功能。

**基础路径**: `/api/v1/like`

**版本**: v2.0.0 (MySQL 8.0 优化版)

**特性**:
- ✅ 支持内容、评论、动态三种类型点赞
- ✅ 用户、目标对象、作者三个维度查询
- ✅ 时间范围查询和批量操作
- ✅ 分布式缓存优化
- ✅ MySQL 8.0 索引优化

---

## 🎯 核心功能

### 1. 添加点赞

**接口地址**: `POST /api/v1/like/add`

**接口描述**: 用户对内容、评论或动态进行点赞

**请求参数**:
```json
{
    "userId": 123,
    "likeType": "CONTENT",
    "targetId": 456,
    "targetTitle": "示例内容标题",
    "targetAuthorId": 789,
    "userNickname": "用户昵称",
    "userAvatar": "https://example.com/avatar.jpg"
}
```

**参数说明**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Long | ✅ | 点赞用户ID |
| likeType | String | ✅ | 点赞类型: CONTENT/COMMENT/DYNAMIC |
| targetId | Long | ✅ | 目标对象ID |
| targetTitle | String | ❌ | 目标对象标题（冗余） |
| targetAuthorId | Long | ❌ | 目标对象作者ID（冗余） |
| userNickname | String | ❌ | 用户昵称（冗余） |
| userAvatar | String | ❌ | 用户头像（冗余） |

**响应示例**:
```json
{
    "success": true,
    "code": "200",
    "message": "操作成功",
    "data": {
        "id": 1001,
        "userId": 123,
        "likeType": "CONTENT",
        "targetId": 456,
        "targetTitle": "示例内容标题",
        "targetAuthorId": 789,
        "userNickname": "用户昵称",
        "userAvatar": "https://example.com/avatar.jpg",
        "status": "active",
        "createTime": "2024-01-16T10:30:00",
        "updateTime": "2024-01-16T10:30:00"
    }
}
```

---

### 2. 取消点赞

**接口地址**: `POST /api/v1/like/cancel`

**接口描述**: 取消用户的点赞

**请求参数**:
```json
{
    "userId": 123,
    "likeType": "CONTENT",
    "targetId": 456
}
```

**参数说明**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Long | ✅ | 点赞用户ID |
| likeType | String | ✅ | 点赞类型: CONTENT/COMMENT/DYNAMIC |
| targetId | Long | ✅ | 目标对象ID |

**响应示例**:
```json
{
    "success": true,
    "code": "200",
    "message": "操作成功",
    "data": null
}
```

---

### 3. 切换点赞状态

**接口地址**: `POST /api/v1/like/toggle`

**接口描述**: 智能切换点赞状态，如果已点赞则取消，如果未点赞则添加

**请求参数**:
```json
{
    "userId": 123,
    "likeType": "CONTENT",
    "targetId": 456,
    "targetTitle": "示例内容标题",
    "targetAuthorId": 789,
    "userNickname": "用户昵称",
    "userAvatar": "https://example.com/avatar.jpg"
}
```

**响应示例（点赞操作）**:
```json
{
    "success": true,
    "code": "200",
    "message": "操作成功",
    "data": {
        "id": 1001,
        "userId": 123,
        "likeType": "CONTENT",
        "targetId": 456,
        "status": "active",
        "createTime": "2024-01-16T10:30:00"
    }
}
```

**响应示例（取消操作）**:
```json
{
    "success": true,
    "code": "200",
    "message": "操作成功",
    "data": null
}
```

---

### 4. 检查点赞状态

**接口地址**: `POST /api/v1/like/check`

**接口描述**: 检查用户是否已对目标对象点赞

**请求参数**:
```json
{
    "userId": 123,
    "likeType": "CONTENT",
    "targetId": 456
}
```

**响应示例**:
```json
{
    "success": true,
    "code": "200",
    "message": "操作成功",
    "data": true
}
```

---

## 📋 查询功能

### 5. 分页查询用户点赞记录

**接口地址**: `POST /api/v1/like/user/likes`

**接口描述**: 查询指定用户的点赞记录列表

**请求参数**:
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| userId | Long | ✅ | - | 用户ID |
| likeType | String | ❌ | null | 点赞类型筛选 |
| status | String | ❌ | null | 状态筛选: active/cancelled |
| currentPage | Integer | ❌ | 1 | 当前页码 |
| pageSize | Integer | ❌ | 10 | 页面大小 |

**URL示例**: 
```
POST /api/v1/like/user/likes?userId=123&likeType=CONTENT&currentPage=1&pageSize=10
```

**响应示例**:
```json
{
    "success": true,
    "code": "200",
    "message": "操作成功",
    "data": {
        "datas": [
            {
                "id": 1001,
                "userId": 123,
                "likeType": "CONTENT",
                "targetId": 456,
                "targetTitle": "示例内容标题",
                "status": "active",
                "createTime": "2024-01-16T10:30:00"
            }
        ],
        "total": 50,
        "currentPage": 1,
        "pageSize": 10,
        "totalPage": 5
    }
}
```

---

### 6. 分页查询目标对象点赞记录

**接口地址**: `POST /api/v1/like/target/likes`

**接口描述**: 查询指定目标对象的点赞记录列表

**请求参数**:
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| targetId | Long | ✅ | - | 目标对象ID |
| likeType | String | ✅ | - | 点赞类型 |
| status | String | ❌ | null | 状态筛选 |
| currentPage | Integer | ❌ | 1 | 当前页码 |
| pageSize | Integer | ❌ | 10 | 页面大小 |

**URL示例**: 
```
POST /api/v1/like/target/likes?targetId=456&likeType=CONTENT&currentPage=1&pageSize=10
```

**响应格式**: 同用户点赞记录查询

---

### 7. 分页查询作者作品点赞记录

**接口地址**: `POST /api/v1/like/author/likes`

**接口描述**: 查询指定作者作品的点赞记录列表

**请求参数**:
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| targetAuthorId | Long | ✅ | - | 作品作者ID |
| likeType | String | ❌ | null | 点赞类型筛选 |
| status | String | ❌ | null | 状态筛选 |
| currentPage | Integer | ❌ | 1 | 当前页码 |
| pageSize | Integer | ❌ | 10 | 页面大小 |

**URL示例**: 
```
POST /api/v1/like/author/likes?targetAuthorId=789&likeType=CONTENT&currentPage=1&pageSize=10
```

**响应格式**: 同用户点赞记录查询

---

## 📊 统计功能

### 8. 统计目标对象点赞数量

**接口地址**: `GET /api/v1/like/target/{targetId}/count`

**接口描述**: 统计指定目标对象的点赞数量

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| targetId | Long | ✅ | 目标对象ID |

**查询参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| likeType | String | ✅ | 点赞类型 |

**URL示例**: 
```
GET /api/v1/like/target/456/count?likeType=CONTENT
```

**响应示例**:
```json
{
    "success": true,
    "code": "200",
    "message": "操作成功",
    "data": 156
}
```

---

### 9. 统计用户点赞数量

**接口地址**: `GET /api/v1/like/user/{userId}/count`

**接口描述**: 统计指定用户的点赞数量

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Long | ✅ | 用户ID |

**查询参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| likeType | String | ❌ | 点赞类型筛选 |

**URL示例**: 
```
GET /api/v1/like/user/123/count?likeType=CONTENT
```

**响应示例**:
```json
{
    "success": true,
    "code": "200",
    "message": "操作成功",
    "data": 42
}
```

---

### 10. 统计作者作品被点赞数量

**接口地址**: `GET /api/v1/like/author/{targetAuthorId}/count`

**接口描述**: 统计指定作者作品的被点赞数量

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| targetAuthorId | Long | ✅ | 作品作者ID |

**查询参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| likeType | String | ❌ | 点赞类型筛选 |

**URL示例**: 
```
GET /api/v1/like/author/789/count?likeType=CONTENT
```

**响应示例**:
```json
{
    "success": true,
    "code": "200",
    "message": "操作成功",
    "data": 1250
}
```

---

## 🔍 扩展功能

### 11. 查询时间范围内的点赞记录

**接口地址**: `GET /api/v1/like/time-range`

**接口描述**: 查询指定时间范围内的点赞记录

**查询参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| startTime | DateTime | ✅ | 开始时间 (ISO 8601格式) |
| endTime | DateTime | ✅ | 结束时间 (ISO 8601格式) |
| likeType | String | ❌ | 点赞类型筛选 |
| status | String | ❌ | 状态筛选 |

**URL示例**: 
```
GET /api/v1/like/time-range?startTime=2024-01-01T00:00:00&endTime=2024-01-31T23:59:59&likeType=CONTENT
```

**响应示例**:
```json
{
    "success": true,
    "code": "200",
    "message": "操作成功",
    "data": [
        {
            "id": 1001,
            "userId": 123,
            "likeType": "CONTENT",
            "targetId": 456,
            "status": "active",
            "createTime": "2024-01-16T10:30:00"
        }
    ]
}
```

---

### 12. 批量检查点赞状态

**接口地址**: `POST /api/v1/like/batch/check`

**接口描述**: 批量检查用户对多个目标对象的点赞状态

**请求参数**:
```json
{
    "userId": 123,
    "likeType": "CONTENT",
    "targetIds": [456, 457, 458, 459]
}
```

**参数说明**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Long | ✅ | 用户ID |
| likeType | String | ✅ | 点赞类型 |
| targetIds | List\<Long\> | ✅ | 目标对象ID列表 |

**响应示例**:
```json
{
    "success": true,
    "code": "200",
    "message": "操作成功",
    "data": {
        "456": true,
        "457": false,
        "458": true,
        "459": false
    }
}
```

---

## 🚨 错误代码

| 错误代码 | 说明 | HTTP状态码 |
|----------|------|------------|
| `USER_NOT_FOUND` | 用户不存在 | 400 |
| `AUTHOR_NOT_FOUND` | 作者不存在 | 400 |
| `LIKE_PARAM_ERROR` | 点赞参数验证失败 | 400 |
| `LIKE_ADD_ERROR` | 添加点赞失败 | 500 |
| `LIKE_CANCEL_ERROR` | 取消点赞失败 | 500 |
| `LIKE_TOGGLE_ERROR` | 切换点赞状态失败 | 500 |
| `LIKE_CHECK_ERROR` | 检查点赞状态失败 | 500 |
| `USER_LIKES_QUERY_ERROR` | 查询用户点赞记录失败 | 500 |
| `TARGET_LIKES_QUERY_ERROR` | 查询目标对象点赞记录失败 | 500 |
| `AUTHOR_LIKES_QUERY_ERROR` | 查询作者作品点赞记录失败 | 500 |
| `TARGET_LIKE_COUNT_ERROR` | 统计目标对象点赞数量失败 | 500 |
| `USER_LIKE_COUNT_ERROR` | 统计用户点赞数量失败 | 500 |
| `AUTHOR_LIKE_COUNT_ERROR` | 统计作者作品被点赞数量失败 | 500 |
| `TIME_RANGE_QUERY_ERROR` | 查询时间范围内的点赞记录失败 | 500 |
| `BATCH_CHECK_ERROR` | 批量检查点赞状态失败 | 500 |

---

## 📈 性能优化

### 缓存策略
- ✅ **点赞状态缓存**: 30分钟缓存，减少重复查询
- ✅ **点赞数量缓存**: 15分钟缓存，提升统计接口性能
- ✅ **批量状态缓存**: 10分钟缓存，优化批量查询
- ✅ **分页记录缓存**: 5分钟缓存，提升列表查询效率

### 数据库优化
- ✅ **MySQL 8.0 索引**: 13个专门优化的复合索引
- ✅ **覆盖索引**: 减少回表查询，提升查询效率
- ✅ **降序索引**: 优化时间排序查询
- ✅ **前缀索引**: 优化字符串字段查询

### 接口性能指标
- 🎯 **核心接口**: 响应时间 < 100ms
- 🎯 **查询接口**: 响应时间 < 200ms
- 🎯 **统计接口**: 响应时间 < 150ms
- 🎯 **批量接口**: 响应时间 < 300ms

---

## 📝 使用说明

1. **认证**: 所有接口需要用户认证
2. **限流**: 每用户每分钟最多1000次请求
3. **版本**: 通过Accept头或URL参数指定版本
4. **编码**: 统一使用UTF-8编码
5. **时间格式**: ISO 8601格式 (YYYY-MM-DDTHH:mm:ss)

## 🔗 相关文档

- [点赞Facade接口文档](../facade/like-facade-api.md)
- [数据库设计文档](../database/like-schema.md)
- [缓存策略文档](../cache/like-cache.md)

---

**最后更新**: 2024-01-16  
**文档版本**: v2.0.0  
**维护团队**: GIG Team
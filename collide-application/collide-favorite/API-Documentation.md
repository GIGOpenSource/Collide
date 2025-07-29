# Collide 收藏服务 API 文档

## 概述

Collide 收藏服务提供完整的内容收藏功能，支持多种类型的收藏对象，包括内容、用户、动态、评论、商品等。提供收藏管理、查询统计、个性化推荐等核心功能。

**服务版本**: v2.0  
**基础路径**: `/api/favorite`  
**设计理念**: 统一收藏体系，支持多元化收藏类型，提供智能收藏管理和个性化体验

---

## 收藏基础功能 API

### 1. 添加收藏
**接口路径**: `POST /api/favorite/add`  
**接口描述**: 用户收藏内容、用户、动态等，支持多种收藏类型

#### 请求参数
```json
{
  "userId": 12345,                     // 必填，用户ID
  "favoriteType": "CONTENT",           // 必填，收藏类型（CONTENT/USER/DYNAMIC/COMMENT/GOODS）
  "targetId": 67890,                   // 必填，目标对象ID
  "targetTitle": "Java基础教程",         // 可选，目标对象标题
  "targetDescription": "详细的Java教程", // 可选，目标对象描述
  "targetUrl": "https://example.com/article/123", // 可选，目标对象URL
  "targetCoverUrl": "https://example.com/cover.jpg", // 可选，目标对象封面
  "folderId": 1001,                    // 可选，收藏夹ID
  "tags": ["Java", "编程", "教程"],      // 可选，收藏标签
  "remark": "好文章，值得收藏"           // 可选，收藏备注
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "收藏成功",
  "data": {
    "id": 98765,
    "userId": 12345,
    "favoriteType": "CONTENT",
    "targetId": 67890,
    "targetTitle": "Java基础教程",
    "targetDescription": "详细的Java教程",
    "targetUrl": "https://example.com/article/123",
    "targetCoverUrl": "https://example.com/cover.jpg",
    "folderId": 1001,
    "folderName": "编程学习",
    "tags": ["Java", "编程", "教程"],
    "remark": "好文章，值得收藏",
    "status": "active",
    "createTime": "2024-01-16T10:30:00",
    "updateTime": "2024-01-16T10:30:00"
  }
}
```

**失败响应 (400)**:
```json
{
  "success": false,
  "responseCode": "ALREADY_FAVORITED",
  "responseMessage": "您已经收藏了该内容"
}
```

---

### 2. 取消收藏
**接口路径**: `POST /api/favorite/remove`  
**接口描述**: 用户取消收藏，移除收藏关系

#### 请求参数
```json
{
  "userId": 12345,           // 必填，用户ID
  "favoriteType": "CONTENT", // 必填，收藏类型
  "targetId": 67890          // 必填，目标对象ID
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "取消收藏成功"
}
```

**失败响应 (400)**:
```json
{
  "success": false,
  "responseCode": "NOT_FAVORITED",
  "responseMessage": "您尚未收藏该内容"
}
```

---

## 收藏查询功能 API

### 3. 检查收藏状态
**接口路径**: `GET /api/favorite/check`  
**接口描述**: 检查用户是否已收藏指定内容

#### 查询参数
- **userId** (query): 用户ID，必填
- **favoriteType** (query): 收藏类型，必填
- **targetId** (query): 目标ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": true
}
```

---

### 4. 获取收藏详情
**接口路径**: `GET /api/favorite/detail`  
**接口描述**: 获取收藏的详细信息

#### 查询参数
- **userId** (query): 用户ID，必填
- **favoriteType** (query): 收藏类型，必填
- **targetId** (query): 目标ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": {
    "id": 98765,
    "userId": 12345,
    "favoriteType": "CONTENT",
    "targetId": 67890,
    "targetTitle": "Java基础教程",
    "targetDescription": "详细的Java教程",
    "targetUrl": "https://example.com/article/123",
    "targetCoverUrl": "https://example.com/cover.jpg",
    "folderId": 1001,
    "folderName": "编程学习",
    "tags": ["Java", "编程", "教程"],
    "remark": "好文章，值得收藏",
    "status": "active",
    "createTime": "2024-01-16T10:30:00",
    "updateTime": "2024-01-16T10:30:00"
  }
}
```

---

### 5. 分页查询收藏记录
**接口路径**: `GET /api/favorite/query`  
**接口描述**: 支持多种条件的收藏记录分页查询

#### 查询参数
- **userId** (query): 用户ID，可选
- **favoriteType** (query): 收藏类型，可选
- **targetId** (query): 目标ID，可选
- **status** (query): 收藏状态，可选
- **page** (query): 页码，默认1
- **size** (query): 每页大小，默认20

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": {
    "total": 156,
    "pageNum": 1,
    "pageSize": 20,
    "pages": 8,
    "list": [
      {
        "id": 98765,
        "userId": 12345,
        "favoriteType": "CONTENT",
        "targetId": 67890,
        "targetTitle": "Java基础教程",
        "targetCoverUrl": "https://example.com/cover.jpg",
        "folderId": 1001,
        "folderName": "编程学习",
        "createTime": "2024-01-16T10:30:00",
        "status": "active"
      }
    ]
  }
}
```

---

## 收藏列表功能 API

### 6. 获取用户收藏列表
**接口路径**: `GET /api/favorite/user`  
**接口描述**: 获取用户的收藏分页列表，支持按类型筛选

#### 查询参数
- **userId** (query): 用户ID，必填
- **favoriteType** (query): 收藏类型，可选
- **page** (query): 页码，默认1
- **size** (query): 每页大小，默认20

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": {
    "total": 356,
    "pageNum": 1,
    "pageSize": 20,
    "pages": 18,
    "list": [
      {
        "id": 98765,
        "favoriteType": "CONTENT",
        "targetId": 67890,
        "targetTitle": "Java基础教程",
        "targetDescription": "详细的Java教程",
        "targetUrl": "https://example.com/article/123",
        "targetCoverUrl": "https://example.com/cover.jpg",
        "targetAuthor": "技术专家",
        "targetCreateTime": "2024-01-15T10:30:00",
        "folderId": 1001,
        "folderName": "编程学习",
        "tags": ["Java", "编程", "教程"],
        "remark": "好文章，值得收藏",
        "createTime": "2024-01-16T10:30:00",
        "readStatus": false,
        "likeCount": 125,
        "commentCount": 23
      }
    ]
  }
}
```

---

### 7. 获取内容收藏用户列表
**接口路径**: `GET /api/favorite/target`  
**接口描述**: 获取收藏了指定内容的用户分页列表

#### 查询参数
- **favoriteType** (query): 收藏类型，必填
- **targetId** (query): 目标内容ID，必填
- **page** (query): 页码，默认1
- **size** (query): 每页大小，默认20

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": {
    "total": 1250,
    "pageNum": 1,
    "pageSize": 20,
    "pages": 63,
    "list": [
      {
        "id": 98765,
        "userId": 12345,
        "username": "user123",
        "nickname": "小明",
        "avatar": "https://avatar.example.com/user123.jpg",
        "favoriteType": "CONTENT",
        "targetId": 67890,
        "folderId": 1001,
        "folderName": "编程学习",
        "remark": "好文章，值得收藏",
        "createTime": "2024-01-16T10:30:00"
      }
    ]
  }
}
```

---

## 收藏统计功能 API

### 8. 获取用户收藏统计
**接口路径**: `GET /api/favorite/user/statistics`  
**接口描述**: 获取用户的收藏统计信息，包括各种类型的收藏数量

#### 查询参数
- **userId** (query): 用户ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": {
    "userId": 12345,
    "totalFavorites": 1250,
    "favoritesByType": {
      "CONTENT": 856,
      "USER": 125,
      "DYNAMIC": 189,
      "COMMENT": 45,
      "GOODS": 35
    },
    "totalFolders": 12,
    "newFavoritesToday": 8,
    "newFavoritesThisWeek": 45,
    "mostUsedType": "CONTENT",
    "averageFavoritesPerDay": 12.5,
    "lastFavoriteTime": "2024-01-16T15:30:00",
    "readProgress": 0.75
  }
}
```

---

### 9. 获取用户收藏数量
**接口路径**: `GET /api/favorite/user/count`  
**接口描述**: 获取用户的收藏数量统计

#### 查询参数
- **userId** (query): 用户ID，必填
- **favoriteType** (query): 收藏类型，可选

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": 856
}
```

---

### 10. 获取内容被收藏数量
**接口路径**: `GET /api/favorite/target/count`  
**接口描述**: 获取指定内容的被收藏数量统计

#### 查询参数
- **favoriteType** (query): 收藏类型，必填
- **targetId** (query): 目标内容ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": 1250
}
```

---

## 收藏夹管理 API

### 11. 创建收藏夹
**接口路径**: `POST /api/favorite/folder`  
**接口描述**: 创建新的收藏夹，用于分类管理收藏内容

#### 请求参数
```json
{
  "userId": 12345,                 // 必填，用户ID
  "name": "编程学习",               // 必填，收藏夹名称
  "description": "编程相关的文章收藏", // 可选，收藏夹描述
  "coverUrl": "https://example.com/folder-cover.jpg", // 可选，收藏夹封面
  "isPrivate": false,              // 可选，是否私有（默认false）
  "sort": 10                       // 可选，排序值（默认0）
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "创建成功",
  "data": {
    "id": 1001,
    "userId": 12345,
    "name": "编程学习",
    "description": "编程相关的文章收藏",
    "coverUrl": "https://example.com/folder-cover.jpg",
    "isPrivate": false,
    "sort": 10,
    "itemCount": 0,
    "createTime": "2024-01-16T10:30:00",
    "updateTime": "2024-01-16T10:30:00"
  }
}
```

---

### 12. 获取用户收藏夹列表
**接口路径**: `GET /api/favorite/folders`  
**接口描述**: 获取用户的收藏夹列表

#### 查询参数
- **userId** (query): 用户ID，必填
- **includePrivate** (query): 是否包含私有收藏夹，默认true

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": [
    {
      "id": 1001,
      "name": "编程学习",
      "description": "编程相关的文章收藏",
      "coverUrl": "https://example.com/folder-cover.jpg",
      "isPrivate": false,
      "sort": 10,
      "itemCount": 156,
      "lastUpdateTime": "2024-01-16T15:30:00",
      "createTime": "2024-01-16T10:30:00"
    },
    {
      "id": 1002,
      "name": "技术文档",
      "description": "API文档和技术资料",
      "isPrivate": true,
      "sort": 20,
      "itemCount": 89,
      "lastUpdateTime": "2024-01-15T14:20:00",
      "createTime": "2024-01-15T10:00:00"
    }
  ]
}
```

---

## 高级功能 API

### 13. 移动收藏到收藏夹
**接口路径**: `POST /api/favorite/move`  
**接口描述**: 将收藏移动到指定收藏夹

#### 请求参数
```json
{
  "favoriteId": 98765,    // 必填，收藏ID
  "targetFolderId": 1002, // 必填，目标收藏夹ID
  "userId": 12345         // 必填，用户ID（权限验证）
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "移动成功"
}
```

---

### 14. 批量操作收藏
**接口路径**: `POST /api/favorite/batch`  
**接口描述**: 批量移动、删除或标记收藏

#### 请求参数
```json
{
  "favoriteIds": [98765, 98766, 98767], // 必填，收藏ID列表
  "operation": "move",                   // 必填，操作类型（move/delete/read/unread）
  "targetFolderId": 1002,               // 可选，目标收藏夹ID（move操作时必填）
  "userId": 12345                       // 必填，用户ID（权限验证）
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "批量操作成功",
  "data": {
    "successCount": 3,
    "failedCount": 0,
    "failedItems": []
  }
}
```

---

### 15. 搜索收藏
**接口路径**: `GET /api/favorite/search`  
**接口描述**: 在用户收藏中搜索内容

#### 查询参数
- **userId** (query): 用户ID，必填
- **keyword** (query): 搜索关键词，必填
- **favoriteType** (query): 收藏类型，可选
- **folderId** (query): 收藏夹ID，可选
- **page** (query): 页码，默认1
- **size** (query): 每页大小，默认20

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "搜索成功",
  "data": {
    "total": 25,
    "pageNum": 1,
    "pageSize": 20,
    "pages": 2,
    "keyword": "Java",
    "list": [
      {
        "id": 98765,
        "favoriteType": "CONTENT",
        "targetId": 67890,
        "targetTitle": "Java基础教程",
        "targetDescription": "详细的Java教程",
        "matchedFields": ["title", "tags"],
        "highlightTitle": "<em>Java</em>基础教程",
        "createTime": "2024-01-16T10:30:00"
      }
    ]
  }
}
```

---

## 错误码说明

| 错误码 | 错误描述 | 解决方案 |
|--------|----------|----------|
| TARGET_NOT_FOUND | 目标对象不存在 | 检查目标ID是否正确 |
| ALREADY_FAVORITED | 已经收藏该内容 | 内容已在收藏列表中 |
| NOT_FAVORITED | 尚未收藏该内容 | 无法取消未收藏的内容 |
| FOLDER_NOT_FOUND | 收藏夹不存在 | 检查收藏夹ID是否正确 |
| FOLDER_NAME_EXISTS | 收藏夹名称已存在 | 使用不同的收藏夹名称 |
| FAVORITE_LIMIT_EXCEEDED | 收藏数量超出限制 | 减少收藏数量或升级账户 |
| FOLDER_LIMIT_EXCEEDED | 收藏夹数量超出限制 | 减少收藏夹数量或升级账户 |
| FAVORITE_PERMISSION_DENIED | 无收藏权限 | 检查用户权限或账户状态 |

---

## 数据模型

### FavoriteResponse
```typescript
interface FavoriteResponse {
  id: number;                     // 收藏ID
  userId: number;                 // 用户ID
  favoriteType: string;           // 收藏类型
  targetId: number;               // 目标对象ID
  targetTitle?: string;           // 目标对象标题
  targetDescription?: string;     // 目标对象描述
  targetUrl?: string;             // 目标对象URL
  targetCoverUrl?: string;        // 目标对象封面
  targetAuthor?: string;          // 目标对象作者
  targetCreateTime?: string;      // 目标对象创建时间
  folderId?: number;              // 收藏夹ID
  folderName?: string;            // 收藏夹名称
  tags?: string[];                // 收藏标签
  remark?: string;                // 收藏备注
  status: string;                 // 收藏状态（active/deleted）
  readStatus?: boolean;           // 阅读状态
  createTime: string;             // 创建时间（ISO 8601格式）
  updateTime: string;             // 更新时间（ISO 8601格式）
  likeCount?: number;             // 点赞数
  commentCount?: number;          // 评论数
}
```

### FavoriteFolder
```typescript
interface FavoriteFolder {
  id: number;                     // 收藏夹ID
  userId: number;                 // 用户ID
  name: string;                   // 收藏夹名称
  description?: string;           // 收藏夹描述
  coverUrl?: string;              // 收藏夹封面
  isPrivate: boolean;             // 是否私有
  sort: number;                   // 排序值
  itemCount: number;              // 收藏项数量
  createTime: string;             // 创建时间
  updateTime: string;             // 更新时间
  lastUpdateTime?: string;        // 最后更新时间
}
```

---

## 使用示例

### 收藏内容流程
```javascript
// 1. 检查是否已收藏
const isFavorited = await checkFavoriteStatus(12345, "CONTENT", 67890);

if (!isFavorited.data) {
  // 2. 添加收藏
  const favoriteResult = await addFavorite({
    userId: 12345,
    favoriteType: "CONTENT",
    targetId: 67890,
    targetTitle: "Java基础教程",
    folderId: 1001,
    tags: ["Java", "编程", "教程"],
    remark: "好文章，值得收藏"
  });
  
  console.log("收藏成功:", favoriteResult.data);
}

// 3. 获取收藏统计
const stats = await getUserFavoriteStatistics(12345);
console.log("收藏统计:", stats.data);
```

### 收藏夹管理
```javascript
// 创建收藏夹
const folder = await createFolder({
  userId: 12345,
  name: "编程学习",
  description: "编程相关的文章收藏",
  isPrivate: false
});

// 获取收藏夹列表
const folders = await getFolders(12345);

// 移动收藏到收藏夹
const moveResult = await moveFavorite({
  favoriteId: 98765,
  targetFolderId: folder.data.id,
  userId: 12345
});
```

### 收藏列表和搜索
```javascript
// 获取用户收藏列表
const favorites = await getUserFavorites(12345, {
  favoriteType: "CONTENT",
  page: 1,
  size: 20
});

// 搜索收藏
const searchResult = await searchFavorites(12345, {
  keyword: "Java",
  favoriteType: "CONTENT",
  page: 1,
  size: 20
});

// 批量操作
const batchResult = await batchOperateFavorites({
  favoriteIds: [98765, 98766, 98767],
  operation: "move",
  targetFolderId: 1002,
  userId: 12345
});
```
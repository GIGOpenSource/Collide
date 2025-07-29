# Collide 社交服务 API 文档

## 概述

Collide 社交服务提供完整的社交功能，包括动态发布、动态流、社交互动、用户社交关系管理等核心功能。支持多媒体动态、话题标签、社交推荐等特性。

**服务版本**: v1.0.0  
**基础路径**: `/api/v1/social`  
**Dubbo服务**: `collide-social`  
**设计理念**: 构建活跃的社交生态，促进用户互动和内容传播

---

## 动态管理 API

### 1. 发布动态
**接口路径**: `POST /api/v1/social/dynamics`  
**接口描述**: 发布新的社交动态

#### 请求参数
```json
{
  "userId": 12345,                           // 必填，发布者用户ID
  "content": "今天学习了Java设计模式，收获很大！", // 必填，动态内容
  "dynamicType": "text",                     // 必填，动态类型：text/image/video/link
  "mediaUrls": [                            // 可选，媒体文件URL列表
    "https://example.com/image1.jpg",
    "https://example.com/image2.jpg"
  ],
  "tags": ["#Java", "#学习"],                // 可选，标签列表
  "location": "北京市朝阳区",                 // 可选，地理位置
  "visibility": "public",                   // 可选，可见性：public/friends/private
  "allowComment": true,                     // 可选，是否允许评论
  "allowShare": true                        // 可选，是否允许分享
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "动态发布成功",
  "data": {
    "id": 789012,
    "userId": 12345,
    "username": "techuser",
    "userNickname": "技术达人",
    "userAvatar": "https://example.com/avatar.jpg",
    "content": "今天学习了Java设计模式，收获很大！",
    "dynamicType": "text",
    "mediaUrls": [
      "https://example.com/image1.jpg",
      "https://example.com/image2.jpg"
    ],
    "tags": ["#Java", "#学习"],
    "location": "北京市朝阳区",
    "visibility": "public",
    "allowComment": true,
    "allowShare": true,
    "status": "published",
    "likeCount": 0,
    "commentCount": 0,
    "shareCount": 0,
    "viewCount": 0,
    "createTime": "2024-01-16T10:30:00",
    "updateTime": "2024-01-16T10:30:00"
  }
}
```

---

### 2. 更新动态
**接口路径**: `PUT /api/v1/social/dynamics/{id}`  
**接口描述**: 更新动态内容

#### 请求参数
- **id** (path): 动态ID，必填

```json
{
  "content": "修改后的动态内容",              // 可选，新的动态内容
  "tags": ["#Java", "#学习", "#设计模式"],   // 可选，更新标签
  "visibility": "friends",                // 可选，更新可见性
  "allowComment": false,                  // 可选，更新评论权限
  "allowShare": false                     // 可选，更新分享权限
}
```

---

### 3. 删除动态
**接口路径**: `DELETE /api/v1/social/dynamics/{id}`  
**接口描述**: 删除动态

#### 请求参数
- **id** (path): 动态ID，必填
- **userId** (query): 操作用户ID，必填

---

### 4. 获取动态详情
**接口路径**: `GET /api/v1/social/dynamics/{id}`  
**接口描述**: 获取动态详细信息

#### 请求参数
- **id** (path): 动态ID，必填
- **viewerId** (query): 查看者用户ID，可选

---

### 5. 查询动态列表
**接口路径**: `POST /api/v1/social/dynamics/query`  
**接口描述**: 分页查询动态列表

#### 请求参数
```json
{
  "pageNum": 1,                    // 页码（从1开始）
  "pageSize": 20,                  // 每页大小
  "userId": 12345,                 // 可选，发布者用户ID
  "dynamicType": "text",           // 可选，动态类型
  "tag": "#Java",                  // 可选，标签筛选
  "location": "北京市",            // 可选，地理位置
  "visibility": "public",          // 可选，可见性
  "status": "published",           // 可选，状态
  "keyword": "Java",               // 可选，内容关键词搜索
  "timeRange": 7,                  // 可选，时间范围（天）
  "orderBy": "createTime",         // 可选，排序字段
  "orderDirection": "DESC"         // 可选，排序方向
}
```

---

## 动态流 API

### 1. 获取个人动态流
**接口路径**: `GET /api/v1/social/feed/personal`  
**接口描述**: 获取用户的个人动态流（关注用户的动态）

#### 请求参数
- **userId** (query): 用户ID，必填
- **pageNum** (query): 页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20
- **lastId** (query): 上次加载的最后一条动态ID，可选（用于无限滚动）

---

### 2. 获取推荐动态流
**接口路径**: `GET /api/v1/social/feed/recommend`  
**接口描述**: 获取推荐动态流（基于用户兴趣推荐）

#### 请求参数
- **userId** (query): 用户ID，必填
- **pageNum** (query): 页码，可选
- **pageSize** (query): 页面大小，可选

---

### 3. 获取热门动态流
**接口路径**: `GET /api/v1/social/feed/hot`  
**接口描述**: 获取热门动态流

#### 请求参数
- **timeRange** (query): 时间范围（小时），可选，默认24
- **pageNum** (query): 页码，可选
- **pageSize** (query): 页面大小，可选

---

### 4. 获取最新动态流
**接口路径**: `GET /api/v1/social/feed/latest`  
**接口描述**: 获取最新动态流

---

### 5. 获取用户动态
**接口路径**: `GET /api/v1/social/users/{userId}/dynamics`  
**接口描述**: 获取指定用户的动态列表

#### 请求参数
- **userId** (path): 用户ID，必填
- **viewerId** (query): 查看者用户ID，可选
- **pageNum** (query): 页码，可选
- **pageSize** (query): 页面大小，可选

---

## 动态互动 API

### 1. 点赞动态
**接口路径**: `POST /api/v1/social/dynamics/{id}/like`  
**接口描述**: 给动态点赞

#### 请求参数
- **id** (path): 动态ID，必填
- **userId** (query): 用户ID，必填

---

### 2. 取消点赞
**接口路径**: `DELETE /api/v1/social/dynamics/{id}/like`  
**接口描述**: 取消动态点赞

---

### 3. 评论动态
**接口路径**: `POST /api/v1/social/dynamics/{id}/comments`  
**接口描述**: 评论动态

#### 请求参数
- **id** (path): 动态ID，必填

```json
{
  "userId": 67890,                    // 必填，评论者用户ID
  "content": "说得很好！",            // 必填，评论内容
  "parentId": null,                   // 可选，父评论ID（回复时需要）
  "replyToUserId": null              // 可选，回复的用户ID
}
```

---

### 4. 分享动态
**接口路径**: `POST /api/v1/social/dynamics/{id}/share`  
**接口描述**: 分享动态

#### 请求参数
- **id** (path): 动态ID，必填

```json
{
  "userId": 67890,                    // 必填，分享者用户ID
  "shareType": "repost",              // 必填，分享类型：repost/forward
  "shareContent": "转发一个很好的内容", // 可选，分享时的评论
  "shareTarget": "timeline"           // 可选，分享目标：timeline/group/private
}
```

---

### 5. 收藏动态
**接口路径**: `POST /api/v1/social/dynamics/{id}/favorite`  
**接口描述**: 收藏动态

---

### 6. 举报动态
**接口路径**: `POST /api/v1/social/dynamics/{id}/report`  
**接口描述**: 举报不当动态

#### 请求参数
- **id** (path): 动态ID，必填

```json
{
  "userId": 67890,                    // 必填，举报用户ID
  "reason": "inappropriate",          // 必填，举报原因
  "description": "内容不当"           // 可选，详细描述
}
```

---

## 话题管理 API

### 1. 创建话题
**接口路径**: `POST /api/v1/social/topics`  
**接口描述**: 创建新话题

#### 请求参数
```json
{
  "name": "Java学习心得",               // 必填，话题名称
  "description": "分享Java学习经验",     // 可选，话题描述
  "tags": ["Java", "编程", "学习"],      // 可选，相关标签
  "coverUrl": "https://example.com/cover.jpg", // 可选，话题封面
  "creatorId": 12345,                  // 必填，创建者ID
  "isPublic": true                     // 可选，是否公开话题
}
```

---

### 2. 获取热门话题
**接口路径**: `GET /api/v1/social/topics/hot`  
**接口描述**: 获取热门话题列表

#### 请求参数
- **timeRange** (query): 时间范围（天），可选，默认7
- **limit** (query): 返回数量，可选，默认10

---

### 3. 搜索话题
**接口路径**: `GET /api/v1/social/topics/search`  
**接口描述**: 搜索话题

#### 请求参数
- **keyword** (query): 搜索关键词，必填
- **pageNum** (query): 页码，可选
- **pageSize** (query): 页面大小，可选

---

### 4. 参与话题
**接口路径**: `POST /api/v1/social/topics/{id}/join`  
**接口描述**: 参与话题讨论

#### 请求参数
- **id** (path): 话题ID，必填
- **userId** (query): 用户ID，必填

---

### 5. 获取话题动态
**接口路径**: `GET /api/v1/social/topics/{id}/dynamics`  
**接口描述**: 获取话题下的动态列表

---

## 社交统计 API

### 1. 获取用户社交统计
**接口路径**: `GET /api/v1/social/statistics/user/{userId}`  
**接口描述**: 获取用户的社交统计信息

#### 响应示例
```json
{
  "success": true,
  "data": {
    "userId": 12345,
    "dynamicCount": 156,           // 发布动态数
    "followingCount": 89,          // 关注数
    "followerCount": 234,          // 粉丝数
    "likeReceived": 1250,          // 收到的赞数
    "commentReceived": 567,        // 收到的评论数
    "shareReceived": 89,           // 被分享次数
    "activeLevel": "high",         // 活跃等级
    "influenceScore": 85.6         // 影响力分数
  }
}
```

---

### 2. 获取动态统计
**接口路径**: `GET /api/v1/social/statistics/dynamic/{id}`  
**接口描述**: 获取动态的统计信息

---

### 3. 获取热门用户
**接口路径**: `GET /api/v1/social/users/hot`  
**接口描述**: 获取热门用户列表

---

## 社交推荐 API

### 1. 推荐关注用户
**接口路径**: `GET /api/v1/social/recommend/users`  
**接口描述**: 推荐用户可能感兴趣的用户

#### 请求参数
- **userId** (query): 当前用户ID，必填
- **limit** (query): 推荐数量，可选，默认10

---

### 2. 推荐话题
**接口路径**: `GET /api/v1/social/recommend/topics`  
**接口描述**: 推荐话题

---

### 3. 推荐动态
**接口路径**: `GET /api/v1/social/recommend/dynamics`  
**接口描述**: 基于算法推荐动态

---

## 社交设置 API

### 1. 设置隐私权限
**接口路径**: `POST /api/v1/social/settings/privacy`  
**接口描述**: 设置用户社交隐私权限

#### 请求参数
```json
{
  "userId": 12345,                    // 必填，用户ID
  "allowFollow": true,                // 是否允许被关注
  "dynamicVisibility": "public",      // 动态默认可见性
  "allowComment": true,               // 是否允许评论
  "allowShare": true,                 // 是否允许分享
  "allowRecommend": true              // 是否允许被推荐
}
```

---

### 2. 获取社交设置
**接口路径**: `GET /api/v1/social/settings/{userId}`  
**接口描述**: 获取用户的社交设置

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| DYNAMIC_NOT_FOUND | 动态不存在 |
| DYNAMIC_DELETED | 动态已删除 |
| DYNAMIC_PERMISSION_DENIED | 动态访问权限不足 |
| TOPIC_NOT_FOUND | 话题不存在 |
| ALREADY_LIKED | 已经点赞过 |
| ALREADY_SHARED | 已经分享过 |
| CONTENT_TOO_LONG | 内容过长 |
| MEDIA_UPLOAD_FAILED | 媒体文件上传失败 |
| USER_BLOCKED | 用户被屏蔽 |
| SENSITIVE_CONTENT | 内容包含敏感信息 |

---

**最后更新**: 2024-01-16  
**文档版本**: v1.0.0
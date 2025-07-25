# Social 模块 API 接口文档

## 📋 目录
- [模块概述](#模块概述)
- [数据库设计](#数据库设计)
- [接口列表](#接口列表)
- [数据模型](#数据模型)
- [错误码定义](#错误码定义)
- [使用示例](#使用示例)

---

## 📚 模块概述

Social 模块是 Collide 社交平台的核心社交互动系统，为用户提供丰富的社交动态发布、互动和时间线展示功能，支持多媒体内容分享和实时社交体验。

### 主要功能
- **动态发布**: 支持文字、图片、视频、音频、链接等多种类型的社交动态
- **内容互动**: 点赞、评论、转发、收藏等社交互动功能
- **时间线管理**: 个人时间线和关注用户的动态时间线
- **话题系统**: 话题标签、话题动态聚合
- **用户提及**: @用户功能，消息通知
- **地理位置**: 位置打卡、附近动态
- **隐私控制**: 动态可见性设置、评论转发权限控制
- **实时推送**: 基于WebSocket的实时动态推送

### 支持的动态类型
- **TEXT**: 纯文字动态
- **IMAGE**: 图片动态（支持多图）
- **VIDEO**: 视频动态
- **AUDIO**: 音频动态
- **LINK**: 链接分享
- **ARTICLE**: 文章分享
- **POLL**: 投票动态
- **LOCATION**: 位置签到动态

### 技术特色
- **去连表化设计**: 避免复杂关联查询，提升性能
- **冗余存储**: 存储用户基础信息，减少实时查询
- **缓存优化**: Redis缓存热门动态和用户时间线
- **异步处理**: 动态统计和推荐算法异步计算

---

## 🗄️ 数据库设计

### 社交动态表 (t_social_post)

| 字段名 | 类型 | 是否必填 | 默认值 | 说明 |
|--------|------|----------|--------|------|
| id | BIGINT | 是 | AUTO_INCREMENT | 动态ID，主键 |
| post_type | VARCHAR(50) | 是 | - | 动态类型枚举 |
| content | TEXT | 是 | - | 动态内容 |
| media_urls | JSON | 否 | - | 媒体文件URL列表 |
| location | VARCHAR(200) | 否 | - | 位置信息 |
| longitude | DOUBLE | 否 | - | 经度 |
| latitude | DOUBLE | 否 | - | 纬度 |
| topics | JSON | 否 | - | 话题标签列表 |
| mentioned_user_ids | JSON | 否 | - | 提及的用户ID列表 |
| status | VARCHAR(50) | 是 | PUBLISHED | 动态状态 |
| visibility | TINYINT | 是 | 0 | 可见性：0-公开，1-关注者，2-私密 |
| allow_comments | BOOLEAN | 是 | TRUE | 是否允许评论 |
| allow_shares | BOOLEAN | 是 | TRUE | 是否允许转发 |
| author_id | BIGINT | 是 | - | 作者用户ID |
| author_name | VARCHAR(100) | 是 | - | 作者昵称（冗余） |
| author_avatar | VARCHAR(500) | 否 | - | 作者头像URL（冗余） |
| like_count | BIGINT | 是 | 0 | 点赞数 |
| comment_count | BIGINT | 是 | 0 | 评论数 |
| share_count | BIGINT | 是 | 0 | 转发数 |
| view_count | BIGINT | 是 | 0 | 浏览数 |
| create_time | TIMESTAMP | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | TIMESTAMP | 是 | CURRENT_TIMESTAMP | 更新时间 |

### 索引设计
- **主键索引**: `PRIMARY KEY (id)`
- **作者索引**: `KEY idx_author_id (author_id)`
- **时间索引**: `KEY idx_create_time (create_time)`
- **状态索引**: `KEY idx_status (status)`
- **可见性索引**: `KEY idx_visibility (visibility)`
- **话题索引**: `KEY idx_topics (topics(100))`（前缀索引）
- **位置索引**: `KEY idx_location (longitude, latitude)`

---

## 🔗 接口列表

### 动态管理

| 功能 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 发布动态 | POST | `/api/v1/social/posts` | 发布新的社交动态 |
| 更新动态 | PUT | `/api/v1/social/posts/{postId}` | 更新指定动态内容 |
| 删除动态 | DELETE | `/api/v1/social/posts/{postId}` | 删除指定动态 |
| 查询动态详情 | GET | `/api/v1/social/posts/{postId}` | 查询动态详细信息 |

### 动态查询与发现

| 功能 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 分页查询动态 | GET | `/api/v1/social/posts` | 分页查询动态列表 |
| 获取用户时间线 | GET | `/api/v1/social/timeline/{userId}` | 获取用户的动态时间线 |
| 获取关注时间线 | GET | `/api/v1/social/timeline/following` | 获取关注用户的动态时间线 |
| 话题动态查询 | GET | `/api/v1/social/posts/topic/{topic}` | 查询指定话题的动态 |
| 附近动态查询 | GET | `/api/v1/social/posts/nearby` | 查询附近的动态 |
| 热门动态查询 | GET | `/api/v1/social/posts/trending` | 查询热门动态 |

---

## 📊 接口详情

### 1. 发布动态

**接口地址**: `POST /api/v1/social/posts`

**请求参数**:
```json
{
  "postType": "IMAGE",                          // 动态类型，必填
  "content": "今天天气真好，阳光明媚！",       // 动态内容，必填
  "mediaUrls": [                                // 媒体文件URL列表，可选
    "https://example.com/image1.jpg",
    "https://example.com/image2.jpg"
  ],
  "location": "北京市朝阳区",                  // 位置信息，可选
  "longitude": 116.4074,                        // 经度，可选
  "latitude": 39.9042,                         // 纬度，可选
  "topics": ["春天", "旅行", "美食"],          // 话题标签，可选
  "mentionedUserIds": [12345, 67890],          // 提及的用户ID，可选
  "allowComments": true,                        // 是否允许评论，默认true
  "allowShares": true,                         // 是否允许转发，默认true
  "visibility": 0                              // 可见性：0-公开，1-关注者，2-私密
}
```

**响应结果**:
```json
{
  "code": 200,
  "message": "发布成功",
  "data": {
    "postId": 987654321,
    "status": "PUBLISHED",
    "createTime": "2024-01-15T10:30:00Z"
  },
  "timestamp": 1705312200000,
  "traceId": "trace-123456"
}
```

### 2. 更新动态

**接口地址**: `PUT /api/v1/social/posts/{postId}`

**路径参数**:
- `postId`: 动态ID

**请求参数**:
```json
{
  "content": "更新后的动态内容",               // 更新的内容
  "mediaUrls": [                               // 更新的媒体文件
    "https://example.com/new-image.jpg"
  ],
  "topics": ["更新", "修改"],                 // 更新的话题标签
  "allowComments": false,                      // 更新评论权限
  "allowShares": true,                        // 更新转发权限
  "visibility": 1                             // 更新可见性
}
```

**响应结果**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "postId": 987654321,
    "updateTime": "2024-01-15T11:00:00Z"
  },
  "timestamp": 1705314000000,
  "traceId": "trace-123456"
}
```

### 3. 删除动态

**接口地址**: `DELETE /api/v1/social/posts/{postId}`

**路径参数**:
- `postId`: 动态ID

**响应结果**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null,
  "timestamp": 1705314000000,
  "traceId": "trace-123456"
}
```

### 4. 查询动态详情

**接口地址**: `GET /api/v1/social/posts/{postId}`

**路径参数**:
- `postId`: 动态ID

**查询参数**:
- `currentUserId`: 当前用户ID，可选（用于个性化信息）

**响应结果**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "id": 987654321,
    "postType": "IMAGE",
    "content": "今天天气真好，阳光明媚！",
    "mediaUrls": [
      "https://example.com/image1.jpg",
      "https://example.com/image2.jpg"
    ],
    "location": "北京市朝阳区",
    "longitude": 116.4074,
    "latitude": 39.9042,
    "topics": ["春天", "旅行", "美食"],
    "mentionedUserIds": [12345, 67890],
    "status": "PUBLISHED",
    "visibility": 0,
    "allowComments": true,
    "allowShares": true,
    "authorId": 123456,
    "authorName": "张三",
    "authorAvatar": "https://example.com/avatar.jpg",
    "likeCount": 125,
    "commentCount": 23,
    "shareCount": 8,
    "viewCount": 1520,
    "isLiked": true,
    "isShared": false,
    "createTime": "2024-01-15T10:30:00Z",
    "updateTime": "2024-01-15T10:30:00Z"
  },
  "timestamp": 1705312200000,
  "traceId": "trace-123456"
}
```

### 5. 分页查询动态

**接口地址**: `GET /api/v1/social/posts`

**查询参数**:
- `pageNo`: 页码，默认1
- `pageSize`: 每页大小，默认20，最大100
- `authorId`: 作者ID，可选
- `postType`: 动态类型，可选
- `topic`: 话题标签，可选
- `location`: 位置信息，可选
- `visibility`: 可见性，可选
- `sortBy`: 排序字段，默认createTime
- `sortOrder`: 排序方向，默认DESC

**响应结果**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "records": [
      {
        "id": 987654321,
        "postType": "IMAGE",
        "content": "今天天气真好，阳光明媚！",
        "mediaUrls": ["https://example.com/image1.jpg"],
        "location": "北京市朝阳区",
        "topics": ["春天", "旅行"],
        "authorId": 123456,
        "authorName": "张三",
        "authorAvatar": "https://example.com/avatar.jpg",
        "likeCount": 125,
        "commentCount": 23,
        "shareCount": 8,
        "viewCount": 1520,
        "createTime": "2024-01-15T10:30:00Z"
      }
    ],
    "total": 500,
    "pageNo": 1,
    "pageSize": 20,
    "totalPages": 25,
    "hasNext": true,
    "hasPrev": false
  },
  "timestamp": 1705312200000,
  "traceId": "trace-123456"
}
```

### 6. 获取用户时间线

**接口地址**: `GET /api/v1/social/timeline/{userId}`

**路径参数**:
- `userId`: 用户ID

**查询参数**:
- `pageNo`: 页码，默认1
- `pageSize`: 每页大小，默认20

**响应结果**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "records": [
      {
        "id": 987654321,
        "postType": "IMAGE",
        "content": "今天天气真好！",
        "mediaUrls": ["https://example.com/image1.jpg"],
        "authorId": 123456,
        "authorName": "张三",
        "authorAvatar": "https://example.com/avatar.jpg",
        "likeCount": 125,
        "commentCount": 23,
        "createTime": "2024-01-15T10:30:00Z"
      }
    ],
    "total": 50,
    "pageNo": 1,
    "pageSize": 20,
    "totalPages": 3,
    "hasNext": true,
    "hasPrev": false
  },
  "timestamp": 1705312200000,
  "traceId": "trace-123456"
}
```

### 7. 获取关注时间线

**接口地址**: `GET /api/v1/social/timeline/following`

**查询参数**:
- `pageNo`: 页码，默认1
- `pageSize`: 每页大小，默认20
- `currentUserId`: 当前用户ID，必填

**响应结果**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "records": [
      {
        "id": 987654321,
        "postType": "TEXT",
        "content": "关注的朋友发布的动态",
        "authorId": 789012,
        "authorName": "李四",
        "authorAvatar": "https://example.com/avatar2.jpg",
        "likeCount": 89,
        "commentCount": 15,
        "createTime": "2024-01-15T09:30:00Z"
      }
    ],
    "total": 200,
    "pageNo": 1,
    "pageSize": 20,
    "totalPages": 10,
    "hasNext": true,
    "hasPrev": false
  },
  "timestamp": 1705312200000,
  "traceId": "trace-123456"
}
```

### 8. 话题动态查询

**接口地址**: `GET /api/v1/social/posts/topic/{topic}`

**路径参数**:
- `topic`: 话题名称

**查询参数**:
- `pageNo`: 页码，默认1
- `pageSize`: 每页大小，默认20
- `sortBy`: 排序字段，默认createTime

**响应结果**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "topic": "春天",
    "totalPosts": 1500,
    "records": [
      {
        "id": 987654321,
        "postType": "IMAGE",
        "content": "春天的花朵真美丽！",
        "mediaUrls": ["https://example.com/spring-flower.jpg"],
        "topics": ["春天", "花朵", "美景"],
        "authorId": 123456,
        "authorName": "张三",
        "authorAvatar": "https://example.com/avatar.jpg",
        "likeCount": 256,
        "commentCount": 45,
        "createTime": "2024-01-15T08:30:00Z"
      }
    ],
    "total": 100,
    "pageNo": 1,
    "pageSize": 20,
    "totalPages": 5,
    "hasNext": true,
    "hasPrev": false
  },
  "timestamp": 1705312200000,
  "traceId": "trace-123456"
}
```

### 9. 附近动态查询

**接口地址**: `GET /api/v1/social/posts/nearby`

**查询参数**:
- `longitude`: 当前经度，必填
- `latitude`: 当前纬度，必填
- `radius`: 查询半径（公里），默认10
- `pageNo`: 页码，默认1
- `pageSize`: 每页大小，默认20

**响应结果**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "currentLocation": {
      "longitude": 116.4074,
      "latitude": 39.9042
    },
    "radius": 10,
    "records": [
      {
        "id": 987654321,
        "postType": "LOCATION",
        "content": "在这个美丽的地方打卡！",
        "location": "北京市朝阳区三里屯",
        "longitude": 116.4100,
        "latitude": 39.9050,
        "distance": 0.8,
        "authorId": 123456,
        "authorName": "张三",
        "authorAvatar": "https://example.com/avatar.jpg",
        "likeCount": 89,
        "commentCount": 12,
        "createTime": "2024-01-15T11:30:00Z"
      }
    ],
    "total": 25,
    "pageNo": 1,
    "pageSize": 20,
    "totalPages": 2,
    "hasNext": true,
    "hasPrev": false
  },
  "timestamp": 1705312200000,
  "traceId": "trace-123456"
}
```

### 10. 热门动态查询

**接口地址**: `GET /api/v1/social/posts/trending`

**查询参数**:
- `timeRange`: 时间范围，可选值：24h、7d、30d，默认24h
- `postType`: 动态类型，可选
- `pageNo`: 页码，默认1，最大10
- `pageSize`: 每页大小，默认20

**响应结果**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "timeRange": "24h",
    "trendingAlgorithm": "综合热度排序",
    "records": [
      {
        "id": 987654321,
        "postType": "VIDEO",
        "content": "超级搞笑的视频，笑死我了！",
        "mediaUrls": ["https://example.com/funny-video.mp4"],
        "topics": ["搞笑", "视频", "娱乐"],
        "authorId": 123456,
        "authorName": "网红博主",
        "authorAvatar": "https://example.com/influencer-avatar.jpg",
        "likeCount": 15600,
        "commentCount": 2340,
        "shareCount": 890,
        "viewCount": 123000,
        "hotScore": 98.5,
        "createTime": "2024-01-15T06:00:00Z"
      }
    ],
    "total": 50,
    "pageNo": 1,
    "pageSize": 20,
    "totalPages": 3,
    "hasNext": true,
    "hasPrev": false
  },
  "timestamp": 1705312200000,
  "traceId": "trace-123456"
}
```

---

## 📋 数据模型

### SocialPostInfo - 社交动态信息

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 动态ID |
| postType | String | 动态类型 |
| content | String | 动态内容 |
| mediaUrls | List<String> | 媒体文件URL列表 |
| location | String | 位置信息 |
| longitude | Double | 经度 |
| latitude | Double | 纬度 |
| topics | List<String> | 话题标签列表 |
| mentionedUserIds | List<Long> | 提及的用户ID列表 |
| status | String | 动态状态 |
| visibility | Integer | 可见性设置 |
| allowComments | Boolean | 是否允许评论 |
| allowShares | Boolean | 是否允许转发 |
| authorId | Long | 作者用户ID |
| authorName | String | 作者昵称 |
| authorAvatar | String | 作者头像URL |
| likeCount | Long | 点赞数 |
| commentCount | Long | 评论数 |
| shareCount | Long | 转发数 |
| viewCount | Long | 浏览数 |
| isLiked | Boolean | 当前用户是否已点赞 |
| isShared | Boolean | 当前用户是否已转发 |
| hotScore | Double | 热度分数 |
| distance | Double | 距离（km，仅附近动态查询返回） |
| createTime | LocalDateTime | 创建时间 |
| updateTime | LocalDateTime | 更新时间 |

### SocialPostCreateRequest - 创建动态请求

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| postType | SocialPostType | 是 | 动态类型 |
| content | String | 是 | 动态内容，最大2000字 |
| mediaUrls | List<String> | 否 | 媒体文件URL列表 |
| location | String | 否 | 位置信息 |
| longitude | Double | 否 | 经度 |
| latitude | Double | 否 | 纬度 |
| topics | List<String> | 否 | 话题标签列表 |
| mentionedUserIds | List<Long> | 否 | 提及的用户ID列表 |
| allowComments | Boolean | 否 | 是否允许评论，默认true |
| allowShares | Boolean | 否 | 是否允许转发，默认true |
| visibility | Integer | 否 | 可见性：0-公开，1-关注者，2-私密 |

### SocialPostQueryRequest - 查询动态请求

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| authorId | Long | 否 | 作者ID筛选 |
| postType | String | 否 | 动态类型筛选 |
| topic | String | 否 | 话题标签筛选 |
| location | String | 否 | 位置信息筛选 |
| visibility | Integer | 否 | 可见性筛选 |
| startTime | LocalDateTime | 否 | 开始时间 |
| endTime | LocalDateTime | 否 | 结束时间 |
| pageNo | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页大小，默认20 |
| sortBy | String | 否 | 排序字段，默认createTime |
| sortOrder | String | 否 | 排序方向，默认DESC |

---

## ❌ 错误码定义

### 社交动态相关错误码

| 错误码 | HTTP状态码 | 错误信息 | 说明 |
|--------|------------|----------|------|
| SOCIAL_001 | 400 | 动态内容不能为空 | 发布动态时内容为空 |
| SOCIAL_002 | 400 | 动态内容长度不能超过2000字 | 内容长度超出限制 |
| SOCIAL_003 | 400 | 媒体文件数量不能超过9个 | 上传媒体文件过多 |
| SOCIAL_004 | 400 | 不支持的动态类型 | 动态类型不在支持范围内 |
| SOCIAL_005 | 404 | 动态不存在 | 指定的动态ID不存在 |
| SOCIAL_006 | 403 | 无权限访问此动态 | 动态可见性限制 |
| SOCIAL_007 | 403 | 无权限编辑此动态 | 只有作者可以编辑动态 |
| SOCIAL_008 | 403 | 无权限删除此动态 | 只有作者可以删除动态 |
| SOCIAL_009 | 400 | 动态已被删除，无法操作 | 对已删除动态进行操作 |
| SOCIAL_010 | 400 | 评论功能已关闭 | 动态不允许评论 |
| SOCIAL_011 | 400 | 转发功能已关闭 | 动态不允许转发 |
| SOCIAL_012 | 400 | 话题标签数量不能超过10个 | 话题标签过多 |
| SOCIAL_013 | 400 | 提及用户数量不能超过20个 | 提及用户过多 |
| SOCIAL_014 | 429 | 发布频率过快，请稍后再试 | 发布频率限制 |
| SOCIAL_015 | 400 | 位置坐标格式错误 | 经纬度格式不正确 |

### 通用错误码

| 错误码 | HTTP状态码 | 错误信息 | 说明 |
|--------|------------|----------|------|
| PARAM_INVALID | 400 | 参数无效 | 请求参数格式错误 |
| USER_NOT_FOUND | 404 | 用户不存在 | 指定用户ID不存在 |
| PERMISSION_DENIED | 403 | 权限不足 | 无足够权限执行操作 |
| RATE_LIMIT_EXCEEDED | 429 | 请求频率过快 | 触发限流策略 |
| SYSTEM_ERROR | 500 | 系统内部错误 | 服务器内部异常 |

---

## 🔧 使用示例

### 1. 发布文字动态

```bash
curl -X POST "http://localhost:8080/api/v1/social/posts" \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "postType": "TEXT",
    "content": "今天心情很好，分享一下！",
    "topics": ["心情", "分享"],
    "allowComments": true,
    "allowShares": true,
    "visibility": 0
  }'
```

### 2. 发布带图片的动态

```bash
curl -X POST "http://localhost:8080/api/v1/social/posts" \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "postType": "IMAGE",
    "content": "今天拍的美景，太漂亮了！",
    "mediaUrls": [
      "https://example.com/image1.jpg",
      "https://example.com/image2.jpg"
    ],
    "location": "北京市朝阳区",
    "longitude": 116.4074,
    "latitude": 39.9042,
    "topics": ["美景", "摄影", "北京"],
    "visibility": 0
  }'
```

### 3. 发布位置签到动态

```bash
curl -X POST "http://localhost:8080/api/v1/social/posts" \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "postType": "LOCATION",
    "content": "在这个美丽的地方打卡！",
    "location": "北京市朝阳区三里屯",
    "longitude": 116.4100,
    "latitude": 39.9050,
    "topics": ["打卡", "三里屯"],
    "visibility": 0
  }'
```

### 4. 查询用户时间线

```bash
curl -X GET "http://localhost:8080/api/v1/social/timeline/123456?pageNo=1&pageSize=20" \
  -H "Authorization: Bearer {token}"
```

### 5. 查询话题动态

```bash
curl -X GET "http://localhost:8080/api/v1/social/posts/topic/春天?pageNo=1&pageSize=20" \
  -H "Authorization: Bearer {token}"
```

### 6. 查询附近动态

```bash
curl -X GET "http://localhost:8080/api/v1/social/posts/nearby?longitude=116.4074&latitude=39.9042&radius=5&pageNo=1&pageSize=20" \
  -H "Authorization: Bearer {token}"
```

### 7. 查询热门动态

```bash
curl -X GET "http://localhost:8080/api/v1/social/posts/trending?timeRange=24h&pageNo=1&pageSize=20" \
  -H "Authorization: Bearer {token}"
```

---

## 📝 注意事项

1. **内容审核**: 所有发布的动态内容都会进行自动审核，包含敏感词的内容会被标记或拒绝
2. **频率限制**: 用户每分钟最多发布10条动态，防止刷屏行为
3. **媒体文件**: 单条动态最多支持9个媒体文件，单个文件大小不超过50MB
4. **地理位置**: 位置信息基于GPS坐标，支持国内外主要城市的位置识别
5. **话题标签**: 话题标签自动去重，支持中英文，单个标签长度不超过20字符
6. **用户提及**: 被提及的用户会收到消息通知，需要处理用户隐私设置
7. **可见性控制**: 私密动态只有作者本人可见，关注者动态只有相互关注的用户可见
8. **缓存策略**: 热门动态和用户时间线使用Redis缓存，缓存时间为5分钟
9. **异步统计**: 点赞数、评论数等统计数据采用异步更新，可能存在短暂延迟
10. **删除策略**: 删除动态为逻辑删除，数据保留30天后物理删除

---

**文档版本**: 1.0.0  
**最后更新**: 2024-01-15  
**维护团队**: Collide Backend Team 
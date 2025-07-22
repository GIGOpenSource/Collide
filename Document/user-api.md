# User 模块 API 接口文档

## 📋 目录
- [模块概述](#模块概述)
- [数据库设计](#数据库设计)
- [接口列表](#接口列表)
- [数据模型](#数据模型)
- [错误码定义](#错误码定义)
- [使用示例](#使用示例)

---

## 📚 模块概述

User 模块是 Collide 社交平台的核心模块之一，负责用户信息管理、用户档案维护、用户权限控制等功能。

### 主要功能
- 用户基础信息管理
- 用户扩展档案管理
- 用户认证和权限管理
- 用户状态管理
- 博主认证申请
- 用户统计数据管理

### 技术架构
- **框架**: Spring Boot 3.x + Spring Cloud
- **数据库**: MySQL 8.0
- **ORM**: MyBatis Plus
- **RPC**: Apache Dubbo
- **认证**: Sa-Token
- **文档**: OpenAPI 3.0

---

## 🗄️ 数据库设计

### 用户基础表 (t_user)

| 字段名 | 类型 | 是否必填 | 默认值 | 说明 |
|--------|------|----------|--------|------|
| id | BIGINT | 是 | AUTO_INCREMENT | 用户ID，主键 |
| username | VARCHAR(255) | 是 | - | 用户名，唯一 |
| nickname | VARCHAR(255) | 否 | - | 用户昵称 |
| avatar | VARCHAR(255) | 否 | - | 头像URL |
| email | VARCHAR(255) | 否 | - | 邮箱地址 |
| phone | VARCHAR(20) | 否 | - | 手机号码 |
| password_hash | VARCHAR(255) | 是 | - | 密码哈希值 |
| salt | VARCHAR(255) | 否 | - | 密码盐值 |
| role | VARCHAR(50) | 是 | USER | 用户角色：USER、ADMIN、BLOGGER |
| status | VARCHAR(50) | 是 | ACTIVE | 用户状态：ACTIVE、FROZEN、DELETED |
| last_login_time | DATETIME | 否 | - | 最后登录时间 |
| create_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 是 | CURRENT_TIMESTAMP | 更新时间 |

### 用户扩展表 (t_user_profile)

| 字段名 | 类型 | 是否必填 | 默认值 | 说明 |
|--------|------|----------|--------|------|
| id | BIGINT | 是 | AUTO_INCREMENT | 扩展信息ID，主键 |
| user_id | BIGINT | 是 | - | 用户ID，外键 |
| bio | VARCHAR(500) | 否 | - | 个人简介 |
| birthday | DATE | 否 | - | 生日 |
| gender | VARCHAR(20) | 否 | unknown | 性别：male、female、unknown |
| location | VARCHAR(100) | 否 | - | 所在地 |
| follower_count | BIGINT | 否 | 0 | 粉丝数 |
| following_count | BIGINT | 否 | 0 | 关注数 |
| content_count | BIGINT | 否 | 0 | 内容数 |
| like_count | BIGINT | 否 | 0 | 获得点赞数 |
| vip_expire_time | DATETIME | 否 | - | VIP过期时间 |
| blogger_status | VARCHAR(20) | 否 | none | 博主认证状态 |
| blogger_apply_time | DATETIME | 否 | - | 博主申请时间 |
| blogger_approve_time | DATETIME | 否 | - | 博主认证时间 |

---

## 🔗 接口列表

### 1. 获取当前用户信息

**接口描述**: 获取当前登录用户的详细信息

**请求信息**:
- **URL**: `GET /api/v1/users/me`
- **需要认证**: 是

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 12345,
    "userName": "johndoe",
    "nickName": "John Doe",
    "profilePhotoUrl": "https://example.com/avatar.jpg",
    "email": "john@example.com",
    "phone": "13800138000",
    "role": "USER",
    "status": "ACTIVE",
    "bio": "热爱生活，热爱分享",
    "gender": "male",
    "birthday": "1990-01-01",
    "location": "北京市",
    "followerCount": 156,
    "followingCount": 89,
    "contentCount": 42,
    "likeCount": 1205,
    "vipExpireTime": null,
    "bloggerStatus": "none",
    "lastLoginTime": "2024-01-15T10:30:00",
    "createTime": "2023-12-01T08:00:00",
    "updateTime": "2024-01-15T10:30:00"
  }
}
```

---

### 2. 根据用户ID获取用户信息

**接口描述**: 根据用户ID获取指定用户的公开信息

**请求信息**:
- **URL**: `GET /api/v1/users/{userId}`
- **需要认证**: 否

**路径参数**:

| 参数名 | 类型 | 是否必填 | 说明 |
|--------|------|----------|------|
| userId | Long | 是 | 用户ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 67890,
    "userName": "janedoe",
    "nickName": "Jane Doe",
    "profilePhotoUrl": "https://example.com/avatar2.jpg",
    "bio": "摄影爱好者，记录美好生活",
    "gender": "female",
    "location": "上海市",
    "followerCount": 203,
    "followingCount": 145,
    "contentCount": 78,
    "likeCount": 2340,
    "bloggerStatus": "approved",
    "createTime": "2023-11-15T14:20:00"
  }
}
```

**注意**: 公开接口不返回敏感信息如邮箱、手机号等。

---

### 3. 更新用户信息

**接口描述**: 更新当前用户的基础信息和扩展信息

**请求信息**:
- **URL**: `PUT /api/v1/users/me`
- **Content-Type**: `application/json`
- **需要认证**: 是

**请求参数**:
```json
{
  "nickName": "New Nickname",
  "bio": "更新后的个人简介",
  "gender": "male",
  "birthday": "1990-01-01",
  "location": "深圳市",
  "profilePhotoUrl": "https://example.com/new-avatar.jpg"
}
```

| 参数名 | 类型 | 是否必填 | 说明 |
|--------|------|----------|------|
| nickName | String | 否 | 用户昵称，长度2-20字符 |
| bio | String | 否 | 个人简介，最长500字符 |
| gender | String | 否 | 性别：male、female、unknown |
| birthday | String | 否 | 生日，格式：YYYY-MM-DD |
| location | String | 否 | 所在地，最长100字符 |
| profilePhotoUrl | String | 否 | 头像URL |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 12345,
    "userName": "johndoe",
    "nickName": "New Nickname",
    "profilePhotoUrl": "https://example.com/new-avatar.jpg",
    "bio": "更新后的个人简介",
    "gender": "male",
    "birthday": "1990-01-01",
    "location": "深圳市",
    "updateTime": "2024-01-15T11:45:00"
  }
}
```

---

### 4. 申请博主认证

**接口描述**: 当前用户申请成为认证博主

**请求信息**:
- **URL**: `POST /api/v1/users/blogger/apply`
- **需要认证**: 是

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": "博主认证申请已提交，请耐心等待审核结果"
}
```

**业务规则**:
- 用户必须已完善基本信息（昵称、头像、简介）
- 用户必须发布过至少5篇内容
- 用户粉丝数需达到100以上
- 同一用户30天内只能申请一次

---

## 🎯 RPC 接口列表

### 1. 用户信息查询 (RPC)

**服务接口**: `UserFacadeService.query()`

**请求参数**:
```json
{
  "userIdQueryCondition": {
    "userId": 12345
  }
}
```

**或者**:
```json
{
  "userUserNameQueryCondition": {
    "userName": "johndoe"
  }
}
```

**响应示例**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": {
    "userId": 12345,
    "userName": "johndoe",
    "nickName": "John Doe",
    "profilePhotoUrl": "https://example.com/avatar.jpg",
    "email": "john@example.com",
    "role": "USER",
    "status": "ACTIVE"
  }
}
```

### 2. 用户分页查询 (RPC)

**服务接口**: `UserFacadeService.pageQuery()`

**请求参数**:
```json
{
  "pageNo": 1,
  "pageSize": 20,
  "role": "USER",
  "status": "ACTIVE"
}
```

**响应示例**:
```json
{
  "records": [
    {
      "userId": 12345,
      "userName": "johndoe",
      "nickName": "John Doe",
      "profilePhotoUrl": "https://example.com/avatar.jpg",
      "role": "USER",
      "status": "ACTIVE",
      "createTime": "2023-12-01T08:00:00"
    }
  ],
  "total": 156,
  "size": 20,
  "current": 1,
  "pages": 8
}
```

### 3. 用户注册 (RPC)

**服务接口**: `UserFacadeService.register()`

**请求参数**:
```json
{
  "username": "newuser",
  "password": "encrypted_password",
  "email": "newuser@example.com",
  "phone": "13900139000"
}
```

**响应示例**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "注册成功",
  "userId": 12346
}
```

### 4. 用户信息修改 (RPC)

**服务接口**: `UserFacadeService.modify()`

**请求参数**:
```json
{
  "userId": 12345,
  "nickName": "Updated Nickname",
  "bio": "Updated bio",
  "gender": "male",
  "location": "Updated location"
}
```

### 5. 用户认证 (RPC)

**服务接口**: `UserFacadeService.auth()`

**请求参数**:
```json
{
  "userId": 12345,
  "realName": "张三",
  "idCardNo": "110101199001011234",
  "authType": "ID_CARD"
}
```

### 6. 用户激活/冻结 (RPC)

**服务接口**: `UserFacadeService.active()`

**请求参数**:
```json
{
  "userId": 12345,
  "action": "ACTIVE",
  "reason": "用户申请恢复账号"
}
```

---

## 📊 数据模型

### UserInfo

用户完整信息对象

```json
{
  "userId": 12345,
  "userName": "johndoe",
  "nickName": "John Doe",
  "profilePhotoUrl": "https://example.com/avatar.jpg",
  "email": "john@example.com",
  "phone": "13800138000",
  "role": "USER",
  "status": "ACTIVE",
  "bio": "热爱生活，热爱分享",
  "gender": "male",
  "birthday": "1990-01-01",
  "location": "北京市",
  "followerCount": 156,
  "followingCount": 89,
  "contentCount": 42,
  "likeCount": 1205,
  "vipExpireTime": null,
  "bloggerStatus": "none",
  "bloggerApplyTime": null,
  "bloggerApproveTime": null,
  "lastLoginTime": "2024-01-15T10:30:00",
  "createTime": "2023-12-01T08:00:00",
  "updateTime": "2024-01-15T10:30:00"
}
```

### BasicUserInfo

用户基础信息对象

```json
{
  "userId": 12345,
  "userName": "johndoe",
  "nickName": "John Doe",
  "profilePhotoUrl": "https://example.com/avatar.jpg",
  "role": "USER",
  "bloggerStatus": "none"
}
```

### UserModifyRequest

用户信息修改请求对象

```json
{
  "userId": 12345,
  "nickName": "New Nickname",
  "bio": "新的个人简介",
  "gender": "male",
  "birthday": "1990-01-01",
  "location": "深圳市",
  "profilePhotoUrl": "https://example.com/new-avatar.jpg"
}
```

---

## ❌ 错误码定义

### 通用错误码

| 错误码 | HTTP状态码 | 说明 |
|--------|-----------|------|
| 200 | 200 | 成功 |
| 400 | 400 | 请求参数错误 |
| 401 | 401 | 未登录或登录已过期 |
| 403 | 403 | 权限不足 |
| 404 | 404 | 用户不存在 |
| 500 | 500 | 服务器内部错误 |

### 业务错误码

| 错误码 | 说明 |
|--------|------|
| USER_NOT_FOUND | 用户不存在 |
| USER_ALREADY_EXISTS | 用户已存在 |
| INVALID_GENDER | 性别参数无效 |
| EMAIL_ALREADY_EXISTS | 邮箱已被使用 |
| PHONE_ALREADY_EXISTS | 手机号已被使用 |
| BLOGGER_APPLY_REJECTED | 博主申请被拒绝 |
| BLOGGER_APPLY_TOO_FREQUENT | 博主申请过于频繁 |
| USER_STATUS_FROZEN | 用户账号已冻结 |
| USER_UPDATE_ERROR | 用户信息更新失败 |

---

## 💡 使用示例

### 1. 用户信息管理流程示例

```bash
# 1. 获取当前用户信息
curl -X GET "http://localhost:8085/api/v1/users/me" \
  -H "Authorization: Bearer YOUR_TOKEN"

# 2. 更新用户信息
curl -X PUT "http://localhost:8085/api/v1/users/me" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "nickName": "新昵称",
    "bio": "更新后的个人简介",
    "gender": "male",
    "location": "深圳市"
  }'

# 3. 查看其他用户信息
curl -X GET "http://localhost:8085/api/v1/users/67890"

# 4. 申请博主认证
curl -X POST "http://localhost:8085/api/v1/users/blogger/apply" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 2. JavaScript 调用示例

```javascript
// 获取当前用户信息
async function getCurrentUser() {
  try {
    const response = await fetch('/api/v1/users/me', {
      headers: {
        'Authorization': `Bearer ${getToken()}`
      }
    });

    const result = await response.json();
    
    if (result.code === 200) {
      console.log('用户信息:', result.data);
      return result.data;
    } else {
      console.error('获取用户信息失败:', result.message);
    }
  } catch (error) {
    console.error('请求失败:', error);
  }
}

// 更新用户信息
async function updateUserInfo(updateData) {
  try {
    const response = await fetch('/api/v1/users/me', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${getToken()}`
      },
      body: JSON.stringify(updateData)
    });

    const result = await response.json();
    
    if (result.code === 200) {
      console.log('用户信息更新成功:', result.data);
      return result.data;
    } else {
      console.error('用户信息更新失败:', result.message);
    }
  } catch (error) {
    console.error('请求失败:', error);
  }
}

// 申请博主认证
async function applyForBlogger() {
  try {
    const response = await fetch('/api/v1/users/blogger/apply', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${getToken()}`
      }
    });

    const result = await response.json();
    
    if (result.code === 200) {
      alert('博主认证申请已提交，请耐心等待审核结果');
      return true;
    } else {
      alert(`申请失败: ${result.message}`);
      return false;
    }
  } catch (error) {
    console.error('请求失败:', error);
    alert('申请失败，请稍后重试');
    return false;
  }
}
```

### 3. RPC 服务调用示例

```java
@Service
public class UserBusinessService {
    
    @DubboReference(version = "1.0.0")
    private UserFacadeService userFacadeService;
    
    /**
     * 根据用户ID查询用户信息
     */
    public UserInfo getUserById(Long userId) {
        UserQueryRequest request = new UserQueryRequest();
        UserIdQueryCondition condition = new UserIdQueryCondition();
        condition.setUserId(userId);
        request.setUserIdQueryCondition(condition);
        
        UserQueryResponse<UserInfo> response = userFacadeService.query(request);
        
        if (response.getSuccess()) {
            return response.getData();
        } else {
            throw new RuntimeException("查询用户失败: " + response.getResponseMessage());
        }
    }
    
    /**
     * 根据用户名查询用户信息
     */
    public UserInfo getUserByUsername(String username) {
        UserQueryRequest request = new UserQueryRequest();
        UserUserNameQueryCondition condition = new UserUserNameQueryCondition();
        condition.setUserName(username);
        request.setUserUserNameQueryCondition(condition);
        
        UserQueryResponse<UserInfo> response = userFacadeService.query(request);
        
        if (response.getSuccess()) {
            return response.getData();
        }
        return null;
    }
}
```

---

## 📝 注意事项

### 1. 数据安全
- 用户密码使用BCrypt加密存储
- 敏感信息（手机号、邮箱）仅对本人可见
- 用户隐私设置控制信息公开范围

### 2. 性能优化
- 用户基础信息与扩展信息分表存储
- 热点用户信息使用Redis缓存
- 头像等静态资源使用CDN加速

### 3. 业务规则
- 用户名注册后不可修改
- 昵称可以重复，但建议唯一性
- 博主认证有严格的审核流程
- 用户状态变更需要管理员权限

### 4. 接口限制
- 个人信息更新频率限制：每分钟最多3次
- 博主申请频率限制：每30天最多1次
- 头像上传大小限制：最大2MB
- 个人简介长度限制：最多500字符

---

## 📞 技术支持

- **开发团队**: Collide Team
- **文档版本**: v1.0
- **更新日期**: 2024-01-15
- **联系方式**: tech@collide.com

---

*本文档基于 User 模块 v1.0.0 版本生成，如有疑问请联系技术团队。* 
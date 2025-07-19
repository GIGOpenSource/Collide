# Collide 用户服务接口文档

## 目录

- [服务介绍](#服务介绍)
- [统一响应格式](#统一响应格式)
- [接口列表](#接口列表)
  - [获取用户信息](#获取用户信息)
  - [根据手机号查询用户](#根据手机号查询用户)
  - [修改昵称](#修改昵称)
  - [修改密码](#修改密码)
  - [修改头像](#修改头像)
  - [获取邀请排行榜](#获取邀请排行榜)
  - [获取我的邀请排名](#获取我的邀请排名)
  - [获取邀请列表](#获取邀请列表)
- [错误码说明](#错误码说明)
- [数据字典](#数据字典)
- [注意事项](#注意事项)

---

## 服务介绍

**服务名称：** collide-users  
**服务端口：** 8083  
**网关访问：** http://localhost:8081  
**服务描述：** 负责用户信息管理、用户操作、邀请系统等功能

---

## 统一响应格式

**普通响应格式：**
```json
{
    "code": "string",      // 响应码
    "success": boolean,    // 是否成功
    "message": "string",   // 响应消息
    "data": object        // 响应数据
}
```

**分页响应格式：**
```json
{
    "code": "string",
    "success": boolean,
    "message": "string",
    "data": [],           // 数据列表
    "total": number,      // 总记录数
    "page": number,       // 当前页码
    "size": number        // 每页大小
}
```

---

## 接口列表

### 获取用户信息

**接口地址：** `GET /users/getUserInfo`

**请求头：**
```http
Authorization: Bearer {token}
```

**请求示例：**
```http
GET /users/getUserInfo
Authorization: Bearer eyJ0eXAiOiJKV1Q...
```

**响应示例：**
```json
{
    "code": "SUCCESS",
    "success": true,
    "message": "SUCCESS",
    "data": {
        "userId": 123,                          // 用户ID
        "nickName": "CD_ABC123",                // 用户昵称
        "userName": "testuser",                 // 用户名
        "telephone": "138****0001",             // 手机号(脱敏)
        "state": "ACTIVE",                      // 用户状态
        "blockChainUrl": "0x1234...",          // 区块链地址
        "blockChainPlatform": "ETH",           // 区块链平台
        "certification": true,                  // 是否实名认证
        "userRole": "CUSTOMER",                // 用户角色
        "inviteCode": "ABC123",                // 邀请码
        "createTime": "2023-01-01T00:00:00",   // 注册时间
        "profilePhotoUrl": "https://..."       // 头像URL
    }
}
```

**可能的错误：**
- `USER_NOT_EXIST`: 用户不存在
- `USER_NOT_LOGIN`: 用户未登录

**说明：**
- 需要登录认证
- 通过Token自动获取当前用户信息
- 敏感信息已脱敏处理

---

### 根据手机号查询用户

**接口地址：** `GET /users/queryUserByTel`

**请求参数：**
| 参数名 | 类型 | 必填 | 描述 | 示例 |
|-------|------|------|------|------|
| telephone | string | 是 | 手机号 | 13800000001 |

**请求示例：**
```http
GET /users/queryUserByTel?telephone=13800000001
```

**响应示例：**
```json
{
    "code": "SUCCESS",
    "success": true,
    "message": "SUCCESS",
    "data": {
        "userId": 123,                   // 用户ID
        "nickName": "CD_ABC123",         // 用户昵称
        "profilePhotoUrl": "https://..." // 头像URL
    }
}
```

**可能的错误：**
- `USER_NOT_EXIST`: 用户不存在

**说明：**
- 公开接口，无需认证
- 返回基础用户信息
- 用于用户搜索、展示等场景

---

### 修改昵称

**接口地址：** `POST /users/modifyNickName`

**请求头：**
```http
Authorization: Bearer {token}
Content-Type: application/json
```

**请求体：**
```json
{
    "nickName": "新昵称"    // 新昵称(必填)
}
```

**参数说明：**
| 参数名 | 类型 | 必填 | 描述 | 验证规则 |
|-------|------|------|------|----------|
| nickName | string | 是 | 新昵称 | 非空，长度限制 |

**请求示例：**
```http
POST /users/modifyNickName
Authorization: Bearer eyJ0eXAiOiJKV1Q...
Content-Type: application/json

{
    "nickName": "我的新昵称"
}
```

**响应示例：**
```json
{
    "code": "SUCCESS",
    "success": true,
    "message": "SUCCESS",
    "data": true
}
```

**可能的错误：**
- `NICK_NAME_EXIST`: 昵称已存在
- `USER_STATUS_CANT_OPERATE`: 用户状态不允许修改
- `USER_NOT_EXIST`: 用户不存在

**说明：**
- 需要登录认证
- 昵称唯一性检查使用布隆过滤器优化
- 只有特定状态的用户才能修改昵称
- 修改成功会记录操作流水

---

### 修改密码

**接口地址：** `POST /users/modifyPassword`

**请求头：**
```http
Authorization: Bearer {token}
Content-Type: application/json
```

**请求体：**
```json
{
    "oldPassword": "旧密码",    // 旧密码(必填)
    "newPassword": "新密码"     // 新密码(必填)
}
```

**参数说明：**
| 参数名 | 类型 | 必填 | 描述 | 验证规则 |
|-------|------|------|------|----------|
| oldPassword | string | 是 | 旧密码 | 非空 |
| newPassword | string | 是 | 新密码 | 非空 |

**请求示例：**
```http
POST /users/modifyPassword
Authorization: Bearer eyJ0eXAiOiJKV1Q...
Content-Type: application/json

{
    "oldPassword": "oldpass123",
    "newPassword": "newpass456"
}
```

**响应示例：**
```json
{
    "code": "SUCCESS",
    "success": true,
    "message": "SUCCESS",
    "data": true
}
```

**可能的错误：**
- `USER_PASSWD_CHECK_FAIL`: 旧密码错误
- `USER_NOT_EXIST`: 用户不存在
- `USER_STATUS_CANT_OPERATE`: 用户状态不允许修改

**说明：**
- 需要登录认证
- 必须验证旧密码
- 新密码使用MD5加密存储
- 修改成功会记录操作流水

---

### 修改头像

**接口地址：** `POST /users/modifyProfilePhoto`

**请求头：**
```http
Authorization: Bearer {token}
Content-Type: multipart/form-data
```

**请求参数：**
| 参数名 | 类型 | 必填 | 描述 | 限制 |
|-------|------|------|------|------|
| file_data | file | 是 | 头像文件 | 图片格式，大小限制 |

**请求示例：**
```http
POST /users/modifyProfilePhoto
Authorization: Bearer eyJ0eXAiOiJKV1Q...
Content-Type: multipart/form-data

[文件上传]
```

**响应示例：**
```json
{
    "code": "SUCCESS",
    "success": true,
    "message": "SUCCESS",
    "data": "https://nfturbo-file.oss-cn-hangzhou.aliyuncs.com/profile/123/avatar.jpg"
}
```

**可能的错误：**
- `USER_UPLOAD_PICTURE_FAIL`: 文件上传失败
- `USER_NOT_EXIST`: 用户不存在

**说明：**
- 需要登录认证
- 支持常见图片格式
- 文件存储到OSS
- 文件路径：`profile/{userId}/{filename}`
- 返回完整的文件访问URL

---

### 获取邀请排行榜

**接口地址：** `GET /user/invite/getTopN`

**请求参数：**
| 参数名 | 类型 | 必填 | 描述 | 默认值 | 最大值 |
|-------|------|------|------|-------|-------|
| topN | integer | 否 | 排行榜数量 | 100 | 100 |

**请求示例：**
```http
GET /user/invite/getTopN?topN=10
```

**响应示例：**
```json
{
    "code": "SUCCESS",
    "success": true,
    "message": "SUCCESS",
    "data": [
        {
            "nickName": "C***23",        // 脱敏处理的昵称
            "inviteCode": "ABC123",      // 邀请码
            "inviteScore": 50            // 邀请积分
        },
        {
            "nickName": "D***45",
            "inviteCode": "DEF456",
            "inviteScore": 30
        }
    ],
    "total": 100,
    "page": 1,
    "size": 10
}
```

**说明：**
- 公开接口，无需认证
- 使用Redis有序集合实现排行榜
- 昵称自动脱敏处理
- 按邀请积分倒序排列
- 最多返回100条记录

---

### 获取我的邀请排名

**接口地址：** `GET /user/invite/getMyRank`

**请求头：**
```http
Authorization: Bearer {token}
```

**请求示例：**
```http
GET /user/invite/getMyRank
Authorization: Bearer eyJ0eXAiOiJKV1Q...
```

**响应示例：**
```json
{
    "code": "SUCCESS",
    "success": true,
    "message": "SUCCESS",
    "data": 5    // 排名，null表示未上榜
}
```

**说明：**
- 需要登录认证
- 返回当前用户在邀请排行榜中的排名
- 排名从1开始计算
- 未上榜返回null

---

### 获取邀请列表

**接口地址：** `GET /user/invite/getInviteList`

**请求头：**
```http
Authorization: Bearer {token}
```

**请求参数：**
| 参数名 | 类型 | 必填 | 描述 |
|-------|------|------|------|
| currentPage | integer | 是 | 当前页码 |

**请求示例：**
```http
GET /user/invite/getInviteList?currentPage=1
Authorization: Bearer eyJ0eXAiOiJKV1Q...
```

**响应示例：**
```json
{
    "code": "SUCCESS",
    "success": true,
    "message": "SUCCESS",
    "data": [
        {
            "userId": 124,
            "nickName": "被邀请用户1",
            "profilePhotoUrl": "https://...",
            "createTime": "2023-01-01T00:00:00"
        },
        {
            "userId": 125,
            "nickName": "被邀请用户2",
            "profilePhotoUrl": "https://...",
            "createTime": "2023-01-02T00:00:00"
        }
    ],
    "total": 100,
    "page": 1,
    "size": 20
}
```

**说明：**
- 需要登录认证
- 查询当前用户邀请的所有用户
- 每页固定20条记录
- 按注册时间倒序排列

---

## 错误码说明

### 用户相关错误码

| 错误码 | 描述 | HTTP状态码 | 说明 |
|-------|------|-----------|------|
| DUPLICATE_TELEPHONE_NUMBER | 重复电话号码 | 200 | 手机号已被注册 |
| DUPLICATE_USERNAME_NUMBER | 重复用户名 | 200 | 用户名已存在 |
| USER_STATUS_IS_NOT_INIT | 用户状态不能进行实名认证 | 200 | 只有INIT状态才能实名认证 |
| USER_NOT_EXIST | 用户不存在 | 200 | 用户不存在 |
| USER_STATUS_CANT_OPERATE | 用户状态不能进行操作 | 200 | 用户状态不允许当前操作 |
| USER_STATUS_IS_NOT_ACTIVE | 用户状态未激活成功 | 200 | 用户未完成激活流程 |
| USER_OPERATE_FAILED | 用户操作失败 | 200 | 用户操作执行失败 |
| USER_AUTH_FAIL | 用户实名认证失败 | 200 | 实名认证验证失败 |
| USER_PASSWD_CHECK_FAIL | 用户密码校验失败 | 200 | 密码验证失败 |
| USER_QUERY_FAIL | 用户查询失败 | 200 | 用户查询异常 |
| NICK_NAME_EXIST | 昵称已存在 | 200 | 昵称重复 |
| USER_CREATE_CHAIN_FAIL | 用户创建链账号失败 | 200 | 区块链账号创建失败 |
| USER_STATUS_IS_NOT_AUTH | 用户状态不能进行激活 | 200 | 只有AUTH状态才能激活 |
| USER_UPLOAD_PICTURE_FAIL | 用户上传图片失败 | 200 | 文件上传异常 |

### 通用错误码

| 错误码 | 描述 | HTTP状态码 |
|-------|------|-----------|
| SYSTEM_ERROR | 系统错误 | 200 |
| SUCCESS | 成功 | 200 |
| USER_NOT_LOGIN | 用户未登录 | 401 |

---

## 数据字典

### 用户状态 (UserStateEnum)

| 状态值 | 描述 | 说明 | 可执行操作 |
|-------|------|------|-----------|
| INIT | 初始状态 | 注册成功，未实名认证 | 实名认证、修改信息 |
| AUTH | 已认证 | 实名认证通过 | 激活、修改信息 |
| ACTIVE | 已激活 | 上链成功，完全激活 | 所有操作 |
| FROZEN | 已冻结 | 账户被冻结 | 受限操作 |

### 用户角色 (UserRole)

| 角色值 | 描述 | 权限说明 |
|-------|------|----------|
| CUSTOMER | 普通用户 | 基础功能权限 |
| ARTIST | 艺术家 | 创作者权限 |
| ADMIN | 管理员 | 管理权限 |

### 用户权限 (UserPermission)

| 权限值 | 描述 | 适用场景 |
|-------|------|----------|
| BASIC | 基本权限 | 基础操作 |
| AUTH | 认证权限 | 需要实名认证的操作 |
| PRO | 付费权限 | 付费功能 |
| FROZEN | 冻结权限 | 冻结状态下的有限操作 |
| NONE | 无权限 | 禁止所有操作 |

### 操作类型 (UserOperateTypeEnum)

| 类型值 | 描述 | 触发场景 |
|-------|------|----------|
| FREEZE | 冻结 | 管理员冻结用户 |
| UNFREEZE | 解冻 | 管理员解冻用户 |
| LOGIN | 登录 | 用户登录 |
| REGISTER | 注册 | 用户注册 |
| ACTIVE | 激活 | 用户激活 |
| AUTH | 实名认证 | 用户实名认证 |
| MODIFY | 修改信息 | 用户修改个人信息 |

### 用户信息对象 (UserInfo)

| 字段名 | 类型 | 描述 | 是否脱敏 |
|-------|------|------|----------|
| userId | Long | 用户ID | 否 |
| nickName | String | 用户昵称 | 是 |
| userName | String | 用户名 | 否 |
| telephone | String | 手机号 | 是 |
| state | String | 用户状态 | 否 |
| blockChainUrl | String | 区块链地址 | 否 |
| blockChainPlatform | String | 区块链平台 | 否 |
| certification | Boolean | 是否实名认证 | 否 |
| userRole | String | 用户角色 | 否 |
| inviteCode | String | 邀请码 | 否 |
| createTime | Date | 注册时间 | 否 |
| profilePhotoUrl | String | 头像URL | 否 |

### 邀请排行信息 (InviteRankInfo)

| 字段名 | 类型 | 描述 | 是否脱敏 |
|-------|------|------|----------|
| nickName | String | 用户昵称 | 是 |
| inviteCode | String | 邀请码 | 否 |
| inviteScore | Integer | 邀请积分 | 否 |

---

## 注意事项

### 1. 数据安全
- **数据脱敏：** 手机号、昵称等敏感信息在响应中会进行脱敏处理
- **敏感数据加密：** 身份证号、真实姓名使用AES加密存储
- **权限控制：** 不同状态用户有不同的操作权限

### 2. 性能优化
- **缓存机制：** 用户信息使用JetCache进行两级缓存
- **布隆过滤器：** 昵称、邀请码重复检查使用Redis布隆过滤器
- **延迟删除：** 用户信息变更后延迟删除缓存
- **分页查询：** 大数据量查询统一使用分页

### 3. 并发控制
- **分布式锁：** 注册、修改等关键操作使用分布式锁
- **乐观锁：** 数据修改使用版本号进行乐观锁控制
- **事务管理：** 关键操作使用数据库事务

### 4. 操作审计
- **操作流水：** 用户的关键操作会记录到`user_operate_stream`表
- **日志内容：** 包含用户ID、操作类型、操作时间、操作参数等
- **流水查询：** 支持按用户、时间、操作类型查询

### 5. 文件管理
- **存储方式：** 使用阿里云OSS存储文件
- **路径规则：** `profile/{userId}/{filename}`
- **访问控制：** 支持公开访问
- **大小限制：** 头像文件大小限制

### 6. 邀请系统
- **排行榜：** 使用Redis有序集合实现高性能排行榜
- **积分规则：** 邀请成功用户获得积分
- **数据脱敏：** 排行榜数据自动脱敏
- **实时更新：** 邀请关系实时更新排行榜

### 7. 数据一致性
- **缓存一致性：** 使用缓存更新和延迟删除策略
- **主从同步：** 考虑数据库主从延迟
- **最终一致性：** 部分场景采用最终一致性

### 8. 开发调试
- **日志级别：** 开发环境使用DEBUG级别
- **Mock数据：** 支持测试数据初始化
- **性能监控：** 关键操作有性能监控
```

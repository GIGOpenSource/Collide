# Collide 认证服务 v2.0

> 基于简洁版用户API 2.0重构的高效认证系统

## 🆕 v2.0 更新内容
- ✅ **API重构**: 基于简洁版用户模块API完全重构
- ✅ **性能优化**: 底层调用更加高效，减少复杂转换
- ✅ **架构简化**: 删除冗余组件，保持核心功能
- ✅ **错误统一**: 标准化错误处理和响应格式
- ✅ **兼容保证**: 前端接口100%兼容，无需修改

## 🚀 快速开始

### 核心接口（推荐）
```bash
# 登录或注册 - 一个接口解决所有需求
curl -X POST http://localhost:9502/api/v1/auth/login-or-register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "123456"
  }'
```

### 基础流程
1. **注册/登录** → 获取 token
2. **携带 token** → 访问其他接口
3. **登出** → 清除会话

## 📋 接口概览

| 接口 | 方法 | 路径 | 描述 | 状态 |
|------|------|------|------|------|
| 🔥 **登录或注册** | POST | `/login-or-register` | 核心接口，自动注册 | ✅ v2.0优化 |
| 📝 注册 | POST | `/register` | 传统注册 | ✅ v2.0重构 |
| 🔑 登录 | POST | `/login` | 传统登录 | ✅ v2.0重构 |
| 🚪 登出 | POST | `/logout` | 退出登录 | ✅ 保持兼容 |
| 👤 当前用户 | GET | `/me` | 获取用户信息 | ✅ v2.0新增 |
| 🔍 Token验证 | GET | `/verify-token` | 验证Token | ✅ v2.0优化 |
| 🎫 验证邀请码 | GET | `/validate-invite-code` | 邀请码验证 | ✅ 保持兼容 |
| 📊 邀请信息 | GET | `/my-invite-info` | 我的邀请统计 | ✅ v2.0优化 |
| ❤️ 健康检查 | GET | `/test` | 服务状态 | ✅ 保持兼容 |

## ✨ 设计特色

### 🎯 **v2.0架构升级**
- ✅ **简洁API**: 基于用户模块2.0的简洁API
- ✅ **高效调用**: 直接调用`login()`等核心方法
- ✅ **统一响应**: 全面使用`Result<T>`标准格式
- ✅ **错误标准**: 统一错误码和消息格式

### 🔐 **安全保障**
- ✅ 密码加盐哈希存储（用户模块处理）
- ✅ 完整参数验证
- ✅ 详细操作日志
- ✅ Token 会话管理

## 🔧 v2.0 技术栈

### 后端集成
```java
// v2.0 简洁版API调用示例
@DubboReference(version = "2.0.0")
private UserFacadeService userFacadeService;

// 直接登录验证
Result<UserResponse> result = userFacadeService.login(username, password);

// 用户注册
UserCreateRequest request = new UserCreateRequest();
Result<UserResponse> result = userFacadeService.createUser(request);
```

### 响应格式
```json
{
  "success": true,
  "code": "SUCCESS", 
  "message": "登录成功",
  "data": {
    "user": {
      "id": 12345,
      "username": "testuser",
      "nickname": "测试用户",
      "role": "user"
    },
    "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...",
    "isNewUser": false
  }
}
```

## 🔧 使用示例

### JavaScript/Node.js
```javascript
// 登录或注册
const auth = await fetch('/api/v1/auth/login-or-register', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    username: 'demo',
    password: '123456'
  })
});

const { data } = await auth.json();
const token = data.token;

// 后续请求携带 token
const profile = await fetch('/api/v1/auth/my-invite-info', {
  headers: { 'Authorization': `Bearer ${token}` }
});
```

### Python
```python
import requests

# 登录或注册
response = requests.post('http://localhost:8080/api/v1/auth/login-or-register', 
                        json={'username': 'demo', 'password': '123456'})
token = response.json()['data']['token']

# 后续请求
headers = {'Authorization': f'Bearer {token}'}
profile = requests.get('http://localhost:8080/api/v1/auth/my-invite-info', 
                      headers=headers)
```

### cURL
```bash
# 1. 登录获取 token
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login-or-register \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"123456"}' | \
  jq -r '.data.token')

# 2. 使用 token 访问接口
curl -X GET http://localhost:8080/api/v1/auth/my-invite-info \
  -H "Authorization: Bearer $TOKEN"
```

## 📖 详细文档

查看完整 API 文档：[API-Documentation.md](./API-Documentation.md)

## 🏗️ 项目结构

```
collide-auth/
├── src/main/java/com/gig/collide/auth/
│   ├── controller/          # 控制器层
│   │   ├── AuthController.java     # 认证控制器
│   │   └── TokenController.java    # Token 管理
│   ├── param/              # 请求参数
│   │   ├── RegisterParam.java      # 注册参数
│   │   ├── LoginParam.java         # 登录参数
│   │   └── LoginOrRegisterParam.java # 登录或注册参数
│   ├── exception/          # 异常定义
│   │   ├── AuthException.java      # 认证异常
│   │   └── AuthErrorCode.java      # 错误码
│   └── ...
├── pom.xml                 # Maven 配置
├── Dockerfile              # Docker 配置
├── README.md               # 快速入门（本文件）
└── API-Documentation.md    # 完整 API 文档
```

## 🎉 特别推荐

### `/login-or-register` 接口
这是我们的核心接口，参考 nft-turbo 设计理念：

**特点**:
- 🔥 用户不存在时自动注册
- 🎯 一个接口解决所有认证需求
- ✨ 通过 `isNewUser` 字段区分新老用户
- 🚀 大大简化前端逻辑

**建议前端这样处理**:
```javascript
const handleAuth = async (username, password) => {
  const response = await authService.loginOrRegister(username, password);
  
  if (response.data.isNewUser) {
    // 新用户，显示欢迎信息
    showWelcomeMessage();
  } else {
    // 老用户，直接进入系统
    redirectToHome();
  }
};
```

---

**服务版本**: v2.0  
**技术栈**: Spring Boot + Sa-Token + Dubbo  
**设计理念**: 参考 nft-turbo，简化认证流程 
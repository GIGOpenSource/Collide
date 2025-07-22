# Collide API 模块

## 📋 概述
Collide API 模块按业务领域重新组织，提供统一的公共API接口、请求响应对象、常量定义和RPC服务接口。

## 🏗️ 架构设计

### 设计原则
- **业务内聚**: 相关功能聚合在同一业务域
- **低耦合**: 业务域之间最小化依赖
- **统一规范**: 所有业务域遵循相同的目录结构
- **向下兼容**: 保持现有接口不变，逐步迁移

### 目录结构规范
每个业务域都包含以下标准目录：
```
{domain}/
├── constant/     # 常量定义
├── request/      # 请求对象
├── response/     # 响应对象  
├── service/      # RPC服务接口
└── enums/        # 枚举定义
```

## 📁 当前业务域

### 🔧 common/ - 通用组件
**作用**: 提供各业务域共享的基础组件
```
common/
├── constant/
│   ├── CommonConstant.java          # 通用常量
│   ├── BusinessCode.java            # 业务代码枚举
│   └── BizOrderType.java            # 订单类型枚举
├── request/
│   └── BasePageRequest.java         # 通用分页请求基类
├── response/
│   └── BaseListResponse.java        # 通用列表响应基类
└── enums/
    └── BaseStatus.java              # 通用状态枚举
```

### 🔐 auth/ - 认证授权
**作用**: 提供认证、授权、OAuth2.0相关的API定义
```
auth/
├── constant/
│   └── AuthConstant.java            # 认证相关常量
├── request/
│   └── LoginRequest.java            # 统一登录请求
├── response/
│   └── LoginResponse.java           # 登录响应
├── service/
│   └── AuthFacadeService.java       # 认证RPC服务接口
└── enums/
    └── LoginType.java               # 登录类型枚举
```

**核心功能**:
- 多方式登录支持（用户名密码、手机验证码、第三方OAuth）
- OAuth2.0完整流程（授权码、访问令牌、刷新令牌）
- 验证码发送和验证
- Token验证和刷新

### 👤 user/ - 用户管理
**作用**: 提供用户信息管理相关API
```
user/
├── constant/           # 用户相关常量
├── request/
│   ├── condition/      # 查询条件对象
│   └── UserModifyRequest.java       # 用户修改请求
├── response/
│   └── data/
│       ├── BasicUserInfo.java       # 基础用户信息
│       └── UserInfo.java            # 完整用户信息
├── service/
│   ├── UserFacadeService.java       # 用户RPC服务接口
│   └── UserManageFacadeService.java # 用户管理RPC服务接口
└── enums/
    ├── UserRole.java                # 用户角色枚举
    ├── UserStateEnum.java           # 用户状态枚举
    ├── UserType.java                # 用户类型枚举
    ├── UserOperateTypeEnum.java     # 用户操作类型枚举
    └── UserPermission.java          # 用户权限枚举
```

### 📝 content/ - 内容管理
**作用**: 提供内容发布、管理、审核相关API
```
content/
├── constant/
│   └── ContentConstant.java         # 内容相关常量
└── enums/
    └── ContentType.java             # 内容类型枚举
```

**支持的内容类型**:
- 文本内容、图片内容、视频内容、音频内容
- 链接分享、混合内容
- 完整的审核流程和状态管理

### 🌟 social/ - 社交互动
**作用**: 提供社交动态、时间线、通知相关API
```
social/
├── constant/
│   └── SocialConstant.java          # 社交相关常量
└── ...
```

**核心功能**:
- 动态发布（原创、转发、回复）
- 多种可见性控制
- 完整的互动类型（点赞、评论、转发、收藏、分享）
- 多类型通知系统
- 多种时间线（主页、关注、推荐、热门、个人）

### 💫 interaction/ - 互动管理
**作用**: 提供点赞、收藏、评论等互动功能API
```
interaction/
└── enums/
    └── InteractionType.java         # 互动类型枚举
```

**支持的互动类型**:
- 点赞/取消点赞、收藏/取消收藏
- 评论/删除评论、转发/取消转发  
- 分享、关注/取消关注、举报

## 🚀 使用指南

### 1. 添加依赖
```xml
<dependency>
    <groupId>com.gig</groupId>
    <artifactId>collide-api</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 2. 使用通用组件
```java
// 使用通用分页请求
public class UserPageRequest extends BasePageRequest {
    private String keyword;
    // ...
}

// 使用通用状态枚举
BaseStatus status = BaseStatus.ACTIVE;

// 使用通用列表响应
BaseListResponse<UserInfo> response = BaseListResponse.success(userList, totalCount);
```

### 3. 实现RPC服务
```java
@DubboService
public class AuthFacadeServiceImpl implements AuthFacadeService {
    
    @Override
    public LoginResponse login(LoginRequest request) {
        // 实现登录逻辑
        return LoginResponse.success(accessToken, refreshToken, expiresIn, userId, username);
    }
}
```

### 4. 使用枚举和常量
```java
// 使用登录类型枚举
if (loginType == LoginType.USERNAME_PASSWORD) {
    // 用户名密码登录逻辑
}

// 使用认证常量
String tokenHeader = AuthConstant.Token.TOKEN_HEADER;
int tokenExpire = AuthConstant.Token.DEFAULT_ACCESS_TOKEN_EXPIRE;
```

## 📈 扩展规划

### 即将添加的业务域

1. **follow/** - 关注关系管理
2. **category/** - 分类管理
3. **tag/** - 标签管理  
4. **payment/** - 支付系统
5. **statistics/** - 统计分析
6. **admin/** - 管理后台

### 扩展原则

1. **遵循现有规范**: 新业务域必须遵循标准目录结构
2. **保持向下兼容**: 不破坏现有接口
3. **文档先行**: 新增业务域需要完整的API文档
4. **测试覆盖**: 确保RPC接口有完整的测试用例

## 🔄 迁移指南

### 对于新开发
- 直接使用新的业务域API
- 遵循统一的命名规范
- 使用通用基类减少重复代码

### 对于现有代码
- 逐步迁移到新的API结构
- 保持现有接口的向下兼容性
- 新功能优先使用新结构

## 📚 相关文档
- [API设计规范](../../../docs/api-design-standards.md)
- [RPC服务开发指南](../../../docs/rpc-development-guide.md)
- [数据库设计规范](../../../docs/database-design-standards.md)

---

**通过业务域组织，Collide API模块实现了更好的代码复用和模块解耦！** 🚀 
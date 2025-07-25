# Collide 项目 Docker 部署指南

## 📋 部署架构

本项目采用微服务架构，包含中间件和业务服务两个部分，所有服务都运行在同一个自定义Docker网络中，使用固定IP地址进行通信。

### 🌐 网络配置

- **网络名称**: `collide-network`
- **子网范围**: `172.20.0.0/16`
- **网关地址**: `172.20.0.1`

### 📍 IP 地址分配

#### 中间件服务 (172.20.1.x)
| 服务 | 容器名 | IP地址 | 端口映射 | 用途 |
|------|--------|--------|----------|------|
| MySQL | mysql | 172.20.1.10 | 3306:3306 | 数据库 |
| MinIO | minio | 172.20.1.20 | 9000:9000, 9001:9001 | 对象存储 |
| Nacos | nacos | 172.20.1.30 | 8848:8848, 9848:9848 | 服务注册/配置中心 |
| Redis | redis | 172.20.1.40 | 6379:6379 | 缓存数据库 |
| Seata | seata-server | 172.20.1.50 | 7091:7091, 8091:8091 | 分布式事务 |
| Sentinel | sentinel-dashboard | 172.20.1.60 | 8888:8888 | 流量控制 |
| RocketMQ NameServer | rocketmq-nameserver | 172.20.1.70 | 9876:9876 | 消息队列命名服务 |
| RocketMQ Broker | rocketmq-broker | 172.20.1.71 | 10909:10909, 10911:10911 | 消息队列代理 |
| Elasticsearch | elasticsearch | 172.20.1.80 | 9200:9200, 9300:9300 | 搜索引擎 |

#### 业务服务 (172.20.2.x)
| 服务 | 容器名 | IP地址 | 端口映射 | 用途 |
|------|--------|--------|----------|------|
| Gateway | collide-gateway | 172.20.2.10 | 9501:9501 | API网关 |
| Auth | collide-auth | 172.20.2.20 | 9502:9502 | 认证服务 |
| Application | collide-application | 172.20.2.30 | 9503:9503 | 业务聚合服务 |

## 🚀 部署步骤

### 1. 环境准备

确保您的系统已安装：
- Docker (20.10+)
- Docker Compose (2.0+)

### 2. 创建网络并初始化数据库

在项目根目录下运行网络初始化脚本，脚本会自动：
- 创建Docker自定义网络
- 检测MySQL容器状态
- 自动创建collide相关数据库
- 按顺序导入所有SQL文件

**Linux/macOS:**
```bash
# 赋予执行权限
chmod +x scripts/init-network.sh

# 运行脚本（会询问是否初始化数据库）
./scripts/init-network.sh
```

**Windows:**
```cmd
# 运行批处理脚本（会询问是否初始化数据库）
scripts\init-network.bat
```

**数据库初始化功能包括：**
- 自动创建数据库：`collide_auth`、`collide_business`、`nacos_mysql`
- 按顺序导入SQL文件：
  1. `01-init-database.sql` - 初始化数据库结构
  2. `nacos-mysql.sql` - Nacos配置中心表
  3. `02-auth-tables.sql` - 认证相关表
  4. `02-user-profile-table.sql` - 用户配置表
  5. `collide-business.sql` - 业务核心表
  6. `03-fix-status-field.sql` - 状态字段修复
  7. `04-performance-optimization.sql` - 性能优化脚本
  8. `06-search-tables.sql` - 搜索功能表
  9. `07-category-tag-system.sql` - 分类标签系统表

### 3. 启动中间件服务

```bash
# 进入中间件目录
cd middleware

# 启动所有中间件服务
docker-compose up -d

# 查看服务状态
docker-compose ps
```

### 4. 等待中间件初始化

等待关键服务启动完成（特别是MySQL和Nacos）：

```bash
# 检查MySQL健康状态
docker logs mysql

# 检查Nacos启动状态
docker logs nacos
```

### 5. 启动业务服务

```bash
# 返回项目根目录
cd ..

# 构建并启动业务服务
docker-compose up -d --build

# 查看服务状态
docker-compose ps
```

## 🔧 配置说明

### 环境变量配置

业务服务已预配置所有中间件的固定IP地址：

- **Nacos**: `172.20.1.30:8848`
- **MySQL**: `172.20.1.10:3306`
- **Redis**: `172.20.1.40:6379`
- **RocketMQ**: `172.20.1.70:9876`
- **Elasticsearch**: `172.20.1.80:9200`
- **Seata**: `172.20.1.50:8091`
- **MinIO**: `172.20.1.20:9000`

### 数据库自动初始化

如果在步骤2中选择了数据库初始化，所有数据库和表已自动创建。如果跳过了初始化，可以手动运行：

```bash
# 手动运行数据库初始化（确保MySQL容器已启动）
./scripts/init-network.sh  # Linux/macOS
# 或
scripts\init-network.bat   # Windows
```

## 📊 服务监控

### 访问地址

- **Nacos控制台**: http://localhost:8848/nacos (nacos/nacos)
- **Sentinel控制台**: http://localhost:8888 (sentinel/sentinel)
- **MinIO控制台**: http://localhost:9001 (minioadmin/minioadmin)
- **Elasticsearch**: http://localhost:9200
- **Redis**: localhost:6379 (密码: 123456)

### 健康检查

```bash
# 检查网络连接
docker network inspect collide-network

# 查看所有容器状态
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

# 查看服务日志
docker logs <container_name>
```

## 🔍 故障排除

### 常见问题

1. **网络连接问题**
   ```bash
   # 重新创建网络
   docker network rm collide-network
   ./scripts/init-network.sh
   ```

2. **端口冲突**
   ```bash
   # 检查端口占用
   netstat -tlnp | grep <port>
   ```

3. **服务启动失败**
   ```bash
   # 查看详细日志
   docker-compose logs <service_name>
   
   # 重启服务
   docker-compose restart <service_name>
   ```

### 清理和重置

```bash
# 停止所有服务
docker-compose down
cd middleware && docker-compose down

# 删除所有容器和镜像（谨慎使用）
docker system prune -af

# 删除数据卷（会丢失数据）
docker volume prune -f
```

## 🔐 安全注意事项

1. **生产环境部署**：
   - 修改所有默认密码
   - 使用环境变量文件管理敏感信息
   - 配置防火墙规则限制外部访问

2. **网络安全**：
   - 生产环境建议使用更复杂的网络配置
   - 考虑使用Docker Swarm或Kubernetes进行编排

3. **数据备份**：
   - 定期备份MySQL数据
   - 备份Nacos配置
   - 备份MinIO存储数据

## 📞 技术支持

如果在部署过程中遇到问题，请：

1. 检查Docker和Docker Compose版本
2. 确认网络配置正确
3. 查看相关服务日志
4. 参考项目文档或联系技术支持

---

**注意**: 此配置适用于开发和测试环境。生产环境部署请根据实际需求调整配置。 
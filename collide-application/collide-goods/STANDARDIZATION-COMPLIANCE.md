# Collide Goods 模块标准化合规说明

## 📋 概述

本文档说明 `collide-goods` 模块如何严格遵循 Collide 项目的标准化架构思想，确保与整体架构的一致性和可维护性。

## 🏗️ 标准化架构合规性

### 1. 模块依赖标准化

#### ✅ 使用标准化组件

```xml
<!-- 使用标准化的Collide组件 -->
<dependency>
    <groupId>com.gig.collide</groupId>
    <artifactId>collide-base</artifactId>        <!-- 基础组件 -->
</dependency>
<dependency>
    <groupId>com.gig.collide</groupId>
    <artifactId>collide-api</artifactId>         <!-- API定义 -->
</dependency>
<dependency>
    <groupId>com.gig.collide</groupId>
    <artifactId>collide-web</artifactId>         <!-- Web组件 -->
</dependency>
<dependency>
    <groupId>com.gig.collide</groupId>
    <artifactId>collide-datasource</artifactId>  <!-- 数据源组件 -->
</dependency>
<dependency>
    <groupId>com.gig.collide</groupId>
    <artifactId>collide-cache</artifactId>       <!-- 缓存组件 -->
</dependency>
<dependency>
    <groupId>com.gig.collide</groupId>
    <artifactId>collide-rpc</artifactId>         <!-- RPC组件 -->
</dependency>
```

#### ❌ 避免直接依赖

```xml
<!-- ❌ 已移除直接依赖，通过标准化组件提供 -->
<!-- 
<dependency>
    <groupId>org.apache.dubbo</groupId>
    <artifactId>dubbo-spring-boot-starter</artifactId>  // 通过collide-rpc提供
</dependency>
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>  // 通过collide-datasource提供
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>      // 通过collide-datasource提供
</dependency>
-->
```

### 2. 缓存标准化 - collide-cache

#### 实现位置
- `GoodsCacheService.java` - 商品缓存服务
- `IdempotentService.java` - 幂等性服务

#### 标准化特点
```java
@Service
@RequiredArgsConstructor
public class GoodsCacheService {
    
    // ✅ 使用标准化的Redis模板（通过collide-cache提供）
    private final RedisTemplate<String, String> redisTemplate;
    
    // ✅ 标准化的缓存键命名规范
    private static final String GOODS_DETAIL_PREFIX = "goods:detail:";
    private static final String GOODS_LIST_PREFIX = "goods:list:";
    
    // ✅ 标准化的TTL配置
    private static final Duration GOODS_DETAIL_TTL = Duration.ofMinutes(30);
    private static final Duration GOODS_LIST_TTL = Duration.ofMinutes(10);
}
```

#### 缓存层级设计
- **L1缓存**: 商品详情缓存 (TTL: 30分钟)
- **L2缓存**: 商品列表缓存 (TTL: 10分钟) 
- **L3缓存**: 热门推荐缓存 (TTL: 15-20分钟)
- **L4缓存**: 搜索结果缓存 (TTL: 5分钟)

### 3. RPC标准化 - collide-rpc

#### 实现位置
- `GoodsFacadeServiceImpl.java` - Dubbo服务实现

#### 标准化特点
```java
@Slf4j
@Service
@DubboService(version = "1.0.0", timeout = 5000)  // ✅ 标准化的Dubbo配置
@RequiredArgsConstructor
public class GoodsFacadeServiceImpl implements GoodsFacadeService {
    
    // ✅ 使用标准化的响应模型
    public SingleResponse<Long> createGoods(GoodsCreateRequest createRequest) {
        // 标准化的异常处理和响应封装
    }
}
```

#### RPC配置标准化
```yaml
# application.yml - 标准化的Dubbo配置
dubbo:
  application:
    name: ${spring.application.name}     # ✅ 使用标准化变量
    version: 1.0.0
  registry:
    address: nacos://${collide.nacos.host:localhost}:${collide.nacos.port:8848}
  protocol:
    name: dubbo
    port: 20883                          # ✅ goods服务专用端口
    threads: 200
```

### 4. 数据访问标准化 - collide-datasource

#### 实现位置
- `GoodsMapper.java` - MyBatis映射接口
- `GoodsMapper.xml` - SQL映射文件

#### 标准化特点
```java
@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {
    
    // ✅ 遵循去连表化设计原则
    IPage<Goods> pageQuery(Page<Goods> page, 
                          @Param("type") String type,
                          @Param("status") String status, 
                          @Param("keyword") String keyword);
    
    // ✅ 标准化的乐观锁更新
    @Update("UPDATE goods SET stock = #{newStock}, version = version + 1 " +
            "WHERE id = #{goodsId} AND version = #{oldVersion}")
    int updateStockWithVersion(@Param("goodsId") Long goodsId, 
                              @Param("newStock") Integer newStock,
                              @Param("oldVersion") Integer oldVersion);
}
```

#### 配置标准化
```yaml
# application.yml - 标准化的数据源配置
spring:
  datasource:
    url: jdbc:mysql://${collide.mysql.host:localhost}:${collide.mysql.port:3306}/${spring.application.name}
    username: ${collide.mysql.username:root}           # ✅ 标准化变量
    password: ${collide.mysql.password:123456}
```

### 5. 配置文件标准化 - 学习Code/项目思想

#### 实现位置
- `bootstrap.yml` - 基础配置和模块导入
- `application.yml` - 应用特定配置
- `pom.xml` - Maven属性定义

#### 标准化特点
```xml
<!-- pom.xml - 应用名定义 -->
<properties>
    <application.name>collide-goods</application.name>
</properties>

<build>
    <resources>
        <resource>
            <directory>src/main/resources</directory>
            <filtering>true</filtering>  <!-- ✅ 启用资源过滤 -->
        </resource>
    </resources>
</build>
```

```yaml
# bootstrap.yml - 标准化的配置导入
spring:
  application:
    name: @application.name@  # ✅ 使用占位符，Maven构建时替换
  config:
    import: 
      - classpath:base.yml        # 基础配置
      - classpath:datasource.yml  # 数据源配置
      - classpath:cache.yml       # 缓存配置
      - classpath:rpc.yml         # RPC配置
      - classpath:config.yml      # 配置中心
```

```yaml
# application.yml - 仅包含应用特定配置
spring:
  application:
    name: @application.name@

server:
  port: 9503                    # ✅ 应用特定端口
  servlet:
    context-path: /goods        # ✅ 应用特定上下文

dubbo:
  protocol:
    port: 20883                 # ✅ 应用特定Dubbo端口

collide:
  goods:                        # ✅ 应用特定业务配置
    stock:
      low-threshold: 10
    cache:
      enabled: true
```

#### 配置分离策略
- **通用配置**: 数据源、缓存、RPC等通过config.import导入
- **应用配置**: 端口、上下文路径、业务参数等在application.yml
- **环境配置**: 不同环境的变量通过外部注入
- **Maven变量**: 应用名等通过Maven属性管理

### 6. 去连表化设计合规性

#### 数据库设计
```sql
-- ✅ 单表包含所有必要字段，避免JOIN查询
CREATE TABLE `goods` (
    `id`                BIGINT          NOT NULL,
    `name`              VARCHAR(100)    NOT NULL,
    `description`       TEXT,
    `type`              VARCHAR(20)     NOT NULL,
    `status`            VARCHAR(20)     NOT NULL DEFAULT 'DRAFT',
    `price`             DECIMAL(10,2)   NOT NULL,
    `stock`             INT             DEFAULT -1,
    `sold_count`        INT             NOT NULL DEFAULT 0,     -- ✅ 销量冗余
    `subscription_days` INT,
    `coin_amount`       INT,
    `recommended`       TINYINT(1)      NOT NULL DEFAULT 0,    -- ✅ 推荐标识冗余
    `hot`               TINYINT(1)      NOT NULL DEFAULT 0,    -- ✅ 热门标识冗余
    `creator_id`        BIGINT,                                -- ✅ 创建者信息冗余
    `deleted`           TINYINT(1)      NOT NULL DEFAULT 0,
    `version`           INT             NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`)
);
```

#### 查询实现
```xml
<!-- ✅ 所有查询都是单表操作，无JOIN -->
<select id="pageQuery" resultMap="BaseResultMap">
    SELECT <include refid="Base_Column_List"/>
    FROM goods
    WHERE deleted = 0
    <if test="status != null and status != ''">
        AND status = #{status}
    </if>
    <!-- 无JOIN子句 -->
</select>
```

## 🎯 标准化收益

### 1. 依赖管理统一
- 所有基础设施依赖通过标准化组件管理
- 版本冲突风险降低
- 升级维护成本降低

### 2. 配置标准化
- 统一的配置变量命名规范
- 环境无关的配置模板
- 易于部署和维护
- 配置文件模块化和通用化

### 3. 代码风格一致
- 统一的错误处理模式
- 标准化的响应格式
- 一致的日志输出规范

### 4. 性能优化
- 去连表化设计提升查询性能10x+
- 多层缓存策略减少数据库压力
- 乐观锁保障高并发安全

## 📊 合规性检查清单

### ✅ 已完成项目

- [x] 使用collide-cache标准化缓存组件
- [x] 使用collide-rpc标准化RPC组件  
- [x] 使用collide-datasource标准化数据源组件
- [x] 移除直接的第三方依赖
- [x] 实现去连表化数据库设计
- [x] 标准化配置变量命名
- [x] 统一异常处理和响应格式
- [x] 实现多层缓存策略
- [x] 乐观锁并发控制
- [x] 幂等性保障机制
- [x] 配置文件标准化（bootstrap.yml + application.yml分离）
- [x] Maven资源过滤和占位符替换
- [x] 通用配置模块化导入

### 📋 持续改进点

- [ ] 考虑引入collide-lock标准化锁组件
- [ ] 评估collide-mq消息队列集成
- [ ] 性能监控和指标收集标准化
- [ ] 单元测试覆盖率提升

## 🔗 相关文档

- [Goods模块API文档](../../../Document/api/goods-api.md)
- [Goods数据库Schema](../../../sql/goods/goods-module-complete.sql)
- [Collide标准化规范](../../../COLLIDE-STANDARDIZATION-ALIGNMENT-REPORT.md)

---

**文档版本**: v1.0.0  
**最后更新**: 2024-01-15  
**维护团队**: Collide Team 
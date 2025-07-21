# 多阶段构建 - 构建阶段
FROM eclipse-temurin:21-jdk-alpine AS builder

# 设置工作目录
WORKDIR /app

# 安装Maven
RUN apk add --no-cache maven

# 复制项目文件
COPY . .

# 构建整个项目（包括所有依赖模块）
RUN mvn clean package -DskipTests

# 运行阶段
FROM eclipse-temurin:21-jre-alpine

# 设置工作目录
WORKDIR /app

# 创建非root用户
RUN addgroup -S collide && adduser -S collide -G collide

# 安装必要的工具
RUN apk add --no-cache curl

# 从构建阶段复制jar文件
COPY --from=builder /app/collide-auth/target/collide-auth-*.jar app.jar

# 更改文件所有权
RUN chown -R collide:collide /app

# 切换到非root用户
USER collide

# 暴露端口
EXPOSE 8082

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8082/actuator/health || exit 1

# 设置JVM参数和启动命令
ENTRYPOINT ["java", \
    "-Xmx512m", \
    "-Xms256m", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dspring.profiles.active=docker", \
    "-jar", \
    "app.jar"] 
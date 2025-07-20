# 多阶段构建 - 构建阶段
FROM openjdk:21-jdk-slim as builder

# 设置工作目录
WORKDIR /app

# 复制项目文件
COPY . .

# 给 mvnw 执行权限
RUN chmod +x mvnw

# 构建项目 (跳过测试)
RUN ./mvnw clean package -DskipTests -pl collide-auth -am

# 运行阶段
FROM openjdk:21-jre-slim

# 设置工作目录
WORKDIR /app

# 创建非root用户
RUN groupadd -r collide && useradd -r -g collide collide

# 安装必要的工具
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

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
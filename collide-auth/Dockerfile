# 使用官方OpenJDK 21运行时镜像
FROM eclipse-temurin:21-jre-alpine

# 设置工作目录
WORKDIR /app

# 创建非root用户
RUN addgroup -S collide && adduser -S collide -G collide

# 安装必要的工具和字体（支持中文）
RUN apk add --no-cache \
    curl \
    tzdata \
    ttf-dejavu

# 设置时区为中国时区
RUN cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

# 从构建目录复制JAR文件
COPY collide-auth/target/collide-auth-*.jar app.jar

# 创建日志目录
RUN mkdir -p /app/logs

# 更改文件所有权
RUN chown -R collide:collide /app

# 切换到非root用户
USER collide

# 暴露端口（认证服务端口）
EXPOSE 9502

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:9502/actuator/health || exit 1

# 设置JVM参数和启动命令
ENTRYPOINT ["java", \
    "-server", \
    "-Xmx512m", \
    "-Xms256m", \
    "-XX:+UseG1GC", \
    "-XX:MaxGCPauseMillis=200", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Djava.awt.headless=true", \
    "-Dspring.profiles.active=docker", \
    "-Duser.timezone=Asia/Shanghai", \
    "-Dfile.encoding=UTF-8", \
    "-jar", \
    "app.jar"] 
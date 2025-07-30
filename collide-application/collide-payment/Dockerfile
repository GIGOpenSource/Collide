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

# 从构建目录复制JAR文件（聚合应用JAR）
COPY collide-application/collide-payment/target/collide-payment-1.0.0-SNAPSHOT.jar app.jar

# 创建日志目录
RUN mkdir -p /app/logs

# 更改文件所有权
RUN chown -R collide:collide /app

# 切换到非root用户
USER collide

# 暴露端口（业务应用端口）
EXPOSE 9605

# 设置JVM参数和启动命令
ENTRYPOINT ["java", \
    "-server", \
    "-Xmx256m", \
    "-Xms128m", \
    "-XX:+UseG1GC", \
    "-XX:MaxGCPauseMillis=200", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Djava.awt.headless=true", \
    "-Duser.timezone=Asia/Shanghai", \
    "-Dfile.encoding=UTF-8", \
    "-Dnacos.remote.client.grpc.timeout=30000", \
    "-Dnacos.remote.client.grpc.server.check.timeout=30000", \
    "-Dnacos.remote.client.grpc.health.timeout=30000", \
    "-jar", \
    "app.jar"] 
# 基础镜像
FROM openjdk:21-jdk-slim

# 设置工作目录
WORKDIR /app

# 复制jar文件
COPY target/collide-task-*.jar app.jar

# 设置环境变量
ENV JAVA_OPTS="-Xms512m -Xmx1024m -Djava.security.egd=file:/dev/./urandom"
ENV SERVER_PORT=8207
ENV DUBBO_PORT=20207

# 暴露端口
EXPOSE ${SERVER_PORT} ${DUBBO_PORT}

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:${SERVER_PORT}/actuator/health || exit 1

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# 元数据标签
LABEL maintainer="GIG Team"
LABEL description="Collide Task Service - 任务管理服务"
LABEL version="2.0.0"
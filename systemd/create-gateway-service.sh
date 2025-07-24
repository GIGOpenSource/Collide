#!/bin/bash

# 定义变量
APP_NAME="collide-gateway"
SERVICE_FILE="/etc/systemd/system/$APP_NAME.service"
INSTALL_DIR="/app"
LOG_DIR="$INSTALL_DIR/logs"
JAR_FILE="$INSTALL_DIR/app.jar"
BACKUP_DIR="$INSTALL_DIR/backup"
TIMESTAMP=$(date +%Y%m%d%H%M%S)

# 检查是否为root用户
if [ "$(id -u)" != "0" ]; then
   echo "此脚本必须以root用户运行" 1>&2
   exit 1
fi

# 创建必要的目录和用户
echo "设置系统用户和目录..."
if ! id -u collide >/dev/null 2>&1; then
    addgroup collide
    adduser -S -G collide collide
fi

# 创建目录结构
mkdir -p "$INSTALL_DIR" "$LOG_DIR" "$BACKUP_DIR"

# 设置目录权限（关键修改部分）
chown -R collide:collide "$INSTALL_DIR"
chmod 755 "$INSTALL_DIR"  # 允许组用户进入目录
chmod 750 "$LOG_DIR"     # 日志目录更严格的权限

# 预先创建日志文件并设置权限
touch "$LOG_DIR/collide-gateway.log"
touch "$LOG_DIR/collide-gateway-error.log"
chown collide:collide "$LOG_DIR"/*.log
chmod 640 "$LOG_DIR"/*.log  # 用户可读写，组用户只读

# 备份旧版本
if [ -f "$JAR_FILE" ]; then
    echo "备份旧版本..."
    cp "$JAR_FILE" "$BACKUP_DIR/app.jar.$TIMESTAMP"
fi

# 复制新版本JAR文件
echo "部署新版本..."
cp ./collide-gateway/target/collide-gateway-*.jar "$JAR_FILE"

# 设置权限
chown -R collide:collide "$INSTALL_DIR"
chmod 750 "$INSTALL_DIR"
chmod 500 "$JAR_FILE"
chmod 750 "$LOG_DIR"

# 创建或更新systemd服务文件
echo "配置systemd服务..."
cat > "$SERVICE_FILE" <<EOL
[Unit]
Description=Collide Gateway Service
After=syslog.target network.target

[Service]
User=collide
Group=collide
WorkingDirectory=$INSTALL_DIR

Environment="JAVA_HOME=/usr/lib/jvm/java-21-openjdk"
Environment="TZ=Asia/Shanghai"
Environment="JAVA_OPTS=-server -Xmx1024m -Xms512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom -Djava.awt.headless=true -Duser.timezone=Asia/Shanghai -Dfile.encoding=UTF-8 -Dnacos.remote.client.grpc.timeout=30000 -Dnacos.remote.client.grpc.server.check.timeout=30000 -Dnacos.remote.client.grpc.health.timeout=30000"

ExecStart=/usr/bin/java \$JAVA_OPTS -jar $JAR_FILE

StandardOutput=file:$LOG_DIR/collide-gateway.log
StandardError=file:$LOG_DIR/collide-gateway-error.log

Restart=always
RestartSec=10

LimitNOFILE=65536
LimitNPROC=4096

[Install]
WantedBy=multi-user.target
EOL

# 重新加载systemd
systemctl daemon-reload

# 启用并启动服务
echo "启动$APP_NAME服务..."
systemctl enable "$APP_NAME"
systemctl restart "$APP_NAME"

# 检查服务状态
echo "检查服务状态..."
systemctl status "$APP_NAME" --no-pager

echo "部署完成! 日志文件位于 $LOG_DIR/"
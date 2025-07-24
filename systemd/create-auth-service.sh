#!/bin/bash

# 精确路径配置
APP_NAME="collide-auth"
APP_HOME="/www/Collide/collide-auth"
TARGET_DIR="$APP_HOME/target"
LOG_DIR="/www/Collide/logs"
SERVICE_FILE="/etc/systemd/system/$APP_NAME.service"

# 自动获取最新构建的JAR文件
JAR_FILE=$(ls -1v $TARGET_DIR/collide-auth-*.jar | tail -n 1)

# 检查JAR文件是否存在
if [ ! -f "$JAR_FILE" ]; then
    echo "错误：在 $TARGET_DIR 下未找到collide-auth的JAR文件" >&2
    exit 1
fi

# 创建系统用户
if ! id -u collide >/dev/null 2>&1; then
    echo "创建系统用户..."
    groupadd -r collide
    useradd -r -g collide -d "$APP_HOME" -s /usr/sbin/nologin collide
fi

# 设置目录权限
echo "配置目录权限..."
mkdir -p "$LOG_DIR"
chown -R collide:collide /www/Collide
chmod 750 "$APP_HOME"
chmod 750 "$LOG_DIR"

# 备份旧版本（保留最近5个）
BACKUP_DIR="$APP_HOME/backups"
mkdir -p "$BACKUP_DIR"
find "$BACKUP_DIR" -name '*.bak' -mtime +30 -delete

if compgen -G "$TARGET_DIR/collide-auth-*.jar" > /dev/null; then
    echo "备份现有版本..."
    cp "$JAR_FILE" "$BACKUP_DIR/collide-auth-$(date +%Y%m%d%H%M%S).bak"
fi

# 创建服务文件
echo "创建系统服务..."
cat > "$SERVICE_FILE" <<EOF
[Unit]
Description=Collide Auth Service
After=syslog.target network.target
StartLimitIntervalSec=60

[Service]
User=collide
Group=collide
WorkingDirectory=$APP_HOME

Environment="JAVA_HOME=/usr/lib/jvm/java-21-openjdk"
Environment="TZ=Asia/Shanghai"
Environment="JAVA_OPTS=-server -Xmx512m -Xms256m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom -Djava.awt.headless=true -Duser.timezone=Asia/Shanghai -Dfile.encoding=UTF-8 -Dnacos.remote.client.grpc.timeout=30000 -Dnacos.remote.client.grpc.server.check.timeout=30000 -Dnacos.remote.client.grpc.health.timeout=30000 -DDUBBO_IP_TO_REGISTRY=18.166.150.123 -Dspring.cloud.nacos.discovery.ip=18.166.150.123"

ExecStart=/usr/bin/java \$JAVA_OPTS -jar $JAR_FILE

StandardOutput=append:$LOG_DIR/collide-auth.log
StandardError=append:$LOG_DIR/collide-auth-error.log

LimitNOFILE=100000
LimitNPROC=8192
LimitCORE=infinity

Restart=always
RestartSec=5
TimeoutStopSec=30

[Install]
WantedBy=multi-user.target
EOF

# 设置日志权限
touch "$LOG_DIR/collide-auth.log" "$LOG_DIR/collide-auth-error.log"
chown collide:collide "$LOG_DIR"/*.log
chmod 640 "$LOG_DIR"/*.log

# 配置logrotate
cat > /etc/logrotate.d/collide-auth <<EOF
$LOG_DIR/collide-auth*.log {
    daily
    missingok
    rotate 30
    compress
    delaycompress
    notifempty
    create 640 collide collide
    sharedscripts
    postrotate
        /bin/systemctl reload $APP_NAME >/dev/null 2>&1 || true
    endscript
}
EOF

# 重载并启动服务
systemctl daemon-reload
systemctl enable $APP_NAME
systemctl restart $APP_NAME

echo "部署完成!"
echo "服务状态: systemctl status $APP_NAME"
echo "日志文件: $LOG_DIR/collide-auth*.log"
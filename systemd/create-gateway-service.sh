#!/bin/bash

# 精确路径配置
APP_NAME="collide-gateway"
APP_HOME="/www/Collide/collide-gateway"
TARGET_DIR="$APP_HOME/target"
LOG_DIR="/www/Collide/logs"
SERVICE_FILE="/etc/systemd/system/$APP_NAME.service"

# 自动获取最新构建的JAR文件
JAR_FILE=$(ls -1v $TARGET_DIR/collide-gateway-*.jar 2>/dev/null | tail -n 1)

# 检查JAR文件是否存在
if [ -z "$JAR_FILE" ]; then
    echo "错误：在 $TARGET_DIR 下未找到collide-gateway的JAR文件" >&2
    echo "可用文件：" >&2
    ls -l $TARGET_DIR/ >&2
    exit 1
fi

# 创建系统用户（如果不存在）
if ! id -u collide >/dev/null 2>&1; then
    echo "创建系统用户..."
    groupadd -r collide
    useradd -r -g collide -d "/www/Collide" -s /usr/sbin/nologin collide
fi

# 设置目录权限
echo "配置目录权限..."
mkdir -p "$LOG_DIR" "$APP_HOME/backups"
chown -R collide:collide /www/Collide
find /www/Collide -type d -exec chmod 750 {} \;

# 备份旧版本（保留最近5个）
BACKUP_DIR="$APP_HOME/backups"
echo "备份现有版本到 $BACKUP_DIR..."
find "$BACKUP_DIR" -name '*.bak' -mtime +30 -delete
cp "$JAR_FILE" "$BACKUP_DIR/collide-gateway-$(date +%Y%m%d%H%M%S).bak"

# 创建服务文件
echo "创建系统服务..."
cat > "$SERVICE_FILE" <<EOF
[Unit]
Description=Collide Gateway Service
After=syslog.target network.target collide-auth.service
StartLimitIntervalSec=60

[Service]
User=collide
Group=collide
WorkingDirectory=$APP_HOME

Environment="JAVA_HOME=/usr/lib/jvm/java-21-openjdk"
Environment="TZ=Asia/Shanghai"
Environment="JAVA_OPTS=-server -Xmx1024m -Xms512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom -Djava.awt.headless=true -Duser.timezone=Asia/Shanghai -Dfile.encoding=UTF-8 -Dnacos.remote.client.grpc.timeout=30000 -Dnacos.remote.client.grpc.server.check.timeout=30000 -Dnacos.remote.client.grpc.health.timeout=30000"

ExecStart=/bin/sh -c '/usr/bin/java \$JAVA_OPTS -jar $TARGET_DIR/collide-gateway-*.jar'

StandardOutput=append:$LOG_DIR/collide-gateway.log
StandardError=append:$LOG_DIR/collide-gateway-error.log

LimitNOFILE=200000
LimitNPROC=16384
LimitCORE=infinity

Restart=always
RestartSec=5
TimeoutStopSec=45

[Install]
WantedBy=multi-user.target
EOF

# 设置日志权限
for logfile in "$LOG_DIR/collide-gateway.log" "$LOG_DIR/collide-gateway-error.log"; do
    touch "$logfile"
    chown collide:collide "$logfile"
    chmod 640 "$logfile"
done

# 配置logrotate
echo "配置日志轮转..."
cat > /etc/logrotate.d/collide-gateway <<EOF
$LOG_DIR/collide-gateway*.log {
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

# 特殊处理：等待依赖服务就绪
if systemctl is-active --quiet collide-auth; then
    systemctl restart $APP_NAME
else
    echo "警告：依赖服务collide-auth未运行，请先启动auth服务" >&2
    systemctl start $APP_NAME || true
fi

echo "部署完成!"
echo "服务状态: systemctl status $APP_NAME"
echo "实时日志: journalctl -u $APP_NAME -f"
echo "访问测试: curl -I http://localhost:9501"
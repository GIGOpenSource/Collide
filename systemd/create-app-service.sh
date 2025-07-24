#!/bin/bash

# 精确路径配置
APP_NAME="collide-app"
APP_ROOT="/www/Collide/collide-application"
TARGET_DIR="${APP_ROOT}/collide-app/target"
JAR_FILE="${TARGET_DIR}/collide-app.jar"
LOG_DIR="${APP_ROOT}/logs"
SERVICE_FILE="/etc/systemd/system/${APP_NAME}.service"

# 检查root权限
if [ "$(id -u)" != "0" ]; then
   echo "错误：必须使用root用户执行" >&2
   exit 1
fi

# 验证JAR文件存在
if [ ! -f "$JAR_FILE" ]; then
    echo "错误：未找到JAR文件 ${JAR_FILE}" >&2
    exit 1
fi

# 创建系统用户
if ! id -u collide >/dev/null 2>&1; then
    echo "创建系统用户..."
    groupadd -r collide
    useradd -r -g collide -s /usr/sbin/nologin -d "$APP_ROOT" collide
fi

# 初始化目录结构
echo "初始化目录..."
mkdir -p "$LOG_DIR" "${APP_ROOT}/backups"
chown -R collide:collide "$APP_ROOT"
chmod 750 "$APP_ROOT"

# 备份管理（保留最近5个版本）
echo "备份当前版本..."
BACKUP_FILE="${APP_ROOT}/backups/collide-app_$(date +%Y%m%d%H%M%S).jar"
[ -f "$JAR_FILE" ] && cp "$JAR_FILE" "$BACKUP_FILE"
ls -1t "${APP_ROOT}/backups/"*.jar | tail -n +6 | xargs rm -f

# 设置文件权限
chown collide:collide "$JAR_FILE"
chmod 500 "$JAR_FILE"  # 只读执行权限

# 配置日志文件
touch "${LOG_DIR}/app.log" "${LOG_DIR}/app-error.log"
chown collide:collide "${LOG_DIR}"/*.log
chmod 640 "${LOG_DIR}"/*.log

# 生成服务文件
echo "生成系统服务..."
cat > "$SERVICE_FILE" <<EOF
[Unit]
Description=Collide Application Service
After=syslog.target network.target
StartLimitIntervalSec=60

[Service]
User=collide
Group=collide
WorkingDirectory=$TARGET_DIR

Environment="JAR_PATH=$JAR_FILE"
Environment="LOG_DIR=$LOG_DIR"
Environment="JAVA_HOME=/usr/lib/jvm/java-21-openjdk"
Environment="TZ=Asia/Shanghai"
Environment="JAVA_OPTS=-server -Xmx1024m -Xms512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom -Djava.awt.headless=true -Duser.timezone=Asia/Shanghai -Dfile.encoding=UTF-8 -Dnacos.remote.client.grpc.timeout=30000 -Dnacos.remote.client.grpc.server.check.timeout=30000 -Dnacos.remote.client.grpc.health.timeout=30000"

ExecStart=/usr/bin/java \$JAVA_OPTS -jar \$JAR_PATH

StandardOutput=append:\${LOG_DIR}/app.log
StandardError=append:\${LOG_DIR}/app-error.log

LimitNOFILE=100000
LimitNPROC=8192
LimitCORE=infinity

Restart=on-failure
RestartSec=10
TimeoutStopSec=45

[Install]
WantedBy=multi-user.target
EOF

# 配置日志轮转
cat > /etc/logrotate.d/collide-app <<EOF
${LOG_DIR}/app*.log {
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

# 重载服务配置
systemctl daemon-reload
systemctl enable "$APP_NAME"
systemctl restart "$APP_NAME"

echo "部署完成！"
echo "服务状态：systemctl status $APP_NAME"
echo "应用日志：tail -f ${LOG_DIR}/app.log"
echo "错误日志：tail -f ${LOG_DIR}/app-error.log"
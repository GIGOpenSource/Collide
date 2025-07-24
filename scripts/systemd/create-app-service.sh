#!/bin/bash

# 创建Application服务的systemd配置
echo "正在创建 collide-application systemd 服务..."

# 创建日志目录
sudo mkdir -p /root/logs/app

# 创建systemd服务文件
sudo tee /etc/systemd/system/collide-application.service <<EOF
[Unit]
Description=Collide Application Service
Documentation=https://github.com/your-org/collide
After=network.target nacos.service mysql.service redis.service
Wants=nacos.service

[Service]
Type=simple
User=root
Group=root
WorkingDirectory=/www/Collide/collide-application/collide-app/target
ExecStart=/usr/bin/java -jar -server -DDUBBO_IP_TO_REGISTRY=18.166.150.123 -Dspring.cloud.nacos.discovery.ip=18.166.150.123 -Dspring.profiles.active=prod -Xms2g -Xmx2g -XX:MetaspaceSize=512m -XX:MaxMetaspaceSize=512m -XX:MaxDirectMemorySize=1g -XX:+UseG1GC -Xlog:gc:/root/logs/app/gc.log -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/root/logs/app/java.hprof -Djava.awt.headless=true -Dfile.encoding=UTF-8 -Duser.timezone=Asia/Shanghai collide-app.jar

Environment=JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
Environment=LANG=zh_CN.UTF-8
Environment=TZ=Asia/Shanghai

Restart=always
RestartSec=10
TimeoutStopSec=30
KillMode=mixed

StandardOutput=file:/root/logs/app/app.log
StandardError=file:/root/logs/app/error.log

LimitNOFILE=65536
LimitNPROC=4096

[Install]
WantedBy=multi-user.target
EOF

# 重新加载systemd并启用服务
sudo systemctl daemon-reload
sudo systemctl enable --now collide-application

echo "✅ collide-application 服务已创建并启动！"
echo ""
echo "常用命令："
echo "  查看状态: sudo systemctl status collide-application"
echo "  查看日志: sudo tail -f /root/logs/app/app.log"
echo "  重启服务: sudo systemctl restart collide-application"
echo "  停止服务: sudo systemctl stop collide-application" 
#!/bin/bash

# 创建Auth服务的systemd配置
echo "正在创建 collide-auth systemd 服务..."

# 创建日志目录
sudo mkdir -p /root/logs/auth

# 创建systemd服务文件
sudo tee /etc/systemd/system/collide-auth.service <<EOF
[Unit]
Description=Collide Auth Service
Documentation=https://github.com/your-org/collide
After=network.target nacos.service
Wants=nacos.service

[Service]
Type=simple
User=root
Group=root
WorkingDirectory=/www/Collide/collide-auth/target
ExecStart=/usr/bin/java -jar -server -DDUBBO_IP_TO_REGISTRY=127.0.0.1 -Dspring.cloud.nacos.discovery.ip=127.0.0.1 -Dspring.profiles.active=prod -Xms1g -Xmx1g -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=256m -XX:MaxDirectMemorySize=512m -XX:+UseG1GC -Xlog:gc:/root/logs/auth/gc.log -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/root/logs/auth/java.hprof -Djava.awt.headless=true -Dfile.encoding=UTF-8 -Duser.timezone=Asia/Shanghai collide-auth-1.0.0-SNAPSHOT.jar

Environment=JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
Environment=LANG=zh_CN.UTF-8
Environment=TZ=Asia/Shanghai

Restart=always
RestartSec=10
TimeoutStopSec=30
KillMode=mixed

StandardOutput=file:/root/logs/collide-auth/app.log
StandardError=file:/root/logs/collide-auth/error.log

LimitNOFILE=65536
LimitNPROC=4096

[Install]
WantedBy=multi-user.target
EOF

# 重新加载systemd并启用服务
sudo systemctl daemon-reload
sudo systemctl enable --now collide-auth

echo "✅ collide-auth 服务已创建并启动！"
echo ""
echo "常用命令："
echo "  查看状态: sudo systemctl status collide-auth"
echo "  查看日志: sudo tail -f /root/logs/auth/app.log"
echo "  重启服务: sudo systemctl restart collide-auth"
echo "  停止服务: sudo systemctl stop collide-auth" 
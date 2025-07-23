#!/bin/bash

# 创建Gateway服务的systemd配置
echo "正在创建 collide-gateway systemd 服务..."

# 创建日志目录
sudo mkdir -p /root/logs/gateway

# 创建systemd服务文件
sudo tee /etc/systemd/system/collide-gateway.service <<EOF
[Unit]
Description=Collide Gateway Service
Documentation=https://github.com/your-org/collide
After=network.target nacos.service redis.service
Wants=nacos.service

[Service]
Type=simple
User=root
Group=root
WorkingDirectory=/www/Collide/collide-gateway/target
ExecStart=/usr/bin/java \\
    -jar \\
    -server \\
    -DDUBBO_IP_TO_REGISTRY=127.0.0.1 \\
    -Dspring.cloud.nacos.discovery.ip=127.0.0.1 \\
    -Dspring.profiles.active=prod \\
    -Xms1g \\
    -Xmx2g \\
    -XX:MetaspaceSize=256m \\
    -XX:MaxMetaspaceSize=512m \\
    -XX:MaxDirectMemorySize=512m \\
    -XX:+UseG1GC \\
    -Xlog:gc:/root/logs/gateway/gc.log \\
    -XX:+HeapDumpOnOutOfMemoryError \\
    -XX:HeapDumpPath=/root/logs/gateway/java.hprof \\
    -Djava.awt.headless=true \\
    -Dfile.encoding=UTF-8 \\
    -Duser.timezone=Asia/Shanghai \\
    collide-gateway.jar

Environment=JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
Environment=LANG=zh_CN.UTF-8
Environment=TZ=Asia/Shanghai

Restart=always
RestartSec=10
TimeoutStopSec=30
KillMode=mixed

StandardOutput=file:/root/logs/gateway/app.log
StandardError=file:/root/logs/gateway/error.log

LimitNOFILE=65536
LimitNPROC=4096

[Install]
WantedBy=multi-user.target
EOF

# 重新加载systemd并启用服务
sudo systemctl daemon-reload
sudo systemctl enable --now collide-gateway

echo "✅ collide-gateway 服务已创建并启动！"
echo ""
echo "常用命令："
echo "  查看状态: sudo systemctl status collide-gateway"
echo "  查看日志: sudo tail -f /root/logs/gateway/app.log"
echo "  重启服务: sudo systemctl restart collide-gateway"
echo "  停止服务: sudo systemctl stop collide-gateway" 
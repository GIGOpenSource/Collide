#!/bin/bash

# 一键部署所有Collide服务的systemd配置
echo "=== Collide 微服务 Systemd 一键部署脚本 ==="

# 检查是否为root用户
if [[ $EUID -ne 0 ]]; then
   echo "错误: 请使用root权限运行此脚本"
   echo "使用方法: sudo bash deploy-all-services.sh"
   exit 1
fi

# 停止可能存在的旧服务
echo "停止现有服务..."
systemctl stop collide-gateway 2>/dev/null || true
systemctl stop collide-auth 2>/dev/null || true  
systemctl stop collide-application 2>/dev/null || true

# 创建日志目录
echo "创建日志目录..."
mkdir -p /root/logs/{gateway,auth,app}

echo "创建并启动服务..."

# 1. 创建Gateway服务
echo "1/3 创建Gateway服务..."
bash create-gateway-service.sh

sleep 3

# 2. 创建Auth服务  
echo "2/3 创建Auth服务..."
bash create-auth-service.sh

sleep 3

# 3. 创建Application服务
echo "3/3 创建Application服务..."
bash create-app-service.sh

sleep 5

# 检查所有服务状态
echo ""
echo "=== 检查服务状态 ==="
for service in collide-gateway collide-auth collide-application; do
    if systemctl is-active --quiet $service; then
        echo "✅ $service: 运行中"
    else
        echo "❌ $service: 未运行"
    fi
done

echo ""
echo "=== 部署完成 ==="
echo "查看所有服务状态: sudo systemctl status collide-*"
echo "查看服务日志目录: ls -la /root/logs/"

# 显示服务端口
echo ""
echo "=== 服务端口 ==="
echo "Gateway:     http://127.0.0.1:9501"
echo "Auth:        http://127.0.0.1:9502"
echo "Application: http://127.0.0.1:9503"
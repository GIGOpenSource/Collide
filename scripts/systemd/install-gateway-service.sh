#!/bin/bash

# Gateway Systemd 服务安装脚本
# 使用方法: sudo bash install-gateway-service.sh

set -e

echo "=== Collide Gateway Systemd 服务安装脚本 ==="

# 检查是否为root用户
if [[ $EUID -ne 0 ]]; then
   echo "错误: 请使用root权限运行此脚本"
   echo "使用方法: sudo bash install-gateway-service.sh"
   exit 1
fi

# 定义变量
SERVICE_NAME="collide-gateway"
SERVICE_FILE="/etc/systemd/system/${SERVICE_NAME}.service"
JAR_PATH="/www/Collide/collide-gateway/target/collide-gateway.jar"
LOG_DIR="/root/logs/gateway"

echo "正在检查系统环境..."

# 创建日志目录
echo "创建日志目录: ${LOG_DIR}"
mkdir -p ${LOG_DIR}
chmod 755 ${LOG_DIR}

# 检查Java是否安装
if ! command -v java &> /dev/null; then
    echo "错误: 未找到Java运行环境"
    echo "请先安装OpenJDK 21或更高版本"
    exit 1
fi

# 检查jar包是否存在
if [[ ! -f "${JAR_PATH}" ]]; then
    echo "错误: 未找到Gateway jar包"
    echo "路径: ${JAR_PATH}"
    echo "请先编译并部署Gateway应用"
    exit 1
fi

# 停止现有服务（如果存在）
if systemctl is-active --quiet ${SERVICE_NAME}; then
    echo "停止现有的${SERVICE_NAME}服务..."
    systemctl stop ${SERVICE_NAME}
fi

# 复制service文件
echo "安装systemd服务文件..."
cp collide-gateway.service ${SERVICE_FILE}

# 设置文件权限
chmod 644 ${SERVICE_FILE}

# 重新加载systemd配置
echo "重新加载systemd配置..."
systemctl daemon-reload

# 启用服务
echo "启用${SERVICE_NAME}服务..."
systemctl enable ${SERVICE_NAME}

# 启动服务
echo "启动${SERVICE_NAME}服务..."
systemctl start ${SERVICE_NAME}

# 等待服务启动
echo "等待服务启动..."
sleep 5

# 检查服务状态
echo "检查服务状态..."
if systemctl is-active --quiet ${SERVICE_NAME}; then
    echo "✅ ${SERVICE_NAME}服务启动成功！"
    echo ""
    echo "常用命令:"
    echo "  查看服务状态: systemctl status ${SERVICE_NAME}"
    echo "  查看服务日志: journalctl -u ${SERVICE_NAME} -f"
    echo "  重启服务:     systemctl restart ${SERVICE_NAME}"
    echo "  停止服务:     systemctl stop ${SERVICE_NAME}"
    echo "  禁用服务:     systemctl disable ${SERVICE_NAME}"
    echo ""
    echo "日志文件位置:"
    echo "  应用日志: ${LOG_DIR}/application.log"
    echo "  GC日志:   ${LOG_DIR}/gc.log"
    echo "  系统日志: journalctl -u ${SERVICE_NAME}"
else
    echo "❌ ${SERVICE_NAME}服务启动失败！"
    echo "请检查日志: journalctl -u ${SERVICE_NAME} -n 50"
    exit 1
fi

echo ""
echo "=== Gateway服务安装完成 ===" 
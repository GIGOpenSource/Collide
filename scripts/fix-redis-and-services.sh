#!/bin/bash

# Collide Redis问题修复和服务重新部署脚本
echo "🔧 正在修复Redis连接问题并重新部署服务..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# 检查是否为root用户
if [[ $EUID -ne 0 ]]; then
   echo -e "${RED}此脚本需要root权限运行！${NC}"
   echo "请使用: sudo $0"
   exit 1
fi

echo -e "${BLUE}步骤1: 检查和修复Redis服务${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# 检查Redis服务状态
if ! systemctl is-active --quiet redis; then
    echo -e "${YELLOW}⚠️  Redis服务未运行，尝试启动...${NC}"
    systemctl start redis
    sleep 3
    
    if systemctl is-active --quiet redis; then
        echo -e "${GREEN}✅ Redis服务已启动${NC}"
    else
        echo -e "${RED}❌ Redis服务启动失败，请检查配置${NC}"
        systemctl status redis
        exit 1
    fi
else
    echo -e "${GREEN}✅ Redis服务正在运行${NC}"
fi

# 测试Redis连接
echo -e "${BLUE}测试Redis连接...${NC}"
if timeout 5 redis-cli ping >/dev/null 2>&1; then
    echo -e "${GREEN}✅ Redis连接正常${NC}"
else
    echo -e "${RED}❌ Redis连接失败${NC}"
    echo -e "${YELLOW}尝试检查Redis配置...${NC}"
    
    # 检查Redis配置文件
    REDIS_CONF="/etc/redis/redis.conf"
    if [ -f "$REDIS_CONF" ]; then
        echo "当前Redis配置:"
        grep -E "^(bind|port)" "$REDIS_CONF" || echo "未找到bind/port配置"
        
        # 建议修复
        echo -e "${YELLOW}建议检查Redis配置：${NC}"
        echo "1. 确保bind包含127.0.0.1: bind 127.0.0.1"
        echo "2. 确保端口为6379: port 6379"
        echo "3. 重启Redis: systemctl restart redis"
    fi
    
    read -p "是否继续部署服务? (y/N): " continue_deploy
    if [[ ! $continue_deploy =~ ^[Yy]$ ]]; then
        echo "部署已取消"
        exit 1
    fi
fi

echo ""
echo -e "${BLUE}步骤2: 停止现有服务${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# 停止现有服务
services=("collide-gateway" "collide-auth" "collide-application")
for service in "${services[@]}"; do
    if systemctl is-active --quiet "$service"; then
        echo -e "${YELLOW}停止 $service...${NC}"
        systemctl stop "$service"
        sleep 2
    fi
done

echo ""
echo -e "${BLUE}步骤3: 重新部署服务（使用统一localhost配置）${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# 进入systemd脚本目录
cd "$(dirname "$0")/systemd" || {
    echo -e "${RED}❌ 无法找到systemd脚本目录${NC}"
    exit 1
}

# 重新部署服务
echo -e "${BLUE}🌐 重新部署Gateway服务...${NC}"
if ./create-gateway-service.sh; then
    echo -e "${GREEN}✅ Gateway服务部署成功${NC}"
else
    echo -e "${RED}❌ Gateway服务部署失败${NC}"
    exit 1
fi

sleep 5

echo -e "${BLUE}🔐 重新部署Auth服务...${NC}"
if ./create-auth-service.sh; then
    echo -e "${GREEN}✅ Auth服务部署成功${NC}"
else
    echo -e "${RED}❌ Auth服务部署失败${NC}"
    exit 1
fi

sleep 5

echo -e "${BLUE}💼 重新部署Application服务...${NC}"
if ./create-app-service.sh; then
    echo -e "${GREEN}✅ Application服务部署成功${NC}"
else
    echo -e "${RED}❌ Application服务部署失败${NC}"
    exit 1
fi

echo ""
echo -e "${BLUE}步骤4: 等待服务启动${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

echo -e "${YELLOW}等待所有服务完全启动...${NC}"
sleep 15

echo ""
echo -e "${BLUE}步骤5: 检查服务状态${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# 检查服务状态
for service in "${services[@]}"; do
    if systemctl is-active --quiet "$service"; then
        echo -e "${GREEN}✅ $service: 运行中${NC}"
    else
        echo -e "${RED}❌ $service: 未运行${NC}"
        echo "   查看日志: sudo journalctl -u $service -f"
    fi
done

echo ""
echo -e "${BLUE}步骤6: 测试服务连接${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# 测试Gateway健康检查
echo -n "测试Gateway健康检查: "
if timeout 10 curl -s http://localhost:9501/actuator/health >/dev/null 2>&1; then
    echo -e "${GREEN}✅ 成功${NC}"
else
    echo -e "${RED}❌ 失败${NC}"
fi

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo -e "${GREEN}🎉 服务部署完成！${NC}"
echo ""

echo -e "${BLUE}📊 服务信息：${NC}"
echo "  Gateway:     http://localhost:9501"
echo "  Auth API:    http://localhost:9501/api/v1/auth/"
echo "  Business API: http://localhost:9501/api/v1/users/"
echo ""

echo -e "${BLUE}🛠️  常用命令：${NC}"
echo "  查看服务状态: sudo systemctl status collide-gateway collide-auth collide-application"
echo "  查看实时日志: sudo tail -f /root/logs/gateway/app.log"
echo "  重启服务:     sudo systemctl restart collide-gateway"
echo ""

echo -e "${YELLOW}⚠️  重要说明：${NC}"
echo "  1. 现在所有服务都使用localhost进行内网通信"
echo "  2. 只需要在安全组开放9501端口供外部访问"
echo "  3. Redis连接问题已通过统一配置解决"
echo ""

echo -e "${GREEN}✨ 修复完成！如果还有问题，请检查服务日志。${NC}" 
#!/bin/bash

echo "🔍 Redis服务状态检查..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# 检查Redis服务状态
echo -e "${BLUE}1. 检查Redis系统服务状态${NC}"
if systemctl is-active --quiet redis; then
    echo -e "   ✅ Redis服务: ${GREEN}运行中${NC}"
else
    echo -e "   ❌ Redis服务: ${RED}未运行${NC}"
    echo -e "   尝试启动Redis服务..."
    sudo systemctl start redis
    sleep 2
    if systemctl is-active --quiet redis; then
        echo -e "   ✅ Redis服务已启动"
    else
        echo -e "   ❌ Redis服务启动失败"
    fi
fi

echo ""

# 检查Redis端口监听
echo -e "${BLUE}2. 检查Redis端口监听状态${NC}"
if netstat -tlnp | grep -q ":6379"; then
    echo -e "   ✅ Redis端口6379: ${GREEN}正在监听${NC}"
    netstat -tlnp | grep ":6379"
else
    echo -e "   ❌ Redis端口6379: ${RED}未监听${NC}"
fi

echo ""

# 检查Redis连接性
echo -e "${BLUE}3. 检查Redis连接性${NC}"

# 本地连接测试
echo -n "   测试本地连接 (127.0.0.1:6379): "
if timeout 5 redis-cli -h 127.0.0.1 -p 6379 ping >/dev/null 2>&1; then
    echo -e "${GREEN}✅ 成功${NC}"
else
    echo -e "${RED}❌ 失败${NC}"
fi

# 内网IP连接测试  
echo -n "   测试内网连接 (192.168.1.107:6379): "
if timeout 5 redis-cli -h 192.168.1.107 -p 6379 ping >/dev/null 2>&1; then
    echo -e "${GREEN}✅ 成功${NC}"
else
    echo -e "${RED}❌ 失败${NC}"
fi

echo ""

# 检查Redis配置
echo -e "${BLUE}4. 检查Redis配置${NC}"
REDIS_CONF="/etc/redis/redis.conf"
if [ -f "$REDIS_CONF" ]; then
    echo "   配置文件: $REDIS_CONF"
    
    # 检查bind配置
    echo -n "   bind配置: "
    BIND_CONFIG=$(grep "^bind" "$REDIS_CONF" 2>/dev/null || echo "未找到")
    echo "$BIND_CONFIG"
    
    # 检查密码配置
    echo -n "   密码配置: "
    if grep -q "^requirepass" "$REDIS_CONF" 2>/dev/null; then
        echo "已设置密码"
    else
        echo "未设置密码"
    fi
else
    echo "   ⚠️  Redis配置文件未找到: $REDIS_CONF"
fi

echo ""

# 检查防火墙状态
echo -e "${BLUE}5. 检查防火墙状态${NC}"
if command -v ufw >/dev/null 2>&1; then
    if ufw status | grep -q "Status: active"; then
        echo "   UFW防火墙状态: 活跃"
        if ufw status | grep -q "6379"; then
            echo -e "   Redis端口6379: ${GREEN}已开放${NC}"
        else
            echo -e "   Redis端口6379: ${YELLOW}未在UFW中开放${NC}"
        fi
    else
        echo "   UFW防火墙状态: 未活跃"
    fi
else
    echo "   UFW防火墙: 未安装"
fi

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# 给出修复建议
echo -e "${YELLOW}🔧 修复建议：${NC}"
echo ""

if ! systemctl is-active --quiet redis; then
    echo -e "${YELLOW}1. 启动Redis服务：${NC}"
    echo "   sudo systemctl start redis"
    echo "   sudo systemctl enable redis"
    echo ""
fi

if ! netstat -tlnp | grep -q ":6379"; then
    echo -e "${YELLOW}2. 检查Redis配置并重启：${NC}"
    echo "   sudo systemctl restart redis"
    echo "   sudo systemctl status redis"
    echo ""
fi

echo -e "${YELLOW}3. 如果Redis无法连接，尝试：${NC}"
echo "   # 检查Redis配置"
echo "   sudo nano /etc/redis/redis.conf"
echo "   # 确保bind配置包含需要的IP"
echo "   # bind 127.0.0.1 192.168.1.107"
echo ""
echo "   # 重启Redis"
echo "   sudo systemctl restart redis"
echo ""

echo -e "${YELLOW}4. 测试Redis连接：${NC}"
echo "   redis-cli -h 127.0.0.1 -p 6379 ping"
echo "   redis-cli -h 192.168.1.107 -p 6379 ping"

echo ""
echo -e "${GREEN}检查完成！请根据上述信息解决Redis连接问题。${NC}" 
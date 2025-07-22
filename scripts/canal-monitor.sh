#!/bin/bash

# =============================================================================
# Canal数据同步监控脚本
# =============================================================================
# 用途：监控Canal同步状态，验证ES中的日期格式是否正确
# 使用：./scripts/canal-monitor.sh
# =============================================================================

# 配置参数
ES_HOST=${1:-"192.168.1.107:9200"}
ES_USERNAME=${2:-"elastic"}
ES_PASSWORD=${3:-"123456"}
INDEX_NAME="collide_collection"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

echo -e "${GREEN}🔍 Canal数据同步监控开始...${NC}"

# 基础认证
AUTH=$(echo -n "$ES_USERNAME:$ES_PASSWORD" | base64)

# 1. 检查ES连接
echo -e "${CYAN}1. 检查ES连接状态...${NC}"
ES_STATUS=$(curl -s -w "%{http_code}" -o /dev/null -H "Authorization: Basic $AUTH" "http://$ES_HOST")
if [ "$ES_STATUS" = "200" ]; then
    echo -e "${GREEN}✅ ES连接正常${NC}"
else
    echo -e "${RED}❌ ES连接失败，状态码: $ES_STATUS${NC}"
    exit 1
fi

# 2. 检查索引是否存在
echo -e "${CYAN}2. 检查索引状态...${NC}"
INDEX_EXISTS=$(curl -s -H "Authorization: Basic $AUTH" "http://$ES_HOST/$INDEX_NAME" | grep -o '"found":true')
if [[ -n "$INDEX_EXISTS" ]]; then
    echo -e "${GREEN}✅ 索引 $INDEX_NAME 存在${NC}"
else
    echo -e "${RED}❌ 索引 $INDEX_NAME 不存在${NC}"
    exit 1
fi

# 3. 检查数据量
echo -e "${CYAN}3. 检查数据量...${NC}"
DOC_COUNT=$(curl -s -H "Authorization: Basic $AUTH" "http://$ES_HOST/$INDEX_NAME/_count" | grep -o '"count":[0-9]*' | cut -d':' -f2)
echo -e "${GREEN}📊 当前文档数量: $DOC_COUNT${NC}"

# 4. 检查日期格式
echo -e "${CYAN}4. 检查日期格式...${NC}"
SAMPLE_DATA=$(curl -s -H "Authorization: Basic $AUTH" -H "Content-Type: application/json" \
    "http://$ES_HOST/$INDEX_NAME/_search?size=5" \
    -d '{"query": {"match_all": {}}, "sort": [{"gmt_create": {"order": "desc"}}]}')

# 解析日期字段
SALE_TIMES=$(echo "$SAMPLE_DATA" | grep -o '"sale_time":"[^"]*"' | head -5)
CREATE_TIMES=$(echo "$SAMPLE_DATA" | grep -o '"create_time":"[^"]*"' | head -5)

echo -e "${YELLOW}📅 最近5条记录的日期格式:${NC}"
echo "$SALE_TIMES" | while read -r line; do
    if [[ -n "$line" ]]; then
        # 提取日期值
        DATE_VALUE=$(echo "$line" | sed 's/"sale_time":"\([^"]*\)"/\1/')
        
        # 检查格式
        if [[ "$DATE_VALUE" =~ ^[0-9]{4}-[0-9]{2}-[0-9]{2}\ [0-9]{2}:[0-9]{2}:[0-9]{2}$ ]]; then
            echo -e "${GREEN}✅ 正确格式: $DATE_VALUE${NC}"
        elif [[ "$DATE_VALUE" =~ ^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\+[0-9]{2}:[0-9]{2}$ ]]; then
            echo -e "${RED}❌ 旧格式(需修复): $DATE_VALUE${NC}"
        else
            echo -e "${YELLOW}⚠️  未知格式: $DATE_VALUE${NC}"
        fi
    fi
done

# 5. 测试Spring Data Elasticsearch兼容性
echo -e "${CYAN}5. 测试应用兼容性...${NC}"
APP_TEST_URL="http://localhost:8085/collection/test"
APP_STATUS=$(curl -s -w "%{http_code}" -o /dev/null "$APP_TEST_URL")
if [ "$APP_STATUS" = "200" ]; then
    echo -e "${GREEN}✅ 应用端点可达${NC}"
    
    # 测试实际查询
    COLLECTION_URL="http://localhost:8085/collection/collectionList?pageSize=1"
    COLLECTION_RESULT=$(curl -s "$COLLECTION_URL")
    
    if echo "$COLLECTION_RESULT" | grep -q '"success":true'; then
        echo -e "${GREEN}✅ 藏品查询正常，无日期转换错误${NC}"
    else
        echo -e "${RED}❌ 藏品查询失败，可能存在日期转换问题${NC}"
        echo -e "${YELLOW}响应: $(echo "$COLLECTION_RESULT" | head -100)...${NC}"
    fi
else
    echo -e "${YELLOW}⚠️  应用端点不可达，状态码: $APP_STATUS${NC}"
fi

# 6. Canal同步延迟检查
echo -e "${CYAN}6. 检查同步延迟...${NC}"
# 查询最新的ES文档时间
LATEST_ES_TIME=$(curl -s -H "Authorization: Basic $AUTH" -H "Content-Type: application/json" \
    "http://$ES_HOST/$INDEX_NAME/_search?size=1" \
    -d '{"query": {"match_all": {}}, "sort": [{"gmt_modified": {"order": "desc"}}]}' | \
    grep -o '"gmt_modified":"[^"]*"' | head -1 | sed 's/"gmt_modified":"\([^"]*\)"/\1/')

if [[ -n "$LATEST_ES_TIME" ]]; then
    echo -e "${GREEN}📅 ES中最新数据时间: $LATEST_ES_TIME${NC}"
    
    # 转换为时间戳进行比较（简化处理）
    CURRENT_TIME=$(date '+%Y-%m-%d %H:%M:%S')
    echo -e "${GREEN}📅 当前时间: $CURRENT_TIME${NC}"
    
    # 这里可以添加更详细的延迟计算逻辑
else
    echo -e "${YELLOW}⚠️  无法获取ES中的最新时间${NC}"
fi

# 7. 生成报告
echo -e "${CYAN}7. 生成监控报告...${NC}"
REPORT_FILE="/tmp/canal_monitor_$(date +%Y%m%d_%H%M%S).txt"
cat > "$REPORT_FILE" << EOF
Canal数据同步监控报告
===================
时间: $(date)
ES地址: $ES_HOST
索引: $INDEX_NAME
文档数量: $DOC_COUNT

日期格式检查:
$(echo "$SALE_TIMES" | while read -r line; do
    if [[ -n "$line" ]]; then
        DATE_VALUE=$(echo "$line" | sed 's/"sale_time":"\([^"]*\)"/\1/')
        if [[ "$DATE_VALUE" =~ ^[0-9]{4}-[0-9]{2}-[0-9]{2}\ [0-9]{2}:[0-9]{2}:[0-9]{2}$ ]]; then
            echo "✅ $DATE_VALUE"
        else
            echo "❌ $DATE_VALUE"
        fi
    fi
done)

应用状态: HTTP $APP_STATUS
EOF

echo -e "${GREEN}📋 监控报告已生成: $REPORT_FILE${NC}"

echo -e "${GREEN}🎉 监控完成！${NC}" 
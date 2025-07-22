#!/bin/bash

# =============================================================================
# ES时区测试脚本
# =============================================================================

ES_HOST="192.168.1.107:9200"
ES_USERNAME="elastic"
ES_PASSWORD="123456"
INDEX_NAME="collide_collection"

echo "🔍 测试ES时间格式和时区处理..."
echo ""

# 基础认证
AUTH=$(echo -n "$ES_USERNAME:$ES_PASSWORD" | base64)

echo "1. 📅 检查当前ES中的时间格式："
echo "----------------------------------------"

# 获取样本数据
SAMPLE_DATA=$(curl -s -H "Authorization: Basic $AUTH" \
    "http://$ES_HOST/$INDEX_NAME/_search?size=3&pretty" \
    -H "Content-Type: application/json" \
    -d '{
        "query": {"match_all": {}},
        "sort": [{"gmt_create": {"order": "desc"}}]
    }')

# 提取时间字段
echo "sale_time 字段格式："
echo "$SAMPLE_DATA" | grep -o '"sale_time":"[^"]*"' | head -3
echo ""

echo "create_time 字段格式："
echo "$SAMPLE_DATA" | grep -o '"create_time":"[^"]*"' | head -3
echo ""

echo "gmt_create 字段格式："
echo "$SAMPLE_DATA" | grep -o '"gmt_create":"[^"]*"' | head -3
echo ""

echo "2. 🧪 测试不同时间格式的插入："
echo "----------------------------------------"

# 测试文档ID
TEST_DOC_ID="timezone_test_001"

# 删除可能存在的测试文档
curl -s -H "Authorization: Basic $AUTH" \
    -X DELETE "http://$ES_HOST/$INDEX_NAME/_doc/$TEST_DOC_ID" > /dev/null

echo "测试1: 简单格式 (yyyy-MM-dd HH:mm:ss)"
TEST_RESULT_1=$(curl -s -H "Authorization: Basic $AUTH" \
    -H "Content-Type: application/json" \
    -X POST "http://$ES_HOST/$INDEX_NAME/_doc/$TEST_DOC_ID" \
    -d '{
        "name": "时区测试1",
        "state": "SUCCEED",
        "deleted": "0",
        "saleable_inventory": 100,
        "sale_time": "2024-01-21 08:00:00",
        "create_time": "2024-01-21 08:00:00",
        "gmt_create": "2024-01-21 08:00:00"
    }' | grep -o '"result":"[^"]*"')

echo "结果: $TEST_RESULT_1"

# 立即查询验证
sleep 1
QUERY_RESULT_1=$(curl -s -H "Authorization: Basic $AUTH" \
    "http://$ES_HOST/$INDEX_NAME/_doc/$TEST_DOC_ID" | \
    grep -o '"sale_time":"[^"]*"')
echo "ES中存储格式: $QUERY_RESULT_1"
echo ""

# 删除测试文档
curl -s -H "Authorization: Basic $AUTH" \
    -X DELETE "http://$ES_HOST/$INDEX_NAME/_doc/$TEST_DOC_ID" > /dev/null

echo "测试2: ISO格式带时区 (yyyy-MM-ddTHH:mm:ss+08:00)"
TEST_RESULT_2=$(curl -s -H "Authorization: Basic $AUTH" \
    -H "Content-Type: application/json" \
    -X POST "http://$ES_HOST/$INDEX_NAME/_doc/${TEST_DOC_ID}_2" \
    -d '{
        "name": "时区测试2", 
        "state": "SUCCEED",
        "deleted": "0",
        "saleable_inventory": 100,
        "sale_time": "2024-01-21T08:00:00+08:00",
        "create_time": "2024-01-21T08:00:00+08:00",
        "gmt_create": "2024-01-21T08:00:00+08:00"
    }' | grep -o '"result":"[^"]*"')

echo "结果: $TEST_RESULT_2"

# 立即查询验证
sleep 1
QUERY_RESULT_2=$(curl -s -H "Authorization: Basic $AUTH" \
    "http://$ES_HOST/$INDEX_NAME/_doc/${TEST_DOC_ID}_2" | \
    grep -o '"sale_time":"[^"]*"')
echo "ES中存储格式: $QUERY_RESULT_2"
echo ""

# 删除测试文档
curl -s -H "Authorization: Basic $AUTH" \
    -X DELETE "http://$ES_HOST/$INDEX_NAME/_doc/${TEST_DOC_ID}_2" > /dev/null

echo "测试3: ISO格式不带时区 (yyyy-MM-ddTHH:mm:ss)"
TEST_RESULT_3=$(curl -s -H "Authorization: Basic $AUTH" \
    -H "Content-Type: application/json" \
    -X POST "http://$ES_HOST/$INDEX_NAME/_doc/${TEST_DOC_ID}_3" \
    -d '{
        "name": "时区测试3",
        "state": "SUCCEED", 
        "deleted": "0",
        "saleable_inventory": 100,
        "sale_time": "2024-01-21T08:00:00",
        "create_time": "2024-01-21T08:00:00", 
        "gmt_create": "2024-01-21T08:00:00"
    }' | grep -o '"result":"[^"]*"')

echo "结果: $TEST_RESULT_3"

# 立即查询验证
sleep 1 
QUERY_RESULT_3=$(curl -s -H "Authorization: Basic $AUTH" \
    "http://$ES_HOST/$INDEX_NAME/_doc/${TEST_DOC_ID}_3" | \
    grep -o '"sale_time":"[^"]*"')
echo "ES中存储格式: $QUERY_RESULT_3"
echo ""

# 清理测试文档
curl -s -H "Authorization: Basic $AUTH" \
    -X DELETE "http://$ES_HOST/$INDEX_NAME/_doc/${TEST_DOC_ID}_3" > /dev/null

echo "3. 🔄 测试Spring Data Elasticsearch兼容性："
echo "----------------------------------------"

# 测试应用查询
APP_URL="http://localhost:8085/collection/collectionList?pageSize=1"
echo "测试URL: $APP_URL"

APP_RESULT=$(curl -s "$APP_URL")
if echo "$APP_RESULT" | grep -q '"success":true'; then
    echo "✅ 应用查询成功"
    RETURNED_DATA=$(echo "$APP_RESULT" | head -200)
    echo "返回数据片段: $RETURNED_DATA"
else
    echo "❌ 应用查询失败"
    ERROR_INFO=$(echo "$APP_RESULT" | head -200)
    echo "错误信息: $ERROR_INFO"
fi
echo ""

echo "4. 📊 结论分析："
echo "----------------------------------------"
echo "• 简单格式存储: $QUERY_RESULT_1" 
echo "• 带时区格式存储: $QUERY_RESULT_2"
echo "• ISO格式存储: $QUERY_RESULT_3"
echo ""
echo "💡 建议："
echo "如果ES总是转换为ISO格式存储，那么Spring Data Elasticsearch"
echo "需要能够处理这种格式的反序列化。"
echo ""
echo "如果Canal修改后的简单格式在ES中仍然被转换为ISO格式，"
echo "那么问题可能在于ES的映射配置或Spring的反序列化逻辑。" 
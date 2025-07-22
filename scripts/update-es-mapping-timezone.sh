#!/bin/bash

# =============================================================================
# ES索引映射更新脚本 - 支持时区
# =============================================================================

ES_HOST="192.168.1.107:9200"
ES_USERNAME="elastic"
ES_PASSWORD="123456"
INDEX_NAME="collide_collection"

echo "🔄 更新ES索引映射以支持时区处理..."
echo ""

# 基础认证
AUTH=$(echo -n "$ES_USERNAME:$ES_PASSWORD" | base64)

echo "1. 📋 当前索引映射："
echo "----------------------------------------"
curl -s -H "Authorization: Basic $AUTH" \
    "http://$ES_HOST/$INDEX_NAME/_mapping" | jq '.collide_collection.mappings.properties | {sale_time, create_time}' 2>/dev/null || echo "当前映射信息获取失败"
echo ""

echo "2. 🔧 更新索引映射："
echo "----------------------------------------"

# 更新索引映射以支持多种时间格式
UPDATE_MAPPING='{
  "properties": {
    "sale_time": {
      "type": "date",
      "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd'\''T'\''HH:mm:ss||yyyy-MM-dd'\''T'\''HH:mm:ssXXX||strict_date_optional_time||epoch_millis"
    },
    "create_time": {
      "type": "date", 
      "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd'\''T'\''HH:mm:ss||yyyy-MM-dd'\''T'\''HH:mm:ssXXX||strict_date_optional_time||epoch_millis"
    },
    "sync_chain_time": {
      "type": "date",
      "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd'\''T'\''HH:mm:ss||yyyy-MM-dd'\''T'\''HH:mm:ssXXX||strict_date_optional_time||epoch_millis"
    },
    "book_start_time": {
      "type": "date",
      "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd'\''T'\''HH:mm:ss||yyyy-MM-dd'\''T'\''HH:mm:ssXXX||strict_date_optional_time||epoch_millis"
    },
    "book_end_time": {
      "type": "date",
      "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd'\''T'\''HH:mm:ss||yyyy-MM-dd'\''T'\''HH:mm:ssXXX||strict_date_optional_time||epoch_millis"
    }
  }
}'

# 尝试更新映射
MAPPING_RESULT=$(curl -s -w "%{http_code}" -o /dev/null \
    -H "Authorization: Basic $AUTH" \
    -H "Content-Type: application/json" \
    -X PUT "http://$ES_HOST/$INDEX_NAME/_mapping" \
    -d "$UPDATE_MAPPING")

if [ "$MAPPING_RESULT" = "200" ]; then
    echo "✅ 索引映射更新成功"
else
    echo "❌ 索引映射更新失败，状态码: $MAPPING_RESULT"
    echo "注意: 已存在的字段映射可能无法修改，可能需要重建索引"
fi
echo ""

echo "3. 🧪 测试不同时区格式："
echo "----------------------------------------"

# 测试数据数组
declare -a TEST_FORMATS=(
    "2024-01-21 08:00:00"
    "2024-01-21T08:00:00"
    "2024-01-21T08:00:00+08:00"
    "2024-01-21T00:00:00Z"
)

declare -a FORMAT_NAMES=(
    "简单格式"
    "ISO格式(无时区)"
    "ISO格式(+08:00)"
    "ISO格式(UTC)"
)

for i in "${!TEST_FORMATS[@]}"; do
    FORMAT="${TEST_FORMATS[$i]}"
    NAME="${FORMAT_NAMES[$i]}"
    DOC_ID="timezone_test_$i"
    
    echo "测试 $NAME: $FORMAT"
    
    # 删除可能存在的测试文档
    curl -s -H "Authorization: Basic $AUTH" \
        -X DELETE "http://$ES_HOST/$INDEX_NAME/_doc/$DOC_ID" > /dev/null
    
    # 插入测试文档
    TEST_DOC="{
        \"name\": \"时区测试$i\",
        \"state\": \"SUCCEED\",
        \"deleted\": \"0\",
        \"saleable_inventory\": 100,
        \"sale_time\": \"$FORMAT\",
        \"create_time\": \"$FORMAT\"
    }"
    
    INSERT_RESULT=$(curl -s -w "%{http_code}" -o /dev/null \
        -H "Authorization: Basic $AUTH" \
        -H "Content-Type: application/json" \
        -X POST "http://$ES_HOST/$INDEX_NAME/_doc/$DOC_ID" \
        -d "$TEST_DOC")
    
    if [ "$INSERT_RESULT" = "201" ]; then
        echo "  ✅ 插入成功"
        
        # 查询插入的文档
        sleep 1
        STORED_FORMAT=$(curl -s -H "Authorization: Basic $AUTH" \
            "http://$ES_HOST/$INDEX_NAME/_doc/$DOC_ID" | \
            jq -r '._source.sale_time' 2>/dev/null)
        
        if [ "$STORED_FORMAT" != "null" ] && [ "$STORED_FORMAT" != "" ]; then
            echo "  📅 ES存储格式: $STORED_FORMAT"
        else
            echo "  ⚠️  无法获取存储格式"
        fi
    else
        echo "  ❌ 插入失败，状态码: $INSERT_RESULT"
    fi
    
    # 清理测试文档
    curl -s -H "Authorization: Basic $AUTH" \
        -X DELETE "http://$ES_HOST/$INDEX_NAME/_doc/$DOC_ID" > /dev/null
    echo ""
done

echo "4. 🔍 验证现有数据的时间格式："
echo "----------------------------------------"

# 查询现有数据的时间格式
EXISTING_DATA=$(curl -s -H "Authorization: Basic $AUTH" \
    -H "Content-Type: application/json" \
    "http://$ES_HOST/$INDEX_NAME/_search?size=3" \
    -d '{
        "query": {"match_all": {}},
        "sort": [{"gmt_create": {"order": "desc"}}]
    }')

if echo "$EXISTING_DATA" | grep -q '"hits"'; then
    echo "现有数据的时间格式示例："
    echo "$EXISTING_DATA" | jq -r '.hits.hits[]._source | {sale_time, create_time}' 2>/dev/null | head -10
else
    echo "⚠️  无法获取现有数据或索引为空"
fi

echo ""
echo "5. 💡 建议："
echo "----------------------------------------"
echo "• 如果现有数据格式不一致，建议重新同步Canal数据"
echo "• 确保Canal输出带时区的ISO 8601格式"
echo "• 验证Spring Data Elasticsearch能正确反序列化"
echo "• 考虑在应用启动时检查时区配置一致性" 
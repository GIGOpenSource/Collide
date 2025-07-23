#!/bin/bash

# Collide 项目集成测试脚本
# 验证所有微服务模块的RPC调用和API接口

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 配置信息
GATEWAY_URL="http://localhost:8080"
AUTH_URL="http://localhost:8081"
USER_URL="http://localhost:8082"
FOLLOW_URL="http://localhost:8083"
CONTENT_URL="http://localhost:8084"
BUSINESS_URL="http://localhost:8085"

# 测试结果统计
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0

# 日志函数
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
    ((PASSED_TESTS++))
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
    ((FAILED_TESTS++))
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# HTTP请求函数
http_get() {
    local url=$1
    local expected_code=${2:-200}
    local description=$3
    
    ((TOTAL_TESTS++))
    log_info "测试: $description"
    
    response=$(curl -s -w "%{http_code}" -o /tmp/response_body "$url" || echo "000")
    
    if [ "$response" = "$expected_code" ]; then
        log_success "✓ $description - HTTP $response"
        return 0
    else
        log_error "✗ $description - Expected HTTP $expected_code, got $response"
        if [ -f /tmp/response_body ]; then
            echo "Response body: $(cat /tmp/response_body)"
        fi
        return 1
    fi
}

http_post() {
    local url=$1
    local data=$2
    local expected_code=${3:-200}
    local description=$4
    local token=${5:-""}
    
    ((TOTAL_TESTS++))
    log_info "测试: $description"
    
    local headers=""
    if [ -n "$token" ]; then
        headers="-H 'Authorization: Bearer $token'"
    fi
    
    response=$(curl -s -w "%{http_code}" -o /tmp/response_body \
        -X POST \
        -H "Content-Type: application/json" \
        $headers \
        -d "$data" \
        "$url" || echo "000")
    
    if [ "$response" = "$expected_code" ]; then
        log_success "✓ $description - HTTP $response"
        return 0
    else
        log_error "✗ $description - Expected HTTP $expected_code, got $response"
        if [ -f /tmp/response_body ]; then
            echo "Response body: $(cat /tmp/response_body)"
        fi
        return 1
    fi
}

# 等待服务启动
wait_for_service() {
    local url=$1
    local service_name=$2
    local max_attempts=30
    local attempt=1
    
    log_info "等待 $service_name 服务启动..."
    
    while [ $attempt -le $max_attempts ]; do
        if curl -s -f "$url/actuator/health" > /dev/null 2>&1; then
            log_success "$service_name 服务已启动"
            return 0
        fi
        
        log_info "尝试 $attempt/$max_attempts - $service_name 服务未就绪，等待3秒..."
        sleep 3
        ((attempt++))
    done
    
    log_error "$service_name 服务启动超时"
    return 1
}

# 健康检查测试
test_health_checks() {
    log_info "========== 健康检查测试 =========="
    
    # 检查各个服务的健康状态
    http_get "$AUTH_URL/actuator/health" 200 "Auth服务健康检查"
    http_get "$USER_URL/actuator/health" 200 "User服务健康检查"
    http_get "$FOLLOW_URL/actuator/health" 200 "Follow服务健康检查"
    http_get "$CONTENT_URL/actuator/health" 200 "Content服务健康检查"
    http_get "$BUSINESS_URL/actuator/health" 200 "Business聚合服务健康检查"
    
    echo ""
}

# 认证流程测试
test_auth_flow() {
    log_info "========== 认证流程测试 =========="
    
    # 用户注册
    local register_data='{
        "username": "testuser001",
        "password": "test123456",
        "nickname": "测试用户",
        "email": "test@example.com"
    }'
    
    http_post "$GATEWAY_URL/api/v1/auth/register" "$register_data" 200 "用户注册"
    
    # 用户登录
    local login_data='{
        "username": "testuser001",
        "password": "test123456"
    }'
    
    if http_post "$GATEWAY_URL/api/v1/auth/login" "$login_data" 200 "用户登录"; then
        # 提取token（简化处理）
        TOKEN=$(cat /tmp/response_body | grep -o '"token":"[^"]*"' | cut -d'"' -f4 || echo "")
        if [ -n "$TOKEN" ]; then
            log_success "成功获取认证Token: ${TOKEN:0:20}..."
        else
            log_warning "未能提取认证Token"
        fi
    fi
    
    # Token验证
    if [ -n "$TOKEN" ]; then
        http_get "$GATEWAY_URL/api/v1/token/verify?token=$TOKEN" 200 "Token验证"
    fi
    
    echo ""
}

# 用户管理测试
test_user_management() {
    log_info "========== 用户管理测试 =========="
    
    # 获取当前用户信息
    if [ -n "$TOKEN" ]; then
        http_get "$GATEWAY_URL/api/v1/user/profile" 200 "获取用户信息" "$TOKEN"
    else
        log_warning "跳过用户信息测试 - 缺少认证Token"
    fi
    
    # 用户列表查询（公开接口）
    http_get "$GATEWAY_URL/api/v1/user/list?pageNo=1&pageSize=10" 200 "用户列表查询"
    
    echo ""
}

# 关注功能测试
test_follow_features() {
    log_info "========== 关注功能测试 =========="
    
    if [ -z "$TOKEN" ]; then
        log_warning "跳过关注功能测试 - 缺少认证Token"
        return
    fi
    
    # 关注用户
    local follow_data='{
        "followedUserId": 2,
        "followType": "NORMAL"
    }'
    
    http_post "$GATEWAY_URL/api/v1/follow" "$follow_data" 200 "关注用户" "$TOKEN"
    
    # 获取关注列表
    http_get "$GATEWAY_URL/api/v1/follow/following?pageNo=1&pageSize=10" 200 "获取关注列表" "$TOKEN"
    
    # 获取粉丝列表
    http_get "$GATEWAY_URL/api/v1/follow/followers?pageNo=1&pageSize=10" 200 "获取粉丝列表" "$TOKEN"
    
    # 获取关注统计
    http_get "$GATEWAY_URL/api/v1/follow/statistics" 200 "获取关注统计" "$TOKEN"
    
    echo ""
}

# 内容管理测试
test_content_management() {
    log_info "========== 内容管理测试 =========="
    
    # 获取内容列表（公开接口）
    http_get "$GATEWAY_URL/api/v1/content?type=LATEST&pageNo=1&pageSize=10" 200 "获取最新内容列表"
    
    # 获取推荐内容
    http_get "$GATEWAY_URL/api/v1/content?type=RECOMMENDED&contentType=NOVEL&pageNo=1&pageSize=5" 200 "获取推荐内容"
    
    # 获取热门内容
    http_get "$GATEWAY_URL/api/v1/content?type=HOT&pageNo=1&pageSize=5" 200 "获取热门内容"
    
    if [ -z "$TOKEN" ]; then
        log_warning "跳过内容创建测试 - 缺少认证Token"
        return
    fi
    
    # 创建内容
    local content_data='{
        "title": "测试小说",
        "description": "这是一个测试用的小说内容",
        "contentType": "NOVEL",
        "contentData": {
            "synopsis": "测试小说简介",
            "chapters": [
                {
                    "title": "第一章",
                    "content": "这是第一章的内容...",
                    "wordCount": 100
                }
            ]
        },
        "categoryId": 1,
        "tags": ["测试", "小说"]
    }'
    
    if http_post "$GATEWAY_URL/api/v1/content" "$content_data" 200 "创建内容" "$TOKEN"; then
        CONTENT_ID=$(cat /tmp/response_body | grep -o '"data":[0-9]*' | cut -d':' -f2 || echo "")
        if [ -n "$CONTENT_ID" ]; then
            log_success "成功创建内容，ID: $CONTENT_ID"
            
            # 获取内容详情
            http_get "$GATEWAY_URL/api/v1/content/$CONTENT_ID" 200 "获取内容详情"
            
            # 点赞内容
            http_post "$GATEWAY_URL/api/v1/content/$CONTENT_ID/like" "{}" 200 "点赞内容" "$TOKEN"
            
            # 收藏内容
            http_post "$GATEWAY_URL/api/v1/content/$CONTENT_ID/favorite" "{}" 200 "收藏内容" "$TOKEN"
            
            # 获取内容统计
            http_get "$GATEWAY_URL/api/v1/content/$CONTENT_ID/statistics" 200 "获取内容统计"
        fi
    fi
    
    # 获取我的内容
    http_get "$GATEWAY_URL/api/v1/content/my?pageNo=1&pageSize=10" 200 "获取我的内容" "$TOKEN"
    
    echo ""
}

# RPC服务连通性测试
test_rpc_connectivity() {
    log_info "========== RPC服务连通性测试 =========="
    
    # 检查Dubbo服务注册情况（通过actuator端点）
    http_get "$BUSINESS_URL/actuator/dubbo" 200 "Dubbo服务状态检查"
    
    # 检查各服务的Dubbo端点
    http_get "$USER_URL/actuator/dubbo" 200 "User服务Dubbo状态"
    http_get "$FOLLOW_URL/actuator/dubbo" 200 "Follow服务Dubbo状态"
    http_get "$CONTENT_URL/actuator/dubbo" 200 "Content服务Dubbo状态"
    
    echo ""
}

# 性能基准测试
test_performance_benchmark() {
    log_info "========== 性能基准测试 =========="
    
    # 并发请求测试（简化版）
    log_info "执行并发请求测试..."
    
    # 创建临时测试脚本
    cat > /tmp/concurrent_test.sh << EOF
#!/bin/bash
for i in {1..10}; do
    curl -s "$GATEWAY_URL/api/v1/content?pageNo=1&pageSize=5" > /dev/null &
done
wait
EOF
    
    chmod +x /tmp/concurrent_test.sh
    
    # 测量执行时间
    start_time=$(date +%s.%N)
    /tmp/concurrent_test.sh
    end_time=$(date +%s.%N)
    
    duration=$(echo "$end_time - $start_time" | bc -l 2>/dev/null || echo "N/A")
    
    if [ "$duration" != "N/A" ]; then
        log_success "10个并发请求完成，耗时: ${duration}s"
    else
        log_info "并发测试完成（无法计算精确时间）"
    fi
    
    # 清理临时文件
    rm -f /tmp/concurrent_test.sh
    
    echo ""
}

# 数据一致性测试
test_data_consistency() {
    log_info "========== 数据一致性测试 =========="
    
    if [ -z "$TOKEN" ] || [ -z "$CONTENT_ID" ]; then
        log_warning "跳过数据一致性测试 - 缺少必要的测试数据"
        return
    fi
    
    # 检查点赞后的数据一致性
    log_info "检查点赞操作的数据一致性..."
    
    # 再次获取内容统计，验证点赞数是否正确更新
    if http_get "$GATEWAY_URL/api/v1/content/$CONTENT_ID/statistics" 200 "验证点赞数据更新"; then
        local like_count=$(cat /tmp/response_body | grep -o '"totalLikes":[0-9]*' | cut -d':' -f2 || echo "0")
        if [ "$like_count" -gt 0 ]; then
            log_success "点赞数据一致性验证通过，当前点赞数: $like_count"
        else
            log_warning "点赞数据可能存在延迟或问题"
        fi
    fi
    
    echo ""
}

# 错误处理测试
test_error_handling() {
    log_info "========== 错误处理测试 =========="
    
    # 测试不存在的接口
    http_get "$GATEWAY_URL/api/v1/nonexistent" 404 "不存在的接口"
    
    # 测试无效的内容ID
    http_get "$GATEWAY_URL/api/v1/content/999999" 404 "不存在的内容ID"
    
    # 测试无效的认证Token
    http_get "$GATEWAY_URL/api/v1/user/profile" 401 "无效的认证Token" "invalid_token"
    
    # 测试无效的请求参数
    local invalid_data='{"invalid": "data"}'
    http_post "$GATEWAY_URL/api/v1/auth/login" "$invalid_data" 400 "无效的登录参数"
    
    echo ""
}

# 生成测试报告
generate_report() {
    log_info "========== 测试报告 =========="
    
    echo -e "${BLUE}总测试数:${NC} $TOTAL_TESTS"
    echo -e "${GREEN}通过测试:${NC} $PASSED_TESTS"
    echo -e "${RED}失败测试:${NC} $FAILED_TESTS"
    
    local success_rate=0
    if [ $TOTAL_TESTS -gt 0 ]; then
        success_rate=$((PASSED_TESTS * 100 / TOTAL_TESTS))
    fi
    
    echo -e "${BLUE}成功率:${NC} $success_rate%"
    
    if [ $FAILED_TESTS -eq 0 ]; then
        echo -e "${GREEN}🎉 所有测试通过！系统运行正常。${NC}"
        return 0
    else
        echo -e "${RED}⚠️  发现 $FAILED_TESTS 个问题，请检查日志。${NC}"
        return 1
    fi
}

# 主函数
main() {
    echo -e "${BLUE}========================================${NC}"
    echo -e "${BLUE}    Collide 项目集成测试开始${NC}"
    echo -e "${BLUE}========================================${NC}"
    echo ""
    
    # 等待所有服务启动
    log_info "检查服务状态..."
    
    # 注意：这里假设服务已经启动，实际使用时可以启用等待逻辑
    # wait_for_service "$AUTH_URL" "Auth"
    # wait_for_service "$USER_URL" "User" 
    # wait_for_service "$FOLLOW_URL" "Follow"
    # wait_for_service "$CONTENT_URL" "Content"
    # wait_for_service "$BUSINESS_URL" "Business"
    
    # 执行测试套件
    test_health_checks
    test_auth_flow
    test_user_management
    test_follow_features
    test_content_management
    test_rpc_connectivity
    test_performance_benchmark
    test_data_consistency
    test_error_handling
    
    # 生成报告
    generate_report
    
    echo ""
    echo -e "${BLUE}========================================${NC}"
    echo -e "${BLUE}    Collide 项目集成测试完成${NC}"
    echo -e "${BLUE}========================================${NC}"
}

# 清理函数
cleanup() {
    rm -f /tmp/response_body /tmp/concurrent_test.sh
}

# 设置清理trap
trap cleanup EXIT

# 检查依赖
if ! command -v curl &> /dev/null; then
    log_error "curl 命令未找到，请安装 curl"
    exit 1
fi

# 执行主函数
main "$@" 
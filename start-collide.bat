@echo off
title Collide Social Platform

echo.
echo ========================================
echo    Collide 社交平台启动脚本
echo ========================================
echo.

echo [1/4] 检查Java环境...
java -version
if %errorlevel% neq 0 (
    echo 错误: 未找到Java环境，请先安装JDK 21
    pause
    exit /b 1
)

echo.
echo [2/4] 检查Maven环境...
"C:\Program Files\apache-maven-3.9.11\bin\mvn.cmd" -version
if %errorlevel% neq 0 (
    echo 错误: 未找到Maven环境，请检查Maven安装
    pause
    exit /b 1
)

echo.
echo [3/4] 编译项目...
"C:\Program Files\apache-maven-3.9.11\bin\mvn.cmd" clean compile -pl collide-application/collide-app -am
if %errorlevel% neq 0 (
    echo 错误: 项目编译失败
    pause
    exit /b 1
)

echo.
echo [4/4] 启动Collide聚合应用...
echo.
echo ================================================
echo   🚀 启动中...
echo   📱 服务模块: Users | Follow | Content | Comment | Like | Favorite
echo   🌐 访问地址: http://localhost:8080/api
echo   📋 API文档: http://localhost:8080/api/swagger-ui.html
echo   🔍 健康检查: http://localhost:8080/api/actuator/health
echo   🧪 集成测试: http://localhost:8080/api/test/all-services
echo ================================================
echo.

cd collide-application\collide-app
"C:\Program Files\apache-maven-3.9.11\bin\mvn.cmd" spring-boot:run

echo.
echo 应用已停止
pause 
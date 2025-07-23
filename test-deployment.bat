@echo off
chcp 65001 >nul
echo.
echo ========================================
echo  🧪 Collide 应用服务部署测试
echo ========================================
echo.

:: 设置颜色
set "GREEN=[92m"
set "RED=[91m"
set "YELLOW=[93m"
set "BLUE=[94m"
set "NC=[0m"

:: 测试服务连通性
echo %BLUE%正在测试应用服务连通性...%NC%
echo.

:: 等待服务启动
echo %YELLOW%等待服务完全启动 (30秒)...%NC%
timeout /t 30 /nobreak >nul

:: 测试网关服务 (9500)
echo %BLUE%🌐 测试网关服务 (端口 9500)...%NC%
curl -s -f http://localhost:9500/actuator/health >nul
if %errorlevel%==0 (
    echo %GREEN%✅ 网关服务健康检查通过%NC%
) else (
    echo %RED%❌ 网关服务健康检查失败%NC%
    goto :error
)

:: 测试认证服务 (9501)
echo %BLUE%🔐 测试认证服务 (端口 9501)...%NC%
curl -s -f http://localhost:9501/actuator/health >nul
if %errorlevel%==0 (
    echo %GREEN%✅ 认证服务健康检查通过%NC%
) else (
    echo %RED%❌ 认证服务健康检查失败%NC%
    goto :error
)

:: 测试业务服务 (9502)
echo %BLUE%💼 测试业务服务 (端口 9502)...%NC%
curl -s -f http://localhost:9502/actuator/health >nul
if %errorlevel%==0 (
    echo %GREEN%✅ 业务服务健康检查通过%NC%
) else (
    echo %RED%❌ 业务服务健康检查失败%NC%
    goto :error
)

echo.
echo %GREEN%========================================%NC%
echo %GREEN%🎉 所有应用服务测试通过！%NC%
echo %GREEN%========================================%NC%
echo.
echo %BLUE%📋 服务访问地址：%NC%
echo   🌐 网关服务: http://localhost:9500
echo   🔐 认证服务: http://localhost:9501  
echo   💼 业务服务: http://localhost:9502
echo.
echo %BLUE%📊 健康检查地址：%NC%
echo   🌐 网关健康检查: http://localhost:9500/actuator/health
echo   🔐 认证健康检查: http://localhost:9501/actuator/health
echo   💼 业务健康检查: http://localhost:9502/actuator/health
echo.
echo %YELLOW%💡 提示：如需查看详细信息，可访问上述健康检查地址%NC%
echo.
pause
exit /b 0

:error
echo.
echo %RED%========================================%NC%
echo %RED%❌ 应用服务测试失败！%NC%
echo %RED%========================================%NC%
echo.
echo %YELLOW%🔍 故障排除建议：%NC%
echo   1. 检查 Docker 容器是否正常运行: docker-compose ps
echo   2. 查看服务日志: docker-compose logs [service-name]
echo   3. 确认外部中间件服务已启动并可访问
echo   4. 检查 .env 文件中的配置是否正确
echo   5. 确认防火墙未阻止端口 9500-9502
echo.
echo %YELLOW%📝 常用调试命令：%NC%
echo   docker-compose logs collide-gateway
echo   docker-compose logs collide-auth
echo   docker-compose logs collide-application
echo.
pause
exit /b 1 
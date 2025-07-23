@echo off
setlocal enabledelayedexpansion

:: Collide 项目健康检查脚本 (Windows版)
:: 用途：检查 Gateway、Auth、Application 三个服务的健康状态

:: 配置信息
set SERVICES_INFO=gateway:9501:Gateway auth:9502:Auth application:9503:Application

:: 打印带颜色的信息
:print_info
echo [INFO] %~1
goto :eof

:print_success
echo [SUCCESS] %~1
goto :eof

:print_warning
echo [WARNING] %~1
goto :eof

:print_error
echo [ERROR] %~1
goto :eof

:print_header
echo === %~1 ===
goto :eof

:: 检查进程状态
:check_process
set SERVICE_NAME=%~1
set PID_FILE=%SERVICE_NAME%.pid

if exist "%PID_FILE%" (
    set /p PID=<"%PID_FILE%"
    
    tasklist /fi "PID eq !PID!" 2>nul | find "!PID!" >nul
    if not errorlevel 1 (
        echo   进程状态: √ 进程运行中 ^(PID: !PID!^)
        set PROCESS_OK=1
        goto :eof
    ) else (
        echo   进程状态: × PID文件存在但进程不存在 ^(PID: !PID!^)
        set PROCESS_OK=0
        goto :eof
    )
) else (
    echo   进程状态: ? PID文件不存在
    set PROCESS_OK=0
    goto :eof
)

:: 检查端口状态
:check_port
set PORT=%~1

netstat -an | find ":%PORT%" | find "LISTENING" >nul 2>&1
if not errorlevel 1 (
    for /f "tokens=5" %%a in ('netstat -ano ^| find ":%PORT%" ^| find "LISTENING"') do (
        set PID=%%a
        echo   端口状态: √ 端口 %PORT% 监听中 ^(PID: !PID!^)
        set PORT_OK=1
        goto :eof
    )
) else (
    echo   端口状态: × 端口 %PORT% 未监听
    set PORT_OK=0
    goto :eof
)

:: 检查HTTP健康端点
:check_http_health
set PORT=%~1
set SERVICE_NAME=%~2
set URL=http://localhost:%PORT%/actuator/health

:: 检查curl是否可用
curl --version >nul 2>&1
if errorlevel 1 (
    echo   健康检查: ? curl 不可用，跳过HTTP健康检查
    set HTTP_OK=0
    goto :eof
)

:: 发送HTTP请求
set TEMP_FILE=%TEMP%\health_check_%RANDOM%.tmp
curl -s -w "HTTPCODE:%%{http_code}" "%URL%" > "%TEMP_FILE%" 2>&1

if exist "%TEMP_FILE%" (
    for /f "tokens=*" %%i in ('type "%TEMP_FILE%"') do (
        set RESPONSE=%%i
    )
    del "%TEMP_FILE%" >nul 2>&1
    
    echo !RESPONSE! | find "HTTPCODE:200" >nul
    if not errorlevel 1 (
        echo !RESPONSE! | find """status"":""UP""" >nul
        if not errorlevel 1 (
            echo   健康检查: √ HTTP健康检查通过
            set HTTP_OK=1
        ) else (
            echo   健康检查: × 服务状态异常
            set HTTP_OK=0
        )
    ) else (
        echo   健康检查: × HTTP请求失败
        set HTTP_OK=0
    )
) else (
    echo   健康检查: × HTTP请求失败
    set HTTP_OK=0
)
goto :eof

:: 检查JVM内存使用
:check_jvm_memory
set PORT=%~1
set URL=http://localhost:%PORT%/actuator/metrics/jvm.memory.used

curl --version >nul 2>&1
if errorlevel 1 (
    echo   内存使用: ? curl 不可用，无法获取内存信息
    goto :eof
)

set TEMP_FILE=%TEMP%\memory_check_%RANDOM%.tmp
curl -s "%URL%" > "%TEMP_FILE%" 2>&1

if exist "%TEMP_FILE%" (
    for /f "tokens=*" %%i in ('type "%TEMP_FILE%" ^| find "value"') do (
        set MEMORY_LINE=%%i
    )
    del "%TEMP_FILE%" >nul 2>&1
    
    if defined MEMORY_LINE (
        :: 简单解析内存值（这里只是示例，实际解析可能需要更复杂的逻辑）
        echo   内存使用: ℹ JVM内存使用: 获取成功
    ) else (
        echo   内存使用: ? 无法获取内存信息
    )
) else (
    echo   内存使用: ? 无法获取内存信息
)
goto :eof

:: 检查单个服务
:check_single_service
set SERVICE_KEY=%~1
set PORT=%~2
set DISPLAY_NAME=%~3

echo.
echo ▶ %DISPLAY_NAME% 服务检查：
echo   端口: %PORT%

set PROCESS_OK=0
set PORT_OK=0
set HTTP_OK=0

:: 检查进程
call :check_process %SERVICE_KEY%

:: 检查端口
call :check_port %PORT%

:: 检查HTTP健康端点
if !PORT_OK!==1 (
    call :check_http_health %PORT% "%DISPLAY_NAME%"
    call :check_jvm_memory %PORT%
)

:: 返回综合状态
if !PROCESS_OK!==1 if !PORT_OK!==1 if !HTTP_OK!==1 (
    set SERVICE_HEALTHY=1
) else (
    set SERVICE_HEALTHY=0
)
goto :eof

:: 显示系统资源信息
:show_system_info
call :print_header "系统资源信息"

:: CPU使用率（简化版本）
echo CPU使用率: 信息不可用^(需要更复杂的实现^)

:: 内存使用
for /f "skip=1 tokens=4" %%i in ('wmic OS get TotalVisibleMemorySize /value') do set TotalMem=%%i
for /f "skip=1 tokens=4" %%i in ('wmic OS get FreePhysicalMemory /value') do set FreeMem=%%i
if defined TotalMem if defined FreeMem (
    set /a UsedMem=%TotalMem%-%FreeMem%
    set /a MemPercent=!UsedMem!*100/%TotalMem%
    echo 内存使用: !MemPercent!%%
)

:: 磁盘使用
for /f "tokens=3" %%i in ('dir /-c ^| find "bytes free"') do set FreeDisk=%%i
echo 磁盘使用: 可用空间 %FreeDisk% bytes

:: Java进程统计
set JAVA_COUNT=0
for /f %%i in ('tasklist /fi "imagename eq java.exe" ^| find /c "java.exe"') do set JAVA_COUNT=%%i
echo Java进程数: %JAVA_COUNT%
goto :eof

:: 显示服务状态总结
:show_summary
call :print_header "服务状态总结"

set RUNNING_COUNT=0
set TOTAL_COUNT=3

echo ┌─────────────┬─────────┬─────────────────────────────────┐
echo │   服务名    │  状态   │           访问地址              │
echo ├─────────────┼─────────┼─────────────────────────────────┤

:: 检查Gateway
netstat -an | find ":9501" | find "LISTENING" >nul 2>&1
if not errorlevel 1 (
    echo │ Gateway     │ 运行中  │ http://localhost:9501           │
    set /a RUNNING_COUNT+=1
) else (
    echo │ Gateway     │ 已停止  │ http://localhost:9501           │
)

:: 检查Auth
netstat -an | find ":9502" | find "LISTENING" >nul 2>&1
if not errorlevel 1 (
    echo │ Auth        │ 运行中  │ http://localhost:9502           │
    set /a RUNNING_COUNT+=1
) else (
    echo │ Auth        │ 已停止  │ http://localhost:9502           │
)

:: 检查Application
netstat -an | find ":9503" | find "LISTENING" >nul 2>&1
if not errorlevel 1 (
    echo │ Application │ 运行中  │ http://localhost:9503           │
    set /a RUNNING_COUNT+=1
) else (
    echo │ Application │ 已停止  │ http://localhost:9503           │
)

echo └─────────────┴─────────┴─────────────────────────────────┘
echo.

if !RUNNING_COUNT!==!TOTAL_COUNT! (
    echo 总体状态: 所有服务运行正常 √ ^(!RUNNING_COUNT!/!TOTAL_COUNT!^)
) else if !RUNNING_COUNT! gtr 0 (
    echo 总体状态: 部分服务运行 ⚠ ^(!RUNNING_COUNT!/!TOTAL_COUNT!^)
) else (
    echo 总体状态: 所有服务已停止 × ^(!RUNNING_COUNT!/!TOTAL_COUNT!^)
)
goto :eof

:: 主检查流程
:main
set TARGET_SERVICE=%~1

if not "%TARGET_SERVICE%"=="" (
    :: 检查指定服务
    if /i "%TARGET_SERVICE%"=="gateway" (
        call :print_header "检查 gateway 服务"
        call :check_single_service gateway 9501 Gateway
        if !SERVICE_HEALTHY!==1 (
            call :print_success "gateway 服务运行正常"
        ) else (
            call :print_error "gateway 服务存在问题"
            exit /b 1
        )
    ) else if /i "%TARGET_SERVICE%"=="auth" (
        call :print_header "检查 auth 服务"
        call :check_single_service auth 9502 Auth
        if !SERVICE_HEALTHY!==1 (
            call :print_success "auth 服务运行正常"
        ) else (
            call :print_error "auth 服务存在问题"
            exit /b 1
        )
    ) else if /i "%TARGET_SERVICE%"=="app" (
        call :print_header "检查 application 服务"
        call :check_single_service application 9503 Application
        if !SERVICE_HEALTHY!==1 (
            call :print_success "application 服务运行正常"
        ) else (
            call :print_error "application 服务存在问题"
            exit /b 1
        )
    ) else if /i "%TARGET_SERVICE%"=="application" (
        call :print_header "检查 application 服务"
        call :check_single_service application 9503 Application
        if !SERVICE_HEALTHY!==1 (
            call :print_success "application 服务运行正常"
        ) else (
            call :print_error "application 服务存在问题"
            exit /b 1
        )
    ) else (
        call :print_error "未知的服务名：%TARGET_SERVICE%"
        call :print_info "支持的服务名：gateway, auth, app"
        exit /b 1
    )
) else (
    :: 检查所有服务
    call :print_header "Collide 项目健康检查"
    
    set ALL_HEALTHY=1
    
    :: 逐个检查服务
    call :check_single_service gateway 9501 Gateway
    if !SERVICE_HEALTHY!==0 set ALL_HEALTHY=0
    
    call :check_single_service auth 9502 Auth
    if !SERVICE_HEALTHY!==0 set ALL_HEALTHY=0
    
    call :check_single_service application 9503 Application
    if !SERVICE_HEALTHY!==0 set ALL_HEALTHY=0
    
    echo.
    
    :: 显示系统信息
    call :show_system_info
    
    echo.
    
    :: 显示总结
    call :show_summary
    
    :: 设置退出码
    if !ALL_HEALTHY!==0 (
        exit /b 1
    )
)
goto :eof

:: 执行主流程
call :main %* 
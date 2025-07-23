@echo off
setlocal enabledelayedexpansion

:: Collide 项目日志管理脚本 (Windows版)
:: 用途：查看和管理 Gateway、Auth、Application 三个服务的日志

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

:: 显示使用帮助
:show_usage
echo 用法: %0 ^<服务名^> [选项]
echo.
echo 服务名:
echo   gateway, gw     - Gateway服务日志
echo   auth            - Auth服务日志
echo   app, application - Application服务日志
echo   all             - 所有服务日志
echo.
echo 选项:
echo   -n ^<数量^>       - 显示最后N行日志 ^(默认: 50^)
echo   -f, --follow    - 实时跟踪日志
echo   -e, --error     - 只显示错误和警告日志
echo   -t, --today     - 只显示今天的日志
echo   -s, --search ^<关键词^> - 搜索包含关键词的日志
echo   --clear         - 清空日志文件
echo   -h, --help      - 显示此帮助信息
echo.
echo 示例:
echo   %0 gateway              # 查看Gateway最后50行日志
echo   %0 auth -f              # 实时跟踪Auth日志
echo   %0 app -n 100           # 查看Application最后100行日志
echo   %0 gateway -e           # 只显示Gateway的错误日志
echo   %0 auth -t              # 只显示Auth今天的日志
echo   %0 app -s "error"       # 搜索Application日志中包含error的行
echo   %0 gateway --clear      # 清空Gateway日志文件
echo   %0 all -e               # 显示所有服务的错误日志
goto :eof

:: 获取日志文件路径
:get_log_file
set SERVICE_NAME=%~1
if /i "%SERVICE_NAME%"=="gateway" (
    set LOG_FILE=logs\gateway.log
) else if /i "%SERVICE_NAME%"=="gw" (
    set LOG_FILE=logs\gateway.log
) else if /i "%SERVICE_NAME%"=="auth" (
    set LOG_FILE=logs\auth.log
) else if /i "%SERVICE_NAME%"=="app" (
    set LOG_FILE=logs\application.log
) else if /i "%SERVICE_NAME%"=="application" (
    set LOG_FILE=logs\application.log
) else (
    set LOG_FILE=
)
goto :eof

:: 检查日志文件是否存在
:check_log_file
set LOG_FILE=%~1
set SERVICE_NAME=%~2

if not exist "%LOG_FILE%" (
    call :print_error "%SERVICE_NAME% 日志文件不存在: %LOG_FILE%"
    call :print_info "请确保服务已启动并生成了日志文件"
    exit /b 1
)
goto :eof

:: 显示日志文件信息
:show_log_info
set LOG_FILE=%~1
set SERVICE_NAME=%~2

if exist "%LOG_FILE%" (
    for %%i in ("%LOG_FILE%") do set FILE_SIZE=%%~zi
    for /f %%i in ('find /c /v "" ^< "%LOG_FILE%"') do set LINE_COUNT=%%i
    for %%i in ("%LOG_FILE%") do set LAST_MODIFIED=%%~ti
    
    echo 文件信息:
    echo   路径: %LOG_FILE%
    echo   大小: !FILE_SIZE! bytes
    echo   行数: !LINE_COUNT!
    echo   修改时间: !LAST_MODIFIED!
    echo.
)
goto :eof

:: 查看单个服务日志
:view_single_log
set SERVICE_NAME=%~1
set LOG_FILE=%~2
set LINES=%~3
set FOLLOW=%~4
set ERROR_ONLY=%~5
set TODAY_ONLY=%~6
set SEARCH_KEYWORD=%~7
set CLEAR_LOG=%~8

:: 检查日志文件
call :check_log_file "%LOG_FILE%" "%SERVICE_NAME%"
if errorlevel 1 goto :eof

:: 清空日志文件
if /i "%CLEAR_LOG%"=="true" (
    call :print_warning "即将清空 %SERVICE_NAME% 日志文件: %LOG_FILE%"
    set /p CONFIRM="确认清空？(y/N): "
    if /i "!CONFIRM!"=="y" (
        type nul > "%LOG_FILE%"
        call :print_success "%SERVICE_NAME% 日志文件已清空"
    ) else (
        call :print_info "操作已取消"
    )
    goto :eof
)

:: 显示日志头信息
call :print_header "%SERVICE_NAME% 服务日志"
call :show_log_info "%LOG_FILE%" "%SERVICE_NAME%"

:: 构建PowerShell命令来查看日志
set PS_CMD=Get-Content '%LOG_FILE%'

:: 设置行数
if not "%LINES%"=="50" (
    set PS_CMD=!PS_CMD! -Tail %LINES%
)

:: 添加过滤条件
set FILTER_ADDED=false

:: 今天的日志过滤
if /i "%TODAY_ONLY%"=="true" (
    for /f "tokens=2 delims= " %%i in ('date /t') do set TODAY_DATE=%%i
    if not defined TODAY_DATE (
        for /f "tokens=1 delims= " %%i in ('date /t') do set TODAY_DATE=%%i
    )
    :: 简化的日期格式处理
    set PS_CMD=!PS_CMD! | Where-Object { $_ -match '!TODAY_DATE:~0,4!!TODAY_DATE:~5,2!!TODAY_DATE:~8,2!' }
    set FILTER_ADDED=true
)

:: 错误日志过滤
if /i "%ERROR_ONLY%"=="true" (
    set PS_CMD=!PS_CMD! | Where-Object { $_ -match '(?i)(error|exception|fail|warn|fatal)' }
    set FILTER_ADDED=true
)

:: 关键词搜索
if not "%SEARCH_KEYWORD%"=="" (
    set PS_CMD=!PS_CMD! | Where-Object { $_ -match '(?i)%SEARCH_KEYWORD%' }
    set FILTER_ADDED=true
)

:: 实时跟踪
if /i "%FOLLOW%"=="true" (
    call :print_info "实时跟踪 %SERVICE_NAME% 日志，按 Ctrl+C 退出..."
    if "%FILTER_ADDED%"=="true" (
        call :print_info "注意：Windows环境下实时跟踪时过滤功能可能不稳定"
    )
    powershell -command "!PS_CMD! -Wait"
) else (
    powershell -command "!PS_CMD!"
)

goto :eof

:: 查看所有服务日志
:view_all_logs
set LINES=%~1
set FOLLOW=%~2
set ERROR_ONLY=%~3
set TODAY_ONLY=%~4
set SEARCH_KEYWORD=%~5

if /i "%FOLLOW%"=="true" (
    call :print_header "实时跟踪所有服务日志"
    call :print_info "按 Ctrl+C 退出..."
    call :print_warning "Windows环境下同时跟踪多个文件可能不稳定"
    echo.
    
    :: Windows下同时跟踪多个文件比较复杂，这里提供简化版本
    set LOG_FILES=
    if exist "logs\gateway.log" set LOG_FILES=!LOG_FILES! logs\gateway.log
    if exist "logs\auth.log" set LOG_FILES=!LOG_FILES! logs\auth.log
    if exist "logs\application.log" set LOG_FILES=!LOG_FILES! logs\application.log
    
    if defined LOG_FILES (
        powershell -command "Get-Content !LOG_FILES! -Wait"
    ) else (
        call :print_error "没有找到任何日志文件"
        exit /b 1
    )
) else (
    :: 逐个显示各服务日志
    if exist "logs\gateway.log" (
        call :view_single_log "gateway" "logs\gateway.log" "%LINES%" "false" "%ERROR_ONLY%" "%TODAY_ONLY%" "%SEARCH_KEYWORD%" "false"
        echo.
        echo ----------------------------------------
        echo.
    )
    if exist "logs\auth.log" (
        call :view_single_log "auth" "logs\auth.log" "%LINES%" "false" "%ERROR_ONLY%" "%TODAY_ONLY%" "%SEARCH_KEYWORD%" "false"
        echo.
        echo ----------------------------------------
        echo.
    )
    if exist "logs\application.log" (
        call :view_single_log "application" "logs\application.log" "%LINES%" "false" "%ERROR_ONLY%" "%TODAY_ONLY%" "%SEARCH_KEYWORD%" "false"
        echo.
        echo ----------------------------------------
        echo.
    )
)
goto :eof

:: 主函数
:main
set SERVICE_NAME=
set LINES=50
set FOLLOW=false
set ERROR_ONLY=false
set TODAY_ONLY=false
set SEARCH_KEYWORD=
set CLEAR_LOG=false

:: 解析参数
:parse_args
if "%~1"=="" goto :args_parsed

:: 服务名
if /i "%~1"=="gateway" (
    set SERVICE_NAME=%~1
    shift
    goto :parse_args
) else if /i "%~1"=="gw" (
    set SERVICE_NAME=%~1
    shift
    goto :parse_args
) else if /i "%~1"=="auth" (
    set SERVICE_NAME=%~1
    shift
    goto :parse_args
) else if /i "%~1"=="app" (
    set SERVICE_NAME=%~1
    shift
    goto :parse_args
) else if /i "%~1"=="application" (
    set SERVICE_NAME=%~1
    shift
    goto :parse_args
) else if /i "%~1"=="all" (
    set SERVICE_NAME=%~1
    shift
    goto :parse_args
)

:: 选项
if /i "%~1"=="-n" (
    if "%~2"=="" (
        call :print_error "参数 -n 需要一个数字"
        exit /b 1
    )
    set LINES=%~2
    shift
    shift
    goto :parse_args
) else if /i "%~1"=="-f" (
    set FOLLOW=true
    shift
    goto :parse_args
) else if /i "%~1"=="--follow" (
    set FOLLOW=true
    shift
    goto :parse_args
) else if /i "%~1"=="-e" (
    set ERROR_ONLY=true
    shift
    goto :parse_args
) else if /i "%~1"=="--error" (
    set ERROR_ONLY=true
    shift
    goto :parse_args
) else if /i "%~1"=="-t" (
    set TODAY_ONLY=true
    shift
    goto :parse_args
) else if /i "%~1"=="--today" (
    set TODAY_ONLY=true
    shift
    goto :parse_args
) else if /i "%~1"=="-s" (
    if "%~2"=="" (
        call :print_error "参数 -s/--search 需要一个关键词"
        exit /b 1
    )
    set SEARCH_KEYWORD=%~2
    shift
    shift
    goto :parse_args
) else if /i "%~1"=="--search" (
    if "%~2"=="" (
        call :print_error "参数 -s/--search 需要一个关键词"
        exit /b 1
    )
    set SEARCH_KEYWORD=%~2
    shift
    shift
    goto :parse_args
) else if /i "%~1"=="--clear" (
    set CLEAR_LOG=true
    shift
    goto :parse_args
) else if /i "%~1"=="-h" (
    call :show_usage
    exit /b 0
) else if /i "%~1"=="--help" (
    call :show_usage
    exit /b 0
) else (
    call :print_error "未知参数: %~1"
    call :show_usage
    exit /b 1
)

:args_parsed

:: 检查服务名是否提供
if "%SERVICE_NAME%"=="" (
    call :print_error "请指定服务名"
    call :show_usage
    exit /b 1
)

:: 处理服务名
if /i "%SERVICE_NAME%"=="all" (
    if /i "%CLEAR_LOG%"=="true" (
        call :print_error "不支持同时清空所有日志文件，请分别清空"
        exit /b 1
    )
    call :view_all_logs "%LINES%" "%FOLLOW%" "%ERROR_ONLY%" "%TODAY_ONLY%" "%SEARCH_KEYWORD%"
) else (
    :: 获取日志文件路径
    call :get_log_file "%SERVICE_NAME%"
    if "%LOG_FILE%"=="" (
        call :print_error "未知的服务名: %SERVICE_NAME%"
        call :print_info "支持的服务名: gateway, auth, app, all"
        exit /b 1
    )
    
    call :view_single_log "%SERVICE_NAME%" "%LOG_FILE%" "%LINES%" "%FOLLOW%" "%ERROR_ONLY%" "%TODAY_ONLY%" "%SEARCH_KEYWORD%" "%CLEAR_LOG%"
)

goto :eof

:: 执行主函数
call :main %* 
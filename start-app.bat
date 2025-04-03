@echo off
chcp 65001 > nul
echo 正在启动机器人龟棋应用...

REM 检查Docker是否已安装
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo Docker未安装或未运行。请安装Docker Desktop并确保它正在运行。
    pause
    exit /b 1
)

REM 构建并启动容器
echo 构建并启动容器...
docker-compose up --build -d

if %errorlevel% neq 0 (
    echo 启动失败，请检查错误信息。
    pause
    exit /b 1
)

echo.
echo 机器人龟棋应用已成功启动！
echo 前端: http://localhost:3000
echo 后端API: http://localhost:5000
echo.
echo 按任意键打开游戏...
pause >nul

start http://localhost:3000

echo.
echo 要停止应用，请运行 stop-app.bat 或在命令行中执行 docker-compose down
echo. 
@echo off
chcp 65001 > nul
echo 正在重建机器人龟棋应用...

REM 停止并删除现有容器
echo 停止并删除现有容器...
docker-compose down

REM 清理缓存镜像
echo 清理缓存镜像...
docker-compose rm -f

REM 重建并启动容器
echo 重建并启动容器...
docker-compose up --build -d

if %errorlevel% neq 0 (
    echo 重建失败，请检查错误信息。
    pause
    exit /b 1
)

echo.
echo 机器人龟棋应用已成功重建并启动！
echo 前端: http://localhost:3000
echo 后端API: http://localhost:5000
echo.
echo 按任意键打开游戏...
pause >nul

start http://localhost:3000

echo.
echo 查看容器日志:
echo docker-compose logs -f
echo. 
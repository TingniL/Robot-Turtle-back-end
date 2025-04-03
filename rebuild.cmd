@echo off
echo 正在重建机器人龟棋应用...

docker-compose down
docker-compose up --build -d

echo.
echo 机器人龟棋应用已启动！
echo 前端: http://localhost:3000
echo 后端API: http://localhost:5000
echo.
echo 按任意键打开游戏...
pause >nul

start http://localhost:3000 
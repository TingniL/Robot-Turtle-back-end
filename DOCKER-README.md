# 机器人龟棋 Docker 部署指南

## 简介

本文档提供使用 Docker 部署机器人龟棋游戏的详细说明。系统包含前端（React）和后端（Python Flask API）两个部分，通过 Docker Compose 进行编排和管理。

## 前提条件

1. 安装 [Docker Desktop](https://www.docker.com/products/docker-desktop/)
2. 确保 Docker 服务正在运行
3. 确保端口 3000 和 5000 未被占用

## 文件结构

```
G:/JAVA/
├── docker-compose.yml           # Docker Compose 配置文件
├── start-app.bat                # 启动脚本
├── stop-app.bat                 # 停止脚本
├── DOCKER-README.md             # 本文档
├── RobotTurtles-master/         # 后端代码与模型
│   ├── Dockerfile.backend       # 后端 Docker 构建文件
│   ├── improved_model/          # AI 模型文件
│   ├── robot-turtles-server/    # 后端服务器代码
│   └── ...
└── robot-turtles-frontend/      # 前端代码
    ├── Dockerfile.frontend      # 前端 Docker 构建文件
    └── ...
```

## 快速开始

### 方法一：使用批处理脚本（推荐）

1. 双击 `start-app.bat` 启动应用
2. 等待容器构建和启动完成
3. 浏览器将自动打开 http://localhost:3000 访问游戏
4. 完成后双击 `stop-app.bat` 停止应用

### 方法二：使用命令行

```bash
# 启动应用
docker-compose up -d

# 停止应用
docker-compose down
```

## 端口映射

- 前端: http://localhost:3000
- 后端 API: http://localhost:5000

## 常见问题

1. **启动失败**
   - 确保 Docker 服务正在运行
   - 检查端口 3000 和 5000 是否被占用
   - 查看 Docker 日志: `docker-compose logs`

2. **无法连接到游戏**
   - 确保容器正在运行: `docker-compose ps`
   - 检查防火墙设置是否阻止了端口访问

3. **AI 模型无法加载**
   - 检查 `improved_model` 目录中是否存在模型文件
   - 查看后端日志: `docker-compose logs backend`

## 自定义配置

如需修改端口或其他配置，请编辑 `docker-compose.yml` 文件。

## 查看日志

```bash
# 查看所有日志
docker-compose logs

# 查看前端日志
docker-compose logs frontend

# 查看后端日志
docker-compose logs backend

# 实时查看日志
docker-compose logs -f
```

---

# Robot Turtles Docker Deployment Guide

## Introduction

This document provides detailed instructions for deploying the Robot Turtles game using Docker. The system includes both frontend (React) and backend (Python Flask API) components, orchestrated and managed through Docker Compose.

## Prerequisites

1. Install [Docker Desktop](https://www.docker.com/products/docker-desktop/)
2. Ensure Docker service is running
3. Make sure ports 3000 and 5000 are not in use

## File Structure

```
G:/JAVA/
├── docker-compose.yml           # Docker Compose configuration file
├── start-app.bat                # Startup script
├── stop-app.bat                 # Shutdown script
├── DOCKER-README.md             # This document
├── RobotTurtles-master/         # Backend code and model
│   ├── Dockerfile.backend       # Backend Docker build file
│   ├── improved_model/          # AI model files
│   ├── robot-turtles-server/    # Backend server code
│   └── ...
└── robot-turtles-frontend/      # Frontend code
    ├── Dockerfile.frontend      # Frontend Docker build file
    └── ...
```

## Quick Start

### Method 1: Using Batch Scripts (Recommended)

1. Double-click `start-app.bat` to start the application
2. Wait for container build and startup to complete
3. Browser will automatically open http://localhost:3000
4. When finished, double-click `stop-app.bat` to stop the application

### Method 2: Using Command Line

```bash
# Start application
docker-compose up -d

# Stop application
docker-compose down
```

## Port Mapping

- Frontend: http://localhost:3000
- Backend API: http://localhost:5000

## Common Issues

1. **Startup Failure**
   - Ensure Docker service is running
   - Check if ports 3000 and 5000 are already in use
   - View Docker logs: `docker-compose logs`

2. **Cannot Connect to Game**
   - Ensure containers are running: `docker-compose ps`
   - Check if firewall settings are blocking port access

3. **AI Model Cannot Load**
   - Check if model files exist in the `improved_model` directory
   - View backend logs: `docker-compose logs backend`

## Custom Configuration

To modify ports or other configurations, edit the `docker-compose.yml` file.

## Viewing Logs

```bash
# View all logs
docker-compose logs

# View frontend logs
docker-compose logs frontend

# View backend logs
docker-compose logs backend

# View logs in real-time
docker-compose logs -f
``` 
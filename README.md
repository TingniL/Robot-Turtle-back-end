# Robot Turtles

Robot Turtles是一个基于Java开发的游戏项目，灵感来源于同名的编程教育桌游。这个项目包含了游戏的核心逻辑和服务器端实现。

## 项目结构

项目分为两个主要部分：

1. **RobotTurtles-master** - 游戏的核心实现
   - `src/com/company` - 包含游戏的主要类和逻辑
   - `imgCartes` - 游戏中使用的图像资源

2. **robot-turtles-server** - 服务器端实现
   - 基于Maven构建的Java服务器项目
   - 提供网络多人游戏支持

## 游戏简介

Robot Turtles是一个教授编程基础概念的游戏。玩家通过使用指令卡（如前进、左转、右转等）来控制自己的机器龟，目标是到达宝石位置。游戏中包含各种障碍物，玩家需要通过编写"程序"（指令序列）来避开这些障碍物并达到目标。

## 主要功能

- 多玩家支持
- 图形化界面
- 指令卡系统
- 障碍物系统
- 程序执行功能

## 技术栈

- Java
- Swing (GUI)
- Maven (服务器端)

## 如何运行

### 客户端

1. 克隆仓库
2. 使用Java IDE（如IntelliJ IDEA）打开RobotTurtles-master目录
3. 运行`src/com/company/Main.java`文件

### 服务器端

1. 进入robot-turtles-server目录
2. 使用Maven构建项目：`mvn clean install`
3. 运行生成的JAR文件

## 贡献

欢迎提交问题和改进建议！

## 许可证

[MIT](LICENSE) 
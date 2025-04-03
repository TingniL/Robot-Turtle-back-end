# Robot Turtles 游戏前端 / Robot Turtles Game Frontend

[🇫🇷 Version française plus bas](#frontend-du-jeu-robot-turtles)

## 简介 / Introduction

这是机器人龟棋游戏的React前端实现。游戏使用基于PPO (Proximal Policy Optimization) 强化学习的AI模型来控制或辅助玩家。

This is the React frontend implementation for the Robot Turtles game. The game uses an AI model based on PPO (Proximal Policy Optimization) reinforcement learning to control or assist the player.

## 功能 / Features

- 交互式游戏棋盘 / Interactive game board
- AI模式 / AI mode
- 玩家模式 / Player mode 
- AI决策可视化 / AI decision visualization
- 双语界面 (中/英) / Bilingual interface (CN/EN)

## 技术栈 / Tech Stack

- React
- TypeScript
- Axios 
- CSS (纯CSS，无UI库) / (Pure CSS, no UI libraries)

## 项目结构 / Project Structure

```
robot-turtles-frontend/
├── public/               # 静态资源 / Static assets
├── src/                  # 源代码 / Source code
│   ├── assets/           # 图片等资源 / Images and assets
│   ├── components/       # React组件 / React components
│   │   ├── Board.tsx     # 游戏棋盘组件 / Game board component
│   │   └── Cell.tsx      # 棋盘格子组件 / Board cell component
│   ├── hooks/            # 自定义钩子 / Custom hooks 
│   │   └── useGameState.ts # 游戏状态管理 / Game state management
│   ├── services/         # API服务 / API services
│   │   ├── api.ts        # 后端API调用 / Backend API calls
│   │   └── mockAI.ts     # 模拟AI服务 / Mock AI service
│   ├── App.tsx           # 主应用组件 / Main app component
│   └── index.tsx         # 入口文件 / Entry point
└── package.json          # 项目依赖 / Project dependencies
```

## 开发设置 / Development Setup

### 安装依赖 / Install Dependencies

```bash
npm install
```

### 启动开发服务器 / Start Development Server

```bash
npm start
```

### 构建生产版本 / Build for Production

```bash
npm run build
```

## 游戏说明 / Game Instructions

游戏目标是控制机器人龟到达宝石。可以使用以下操作：

The goal of the game is to control a robot turtle to reach a jewel. You can use the following actions:

- **前进 (Forward)**: 向当前方向移动一格 / Move one step in the current direction
- **左转 (Left)**: 向左转90度 / Turn 90 degrees left
- **右转 (Right)**: 向右转90度 / Turn 90 degrees right
- **激光 (Laser)**: 发射激光摧毁前方的墙 / Fire laser to destroy a wall in front

## 与后端集成 / Backend Integration

前端通过API与后端通信。如需连接到真实后端，请更新 `.env` 文件中的 `REACT_APP_API_URL`。

The frontend communicates with the backend via API. To connect to a real backend, update `REACT_APP_API_URL` in the `.env` file.

## AI模式 / AI Mode

在AI模式下，系统会显示每个可能动作的概率，并推荐最佳操作。

In AI mode, the system displays the probability for each possible action and recommends the best one.

---

# Frontend du Jeu Robot Turtles

## Introduction

Ceci est l'implémentation frontend React du jeu Robot Turtles. Le jeu utilise un modèle d'IA basé sur l'apprentissage par renforcement PPO (Proximal Policy Optimization) pour contrôler ou assister le joueur.

## Fonctionnalités

- Plateau de jeu interactif
- Mode IA
- Mode joueur
- Visualisation des décisions de l'IA
- Interface bilingue (CN/EN)

## Stack Technique

- React
- TypeScript
- Axios
- CSS (CSS pur, pas de bibliothèques UI)

## Structure du Projet

```
robot-turtles-frontend/
├── public/               # Ressources statiques
├── src/                  # Code source
│   ├── assets/           # Images et ressources
│   ├── components/       # Composants React
│   │   ├── Board.tsx     # Composant plateau de jeu
│   │   └── Cell.tsx      # Composant cellule du plateau
│   ├── hooks/            # Hooks personnalisés
│   │   └── useGameState.ts # Gestion de l'état du jeu
│   ├── services/         # Services API
│   │   ├── api.ts        # Appels API backend
│   │   └── mockAI.ts     # Service IA simulé
│   ├── App.tsx           # Composant principal de l'application
│   └── index.tsx         # Point d'entrée
└── package.json          # Dépendances du projet
```

## Configuration de Développement

### Installer les Dépendances

```bash
npm install
```

### Démarrer le Serveur de Développement

```bash
npm start
```

### Construire pour la Production

```bash
npm run build
```

## Instructions du Jeu

L'objectif du jeu est de contrôler une tortue robot pour atteindre un joyau. Vous pouvez utiliser les actions suivantes:

- **Avancer (Forward)**: Avancer d'une case dans la direction actuelle
- **Gauche (Left)**: Tourner de 90 degrés à gauche
- **Droite (Right)**: Tourner de 90 degrés à droite
- **Laser (Laser)**: Tirer un laser pour détruire un mur devant

## Intégration Backend

Le frontend communique avec le backend via API. Pour se connecter à un vrai backend, mettez à jour `REACT_APP_API_URL` dans le fichier `.env`.

## Mode IA

En mode IA, le système affiche la probabilité pour chaque action possible et recommande la meilleure. 
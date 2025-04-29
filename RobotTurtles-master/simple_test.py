#!/usr/bin/env python
# -*- coding: utf-8 -*-

import os
import sys
import numpy as np
import torch
import time
from datetime import datetime

# 使用简化环境，避免导入原始环境
class SimpleEnv:
    def __init__(self):
        self.size = 8
        self.reset()
    
    def reset(self):
        # 创建简单的8x8棋盘
        self.board = np.zeros((self.size, self.size), dtype=np.int8)
        # 初始化海龟位置 (7,3)
        self.turtle_pos = (7, 3)
        self.board[self.turtle_pos] = 1
        # 初始化宝石位置 (0,4)
        self.jewel_pos = (0, 4)
        self.board[self.jewel_pos] = 2
        # 初始化一些墙 (3,3) 和 (3,4)
        self.board[3, 3] = 3
        self.board[3, 4] = 3
        # 初始化方向 (0-北, 1-东, 2-南, 3-西)
        self.direction = 0
        self.steps = 0
        return self.board.flatten()  # 只返回棋盘状态，与训练时保持一致
    
    def step(self, action):
        # 记录步数
        self.steps += 1
        
        # 初始化奖励为每步的小惩罚
        reward = -0.1
        done = False
        
        # 处理动作
        if action == 0:  # 前进
            # 计算新位置
            if self.direction == 0:  # 北
                new_pos = (self.turtle_pos[0] - 1, self.turtle_pos[1])
            elif self.direction == 1:  # 东
                new_pos = (self.turtle_pos[0], self.turtle_pos[1] + 1)
            elif self.direction == 2:  # 南
                new_pos = (self.turtle_pos[0] + 1, self.turtle_pos[1])
            else:  # 西
                new_pos = (self.turtle_pos[0], self.turtle_pos[1] - 1)
            
            # 检查是否可以移动
            if (0 <= new_pos[0] < self.size and 
                0 <= new_pos[1] < self.size and
                self.board[new_pos] != 3):  # 不是墙
                # 更新位置
                self.board[self.turtle_pos] = 0
                self.turtle_pos = new_pos
                self.board[self.turtle_pos] = 1
        
        elif action == 1:  # 左转
            self.direction = (self.direction - 1) % 4
        
        elif action == 2:  # 右转
            self.direction = (self.direction + 1) % 4
        
        elif action == 3:  # 激光
            # 简化的激光，只检查前方是否有墙
            if self.direction == 0:  # 北
                pos = (self.turtle_pos[0] - 1, self.turtle_pos[1])
            elif self.direction == 1:  # 东
                pos = (self.turtle_pos[0], self.turtle_pos[1] + 1)
            elif self.direction == 2:  # 南
                pos = (self.turtle_pos[0] + 1, self.turtle_pos[1])
            else:  # 西
                pos = (self.turtle_pos[0], self.turtle_pos[1] - 1)
            
            # 检查是否在边界内且是墙
            if (0 <= pos[0] < self.size and 
                0 <= pos[1] < self.size and
                self.board[pos] == 3):
                # 移除墙
                self.board[pos] = 0
        
        # 检查是否到达宝石
        if self.turtle_pos == self.jewel_pos:
            reward = 10.0
            done = True
        
        # 检查是否超过最大步数
        if self.steps >= 50:
            done = True
        
        return self.board.flatten(), reward, done, {}

# 添加与create_basic_model.py相同的模型定义
class SimplePolicy(torch.nn.Module):
    def __init__(self, input_dim=64, hidden_dim=128, action_dim=4):  # 与训练时保持一致：输入维度是64
        super(SimplePolicy, self).__init__()
        self.actor = torch.nn.Sequential(
            torch.nn.Linear(input_dim, hidden_dim),
            torch.nn.ReLU(),
            torch.nn.Linear(hidden_dim, hidden_dim),
            torch.nn.ReLU(),
            torch.nn.Linear(hidden_dim, action_dim),
            torch.nn.Softmax(dim=-1)
        )
        
        self.critic = torch.nn.Sequential(
            torch.nn.Linear(input_dim, hidden_dim),
            torch.nn.ReLU(),
            torch.nn.Linear(hidden_dim, hidden_dim),
            torch.nn.ReLU(),
            torch.nn.Linear(hidden_dim, 1)
        )
    
    def forward(self, x):
        action_probs = self.actor(x)
        value = self.critic(x)
        return action_probs, value
    
    def act(self, x):
        """仅返回动作概率，用于推断"""
        return self.actor(x)

# 改进的适配器，专注于从检查点加载
class SimpleAdapter:
    def __init__(self, model_path):
        self.model_path = model_path
        self.model = None
        self.load_success = False
        
        # 创建模型实例
        self.model = SimplePolicy()
        
        # 尝试加载检查点
        try:
            print(f"尝试加载检查点: {model_path}")
            # 直接加载检查点
            checkpoint = None
            
            # 使用weights_only=False加载
            checkpoint = torch.load(model_path, map_location=torch.device('cpu'), weights_only=False)
            
            if isinstance(checkpoint, dict) and 'model_state_dict' in checkpoint:
                print("检测到有效的检查点格式")
                # 加载模型状态字典
                self.model.load_state_dict(checkpoint['model_state_dict'])
                print(f"成功从检查点加载模型状态，检查点轮次: {checkpoint.get('epoch', 'unknown')}")
                print(f"检查点平均奖励: {checkpoint.get('avg_reward', 'unknown')}")
                self.load_success = True
            else:
                print("检查点格式无效，使用随机初始化模型")
        except Exception as e:
            print(f"加载检查点失败: {str(e)}")
        
        # 设置为评估模式
        self.model.eval()
        print(f"模型设置为评估模式，加载{'成功' if self.load_success else '失败'}")
    
    def predict(self, observation):
        """根据观察预测动作"""
        try:
            # 转换为张量
            obs_tensor = torch.FloatTensor(observation).view(1, -1)
            
            # 预测动作
            with torch.no_grad():
                if self.load_success:
                    # 使用actor网络获取动作概率
                    action_probs = self.model.act(obs_tensor)
                    
                    # 打印动作概率分布以便调试
                    probs_np = action_probs.cpu().numpy()
                    print(f"动作概率: 前进={probs_np[0,0]:.4f}, 左转={probs_np[0,1]:.4f}, 右转={probs_np[0,2]:.4f}, 激光={probs_np[0,3]:.4f}")
                    
                    # 使用概率进行采样，而不是直接取最大值
                    # 这有助于探索更多策略，避免模型被困在重复动作中
                    if np.random.random() < 0.8:  # 80%的概率选择最高概率的动作
                        action = torch.argmax(action_probs).item()
                    else:  # 20%的概率随机探索
                        # 使用softmax概率进行加权随机选择
                        action = np.random.choice(4, p=probs_np[0])
                else:
                    # 随机动作作为后备
                    action = np.random.randint(0, 4)
                    print(f"使用随机动作: {action}")
                
                return action
        except Exception as e:
            print(f"预测过程中发生错误: {str(e)}")
            # 随机动作作为最后的后备
            return np.random.randint(0, 4)

# 测试函数
def test_model(model_path, episodes=10, log_file=None):
    # 创建环境和适配器
    env = SimpleEnv()
    adapter = SimpleAdapter(model_path)
    
    # 记录结果
    total_reward = 0
    wins = 0
    
    # 创建日志文件
    if log_file is None:
        log_file = f"test_results_{datetime.now().strftime('%Y%m%d_%H%M%S')}.txt"
    
    with open(log_file, 'w') as f:
        f.write(f"测试模型: {model_path}\n")
        f.write(f"开始时间: {datetime.now()}\n")
        f.write(f"测试回合数: {episodes}\n\n")
        
        # 运行测试
        for ep in range(episodes):
            f.write(f"回合 {ep+1}/{episodes}\n")
            
            # 重置环境
            obs = env.reset()
            episode_reward = 0
            done = False
            step = 0
            
            # 回合循环
            while not done and step < 50:
                # 获取动作
                action = adapter.predict(obs)
                f.write(f"  步骤 {step+1}: 动作={action}\n")
                
                # 执行动作
                obs, reward, done, _ = env.step(action)
                episode_reward += reward
                step += 1
                
                # 记录当前状态
                f.write(f"  位置: {env.turtle_pos}, 方向: {env.direction}, 奖励: {reward:.2f}\n")
                
                # 检查是否完成
                if done:
                    # 检查是否赢了
                    if env.turtle_pos == env.jewel_pos:
                        f.write(f"  成功到达宝石! 总步数: {step}\n")
                        wins += 1
                    else:
                        f.write(f"  回合结束. 总步数: {step}\n")
                    
                    f.write(f"  回合奖励: {episode_reward:.2f}\n\n")
                    break
            
            # 如果达到最大步数但没有完成
            if not done:
                f.write(f"  达到最大步数. 总步数: {step}\n")
                f.write(f"  回合奖励: {episode_reward:.2f}\n\n")
            
            # 累计总奖励
            total_reward += episode_reward
        
        # 输出总结
        f.write("\n总结\n")
        f.write(f"胜率: {wins}/{episodes} ({wins/episodes*100:.2f}%)\n")
        f.write(f"平均奖励: {total_reward/episodes:.2f}\n")
        f.write(f"测试结束时间: {datetime.now()}\n")
    
    # 返回结果
    print(f"测试完成! 胜率: {wins}/{episodes} ({wins/episodes*100:.2f}%)")
    print(f"平均奖励: {total_reward/episodes:.2f}")
    print(f"详细结果已保存到 {log_file}")
    
    return wins, total_reward/episodes

if __name__ == "__main__":
    import argparse
    
    parser = argparse.ArgumentParser(description='测试训练好的模型')
    parser.add_argument('--model', type=str, required=True, help='模型文件路径')
    parser.add_argument('--episodes', type=int, default=5, help='测试回合数')
    parser.add_argument('--log', type=str, default=None, help='日志文件路径')
    
    args = parser.parse_args()
    
    # 测试模型
    start_time = time.time()
    wins, avg_reward = test_model(args.model, args.episodes, args.log)
    end_time = time.time()
    
    print(f"总测试时间: {end_time - start_time:.2f} 秒") 
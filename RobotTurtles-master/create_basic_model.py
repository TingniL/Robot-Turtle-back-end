#!/usr/bin/env python
# -*- coding: utf-8 -*-

import os
import sys
import numpy as np
import torch
import torch.nn as nn
import torch.optim as optim
import random
from datetime import datetime

# 定义简单环境，与测试脚本中的相同
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
        return self.board.copy()
    
    def step(self, action):
        # 记录步数和前一个位置
        self.steps += 1
        prev_pos = self.turtle_pos
        prev_dist_to_jewel = self._manhattan_dist(self.turtle_pos, self.jewel_pos)
        
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
                
                # 计算移动后与宝石的距离
                new_dist_to_jewel = self._manhattan_dist(self.turtle_pos, self.jewel_pos)
                
                # 根据距离变化给予奖励
                if new_dist_to_jewel < prev_dist_to_jewel:
                    # 靠近宝石，给予正奖励
                    reward += 0.5
                elif new_dist_to_jewel > prev_dist_to_jewel:
                    # 远离宝石，给予负奖励
                    reward -= 0.5
        
        elif action == 1:  # 左转
            self.direction = (self.direction - 1) % 4
            # 稍微惩罚旋转，鼓励更直接的行动
            reward -= 0.05
        
        elif action == 2:  # 右转
            self.direction = (self.direction + 1) % 4
            # 稍微惩罚旋转，鼓励更直接的行动
            reward -= 0.05
        
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
                # 清除墙壁应该得到奖励
                reward += 1.0
        
        # 检查是否到达宝石
        if self.turtle_pos == self.jewel_pos:
            reward = 20.0  # 增加宝石奖励
            done = True
        
        # 检查是否超过最大步数
        if self.steps >= 50:
            # 如果接近宝石但未到达，给予一些奖励
            dist_to_jewel = self._manhattan_dist(self.turtle_pos, self.jewel_pos)
            if dist_to_jewel <= 3:
                reward += (10.0 / (dist_to_jewel + 1))  # 越近奖励越高
            done = True
        
        return self.board.copy(), reward, done, {}
    
    def _manhattan_dist(self, pos1, pos2):
        """计算两点间的曼哈顿距离"""
        return abs(pos1[0] - pos2[0]) + abs(pos1[1] - pos2[1])

# 定义简单的策略网络
class SimplePolicy(nn.Module):
    def __init__(self, input_dim=64, hidden_dim=128, action_dim=4):
        super(SimplePolicy, self).__init__()
        self.actor = nn.Sequential(
            nn.Linear(input_dim, hidden_dim),
            nn.ReLU(),
            nn.Linear(hidden_dim, hidden_dim),
            nn.ReLU(),
            nn.Linear(hidden_dim, action_dim),
            nn.Softmax(dim=-1)
        )
        
        self.critic = nn.Sequential(
            nn.Linear(input_dim, hidden_dim),
            nn.ReLU(),
            nn.Linear(hidden_dim, hidden_dim),
            nn.ReLU(),
            nn.Linear(hidden_dim, 1)
        )
    
    def forward(self, x):
        action_probs = self.actor(x)
        value = self.critic(x)
        return action_probs, value

# 简单的PPO训练
def train_policy(epochs=1000, batch_size=32, lr=0.001, gamma=0.99, epsilon=0.2, value_coef=0.5, entropy_coef=0.01):
    # 创建环境和策略
    env = SimpleEnv()
    policy = SimplePolicy()
    optimizer = optim.Adam(policy.parameters(), lr=lr)
    
    # 日志变量
    rewards_history = []
    avg_reward_history = []
    
    print(f"开始训练基础模型...\n总回合数: {epochs}")
    print(f"学习率: {lr}, Batch大小: {batch_size}, PPO epsilon: {epsilon}")
    
    for epoch in range(epochs):
        # 收集一个batch的经验
        states = []
        actions = []
        rewards = []
        log_probs = []
        values = []
        done_masks = []
        
        # 收集一个batch的经验
        for _ in range(batch_size):
            state = env.reset()
            episode_reward = 0
            episode_states = []
            episode_actions = []
            episode_rewards = []
            episode_log_probs = []
            episode_values = []
            episode_done_masks = []
            
            done = False
            
            while not done:
                # 转换状态为Tensor
                state_tensor = torch.FloatTensor(state).view(1, -1)
                
                # 获取动作概率和值
                with torch.no_grad():
                    action_probs, value = policy(state_tensor)
                
                # 选择动作
                action_dist = torch.distributions.Categorical(action_probs)
                action = action_dist.sample()
                log_prob = action_dist.log_prob(action)
                
                # 执行动作
                next_state, reward, done, _ = env.step(action.item())
                
                # 保存经验
                episode_states.append(state)
                episode_actions.append(action.item())
                episode_rewards.append(reward)
                episode_log_probs.append(log_prob)
                episode_values.append(value)
                episode_done_masks.append(0.0 if done else 1.0)
                
                state = next_state
                episode_reward += reward
                
                if done:
                    break
            
            # 添加到batch
            states.extend(episode_states)
            actions.extend(episode_actions)
            rewards.extend(episode_rewards)
            log_probs.extend(episode_log_probs)
            values.extend(episode_values)
            done_masks.extend(episode_done_masks)
            
            rewards_history.append(episode_reward)
        
        # 计算平均奖励
        recent_rewards = rewards_history[-batch_size:]
        avg_reward = sum(recent_rewards) / batch_size
        avg_reward_history.append(avg_reward)
        
        # 如果有胜利，记录下来
        wins = sum(1 for r in recent_rewards if r > 15.0)  # 胜利的奖励通常大于15
        win_rate = (wins / batch_size) * 100
        
        # 计算回报
        returns = []
        R = 0
        for r, mask in zip(reversed(rewards), reversed(done_masks)):
            R = r + gamma * R * mask
            returns.insert(0, R)
        returns = torch.tensor(returns)
        
        # 标准化回报
        if len(returns) > 1:
            returns = (returns - returns.mean()) / (returns.std() + 1e-8)
        
        # 转换为Tensor
        states = torch.FloatTensor(np.array(states)).view(len(states), -1)
        actions = torch.LongTensor(actions)
        old_log_probs = torch.cat(log_probs)
        old_values = torch.cat(values).squeeze()
        
        # 计算优势
        advantages = returns - old_values.detach()
        
        # 进行多次更新以提高样本效率
        for _ in range(3):  # 每批次数据更新多次
            # 更新策略
            optimizer.zero_grad()
            
            # 获取当前策略的预测
            action_probs, values = policy(states)
            
            # 创建分布
            dist = torch.distributions.Categorical(action_probs)
            new_log_probs = dist.log_prob(actions)
            
            # 策略损失 (PPO-Clip)
            ratio = torch.exp(new_log_probs - old_log_probs.detach())
            surr1 = ratio * advantages.detach()
            surr2 = torch.clamp(ratio, 1.0 - epsilon, 1.0 + epsilon) * advantages.detach()
            policy_loss = -torch.min(surr1, surr2).mean()
            
            # 值函数损失
            value_loss = value_coef * ((returns - values.squeeze()).pow(2).mean())
            
            # 熵正则化
            entropy = entropy_coef * dist.entropy().mean()
            
            # 总损失
            loss = policy_loss + value_loss - entropy
            
            # 反向传播
            loss.backward()
            optimizer.step()
        
        # 打印进度
        if (epoch + 1) % 50 == 0 or epoch == 0:
            print(f"Epoch {epoch+1}/{epochs} - Avg Reward: {avg_reward:.2f}, Win Rate: {win_rate:.1f}%")
    
    # 保存模型
    output_dir = "basic_model"
    os.makedirs(output_dir, exist_ok=True)
    
    model_path = os.path.join(output_dir, f"basic_model.pt")
    torch.save(policy, model_path)
    
    checkpoint_path = os.path.join(output_dir, f"basic_checkpoint.pt")
    torch.save({
        'epoch': epochs,
        'model_state_dict': policy.state_dict(),
        'optimizer_state_dict': optimizer.state_dict(),
        'avg_reward': avg_reward,
        'win_rate': win_rate
    }, checkpoint_path)
    
    print(f"\n训练完成!")
    print(f"最终平均奖励: {avg_reward:.2f}")
    print(f"最终胜率: {win_rate:.1f}%")
    print(f"模型已保存到: {model_path}")
    print(f"检查点已保存到: {checkpoint_path}")
    
    return policy, model_path, checkpoint_path

# 测试训练的策略
def test_policy(policy, episodes=10):
    env = SimpleEnv()
    total_reward = 0
    wins = 0
    
    print(f"\n开始测试模型...")
    
    for ep in range(episodes):
        state = env.reset()
        done = False
        episode_reward = 0
        steps = 0
        
        while not done:
            # 转换状态为Tensor
            state_tensor = torch.FloatTensor(state).view(1, -1)
            
            # 获取动作概率
            with torch.no_grad():
                action_probs, _ = policy(state_tensor)
            
            # 选择最佳动作
            action = torch.argmax(action_probs).item()
            
            # 执行动作
            state, reward, done, _ = env.step(action)
            episode_reward += reward
            steps += 1
            
            if done:
                if env.turtle_pos == env.jewel_pos:
                    print(f"回合 {ep+1}: 胜利! 步数: {steps}, 奖励: {episode_reward:.2f}")
                    wins += 1
                else:
                    print(f"回合 {ep+1}: 失败. 步数: {steps}, 奖励: {episode_reward:.2f}")
                    # 打印最终位置和方向
                    print(f"  最终位置: {env.turtle_pos}, 方向: {env.direction}, 距离宝石: {env._manhattan_dist(env.turtle_pos, env.jewel_pos)}")
        
        total_reward += episode_reward
    
    avg_reward = total_reward / episodes
    win_rate = (wins / episodes) * 100
    
    print(f"\n测试结果:")
    print(f"总回合数: {episodes}")
    print(f"胜率: {wins}/{episodes} ({win_rate:.2f}%)")
    print(f"平均奖励: {avg_reward:.2f}")
    
    return win_rate, avg_reward

if __name__ == "__main__":
    import argparse
    
    parser = argparse.ArgumentParser(description="训练和测试简单的RobotTurtles策略")
    parser.add_argument("--epochs", type=int, default=500, help="训练回合数")
    parser.add_argument("--batch_size", type=int, default=32, help="批量大小")
    parser.add_argument("--lr", type=float, default=0.002, help="学习率")
    parser.add_argument("--test", type=int, default=10, help="测试回合数")
    
    args = parser.parse_args()
    
    # 训练策略
    policy, model_path, checkpoint_path = train_policy(
        epochs=args.epochs,
        batch_size=args.batch_size,
        lr=args.lr
    )
    
    # 测试策略
    test_policy(policy, episodes=args.test) 
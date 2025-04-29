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
import time
import argparse

# 导入改进的环境和策略类
from improved_training import ImprovedEnv, ImprovedPolicy

def continue_training(checkpoint_path, additional_epochs=500, batch_size=64, lr=0.0005, 
                      gamma=0.99, epsilon=0.1, value_coef=0.5, entropy_coef=0.01, 
                      save_interval=100):
    """Continue training from an existing checkpoint"""
    
    # Check if checkpoint file exists
    if not os.path.exists(checkpoint_path):
        print(f"Error: Checkpoint file does not exist: {checkpoint_path}")
        return None, None
    
    # Create environment
    env = ImprovedEnv()
    
    # Create policy model
    policy = ImprovedPolicy()
    
    # Load checkpoint
    print(f"Loading checkpoint: {checkpoint_path}")
    try:
        checkpoint = torch.load(checkpoint_path, map_location=torch.device('cpu'))
        
        # Load model state
        policy.load_state_dict(checkpoint['model_state_dict'])
        print(f"Successfully loaded model state, checkpoint epoch: {checkpoint.get('epoch', 'unknown')}")
        
        # Create optimizer
        optimizer = optim.Adam(policy.parameters(), lr=lr)
        
        # Load optimizer state if available
        if 'optimizer_state_dict' in checkpoint:
            optimizer.load_state_dict(checkpoint['optimizer_state_dict'])
            print("Successfully loaded optimizer state")
        
        # Get previous performance metrics
        prev_avg_reward = checkpoint.get('avg_reward', 0)
        prev_win_rate = checkpoint.get('win_rate', 0)
        print(f"Previous average reward: {prev_avg_reward:.2f}, win rate: {prev_win_rate:.2f}%")
        
    except Exception as e:
        print(f"Error loading checkpoint: {str(e)}")
        print("Initializing new model and optimizer")
        policy = ImprovedPolicy()
        optimizer = optim.Adam(policy.parameters(), lr=lr)
        prev_avg_reward = 0
        prev_win_rate = 0
    
    # Learning rate scheduler - slower decay
    scheduler = optim.lr_scheduler.StepLR(optimizer, step_size=200, gamma=0.7)
    
    # Logging variables
    rewards_history = []
    avg_reward_history = []
    win_rates = []
    
    # Output directory
    output_dir = "improved_model_continued"
    os.makedirs(output_dir, exist_ok=True)
    
    print(f"\nStarting continued training...\nTotal training epochs: {additional_epochs}")
    print(f"Learning rate: {lr}, Batch size: {batch_size}, PPO epsilon: {epsilon}, Entropy coefficient: {entropy_coef}")
    
    start_time = time.time()
    best_win_rate = prev_win_rate
    best_checkpoint_path = None
    
    for epoch in range(additional_epochs):
        # 收集经验
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
                
                # 选择动作 - 使用epsilon-greedy策略增加探索
                if random.random() < 0.05:  # 5%的随机探索
                    action = torch.tensor([random.randint(0, 3)])
                    log_prob = torch.distributions.Categorical(action_probs).log_prob(action)
                else:
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
        
        # 计算本轮平均奖励
        recent_rewards = rewards_history[-batch_size:]
        avg_reward = sum(recent_rewards) / batch_size
        avg_reward_history.append(avg_reward)
        
        # 计算胜率
        wins = sum(1 for r in recent_rewards if r > 40.0)  # 胜利的奖励通常大于40
        win_rate = (wins / batch_size) * 100
        win_rates.append(win_rate)
        
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
        for _ in range(5):  # 增加更新次数
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
            
            # 梯度裁剪，防止梯度爆炸
            torch.nn.utils.clip_grad_norm_(policy.parameters(), max_norm=0.5)
            
            optimizer.step()
        
        # 更新学习率
        scheduler.step()
        
        # 打印进度
        if (epoch + 1) % 50 == 0 or epoch == 0:
            elapsed_time = time.time() - start_time
            print(f"Epoch {epoch+1}/{additional_epochs} - Avg Reward: {avg_reward:.2f}, Win Rate: {win_rate:.1f}% - 用时: {elapsed_time:.1f}秒")
        
        # 保存检查点
        if (epoch + 1) % save_interval == 0 or epoch == additional_epochs - 1:
            checkpoint_path = os.path.join(output_dir, f"improved_checkpoint_{epoch+1}.pt")
            torch.save({
                'epoch': checkpoint.get('epoch', 0) + epoch + 1,
                'model_state_dict': policy.state_dict(),
                'optimizer_state_dict': optimizer.state_dict(),
                'avg_reward': avg_reward,
                'win_rate': win_rate,
                'train_time': elapsed_time
            }, checkpoint_path)
            print(f"保存检查点到: {checkpoint_path}")
            
            # 如果是目前最好的模型，记录下来
            if win_rate > best_win_rate:
                best_win_rate = win_rate
                best_checkpoint_path = checkpoint_path
                # 保存最佳模型
                best_model_path = os.path.join(output_dir, "best_model.pt")
                torch.save(policy, best_model_path)
                print(f"新的最佳模型! 胜率: {win_rate:.1f}%, 保存到: {best_model_path}")
    
    # 保存最终模型
    final_model_path = os.path.join(output_dir, "final_model.pt")
    torch.save(policy, final_model_path)
    
    final_checkpoint_path = os.path.join(output_dir, "final_checkpoint.pt")
    torch.save({
        'epoch': checkpoint.get('epoch', 0) + additional_epochs,
        'model_state_dict': policy.state_dict(),
        'optimizer_state_dict': optimizer.state_dict(),
        'avg_reward': avg_reward,
        'win_rate': win_rate,
        'train_time': time.time() - start_time
    }, final_checkpoint_path)
    
    print(f"\n继续训练完成! 总用时: {time.time() - start_time:.1f}秒")
    print(f"最终平均奖励: {avg_reward:.2f}")
    print(f"最终胜率: {win_rate:.1f}%")
    print(f"最佳胜率: {best_win_rate:.1f}%")
    print(f"最佳模型检查点: {best_checkpoint_path if best_checkpoint_path else '无'}")
    print(f"最终模型已保存到: {final_model_path}")
    print(f"最终检查点已保存到: {final_checkpoint_path}")
    
    # 返回最终模型和最佳模型的路径
    return final_model_path, best_checkpoint_path or final_checkpoint_path

# 测试训练的策略，与原始测试函数类似
def test_improved_policy(model_path, episodes=10, verbose=True):
    """测试改进的策略模型"""
    print(f"仅测试模式，使用模型: {model_path}")
    
    # 尝试加载模型
    print(f"尝试加载模型: {model_path}")
    try:
        # 尝试直接加载模型
        model = torch.load(model_path, map_location=torch.device('cpu'))
        print("直接加载模型成功")
        if isinstance(model, dict) and 'model_state_dict' in model:
            # 这是一个检查点文件，从中提取模型状态
            print(f"从检查点加载模型成功: {model_path}")
            checkpoint = model
            model = ImprovedPolicy()
            model.load_state_dict(checkpoint['model_state_dict'])
            # 打印检查点信息
            print(f"检查点轮次: {checkpoint.get('epoch', 'unknown')}")
            print(f"检查点平均奖励: {checkpoint.get('avg_reward', 'unknown')}")
            print(f"检查点胜率: {checkpoint.get('win_rate', 'unknown')}%")
    except Exception as e:
        print(f"模型加载错误: {str(e)}")
        print("初始化随机模型以进行测试")
        model = ImprovedPolicy()
    
    # 切换到评估模式
    model.eval()
    
    # 创建环境
    env = ImprovedEnv()
    
    # 测试参数
    total_rewards = 0
    wins = 0
    loss_rewards = []
    
    # 创建日志文件
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    log_file = f"test_detailed_{timestamp}.txt"
    
    # 打开日志文件
    if verbose:
        print("\n开始测试模型...")
        with open(log_file, 'w', encoding='utf-8') as f:
            f.write(f"详细测试日志 - {timestamp}\n")
            f.write(f"模型: {model_path}\n\n")
    
    # 添加动作映射，便于调试
    action_names = ["前进", "左转", "右转", "激光"]
    
    # 运行测试回合
    for episode in range(1, episodes + 1):
        if verbose:
            print(f"回合 {episode}/{episodes}")
        
        state = env.reset()
        done = False
        episode_reward = 0
        step_count = 0
        
        while not done and step_count < 50:  # 限制最大步数
            # 转换状态
            state_tensor = torch.FloatTensor(state).view(1, -1)
            
            # 获取动作概率
            with torch.no_grad():
                action_probs, _ = model(state_tensor)
            
            # 记录当前位置和动作概率
            if verbose:
                # 打印当前状态信息
                x, y = env.turtle_pos
                direction = env.direction
                direction_symbol = "^>v<"[direction]  # 北东南西的符号表示
                
                # 打印概率详情
                probs_str = ", ".join([f"{action_names[i]}={action_probs[0][i]:.4f}" for i in range(4)])
                
                # 计算与宝石的距离
                dist = env._manhattan_dist(env.turtle_pos, env.jewel_pos)
                msg = f"  位置: ({x}, {y}), 方向: {direction} [{direction_symbol}], 距离宝石: {dist}, 动作概率: {probs_str}"
                print(msg)
                
                with open(log_file, 'a', encoding='utf-8') as f:
                    f.write(f"步骤 {step_count}: {msg}\n")
            
            # 选择动作 - 测试时使用贪婪策略
            action = torch.argmax(action_probs, dim=1).item()
            
            # 检查是否需要强制转向
            # 如果在边界并且试图向边界外移动，强制选择旋转动作
            if action == 0:  # 前进
                next_x, next_y = env.turtle_pos
                if env.direction == 0:  # 北
                    next_x -= 1
                elif env.direction == 1:  # 东
                    next_y += 1
                elif env.direction == 2:  # 南
                    next_x += 1
                else:  # 西
                    next_y -= 1
                
                # 检查是否会出界或撞墙
                if (next_x < 0 or next_x >= env.size or 
                    next_y < 0 or next_y >= env.size or
                    (0 <= next_x < env.size and 0 <= next_y < env.size and env.board[next_x, next_y] == 3)):
                    # 选择一个旋转动作
                    if verbose:
                        print(f"  警告: 检测到边界或墙壁，强制转向")
                        with open(log_file, 'a', encoding='utf-8') as f:
                            f.write(f"  警告: 检测到边界或墙壁，强制转向\n")
                    
                    # 优先选择朝向宝石的方向
                    jewel_x, jewel_y = env.jewel_pos
                    dx, dy = jewel_x - env.turtle_pos[0], jewel_y - env.turtle_pos[1]
                    
                    # 根据宝石位置选择转向
                    if abs(dx) > abs(dy):  # 优先调整x方向
                        if dx < 0 and env.direction != 0:  # 宝石在北方
                            action = 1 if env.direction == 1 else 2  # 左转或右转
                        elif dx > 0 and env.direction != 2:  # 宝石在南方
                            action = 1 if env.direction == 3 else 2  # 左转或右转
                    else:  # 优先调整y方向
                        if dy > 0 and env.direction != 1:  # 宝石在东方
                            action = 1 if env.direction == 2 else 2  # 左转或右转
                        elif dy < 0 and env.direction != 3:  # 宝石在西方
                            action = 1 if env.direction == 0 else 2  # 左转或右转
                    
                    if verbose:
                        print(f"  强制选择动作: {action} ({action_names[action]})")
                        with open(log_file, 'a', encoding='utf-8') as f:
                            f.write(f"  强制选择动作: {action} ({action_names[action]})\n")
            
            if verbose:
                print(f"  选择动作: {action} ({action_names[action]})")
                with open(log_file, 'a', encoding='utf-8') as f:
                    f.write(f"  选择动作: {action} ({action_names[action]})\n")
            
            # 执行动作
            prev_pos = env.turtle_pos
            next_state, reward, done, _ = env.step(action)
            
            # 验证位置变化的合理性
            if env.turtle_pos != prev_pos and action == 0:
                dx = abs(env.turtle_pos[0] - prev_pos[0])
                dy = abs(env.turtle_pos[1] - prev_pos[1])
                
                # 检查是否发生了不合理的位置变化 (只能移动一步)
                if dx > 1 or dy > 1 or (dx == 1 and dy == 1):
                    print(f"  警告: 检测到不正常的位置变化: {prev_pos} -> {env.turtle_pos}")
                    with open(log_file, 'a', encoding='utf-8') as f:
                        f.write(f"  警告: 检测到不正常的位置变化: {prev_pos} -> {env.turtle_pos}\n")
            
            state = next_state
            episode_reward += reward
            step_count += 1
            
            if done:
                if env.turtle_pos == env.jewel_pos:
                    wins += 1
                    if verbose:
                        msg = f"  回合结果: 成功! 总步数: {step_count}, 奖励: {episode_reward:.2f}"
                        print(msg)
                        with open(log_file, 'a', encoding='utf-8') as f:
                            f.write(f"{msg}\n")
                else:
                    if verbose:
                        dist_to_jewel = env._manhattan_dist(env.turtle_pos, env.jewel_pos)
                        msg = f"  最终位置: {env.turtle_pos}, 方向: {env.direction}, 距离宝石: {dist_to_jewel}"
                        print(msg)
                        msg = f"  回合结果: 失败. 总步数: {step_count}, 奖励: {episode_reward:.2f}"
                        print(msg)
                        with open(log_file, 'a', encoding='utf-8') as f:
                            f.write(f"  最终位置: {env.turtle_pos}, 方向: {env.direction}, 距离宝石: {dist_to_jewel}\n")
                            f.write(f"{msg}\n")
                    loss_rewards.append(episode_reward)
                break
        
        if not done:
            if verbose:
                dist_to_jewel = env._manhattan_dist(env.turtle_pos, env.jewel_pos)
                msg = f"  最终位置: {env.turtle_pos}, 方向: {env.direction}, 距离宝石: {dist_to_jewel}"
                print(msg)
                msg = f"  回合结果: 超时. 总步数: {step_count}, 奖励: {episode_reward:.2f}"
                print(msg)
                with open(log_file, 'a', encoding='utf-8') as f:
                    f.write(f"  最终位置: {env.turtle_pos}, 方向: {env.direction}, 距离宝石: {dist_to_jewel}\n")
                    f.write(f"{msg}\n")
            loss_rewards.append(episode_reward)
        
        total_rewards += episode_reward
    
    # 计算平均奖励和胜率
    avg_reward = total_rewards / episodes
    win_rate = (wins / episodes) * 100
    
    # 输出结果
    print("\n测试结果:")
    print(f"总回合数: {episodes}")
    print(f"胜率: {wins}/{episodes} ({win_rate:.2f}%)")
    print(f"平均奖励: {avg_reward:.2f}")
    
    if verbose:
        print(f"详细结果已保存到: {log_file}")
    
    # 写入总结
    with open(log_file, 'a', encoding='utf-8') as f:
        f.write("\n------ 总结 ------\n")
        f.write(f"总回合数: {episodes}\n")
        f.write(f"胜率: {wins}/{episodes} ({win_rate:.2f}%)\n")
        f.write(f"平均奖励: {avg_reward:.2f}\n")
    
    return win_rate, avg_reward

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Continue training and test improved RobotTurtles policy")
    parser.add_argument("--checkpoint", type=str, default="improved_model/improved_checkpoint.pt", 
                        help="Checkpoint file path to continue training from")
    parser.add_argument("--epochs", type=int, default=500, help="Number of epochs to continue training")
    parser.add_argument("--batch_size", type=int, default=64, help="Batch size")
    parser.add_argument("--lr", type=float, default=0.0005, help="Learning rate")
    parser.add_argument("--save_interval", type=int, default=100, help="Interval for saving checkpoints")
    parser.add_argument("--test", type=int, default=10, help="Number of test episodes")
    parser.add_argument("--test_only", action="store_true", help="Only test, no training")
    parser.add_argument("--test_model", type=str, help="Model file path to test")
    
    args = parser.parse_args()
    
    if args.test_only:
        # Test only mode
        model_path = args.test_model or args.checkpoint
        print(f"Test only mode, using model: {model_path}")
        test_improved_policy(model_path, episodes=args.test)
    else:
        # Continue training
        final_model_path, best_model_path = continue_training(
            args.checkpoint,
            additional_epochs=args.epochs,
            batch_size=args.batch_size,
            lr=args.lr,
            save_interval=args.save_interval
        )
        
        # Test the trained model
        if final_model_path:
            print("\nTesting final model:")
            test_improved_policy(final_model_path, episodes=args.test)
            
            if best_model_path != final_model_path:
                print("\nTesting best model:")
                test_improved_policy(best_model_path, episodes=args.test) 
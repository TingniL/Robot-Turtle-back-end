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

# 改进的简单环境，增强奖励函数
class ImprovedEnv:
    def __init__(self):
        self.size = 8
        # Initialize direction (0-North, 1-East, 2-South, 3-West)
        self.direction = 0
        # Initialize step counter
        self.steps = 0
        # Initialize turtle and jewel positions
        self.turtle_pos = (7, 3)  # Bottom center of the board
        self.jewel_pos = (0, 4)   # Top center of the board
        # Action history to detect repetitive actions
        self.action_history = []
        # Reset environment
        self.reset()
    
    def reset(self):
        """Reset the environment"""
        # Create an empty 8x8 board
        self.board = np.zeros((self.size, self.size), dtype=np.int8)
        
        # Reset step counter
        self.steps = 0
        
        # Reset action history
        self.action_history = []
        
        # Reset turtle position to initial position (7,3)
        self.turtle_pos = (7, 3)
        # Mark turtle position
        self.board[self.turtle_pos] = 1
        
        # Reset jewel position (0,4)
        self.jewel_pos = (0, 4)
        # Mark jewel position
        self.board[self.jewel_pos] = 2
        
        # Initialize walls at (3,3) and (3,4)
        self.board[3, 3] = 3
        self.board[3, 4] = 3
        
        # Reset direction to North
        self.direction = 0
        
        # Calculate initial distance to jewel
        self.last_dist_to_jewel = self._manhattan_dist(self.turtle_pos, self.jewel_pos)
        
        # Return flattened board state
        return self.board.flatten()
    
    def step(self, action):
        """Execute one action step and return new state, reward, done flag, and additional info"""
        # Record step count and previous position
        self.steps += 1
        prev_pos = self.turtle_pos
        prev_dist_to_jewel = self._manhattan_dist(self.turtle_pos, self.jewel_pos)
        
        # Record action history
        self.action_history.append(action)
        if len(self.action_history) > 10:
            self.action_history.pop(0)
        
        # Initialize reward with small penalty per step
        reward = -0.1
        done = False
        debug_info = {"action": action, "prev_pos": prev_pos}
        
        # Process the action
        if action == 0:  # Move forward
            # Calculate new position
            new_pos = None
            if self.direction == 0:  # North
                new_pos = (max(0, self.turtle_pos[0] - 1), self.turtle_pos[1])
                debug_info["move"] = "north"
            elif self.direction == 1:  # East
                new_pos = (self.turtle_pos[0], min(self.size - 1, self.turtle_pos[1] + 1))
                debug_info["move"] = "east"
            elif self.direction == 2:  # South
                new_pos = (min(self.size - 1, self.turtle_pos[0] + 1), self.turtle_pos[1])
                debug_info["move"] = "south"
            else:  # West
                new_pos = (self.turtle_pos[0], max(0, self.turtle_pos[1] - 1))
                debug_info["move"] = "west"
            
            debug_info["new_pos_calculated"] = new_pos
            
            # Check if movement is possible (not a wall and within bounds)
            can_move = False
            
            if (0 <= new_pos[0] < self.size and 
                0 <= new_pos[1] < self.size and
                self.board[new_pos] != 3):  # Not a wall
                can_move = True
            
            debug_info["can_move"] = can_move
            
            if can_move:
                # Clear old position marker
                self.board[self.turtle_pos] = 0
                # Update position
                self.turtle_pos = new_pos
                # Mark turtle at new position
                self.board[self.turtle_pos] = 1
                
                # Calculate new distance to jewel
                new_dist_to_jewel = self._manhattan_dist(self.turtle_pos, self.jewel_pos)
                debug_info["new_dist"] = new_dist_to_jewel
                
                # Adjust reward based on distance change
                if new_dist_to_jewel < prev_dist_to_jewel:
                    # Getting closer to jewel, positive reward
                    reward += 1.0  # Increased reward
                    debug_info["reward_closer"] = 1.0
                elif new_dist_to_jewel > prev_dist_to_jewel:
                    # Moving away from jewel, negative reward
                    reward -= 0.8
                    debug_info["reward_farther"] = -0.8
                
                # Extra reward for reaching a new position
                if prev_pos != self.turtle_pos:
                    reward += 0.1
                    debug_info["reward_new_pos"] = 0.1
            else:
                # Collision with wall or boundary, additional penalty
                reward -= 0.5
                debug_info["reward_blocked"] = -0.5
        
        elif action == 1:  # Turn left
            self.direction = (self.direction - 1) % 4
            debug_info["new_direction"] = self.direction
            # Greater penalty for rotation to encourage direct movement
            reward -= 0.2
            debug_info["reward_turn"] = -0.2
        
        elif action == 2:  # Turn right
            self.direction = (self.direction + 1) % 4
            debug_info["new_direction"] = self.direction
            # Greater penalty for rotation to encourage direct movement
            reward -= 0.2
            debug_info["reward_turn"] = -0.2
        
        elif action == 3:  # Laser
            # Simplified laser, only checks for walls directly ahead
            pos = None
            if self.direction == 0:  # North
                pos = (self.turtle_pos[0] - 1, self.turtle_pos[1])
            elif self.direction == 1:  # East
                pos = (self.turtle_pos[0], self.turtle_pos[1] + 1)
            elif self.direction == 2:  # South
                pos = (self.turtle_pos[0] + 1, self.turtle_pos[1])
            else:  # West
                pos = (self.turtle_pos[0], self.turtle_pos[1] - 1)
            
            debug_info["laser_pos"] = pos
            
            # Check if position is within bounds and is a wall
            hit_wall = False
            if (0 <= pos[0] < self.size and 
                0 <= pos[1] < self.size and
                self.board[pos] == 3):
                # Remove wall
                self.board[pos] = 0
                hit_wall = True
                # Higher reward for removing a wall
                reward += 2.0
                debug_info["reward_hit_wall"] = 2.0
            else:
                # Laser missed, penalty
                reward -= 0.3
                debug_info["reward_miss_wall"] = -0.3
            
            debug_info["hit_wall"] = hit_wall
        
        # Check for repetitive actions
        if len(self.action_history) >= 4:
            last_four = self.action_history[-4:]
            # Detect spinning in place
            if (last_four.count(1) + last_four.count(2) >= 3) and self.action_history[-1] in [1, 2]:
                reward -= 0.5  # Penalty for repetitive rotation
                debug_info["reward_repetitive"] = -0.5
        
        # Check if jewel reached
        if self.turtle_pos == self.jewel_pos:
            reward = 50.0  # Increased jewel reward
            done = True
            debug_info["reached_jewel"] = True
        
        # Check if maximum steps reached
        if self.steps >= 50:
            # Reward based on final state
            dist_to_jewel = self._manhattan_dist(self.turtle_pos, self.jewel_pos)
            debug_info["final_dist"] = dist_to_jewel
            # Higher reward for being closer to jewel
            if dist_to_jewel <= 3:
                final_reward = (20.0 / (dist_to_jewel + 1))
                reward += final_reward
                debug_info["reward_close_final"] = final_reward
            done = True
            debug_info["max_steps_reached"] = True
        
        # Record final debug information
        debug_info["final_pos"] = self.turtle_pos
        debug_info["final_direction"] = self.direction
        debug_info["total_reward"] = reward
        
        return self.board.flatten(), reward, done, debug_info
    
    def _manhattan_dist(self, pos1, pos2):
        """Calculate Manhattan distance between two points"""
        return abs(pos1[0] - pos2[0]) + abs(pos1[1] - pos2[1])

# Define policy network with added capability to handle directional input
class ImprovedPolicy(nn.Module):
    def __init__(self, input_dim=64, hidden_dim=128, action_dim=4):
        super(ImprovedPolicy, self).__init__()
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

# Improved PPO training with increased training epochs and update frequency
def train_improved_policy(epochs=1000, batch_size=64, lr=0.001, gamma=0.99, epsilon=0.2, 
                          value_coef=0.5, entropy_coef=0.02, save_interval=100):
    # Create environment and policy
    env = ImprovedEnv()
    policy = ImprovedPolicy()
    optimizer = optim.Adam(policy.parameters(), lr=lr)
    
    # Learning rate scheduler
    scheduler = optim.lr_scheduler.StepLR(optimizer, step_size=500, gamma=0.5)
    
    # Logging variables
    rewards_history = []
    avg_reward_history = []
    
    print(f"Starting improved model training...\nTotal epochs: {epochs}")
    print(f"Learning rate: {lr}, Batch size: {batch_size}, PPO epsilon: {epsilon}, Entropy coefficient: {entropy_coef}")
    
    # Create output directory
    output_dir = "improved_model"
    os.makedirs(output_dir, exist_ok=True)
    
    start_time = time.time()
    
    for epoch in range(epochs):
        # Collect experience
        states = []
        actions = []
        rewards = []
        log_probs = []
        values = []
        done_masks = []
        
        # Collect batch experience
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
                # Convert state to Tensor
                state_tensor = torch.FloatTensor(state).view(1, -1)
                
                # Get action probabilities and value
                with torch.no_grad():
                    action_probs, value = policy(state_tensor)
                
                # Select action
                action_dist = torch.distributions.Categorical(action_probs)
                action = action_dist.sample()
                log_prob = action_dist.log_prob(action)
                
                # Execute action
                next_state, reward, done, _ = env.step(action.item())
                
                # Save experience
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
            
            # Add to batch
            states.extend(episode_states)
            actions.extend(episode_actions)
            rewards.extend(episode_rewards)
            log_probs.extend(episode_log_probs)
            values.extend(episode_values)
            done_masks.extend(episode_done_masks)
            
            rewards_history.append(episode_reward)
        
        # Calculate average reward
        recent_rewards = rewards_history[-batch_size:]
        avg_reward = sum(recent_rewards) / batch_size
        avg_reward_history.append(avg_reward)
        
        # Record win rate
        wins = sum(1 for r in recent_rewards if r > 40.0)  # Rewards over 40 usually indicate success
        win_rate = (wins / batch_size) * 100
        
        # Calculate returns
        returns = []
        R = 0
        for r, mask in zip(reversed(rewards), reversed(done_masks)):
            R = r + gamma * R * mask
            returns.insert(0, R)
        returns = torch.tensor(returns)
        
        # Normalize returns
        if len(returns) > 1:
            returns = (returns - returns.mean()) / (returns.std() + 1e-8)
        
        # Convert to Tensors
        states = torch.FloatTensor(np.array(states)).view(len(states), -1)
        actions = torch.LongTensor(actions)
        old_log_probs = torch.cat(log_probs)
        old_values = torch.cat(values).squeeze()
        
        # Calculate advantages
        advantages = returns - old_values.detach()
        
        # Multiple updates per batch to improve sample efficiency
        for _ in range(4):  # Multiple updates per batch
            # Update policy
            optimizer.zero_grad()
            
            # Get current policy predictions
            action_probs, values = policy(states)
            
            # Create distribution
            dist = torch.distributions.Categorical(action_probs)
            new_log_probs = dist.log_prob(actions)
            
            # Policy loss (PPO-Clip)
            ratio = torch.exp(new_log_probs - old_log_probs.detach())
            surr1 = ratio * advantages.detach()
            surr2 = torch.clamp(ratio, 1.0 - epsilon, 1.0 + epsilon) * advantages.detach()
            policy_loss = -torch.min(surr1, surr2).mean()
            
            # Value function loss
            value_loss = value_coef * ((returns - values.squeeze()).pow(2).mean())
            
            # Entropy regularization
            entropy = entropy_coef * dist.entropy().mean()
            
            # Total loss
            loss = policy_loss + value_loss - entropy
            
            # Backpropagation
            loss.backward()
            optimizer.step()
        
        # Update learning rate
        scheduler.step()
        
        # Print progress
        if (epoch + 1) % 50 == 0 or epoch == 0:
            elapsed_time = time.time() - start_time
            print(f"Epoch {epoch+1}/{epochs} - Avg Reward: {avg_reward:.2f}, Win Rate: {win_rate:.1f}% - Time: {elapsed_time:.1f}s")
        
        # Periodically save model
        if (epoch + 1) % save_interval == 0 or epoch == epochs - 1:
            checkpoint_path = os.path.join(output_dir, f"improved_checkpoint_{epoch+1}.pt")
            torch.save({
                'epoch': epoch + 1,
                'model_state_dict': policy.state_dict(),
                'optimizer_state_dict': optimizer.state_dict(),
                'avg_reward': avg_reward,
                'win_rate': win_rate
            }, checkpoint_path)
            print(f"Checkpoint saved to: {checkpoint_path}")
    
    # Save final model
    final_model_path = os.path.join(output_dir, f"improved_model.pt")
    torch.save(policy, final_model_path)
    
    final_checkpoint_path = os.path.join(output_dir, f"improved_checkpoint.pt")
    torch.save({
        'epoch': epochs,
        'model_state_dict': policy.state_dict(),
        'optimizer_state_dict': optimizer.state_dict(),
        'avg_reward': avg_reward,
        'win_rate': win_rate
    }, final_checkpoint_path)
    
    print(f"\nTraining complete! Total time: {time.time() - start_time:.1f}s")
    print(f"Final average reward: {avg_reward:.2f}")
    print(f"Final win rate: {win_rate:.1f}%")
    print(f"Model saved to: {final_model_path}")
    print(f"Checkpoint saved to: {final_checkpoint_path}")
    
    return policy, final_model_path, final_checkpoint_path

# 测试训练的策略
def test_improved_policy(policy, episodes=10):
    env = ImprovedEnv()
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
    
    parser = argparse.ArgumentParser(description="训练和测试改进的RobotTurtles策略")
    parser.add_argument("--epochs", type=int, default=1000, help="训练回合数")
    parser.add_argument("--batch_size", type=int, default=64, help="批量大小")
    parser.add_argument("--lr", type=float, default=0.001, help="学习率")
    parser.add_argument("--test", type=int, default=10, help="测试回合数")
    parser.add_argument("--save_interval", type=int, default=200, help="保存检查点的间隔")
    
    args = parser.parse_args()
    
    # 训练策略
    policy, model_path, checkpoint_path = train_improved_policy(
        epochs=args.epochs,
        batch_size=args.batch_size,
        lr=args.lr,
        save_interval=args.save_interval
    )
    
    # 测试策略
    test_improved_policy(policy, episodes=args.test) 
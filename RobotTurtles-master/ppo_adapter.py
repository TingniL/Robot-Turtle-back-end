#!/usr/bin/env python
# -*- coding: utf-8 -*-

import os
import sys
import numpy as np
import torch
import torch.nn as nn
import argparse

# 添加AI项目路径
ai_path = os.path.abspath(os.path.join(os.path.dirname(__file__), '..', 'RobotTurtles-AI'))
if os.path.exists(ai_path):
    sys.path.append(ai_path)
    print(f"Added path: {ai_path}")
    
    # 尝试导入AI项目的模型
    try:
        from src.models.network import RobotTurtlesNet
        print("Imported from src.models.network")
    except ImportError:
        try:
            from models.network import RobotTurtlesNet
            print("Imported from models.network directly")
        except ImportError:
            print("Could not import RobotTurtlesNet")
else:
    print(f"AI path not found: {ai_path}")

try:
    from RobotTurtles_AI.src.environment.game_env import GameConfig
except ImportError:
    try:
        from environment.game_env import GameConfig
    except ImportError:
        print("Failed to import GameConfig")
        raise

class PPOAdapter:
    """PPO模型适配器，用于连接训练好的PPO模型与原始游戏环境"""
    
    def __init__(self, model_path):
        """初始化适配器
        
        Args:
            model_path: 模型文件路径
        """
        self.model_path = model_path
        
        # 加载模型
        try:
            print(f"Loading model from {model_path}")
            checkpoint = torch.load(model_path, map_location=torch.device('cpu'))
            print("Model loaded successfully!")
            
            # 检查是否为检查点格式
            if 'model_state_dict' in checkpoint:
                print("Model is in checkpoint format")
                # 获取实际的模型状态字典
                model_state_dict = checkpoint['model_state_dict']
                
                # 创建一个简单的神经网络
                try:
                    # 尝试导入原始的神经网络类
                    from src.models.network import RobotTurtlesNet
                    self.policy_net = RobotTurtlesNet()
                    # 加载状态字典
                    self.policy_net.load_state_dict(model_state_dict)
                    print("Original model architecture loaded")
                except Exception as e:
                    print(f"Could not load with original architecture: {str(e)}")
                    # 使用简化的神经网络
                    self.policy_net = SimplifiedNet(input_dim=64)
                    # 尝试加载关键的权重
                    try:
                        # 提取和适配关键层的权重
                        self._adapt_weights(model_state_dict)
                        print("Adapted weights to simplified model")
                    except Exception as e:
                        print(f"Failed to adapt weights: {str(e)}")
                        # 随机初始化
                        print("Using randomly initialized model")
            elif isinstance(checkpoint, dict):
                print("Model is in state_dict format")
                # 创建一个简单的神经网络
                self.policy_net = ActorNetwork(input_dim=(8*8), hidden_dim=128, num_actions=4)
                self.policy_net.load_state_dict(checkpoint)
            else:
                print("Model is a complete model object")
                self.policy_net = checkpoint
            
            # 将模型设置为评估模式
            self.policy_net.eval()
            
        except Exception as e:
            print(f"Error loading model: {str(e)}")
            # 创建一个随机模型
            print("Creating a random model")
            self.policy_net = SimplifiedNet(input_dim=64)
            self.policy_net.eval()
    
    def _adapt_weights(self, state_dict):
        """适配复杂模型权重到简化模型
        
        Args:
            state_dict: 原始模型的状态字典
        """
        # 打印状态字典中的所有键，以便于调试
        print("Available keys in state_dict:")
        for key in state_dict.keys():
            print(f"  - {key}")
        
        # 尝试适配各种可能的键名
        try:
            # 检查是否有actor网络
            if any('actor' in key for key in state_dict.keys()):
                # 寻找第一个actor相关的权重矩阵
                actor_weight_key = next((k for k in state_dict.keys() if 'actor' in k and 'weight' in k), None)
                actor_bias_key = next((k for k in state_dict.keys() if 'actor' in k and 'bias' in k), None)
                
                if actor_weight_key and actor_bias_key:
                    print(f"Using actor weights from: {actor_weight_key}")
                    # 获取输入和输出维度
                    src_weight = state_dict[actor_weight_key]
                    out_features, in_features = src_weight.shape
                    
                    # 如果维度匹配，直接复制
                    if out_features == self.policy_net.actor_layer.weight.shape[0] and in_features == self.policy_net.actor_layer.weight.shape[1]:
                        self.policy_net.actor_layer.weight.data.copy_(src_weight)
                        self.policy_net.actor_layer.bias.data.copy_(state_dict[actor_bias_key])
                    else:
                        print(f"Dimension mismatch: source {src_weight.shape}, target {self.policy_net.actor_layer.weight.shape}")
                        
            # 如果没有actor网络，尝试使用最后一个线性层
            else:
                linear_weight_keys = [k for k in state_dict.keys() if 'weight' in k and 'linear' in k.lower()]
                if linear_weight_keys:
                    last_linear_weight = linear_weight_keys[-1]
                    last_linear_bias = last_linear_weight.replace('weight', 'bias')
                    
                    if last_linear_bias in state_dict:
                        print(f"Using final linear layer: {last_linear_weight}")
                        self.policy_net.actor_layer.weight.data.copy_(state_dict[last_linear_weight])
                        self.policy_net.actor_layer.bias.data.copy_(state_dict[last_linear_bias])
        
        except Exception as e:
            print(f"Error during weight adaptation: {str(e)}")
            # 不抛出异常，让模型继续随机初始化

    def predict(self, observation):
        """预测动作
        
        Args:
            observation: 游戏观察，可能是多种格式
            
        Returns:
            预测的动作索引
        """
        # 如果observation不是二维数组，尝试转换
        if not isinstance(observation, np.ndarray):
            try:
                # 尝试获取board属性
                if hasattr(observation, 'board'):
                    observation = observation.board
                # 转换为numpy数组
                observation = np.array(observation)
            except:
                print("Warning: Could not convert observation to numpy array")
                # 创建默认观察
                observation = np.zeros((8, 8), dtype=np.int8)
        
        # 确保observation是2D数组
        if observation.ndim == 1:
            # 尝试将1D数组重塑为2D
            board_size = int(np.sqrt(observation.shape[0]))
            observation = observation.reshape(board_size, board_size)
        
        # 标准化处理，确保数据在0-1之间
        if observation.max() > 1:
            observation = observation / max(1, observation.max())
        
        # 转换为PyTorch张量
        obs_tensor = torch.FloatTensor(observation)
        
        # 使用模型预测动作
        try:
            with torch.no_grad():
                # 首先尝试使用RobotTurtlesNet的调用方式
                if hasattr(self.policy_net, 'forward_inference'):
                    # 调整输入格式为模型期望的格式
                    # 对于RobotTurtlesNet，通常需要的是字典格式
                    board_tensor = obs_tensor.unsqueeze(0)  # 添加batch维度
                    
                    # 如果模型期望9通道输入
                    if hasattr(self.policy_net, 'channels') and self.policy_net.channels == 9:
                        # 创建9通道的输入
                        board_9ch = torch.zeros((1, 9, board_tensor.size(1), board_tensor.size(2)))
                        # 填充通道0（通常代表海龟位置）
                        turtle_positions = (board_tensor == 1)
                        board_9ch[:, 0] = turtle_positions
                        # 填充通道1（通常代表墙和冰）
                        walls = (board_tensor == 3) | (board_tensor == 4)
                        board_9ch[:, 1] = walls
                        # 填充通道2（通常代表宝石）
                        jewels = (board_tensor == 2)
                        board_9ch[:, 2] = jewels
                        
                        # 创建输入字典
                        model_input = {
                            'board': board_9ch.to(torch.float),
                            'hand': torch.zeros((1, 5, 5)),  # 简化的手牌
                            'program': torch.zeros((1, 10, 5))  # 简化的程序
                        }
                    else:
                        # 使用简单的展平观察
                        model_input = {
                            'board': board_tensor.to(torch.float),
                            'hand': torch.zeros((1, 5, 5)),  # 简化的手牌
                            'program': torch.zeros((1, 10, 5))  # 简化的程序
                        }
                    
                    # 使用inference模式调用
                    outputs = self.policy_net.forward_inference(model_input)
                    action_probs = outputs['action_probs'][0]
                    action = torch.argmax(action_probs).item()
                    
                # 尝试SimplifiedNet和ActorNetwork的调用方式
                elif hasattr(self.policy_net, 'forward'):
                    # 调整输入格式
                    if obs_tensor.dim() == 2:
                        obs_tensor = obs_tensor.unsqueeze(0).flatten(1)
                    elif obs_tensor.dim() == 1:
                        obs_tensor = obs_tensor.unsqueeze(0)
                    
                    # 调用forward方法
                    try:
                        action_probs, _ = self.policy_net(obs_tensor)
                        action = torch.argmax(action_probs).item()
                    except:
                        # 尝试只使用actor部分
                        if hasattr(self.policy_net, 'actor'):
                            action_probs = self.policy_net.actor(obs_tensor)
                            action = torch.argmax(action_probs).item()
                        else:
                            raise ValueError("Model does not have expected structure")
                
                # 如果以上方法都失败，使用随机动作
                else:
                    print("Warning: Using fallback random action")
                    action = np.random.randint(0, 4)
                
                return action
                
        except Exception as e:
            print(f"Error during prediction: {str(e)}")
            # 发生错误时使用随机动作
            return np.random.randint(0, 4)

# 简单的Actor网络模型，用于加载state_dict
class ActorNetwork(nn.Module):
    def __init__(self, input_dim, hidden_dim, num_actions):
        super(ActorNetwork, self).__init__()
        self.actor = nn.Sequential(
            nn.Linear(input_dim, hidden_dim),
            nn.ReLU(),
            nn.Linear(hidden_dim, hidden_dim),
            nn.ReLU(),
            nn.Linear(hidden_dim, num_actions),
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

# 简化的神经网络模型，用于适配原始模型
class SimplifiedNet(nn.Module):
    def __init__(self, input_dim=64, hidden_dim=64, num_actions=4):
        super(SimplifiedNet, self).__init__()
        self.input_dim = input_dim
        self.hidden_dim = hidden_dim
        self.num_actions = num_actions
        
        # 简单的前馈网络
        self.shared_layers = nn.Sequential(
            nn.Linear(input_dim, hidden_dim),
            nn.ReLU()
        )
        
        # 策略头
        self.actor_layer = nn.Linear(hidden_dim, num_actions)
        
        # 价值头
        self.critic_layer = nn.Linear(hidden_dim, 1)
        
    def forward(self, x):
        """前向传播
        
        Args:
            x: 输入张量，形状为 [batch_size, input_dim]
            
        Returns:
            action_probs: 动作概率分布
            value: 状态值
        """
        # 如果输入是字典，提取棋盘表示
        if isinstance(x, dict):
            if 'board' in x:
                x = x['board']
            
        # 确保输入是2D张量 [batch_size, input_dim]
        if x.dim() > 2:
            # 如果是多维张量（例如3D或4D），将其展平
            x = x.reshape(x.size(0), -1)
        elif x.dim() == 1:
            # 如果是1D张量，增加batch维度
            x = x.unsqueeze(0)
        
        # 共享层
        x = self.shared_layers(x)
        
        # 策略头
        logits = self.actor_layer(x)
        action_probs = torch.softmax(logits, dim=-1)
        
        # 价值头
        value = self.critic_layer(x)
        
        return action_probs, value

def test_adapter(model_path, episodes=5):
    """测试适配器"""
    # 定义一个使用简化测试环境类的标志
    use_simplified_env = False
    
    try:
        # 尝试导入原始项目的环境
        from environment.game_env import RobotTurtlesEnv
        print("Successfully imported original RobotTurtlesEnv")
        # 创建环境
        env = RobotTurtlesEnv(board_size=8)  # 确保传递board_size参数
    except (ImportError, TypeError) as e:
        print(f"Error with original environment: {str(e)}")
        try:
            # 如果上面的导入或初始化失败，尝试从当前目录导入
            sys.path.append(os.path.abspath(os.path.dirname(__file__)))
            from src.environment.game_env import RobotTurtlesEnv
            print("Imported RobotTurtlesEnv from src.environment")
            # 创建环境
            env = RobotTurtlesEnv(board_size=8)
        except (ImportError, TypeError) as e:
            print(f"Error with src.environment: {str(e)}")
            print("Using simplified test environment...")
            # 设置标志使用简化环境
            use_simplified_env = True
            
    # 如果需要使用简化环境，在此定义并创建
    if use_simplified_env:
        # 创建一个简化版的测试环境
        class SimplifiedRobotTurtlesEnv:
            def __init__(self, board_size=8):
                self.board_size = board_size
                self.board = np.zeros((self.board_size, self.board_size), dtype=np.int8)
                self.jewel_positions = [(0, 4), (0, 6)]
                self.player_pos = (7, 3)
                self.direction = 0  # 0: north, 1: east, 2: south, 3: west
                self.steps = 0
                
            def reset(self):
                self.board = np.zeros((self.board_size, self.board_size), dtype=np.int8)
                self.player_pos = (7, 3)
                self.board[self.player_pos] = 1  # player
                for pos in self.jewel_positions:
                    self.board[pos] = 2  # jewel
                self.board[3, 3] = 3  # wall
                self.board[4, 5] = 4  # ice
                self.direction = 0
                self.steps = 0
                return self.board.copy()
                
            def step(self, action):
                self.steps += 1
                reward = -0.1  # small penalty for each step
                
                if action == 0:  # forward
                    dx, dy = [(-1, 0), (0, 1), (1, 0), (0, -1)][self.direction]
                    new_pos = (self.player_pos[0] + dx, self.player_pos[1] + dy)
                    if 0 <= new_pos[0] < self.board_size and 0 <= new_pos[1] < self.board_size:
                        if self.board[new_pos] not in [3]:  # not wall
                            self.board[self.player_pos] = 0
                            self.player_pos = new_pos
                            self.board[self.player_pos] = 1
                elif action == 1:  # turn left
                    self.direction = (self.direction - 1) % 4
                elif action == 2:  # turn right
                    self.direction = (self.direction + 1) % 4
                elif action == 3:  # laser
                    # 简化版，激光可以清除冰块
                    dx, dy = [(-1, 0), (0, 1), (1, 0), (0, -1)][self.direction]
                    pos = self.player_pos
                    while True:
                        pos = (pos[0] + dx, pos[1] + dy)
                        if not (0 <= pos[0] < self.board_size and 0 <= pos[1] < self.board_size):
                            break
                        if self.board[pos] == 4:  # ice
                            self.board[pos] = 0
                            break
                        if self.board[pos] in [1, 2, 3]:  # player, jewel, wall
                            break
                
                # Check win condition
                done = self.player_pos in self.jewel_positions
                if done:
                    reward = 100  # big reward for reaching jewel
                    
                # Check if max steps reached
                if self.steps >= 50:
                    done = True
                    
                return self.board.copy(), reward, done, {}
                
            def render(self):
                symbols = ['.', 'T', 'J', 'W', 'I']  # 空、海龟、宝石、墙、冰
                directions = ['↑', '→', '↓', '←']
                print(f"Step: {self.steps}, Direction: {directions[self.direction]}")
                for i in range(self.board_size):
                    row = ""
                    for j in range(self.board_size):
                        if (i, j) == self.player_pos:
                            row += 'T '
                        else:
                            row += symbols[int(self.board[i, j])] + ' '
                    print(row)
                print()
        
        # 创建简化环境实例
        env = SimplifiedRobotTurtlesEnv()
        print("Created simplified environment")
                            
    print(f"Testing PPO adapter with model: {model_path}")
    adapter = PPOAdapter(model_path)
    
    total_rewards = 0
    wins = 0
    
    # 运行多个回合进行测试
    for episode in range(episodes):
        print(f"\n=== Episode {episode+1}/{episodes} ===")
        obs = env.reset()
        done = False
        episode_reward = 0
        step_count = 0
        
        while not done and step_count < 50:  # 限制最大步数
            step_count += 1
            
            # 获取适配器预测的动作
            action = adapter.predict(obs)
            print(f"Step {step_count}: Action = {action}")
            
            # 执行动作
            obs, reward, done, _ = env.step(action)
            episode_reward += reward
            
            # 渲染环境（如果支持）
            try:
                env.render()
            except:
                print(f"Current position: {getattr(env, 'player_pos', 'unknown')}")
            
            if done:
                if reward > 10:  # 赢得游戏
                    print(f"Episode {episode+1} WON in {step_count} steps!")
                    wins += 1
                else:
                    print(f"Episode {episode+1} ended in {step_count} steps")
                break
        
        total_rewards += episode_reward
        print(f"Episode {episode+1} reward: {episode_reward:.2f}")
    
    # 显示测试结果
    win_rate = (wins / episodes) * 100
    avg_reward = total_rewards / episodes
    print(f"\n=== Test Results ===")
    print(f"Wins: {wins}/{episodes} ({win_rate:.2f}%)")
    print(f"Average reward: {avg_reward:.2f}")
    
    return win_rate, avg_reward

if __name__ == "__main__":
    import argparse
    
    parser = argparse.ArgumentParser(description="Test PPO model in Robot Turtles game")
    parser.add_argument("--model", type=str, required=True, help="Path to the trained model")
    parser.add_argument("--episodes", type=int, default=5, help="Number of episodes to test")
    
    args = parser.parse_args()
    test_adapter(args.model, args.episodes) 
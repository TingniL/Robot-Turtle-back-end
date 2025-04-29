import numpy as np
import torch
import torch.nn as nn
import torch.nn.functional as F

class DQN(nn.Module):
    """Deep Q-Network模型"""
    
    def __init__(self, board_size=8, n_actions=4):
        """
        初始化DQN模型
        
        Args:
            board_size (int): 棋盘大小
            n_actions (int): 动作空间大小
        """
        super(DQN, self).__init__()
        
        # 输入通道：棋盘状态（空地、海龟、宝石、墙、冰块）+ 方向
        self.input_channels = 5 + 4
        
        # 卷积层
        self.conv1 = nn.Conv2d(self.input_channels, 32, kernel_size=3, padding=1)
        self.conv2 = nn.Conv2d(32, 64, kernel_size=3, padding=1)
        self.conv3 = nn.Conv2d(64, 64, kernel_size=3, padding=1)
        
        # 计算全连接层的输入维度
        conv_out_size = board_size * board_size * 64
        
        # 全连接层
        self.fc1 = nn.Linear(conv_out_size, 512)
        self.fc2 = nn.Linear(512, n_actions)
        
        # Dueling DQN
        self.value_stream = nn.Linear(512, 1)
        self.advantage_stream = nn.Linear(512, n_actions)
        
        # 初始化权重
        self._init_weights()
        
    def _init_weights(self):
        """初始化网络权重"""
        for m in self.modules():
            if isinstance(m, nn.Conv2d):
                nn.init.kaiming_normal_(m.weight, mode='fan_out', nonlinearity='relu')
            elif isinstance(m, nn.Linear):
                nn.init.kaiming_normal_(m.weight, mode='fan_out', nonlinearity='relu')
                nn.init.constant_(m.bias, 0)
    
    def forward(self, state):
        """
        前向传播
        
        Args:
            state: 游戏状态 [batch_size, channels, board_size, board_size]
            
        Returns:
            Q值 [batch_size, n_actions]
        """
        x = F.relu(self.conv1(state))
        x = F.relu(self.conv2(x))
        x = F.relu(self.conv3(x))
        
        x = x.view(x.size(0), -1)  # 展平
        
        x = F.relu(self.fc1(x))
        
        # Dueling DQN: 分离价值和优势
        value = self.value_stream(x)
        advantage = self.advantage_stream(x)
        
        # Q = V + (A - mean(A))
        q = value + (advantage - advantage.mean(dim=1, keepdim=True))
        
        return q
    
    def act(self, state, epsilon=0.0):
        """
        选择动作
        
        Args:
            state: 游戏状态
            epsilon: epsilon-贪婪策略的探索率
            
        Returns:
            选择的动作
        """
        if np.random.random() > epsilon:
            with torch.no_grad():
                state = torch.FloatTensor(state).unsqueeze(0)
                q_values = self.forward(state)
                return q_values.max(1)[1].item()
        else:
            return np.random.randint(4)  # 随机选择动作
            
class ReplayBuffer:
    """经验回放缓冲区"""
    
    def __init__(self, capacity):
        """
        初始化缓冲区
        
        Args:
            capacity (int): 缓冲区容量
        """
        self.capacity = capacity
        self.buffer = []
        self.position = 0
        
    def push(self, state, action, reward, next_state, done):
        """存储转换"""
        if len(self.buffer) < self.capacity:
            self.buffer.append(None)
        self.buffer[self.position] = (state, action, reward, next_state, done)
        self.position = (self.position + 1) % self.capacity
        
    def sample(self, batch_size):
        """采样小批量转换"""
        batch = np.random.choice(len(self.buffer), batch_size, replace=False)
        states, actions, rewards, next_states, dones = zip(*[self.buffer[i] for i in batch])
        return (
            torch.FloatTensor(np.array(states)),
            torch.LongTensor(actions),
            torch.FloatTensor(rewards),
            torch.FloatTensor(np.array(next_states)),
            torch.FloatTensor(dones)
        )
        
    def __len__(self):
        """返回当前大小"""
        return len(self.buffer) 
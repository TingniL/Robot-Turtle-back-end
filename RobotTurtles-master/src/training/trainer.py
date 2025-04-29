import os
import torch
import torch.nn.functional as F
from torch.utils.tensorboard import SummaryWriter
from tqdm import tqdm
import numpy as np

class DQNTrainer:
    """DQN训练器"""
    
    def __init__(
        self,
        env,
        model,
        target_model,
        buffer,
        device,
        learning_rate=1e-4,
        gamma=0.99,
        batch_size=32,
        target_update=1000,
        log_dir='runs'
    ):
        """
        初始化训练器
        
        Args:
            env: 游戏环境
            model: DQN模型
            target_model: 目标网络
            buffer: 经验回放缓冲区
            device: 训练设备（CPU/GPU）
            learning_rate: 学习率
            gamma: 折扣因子
            batch_size: 批量大小
            target_update: 目标网络更新频率
            log_dir: TensorBoard日志目录
        """
        self.env = env
        self.model = model
        self.target_model = target_model
        self.buffer = buffer
        self.device = device
        self.batch_size = batch_size
        self.gamma = gamma
        self.target_update = target_update
        
        self.optimizer = torch.optim.Adam(self.model.parameters(), lr=learning_rate)
        self.writer = SummaryWriter(log_dir)
        
        # 将模型移动到指定设备
        self.model = self.model.to(device)
        self.target_model = self.target_model.to(device)
        
        # 复制模型参数到目标网络
        self.target_model.load_state_dict(self.model.state_dict())
        
    def train_step(self, total_steps):
        """执行一步训练"""
        if len(self.buffer) < self.batch_size:
            return 0.0
            
        # 从缓冲区采样
        states, actions, rewards, next_states, dones = self.buffer.sample(self.batch_size)
        
        # 移动数据到设备
        states = states.to(self.device)
        actions = actions.to(self.device)
        rewards = rewards.to(self.device)
        next_states = next_states.to(self.device)
        dones = dones.to(self.device)
        
        # 计算当前Q值
        current_q = self.model(states).gather(1, actions.unsqueeze(1))
        
        # 计算目标Q值
        with torch.no_grad():
            next_q = self.target_model(next_states).max(1)[0]
            target_q = rewards + (1 - dones) * self.gamma * next_q
            
        # 计算损失
        loss = F.smooth_l1_loss(current_q.squeeze(), target_q)
        
        # 优化
        self.optimizer.zero_grad()
        loss.backward()
        torch.nn.utils.clip_grad_norm_(self.model.parameters(), 1.0)
        self.optimizer.step()
        
        # 更新目标网络
        if total_steps % self.target_update == 0:
            self.target_model.load_state_dict(self.model.state_dict())
            
        return loss.item()
        
    def train(self, n_episodes, max_steps=1000):
        """
        训练模型
        
        Args:
            n_episodes: 训练轮数
            max_steps: 每轮最大步数
        """
        total_steps = 0
        
        for episode in tqdm(range(n_episodes)):
            state = self.env.reset()
            episode_reward = 0
            episode_loss = 0
            
            for step in range(max_steps):
                # epsilon随训练进度衰减
                epsilon = max(0.01, 1.0 - total_steps / 50000)
                
                # 选择动作
                action = self.model.act(state, epsilon)
                
                # 执行动作
                next_state, reward, done, _ = self.env.step(action)
                
                # 存储转换
                self.buffer.push(state, action, reward, next_state, done)
                
                # 训练
                loss = self.train_step(total_steps)
                episode_loss += loss
                episode_reward += reward
                
                if done:
                    break
                    
                state = next_state
                total_steps += 1
                
            # 记录训练信息
            self.writer.add_scalar('Train/Episode_Reward', episode_reward, episode)
            self.writer.add_scalar('Train/Episode_Loss', episode_loss, episode)
            self.writer.add_scalar('Train/Epsilon', epsilon, episode)
            
            # 每100轮保存一次模型
            if (episode + 1) % 100 == 0:
                self.save_model(f'model_episode_{episode+1}.pth')
                
    def save_model(self, path):
        """保存模型"""
        os.makedirs(os.path.dirname(path), exist_ok=True)
        torch.save({
            'model_state_dict': self.model.state_dict(),
            'optimizer_state_dict': self.optimizer.state_dict(),
        }, path)
        
    def load_model(self, path):
        """加载模型"""
        checkpoint = torch.load(path)
        self.model.load_state_dict(checkpoint['model_state_dict'])
        self.optimizer.load_state_dict(checkpoint['optimizer_state_dict'])
        self.target_model.load_state_dict(self.model.state_dict()) 
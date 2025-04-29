import torch
from environment.game_env import RobotTurtlesEnv
from models.dqn import DQN, ReplayBuffer
from training.trainer import DQNTrainer

def main():
    # 设置设备
    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    print(f"Using device: {device}")
    
    # 创建环境
    env = RobotTurtlesEnv(board_size=8)
    
    # 创建模型
    model = DQN(board_size=8, n_actions=4)
    target_model = DQN(board_size=8, n_actions=4)
    
    # 创建经验回放缓冲区
    buffer = ReplayBuffer(capacity=100000)
    
    # 创建训练器
    trainer = DQNTrainer(
        env=env,
        model=model,
        target_model=target_model,
        buffer=buffer,
        device=device,
        learning_rate=1e-4,
        gamma=0.99,
        batch_size=32,
        target_update=1000,
        log_dir='runs'
    )
    
    # 开始训练
    n_episodes = 10000
    trainer.train(n_episodes=n_episodes)
    
    # 保存最终模型
    trainer.save_model('final_model.pth')
    
if __name__ == "__main__":
    main() 
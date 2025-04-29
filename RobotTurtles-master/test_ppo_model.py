#!/usr/bin/env python
# -*- coding: utf-8 -*-

import os
import sys
import argparse
import numpy as np
import torch
from ppo_adapter import PPOAdapter, test_adapter

def main():
    parser = argparse.ArgumentParser(description="Test PPO model in Robot Turtles game")
    parser.add_argument("--model", type=str, required=True, help="Path to the trained model")
    parser.add_argument("--episodes", type=int, default=5, help="Number of episodes to test")
    
    args = parser.parse_args()
    
    # 检查模型文件是否存在
    if not os.path.exists(args.model):
        print(f"Error: Model file not found: {args.model}")
        return
    
    print(f"开始测试PPO模型: {args.model}")
    print(f"测试回合数: {args.episodes}")
    
    try:
        # 运行测试函数
        win_rate, avg_reward = test_adapter(args.model, args.episodes)
        
        # 输出结果
        print("\n=====================")
        print("测试完成!")
        print(f"胜率: {win_rate:.2f}%")
        print(f"平均奖励: {avg_reward:.2f}")
        print("=====================")
        
    except Exception as e:
        print(f"测试过程中发生错误: {str(e)}")
        import traceback
        traceback.print_exc()

if __name__ == "__main__":
    main() 
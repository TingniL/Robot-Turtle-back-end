import torch
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
from torch import nn
from tensorboard.writer import SummaryWriter

def test_environment():
    print("Python环境测试:")
    print("-" * 50)
    
    # 测试PyTorch
    print("1. 测试PyTorch:")
    x = torch.rand(5, 3)
    print(f"PyTorch tensor shape: {x.shape}")
    print(f"PyTorch version: {torch.__version__}")
    print("PyTorch测试通过!\n")
    
    # 测试NumPy
    print("2. 测试NumPy:")
    arr = np.random.rand(3, 3)
    print(f"NumPy array shape: {arr.shape}")
    print(f"NumPy version: {np.__version__}")
    print("NumPy测试通过!\n")
    
    # 测试Pandas
    print("3. 测试Pandas:")
    df = pd.DataFrame({'A': [1, 2, 3], 'B': [4, 5, 6]})
    print("Pandas DataFrame:")
    print(df.head())
    print(f"Pandas version: {pd.__version__}")
    print("Pandas测试通过!\n")
    
    # 测试Matplotlib
    print("4. 测试Matplotlib:")
    plt.figure(figsize=(3, 3))
    plt.plot([1, 2, 3], [1, 2, 3])
    plt.title("测试图")
    plt.close()
    print(f"Matplotlib version: {plt.__version__}")
    print("Matplotlib测试通过!\n")
    
    # 测试TensorBoard
    print("5. 测试TensorBoard:")
    writer = SummaryWriter('test_logs')
    writer.add_scalar('test/scalar', 1.0, 0)
    writer.close()
    print("TensorBoard测试通过!")
    print("-" * 50)
    print("所有测试完成，环境设置正确！")

if __name__ == "__main__":
    test_environment() 
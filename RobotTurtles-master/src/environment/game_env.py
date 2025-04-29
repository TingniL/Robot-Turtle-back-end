import numpy as np

class RobotTurtlesEnv:
    """Robot Turtles游戏环境"""
    
    def __init__(self, board_size=8):
        """
        初始化游戏环境
        
        Args:
            board_size (int): 棋盘大小
        """
        self.board_size = board_size
        self.board = None
        self.current_player = 0
        self.turtle_positions = []
        self.jewel_positions = []
        self.directions = [(0, 1), (1, 0), (0, -1), (-1, 0)]  # 东南西北
        
    def reset(self):
        """重置游戏环境到初始状态"""
        self.board = np.zeros((self.board_size, self.board_size), dtype=np.int8)
        self._place_turtles()
        self._place_jewels()
        self._place_obstacles()
        return self._get_observation()
    
    def step(self, action):
        """
        执行一步动作
        
        Args:
            action (int): 动作ID（0:前进, 1:左转, 2:右转, 3:激光）
            
        Returns:
            tuple: (observation, reward, done, info)
        """
        reward = 0
        done = False
        
        # 执行动作
        valid_move = self._execute_action(action)
        if not valid_move:
            reward = -1
        
        # 检查是否到达目标
        if self._check_win():
            reward = 100
            done = True
        
        return self._get_observation(), reward, done, {}
    
    def _get_observation(self):
        """获取当前状态观察"""
        return self.board.copy()
    
    def _place_turtles(self):
        """放置海龟初始位置"""
        # 在底部边缘随机放置海龟
        positions = np.random.choice(self.board_size, 2, replace=False)
        for pos in positions:
            self.turtle_positions.append((self.board_size-1, pos))
            self.board[self.board_size-1, pos] = 1
    
    def _place_jewels(self):
        """放置宝石"""
        # 在顶部边缘随机放置宝石
        positions = np.random.choice(self.board_size, 2, replace=False)
        for pos in positions:
            self.jewel_positions.append((0, pos))
            self.board[0, pos] = 2
    
    def _place_obstacles(self):
        """放置障碍物"""
        # 随机放置墙和冰块
        n_obstacles = self.board_size * 2
        for _ in range(n_obstacles):
            while True:
                x = np.random.randint(1, self.board_size-1)
                y = np.random.randint(1, self.board_size-1)
                if self.board[x, y] == 0:
                    self.board[x, y] = 3 if np.random.random() < 0.5 else 4
                    break
    
    def _execute_action(self, action):
        """
        执行动作
        
        Args:
            action (int): 动作ID
            
        Returns:
            bool: 动作是否有效
        """
        if action not in range(4):
            return False
            
        turtle_pos = self.turtle_positions[self.current_player]
        
        if action == 0:  # 前进
            new_pos = (
                turtle_pos[0] + self.directions[self.current_player][0],
                turtle_pos[1] + self.directions[self.current_player][1]
            )
            if self._is_valid_position(new_pos):
                self.board[turtle_pos] = 0
                self.board[new_pos] = 1
                self.turtle_positions[self.current_player] = new_pos
                return True
        elif action in [1, 2]:  # 转向
            self.current_player = (self.current_player + (1 if action == 1 else -1)) % 4
            return True
        elif action == 3:  # 激光
            return self._fire_laser()
            
        return False
    
    def _is_valid_position(self, pos):
        """检查位置是否有效"""
        x, y = pos
        if not (0 <= x < self.board_size and 0 <= y < self.board_size):
            return False
        return self.board[x, y] in [0, 2]  # 空位置或宝石
    
    def _fire_laser(self):
        """发射激光"""
        turtle_pos = self.turtle_positions[self.current_player]
        direction = self.directions[self.current_player]
        
        current_pos = (
            turtle_pos[0] + direction[0],
            turtle_pos[1] + direction[1]
        )
        
        while 0 <= current_pos[0] < self.board_size and 0 <= current_pos[1] < self.board_size:
            if self.board[current_pos] == 4:  # 冰块
                self.board[current_pos] = 0
                return True
            elif self.board[current_pos] in [1, 2, 3]:  # 海龟、宝石或墙
                return False
            current_pos = (
                current_pos[0] + direction[0],
                current_pos[1] + direction[1]
            )
        return False
    
    def _check_win(self):
        """检查是否获胜"""
        turtle_pos = self.turtle_positions[self.current_player]
        return turtle_pos in self.jewel_positions
    
    def render(self):
        """渲染当前游戏状态"""
        symbols = ['.', 'T', 'J', 'W', 'I']  # 空、海龟、宝石、墙、冰
        for row in self.board:
            print(' '.join(symbols[int(cell)] for cell in row))
        print(f"当前玩家: {self.current_player}")
        print(f"方向: {['东', '南', '西', '北'][self.current_player]}") 
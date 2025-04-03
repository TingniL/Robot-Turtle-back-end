import axios from 'axios';

// API基础URL，实际项目中会替换为真实API地址
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:5000/api';

// 创建axios实例
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 游戏状态接口
export interface GameStateResponse {
  id: string;
  board: number[];
  turtlePos: [number, number];
  turtleDirection: number;
  jewelPos: [number, number];
  walls: [number, number][];
  status: 'active' | 'completed' | 'failed';
  moves: number;
}

// AI预测接口
export interface AIPredictionResponse {
  action_probabilities: number[];
  recommended_action: string;
}

// 游戏API服务
const gameService = {
  // 获取新游戏
  newGame: async (): Promise<GameStateResponse> => {
    try {
      const response = await api.post('/games/new');
      return response.data;
    } catch (error) {
      console.error('Error creating new game:', error);
      throw error;
    }
  },

  // 执行游戏动作
  performAction: async (gameId: string, action: string): Promise<GameStateResponse> => {
    try {
      const response = await api.post(`/games/${gameId}/action`, { action });
      return response.data;
    } catch (error) {
      console.error('Error performing action:', error);
      throw error;
    }
  },

  // 获取AI预测
  getAIPrediction: async (
    gameId: string, 
    board: number[], 
    turtlePos: [number, number], 
    turtleDirection: number
  ): Promise<AIPredictionResponse> => {
    try {
      const response = await api.post(`/ai/predict`, {
        game_id: gameId,
        board,
        turtle_pos: turtlePos,
        turtle_direction: turtleDirection
      });
      return response.data;
    } catch (error) {
      console.error('Error getting AI prediction:', error);
      // 返回默认预测，避免UI崩溃
      return {
        action_probabilities: [0.25, 0.25, 0.25, 0.25],
        recommended_action: 'FORWARD'
      };
    }
  },
  
  // 获取游戏历史
  getGameHistory: async (): Promise<GameStateResponse[]> => {
    try {
      const response = await api.get('/games/history');
      return response.data;
    } catch (error) {
      console.error('Error fetching game history:', error);
      return [];
    }
  },
};

export default gameService; 
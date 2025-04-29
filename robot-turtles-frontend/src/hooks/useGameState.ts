import { useState, useCallback } from 'react';
import gameService, { GameStateResponse } from '../services/api';

export interface GameState {
  board: number[];
  turtlePos: [number, number] | [number, number][];
  turtleDirection: number | string;
  jewelPos: [number, number];
  walls: [number, number][];
  status: 'active' | 'completed' | 'failed';
  moves: number;
  currentPlayer?: number;
  gameOver: boolean;
  win: boolean;
}

export type ActionType = 'FORWARD' | 'LEFT' | 'RIGHT' | 'LASER';

const initialState: GameState = {
  board: Array(64).fill(0),
  turtlePos: [0, 0],
  turtleDirection: 1, // 右方向，对应数字1
  jewelPos: [7, 7],
  walls: [],
  status: 'active',
  moves: 0,
  gameOver: false,
  win: false
};

export function useGameState() {
  const [gameState, setGameState] = useState<GameState>(initialState);
  const [gameId, setGameId] = useState<string | null>(null);

  // 创建新游戏
  const createNewGame = useCallback(async (playerCount: number = 1): Promise<void> => {
    try {
      // 尝试使用API创建游戏
      const newGameState = await gameService.newGame(playerCount);
      setGameId(newGameState.id);
      
      // 将API返回的游戏状态映射到本地状态
      const directionMap: Record<string, number> = {
        'EAST': 1,
        'SOUTH': 2,
        'WEST': 3,
        'NORTH': 0
      };
      
      setGameState({
        board: newGameState.board,
        turtlePos: newGameState.turtlePos,
        turtleDirection: typeof newGameState.turtleDirection === 'string' 
          ? directionMap[newGameState.turtleDirection] || 1
          : newGameState.turtleDirection,
        jewelPos: newGameState.jewelPos,
        walls: newGameState.walls,
        status: newGameState.status,
        moves: newGameState.moves,
        currentPlayer: newGameState.currentPlayer ?? 0,
        gameOver: newGameState.status !== 'active',
        win: newGameState.status === 'completed'
      });
    } catch (error) {
      console.error('Failed to create game via API, using local state', error);
      // 如果API失败，使用本地初始化状态
      const localState = { ...initialState };
      
      // 为多玩家游戏初始化多个乌龟位置
      if (playerCount > 1) {
        const turtlePositions: [number, number][] = [];
        
        // 为每个玩家分配初始位置（按照角落位置分配）
        switch(playerCount) {
          case 2:
            turtlePositions.push([0, 0]); // 左上角
            turtlePositions.push([7, 7]); // 右下角
            break;
          case 3:
            turtlePositions.push([0, 0]); // 左上角
            turtlePositions.push([7, 0]); // 右上角
            turtlePositions.push([3, 7]); // 中下方
            break;
          case 4:
            turtlePositions.push([0, 0]); // 左上角
            turtlePositions.push([7, 0]); // 右上角
            turtlePositions.push([0, 7]); // 左下角
            turtlePositions.push([7, 7]); // 右下角
            break;
          default:
            // 单人游戏或其他情况
            turtlePositions.push([0, 0]);
        }
        
        localState.turtlePos = turtlePositions;
        localState.currentPlayer = 0;
        
        // 根据玩家数量调整宝石位置
        if (playerCount >= 2) {
          localState.jewelPos = [3, 3]; // 放在中间位置
        }
      }
      
      setGameState(localState);
      setGameId(null);
    }
  }, []);

  // 执行游戏动作
  const performAction = useCallback(async (action: ActionType): Promise<void> => {
    // 如果游戏已完成，则不执行任何操作
    if (gameState.status !== 'active' || gameState.gameOver) {
      return;
    }

    try {
      if (gameId) {
        // 如果有游戏ID，使用API执行动作
        const updatedState = await gameService.performAction(gameId, action);
        
        // 将API返回的游戏状态映射到本地状态
        const directionMap: Record<string, number> = {
          'EAST': 1,
          'SOUTH': 2,
          'WEST': 3,
          'NORTH': 0
        };
        
        setGameState({
          board: updatedState.board,
          turtlePos: updatedState.turtlePos,
          turtleDirection: typeof updatedState.turtleDirection === 'string' 
            ? directionMap[updatedState.turtleDirection] || 1
            : updatedState.turtleDirection,
          jewelPos: updatedState.jewelPos,
          walls: updatedState.walls,
          status: updatedState.status,
          moves: updatedState.moves,
          currentPlayer: updatedState.currentPlayer ?? 0,
          gameOver: updatedState.status !== 'active',
          win: updatedState.status === 'completed'
        });
        
        return;
      }
      
      // 本地状态管理（当API不可用时）
      let newState = { ...gameState };
      newState.moves += 1;
      
      // 处理多人游戏或单人游戏
      const isMultiplayer = Array.isArray(gameState.turtlePos) && gameState.turtlePos.length > 1;
      
      if (isMultiplayer) {
        // 多人游戏逻辑
        const playerTurtlePositions = [...gameState.turtlePos] as [number, number][];
        const currentPlayer = gameState.currentPlayer || 0;
        const currentPos = playerTurtlePositions[currentPlayer];
        
        // 根据动作更新当前玩家的乌龟位置和方向
        let newPos: [number, number] = [...currentPos] as [number, number];
        let newDirection = gameState.turtleDirection;
        
        if (action === 'FORWARD') {
          // 向前移动逻辑
          if (newDirection === 'EAST' || newDirection === 1) newPos[0] += 1;
          else if (newDirection === 'WEST' || newDirection === 3) newPos[0] -= 1;
          else if (newDirection === 'SOUTH' || newDirection === 2) newPos[1] += 1;
          else if (newDirection === 'NORTH' || newDirection === 0) newPos[1] -= 1;
        } else if (action === 'LEFT') {
          // 左转
          if (typeof newDirection === 'string') {
            if (newDirection === 'EAST') newDirection = 0; // NORTH
            else if (newDirection === 'NORTH') newDirection = 3; // WEST
            else if (newDirection === 'WEST') newDirection = 2; // SOUTH
            else if (newDirection === 'SOUTH') newDirection = 1; // EAST
          } else {
            newDirection = (newDirection + 3) % 4;
          }
        } else if (action === 'RIGHT') {
          // 右转
          if (typeof newDirection === 'string') {
            if (newDirection === 'EAST') newDirection = 2; // SOUTH
            else if (newDirection === 'SOUTH') newDirection = 3; // WEST
            else if (newDirection === 'WEST') newDirection = 0; // NORTH
            else if (newDirection === 'NORTH') newDirection = 1; // EAST
          } else {
            newDirection = (newDirection + 1) % 4;
          }
        }
        
        // 更新玩家位置
        playerTurtlePositions[currentPlayer] = newPos;
        
        // 检查游戏是否完成
        const targetPos = gameState.jewelPos;
        if (newPos[0] === targetPos[0] && newPos[1] === targetPos[1]) {
          newState.status = 'completed';
          newState.gameOver = true;
          newState.win = true;
        }
        
        // 更新状态
        newState.turtlePos = playerTurtlePositions;
        newState.turtleDirection = newDirection;
        
        // 切换到下一个玩家
        newState.currentPlayer = (currentPlayer + 1) % playerTurtlePositions.length;
      } else {
        // 单人游戏逻辑
        let turtlePos = Array.isArray(gameState.turtlePos) 
          ? [...gameState.turtlePos[0]] as [number, number]
          : [...gameState.turtlePos] as [number, number];
          
        let turtleDirection = gameState.turtleDirection;
        
        if (action === 'FORWARD') {
          // 向前移动逻辑
          if (turtleDirection === 'EAST' || turtleDirection === 1) turtlePos[0] += 1;
          else if (turtleDirection === 'WEST' || turtleDirection === 3) turtlePos[0] -= 1;
          else if (turtleDirection === 'SOUTH' || turtleDirection === 2) turtlePos[1] += 1;
          else if (turtleDirection === 'NORTH' || turtleDirection === 0) turtlePos[1] -= 1;
        } else if (action === 'LEFT') {
          // 左转
          if (typeof turtleDirection === 'string') {
            if (turtleDirection === 'EAST') turtleDirection = 0; // NORTH
            else if (turtleDirection === 'NORTH') turtleDirection = 3; // WEST
            else if (turtleDirection === 'WEST') turtleDirection = 2; // SOUTH
            else if (turtleDirection === 'SOUTH') turtleDirection = 1; // EAST
          } else {
            turtleDirection = (turtleDirection + 3) % 4;
          }
        } else if (action === 'RIGHT') {
          // 右转
          if (typeof turtleDirection === 'string') {
            if (turtleDirection === 'EAST') turtleDirection = 2; // SOUTH
            else if (turtleDirection === 'SOUTH') turtleDirection = 3; // WEST
            else if (turtleDirection === 'WEST') turtleDirection = 0; // NORTH
            else if (turtleDirection === 'NORTH') turtleDirection = 1; // EAST
          } else {
            turtleDirection = (turtleDirection + 1) % 4;
          }
        }
        
        // 检查游戏是否完成
        const targetPos = gameState.jewelPos;
        if (turtlePos[0] === targetPos[0] && turtlePos[1] === targetPos[1]) {
          newState.status = 'completed';
          newState.gameOver = true;
          newState.win = true;
        }
        
        // 更新状态
        newState.turtlePos = turtlePos;
        newState.turtleDirection = turtleDirection;
      }
      
      setGameState(newState);
    } catch (error) {
      console.error('Error performing action:', error);
    }
  }, [gameId, gameState]);

  // 重置游戏
  const resetGame = useCallback(() => {
    setGameState(initialState);
    setGameId(null);
  }, []);

  return {
    gameState,
    createNewGame,
    performAction,
    resetGame
  };
} 
import { useState, useEffect } from 'react';

interface GameState {
  board: number[];
  turtlePos: [number, number];
  turtleDirection: number;
  jewelPos: [number, number];
  walls: [number, number][];
  gameOver: boolean;
  win: boolean;
  moves: number;
}

const initialState: GameState = {
  board: Array(64).fill(0),
  turtlePos: [7, 3], // 起始位置在底部中间
  turtleDirection: 0, // 0: 上, 1: 右, 2: 下, 3: 左
  jewelPos: [0, 3], // 宝石位置在顶部中间
  walls: [
    [2, 1], [2, 2], [2, 5], [2, 6],
    [4, 1], [4, 2], [4, 5], [4, 6]
  ],
  gameOver: false,
  win: false,
  moves: 0
};

export const useGameState = () => {
  const [gameState, setGameState] = useState<GameState>(initialState);
  
  const resetGame = () => {
    setGameState(initialState);
  };
  
  const performAction = (action: string) => {
    setGameState(prevState => {
      // 复制当前状态
      const newState = { ...prevState };
      newState.moves += 1;
      
      const [row, col] = prevState.turtlePos;
      let newRow = row;
      let newCol = col;
      let newDirection = prevState.turtleDirection;
      
      // 执行动作
      switch (action) {
        case 'FORWARD':
          // 根据朝向移动
          if (newDirection === 0) newRow -= 1; // 上
          else if (newDirection === 1) newCol += 1; // 右
          else if (newDirection === 2) newRow += 1; // 下
          else if (newDirection === 3) newCol -= 1; // 左
          break;
          
        case 'LEFT':
          // 向左转向 (减少方向值，循环回到3)
          newDirection = (newDirection + 3) % 4;
          break;
          
        case 'RIGHT':
          // 向右转向 (增加方向值，循环回到0)
          newDirection = (newDirection + 1) % 4;
          break;
          
        case 'LASER':
          // 发射激光，移除前方的墙
          let laserRow = row;
          let laserCol = col;
          
          // 确定激光方向
          if (newDirection === 0) { // 上
            for (let r = row - 1; r >= 0; r--) {
              const wallIndex = prevState.walls.findIndex(([wr, wc]) => wr === r && wc === col);
              if (wallIndex !== -1) {
                // 移除墙
                const newWalls = [...prevState.walls];
                newWalls.splice(wallIndex, 1);
                newState.walls = newWalls;
                break;
              }
            }
          } else if (newDirection === 1) { // 右
            for (let c = col + 1; c < 8; c++) {
              const wallIndex = prevState.walls.findIndex(([wr, wc]) => wr === row && wc === c);
              if (wallIndex !== -1) {
                const newWalls = [...prevState.walls];
                newWalls.splice(wallIndex, 1);
                newState.walls = newWalls;
                break;
              }
            }
          } else if (newDirection === 2) { // 下
            for (let r = row + 1; r < 8; r++) {
              const wallIndex = prevState.walls.findIndex(([wr, wc]) => wr === r && wc === col);
              if (wallIndex !== -1) {
                const newWalls = [...prevState.walls];
                newWalls.splice(wallIndex, 1);
                newState.walls = newWalls;
                break;
              }
            }
          } else if (newDirection === 3) { // 左
            for (let c = col - 1; c >= 0; c--) {
              const wallIndex = prevState.walls.findIndex(([wr, wc]) => wr === row && wc === c);
              if (wallIndex !== -1) {
                const newWalls = [...prevState.walls];
                newWalls.splice(wallIndex, 1);
                newState.walls = newWalls;
                break;
              }
            }
          }
          break;
      }
      
      // 检查新位置是否有效
      if (action === 'FORWARD') {
        // 检查是否出界
        if (newRow < 0 || newRow >= 8 || newCol < 0 || newCol >= 8) {
          // 留在原地
          newRow = row;
          newCol = col;
        } else {
          // 检查是否撞墙
          const hitWall = prevState.walls.some(([wr, wc]) => wr === newRow && wc === newCol);
          if (hitWall) {
            // 留在原地
            newRow = row;
            newCol = col;
          }
        }
        
        // 更新位置
        newState.turtlePos = [newRow, newCol];
      }
      
      // 更新方向
      newState.turtleDirection = newDirection;
      
      // 检查是否到达宝石
      if (newState.turtlePos[0] === prevState.jewelPos[0] && newState.turtlePos[1] === prevState.jewelPos[1]) {
        newState.gameOver = true;
        newState.win = true;
      }
      
      // 检查步数限制
      if (newState.moves >= 50 && !newState.win) {
        newState.gameOver = true;
      }
      
      return newState;
    });
  };
  
  return {
    gameState,
    resetGame,
    performAction
  };
}; 
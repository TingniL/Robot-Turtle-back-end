import React, { useState, useEffect } from 'react';
import Cell from './Cell';
import { mockAIDecision } from '../services/mockAI';
import './Board.css';

interface GameState {
  board: number[];
  turtlePos: [number, number] | [number, number][];
  turtleDirection: number | string;
  jewelPos: [number, number];
  walls: [number, number][];
  gameOver: boolean;
  win: boolean;
  moves: number;
  currentPlayer?: number;
  status: 'active' | 'completed' | 'failed';
}

interface BoardProps {
  onAction: (action: string) => void;
  aiMode: boolean;
  gameState: GameState;
}

const Board: React.FC<BoardProps> = ({ onAction, aiMode, gameState }) => {
  const { board, turtlePos, turtleDirection, jewelPos, walls } = gameState;
  
  const [actionProbabilities, setActionProbabilities] = useState<number[]>([0.25, 0.25, 0.25, 0.25]);
  
  // 确保turtleDirection是数字类型
  const getNumericDirection = (direction: number | string): number => {
    if (typeof direction === 'number') {
      return direction;
    }
    
    // 将字符串方向转换为数字
    const directionMap: Record<string, number> = {
      'NORTH': 0,
      'EAST': 1,
      'SOUTH': 2,
      'WEST': 3
    };
    
    return directionMap[direction] || 1; // 默认为右向
  };
  
  // 确保turtlePos是单一位置而非数组
  const getSingleTurtlePos = (): [number, number] => {
    if (Array.isArray(turtlePos[0])) {
      // 如果是多玩家游戏，使用当前玩家的位置
      const currentPlayer = gameState.currentPlayer || 0;
      return (turtlePos as [number, number][])[currentPlayer];
    }
    return turtlePos as [number, number];
  };
  
  // For AI mode, fetch action probabilities
  useEffect(() => {
    if (aiMode) {
      // Use mock AI decision instead of actual API call
      const { probabilities } = mockAIDecision(
        board,
        getSingleTurtlePos(),
        getNumericDirection(turtleDirection),
        jewelPos,
        walls
      );
      setActionProbabilities(probabilities);
    }
  }, [aiMode, board, turtlePos, turtleDirection, jewelPos, walls, gameState.currentPlayer]);
  
  const renderBoard = () => {
    const cells = [];
    const singleTurtlePos = getSingleTurtlePos();
    
    for (let row = 0; row < 8; row++) {
      for (let col = 0; col < 8; col++) {
        const isTurtle = singleTurtlePos[0] === row && singleTurtlePos[1] === col;
        const isJewel = jewelPos[0] === row && jewelPos[1] === col;
        const isWall = walls.some(([r, c]) => r === row && c === col);
        
        cells.push(
          <Cell
            key={`${row}-${col}`}
            row={row}
            col={col}
            isTurtle={isTurtle}
            isJewel={isJewel}
            isWall={isWall}
            turtleDirection={isTurtle ? getNumericDirection(turtleDirection) : null}
          />
        );
      }
    }
    return cells;
  };
  
  const actionNames = ["Forward", "Left", "Right", "Laser"];
  const actionKeys = ["FORWARD", "LEFT", "RIGHT", "LASER"];
  
  return (
    <div className="game-board">
      <div className="grid-container">
        {renderBoard()}
      </div>
      
      <div className="controls">
        {actionNames.map((name, idx) => (
          <button 
            key={name} 
            onClick={() => onAction(actionKeys[idx])}
            className={aiMode && idx === actionProbabilities.indexOf(Math.max(...actionProbabilities)) 
              ? 'recommended' 
              : ''}
          >
            {name}
            {aiMode && <div className="probability">{Math.round(actionProbabilities[idx] * 100)}%</div>}
          </button>
        ))}
      </div>
      
      {aiMode && (
        <div className="ai-info">
          <h3>AI Recommendation</h3>
          <p>AI suggests: {actionNames[actionProbabilities.indexOf(Math.max(...actionProbabilities))]}</p>
          <button onClick={() => onAction(actionKeys[actionProbabilities.indexOf(Math.max(...actionProbabilities))])}>
            Follow AI
          </button>
        </div>
      )}
    </div>
  );
};

export default Board; 
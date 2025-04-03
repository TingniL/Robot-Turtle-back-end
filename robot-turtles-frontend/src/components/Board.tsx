import React, { useState, useEffect } from 'react';
import Cell from './Cell';
import { useGameState } from '../hooks/useGameState';
import { mockAIDecision } from '../services/mockAI';
import './Board.css';

interface BoardProps {
  onAction: (action: string) => void;
  aiMode: boolean;
}

const Board: React.FC<BoardProps> = ({ onAction, aiMode }) => {
  const { gameState } = useGameState();
  const { board, turtlePos, turtleDirection, jewelPos, walls } = gameState;
  
  const [actionProbabilities, setActionProbabilities] = useState<number[]>([0.25, 0.25, 0.25, 0.25]);
  
  // For AI mode, fetch action probabilities
  useEffect(() => {
    if (aiMode) {
      // 使用模拟AI决策而非实际API调用
      const { probabilities } = mockAIDecision(
        board,
        turtlePos,
        turtleDirection,
        jewelPos,
        walls
      );
      setActionProbabilities(probabilities);
    }
  }, [aiMode, board, turtlePos, turtleDirection, jewelPos, walls]);
  
  const renderBoard = () => {
    const cells = [];
    for (let row = 0; row < 8; row++) {
      for (let col = 0; col < 8; col++) {
        const isTurtle = turtlePos[0] === row && turtlePos[1] === col;
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
            turtleDirection={isTurtle ? turtleDirection : null}
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
          <h3>AI推荐 / AI Recommendation</h3>
          <p>AI建议: {actionNames[actionProbabilities.indexOf(Math.max(...actionProbabilities))]}</p>
          <button onClick={() => onAction(actionKeys[actionProbabilities.indexOf(Math.max(...actionProbabilities))])}>
            执行AI建议 / Follow AI
          </button>
        </div>
      )}
    </div>
  );
};

export default Board; 
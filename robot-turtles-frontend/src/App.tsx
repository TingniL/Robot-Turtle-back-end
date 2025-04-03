import React, { useState } from 'react';
import Board from './components/Board';
import { useGameState } from './hooks/useGameState';
import './App.css';

const App: React.FC = () => {
  const { gameState, resetGame, performAction } = useGameState();
  const [aiMode, setAIMode] = useState(false);
  const [showInstructions, setShowInstructions] = useState(false);
  
  const handleAction = (action: string) => {
    if (gameState.gameOver) return;
    performAction(action);
  };
  
  return (
    <div className="app">
      <header>
        <h1>机器人龟棋 / Robot Turtles</h1>
        <div className="mode-selector">
          <button 
            className={aiMode ? 'active' : ''} 
            onClick={() => setAIMode(true)}
          >
            AI 模式
          </button>
          <button 
            className={!aiMode ? 'active' : ''} 
            onClick={() => setAIMode(false)}
          >
            玩家模式
          </button>
        </div>
      </header>
      
      <main>
        <div className="game-container">
          <div className="game-info">
            <p>移动次数: {gameState.moves}</p>
            {gameState.gameOver && (
              <div className="game-over">
                <h2>{gameState.win ? '恭喜，你赢了！' : '游戏结束'}</h2>
                <button onClick={resetGame}>重新开始</button>
              </div>
            )}
          </div>
          
          <Board onAction={handleAction} aiMode={aiMode} />
          
          <div className="instructions-toggle">
            <button onClick={() => setShowInstructions(!showInstructions)}>
              {showInstructions ? '隐藏说明' : '显示说明'}
            </button>
          </div>
          
          {showInstructions && (
            <div className="instructions">
              <h3>游戏说明 / Game Instructions</h3>
              <p>控制你的机器人龟，通过使用以下命令到达宝石：</p>
              <ul>
                <li><strong>前进 (Forward)</strong>: 向当前方向移动一格</li>
                <li><strong>左转 (Left)</strong>: 向左转向90度</li>
                <li><strong>右转 (Right)</strong>: 向右转向90度</li>
                <li><strong>激光 (Laser)</strong>: 发射激光摧毁前方的墙</li>
              </ul>
              <p>AI模式下，系统会推荐下一步最佳行动。</p>
              <p>步数限制：50步</p>
            </div>
          )}
        </div>
      </main>
      
      <footer>
        <p>基于强化学习的机器人龟棋游戏 - Robot Turtles Game with Reinforcement Learning</p>
      </footer>
    </div>
  );
};

export default App; 
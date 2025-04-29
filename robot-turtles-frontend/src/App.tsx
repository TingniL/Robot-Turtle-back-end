import React, { useState } from 'react';
import Board from './components/Board';
import MainMenu from './components/MainMenu';
import { useGameState } from './hooks/useGameState';
import './App.css';

const App: React.FC = () => {
  const { gameState, createNewGame, performAction, resetGame } = useGameState();
  const [aiMode, setAIMode] = useState(false);
  const [showInstructions, setShowInstructions] = useState(false);
  const [gameStarted, setGameStarted] = useState(false);
  
  const handleAction = (action: string) => {
    if (gameState.gameOver || gameState.status !== 'active') return;
    performAction(action);
  };
  
  const handleStartGame = (playerCount: number) => {
    createNewGame(playerCount);
    setGameStarted(true);
  };
  
  const handleResetGame = () => {
    resetGame();
    setGameStarted(false);
  };

  const handleNewGame = () => {
    // 返回到主菜单让用户重新选择人数
    setGameStarted(false);
  };
  
  return (
    <div className="app">
      <header>
        <h1>Robot Turtles</h1>
        {gameStarted && (
          <div className="mode-selector">
            <button 
              className={aiMode ? 'active' : ''} 
              onClick={() => setAIMode(true)}
            >
              AI Mode
            </button>
            <button 
              className={!aiMode ? 'active' : ''} 
              onClick={() => setAIMode(false)}
            >
              Manual Mode
            </button>
          </div>
        )}
      </header>
      
      <main>
        {!gameStarted ? (
          <MainMenu onStartGame={handleStartGame} />
        ) : (
          <div className="game-container">
            <div className="game-info">
              <p>Moves: {gameState.moves}</p>
              <p>Current Player: {gameState.currentPlayer !== undefined ? gameState.currentPlayer + 1 : 1}</p>
              {(gameState.gameOver || gameState.status !== 'active') && (
                <div className="game-over">
                  <h2>{gameState.win || gameState.status === 'completed' ? 'Congratulations!' : 'Game Over'}</h2>
                  <button onClick={handleResetGame}>Back to Menu</button>
                </div>
              )}
              {(!gameState.gameOver && gameState.status === 'active') && (
                <button onClick={handleResetGame} className="menu-button">
                  Back to Menu
                </button>
              )}
            </div>
            
            <Board 
              onAction={handleAction} 
              aiMode={aiMode} 
              gameState={gameState}
            />
            
            <div className="controls-footer">
              <button onClick={handleNewGame} className="new-game-button">
                New Game
              </button>
              
              <div className="instructions-toggle">
                <button onClick={() => setShowInstructions(!showInstructions)}>
                  {showInstructions ? 'Hide Instructions' : 'Show Instructions'}
                </button>
              </div>
            </div>
            
            {showInstructions && (
              <div className="instructions">
                <h3>Game Instructions</h3>
                <p>Control your robot turtle to reach the jewel using these commands:</p>
                <ul>
                  <li><strong>Forward</strong>: Move one space in the current direction</li>
                  <li><strong>Left</strong>: Turn 90 degrees to the left</li>
                  <li><strong>Right</strong>: Turn 90 degrees to the right</li>
                  <li><strong>Laser</strong>: Fire a laser to destroy walls in front of you</li>
                </ul>
                <p>In AI mode, the system will recommend the best next action.</p>
                <p>Move limit: 50 moves</p>
              </div>
            )}
          </div>
        )}
      </main>
      
      <footer>
        <p>Robot Turtles Game with Reinforcement Learning</p>
      </footer>
    </div>
  );
};

export default App; 
import React from 'react';
import './MainMenu.css';

interface MainMenuProps {
  onStartGame: (playerCount: number) => void;
}

const MainMenu: React.FC<MainMenuProps> = ({ onStartGame }) => {
  return (
    <div className="main-menu">
      <div className="menu-card">
        <h2>Welcome to Robot Turtles!</h2>
        <p>Select the number of players or start a test game:</p>
        
        <div className="player-buttons">
          <button onClick={() => onStartGame(1)}>
            <span className="number">1</span>
            <span className="label">Single Player</span>
          </button>
          
          <button onClick={() => onStartGame(2)}>
            <span className="number">2</span>
            <span className="label">Two Players</span>
          </button>
          
          <button onClick={() => onStartGame(3)}>
            <span className="number">3</span>
            <span className="label">Three Players</span>
          </button>
          
          <button onClick={() => onStartGame(4)}>
            <span className="number">4</span>
            <span className="label">Four Players</span>
          </button>
        </div>
        
        <div className="test-button">
          <button onClick={() => onStartGame(1)} className="test-game">
            Quick Test Game
          </button>
        </div>
        
        <div className="menu-info">
          <p>
            Robot Turtles is a board game that teaches programming concepts to children.
          </p>
          <p>
            Control your turtle using simple commands to reach the jewel.
          </p>
        </div>
      </div>
    </div>
  );
};

export default MainMenu; 
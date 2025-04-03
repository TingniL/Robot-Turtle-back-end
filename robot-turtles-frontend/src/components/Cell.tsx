import React from 'react';
import './Cell.css';

interface CellProps {
  row: number;
  col: number;
  isTurtle: boolean;
  isJewel: boolean;
  isWall: boolean;
  turtleDirection: number | null;
}

const Cell: React.FC<CellProps> = ({ 
  row, 
  col, 
  isTurtle, 
  isJewel, 
  isWall, 
  turtleDirection 
}) => {
  
  const getDirectionClass = () => {
    if (turtleDirection === null) return '';
    const directions = ['up', 'right', 'down', 'left'];
    return `turtle-${directions[turtleDirection]}`;
  };
  
  const getCellContent = () => {
    if (isTurtle) {
      return <div className={`turtle ${getDirectionClass()}`} />;
    }
    if (isJewel) {
      return <div className="jewel" />;
    }
    if (isWall) {
      return <div className="wall" />;
    }
    return null;
  };
  
  return (
    <div className={`cell ${(row + col) % 2 === 0 ? 'light' : 'dark'}`} data-row={row} data-col={col}>
      {getCellContent()}
    </div>
  );
};

export default Cell; 
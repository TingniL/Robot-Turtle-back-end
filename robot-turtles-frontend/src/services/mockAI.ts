// 简单的模拟AI服务，用于本地开发

// 计算曼哈顿距离
const manhattanDistance = (pos1: [number, number], pos2: [number, number]): number => {
  return Math.abs(pos1[0] - pos2[0]) + Math.abs(pos1[1] - pos2[1]);
};

// 检查是否有墙阻挡
const isWallBlocking = (
  from: [number, number], 
  to: [number, number], 
  walls: [number, number][]
): boolean => {
  // 简化版：只检查目标位置是否有墙
  return walls.some(([r, c]) => r === to[0] && c === to[1]);
};

// 根据方向和位置，计算前进一步的新位置
const getForwardPosition = (pos: [number, number], direction: number): [number, number] => {
  const [row, col] = pos;
  
  switch (direction) {
    case 0: // 上
      return [row - 1, col];
    case 1: // 右
      return [row, col + 1];
    case 2: // 下
      return [row + 1, col];
    case 3: // 左
      return [row, col - 1];
    default:
      return [row, col];
  }
};

// 模拟AI决策
export const mockAIDecision = (
  board: number[],
  turtlePos: [number, number],
  turtleDirection: number,
  jewelPos: [number, number],
  walls: [number, number][]
): { probabilities: number[], bestAction: string } => {
  // 计算龟到宝石的距离
  const distance = manhattanDistance(turtlePos, jewelPos);
  
  // 计算前进后的位置
  const forwardPos = getForwardPosition(turtlePos, turtleDirection);
  
  // 计算前进后的距离
  const forwardDistance = manhattanDistance(forwardPos, jewelPos);
  
  // 判断前进是否被墙挡住或出界
  const isBlocked = isWallBlocking(turtlePos, forwardPos, walls) || 
                    forwardPos[0] < 0 || forwardPos[0] >= 8 || 
                    forwardPos[1] < 0 || forwardPos[1] >= 8;
  
  // 计算左转、右转后的朝向
  const leftDirection = (turtleDirection + 3) % 4;
  const rightDirection = (turtleDirection + 1) % 4;
  
  // 计算左转后前进的位置
  const leftThenForwardPos = getForwardPosition(turtlePos, leftDirection);
  const leftDistance = manhattanDistance(leftThenForwardPos, jewelPos);
  
  // 计算右转后前进的位置
  const rightThenForwardPos = getForwardPosition(turtlePos, rightDirection);
  const rightDistance = manhattanDistance(rightThenForwardPos, jewelPos);
  
  // 初始化概率
  let forwardProb = 0.25;
  let leftProb = 0.25;
  let rightProb = 0.25;
  let laserProb = 0.25;
  
  // 如果前进可以减少距离且没有阻挡，增加前进概率
  if (forwardDistance < distance && !isBlocked) {
    forwardProb = 0.7;
    leftProb = 0.1;
    rightProb = 0.1;
    laserProb = 0.1;
  } 
  // 如果前进被阻挡，但有墙可以用激光清除，增加激光概率
  else if (isBlocked && isWallBlocking(turtlePos, forwardPos, walls)) {
    forwardProb = 0.1;
    leftProb = 0.1;
    rightProb = 0.1;
    laserProb = 0.7;
  }
  // 如果左转后前进更接近宝石，增加左转概率
  else if (leftDistance < distance && leftDistance <= rightDistance) {
    forwardProb = 0.1;
    leftProb = 0.7;
    rightProb = 0.1;
    laserProb = 0.1;
  }
  // 如果右转后前进更接近宝石，增加右转概率
  else if (rightDistance < distance && rightDistance <= leftDistance) {
    forwardProb = 0.1;
    leftProb = 0.1;
    rightProb = 0.7;
    laserProb = 0.1;
  }
  
  const probabilities = [forwardProb, leftProb, rightProb, laserProb];
  const actions = ["FORWARD", "LEFT", "RIGHT", "LASER"];
  const bestActionIndex = probabilities.indexOf(Math.max(...probabilities));
  
  return {
    probabilities,
    bestAction: actions[bestActionIndex]
  };
}; 
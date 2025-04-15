// Game state
let gameState = {
    id: null,
    board: [],
    turtlePos: [],
    turtleDirection: [],
    jewelPos: [],
    walls: [],
    status: 'WAITING',
    moves: 0,
    currentPlayer: 0,
    players: 2,
    cards: []
};

// 用于跟踪游戏会话的计数器，避免初始化混乱
let gameSessionId = 0;

// 方向映射，从逻辑方向到CSS类
const DIRECTION_MAP = {
    'NORTH': 'up',
    'EAST': 'right',
    'SOUTH': 'down',
    'WEST': 'left'
};

// 固定的初始游戏配置 - 修正坐标以便与棋盘显示一致
const DEFAULT_GAME_CONFIG = {
    turtlePos: [[3, 6]],                // 乌龟在底部中间位置
    turtleDirection: ['NORTH'],         // 乌龟朝北
    jewelPos: [3, 1],                   // 宝石在顶部中间位置
    walls: [
        [3, 3],                         // 直接阻挡乌龟前进路径的墙壁
        [2, 3], [4, 3]                  // 左右两侧的墙壁
    ],
    status: 'ACTIVE',
    moves: 0,
    players: 1,
    currentPlayer: 0
};

// Initialize game board
function initBoard() {
    const board = document.getElementById('gameBoard');
    board.innerHTML = '';
    
    // 获取基础URL
    const baseUrl = window.location.href.substring(0, window.location.href.lastIndexOf('/') + 1);
    console.log("Base URL for images:", baseUrl);
    
    // 打印当前游戏状态，用于调试
    console.log('初始化棋盘，游戏状态:', JSON.stringify(gameState));
    console.log('乌龟位置:', JSON.stringify(gameState.turtlePos));
    console.log('乌龟方向:', JSON.stringify(gameState.turtleDirection));
    console.log('宝石位置:', JSON.stringify(gameState.jewelPos));
    console.log('墙壁位置:', JSON.stringify(gameState.walls));
    
    // 创建调试信息
    const debugInfo = document.getElementById('debugInfo');
    if (debugInfo) {
        const stateInfo = document.createElement('div');
        stateInfo.innerHTML = `
            <h4>当前游戏状态:</h4>
            <pre>${JSON.stringify(gameState, null, 2)}</pre>
        `;
        debugInfo.appendChild(stateInfo);
    }
    
    for (let i = 0; i < 8; i++) {
        for (let j = 0; j < 8; j++) {
            const cell = document.createElement('div');
            cell.className = 'cell';
            cell.dataset.x = j;
            cell.dataset.y = i;
            
            // 添加坐标文本，更大更明显
            const coordText = document.createElement('span');
            coordText.textContent = `${j},${i}`;
            coordText.style.position = 'absolute';
            coordText.style.fontSize = '10px';
            coordText.style.top = '2px';
            coordText.style.left = '2px';
            coordText.style.color = '#666';
            coordText.style.fontWeight = 'bold';
            cell.appendChild(coordText);
            
            // 确保单元格有边框
            cell.style.border = '1px solid #ccc';
            
            let cellContent = null;
            let cellType = 'empty';
            
            // 首先检查是否有乌龟
            const turtleIndex = gameState.turtlePos.findIndex(pos => 
                Array.isArray(pos) && pos.length >= 2 && pos[0] === j && pos[1] === i
            );
            
            if (turtleIndex !== -1) {
                // 有乌龟，创建乌龟元素
                cellContent = document.createElement('div');
                const direction = DIRECTION_MAP[gameState.turtleDirection[turtleIndex]] || 'up';
                cellContent.className = `turtle ${direction}`;
                cellContent.style.backgroundImage = `url('${baseUrl}img/turtle1.png')`;
                cellContent.style.border = '2px solid red'; 
                cellContent.style.width = '80%';
                cellContent.style.height = '80%';
                cellContent.style.backgroundSize = 'contain';
                cellContent.style.backgroundRepeat = 'no-repeat';
                cellContent.style.backgroundPosition = 'center';
                cell.style.backgroundColor = '#ffe6e6'; 
                cellType = 'turtle';
                console.log(`放置乌龟 ${turtleIndex + 1} 在位置 (${j}, ${i}) 朝向 ${direction}`);
            } 
            // 检查是否有宝石
            else if (gameState.jewelPos && gameState.jewelPos.length >= 2 && 
                    gameState.jewelPos[0] === j && gameState.jewelPos[1] === i) {
                // 有宝石，创建宝石元素
                cellContent = document.createElement('div');
                cellContent.className = 'jewel';
                cellContent.style.backgroundImage = `url('${baseUrl}img/jewel.png')`;
                cellContent.style.border = '2px solid purple';
                cellContent.style.width = '80%';
                cellContent.style.height = '80%';
                cellContent.style.backgroundSize = 'contain';
                cellContent.style.backgroundRepeat = 'no-repeat';
                cellContent.style.backgroundPosition = 'center';
                cell.style.backgroundColor = '#e6e6ff';
                cellType = 'jewel';
                console.log(`放置宝石在位置 (${j}, ${i})`);
            }
            // 检查是否有墙
            else if (gameState.walls && gameState.walls.some(wall => 
                    Array.isArray(wall) && wall.length >= 2 && wall[0] === j && wall[1] === i)) {
                // 有墙，创建墙元素
                cellContent = document.createElement('div');
                cellContent.className = 'wall';
                cellContent.style.backgroundImage = `url('${baseUrl}img/wall.png')`;
                cellContent.style.border = '2px solid brown';
                cellContent.style.width = '80%';
                cellContent.style.height = '80%';
                cellContent.style.backgroundSize = 'contain';
                cellContent.style.backgroundRepeat = 'no-repeat';
                cellContent.style.backgroundPosition = 'center';
                cell.style.backgroundColor = '#fff7e6';
                cellType = 'wall';
                console.log(`放置墙在位置 (${j}, ${i})`);
            }
            
            // 显示单元格内容类型
            const typeIndicator = document.createElement('div');
            typeIndicator.style.position = 'absolute';
            typeIndicator.style.bottom = '2px';
            typeIndicator.style.right = '2px';
            typeIndicator.style.fontSize = '8px';
            typeIndicator.style.color = '#999';
            typeIndicator.textContent = cellType;
            cell.appendChild(typeIndicator);
            
            // 如果有内容，添加到单元格
            if (cellContent) {
                cell.appendChild(cellContent);
            }
            
            board.appendChild(cell);
        }
    }
}

// 初始化测试数据以便在后端连接之前显示一些内容
function initTestData() {
    // 清空调试信息区域
    const debugInfo = document.getElementById('debugInfo');
    if (debugInfo) {
        debugInfo.innerHTML = '<h3>Debug Info</h3>';
    }
    
    // 增加游戏会话ID以区分不同的游戏
    gameSessionId++;
    console.log(`初始化新游戏，会话ID: ${gameSessionId}`);
    
    // 重新启用所有按钮
    document.getElementById('forward-btn').disabled = false;
    document.getElementById('left-btn').disabled = false;
    document.getElementById('right-btn').disabled = false;
    document.getElementById('laser-btn').disabled = false;
    document.getElementById('ai-btn').disabled = false;
    
    // 清除错误/胜利消息
    const errorMessage = document.getElementById('errorMessage');
    errorMessage.innerHTML = '';
    errorMessage.style.display = 'none';
    
    // 完全重置游戏状态
    gameState = {
        id: null,
        board: [],
        turtlePos: null,
        turtleDirection: null,
        jewelPos: null,
        walls: null,
        status: null,
        moves: null,
        currentPlayer: null,
        players: null,
        cards: null
    };
    
    // 使用深拷贝复制DEFAULT_GAME_CONFIG，避免引用问题
    gameState.turtlePos = JSON.parse(JSON.stringify(DEFAULT_GAME_CONFIG.turtlePos));
    gameState.turtleDirection = JSON.parse(JSON.stringify(DEFAULT_GAME_CONFIG.turtleDirection));
    gameState.jewelPos = JSON.parse(JSON.stringify(DEFAULT_GAME_CONFIG.jewelPos));
    gameState.walls = JSON.parse(JSON.stringify(DEFAULT_GAME_CONFIG.walls));
    gameState.status = DEFAULT_GAME_CONFIG.status;
    gameState.moves = DEFAULT_GAME_CONFIG.moves;
    gameState.currentPlayer = DEFAULT_GAME_CONFIG.currentPlayer;
    gameState.players = DEFAULT_GAME_CONFIG.players;
    gameState.cards = [
        [
            { type: 'FORWARD' }, 
            { type: 'LEFT' }, 
            { type: 'RIGHT' }, 
            { type: 'LASER' }
        ]
    ];
    
    console.log("测试数据已初始化:", JSON.stringify(gameState));
    
    // 显示使用说明
    showMessage("开始游戏！使用镭射摧毁前方的墙壁，然后前进到达宝石位置。", "info");
    
    // 显示当前初始化的配置
    if (debugInfo) {
        // 显示图片测试
        const baseUrl = window.location.href.substring(0, window.location.href.lastIndexOf('/') + 1);
        debugInfo.innerHTML += `
            <h4>图片测试</h4>
            <div style="display: flex; gap: 10px;">
                <div>
                    <p>乌龟图片:</p>
                    <img src="${baseUrl}img/turtle1.png" style="width: 50px; height: 50px;">
                </div>
                <div>
                    <p>宝石图片:</p>
                    <img src="${baseUrl}img/jewel.png" style="width: 50px; height: 50px;">
                </div>
                <div>
                    <p>墙壁图片:</p>
                    <img src="${baseUrl}img/wall.png" style="width: 50px; height: 50px;">
                </div>
            </div>
            <p>基础URL: ${baseUrl}</p>
            <p>乌龟位置: ${JSON.stringify(gameState.turtlePos)}</p>
            <p>宝石位置: ${JSON.stringify(gameState.jewelPos)}</p>
            <p>墙壁位置: ${JSON.stringify(gameState.walls)}</p>
        `;
    }
    
    // 更新界面
    initBoard();
    updateGameInfo();
    updateCardDeck();
    
    // 显示游戏界面，隐藏选择界面
    document.getElementById('playerSelection').style.display = 'none';
    document.getElementById('gameInfo').style.display = 'block';
    document.getElementById('gameControls').style.display = 'flex';
    document.getElementById('cardDeck').style.display = 'flex';
}

// Update game info display
function updateGameInfo() {
    const gameStatus = document.getElementById('gameStatus');
    const moveCount = document.getElementById('moveCount');
    const currentPlayer = document.getElementById('currentPlayer');
    
    // 更新状态文本
    gameStatus.textContent = gameState.status;
    
    // 如果游戏胜利，突出显示
    if (gameState.status === 'WIN') {
        gameStatus.style.color = 'green';
        gameStatus.style.fontWeight = 'bold';
    } else {
        gameStatus.style.color = '';
        gameStatus.style.fontWeight = '';
    }
    
    // 更新移动次数
    moveCount.textContent = gameState.moves;
    
    // 更新当前玩家
    currentPlayer.textContent = `Player ${gameState.currentPlayer + 1}`;
}

// Update game state locally
function updateLocalGameState(action) {
    // 游戏已经结束，不做任何更新
    if (gameState.status === 'WIN') {
        console.log('游戏已经结束，不再更新状态');
        return;
    }
    
    // 捕获当前会话ID
    const currentSessionId = gameSessionId;
    
    if (action === 'FORWARD') {
        const direction = gameState.turtleDirection[gameState.currentPlayer];
        const turtle = gameState.turtlePos[gameState.currentPlayer];
        
        // 保存旧位置用于后续比较
        const oldPosition = [...turtle];
        
        // 计算下一步位置
        let newX = turtle[0];
        let newY = turtle[1];
        
        if (direction === 'NORTH' && turtle[1] > 0) {
            newY--;
        } else if (direction === 'SOUTH' && turtle[1] < 7) {
            newY++;
        } else if (direction === 'EAST' && turtle[0] < 7) {
            newX++;
        } else if (direction === 'WEST' && turtle[0] > 0) {
            newX--;
        }
        
        // 检查是否与墙壁碰撞
        if (gameState.walls.some(wall => wall[0] === newX && wall[1] === newY)) {
            console.log("碰到墙壁，无法前进");
            showError("无法前进，前方有墙壁阻挡！");
            // 不更新位置，保持原位
        } else {
            // 没有墙壁，可以前进
            turtle[0] = newX;
            turtle[1] = newY;
            
            // 检查乌龟是否到达宝石位置
            if (gameState.jewelPos && gameState.jewelPos.length >= 2 && 
                turtle[0] === gameState.jewelPos[0] && turtle[1] === gameState.jewelPos[1]) {
                // 乌龟找到宝石，游戏结束
                gameState.status = 'WIN';
                
                // 立即更新一次游戏界面，确保状态正确
                if (currentSessionId === gameSessionId) {
                    initBoard();
                    updateGameInfo();
                }
                
                showGameWin();
                return; // 立即返回，不再更新移动次数和当前玩家
            }
        }
    } 
    else if (action === 'LEFT') {
        const direction = gameState.turtleDirection[gameState.currentPlayer];
        if (direction === 'NORTH') {
            gameState.turtleDirection[gameState.currentPlayer] = 'WEST';
        } else if (direction === 'WEST') {
            gameState.turtleDirection[gameState.currentPlayer] = 'SOUTH';
        } else if (direction === 'SOUTH') {
            gameState.turtleDirection[gameState.currentPlayer] = 'EAST';
        } else if (direction === 'EAST') {
            gameState.turtleDirection[gameState.currentPlayer] = 'NORTH';
        }
    }
    else if (action === 'RIGHT') {
        const direction = gameState.turtleDirection[gameState.currentPlayer];
        if (direction === 'NORTH') {
            gameState.turtleDirection[gameState.currentPlayer] = 'EAST';
        } else if (direction === 'EAST') {
            gameState.turtleDirection[gameState.currentPlayer] = 'SOUTH';
        } else if (direction === 'SOUTH') {
            gameState.turtleDirection[gameState.currentPlayer] = 'WEST';
        } else if (direction === 'WEST') {
            gameState.turtleDirection[gameState.currentPlayer] = 'NORTH';
        }
    }
    else if (action === 'LASER') {
        // 实现镭射功能
        const direction = gameState.turtleDirection[gameState.currentPlayer];
        const turtle = gameState.turtlePos[gameState.currentPlayer];
        
        // 计算镭射射击位置（乌龟前方格子）
        let targetX = turtle[0];
        let targetY = turtle[1];
        
        if (direction === 'NORTH' && turtle[1] > 0) {
            targetY--;
        } else if (direction === 'SOUTH' && turtle[1] < 7) {
            targetY++;
        } else if (direction === 'EAST' && turtle[0] < 7) {
            targetX++;
        } else if (direction === 'WEST' && turtle[0] > 0) {
            targetX--;
        } else {
            // 如果镭射射向棋盘外，不执行任何操作
            showError("镭射射向棋盘外，无效操作！");
            return;
        }
        
        // 检查是否有墙壁
        const wallIndex = gameState.walls.findIndex(wall => 
            wall[0] === targetX && wall[1] === targetY);
        
        if (wallIndex !== -1) {
            // 发现墙壁，移除
            gameState.walls.splice(wallIndex, 1);
            
            // 显示成功消息
            showMessage("镭射成功摧毁了墙壁！", "success");
            
            // 在界面上显示动画效果（闪烁）
            const cell = document.querySelector(`.cell[data-x="${targetX}"][data-y="${targetY}"]`);
            if (cell) {
                cell.style.animation = "pulsate 0.5s";
                setTimeout(() => { 
                    // 确保仍在同一游戏会话中
                    if (currentSessionId === gameSessionId) {
                        cell.style.animation = "";
                    }
                }, 500);
            }
        } else {
            // 没有墙壁，镭射穿过
            showError("镭射没有击中任何墙壁！");
        }
    }
    
    // 只有在会话ID没有变化时才更新这些值
    if (currentSessionId === gameSessionId) {
        gameState.moves++;
        gameState.currentPlayer = (gameState.currentPlayer + 1) % gameState.players;
    }
}

// 显示游戏胜利信息
function showGameWin() {
    // 保存当前游戏状态，以防止自动刷新或状态改变
    const currentSessionId = gameSessionId;
    
    const errorMessage = document.getElementById('errorMessage');
    const winMessage = document.createElement('div');
    winMessage.innerHTML = `
        <h3 style="color: green;">Victory!</h3>
        <p>Player ${gameState.currentPlayer + 1} found the jewel!</p>
        <p>Total moves: ${gameState.moves}</p>
        <p>Click "New Game" to play again.</p>
    `;
    
    errorMessage.innerHTML = '';
    errorMessage.appendChild(winMessage);
    errorMessage.style.display = 'block';
    errorMessage.style.backgroundColor = '#e6ffe6';
    errorMessage.style.border = '2px solid #4CAF50';
    
    // 禁用所有游戏控制按钮，只保留New Game按钮可用
    document.getElementById('forward-btn').disabled = true;
    document.getElementById('left-btn').disabled = true;
    document.getElementById('right-btn').disabled = true;
    document.getElementById('laser-btn').disabled = true;
    document.getElementById('ai-btn').disabled = true;
    
    // 重新渲染棋盘一次，确保显示最终状态
    setTimeout(() => {
        if (currentSessionId === gameSessionId) {
            initBoard();
        }
    }, 100);
}

// Create new game
async function createNewGame() {
    try {
        showLoading();
        
        // 清空调试信息
        const debugInfo = document.getElementById('debugInfo');
        if (debugInfo) {
            debugInfo.innerHTML = '<h3>Debug Info</h3>';
        }
        
        // 增加游戏会话ID以区分不同的游戏
        gameSessionId++;
        console.log(`创建新游戏，会话ID: ${gameSessionId}`);
        
        // 保存当前会话ID用于安全检查
        const currentSessionId = gameSessionId;
        
        // 清空游戏面板
        const gameBoard = document.getElementById('gameBoard');
        if (gameBoard) {
            gameBoard.innerHTML = '';
        }
        
        // 清空卡片区域
        const cardDeck = document.getElementById('cardDeck');
        if (cardDeck) {
            cardDeck.innerHTML = '';
        }
        
        // 完全重置游戏状态
        gameState = null;
        gameState = {
            id: null,
            board: [],
            turtlePos: null,
            turtleDirection: null,
            jewelPos: null,
            walls: null,
            status: null,
            moves: null,
            currentPlayer: null,
            players: 1, // 默认为单人游戏
            cards: null
        };
        
        // 重新启用所有按钮
        document.getElementById('forward-btn').disabled = false;
        document.getElementById('left-btn').disabled = false;
        document.getElementById('right-btn').disabled = false;
        document.getElementById('laser-btn').disabled = false;
        document.getElementById('ai-btn').disabled = false;
        
        // 清除错误/胜利消息
        const errorMessage = document.getElementById('errorMessage');
        errorMessage.innerHTML = '';
        errorMessage.style.display = 'none';
        
        // 显示临时消息，告知用户游戏正在初始化
        showMessage("正在初始化新游戏...", "info");
        
        // Use the backend API if available, otherwise use local test data
        try {
            const fetchPromise = fetch('http://localhost:5000/api/games/new', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    players: gameState.players
                })
            });
            
            // 设置超时，如果3秒内没有响应，就使用本地数据
            const timeoutPromise = new Promise((resolve, reject) => {
                setTimeout(() => reject(new Error('Backend request timeout')), 3000);
            });
            
            // 使用Promise.race，谁先完成就用谁的结果
            const response = await Promise.race([fetchPromise, timeoutPromise]);
                
            if (!response.ok) {
                throw new Error('Failed to create game');
            }
            
            // 确保仍然是同一个游戏会话
            if (currentSessionId !== gameSessionId) {
                console.log('游戏会话已更改，忽略旧的响应');
                hideLoading();
                return;
            }
            
            const data = await response.json();
            
            // 再次检查会话ID
            if (currentSessionId !== gameSessionId) {
                console.log('游戏会话已更改，忽略旧的响应');
                hideLoading();
                return;
            }
            
            // 更新游戏状态
            gameState.id = data.id;
            gameState.board = data.board;
            gameState.turtlePos = data.turtlePos;
            gameState.turtleDirection = data.turtleDirection;
            gameState.jewelPos = data.jewelPos;
            gameState.walls = data.walls;
            gameState.status = data.status;
            gameState.moves = data.moves;
            gameState.currentPlayer = 0;
            
            console.log("Game created successfully:", gameState);
            showMessage("游戏创建成功！", "success");
            
            // 调用初始化界面
            setTimeout(() => {
                if (currentSessionId === gameSessionId) {
                    initBoard();
                    updateGameInfo();
                    updateCardDeck();
                }
            }, 100);
        } catch (error) {
            console.warn("Backend connection failed, using local data:", error);
            // 确保仍然是同一个游戏会话
            if (currentSessionId === gameSessionId) {
                // Fallback to test data
                showMessage("无法连接到服务器，使用本地数据继续游戏", "info");
                
                // 使用延迟调用，给UI时间刷新
                setTimeout(() => {
                    if (currentSessionId === gameSessionId) {
                        initTestData();
                    }
                }, 100);
            }
        } finally {
            hideLoading();
        }
        
        // 显示游戏界面，隐藏选择界面
        document.getElementById('playerSelection').style.display = 'none';
        document.getElementById('gameInfo').style.display = 'block';
        document.getElementById('gameControls').style.display = 'flex';
        document.getElementById('cardDeck').style.display = 'flex';
    } catch (error) {
        console.error("创建游戏时发生错误:", error);
        showError(`创建游戏失败: ${error.message}`);
        hideLoading();
    }
}

// Start game with selected number of players
function startGame(players) {
    gameState.players = players;
    createNewGame();
}

// Update card deck display
function updateCardDeck() {
    const cardDeck = document.getElementById('cardDeck');
    cardDeck.innerHTML = '';
    
    // 获取基础URL
    const baseUrl = window.location.href.substring(0, window.location.href.lastIndexOf('/') + 1);
    
    // 确保cards数组已经被正确初始化
    if (!gameState.cards || !gameState.cards[gameState.currentPlayer]) {
        return;
    }
    
    gameState.cards[gameState.currentPlayer].forEach((card, index) => {
        const cardElement = document.createElement('div');
        cardElement.className = 'card';
        cardElement.style.backgroundImage = `url('${baseUrl}img/${card.type.toLowerCase()}.png')`;
        cardElement.onclick = () => selectCard(index);
        cardDeck.appendChild(cardElement);
    });
}

// Select card
function selectCard(index) {
    const cards = document.querySelectorAll('.card');
    cards.forEach(card => card.classList.remove('selected'));
    cards[index].classList.add('selected');
}

// Perform action
async function performAction(action) {
    try {
        // 如果游戏已经结束，不允许进一步操作
        if (gameState.status === 'WIN') {
            console.log('游戏已结束，忽略操作:', action);
            return;
        }
        
        // 保存当前的游戏会话ID
        const currentSessionId = gameSessionId;
        
        showLoading();
        
        // Try to use backend API
        if (gameState.id) {
            try {
                const response = await fetch(`http://localhost:5000/api/games/${gameState.id}/action`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        action: action,
                        player: gameState.currentPlayer
                    })
                });
                
                if (!response.ok) {
                    throw new Error('Failed to perform action');
                }
                
                // 确保仍然是同一个游戏会话
                if (currentSessionId !== gameSessionId) {
                    console.log('游戏会话已更改，忽略旧的响应');
                    hideLoading();
                    return;
                }
                
                const data = await response.json();
                gameState = {
                    ...gameState,
                    board: data.board,
                    turtlePos: data.turtlePos,
                    turtleDirection: data.turtleDirection,
                    status: data.status,
                    moves: data.moves,
                    currentPlayer: (gameState.currentPlayer + 1) % gameState.players
                };
                
                console.log("Action performed successfully:", action, data);
                
                // 检查游戏是否结束（如果后端没有处理）
                if (action === 'FORWARD') {
                    const playerIndex = (gameState.currentPlayer + gameState.players - 1) % gameState.players; // 前一个玩家
                    const turtle = gameState.turtlePos[playerIndex];
                    
                    if (gameState.jewelPos && gameState.jewelPos.length >= 2 && 
                        turtle[0] === gameState.jewelPos[0] && turtle[1] === gameState.jewelPos[1]) {
                        gameState.status = 'WIN';
                        gameState.currentPlayer = playerIndex; // 设置为获胜的玩家
                        
                        // 立即更新游戏界面，然后再显示胜利消息
                        initBoard();
                        updateGameInfo();
                        
                        showGameWin();
                        hideLoading();
                        return; // 立即返回，避免进一步的更新
                    }
                }
            } catch (error) {
                console.warn("Backend action failed, using local logic:", error);
                
                // 确保仍然是同一个游戏会话
                if (currentSessionId !== gameSessionId) {
                    console.log('游戏会话已更改，忽略旧的动作');
                    hideLoading();
                    return;
                }
                
                // Fallback to local action handling
                updateLocalGameState(action);
            }
        } else {
            // No connection to backend, update locally
            updateLocalGameState(action);
        }
        
        // 确认游戏仍处于活动状态后再执行这些更新
        if (gameState.status !== 'WIN' && currentSessionId === gameSessionId) {
            initBoard();
            updateGameInfo();
            updateCardDeck();
        }
    } catch (error) {
        showError(error.message);
    } finally {
        hideLoading();
    }
}

// Get AI prediction
async function getAIPrediction() {
    try {
        showLoading();
        
        // Try to get AI prediction from backend
        try {
            const response = await fetch('http://localhost:5000/api/ai/predict', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    state: {
                        turtlePos: gameState.turtlePos,
                        turtleDirection: gameState.turtleDirection,
                        jewelPos: gameState.jewelPos,
                        walls: gameState.walls,
                        currentPlayer: gameState.currentPlayer
                    }
                })
            });
            
            if (!response.ok) {
                throw new Error('Failed to get AI prediction');
            }
            
            const data = await response.json();
            showAIRecommendation(data.action, data.probabilities);
            console.log("AI prediction received:", data);
        } catch (error) {
            console.warn("AI prediction failed, using random:", error);
            // Fallback to random prediction
            generateRandomPrediction();
        }
    } catch (error) {
        showError(error.message);
    } finally {
        hideLoading();
    }
}

// Generate random prediction when backend is unavailable
function generateRandomPrediction() {
    const actions = ['FORWARD', 'LEFT', 'RIGHT', 'LASER'];
    const randomProbs = [];
    let sum = 0;
    
    // Generate random probabilities
    for (let i = 0; i < 4; i++) {
        randomProbs[i] = Math.random();
        sum += randomProbs[i];
    }
    
    // Normalize probabilities to sum to 1
    for (let i = 0; i < 4; i++) {
        randomProbs[i] = randomProbs[i] / sum;
    }
    
    // Find highest probability action
    let maxIndex = 0;
    for (let i = 1; i < 4; i++) {
        if (randomProbs[i] > randomProbs[maxIndex]) {
            maxIndex = i;
        }
    }
    
    const probabilities = {};
    actions.forEach((action, index) => {
        probabilities[action] = randomProbs[index];
    });
    
    showAIRecommendation(actions[maxIndex], probabilities);
}

// Show AI recommendation
function showAIRecommendation(action, probabilities) {
    const recommendation = document.createElement('div');
    recommendation.innerHTML = `
        <h3>AI Assistance</h3>
        <p>AI Recommendation: ${action}</p>
        <div class="probabilities">
            ${Object.entries(probabilities)
                .map(([action, prob]) => `<p>${action}: ${(prob * 100).toFixed(2)}%</p>`)
                .join('')}
        </div>
    `;
    
    const errorMessage = document.getElementById('errorMessage');
    errorMessage.innerHTML = '';
    errorMessage.appendChild(recommendation);
    errorMessage.style.display = 'block';
}

// Show loading spinner
function showLoading() {
    document.getElementById('loading').classList.add('active');
}

// Hide loading spinner
function hideLoading() {
    document.getElementById('loading').classList.remove('active');
}

// Show error message
function showError(message) {
    const errorMessage = document.getElementById('errorMessage');
    errorMessage.textContent = message;
    errorMessage.style.display = 'block';
}

// 显示游戏成功消息
function showMessage(message, type = "info") {
    const errorMessage = document.getElementById('errorMessage');
    errorMessage.textContent = message;
    errorMessage.style.display = 'block';
    
    if (type === "success") {
        errorMessage.style.backgroundColor = '#e6ffe6';
        errorMessage.style.color = 'green';
        errorMessage.style.border = '1px solid green';
    } else {
        errorMessage.style.backgroundColor = '#f8f8f8';
        errorMessage.style.color = 'black';
        errorMessage.style.border = '1px solid #ddd';
    }
    
    // 5秒后自动隐藏
    setTimeout(() => {
        errorMessage.style.display = 'none';
    }, 5000);
}

// 初始化
document.addEventListener('DOMContentLoaded', function() {
  // 初始化玩家选择界面
  document.getElementById('playerSelection').style.display = 'block';
  document.getElementById('gameInfo').style.display = 'none';
  document.getElementById('gameControls').style.display = 'none';
  document.getElementById('cardDeck').style.display = 'none';
  
  // 初始化棋盘
  initBoard();
  
  // 注意：按钮事件现在在HTML中绑定，这里不再需要重复绑定
  console.log('Game initialized!');
}); 
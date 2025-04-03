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

// Initialize game board
function initBoard() {
    const board = document.getElementById('gameBoard');
    board.innerHTML = '';
    
    for (let i = 0; i < 8; i++) {
        for (let j = 0; j < 8; j++) {
            const cell = document.createElement('div');
            cell.className = 'cell';
            cell.dataset.x = j;
            cell.dataset.y = i;
            
            // Check for turtle
            const turtleIndex = gameState.turtlePos.findIndex(pos => pos[0] === j && pos[1] === i);
            if (turtleIndex !== -1) {
                const turtle = document.createElement('div');
                turtle.className = `turtle ${gameState.turtleDirection[turtleIndex].toLowerCase()}`;
                turtle.style.backgroundImage = `url('img/turtle${turtleIndex + 1}.png')`;
                cell.appendChild(turtle);
            }
            
            // Check for jewel
            if (gameState.jewelPos[0] === j && gameState.jewelPos[1] === i) {
                const jewel = document.createElement('div');
                jewel.className = 'jewel';
                jewel.style.backgroundImage = "url('img/jewel.png')";
                cell.appendChild(jewel);
            }
            
            // Check for wall
            if (gameState.walls.some(wall => wall[0] === j && wall[1] === i)) {
                cell.classList.add('wall');
            }
            
            board.appendChild(cell);
        }
    }
}

// Update game info display
function updateGameInfo() {
    document.getElementById('gameStatus').textContent = gameState.status;
    document.getElementById('moveCount').textContent = gameState.moves;
    document.getElementById('currentPlayer').textContent = `Player ${gameState.currentPlayer + 1}`;
}

// Create new game
async function createNewGame() {
    try {
        showLoading();
        const response = await fetch('http://localhost:5000/api/games/new', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                players: gameState.players
            })
        });
        
        if (!response.ok) {
            throw new Error('Failed to create game');
        }
        
        const data = await response.json();
        gameState = {
            ...gameState,
            id: data.id,
            board: data.board,
            turtlePos: data.turtlePos,
            turtleDirection: data.turtleDirection,
            jewelPos: data.jewelPos,
            walls: data.walls,
            status: 'ACTIVE',
            moves: 0,
            currentPlayer: 0,
            cards: data.cards
        };
        
        initBoard();
        updateGameInfo();
        updateCardDeck();
        document.getElementById('playerSelection').style.display = 'none';
        document.getElementById('gameInfo').style.display = 'block';
        document.getElementById('gameControls').style.display = 'flex';
        document.getElementById('cardDeck').style.display = 'flex';
    } catch (error) {
        showError(error.message);
    } finally {
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
    
    gameState.cards[gameState.currentPlayer].forEach((card, index) => {
        const cardElement = document.createElement('div');
        cardElement.className = 'card';
        cardElement.style.backgroundImage = `url('img/${card.type.toLowerCase()}.png')`;
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
        showLoading();
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
        
        const data = await response.json();
        gameState = {
            ...gameState,
            board: data.board,
            turtlePos: data.turtlePos,
            turtleDirection: data.turtleDirection,
            status: data.status,
            moves: data.moves,
            currentPlayer: data.currentPlayer,
            cards: data.cards
        };
        
        initBoard();
        updateGameInfo();
        updateCardDeck();
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
        const response = await fetch(`http://localhost:5000/api/games/${gameState.id}/predict`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                player: gameState.currentPlayer
            })
        });
        
        if (!response.ok) {
            throw new Error('Failed to get AI prediction');
        }
        
        const data = await response.json();
        showAIRecommendation(data.action, data.probabilities);
    } catch (error) {
        showError(error.message);
    } finally {
        hideLoading();
    }
}

// Show AI recommendation
function showAIRecommendation(action, probabilities) {
    const recommendation = document.createElement('div');
    recommendation.innerHTML = `
        <h3>AI Recommendation</h3>
        <p>Recommended Action: ${action}</p>
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

// 初始化
document.addEventListener('DOMContentLoaded', function() {
  // 初始化棋盘
  initBoard();
  
  // 绑定按钮事件
  document.getElementById('new-game-btn')?.addEventListener('click', createNewGame);
  document.getElementById('forward-btn')?.addEventListener('click', () => performAction('FORWARD'));
  document.getElementById('left-btn')?.addEventListener('click', () => performAction('LEFT'));
  document.getElementById('right-btn')?.addEventListener('click', () => performAction('RIGHT'));
  document.getElementById('laser-btn')?.addEventListener('click', () => performAction('LASER'));
  document.getElementById('ai-predict-btn')?.addEventListener('click', getAIPrediction);
  document.getElementById('follow-ai-btn')?.addEventListener('click', followAIRecommendation);
}); 
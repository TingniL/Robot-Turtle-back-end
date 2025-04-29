package com.robotturtle.service.impl;

import com.robotturtle.exception.GameNotFoundException;
import com.robotturtle.exception.InvalidGameStateException;
import com.robotturtle.exception.InvalidMoveException;
import com.robotturtle.model.Board;
import com.robotturtle.model.Card;
import com.robotturtle.model.Cell;
import com.robotturtle.model.CellContent;
import com.robotturtle.model.Game;
import com.robotturtle.model.GameConfig;
import com.robotturtle.model.Player;
import com.robotturtle.model.Position;
import com.robotturtle.model.Wall;
import com.robotturtle.model.enums.CardType;
import com.robotturtle.model.enums.CellType;
import com.robotturtle.model.enums.Direction;
import com.robotturtle.model.enums.GameState;
import com.robotturtle.model.enums.PlayerColor;
import com.robotturtle.model.enums.PlayerType;
import com.robotturtle.model.enums.WallType;
import com.robotturtle.repository.GameRepository;
import com.robotturtle.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {
    private final GameRepository gameRepository;
    private static final int INACTIVE_GAME_TIMEOUT_MINUTES = 30;

    @Override
    public Game createGame(GameConfig config) {
        validateGameConfig(config);
        Game game = new Game(config);
        game.setGameCode(generateGameCode());
        
        // 添加主机玩家
        Player hostPlayer = config.getHostPlayer();
        hostPlayer.setId(UUID.randomUUID().toString());
        game.getPlayers().add(hostPlayer);
        
        // 初始化AI玩家（如果有）
        for (int i = 0; i < config.getAiPlayerCount(); i++) {
            Player aiPlayer = createAIPlayer(getAvailableColors(game).get(0));
            game.getPlayers().add(aiPlayer);
        }
        
        return gameRepository.save(game);
    }

    @Override
    public Game joinGame(String gameCode, Player player) {
        Game game = gameRepository.findByGameCode(gameCode)
                .orElseThrow(() -> new GameNotFoundException(gameCode));
        
        if (game.getPlayers().size() >= game.getConfig().getPlayerCount()) {
            throw new InvalidGameStateException("Game is full");
        }
        
        if (!getAvailableColors(game).contains(player.getColor())) {
            throw new InvalidGameStateException("Color is not available");
        }
        
        player.setId(UUID.randomUUID().toString());
        game.getPlayers().add(player);
        
        if (game.getPlayers().size() == game.getConfig().getPlayerCount()) {
            game.setState(GameState.PLAYING);
            initializeGameBoard(game);
        }
        
        return gameRepository.save(game);
    }

    @Override
    public Game startGame(String gameId) {
        Game game = getGame(gameId);
        if (game.getState() != GameState.WAITING_FOR_PLAYERS) {
            throw new InvalidGameStateException("Game cannot be started");
        }
        
        game.setState(GameState.PLAYING);
        initializeGameBoard(game);
        return gameRepository.save(game);
    }

    @Override
    public Game getGame(String gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));
    }

    @Override
    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    @Override
    public Game executeMove(String gameId, String playerId, List<Card> cards) {
        Game game = getGame(gameId);
        validatePlayerTurn(game, playerId);
        if (!isValidMove(gameId, playerId, cards)) {
            throw new InvalidMoveException("Invalid move");
        }
        
        Player player = findPlayer(game, playerId);
        for (Card card : cards) {
            executeCard(game, player, card);
        }
        
        updateGameState(game);
        return gameRepository.save(game);
    }

    @Override
    public Game placeWall(String gameId, String playerId, Wall wall) {
        Game game = getGame(gameId);
        validatePlayerTurn(game, playerId);
        if (!isValidWallPlacement(gameId, playerId, wall)) {
            throw new IllegalArgumentException("Invalid wall placement");
        }
        
        // 放置墙壁逻辑
        Player player = findPlayer(game, playerId);
        placeWallOnBoard(game, wall);
        
        // 更新游戏状态
        updateGameState(game);
        return gameRepository.save(game);
    }

    @Override
    public Game executeLaser(String gameId, String playerId) {
        Game game = getGame(gameId);
        validatePlayerTurn(game, playerId);
        Player player = findPlayer(game, playerId);
        
        // 执行激光逻辑
        executeLaserAction(game, player);
        
        // 更新游戏状态
        updateGameState(game);
        return gameRepository.save(game);
    }

    @Override
    public List<Card> drawCards(String gameId, String playerId, int count) {
        Game game = getGame(gameId);
        Player player = findPlayer(game, playerId);
        
        // 抽卡逻辑
        List<Card> drawnCards = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            drawnCards.add(new Card(getRandomCardType()));
        }
        
        player.getHand().addAll(drawnCards);
        gameRepository.save(game);
        return drawnCards;
    }

    @Override
    public void discardCards(String gameId, String playerId, List<Card> cards) {
        Game game = getGame(gameId);
        Player player = findPlayer(game, playerId);
        
        // 弃牌逻辑
        player.getHand().removeAll(cards);
        gameRepository.save(game);
    }

    @Override
    public boolean isGameOver(String gameId) {
        Game game = getGame(gameId);
        return game.getState() == GameState.FINISHED;
    }

    @Override
    public boolean isValidMove(String gameId, String playerId, List<Card> cards) {
        Game game = getGame(gameId);
        Player player = findPlayer(game, playerId);
        
        // 验证玩家是否拥有这些卡牌
        if (!player.getHand().containsAll(cards)) {
            return false;
        }
        
        // 模拟移动
        Position currentPos = player.getPosition();
        Direction currentDir = player.getDirection();
        
        for (Card card : cards) {
            switch (card.getType()) {
                case BLUE:
                    Position nextPos = currentDir.getNextPosition(currentPos);
                    if (!isValidPosition(game, nextPos)) {
                        return false;
                    }
                    currentPos = nextPos;
                    break;
                case YELLOW:
                    currentDir = currentDir.turnLeft();
                    break;
                case PURPLE:
                    currentDir = currentDir.turnRight();
                    break;
                case LASER:
                    // 激光卡可以在任何时候使用
                    break;
            }
        }
        
        return true;
    }

    private boolean isValidPosition(Game game, Position position) {
        // 检查是否在棋盘范围内
        if (!position.isValid()) {
            return false;
        }
        
        // 检查是否有墙壁
        Cell cell = game.getBoard().getCells()[position.getX()][position.getY()];
        if (cell.getContent().getType() == CellType.WALL) {
            return false;
        }
        
        // 检查是否有其他玩家
        return game.getPlayers().stream()
                .noneMatch(p -> p.getPosition().equals(position));
    }

    @Override
    public boolean isValidWallPlacement(String gameId, String playerId, Wall wall) {
        Game game = getGame(gameId);
        Player player = findPlayer(game, playerId);
        
        // 检查玩家是否还有这种类型的墙
        if (player.getWalls().stream()
                .noneMatch(w -> w.getType() == wall.getType())) {
            return false;
        }
        
        Position pos = wall.getPosition();
        // 检查位置是否有效
        if (!pos.isValid()) {
            return false;
        }
        
        // 检查目标位置是否为空
        Cell cell = game.getBoard().getCells()[pos.getX()][pos.getY()];
        if (cell.getContent().getType() != CellType.EMPTY) {
            return false;
        }
        
        // 检查是否会阻塞任何宝石
        return !wouldBlockJewel(game, wall);
    }

    private boolean wouldBlockJewel(Game game, Wall wall) {
        // 实现检查墙壁是否会阻塞宝石的逻辑
        // 这需要实现路径查找算法
        return false; // 临时返回
    }

    @Override
    public void endGame(String gameId) {
        gameRepository.deleteById(gameId);
    }

    @Override
    public void removeInactiveGames() {
        Instant cutoffTime = Instant.now().minus(Duration.ofMinutes(INACTIVE_GAME_TIMEOUT_MINUTES));
        gameRepository.findAll().stream()
                .filter(game -> game.getCreatedAt().isBefore(cutoffTime))
                .forEach(game -> gameRepository.deleteById(game.getId()));
    }

    // 辅助方法
    private void validateGameConfig(GameConfig config) {
        if (config.getPlayerCount() < 2 || config.getPlayerCount() > 4) {
            throw new IllegalArgumentException("Invalid player count");
        }
        if (config.getAiPlayerCount() >= config.getPlayerCount()) {
            throw new IllegalArgumentException("Too many AI players");
        }
    }

    private String generateGameCode() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private List<PlayerColor> getAvailableColors(Game game) {
        List<PlayerColor> usedColors = game.getPlayers().stream()
                .map(Player::getColor)
                .collect(Collectors.toList());
        List<PlayerColor> allColors = Arrays.asList(PlayerColor.values());
        return allColors.stream()
                .filter(color -> !usedColors.contains(color))
                .collect(Collectors.toList());
    }

    private Player createAIPlayer(PlayerColor color) {
        return new Player("AI-" + color.name(), color, PlayerType.AI);
    }

    private void initializeGameBoard(Game game) {
        Board board = game.getBoard();
        int playerCount = game.getPlayers().size();
        
        // 设置玩家初始位置
        for (Player player : game.getPlayers()) {
            resetPlayerPosition(game, player);
        }
        
        // 设置宝石位置
        if (playerCount == 2) {
            // 两人模式：宝石在中间
            placeJewel(board, 3, 3, PlayerColor.BLUE);
            placeJewel(board, 4, 4, PlayerColor.RED);
        } else {
            // 多人模式：宝石在对角
            placeJewel(board, 1, 1, PlayerColor.BLUE);
            placeJewel(board, 6, 6, PlayerColor.RED);
            if (playerCount > 2) {
                placeJewel(board, 1, 6, PlayerColor.GREEN);
            }
            if (playerCount > 3) {
                placeJewel(board, 6, 1, PlayerColor.PURPLE);
            }
        }
        
        // 初始化玩家手牌
        for (Player player : game.getPlayers()) {
            player.setHand(generateInitialHand());
            player.setWalls(generateInitialWalls());
        }
    }

    private void placeJewel(Board board, int x, int y, PlayerColor color) {
        board.getCells()[x][y].setContent(new CellContent(CellType.JEWEL, color));
    }

    private List<Card> generateInitialHand() {
        List<Card> hand = new ArrayList<>();
        // 添加初始卡牌：18蓝色，8黄色，8紫色，3激光
        for (int i = 0; i < 18; i++) hand.add(new Card(CardType.BLUE));
        for (int i = 0; i < 8; i++) hand.add(new Card(CardType.YELLOW));
        for (int i = 0; i < 8; i++) hand.add(new Card(CardType.PURPLE));
        for (int i = 0; i < 3; i++) hand.add(new Card(CardType.LASER));
        Collections.shuffle(hand);
        return hand;
    }

    private List<Wall> generateInitialWalls() {
        List<Wall> walls = new ArrayList<>();
        // 添加初始墙壁：3石墙，2冰墙
        for (int i = 0; i < 3; i++) walls.add(new Wall(WallType.STONE));
        for (int i = 0; i < 2; i++) walls.add(new Wall(WallType.ICE));
        return walls;
    }

    private void validatePlayerTurn(Game game, String playerId) {
        if (!playerId.equals(game.getCurrentPlayerId())) {
            throw new InvalidGameStateException("Not player's turn");
        }
    }

    private Player findPlayer(Game game, String playerId) {
        return game.getPlayers().stream()
                .filter(p -> p.getId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new GameNotFoundException("Player not found: " + playerId));
    }

    private void executeCard(Game game, Player player, Card card) {
        switch (card.getType()) {
            case BLUE:
                Position nextPos = player.getDirection().getNextPosition(player.getPosition());
                if (isValidPosition(game, nextPos)) {
                    player.setPosition(nextPos);
                } else {
                    // 如果遇到障碍，玩家转向
                    player.setDirection(player.getDirection().turnLeft().turnLeft());
                }
                break;
            case YELLOW:
                player.turnLeft();
                break;
            case PURPLE:
                player.turnRight();
                break;
            case LASER:
                executeLaserAction(game, player);
                break;
        }
        
        // 从玩家手牌中移除使用的卡牌
        player.getHand().remove(card);
    }

    private void executeLaserAction(Game game, Player player) {
        Position currentPos = player.getPosition();
        Direction direction = player.getDirection();
        final Position targetPos = currentPos;
        
        // 寻找激光射线第一个击中的目标
        while (true) {
            Position nextPos = direction.getNextPosition(targetPos);
            
            // 检查是否超出边界
            if (!isValidPosition(game, nextPos)) {
                break;
            }
            
            final Position finalTargetPos = nextPos;
            
            // 检查是否击中其他玩家
            Optional<Player> hitPlayer = game.getPlayers().stream()
                    .filter(p -> p.getPosition().equals(finalTargetPos))
                    .findFirst();
                    
            if (hitPlayer.isPresent()) {
                // 玩家被击中，重置位置
                resetPlayerPosition(game, hitPlayer.get());
                break;
            }
            
            // 检查是否击中宝石
            if (game.getBoard().getCells()[finalTargetPos.getX()][finalTargetPos.getY()].getContent().getType() == CellType.JEWEL) {
                // 激光被反射，玩家受到影响
                if (game.getPlayers().size() == 2) {
                    player.setDirection(player.getDirection().turnLeft().turnLeft());
                } else {
                    resetPlayerPosition(game, player);
                }
                break;
            }
        }
    }

    private void resetPlayerPosition(Game game, Player player) {
        // 根据玩家颜色设置起始位置
        switch (player.getColor()) {
            case BLUE:
                player.setPosition(new Position(0, 7));
                player.setDirection(Direction.NORTH);
                break;
            case RED:
                player.setPosition(new Position(7, 7));
                player.setDirection(Direction.NORTH);
                break;
            case GREEN:
                player.setPosition(new Position(0, 0));
                player.setDirection(Direction.SOUTH);
                break;
            case PURPLE:
                player.setPosition(new Position(7, 0));
                player.setDirection(Direction.SOUTH);
                break;
        }
    }

    private void placeWallOnBoard(Game game, Wall wall) {
        Player player = findPlayer(game, game.getCurrentPlayerId());
        
        // 从玩家的墙壁中移除一个相同类型的墙
        player.getWalls().removeIf(w -> w.getType() == wall.getType());
        
        // 在棋盘上放置墙
        Position pos = wall.getPosition();
        game.getBoard().getCells()[pos.getX()][pos.getY()]
            .setContent(new CellContent(CellType.WALL, wall));
    }

    private void updateGameState(Game game) {
        // 检查是否有玩家到达宝石
        for (Player player : game.getPlayers()) {
            Position pos = player.getPosition();
            Cell cell = game.getBoard().getCells()[pos.getX()][pos.getY()];
            if (cell.getContent().getType() == CellType.JEWEL) {
                PlayerColor jewelColor = (PlayerColor) cell.getContent().getDetails();
                if (jewelColor == player.getColor()) {
                    // 玩家到达了自己的宝石
                    game.setState(GameState.FINISHED);
                    return;
                }
            }
        }
        
        // 更新当前玩家
        int currentPlayerIndex = game.getPlayers().indexOf(
            findPlayer(game, game.getCurrentPlayerId()));
        int nextPlayerIndex = (currentPlayerIndex + 1) % game.getPlayers().size();
        game.setCurrentPlayerId(game.getPlayers().get(nextPlayerIndex).getId());
    }

    private CardType getRandomCardType() {
        return CardType.values()[new Random().nextInt(CardType.values().length)];
    }
} 
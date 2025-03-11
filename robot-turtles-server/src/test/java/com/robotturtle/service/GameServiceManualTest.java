package com.robotturtle.service;

import com.robotturtle.model.*;
import com.robotturtle.model.enums.*;
import com.robotturtle.repository.InMemoryGameRepository;
import com.robotturtle.service.impl.GameServiceImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GameServiceManualTest {
    private static GameService gameService;
    private static Game game;
    private static String player1Id;
    private static String player2Id;

    public static void main(String[] args) {
        setup();
        runGameLoop();
    }

    private static void setup() {
        // 初始化游戏服务
        gameService = new GameServiceImpl(new InMemoryGameRepository());

        // 创建游戏配置
        GameConfig config = new GameConfig();
        config.setPlayerCount(2);
        config.setAiPlayerCount(0);
        
        // 创建主机玩家
        Player hostPlayer = new Player("Player1", PlayerColor.BLUE, PlayerType.HUMAN);
        config.setHostPlayer(hostPlayer);

        // 创建游戏
        game = gameService.createGame(config);
        player1Id = game.getPlayers().get(0).getId();

        // 加入第二个玩家
        Player player2 = new Player("Player2", PlayerColor.RED, PlayerType.HUMAN);
        game = gameService.joinGame(game.getGameCode(), player2);
        player2Id = game.getPlayers().get(1).getId();

        // 开始游戏
        game = gameService.startGame(game.getId());
        System.out.println("游戏已创建并开始！");
        printGameState();
    }

    private static void runGameLoop() {
        Scanner scanner = new Scanner(System.in);
        while (!gameService.isGameOver(game.getId())) {
            String currentPlayerId = game.getCurrentPlayerId();
            Player currentPlayer = findPlayer(currentPlayerId);
            System.out.println("\n当前玩家: " + currentPlayer.getName() + " (" + currentPlayer.getColor() + ")");
            System.out.println("1. 执行移动");
            System.out.println("2. 放置墙壁");
            System.out.println("3. 发射激光");
            System.out.println("4. 查看游戏状态");
            System.out.println("5. 退出游戏");
            
            System.out.print("请选择操作 (1-5): ");
            int choice = scanner.nextInt();
            
            try {
                switch (choice) {
                    case 1:
                        executeMove(currentPlayerId);
                        break;
                    case 2:
                        placeWall(currentPlayerId);
                        break;
                    case 3:
                        game = gameService.executeLaser(game.getId(), currentPlayerId);
                        break;
                    case 4:
                        printGameState();
                        continue;
                    case 5:
                        System.out.println("游戏结束！");
                        return;
                    default:
                        System.out.println("无效的选择！");
                        continue;
                }
                printGameState();
            } catch (Exception e) {
                System.out.println("错误: " + e.getMessage());
            }
        }
        
        System.out.println("游戏结束！");
        printGameState();
    }

    private static void executeMove(String playerId) {
        Scanner scanner = new Scanner(System.in);
        List<Card> cards = new ArrayList<>();
        
        System.out.println("可用的卡牌类型：");
        System.out.println("1. 蓝卡 (前进)");
        System.out.println("2. 黄卡 (左转)");
        System.out.println("3. 紫卡 (右转)");
        
        System.out.print("请输入要使用的卡牌数量: ");
        int cardCount = scanner.nextInt();
        
        for (int i = 0; i < cardCount; i++) {
            System.out.print("请选择卡牌类型 (1-3): ");
            int cardType = scanner.nextInt();
            switch (cardType) {
                case 1:
                    cards.add(new Card(CardType.BLUE));
                    break;
                case 2:
                    cards.add(new Card(CardType.YELLOW));
                    break;
                case 3:
                    cards.add(new Card(CardType.PURPLE));
                    break;
                default:
                    System.out.println("无效的卡牌类型！");
                    i--;
                    break;
            }
        }
        
        game = gameService.executeMove(game.getId(), playerId, cards);
    }

    private static void placeWall(String playerId) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("墙壁类型：");
        System.out.println("1. 石墙");
        System.out.println("2. 冰墙");
        
        System.out.print("请选择墙壁类型 (1-2): ");
        int wallType = scanner.nextInt();
        
        System.out.print("请输入放置位置 X (0-7): ");
        int x = scanner.nextInt();
        System.out.print("请输入放置位置 Y (0-7): ");
        int y = scanner.nextInt();
        
        Wall wall = new Wall();
        wall.setType(wallType == 1 ? WallType.STONE : WallType.ICE);
        wall.setPosition(new Position(x, y));
        
        game = gameService.placeWall(game.getId(), playerId, wall);
    }

    private static void printGameState() {
        System.out.println("\n当前游戏状态:");
        System.out.println("游戏ID: " + game.getId());
        System.out.println("游戏代码: " + game.getGameCode());
        System.out.println("游戏状态: " + game.getState());
        
        System.out.println("\n玩家信息:");
        for (Player player : game.getPlayers()) {
            System.out.println(String.format(
                "%s (%s) - 位置: %s, 方向: %s",
                player.getName(),
                player.getColor(),
                player.getPosition(),
                player.getDirection()
            ));
        }
        
        System.out.println("\n棋盘状态:");
        Board board = game.getBoard();
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Position pos = new Position(x, y);
                boolean isPlayer = false;
                String playerSymbol = ".";
                
                // 检查是否有玩家在这个位置
                for (Player player : game.getPlayers()) {
                    if (player.getPosition() != null && 
                        player.getPosition().getX() == x && 
                        player.getPosition().getY() == y) {
                        isPlayer = true;
                        playerSymbol = player.getColor().name().substring(0, 1);
                        break;
                    }
                }
                
                if (isPlayer) {
                    System.out.print(playerSymbol);
                } else {
                    Cell cell = board.getCells()[x][y];
                    CellType cellType = cell.getContent().getType();
                    if (cellType == CellType.EMPTY) {
                        System.out.print(".");
                    } else if (cellType == CellType.WALL) {
                        Wall wall = (Wall) cell.getContent().getDetails();
                        System.out.print(wall.getType() == WallType.ICE ? "I" : "S");
                    } else if (cellType == CellType.JEWEL) {
                        PlayerColor jewelColor = (PlayerColor) cell.getContent().getDetails();
                        System.out.print("*");
                    }
                }
                System.out.print(" ");
            }
            System.out.println();
        }
        
        // 显示玩家手牌
        System.out.println("\n玩家手牌:");
        for (Player player : game.getPlayers()) {
            System.out.println(player.getName() + " 的手牌:");
            List<Card> hand = player.getHand();
            if (hand != null && !hand.isEmpty()) {
                for (Card card : hand) {
                    System.out.print(card.getType().name() + " ");
                }
                System.out.println();
            } else {
                System.out.println("无手牌");
            }
        }
    }

    private static Player findPlayer(String playerId) {
        return game.getPlayers().stream()
                .filter(p -> p.getId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Player not found: " + playerId));
    }
} 
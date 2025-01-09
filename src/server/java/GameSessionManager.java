import java.util.HashMap;
import java.util.Map;

public class GameSessionManager {
    private static GameSessionManager instance;
    private HanahudaGameLogic gamelogic = new HanahudaGameLogic();
    private ClientSessionManager clientSessionManager; // セッション管理用
    private Map<String, Integer> clientSessions = new HashMap<>(); // セッションIDとプレイヤー番号
    private Map<String, Boolean> clientReadyStates = new HashMap<>(); // クライアントの準備状態
    private boolean gameStarted = false;

    private int maxClients = 2;
    public GameSessionManager(ClientSessionManager clientSessionManager) {
        this.clientSessionManager = clientSessionManager;
        this.clientSessions = clientSessionManager.getSessions();
    System.out.println("初期化時のセッション: " + clientSessions);
    }
    public static synchronized GameSessionManager getInstance(ClientSessionManager clientSessionManager) {
        if (instance == null) {
            instance = new GameSessionManager(clientSessionManager);
        }
        return instance;
    }

    public synchronized String addClient(String sessionId) {
        if (!clientSessionManager.isValidSession(sessionId)) {
            return "ERROR: セッションが無効です";
        }
        if (clientSessions.size() >= maxClients) {
            return "FULL"; // サーバーが満員の場合
        }
        int playerNumber = clientSessions.size() + 1;
        clientSessions.put(sessionId, playerNumber);
        clientReadyStates.put(sessionId, false); // 初期状態は未準備
        System.err.println("プレイヤー " + playerNumber + " が接続しました。");
        return "PLAYER_NUMBER:" + playerNumber;
    }
    public synchronized String setClientReady(String sessionId) {
        if (!clientSessions.containsKey(sessionId)) {
            return "ERROR: セッションが見つかりません";
        }
        clientReadyStates.put(sessionId, true);
        if (areAllClientsReady()) {
            startGame(sessionId); // 全員準備完了したらゲーム開始
            return "GAME_STARTED";
        }
        return "WAITING_FOR_OTHER_PLAYERS";
    }

    public synchronized String startGame(String sessionId) {
        if (gameStarted) {
            return "ERROR: ゲームはすでに開始しています";
        }
        gameStarted = true;
        // ゲーム開始処理
        gamelogic.resetGame(); // ゲームロジックに委譲
        getGameState(sessionId);
        
        System.out.println("全プレイヤーが準備完了。ゲームを開始します！");
        return "ゲームが開始されました";
        
    }
    private Integer getPlayerNumber(String sessionId) {
        return clientSessions.get(sessionId);
    }
    private boolean areAllClientsReady() {
        return clientReadyStates.size() == 2 && clientReadyStates.values().stream().allMatch(ready -> ready);
    }
    public synchronized String processMessage(String sessionId, String message) {
        System.out.println("受信したセッションID: " + sessionId);
        System.out.println("現在登録されているセッション: " + clientSessions);
        
        Integer playerNumber = clientSessionManager.getPlayerIndex(sessionId);
        if (playerNumber == null) {
            return "ERROR: セッションが見つかりません";
        }

        if (message.startsWith("PLAY_CARD")) {
            String[] parts = message.split(":");
        if (parts.length != 3) {
            return "ERROR: 無効なメッセージ形式";
        }

        String cardInfo = parts[1];
        int playerId = Integer.parseInt(parts[2]);

        // プレイヤーIDを検証（セッションに対応しているか確認）
        if (playerId != playerNumber) {
            return "ERROR: プレイヤーIDが一致しません";
        }

        return gamelogic.processPlayerAction(playerId - 1, "PLAY_CARD:" + cardInfo);
        }

        return "ERROR: 未知のコマンド";
    }
    public synchronized String getGameState(String sessionId) {
        Game game = gamelogic.getGame();
        Integer playerNumber = clientSessions.get(sessionId);
        if (playerNumber == null) {
            return "ERROR: セッションが見つかりません";
        }
        if (gamelogic.isGameFinished()) {
            return "ゲーム終了: プレイヤー " + playerId + " が勝利しました！";
        }
        

        StringBuilder gameState = new StringBuilder();
        gameState.append("Field: ");
        for (Card card : game.getField().getCards()) {
            gameState.append(card.getId()).append(",");
        }
        gameState.append("\n");

        for (int i = 0; i < game.getPlayers().size(); i++) {
            gameState.append("Player").append(i + 1).append(" : ");
                for (Card card : game.getPlayers().get(i).getHand()) {
                    gameState.append(card.getId()).append(",");
                }
            gameState.append("\n");
        }
        gameState.append("現在のターン: ").append(game.getNowTurn()).append("\n");

        return gameState.toString();
}
public synchronized int getPlayerId(String sessionId) {
    Integer playerNumber = clientSessions.get(sessionId);
    if (playerNumber == null) {
        throw new IllegalArgumentException("セッションが見つかりません: " + sessionId);
    }
    return playerNumber;
}
public synchronized boolean removeSession(String sessionId) {
    Integer playerNumber = clientSessions.remove(sessionId); // セッションを削除
    clientSessionManager.removeSession(sessionId);
    if (playerNumber != null) {
        System.out.println("セッションID " + sessionId + " のプレイヤー " + playerNumber + " を削除しました。");
        return true;
    } else {
        System.out.println("セッションID " + sessionId + " は見つかりませんでした。");
        return false;
    }
}
}

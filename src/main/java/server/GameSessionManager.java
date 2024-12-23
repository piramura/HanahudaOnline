import java.util.HashMap;
import java.util.Map;

public class GameSessionManager {
    private HanahudaGameLogic gamelogic = new HanahudaGameLogic();
    private ClientSessionManager clientSessionManager; // セッション管理用
    private Map<String, Integer> clientSessions = new HashMap<>(); // セッションIDとプレイヤー番号
    private int maxClients = 2;
    public GameSessionManager(ClientSessionManager clientSessionManager) {
        this.clientSessionManager = clientSessionManager;
        this.clientSessions = clientSessionManager.getSessions();
        System.out.println("初期化時のセッション: " + clientSessions);
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
        System.out.println("プレイヤー " + playerNumber + " が接続しました。");
        // プレイヤーが全員揃った場合、ゲームを開始
        if (clientSessions.size() == maxClients) {
            startGame();
        }
        return "PLAYER_NUMBER:" + playerNumber;
    }

    public synchronized void startGame() {
        System.out.println("ゲームが開始されました。");
        gamelogic.resetGame(); // ゲームロジックに委譲
    }

    public synchronized String processMessage(String sessionId, String message) {
        System.out.println("受信したセッションID: " + sessionId);
        System.out.println("現在登録されているセッション: " + clientSessions);
        
        Integer playerNumber = clientSessionManager.getPlayerIndex(sessionId);
        if (playerNumber == null) {
            return "ERROR: セッションが見つかりません";
        }

        if (message.startsWith("PLAY_CARD")) {
            int clientIndex = playerNumber - 1; // プレイヤー番号をインデックスに変換
            String response = gamelogic.processPlayerAction(clientIndex, message);
            return response;
        }

        return "ERROR: 未知のコマンド";
    }
    public synchronized String getGameState(String sessionId) {
        Integer playerNumber = clientSessions.get(sessionId);
        if (playerNumber == null) {
            return "ERROR: セッションが見つかりません";
        }

        // プレイヤー固有のゲーム状態を返す（簡易化例）
        return "Game state for player " + playerNumber;
    }
}

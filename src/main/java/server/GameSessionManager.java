import java.util.HashMap;
import java.util.Map;

public class GameSessionManager {
    private HanahudaGameLogic gameManager = new HanahudaGameLogic();
    private Map<String, Integer> clientSessions = new HashMap<>(); // セッションIDとプレイヤー番号
    private int maxClients = 2;

    public synchronized String addClient(String sessionId) {
        if (clientSessions.size() >= maxClients) {
            return "FULL"; // サーバーが満員の場合
        }
        int playerNumber = clientSessions.size() + 1;
        clientSessions.put(sessionId, playerNumber);
        System.out.println("プレイヤー " + playerNumber + " が接続しました。");
        if (clientSessions.size() == maxClients) {
            startGame();
        }
        return "PLAYER_NUMBER:" + playerNumber;
    }

    public synchronized void startGame() {
        gameManager.startGame();
        System.out.println("ゲームが開始されました。");
    }

    public synchronized String processMessage(String sessionId, String message) {
        System.out.println("受信したセッションID: " + sessionId);
        System.out.println("現在登録されているセッション: " + clientSessions);
        
        Integer playerNumber = clientSessions.get(sessionId);
        if (playerNumber == null) {
            return "ERROR: セッションが見つかりません";
        }

        if (message.startsWith("PLAY_CARD")) {
            int clientIndex = playerNumber - 1; // プレイヤー番号をインデックスに変換
            String response = gameManager.processPlayerAction(clientIndex, message);
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

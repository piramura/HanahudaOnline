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
    //プレイヤー情報を保持するマップ
    private Map<Integer, PlayerInfo> playerInfoMap = new HashMap<>();
    
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
    public synchronized PlayerInfo getPlayerInfo(int playerId) {
        return playerInfoMap.get(playerId);
    }
    
    public synchronized PlayerInfo getOpponentInfo(int playerId) {
        int opponentId = (playerId == 1) ? 2 : 1; // 2人用ゲームを想定
        return playerInfoMap.get(opponentId);
    }
    
    //プレイヤー情報を追加
    public synchronized void setPlayerInfo(int playerId, String playerName, int iconNum, int level) {
        PlayerInfo info = new PlayerInfo(playerName, iconNum, level);
        playerInfoMap.put(playerId, info);
        System.out.println("Player info updated: " + info);
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
        //先行後攻をランダムで決める
        //どうしよ。
        getGameState(sessionId);
        
        System.out.println("全プレイヤーが準備完了。ゲームを開始します！");
        return "ゲームが開始されました";
        
    }
    private Integer getPlayerNumber(String sessionId) {
        return clientSessions.get(sessionId);
    }
    private boolean areAllClientsReady() {
        System.out.println("clientReadyStates.size(: " + clientReadyStates.size());
        return clientReadyStates.size() == 2 && clientReadyStates.values().stream().allMatch(ready -> ready);
    }
    public synchronized String processMessage(String sessionId, String message) {
        System.out.println("受信したセッションID: " + sessionId);
        System.out.println("現在登録されているセッション: " + clientSessions);
        
        Integer playerNumber = clientSessionManager.getPlayerIndex(sessionId);
        if (playerNumber == null) {
            return "ERROR: セッションが見つかりません";
        }

        // PLAY_CARD コマンドの処理
        if (message.startsWith("PLAY_CARD")) {
            String[] parts = message.split(":");
            if (parts.length != 4) { // フォーマット: PLAY_CARD:<手札のカードID>:<場のカードID>:<プレイヤーID>
                return "ERROR: 無効なメッセージ形式";
            }

            int cardInfo = Integer.parseInt(parts[1]); // 手札のカードID
            int fieldCardInfo = Integer.parseInt(parts[2]); // 場のカードID
            int playerId = Integer.parseInt(parts[3]); // プレイヤーID

            // プレイヤーIDを検証（セッションに対応しているか確認）
            if (playerId != playerNumber) {
                return "ERROR: プレイヤーIDが一致しません";
            }
            // ゲームロジックにカード情報を渡す
            return gamelogic.processPlayerAction(playerId - 1, cardInfo,fieldCardInfo);
        }
        return "ERROR: 未知のコマンド";
    }
    public String handleKoiKoiResponse(int playerIndex, boolean isKoiKoi) {
        if (isKoiKoi) {
            System.out.println("プレイヤー " + (playerIndex + 1) + " が「こいこい」を選択しました。");
            return "NEXT_TURN: 「こいこい」を選択しました。次のターンに進みます。";
        } else {
            System.out.println("プレイヤー " + (playerIndex + 1) + " がゲームを終了しました。");
            return "GAME_END: ゲーム終了！勝者: " + gamelogic.determineWinner();
        }
    }
    public synchronized String getGameState(String sessionId) {
        Game game = gamelogic.getGame();
        Integer playerNumber = clientSessions.get(sessionId);
        if (playerNumber == null) {
            return "ERROR: セッションが見つかりません";
        }
        if (gamelogic.isGameFinished()) {
            return "ゲーム終了: プレイヤー " + playerNumber+ " が勝利しました！";
        }
        

        StringBuilder gameState = new StringBuilder();
        gameState.append("Field: ");
        for (Card card : game.getField().getCards()) {
            gameState.append(card.getId() -1).append(",");
        }
        gameState.append("\n");

        for (int i = 0; i < game.getPlayers().size(); i++) {
            gameState.append("PlayerHand").append(i + 1).append(" : ");
                for (Card card : game.getPlayers().get(i).getHand()) {
                    gameState.append(card.getId() -1 ).append(",");
                }
            gameState.append("\n");
            gameState.append("PlayerCaptures").append(i + 1).append(" : ");
                for (Card card : game.getPlayers().get(i).getCaptures()) {
                    gameState.append(card.getId() -1 ).append(",");
                }
            gameState.append("\n");
        }
        gameState.append("現在のターン: ").append(game.getNowTurn()).append("\n");
        // 「こいこい」の状態を追加
        gameState.append("KoiKoiStates: ");
        gameState.append(game.getAllKoiKoiStates());
        gameState.append("\n");
        
        System.out.println("DEBUG ゲーム状態: " + gameState);
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
            // ➡️ すべてのセッションが削除された場合
            if (clientSessions.isEmpty()) {
                System.out.println("すべてのプレイヤーが切断されました。ゲームセッションをリセットします。");
                resetAllStates(); // すべての状態をリセット
                HanahudaServer.resetGameSession();
            }
            return true;
        } else {
            System.out.println("セッションID " + sessionId + " は見つかりませんでした。");
            return false;
        }
    }
    // ➡️ ゲームセッションのすべての状態をリセット
    private void resetAllStates() {
        clientSessions.clear();
        clientReadyStates.clear();
        gameStarted = false;
        System.out.println("すべてのゲーム状態がリセットされました。");
    }
    //ゲーム進行
    public void nextTurn(){
        gamelogic.nextTurn();
    }
    public void endGame(){
        gamelogic.endGame();
    }
}

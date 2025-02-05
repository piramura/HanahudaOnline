import java.util.*;
public class GameSessionManager {

    private HanahudaGameLogic gamelogic = new HanahudaGameLogic();
    private Map<String, Integer> clientSessions = new HashMap<>(); // セッションIDとプレイヤー番号
    private Map<String, Boolean> clientReadyStates = new HashMap<>(); // クライアントの準備状態
    private Set<String> botSessions = new HashSet<>(); // BOT戦を管理するセッションセット
    private boolean gameStarted = false;
    private int maxClients = 2;
    private Map<Integer, PlayerInfo> playerInfoMap = new HashMap<>();//プレイヤー情報を保持するマップ

    // BOT戦のセッションを登録
    public synchronized void startBotMatch(String sessionId) {
        botSessions.add(sessionId);
        clientSessions.put(sessionId, 1); // プレイヤー1として登録
        clientSessions.put("BOT", 2); // BOTをプレイヤー2として登録
        clientReadyStates.put(sessionId, false);
        clientReadyStates.put("BOT", true);
        gamelogic.setBotMode(true);
        System.out.println("BOT戦のセッションを登録しました: " + sessionId);
    }
    public boolean isValidSession(String sessionId) {return clientSessions.containsKey(sessionId);}// セッションIDが有効か確認
    public boolean isBotMatch(String sessionId) {return botSessions.contains(sessionId);}//BOT戦かどうかを判定
    public synchronized PlayerInfo getPlayerInfo(int playerId) {return playerInfoMap.get(playerId);}//自分のの情報をゲット
    
    public synchronized PlayerInfo getOpponentInfo(int playerId) {
        int opponentId = (playerId == 1) ? 2 : 1; // 2人用ゲームを想定
        if (!playerInfoMap.containsKey(opponentId)) {
            System.out.println("対戦相手が見つかりません: playerId=" + playerId);
            return new PlayerInfo("Computer", 0, 1); // 仮のデフォルトデータを返す
        }
        return playerInfoMap.get(opponentId);
    }
    public synchronized String getGameResult(String sessionId) {
        Game game = gamelogic.getGame();
        Integer playerNumber = clientSessions.get(sessionId);
        if (playerNumber == null) {
            return "ERROR: セッションが見つかりません";
        }
        return gamelogic.determineWinner();
    }
    
    
    //プレイヤー情報を追加
    public synchronized void setPlayerInfo(int playerId, String playerName, int iconNum, int level) {
        PlayerInfo info = new PlayerInfo(playerName, iconNum, level);
        playerInfoMap.put(playerId, info);
        System.out.println("Player info updated: " + info);
    }
    
    public synchronized String addClient(String sessionId, boolean isBotMatch) {
        if (clientSessions.size() >= maxClients && !isBotMatch) {
            return "FULL"; // 通常のオンライン対戦では満員チェック
        }
    
        if (isBotMatch) {
            startBotMatch(sessionId);
            return "BOT_MATCH_STARTED";
        }
        Random rand = new Random();
        int playerNumber;

        if (clientSessions.isEmpty()) {
            playerNumber = rand.nextInt(2) + 1; // 最初のプレイヤーは 1 または 2 をランダムに決定
        } else if (clientSessions.containsValue(1)) {
            playerNumber = 2; // すでにプレイヤー1がいる場合、プレイヤー2を割り当て
        } else {
            playerNumber = 1; // すでにプレイヤー2がいる場合、プレイヤー1を割り当て
        }
        clientSessions.put(sessionId, playerNumber);
        clientReadyStates.put(sessionId, false);
        System.out.println("プレイヤー " + playerNumber + " が接続しました。");
    
        return "PLAYER_NUMBER:" + playerNumber;
    }
    
    public synchronized String setClientReady(String sessionId) {
        if (!clientSessions.containsKey(sessionId)) {
            return "ERROR: セッションが見つかりません";
        }
        clientReadyStates.put(sessionId, true);
        if (areAllClientsReady() || isBotMatch(sessionId)) {
            startGame(sessionId); // 全員準備完了したらゲーム開始
            return "GAME_STARTED";
        }
        return "WAITING_FOR_OTHER_PLAYERS";
    }

    public synchronized String startGame(String sessionId) {
        if (gameStarted) {
            return "ERROR: ゲームはすでに開始しています";
        }
        gameStarted = true;// ゲーム開始処理
        gamelogic.resetGame(); // ゲームロジックに委譲
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
        
        Integer playerNumber = clientSessions.get(sessionId);
        if (playerNumber == null) {
            return "ERROR: セッションが見つかりません";
        }
        System.out.println("processMessageを処理中");
        System.out.println(message);
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
            return gamelogic.processPlayerAction(playerId, cardInfo,fieldCardInfo);
        }
        return "ERROR: 未知のコマンド";
    }
    public String handleKoiKoiResponse(int playerIndex, boolean isKoiKoi) {
        if (isKoiKoi) {
            System.out.println("プレイヤー " + (playerIndex + 1) + " が「こいこい」を選択しました。");
            return "NEXT_TURN: 「こいこい」を選択しました。次のターンに進みます。";
        } else {
            System.out.println("プレイヤー " + (playerIndex + 1) + " がゲームを終了しました。");
            return "GAME_END: ゲーム終了！";
        }
    }
    public synchronized String getGameState(String sessionId) {
        Game game = gamelogic.getGame();
        if(game == null){
            System.out.println("EROOR Game == null");
            return "EROOR";
        }
        Integer playerNumber = clientSessions.get(sessionId);
        if (playerNumber == null) {
            return "ERROR: セッションが見つかりません";
        }
        if (gamelogic.isGameFinished()) {
            return "GAME_END: gamelogic.isGameFinished()！";
        }
        

        StringBuilder gameState = new StringBuilder();
        gameState.append("Field: ");
        for (Card card : game.getField().getCards()) {
            gameState.append(card.getId()).append(",");
        }
        gameState.append("\n");

        for (int i = 0; i < game.getPlayers().size(); i++) {
            gameState.append("PlayerHand").append(i + 1).append(" : ");
                for (Card card : game.getPlayers().get(i).getHand()) {
                    gameState.append(card.getId() ).append(",");
                }
            gameState.append("\n");
            gameState.append("PlayerCaptures").append(i + 1).append(" : ");
                for (Card card : game.getPlayers().get(i).getCaptures()) {
                    gameState.append(card.getId()).append(",");
                }
            gameState.append("\n");
        }
        //
        gameState.append("現在のターン: ").append(game.getCurrentTurn()).append("\n");
        gameState.append("\n");
        
        //System.out.println("DEBUG ゲーム状態: " + gameState);
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
        System.out.println("DEBUG:  GameSessionManagerでnextTurn()が呼ばれました");
        System.out.println("DEBUG: gamelogic.isKoiKoiWaiting()"+gamelogic.isKoiKoiWaiting());
        if(gamelogic.isKoiKoiWaiting()){
            System.out.println("DEBUG: こいこい待機中なので待て");
        }else{
            gamelogic.nextTurn();
        }
        
    }
    public void endGame(){
        gamelogic.endGame();
    }
    public void processComputerTurn(){
        System.out.println("コンピュータの処理中");
    }
    public void resetKoiKoiWaiting(){
        gamelogic.resetIsKoiKoiWaiting();
        System.out.println("こいこいするときのこいこい待機状態を解除");
    }
    public synchronized void resetKoiKoi(String sessionId) {
        Integer playerNumber = clientSessions.get(sessionId);
        if (playerNumber == null) {
            System.err.println("ERROR: セッションID " + sessionId + " に対応するプレイヤーが見つかりません");
            return;
        }
        Player player = gamelogic.getGame().getPlayers().get(playerNumber - 1);
        player.setKoiKoi(false);
        gamelogic.resetIsKoiKoiWaiting(); // こいこい待機状態を解除
        System.out.println("勝負あった時のこいこい状態をリセット: プレイヤー " + playerNumber);
    }
    
}

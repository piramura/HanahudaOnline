import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
public class GameController {
    //private OnlineGame onlineGame;
    private GameClient gameClient;
    private List<Integer> field;
    private List<Integer> player1Hands;
    private List<Integer> player2Hands;
    private List<Integer> player1Captures;
    private List<Integer> player2Captures;
    private PlayerInfo selfInfo;
    private PlayerInfo opponentInfo;
    private int currentTurn;
    private int playerId; // 自分のプレイヤーIDを管理
    private int elapsedTime = 0;
    private boolean isActive = false;
    private int playCount;
    private boolean isKoikoi = false;
    private boolean isEnd;
    private String resultData;
    private boolean botMode;
    public void setResult(String result) {
        this.resultData = result;
    }
    public String getResult(){
        return resultData;
    }

    public String getResultFromController() {
        return resultData;
    }

    

    public List<Integer> getField() {
        return field;
    }

    public void setField(List<Integer> field) {
        this.field = field;
    }

    public List<Integer> getPlayer1Hands() {
        return player1Hands;
    }
    
    public List<Integer> getPlayer1Captures() {
        return player1Captures;
    }
    public List<Integer> getPlayer2Hands() {
        return player2Hands;
    }
    public List<Integer> getPlayer2Captures() {
        return player2Captures;
    }

    public boolean getIsKoikoi(){
        return isKoikoi;
    }
    public boolean getBotMode(){
        return botMode;
    }
    public void setBotMode(boolean botMode){
        this.botMode = botMode;
    }

    public void setPlayer1Hands(List<Integer> player1Hands) {
        this.player1Hands = player1Hands;
    }
    public void setPlayer1Captures(List<Integer> player1Captures) {
        this.player1Captures = player1Captures;
    }
    public void setPlayer2Hands(List<Integer> player2Hands) {
        this.player2Hands = player2Hands;
    }
    public void setPlayer2Captures(List<Integer> player2Captures) {
        this.player2Captures = player2Captures;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(int currentTurn) {
        this.currentTurn = currentTurn;
    }

    public void setIsKoikoi(boolean isKoikoi) {
        this.isKoikoi = isKoikoi;
    }
    public void setIsEnd(boolean isEnd){
        this.isEnd = isEnd;
    }
    public boolean getIsEnd(){
        return isEnd;
    }

    private PlayerInfo parsePlayerInfo(String playerInfoString) {
        //System.out.println("[DEBUG] サーバーから受信したプレイヤー情報: " + playerInfoString);
        String cleaned = playerInfoString.replace("PlayerInfo{", "").replace("}", "");
        // 各項目を分割
        String[] parts = playerInfoString.split(", ");
        if (parts.length < 3) {
            throw new IllegalArgumentException("プレイヤー情報が不完全です: " + playerInfoString);
        }
        // 各項目を解析
        String playerName = parts[0].split(": ")[1].trim(); // Name
        int iconNum = Integer.parseInt(parts[1].split(": ")[1].trim()); // Icon
        int level = Integer.parseInt(parts[2].split(": ")[1].trim()); // Level

        return new PlayerInfo(playerName, iconNum, level);
    }

    public PlayerInfo getSelfInfo() {
        return selfInfo;
    }

    public PlayerInfo getOpponentInfo() {
        return opponentInfo;
    }
    public void fetchAndSetPlayerInfo(String response) {
        try {
            // レスポンスを解析して情報を設定
            String[] infos = response.split("\n"); // 自分と相手の情報が改行で分かれていると仮定
            for (String info : infos) {
                if (info.contains("Your Info")) {
                    selfInfo = parsePlayerInfo(info.replace("Your Info - ", ""));
                } else if (info.contains("Opponent Info")) {
                    opponentInfo = parsePlayerInfo(info.replace("Opponent Info - ", ""));
                }
            }

        } catch (Exception e) {
            System.err.println("[ERROR] プレイヤー情報の取得に失敗しました: " + e.getMessage());
        }
    }
    public void parseGameState(String rawGameState) {
        
        // System.out.println("[DEBUG] ゲーム状態: " + rawGameState);
        String[] lines = rawGameState.split("\n");
        for (String line : lines) {
            // System.out.println("[DEBUG] 処理中の行: " + line);
            if (line.startsWith("Field:")) {
                setField(parseCardList(line.substring(7)));
            } else if (line.startsWith("PlayerHand1 :")) {
                handlePlayerHands(1, line.substring(12));
            } else if (line.startsWith("PlayerCaptures1 :")) {
                handlePlayerCaptures(1, line.substring(17));
            } else if (line.startsWith("PlayerHand2 :")) {
                handlePlayerHands(2, line.substring(12));
            } else if (line.startsWith("PlayerCaptures2 :")) {
                handlePlayerCaptures(2, line.substring(17));
            } else if (line.startsWith("現在のターン:")) {
                setCurrentTurn(Integer.parseInt(line.substring(7).trim()));
            }
        }//refreshUI();
    }
    private void handlePlayerHands(int playerNumber, String cardData) {
        List<Integer> hands = parseCardList(cardData);
        if (playerId == playerNumber) {
            setPlayer1Hands(hands);
        } else {
            setPlayer2Hands(hands);
        }
    }


    private List<Integer> parseCardList(String cardString) {
        List<Integer> cardList = new ArrayList<>();
        String[] cardNumbers = cardString.split(",");
        for (String card : cardNumbers) {
            try {
                // 数字以外の文字列を削除
                String cleanedCard = card.trim().replaceAll("[^0-9]", "");
                if (!cleanedCard.isEmpty()) {
                    cardList.add(Integer.parseInt(cleanedCard));
                }
            } catch (NumberFormatException e) {
                System.err.println("[WARN] 不正なカードデータをスキップ: " + card);
            }
        }
        return cardList;
    }
    private void handlePlayerCaptures(int playerNumber, String cardData) {
        List<Integer> captures = parseCardList(cardData);
        if (1 == playerNumber) {
            setPlayer1Captures(captures);
            // System.out.println("[DEBUG] 自分の取得カード (Player" + playerNumber + "): " + captures);
        } else {
            setPlayer2Captures(captures);
            // System.out.println("[DEBUG] 相手の取得カード (Player" + playerNumber + "): " + captures);
        }
    }
    public void setGameClient(GameClient gameClient) {
        this.gameClient = gameClient;
    }

    public GameClient getGameClient() {
        return gameClient;
    }

    public int getElapsedTime() {
        return elapsedTime / 1000;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public int getPlayerId() {
        return playerId;
    }
    
    
    
    
    // オンライン対戦開始
    public void startOnlineMatch() {
        try {
            boolean sessionInitialized =false;
            int maxAttempts = 10;
            int attempt = 0;

            while (!sessionInitialized && attempt < maxAttempts) {
                attempt++;
                try {
                    if(!botMode){
                        System.out.println("Online");
                        gameClient.initializeSession("default_passphrase");
                        sessionInitialized = true;
                    }else{
                        System.out.println("Computer");
                        gameClient.initializeSession("default_passphrase: BOT");
                        sessionInitialized = true;
                    }
                    System.out.println("セッションが正常に初期化されました！");
                } catch (Exception e) {
                    System.err.println("セッションの初期化に失敗しました (試行回数: " + attempt + "): " + e.getMessage());
                    if (attempt == maxAttempts) {
                        System.err.println("最大試行回数に達しました。処理を終了します。");
                        break;
                    }
                    try {
                        System.out.println("5秒後に再試行します...");
                        Thread.sleep(5000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            playerId = gameClient.fetchPlayerId(); // 自分のPlayerIDを取得
            System.out.println("現在の Player ID: " + playerId);
            
            final long maxTime = 5 * 60 * 1000;
            isActive = false;
            Timer timer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        elapsedTime += 1000;
                        if (elapsedTime >= maxTime) {
                            System.out.println("5分が経過したため、セッションを切断します...");
                            ((Timer) e.getSource()).stop();
                            gameClient.disconnect();
                            elapsedTime = 0;
                            isActive = true;
                            return;
                        }
                        if(gameClient.isGameStarted()) {
                            ((Timer) e.getSource()).stop();
                            System.out.println("ゲームが開始されました!");
                            gameClient.fetchPlayerInfo();
                            playCount=0;
                        } else {
                            System.out.println("ゲームが開始されるのを待っています...");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        ((Timer) e.getSource()).stop();
                    }
                }
            });
            timer.start();
        } catch (Exception e) {
            System.err.println("セッションの初期化に失敗しました: " + e.getMessage());
        }
    }
    public void fetchState(){
        try {
            gameClient.fetchGameStateAsync();
        }catch(Exception e){
            System.err.println("fetchStateにエラーが発生しました: " + e.getMessage());
                e.printStackTrace();
        }
        
    }
    public void startPlayCardTest() {
            try {
                playCount++;
                if (gameClient == null) {
                    System.out.println("ゲームコントローラーまたはクライアントが未初期化です。");
                    return;
                }
                System.out.println("プレイヤーID"+playerId);
                if((currentTurn % 2) == playerId % 2 ){
                    System.out.println("あなたのターンではありません"+currentTurn);
                    return;
                }
                if(playCount >= 2){
                    System.out.println("あなたのターンではありません"+currentTurn);
                }
                // 手札と場札を取得
                List<Integer> hand = player1Hands;
                List<Integer> fields = field;
                int selectedHandCardId = -1;
                if(playCount == 1){
                    selectedHandCardId = hand.get(hand.size()-1);
                }else{
                    // 手札の0番目のカードを選択
                    selectedHandCardId = hand.get(0);
                }

                // // サーバーからゲーム状態を取得
                // gameClient.fetchGameState();

                

                if (hand.isEmpty()) {
                    System.out.println("手札が空です。テストを終了します。");
                    return;
                }

                

                // 場札から同じカードを探す
                int selectedFieldCardId = -1; // デフォルトは -1
                for (int fieldCardId : fields) {
                    if (fieldCardId/4 == selectedHandCardId/4) {
                        selectedFieldCardId = fieldCardId;
                        break;
                    }
                }

                // playCard メソッドを呼び出す
                System.out.println("選択した手札のカードID: " + selectedHandCardId);
                System.out.println("選択した場のカードID: " + selectedFieldCardId);
                
                gameClient.playCard(selectedHandCardId, playerId, selectedFieldCardId);//ここ

            } catch (Exception ex) {
                System.err.println("仮関数中にエラーが発生しました: " + ex.getMessage());
                ex.printStackTrace();
            }
        

    }

    public void sendMessage(){
        try {
            gameClient.sendNextTurnMessage("Test");
        } catch (Exception e) {
            System.err.println("メッセージ送信エラー: " + e.getMessage());
        }
        
    }
    
}
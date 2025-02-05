import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.*;
public class GameController {
    //private OnlineGame onlineGame;
    private GameClient gameClient;
    private List<Integer> field;
    private List<Integer> player1Hands;
    private List<Integer> player2Hands;
    private List<Integer> player1Captures;
    private List<Integer> player2Captures;
    private List<Integer> previousField = new ArrayList<>();
    private List<Integer> previousOpponentHand = new ArrayList<>();
    private List<Integer> previousOpponentCaptures= new ArrayList<>();
    private PlayerInfo selfInfo;
    private PlayerInfo opponentInfo;
    private int currentTurn;
    private int playerId; // 自分のプレイヤーIDを管理
    private int elapsedTime = 0;//オンラインマッチボタン押してからの経過時間
    private boolean isActive = false;
    private int playCount;
    private boolean isKoikoi = false;
    private boolean isEnd;
    private String resultData;
    private boolean botMode;
    private int opponentFirstPlayedCard = -1;  // 相手の1回目のプレイ
    private int opponentFirstPlayedFieldCard = -1; // 相手の1回目の場のカード
    private int opponentSecondPlayedCard = -1; // 相手の2回目のプレイ(山から)
    private int opponentSecondPlayedFieldCard = -1; // 相手の2回目の場のカード
    private boolean opponentPlayed = false; // UIに通知するためのフラグ
    private int opponentPlayCount;
    private boolean isGameStarted;
    private int[] roleIds = new int[0];
    private int rolePoint = 0;
    public int[] getRoleIdArray(){return roleIds;}
    public int getRolePoint(){return rolePoint;}
    public void incrementOpponentPlayCount(){opponentPlayCount++;}//opponentPlayCountを進める
    public int getOpponentPlayCount(){return opponentPlayCount;}//opponentPlayCountをゲット
    public void resetOpponentPlayCount(){opponentPlayCount = 0;}//opponentPlayCountをリセット

    public GameClient getGameClient() {return gameClient;}//GemeClientをゲット
    public int getElapsedTime() {return elapsedTime / 1000;}//経過時間をゲット
    public boolean getIsActive() {return isActive;}//セッション初期化が失敗したらtrue？？？？？？？？？？？逆だ
    public int getPlayerId() {return playerId;}//プレイヤーIDをゲット
    public int getOpponentFirstPlayedCard() {return opponentFirstPlayedCard;}// 相手の1回目のプレイ手札のindex
    public int getOpponentFirstPlayedFieldCard() {return opponentFirstPlayedFieldCard;}// 相手の1回目のプレイ場のindex
    public int getOpponentSecondPlayedCard() {return opponentSecondPlayedCard;}// 相手の2回目のプレイ手札のindex
    public int getOpponentSecondPlayedFieldCard() {return opponentSecondPlayedFieldCard;}// 相手の2回目のプレイ場のindex
    public boolean hasOpponentPlayed() {return opponentPlayed;}// UIに通知するためのフラグ
    public void resetOpponentPlayedFlag() {opponentPlayed = false;}// UIに通知したらリセット
    public void setResult(String result) {this.resultData = result;}//ゲーム結果をセット
    public String getResult(){return resultData;}//ゲーム結果をゲット
    public void setField(List<Integer> field) {this.field = field;}//場のリストをセット
    public List<Integer> getField() {return field;}//場のリストをゲット
    public List<Integer> getPlayer1Hands() {return player1Hands;}//プレイヤー1の手札をゲット
    public List<Integer> getPlayer1Captures() {return player1Captures;}//プレイヤー1の取り札をゲット
    public List<Integer> getPlayer2Hands() {return player2Hands;}//プレイヤー2の手札をゲット
    public List<Integer> getPlayer2Captures() {return player2Captures;}//プレイヤー2の取り札をゲット
    public boolean getBotMode(){return botMode;}//botモードかどうかをゲット
    public void setBotMode(boolean botMode){this.botMode = botMode;}//botモードかどうかをセット
    public void setPlayer1Hands(List<Integer> player1Hands) {this.player1Hands = player1Hands;}//プレイヤー1の手札をセット
    public void setPlayer1Captures(List<Integer> player1Captures) {this.player1Captures = player1Captures;}//プレイヤー1の取り札をセット
    public void setPlayer2Hands(List<Integer> player2Hands) {this.player2Hands = player2Hands;}//プレイヤー2の手札をセット
    public void setPlayer2Captures(List<Integer> player2Captures) {this.player2Captures = player2Captures;}//プレイヤー2の手札取り札をセット
    public int getCurrentTurn() {return currentTurn;}//現在のターン数をゲット
    public void setCurrentTurn(int currentTurn) {this.currentTurn = currentTurn;}//現在のターン数をセット
    public void setIsKoikoi(boolean isKoikoi) {this.isKoikoi = isKoikoi;}//こいこいがどうかセット
    public boolean getIsKoikoi(){return isKoikoi;}//自分がこいこいしてるかどうかゲット?????????
    public void setIsEnd(boolean isEnd){this.isEnd = isEnd;}//ゲーム終了かどうかセット
    public boolean getIsEnd(){return isEnd;}//ゲーム終了かどうかゲット
    public PlayerInfo getSelfInfo() {return selfInfo;}//自分の情報をゲット
    public PlayerInfo getOpponentInfo() {return opponentInfo;}//相手の情報をゲット

    public void fetchAndSetPlayerInfo(String response) {//サーバーから送られてきた PlayerInfoを設定
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
    private PlayerInfo parsePlayerInfo(String playerInfoString) {//fetchAndSetPlayerInfoのサブ関数
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
    public void parseGameState(String rawGameState) {//サーバーから送られてきたゲーム情報をセット
        if(field !=null){previousField = new ArrayList<>(field);}
        if(player2Hands != null){previousOpponentHand = new ArrayList<>(player2Hands);}
        if(player2Captures != null){previousOpponentCaptures = new ArrayList<>(player2Captures);}
        
         // 相手の手札の前回の状態を保存
        
        String[] lines = rawGameState.split("\n");
        for (String line : lines) {
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
        }
        detectOpponentPlay(); // 変更を検知
    }
    private void detectOpponentPlay() {
        if (currentTurn % 2 + 1 == playerId) {
            resetOpponentState();
            return;
        }
    
        // **手札の変化を確認**
        List<Integer> removedFromHand = new ArrayList<>(previousOpponentHand);
        removedFromHand.removeAll(player2Hands);
    
        // **場の変化を確認**
        List<Integer> addedToField = new ArrayList<>(field);
        addedToField.removeAll(previousField);
    
        List<Integer> removedFromField = new ArrayList<>(previousField);
        removedFromField.removeAll(field); // 場から消えたカードを取得
    
        if (!removedFromHand.isEmpty()) {
            if (opponentFirstPlayedCard == -1) {
                // **1回目のプレイ**
                opponentFirstPlayedCard = removedFromHand.get(0);
    
                if (!addedToField.isEmpty()) {
                    opponentFirstPlayedFieldCard = addedToField.get(0); // **場に置いた**
                } else if (!removedFromField.isEmpty()) {
                    opponentFirstPlayedFieldCard = removedFromField.get(0); // 場から取った
                } else {
                    opponentFirstPlayedFieldCard = -1; //場に変化なし
                }
                System.out.println("[DEBUG] 1回目のプレイ修正前 - 手札から: " + opponentFirstPlayedCard + ", 場: " + opponentFirstPlayedFieldCard);
                if(opponentFirstPlayedCard == opponentFirstPlayedFieldCard){
                    opponentFirstPlayedFieldCard = -1;
                }
                System.out.println("[DEBUG] 1回目のプレイ修正後 - 手札から: " + opponentFirstPlayedCard + ", 場: " + opponentFirstPlayedFieldCard);
                opponentPlayed = true;
            } else if (opponentSecondPlayedCard == -1) {
                // **2回目のプレイ**
                removedFromHand.removeIf(card -> card == opponentFirstPlayedCard);
                opponentSecondPlayedCard = removedFromHand.isEmpty() ? -1 : removedFromHand.get(0);
    
                if (!addedToField.isEmpty()) {
                    opponentSecondPlayedFieldCard = addedToField.get(0); // 場に置いた
                } else if (!removedFromField.isEmpty()) {
                    opponentSecondPlayedFieldCard = removedFromField.get(0); // 場から取った
                } else {
                    opponentSecondPlayedFieldCard = -1; // 場に変化なし
                }
                System.out.println("[DEBUG] 2回目のプレイ修正前 - 山札から: " + opponentSecondPlayedCard + ", 場: " + opponentSecondPlayedFieldCard);
                if(opponentSecondPlayedCard == opponentSecondPlayedFieldCard){
                    opponentSecondPlayedFieldCard = -1;
                }
                System.out.println("[DEBUG] 2回目のプレイ修正後 - 山札から: " + opponentSecondPlayedCard + ", 場: " + opponentSecondPlayedFieldCard);
                opponentPlayed = true;
            }
            System.out.println("[DEBUG] opponentPlayed フラグを true に設定");
        }
    }
    

    public void resetOpponentState(){
        opponentFirstPlayedCard = -1;
        opponentFirstPlayedFieldCard = -1;
        opponentSecondPlayedCard= -1;
        opponentSecondPlayedFieldCard = -1;
        resetOpponentPlayedFlag();
        resetOpponentPlayCount();
    }
    
    private void handlePlayerHands(int playerNumber, String cardData) {//parseGameStateのサブ関数　手札を設定
        List<Integer> hands = parseCardList(cardData);
        if (playerId == playerNumber) {
            setPlayer1Hands(hands);
        } else {
            setPlayer2Hands(hands);
        }
    }
    private void handlePlayerCaptures(int playerNumber, String cardData) {//parseGameStateのサブ関数　取り札を設定
        List<Integer> captures = parseCardList(cardData);
        if (1 == playerNumber) {
            setPlayer1Captures(captures);
            // System.out.println("[DEBUG] 自分の取得カード (Player" + playerNumber + "): " + captures);
        } else {
            setPlayer2Captures(captures);
            // System.out.println("[DEBUG] 相手の取得カード (Player" + playerNumber + "): " + captures);
        }
    }
    private List<Integer> parseCardList(String cardString) {//parseGameStateのサブ関数のサブ関数　文字列をListに変更
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
    public void setGameClient(GameClient gameClient) {this.gameClient = gameClient;}//GemeClientをセット
    
    public void startOnlineMatch() {// オンライン対戦開始
        try {
            isGameStarted = false;
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
            gameClient.sendPlayerInfo(AppPreferences.getString("プレイヤー名", "player"), AppPreferences.getInt("プレイヤーアイコン", 0), AppPreferences.getInt("経験値", 0) / 50);
            //gameclient.sendPlayerInfo(AppPreferences.putString(("プレイヤー名", "player")), AppPreferences.putString("経験値"), AppPreferences.putInt("経験値")/50);
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
                        if(gameClient.isGameStarted() && !isGameStarted) {
                            ((Timer) e.getSource()).stop();
                            System.out.println("ゲームが開始されました!");
                            isGameStarted = true;
                            Thread.sleep(500);
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

    public void parseRoleInfo(String roleString){
        System.out.println("今から処理する部分");
        System.out.println(roleString);

        int roleStartIndex = roleString.indexOf("役:");
        int scoreStartIndex = roleString.indexOf("得点:");

        if (roleStartIndex == -1 || scoreStartIndex == -1) {
            System.err.println("エラー: 役または得点の情報が見つかりません");
            return;
        }

        String rolesString = roleString.substring(roleStartIndex + 2, scoreStartIndex).trim();
        String scoreString = roleString.substring(scoreStartIndex + 3).trim();

        roleIds = parseRolesFromServer(rolesString);
        rolePoint = parseScoreFromServer(scoreString);

        System.out.println("解析結果 - 役 (ID): " + java.util.Arrays.toString(roleIds));
        System.out.println("解析結果 - 得点: " + rolePoint);

    }

    private void setRolePoint(int point){
        this.rolePoint = point;
    }
    private int parseScoreFromServer(String scoreString) {
        try {
            return Integer.parseInt(scoreString.trim());
        } catch (NumberFormatException e) {
            System.err.println("エラー: 得点の解析に失敗しました。デフォルト値 0 を使用します。");
            return 0;
        }
    }
    private int[] parseRolesFromServer(String roleString) {
        Map<String, Integer> roleMapping = new HashMap<>();
        roleMapping.put("五光", 1);
        roleMapping.put("雨四光", 2);
        roleMapping.put("四光", 3);
        roleMapping.put("三光", 4);
        roleMapping.put("花見で一杯", 5);
        roleMapping.put("月見で一杯", 6);
        roleMapping.put("猪鹿蝶", 7);
        roleMapping.put("赤短", 8);
        roleMapping.put("青短", 9);
        roleMapping.put("タネ", 10);
        roleMapping.put("タン", 11);
        roleMapping.put("カス", 12);
    
        // サーバーから送られた役を分解
        String[] roles = roleString.split(",");
        int[] tmproleIds = new int[roles.length];
    
        for (int i = 0; i < roles.length; i++) {
            String cleanedRole = roles[i].trim(); // **余分なスペースを削除**
            tmproleIds[i] = roleMapping.getOrDefault(cleanedRole, -1); // マッピングにない場合は `-1` にする
        }
    
        return tmproleIds;
    }

}



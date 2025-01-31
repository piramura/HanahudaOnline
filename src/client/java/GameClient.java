import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.net.URISyntaxException;

public class GameClient {
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private String sessionId;
    private final String serverUrl; // サーバーURLをフィールドに追加
    private GameController controller;
    // コンストラクタでURLを受け取る
    public GameClient(String serverUrl) {
        this.serverUrl = serverUrl; // デフォルト値を設定
    }
    public void setGameController(GameController controller){
        this.controller = controller;
    }
    //補助メソッド
    private HttpRequest buildRequest(String endpoint, String method, String body) throws URISyntaxException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
            .uri(new URI(serverUrl + endpoint))
            .header("Session-ID", sessionId)
            .header("Content-Type", "text/plain");
    
        if ("POST".equalsIgnoreCase(method)) {
            builder.POST(HttpRequest.BodyPublishers.ofString(body == null ? "" : body)); // nullを空文字に置き換え
        } else if ("GET".equalsIgnoreCase(method)) {
            builder.GET();
        }
    
        return builder.build();
    }
    
    
    // セッションIDを取得
    public void initializeSession(String passphrase) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(serverUrl + "/session"))
            .POST(HttpRequest.BodyPublishers.ofString(passphrase))
            .header("Content-Type", "text/plain")
            .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            sessionId = response.body();
            System.out.println("取得したセッションID: " + sessionId);
            ready();
        } else {
            throw new RuntimeException("セッションID取得エラー: " + response.statusCode());
        }
    }
    //playerIdを取得
    public int fetchPlayerId() throws Exception {
        HttpRequest request = buildRequest("/game/playerId", "GET", null);
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return Integer.parseInt(response.body());
        } else {
            throw new RuntimeException("プレイヤーID取得エラー: " + response.statusCode());
        }
    }
    //自分の情報を送る
    public void sendPlayerInfo(String playerName, int iconNum, int level) throws Exception {
        String message = String.format("%s:%d:%d", playerName, iconNum, level);
        HttpRequest request = buildRequest("/game/player", "POST", message);
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            System.out.println("Player info sent successfully: " + response.body());
        } else {
            throw new RuntimeException("Failed to send player info: " + response.statusCode());
        }
    }
    //相手の情報を取り出す
    public String fetchPlayerInfo() throws Exception {
        HttpRequest request = buildRequest("/game/player", "GET", null);
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            //System.out.println("Player info fetched successfully: " + response.body());
            //
            controller.fetchAndSetPlayerInfo(response.body());
            return response.body();
        } else {
            throw new RuntimeException("Failed to fetch player info: " + response.statusCode());
        }
    }
    // 準備完了通知これいる？
    public void ready() throws Exception {
        System.out.println("準備完了通知送信URL: " + serverUrl + "/game/ready");
        HttpRequest request = buildRequest("/game/ready", "POST", null);
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            System.out.println("準備完了通知送信結果: " + response.body());
        } else {
            throw new RuntimeException("準備完了通知エラー: " + response.statusCode());
        }
    }
    public boolean isGameStarted() throws Exception {
        HttpRequest request = buildRequest("/game/ready", "POST", null);
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    
        if (response.statusCode() == 200) {
            String responseBody = response.body();
            System.out.println("サーバーからのレスポンス: " + responseBody);
            return "GAME_STARTED".equals(responseBody); // レスポンスが "GAME_STARTED" なら true を返す
        } else {
            throw new RuntimeException("準備完了確認エラー: " + response.statusCode());
        }
    }
    // ゲーム状態取得
    public void fetchGameState() throws Exception {
        HttpRequest request = buildRequest("/game/state", "GET", null);
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            // System.out.println("ゲーム状態: " + response.body());
            //
            controller.parseGameState(response.body());
        } else {
            throw new RuntimeException("ゲーム状態取得エラー: " + response.statusCode());
        }
    }
    // // カードプレイ
    // public void playCard(int cardInfo,int playerId,int fieldCardId) throws Exception {//UIをブロックしてる。

    //     String message = String.format("PLAY_CARD:%d:%d:%d", cardInfo, fieldCardId,playerId); // メッセージに PlayerID を追加
    //     HttpRequest request = buildRequest("/game/play", "POST", message);
    //     HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    //     System.out.println("playCard: " + response.body());
    //     if (response.statusCode() == 200) {
    //         System.out.println("カードプレイ成功: " + response.body());
    //         String responseBody = response.body();
    //         // レスポンス内容に応じて処理を分岐
    //         if (responseBody.startsWith("CONTINUE_TURN")){
    //             continueYourTurn();
    //         }else if (responseBody.startsWith("KOIKOI_WAITING")) {
    //             handleKoiKoiSelection(responseBody);
    //         } else if (responseBody.startsWith("NEXT_TURN")) {
    //             handleNextTurn(responseBody);
    //         } else if (responseBody.startsWith("GAME_END")) {
    //             handleGameEnd(responseBody);
    //         } else {
    //             throw new RuntimeException("未知のレスポンス形式: " + responseBody);
    //         }
    //     } else {
    //         throw new RuntimeException("カードプレイエラー: " + response.statusCode());
    //     }
    // }
    public void fetchGameStateAsync() {
        HttpRequest request;
    
        try {
            request = buildRequest("/game/state", "GET", null);
        } catch (URISyntaxException e) {
            System.err.println("無効な URI: " + e.getMessage());
            return;
        }
    
        CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(HttpResponse::body)
            .thenAccept(responseBody -> {
                System.out.println("ゲーム状態: " + responseBody);
                if(responseBody.startsWith("GAME_END")){
                    controller.setIsEnd(true);
                    System.out.println("controller.getIsEnd(): " + controller.getIsEnd());
                }else{
                    controller.parseGameState(responseBody);
                }
            })
            .exceptionally(ex -> {
                System.err.println("ゲーム状態取得エラー: " + ex.getMessage());
                return null;
            });
    }
    

    public void playCard(int cardInfo, int playerId, int fieldCardId) {
        String message = String.format("PLAY_CARD:%d:%d:%d", cardInfo, fieldCardId, playerId);
        HttpRequest request;
        try {
            request = buildRequest("/game/play", "POST", message);
        } catch (URISyntaxException e) {
            System.err.println("無効な URI: " + e.getMessage());
            return;
        }
        CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(HttpResponse::body)
            .thenAccept(responseBody -> {
                System.out.println("playCard: " + responseBody);
                if (responseBody.startsWith("CONTINUE_TURN")) {
                    continueYourTurn();
                } else if (responseBody.startsWith("KOIKOI_WAITING")) {
                    handleKoiKoiSelection(responseBody);
                } else if (responseBody.startsWith("NEXT_TURN")) {
                    handleNextTurn(responseBody);
                } else if (responseBody.startsWith("GAME_END")) {
                    handleGameEnd(responseBody);
                } else {
                    throw new RuntimeException("未知のレスポンス形式: " + responseBody);
                }
            })
            .exceptionally(ex -> {
                System.err.println("カードプレイエラー: " + ex.getMessage());
                return null;
            });
    }
    
    private void continueYourTurn(){
        //もう一枚処理してって命令を送る。なんでも書いていいよ
        try{
            fetchGameState();
        }catch(Exception e){
            System.err.println("continueYourTurnの fetchGameState()でEROOR");
        }

        //二回目のカード選びに移るPLAYCARDを頼みます。

    }
    public void sendNextTurnMessage(String answer)throws Exception {
        // answer は "KOIKOI"  か "END" のいずれかを送信
        String message = String.format("%s", answer);
        System.out.println("messageを作成 " + message);
        HttpRequest request = buildRequest("/game/next", "POST", message);
        System.out.println("requestを作成 " + request);

        // HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());ここで地獄
        CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(HttpResponse::body)
            .thenAccept(responseBody -> {
                System.out.println("返事: " + responseBody);
                if (responseBody.startsWith("NEXT_TURN")) {
                    System.out.println("NEXT_TURN続けます: ");
                    fetchGameStateAsync();
                } else if (responseBody.startsWith("GAME_END")) {
                    System.out.println("ゲーム終了: ");
                } else {
                    throw new RuntimeException("未知のレスポンス形式: " + responseBody);
                }
            })
            .exceptionally(ex -> {
                System.err.println("送信エラー: " + ex.getMessage());
                return null;
            });
    
    }
    
    //ここからPlayCardの補助関数
    private void handleKoiKoiSelection(String responseBody) {
        System.out.println("こいこい待機中:テスト用で、KOIKOIと " + responseBody);
        controller.setIsKoikoi(true);
        try{
            // sendNextTurnMessage("KOIKOI");
        }catch(Exception e){
            System.err.println("sendNextTurnMessage(\"KOIKOI\")でエラー");
        }
    }

    private void handleNextTurn(String responseBody) {
        System.out.println("次のターンに進みます: " + responseBody);
        // try{
        //     fetchGameState();
        // }catch(Exception e){
        //     System.err.println("handleNextTurnの fetchGameState()でEROOR");
        // }
        //次のターンへ移るアニメーションsendNextTurnMessage(NEXT_TURN);
        try{
            sendNextTurnMessage("NEXT_TURN");
            // OnlineGame.updateState();
        }catch(Exception e){
            System.err.println("sendNextTurnMessage(\"NEXT_TURN\")でエラー");
        }
        ;
    }

    private void handleGameEnd(String responseBody) {
        System.out.println("ゲーム終了: " + responseBody);
    }
    //PlayCardの補助関数ここまで

    public void disconnect() throws Exception {
        HttpRequest request = buildRequest("/session/terminate", "POST", null);
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            System.out.println("切断通知成功: " + response.body());
        } else {
            System.err.println("切断通知エラー: " + response.statusCode());
        }
    }   
}

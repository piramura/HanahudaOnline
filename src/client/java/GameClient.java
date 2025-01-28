import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.Scanner;

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

    public int fetchPlayerId() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(serverUrl + "/game/playerId")) // PlayerID取得のエンドポイント
            .GET()
            .header("Session-ID", sessionId)
            .build();
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
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(serverUrl + "/game/player"))
            .POST(HttpRequest.BodyPublishers.ofString(message))
            .header("Session-ID", sessionId)
            .header("Content-Type", "text/plain")
            .build();
    
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            System.out.println("Player info sent successfully: " + response.body());
        } else {
            throw new RuntimeException("Failed to send player info: " + response.statusCode());
        }
    }
    //相手の情報を取り出す
    public String fetchPlayerInfo() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(serverUrl + "/game/player"))
            .GET()
            .header("Session-ID", sessionId)
            .build();
    
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
    
    // 準備完了通知
    public void ready() throws Exception {
        System.out.println("準備完了通知送信URL: " + serverUrl + "/game/ready");

        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(serverUrl + "/game/ready"))
            .POST(HttpRequest.BodyPublishers.noBody())
            .header("Session-ID", sessionId)
            .build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            System.out.println("準備完了通知送信結果: " + response.body());
        } else {
            throw new RuntimeException("準備完了通知エラー: " + response.statusCode());
        }
    }
    public boolean isGameStarted() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(serverUrl + "/game/ready")) // 準備完了状態を確認するエンドポイント
            .POST(HttpRequest.BodyPublishers.noBody())
            .header("Session-ID", sessionId)
            .build();

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
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(serverUrl + "/game/state"))
            .GET()
            .header("Session-ID", sessionId)
            .build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            System.out.println("ゲーム状態: " + response.body());
            //
            controller.parseGameState(response.body());
        } else {
            throw new RuntimeException("ゲーム状態取得エラー: " + response.statusCode());
        }
    }

    
    

    // カードプレイ
    public void playCard(int cardInfo,int playerId,int fieldCardId) throws Exception {
        String message = String.format("PLAY_CARD:%d:%d:%d", cardInfo, fieldCardId,playerId); // メッセージに PlayerID を追加
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(serverUrl + "/game/play"))
            .POST(HttpRequest.BodyPublishers.ofString(message))
            .header("Session-ID", sessionId)
            .header("Content-Type", "text/plain")
            .build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            System.out.println("カードプレイ成功: " + response.body());
            String responseBody = response.body();
            // レスポンス内容に応じて処理を分岐
            if (responseBody.startsWith("KOIKOI_WAITING")) {
                handleKoiKoiSelection(responseBody);
            } else if (responseBody.startsWith("NEXT_TURN")) {
                handleNextTurn(responseBody);
            } else if (responseBody.startsWith("GAME_END")) {
                handleGameEnd(responseBody);
            } else {
                throw new RuntimeException("未知のレスポンス形式: " + responseBody);
            }
        } else {
            throw new RuntimeException("カードプレイエラー: " + response.statusCode());
        }
    }
    /* */
    //ここからPlayCardの補助関数
    private void handleKoiKoiSelection(String responseBody) {
        System.out.println("こいこい待機中: " + responseBody);
    
        // UIやコンソールでプレイヤーに選択をしてもらう。ここ頼む！！！！！！！！！！！！！！！
        //例でScanner使ってるけど、int型のメソッドをどっかで作ってウィンドウ表示でボタンとかだしてchoiceに１か０を入れてくれればOK！！！！
        System.out.println("役が成立しました。「こいこい」しますか？（1: はい, 0: いいえ）");
        Scanner scanner = new Scanner(System.in);
        int choice = -1;
        while (choice != 1 && choice != 0) {
            System.out.print("選択してください (1 または 0): ");
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("無効な入力です。");
            }
        }
        //ここまで頼む！！！！！！！！！！！！！！！
        
        // サーバーに「こいこい」か「終了」の結果を送信
        String koiKoiMessage = String.format("KOIKOI_RESPONSE:%d", choice);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(serverUrl + "/game/play")) // 同じエンドポイントを利用
                .POST(HttpRequest.BodyPublishers.ofString(koiKoiMessage))
                .header("Session-ID", sessionId)
                .header("Content-Type", "text/plain")
                .build();
    
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println("こいこい選択完了: " + response.body());
            } else {
                throw new RuntimeException("こいこい選択エラー: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("こいこい選択中にエラーが発生しました。");
        }
    }
    private void handleNextTurn(String responseBody) {
        System.out.println("次のターンに進みます: " + responseBody);
        //次のターンにいく。勝手に盤面は変わるけど、カットイン演出とか入れるならここで呼び出す。
    }
    private void handleGameEnd(String responseBody) {
        System.out.println("ゲーム終了: " + responseBody);
    
        // ゲーム終了時の処理を書く
    }

    //PlayCardの補助関数ここまで

    public void disconnect() throws Exception {
        if (sessionId == null || sessionId.isEmpty()) {
            System.out.println("セッションIDが未設定のため、切断通知をスキップします。");
            return;
        }

        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(serverUrl + "/session/terminate"))
            .POST(HttpRequest.BodyPublishers.noBody())
            .header("Session-ID", sessionId)
            .build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            System.out.println("切断通知成功: " + response.body());
        } else {
            System.err.println("切断通知エラー: " + response.statusCode());
        }
    }   
}

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

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
            controller.parseGameState(response.body());
        } else {
            throw new RuntimeException("ゲーム状態取得エラー: " + response.statusCode());
        }
    }
    
    

    // カードプレイ
    public void playCard(int cardInfo,int playerId) throws Exception {
        String message = String.format("PLAY_CARD:%d:%d", cardInfo, playerId); // メッセージに PlayerID を追加
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(serverUrl + "/game/play"))
            .POST(HttpRequest.BodyPublishers.ofString(message))
            .header("Session-ID", sessionId)
            .header("Content-Type", "text/plain")
            .build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            System.out.println("カードプレイ成功: " + response.body());
        } else {
            throw new RuntimeException("カードプレイエラー: " + response.statusCode());
        }
    }
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

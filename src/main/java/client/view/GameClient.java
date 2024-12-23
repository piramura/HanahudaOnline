import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

public class GameClient {
    private String sessionId;

    public GameClient(String sessionId) {
        this.sessionId = sessionId;
    }
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

public void playCard(String cardInfo) throws Exception {
    HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI("http://localhost:10030/game/play"))
            .POST(HttpRequest.BodyPublishers.ofString("PLAY_CARD:" + cardInfo))
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

public String fetchGameState() throws Exception {
    HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI("http://localhost:10030/game/state"))
            .GET()
            .header("Session-ID", sessionId)
            .build();

    HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    if (response.statusCode() == 200) {
        return response.body();
    } else {
        throw new RuntimeException("サーバーからの応答エラー: " + response.statusCode());
    }

}
public String initializeSession(String passphrase) throws Exception {
    HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI("http://localhost:10030/session"))
            .POST(HttpRequest.BodyPublishers.ofString(passphrase))
            .header("Content-Type", "text/plain")
            .build();

    HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    if (response.statusCode() == 200) {
        this.sessionId = response.body();
        System.out.println("取得したセッションID: " + sessionId);
        return response.body();
    } else {
        throw new RuntimeException("セッションID取得エラー: " + response.statusCode());
    }
}

}
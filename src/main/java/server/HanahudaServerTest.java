import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class HanahudaServerTest {
    public static void main(String[] args) throws Exception {
        String serverUrl = "http://localhost:10030";
        
        System.out.println("=== サーバーのテスト開始 ===\n");

        // セッション登録のテスト
        System.out.println("--- セッション登録テスト ---");
        String sessionId = registerSession(serverUrl, "default_passphrase");
        System.out.println("取得したセッションID: " + sessionId);


        // セッション登録の失敗テスト
        System.out.println("--- 無効な合言葉でのセッション登録テスト ---");
        String invalidResponse = registerSession(serverUrl, "invalid_passphrase");
        System.out.println("無効なセッション登録結果: " + invalidResponse);
        // セッション登録のテスト
        System.out.println("--- セッション登録テスト 2---");
        String sessionId2 = registerSession(serverUrl, "default_passphrase");
        System.out.println("取得したセッションID2: " + sessionId2);

        // ゲーム状態取得のテスト
        System.out.println("--- ゲーム状態取得テスト ---");
        String gameState = getGameState(serverUrl, sessionId);
        System.out.println("ゲーム状態: " + gameState);

        // カードプレイのテスト
        System.out.println("--- カードプレイテスト ---");
        String playResponse = playCard(serverUrl, sessionId, "PLAY_CARD:0");
        System.out.println("カードプレイ結果: " + playResponse);

        // 無効なセッションIDでのゲーム状態取得テスト
        System.out.println("--- 無効なセッションIDでのゲーム状態取得テスト ---");
        String invalidGameState = getGameState(serverUrl, "invalid_session");
        System.out.println("無効なセッションのゲーム状態: " + invalidGameState);

        System.out.println("\n=== サーバーのテスト終了 ===");
    }

    private static String registerSession(String serverUrl, String passphrase) {
        try {
            URL url = new URL(serverUrl + "/session");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "text/plain");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(passphrase.getBytes());
            }

            if (conn.getResponseCode() == 200) {
                try (Scanner scanner = new Scanner(conn.getInputStream())) {
                    return scanner.nextLine();
                }
            } else {
                try (Scanner scanner = new Scanner(conn.getErrorStream())) {
                    return scanner.nextLine();
                }
            }
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    private static String getGameState(String serverUrl, String sessionId) {
        try {
            URL url = new URL(serverUrl + "/game/state");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Session-ID", sessionId);

            if (conn.getResponseCode() == 200) {
                try (Scanner scanner = new Scanner(conn.getInputStream())) {
                    return scanner.nextLine();
                }
            } else {
                try (Scanner scanner = new Scanner(conn.getErrorStream())) {
                    return scanner.nextLine();
                }
            }
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    private static String playCard(String serverUrl, String sessionId, String message) {
        try {
            URL url = new URL(serverUrl + "/game/play");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Session-ID", sessionId);
            conn.setRequestProperty("Content-Type", "text/plain");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(message.getBytes());
            }

            if (conn.getResponseCode() == 200) {
                try (Scanner scanner = new Scanner(conn.getInputStream())) {
                    return scanner.nextLine();
                }
            } else {
                try (Scanner scanner = new Scanner(conn.getErrorStream())) {
                    return scanner.nextLine();
                }
            }
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}

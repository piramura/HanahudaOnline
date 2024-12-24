public class GameSessionManagerTest {
    public static void main(String[] args) {
        ClientSessionManager clientSessionManager = new ClientSessionManager();
        GameSessionManager gameSessionManager = new GameSessionManager(clientSessionManager);

        System.out.println("=== GameSessionManager テスト開始 ===");

        // クライアント追加テスト
        System.out.println("\n--- クライアントの追加テスト ---");
        System.out.println("クライアント1追加: " + gameSessionManager.addClient("session1"));
        System.out.println("クライアント2追加: " + gameSessionManager.addClient("session2"));
        System.out.println("クライアント3追加: " + gameSessionManager.addClient("session3"));

        // メッセージ処理テスト
        System.out.println("\n--- メッセージ処理テスト ---");
        clientSessionManager.registerSession("session1");
        System.out.println("有効セッションでのメッセージ: " + gameSessionManager.processMessage("session1", "PLAY_CARD:0"));

        System.out.println("\n--- 無効セッションでのメッセージ ---");
        System.out.println("無効セッションでのメッセージ: " + gameSessionManager.processMessage("invalid_session", "PLAY_CARD:0"));

        // ゲーム状態取得テスト
        System.out.println("\n--- ゲーム状態取得テスト ---");
        System.out.println("有効セッションでのゲーム状態取得: " + gameSessionManager.getGameState("session1"));
        System.out.println("無効セッションでのゲーム状態取得: " + gameSessionManager.getGameState("invalid_session"));

        System.out.println("\n=== GameSessionManager テスト終了 ===");
    }
}

public class ClientTest {
    public static void main(String[] args) {
        try {
            // String serverUrl = "https://hanahudaonline.onrender.com"; // サーバーのURLを設定

            GameClient client1 = new GameClient("http://localhost:10030");
            GameClient client2 = new GameClient("http://localhost:10030");

            // クライアント1 セッション初期化
            client1.initializeSession("default_passphrase");

            // クライアント2 セッション初期化
            client2.initializeSession("default_passphrase");

            // クライアント1 準備完了通知
            client1.ready();

            // クライアント2 準備完了通知
            client2.ready();

            // クライアント1 ゲーム状態取得
            client1.fetchGameState();

            // クライアント2 カードプレイ
            client1.playCard("1");

            // クライアント1 ゲーム状態再取得
            client1.fetchGameState();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

import javax.swing.*;
public class GameController {
    private MainFrame mainFrame;
    private GameClient gameClient;

    public GameController() {
        this.mainFrame = new MainFrame(this);
        this.gameClient = new GameClient(null); // セッションIDを後から設定
    }

    // // イベントリスナーを設定
    // private void setupEventListeners() {
    //     mainFrame.addStartGameListener(() -> startOnlineMatch());
    //     mainFrame.addCardClickListener(card -> handleCardClick(card));
    //     mainFrame.addQuitListener(() -> quitGame());
    // }

    // オンライン対戦開始
    public void startOnlineMatch() {
        try {
            gameClient.initializeSession("default_passphrase");
            // メインフレームを表示（既存のフィールドを使用）
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setVisible(true);
            updateGameStateFromServer(); // 初期状態を取得して更新
        } catch (Exception e) {
            System.err.println("セッションの初期化に失敗しました: " + e.getMessage());
        }
    }

    // カードをクリックしたときの処理
    public void handleCardClick(Card card) {
        try {
            gameClient.playCard(String.valueOf(card.getId()));
            updateGameStateFromServer(); // サーバーから状態を取得
        } catch (Exception e) {
            System.err.println("カード送信エラー: " + e.getMessage());
        }
    }

    // ゲーム状態をサーバーから取得して更新
    private void updateGameStateFromServer() {
        try {
            String response = gameClient.fetchGameState();
            mainFrame.syncUIWithGameState(); // UIを同期
        } catch (Exception e) {
            System.err.println("ゲーム状態の取得に失敗しました: " + e.getMessage());
        }
    }

    // ゲーム終了
    private void quitGame() {
        System.out.println("ゲームを終了します。");
        System.exit(0);
    }
}

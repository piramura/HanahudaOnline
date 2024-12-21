import java.util.List;

public class GameController {
    private MainFrame mainFrame;
    private CardFactory cardFactory;
    private CommClient commClient; // サーバーとの通信オブジェクト

    public GameController(MainFrame frame, CommClient client) {
        this.mainFrame = frame;
        this.cardFactory = new CardFactory();
        this.commClient = client; // サーバーとの接続を受け取る
    }

    // ゲーム開始
    public void startGame() {
        setupInitialCards(); // 初期化処理
        //simulateServerUpdate(); // サーバーデータのシミュレーション
    }

    // 初期化処理
    private void setupInitialCards() {
        // 場のカード
        for (int i = 1; i <= 6; i++) {
            Card card = cardFactory.createCard(i, "Field Card " + i, false);
            card.addCardClickListener(cardData -> handleCardClick(card.getId())); // リスナー追加
            mainFrame.getFieldArea().addCard(card);
        }

        // 自分の手札
        for (int i = 7; i <= 12; i++) {
            Card card = cardFactory.createCard(i, "Hand Card " + i, false);
            card.addCardClickListener(cardData -> handleCardClick(card.getId())); // リスナー追加
            mainFrame.getPlayerHandArea().addCard(card);
        }

        // 相手の手札（裏向き）
        for (int i = 13; i <= 18; i++) {
            Card card = cardFactory.createCard(i, "Opponent Card " + i, true);
            mainFrame.getOpponentHandArea().addCard(card);
        }

        // UI更新
        mainFrame.repaint();
    }

    // サーバーからのデータ受信
    public void updateGameStateFromServer(
        List<Integer> fieldCards,
        List<Integer> playerHandCards,
        int opponentHandCount,
        String currentTurn,
        boolean isGameOver
    ) {
        // 場のカードを更新
        mainFrame.getFieldArea().removeAllCards();
        for (int cardID : fieldCards) {
            Card card = cardFactory.createCard(cardID, "Field Card " + cardID, false);
            card.addCardClickListener(cardData -> handleCardClick(card.getId()));
            mainFrame.getFieldArea().addCard(card);
        }

        // 自分の手札を更新
        mainFrame.getPlayerHandArea().removeAllCards();
        for (int cardID : playerHandCards) {
            Card card = cardFactory.createCard(cardID, "Hand Card " + cardID, false);
            card.addCardClickListener(cardData -> handleCardClick(card.getId()));
            mainFrame.getPlayerHandArea().addCard(card);
        }

        // 相手の手札を更新（裏向きカード）
        mainFrame.getOpponentHandArea().removeAllCards();
        for (int i = 0; i < opponentHandCount; i++) {
            Card card = cardFactory.createCard(-1, "Hidden", true);
            mainFrame.getOpponentHandArea().addCard(card);
        }

        // ターン情報とゲーム終了判定
        System.out.println("Current Turn: " + currentTurn);
        if (isGameOver) {
            System.out.println("Game Over!");
        }

        // UI更新
        mainFrame.repaint();
    }

    // サーバーデータのシミュレーション
    public void simulateServerUpdate() {
        List<Integer> fieldCards = List.of(1, 2, 3, 4, 5, 6, 7, 8); // 場のカード
        List<Integer> playerHandCards = List.of(9,10,11,12,13,14,15,16); // 自分の手札
        int opponentHandCount = 8; // 相手の手札枚数
        String currentTurn = "player"; // 現在のターン
        boolean isGameOver = false; // ゲーム終了状態

        updateGameStateFromServer(fieldCards, playerHandCards, opponentHandCount, currentTurn, isGameOver);
    }

    // カードクリック時の処理
    private void handleCardClick(int cardID) {
        System.out.println("Clicked card ID: " + cardID);

        // サーバーにクリックイベントを送信
        sendCardClickToServer(cardID);
    }

    private void sendCardClickToServer(int cardID) {
    try {
        String message = "PLAY_CARD:" + cardID;
        commClient.send(message); // サーバーにメッセージを送信
        System.out.println("Sent to server: " + message);
    } catch (Exception e) {
        System.err.println("Failed to send card click to server: " + e.getMessage());
    }
}

}

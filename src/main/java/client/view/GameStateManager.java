import java.util.ArrayList;
import java.util.List;

public class GameStateManager {
    private List<Card> fieldCards; // フィールド上のカード
    private List<Card> playerHand; // プレイヤーの手札
    private List<Card> opponentHand; // 相手の手札（裏向きカード）
    private int currentPlayer; // 現在のプレイヤー
    private boolean isGameOver; // ゲーム終了フラグ
    private CardFactory cardFactory; // カード生成用ファクトリ
    private List<GameStateListener> listeners; // 状態変更リスナー

    // コンストラクタ
    public GameStateManager() {
        this.fieldCards = new ArrayList<>();
        this.playerHand = new ArrayList<>();
        this.opponentHand = new ArrayList<>();
        this.cardFactory = new CardFactory();
        this.currentPlayer = 0;
        this.isGameOver = false;
        this.listeners = new ArrayList<>();
    }

    // 状態変更リスナーの追加
    public void addListener(GameStateListener listener) {
        listeners.add(listener);
    }

    // 状態変更を通知
    private void notifyListeners() {
        for (GameStateListener listener : listeners) {
            listener.onStateUpdated(this);
        }
    }

    // 状態の一括更新（サーバーからの応答用）
    public void updateState(List<Integer> fieldCardIDs, List<Integer> playerHandIDs, int opponentHandCount) {
        // フィールドカードを更新
        fieldCards = createCardsFromIDs(fieldCardIDs);

        // プレイヤーの手札を更新
        playerHand = createCardsFromIDs(playerHandIDs);

        // 相手の手札を更新（裏向きカード）
        opponentHand.clear();
        for (int i = 0; i < opponentHandCount; i++) {
            opponentHand.add(cardFactory.createCard(-1, "Hidden", true));
        }

        // 状態変更を通知
        notifyListeners();
    }

    // カードIDリストからカードリストを生成
    private List<Card> createCardsFromIDs(List<Integer> cardIDs) {
        List<Card> cards = new ArrayList<>();
        for (int id : cardIDs) {
            cards.add(cardFactory.createCard(id));
        }
        return cards;
    }

    // フィールドカードの取得
    public List<Card> getFieldCards() {
        return new ArrayList<>(fieldCards); // 不変リストを返す
    }

    // プレイヤー手札の取得
    public List<Card> getPlayerHand() {
        return new ArrayList<>(playerHand); // 不変リストを返す
    }

    // 相手手札の取得
    public List<Card> getOpponentHand() {
        return new ArrayList<>(opponentHand); // 不変リストを返す
    }

    // 現在のプレイヤーIDの取得と設定
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(int playerID) {
        this.currentPlayer = playerID;
    }
    public void addFieldCard(Card card) {
    fieldCards.add(card);
}

public void addPlayerCard(Card card) {
    playerHand.add(card);
}

public void addOpponentCard(Card card) {
    opponentHand.add(card);
}

    // 次のターンに進む
    public void nextTurn() {
        currentPlayer = (currentPlayer + 1) % 2; // 2人プレイヤーの場合
        notifyListeners(); // ターン変更を通知
    }

    // ゲーム終了フラグの取得と設定
    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.isGameOver = gameOver;
        notifyListeners(); // 状態変更を通知
    }

    // 相手手札の数を更新
    public void updateOpponentHandCount(int count) {
        opponentHand.clear();
        for (int i = 0; i < count; i++) {
            opponentHand.add(cardFactory.createCard(-1, "Hidden", true)); // 裏向きカード
        }
        notifyListeners(); // 状態変更を通知
    }

    // プレイヤーがカードをプレイする
    public void playCard(Card card) {
        if (!playerHand.contains(card)) {
            throw new IllegalArgumentException("カードがプレイヤーの手札に存在しません。");
        }
        playerHand.remove(card);
        fieldCards.add(card);
        System.out.println("カードがフィールドに移動しました: " + card.getId());
        nextTurn(); // ターンを進める
    }

    // カードクリック処理
    public void processCardClick(Card clickedCard) {
        if (fieldCards.contains(clickedCard)) {
            System.out.println("フィールドのカードがクリックされました: " + clickedCard.getId());
        } else if (playerHand.contains(clickedCard)) {
            playCard(clickedCard); // プレイヤーの手札からフィールドへ移動
        } else {
            System.out.println("無効なカードがクリックされました。");
        }
    }
    public String getWinnerInfo() {
    // ゲーム終了時の勝者情報を返す（仮実装）
    return "プレイヤー 1";
}
    public void addCardToArea(int cardID, String area) {
    Card card = cardFactory.createCard(cardID);
    switch (area) {
        case "field":
            addFieldCard(card);
            break;
        case "hand":
            addPlayerCard(card);
            break;
        case "opponentHand":
            addOpponentCard(card);
            break;
        default:
            throw new IllegalArgumentException("無効なエリア指定: " + area);
    }
    notifyListeners(); // 状態変更を通知
}

}

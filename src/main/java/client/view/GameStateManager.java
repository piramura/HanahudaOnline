import java.util.ArrayList;
import java.util.List;

public class GameStateManager {
    private List<Card> fieldCards;
    private List<Card> playerHand;
    private List<Card> opponentHand;
    public CardFactory cardFactory;
    private int currentPlayer;
    private boolean isGameOver;

    public GameStateManager() {
        this.fieldCards = new ArrayList<>();
        this.playerHand = new ArrayList<>();
        this.opponentHand = new ArrayList<>();
        this.cardFactory = new CardFactory();
        this.currentPlayer = 0;
        this.isGameOver = false;
    }
    // 状態の一括更新
    public void updateState(List<Integer> fieldCardIDs, List<Integer> playerHandIDs, int opponentHandCount) {
        // フィールドカードを更新
        fieldCards.clear();
        for (int id : fieldCardIDs) {
            fieldCards.add(cardFactory.createCard(id));
        }

        // プレイヤーの手札を更新
        playerHand.clear();
        for (int id : playerHandIDs) {
            playerHand.add(cardFactory.createCard(id));
        }

        // 相手の手札を更新（裏向き）
        opponentHand.clear();
        for (int i = 0; i < opponentHandCount; i++) {
            opponentHand.add(cardFactory.createCard(-1, "Hidden", true));
        }
    }
    // フィールドカードの管理
    public List<Card> getFieldCards() {
        return fieldCards;
    }

    public void addFieldCard(Card card) {
        fieldCards.add(card);
    }

    public void removeFieldCard(Card card) {
        fieldCards.remove(card);
    }

    // プレイヤーの手札管理
    public List<Card> getPlayerHand() {
        return playerHand; // 不変リストを返す
    }

    public void addPlayerCard(Card card) {
        playerHand.add(card);
    }

    public void removePlayerCard(Card card) {
        playerHand.remove(card);
    }

    // 相手の手札管理
    public List<Card> getOpponentHand() {
        return opponentHand; // 不変リストを返す
    }

    public void addOpponentCard(Card card) {
        opponentHand.add(card);
    }

    public void removeOpponentCard(Card card) {
        opponentHand.remove(card);
    }

    // 現在のプレイヤー
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void nextTurn() {
        this.currentPlayer = (this.currentPlayer + 1) % 2; // 例: 2人プレイヤーの場合
    }

    // ゲーム終了チェック
    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.isGameOver = gameOver;
    }
    public void updateOpponentHandCount(int count) {
    opponentHand.clear();
    for (int i = 0; i < count; i++) {
        opponentHand.add(cardFactory.createCard(-1, "Hidden", true)); // 裏向きカード
    }
}

    // GameStateManager.java
public void playCard(Card card) {
    if (playerHand.contains(card)) {
        playerHand.remove(card);
        fieldCards.add(card);
        System.out.println("カードがフィールドに移動しました: " + card.getId());
    } else {
        throw new IllegalArgumentException("カードがプレイヤーの手札に存在しません。");
    }
}

    // カードクリック時の処理
    public void processCardClick(Card clickedCard) {
        if (fieldCards.contains(clickedCard)) {
            System.out.println("フィールドのカードがクリックされました: " + clickedCard.getId());
        } else if (playerHand.contains(clickedCard)) {
            System.out.println("プレイヤーのカードがクリックされました: " + clickedCard.getId());
            playCard(clickedCard); // プレイヤーの手札からフィールドへ移動
            nextTurn(); // 次のターンへ
        } else {
            System.out.println("無効なカードがクリックされました。");
        }
    }
}
    

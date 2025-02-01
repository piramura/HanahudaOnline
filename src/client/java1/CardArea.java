import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CardArea extends JPanel {
    private List<Integer> cards; // カードを保持するリスト
    private GameController controller; // コントローラーの参照
    private CardFactory cardFactory = new CardFactory(); // カードファクトリーをインスタンス化

    public CardArea(int rows, int cols, GameController controller) {
        this.cards = new ArrayList<>();
        this.controller = controller; // コントローラーを保持
    }

    public int getCardCount() {
        return cards.size();
    }

    public List<Integer> getCardIds() {
        return cards;
    }

    // カードIDを追加
    public void addCard(int cardId) {
        cards.add(cardId);
        // クリック時のアクションを設定
        JButton cardButton = cardFactory.createCard(cardId, e -> {
            System.out.println("カード " + cardId + " がクリックされました！");
            // カードクリック時の処理をここに記述
            // コントローラーにカードクリックを通知
            if (controller != null) {
                controller.handleCardClick(cardId);
            }
        });
        add(cardButton);
        revalidate();
        repaint();
    }

    // 全カードをクリア
    public void clearCards() {
        SwingUtilities.invokeLater(() -> { // UIスレッドで実行
            try {
                cards.clear();
                removeAll();
                revalidate();
                repaint();
            } catch (Exception e) {
                System.err.println("CardArea のカードクリア中にエラー: " + e.getMessage());
            }
        });
    }

    public void updateCards(List<Integer> newCardIds) {
    System.out.println("更新前のカード数: " + cards.size());
    SwingUtilities.invokeLater(() -> {
        try {
            // 差分計算: 追加が必要なカードID
            List<Integer> cardsToAdd = new ArrayList<>(newCardIds);
            cardsToAdd.removeAll(cards);

            // 差分計算: 削除が必要なカードID
            List<Integer> cardsToRemove = new ArrayList<>(cards);
            cardsToRemove.removeAll(newCardIds);

            // 削除
            for (Integer cardId : cardsToRemove) {
                cards.remove(cardId);
                // 必要に応じて具体的なUI要素削除を追加
            }

            // 追加
            for (Integer cardId : cardsToAdd) {
                addCard(cardId);
            }

            revalidate();
            repaint();
        } catch (Exception e) {
            System.err.println("CardArea のカード更新中にエラー: " + e.getMessage());
            e.printStackTrace();
        }
    });
    System.out.println("新しいカード数: " + newCardIds.size());
}

}

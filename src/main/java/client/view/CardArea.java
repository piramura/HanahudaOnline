import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CardArea extends JPanel {
    private List<Card> cards; // カードを保持するリスト

    public CardArea(int rows, int cols) {
        this.cards = new ArrayList<>();
        setLayout(new GridLayout(rows, cols, 10, 10)); // グリッド設定
        setPreferredSize(new Dimension(400, 200)); // サイズ調整
    }

    // カードを追加
    public void addCard(Card card) {
        cards.add(card);
        add(card); // パネルに追加
        revalidate();
        repaint();
    }

    // カードをすべてクリア
    public void clearCards() {
        cards.clear();
        removeAll();
        revalidate();
        repaint();
    }
    public void removeAllCards() {
    removeAll(); // JPanelのすべての子コンポーネントを削除
    revalidate();
    repaint();
}


    // カードを一括で更新
    public void updateCards(List<Card> newCards) {
        clearCards();
        for (Card card : newCards) {
            addCard(card);
        }
    }
}

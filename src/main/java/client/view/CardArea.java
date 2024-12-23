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
        setFocusable(true); // フォーカス可能にする
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
    public void updateCards(List<Card> newCards) {
        System.out.println("更新前のカード数: " + cards.size());
    SwingUtilities.invokeLater(() -> { // UI更新はEDTで実行
        try {
            // 全てのカードを削除
            cards.clear();
            removeAll();

            // 新しいカードを追加
            for (Card card : newCards) {
                cards.add(card);
                add(card);
            }

            // レイアウトと描画を更新
            revalidate();
            repaint();
        } catch (Exception e) {
            System.err.println("CardArea のカード更新中にエラー: " + e.getMessage());
            e.printStackTrace();
        }
    });
    System.out.println("新しいカード数: " + newCards.size());
}

}

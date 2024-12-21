import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

public class CardFactory {
    // カードを生成する
    public Card createCard(int id, String name, boolean isFaceDown) {
        // IDを基に画像を取得
        String imagePath = "images/" + id + ".png"; // 画像ファイルのパス
        ImageIcon image = new ImageIcon(imagePath);

        // カードを生成
        Card card = new Card(id, name, isFaceDown);
        card.setIcon(image); // 画像を設定
        return card;
    }

    // IDだけでカードを生成（デフォルト名と状態）
    public Card createCard(int id) {
        String name = "Card " + id; // デフォルト名
        boolean isFaceDown = id < 0; // 負のIDは裏向き
        return createCard(id, name, isFaceDown);
    }

}

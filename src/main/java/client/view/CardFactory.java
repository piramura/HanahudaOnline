import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
public class CardFactory {

    public Card createCard(int id, String name, boolean isFaceDown) {
        BufferedImage defaultImage = getDefaultCardImage(id);
        return new Card(id, name, isFaceDown, defaultImage, null);
    }

    // IDのみで生成（デフォルト名と状態を適用）
    public Card createCard(int id) {
        return createCard(id, "Card " + id, id < 0); // デフォルトの名前と状態を設定
    }

    // デフォルト画像を取得するメソッド
    private BufferedImage getDefaultCardImage(int id) {
        BufferedImage dummyImage = new BufferedImage(100, 150, BufferedImage.TYPE_INT_ARGB);
        Graphics g = dummyImage.getGraphics();
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, 100, 150);
        g.dispose();
        return dummyImage;
    }

}

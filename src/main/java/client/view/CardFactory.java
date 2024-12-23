import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.File;
public class CardFactory {

    public Card createCard(int id, String name, boolean isFaceDown) {
        BufferedImage image = loadImage(id); // 画像を読み込む
        return new Card(id, name, isFaceDown, image, null);
    }

    // IDのみで生成（デフォルト名と状態を適用）
    public Card createCard(int id) {
        return createCard(id, "Card " + id, id < 0); // デフォルトの名前と状態を設定
    }
    private BufferedImage loadImage(int id) {
        try {
            String filePath = "images/" + id + ".png"; // カレントディレクトリの image フォルダ内
            return ImageIO.read(new File(filePath)); // 画像を読み込む
        } catch (IOException e) {
            System.err.println("画像の読み込みに失敗しました: " + id + ".png");
            // 画像が見つからない場合、デフォルトのグレー画像を生成
            return getDefaultCardImage(id);
        }
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

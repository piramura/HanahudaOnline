import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.File;
public class CardFactory {
    public JButton createCard(int cardId, ActionListener listener) {
        JButton cardButton = new JButton(String.valueOf(cardId));
        cardButton.setHorizontalAlignment(SwingConstants.CENTER);
        cardButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        cardButton.setOpaque(true);
        ImageIcon cardIcon = loadImage(cardId);
        if (cardIcon != null) {
            cardButton.setIcon(cardIcon);
        }
        // ボタンにアクションリスナーを追加
        cardButton.addActionListener(listener);
        return cardButton;
    }

    // public Card createCard(int id, String name, boolean isFaceDown) {
    //     BufferedImage image = loadImage(id); // 画像を読み込む
    //     return new Card(id, name, isFaceDown, image, null);
    // }

    // // IDのみで生成（デフォルト名と状態を適用）
    // public Card createCard(int id) {
    //     return createCard(id, "Card " + id, id < 0); // デフォルトの名前と状態を設定
    // }
    private ImageIcon loadImage(int id) {
        try {
            String filePath = "images/" + id + ".png"; // カレントディレクトリの image フォルダ内
            BufferedImage image = ImageIO.read(new File(filePath)); // 画像を読み込む
            return new ImageIcon(image); // 画像を読み込む
        } catch (IOException e) {
            System.err.println("画像の読み込みに失敗しました: " + id + ".png");
            // 画像が見つからない場合、デフォルトのグレー画像を生成
            return getDefaultCardImage();
        }
    }

    // デフォルト画像を取得するメソッド
    private ImageIcon getDefaultCardImage() {
        try {
            String filePath = "images/dummy.png"; // カレントディレクトリの images フォルダ内
            BufferedImage image = ImageIO.read(new File(filePath)); // デフォルト画像を読み込む
            return new ImageIcon(image); // BufferedImage を ImageIcon に変換
        } catch (IOException e) {
            System.err.println("デフォルト画像の読み込みに失敗しました。");
            // デフォルト画像が見つからない場合、空の画像を生成
            BufferedImage dummyImage = new BufferedImage(100, 150, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = dummyImage.createGraphics();
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRect(0, 0, 100, 150);
            g2.setColor(Color.BLACK);
            g2.drawString("No Image", 20, 75);
            g2.dispose();
            return new ImageIcon(dummyImage);
        }
    }
}
